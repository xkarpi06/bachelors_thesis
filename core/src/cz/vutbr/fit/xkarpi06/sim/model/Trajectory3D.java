package cz.vutbr.fit.xkarpi06.sim.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Vector3;
import com.sun.org.apache.bcel.internal.Const;
import cz.vutbr.fit.xkarpi06.sim.output.MyLog;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents 3D trajectory with additional data like time of flight, attitude history, ...
 * @author xkarpi06
 * created: 18-4-2020, xkarpi06
 * updated:
 */
public class Trajectory3D {

    /** Logger instance */
    public static final Logger LOGGER = MyLog.getLogger( Trajectory3D.class.getName() );

    /** vertices defining the trajectory */
    private Vector3[] smoothVertices;   // (cartesian)

    /** vertices obtained as input in constructor */
    private Vector3[] controlVertices;  // (cartesian)

    /** 'checkpoints' spread equally across trajectory, including start and finish */
    private float[] times;      // (seconds)

    /** speed at each section between two checkpoints */
    private float[] speeds;

    /** pitch in (radians) state-points spread equally across trajectory, including start and finish*/
    private float[] pitchHistory;    // (radians)

    /** mass in (kg) state-points spread equally across trajectory, including start and finish*/
    private float[] massHistory;    // (kilograms)

    /** vertical velocity in (m/s) state-points spread equally across trajectory, including start and finish*/
    private float[] vrHistory;    // (m/s)

    /** horizontal velocity in (m/s) state-points spread equally across trajectory, including start and finish*/
    private float[] vthHistory;    // (m/s)

    private static final float SCALE = 0.001f;

    /**
     * Constructor
     * @param controlVertices original vertices for trajectory in cartesian form
     * @param times times at which the spacecraft arrives to each vertex, if the amount differs from
     *              amount of vertices, the time-points will be evenly spread among trajectory.
     *              Time equal to 1 and constant speed will be set if null.
     * @param smoothFactor amount of new vertices between each two vertices when smoothing with spline
     */
    private Trajectory3D(Vector3[] controlVertices, float[] times, int smoothFactor) {
//        for (int i = 1; i < controlVertices.length; i++) {
//            System.out.println(i + " Delta: " + controlVertices[i-1].dst(controlVertices[i]));
//        }

        this.controlVertices = controlVertices;

        createSmoothVertices(scale(this.controlVertices, SCALE), smoothFactor);
        this.times = times;
        computeSpeed();
    }

    /**
     * static Creator
     */
    public static Trajectory3D create(Vector3[] controlVertices, float[] times, int smoothFactor) {
        if (controlVertices == null || controlVertices.length < 2) {
            LOGGER.log(Level.INFO, "Not enough vertices for trajectory, less than 2.");
            return null;
        } else if (times == null || times.length < 2) {
            LOGGER.log(Level.INFO, "Not enough discrete times for trajectory, less than 2.");
            return null;
        }
        return new Trajectory3D(controlVertices, times, smoothFactor);
    }

    /**
     * Creates polished trajectory using {@link Bezier}
     * @param input vertex array
     * @param smoothness amount of new vertices between each two input vertices
     */
    private void createSmoothVertices(Vector3[] input, int smoothness) {
        int k = input.length + (input.length - 1)*smoothness;
        smoothVertices = new Vector3[k];

        Bezier<Vector3> bezier = null;
        for(int i = 0; i < input.length; i++) {
            // create new Bezier spline every four vertices
            if (i%3 == 0 || i == input.length - 1) {
                if (i+3 < input.length) {
                    bezier = new Bezier<>(input, i, 4);
//                    System.out.printf("New 4 bezier from input[%d]-input[%d]\n", i, i+3);

                    // these below are only used for last trailing vertices
                } else if (i+2 < input.length) {
                    bezier = new Bezier<>(input, i, 3);
//                    System.out.printf("New 3 bezier from input[%d]-input[%d]\n", i, i+2);
                } else if (i+1 < input.length) {
                    bezier = new Bezier<>(input, i, 2);
//                    System.out.printf("New 2 bezier from input[%d]-input[%d]\n", i, i+1);
                } else { // very last index
                    smoothVertices[smoothVertices.length-1] = input[input.length-1];
//                    System.out.println("Very last smooth.");
//                    System.out.printf("New vertex: input.length=%d, smF=%d, k=%d, i=%d, nextSm=%d\n",
//                            input.length, smoothness, k, i, smoothVertices.length-1);
                    break;
                }
            }

            for (int j = 0; j <= smoothness; j++) {
                int nextSmooth = i*(smoothness + 1) + j;
                smoothVertices[nextSmooth] = new Vector3();
                float offsetOfNext = ((i%3)*(smoothness + 1) + j)/(float)((bezier.points.size - 1)*(smoothness + 1));
                bezier.valueAt(smoothVertices[nextSmooth], offsetOfNext);
//                catmull.valueAt(smoothVertices[i], ((float)i)/((float)k-1));

                LOGGER.log(Level.FINEST, "Trajectory vertex {0}", smoothVertices[nextSmooth]);
//                System.out.printf("New vertex: input.length=%d, smF=%d, k=%d, i=%d, j=%d, nextSm=%d, offNext=%.2f\n",
//                        input.length, smoothness, k, i, j, nextSmooth, offsetOfNext);
            }
        }

    }

    /**
     * Scales all given vertices to new size
     * @param input array of vertices
     * @param scale the scale
     * @return scales vertices
     */
    private Vector3[] scale(Vector3[] input, float scale) {
        Vector3[] output = new Vector3[input.length];
        for (int i = 0; i < output.length; i++) {
            output[i] = new Vector3(input[i]).scl(scale);
        }
        return output;
    }

    /**
     * Since CatmulRomSpline ommits first and last points, one point is added in the begining
     * and one at the end
     * @param input input vertices
     * @return array with 2 new vertices
     */
//    private Vector3[] extendEnds(Vector3[] input) {
//        Vector3[] output = new Vector3[input.length + 2];
//
//        // first vertex
//        Vector3 delta = new Vector3(input[1]).sub(input[0]);
//        output[0] = new Vector3(input[0]).sub(delta);
//
//        // middle
//        for (int i = 0; i < input.length; i++) {
//            output[i+1] = input[i];
//        }
//
//        // last vertex
//        int lastI = input.length - 1;
//        int lastO = output.length - 1;
//        delta = new Vector3(input[lastI]).sub(input[lastI-1]);
//        output[lastO] = new Vector3(input[lastI]).add(delta);
//
//        return output;
//    }

    /**
     * Computes speed at each section based on times,
     * knowing speed is necessary for computing ship position change
     */
    private void computeSpeed() {
        int sections = times.length - 1;
        speeds = new float[sections];
        for (int i = 0; i < sections; i++) {
            float deltat = times[i+1] - times[i];
            speeds[i] = 1/(sections * deltat);  // speed applies for whole section between two time points
        }
    }

    private boolean isValidHistory(float[] input) {
        return input != null && input.length >= 1;
    }

    /**
     * Adds pitch history to trajectory
     * @param input the history
     * @return success of operation
     */
    public boolean addPitchHistory(float[] input) {
        if (isValidHistory(input)) {
            this.pitchHistory = input;
            return true;
        } else {
            LOGGER.log(Level.INFO, "Trajectory: Invalid input for pitch history.");
        }
        return false;
    }

    /**
     * Adds mass history to trajectory
     * @param input the history
     * @return success of operation
     */
    public boolean addMassHistory(float[] input) {
        if (isValidHistory(input)) {
            this.massHistory = input;
            return true;
        } else {
            LOGGER.log(Level.INFO, "Trajectory: Invalid input for mass history.");
        }
        return false;
    }

    /**
     * Adds vertical velocity history to trajectory
     * @param input the history
     * @return success of operation
     */
    public boolean addVrHistory(float[] input) {
        if (isValidHistory(input)) {
            this.vrHistory = input;
            return true;
        } else {
            LOGGER.log(Level.INFO, "Trajectory: Invalid input for vertical velocity history.");
        }
        return false;
    }

    /**
     * Adds horizontal velocity history to trajectory
     * @param input the history
     * @return success of operation
     */
    public boolean addVthHistory(float[] input) {
        if (isValidHistory(input)) {
            this.vthHistory = input;
            return true;
        } else {
            LOGGER.log(Level.INFO, "Trajectory: Invalid input for horizontal velocity history.");
        }
        return false;
    }

    /**
     * Returns speed at specified position
     * @param position value from interval [0,1] excluding 1
     * @return positive value or -1 for invalid position
     */
    public float speedAt(float position) {
        if (position >= 0 && position < 1) {
            return speeds[(int) (position * speeds.length)];
        } else if (position == 1) {
            return speeds[speeds.length-1]; // determines speed for way back from finish
        } else {
            return -1;
        }
    }

    /**
     * Returns vertex at specified postion
     * @param position value from interval [0,1] including bounds
     * @return vertex or null for invalid position
     */
    public Vector3 vertexAt(float position) {
        if (position >= 0 && position <= 1) {
            float traveledVertices = position * (smoothVertices.length - 1);
            Vector3 sectionStart = smoothVertices[(int)traveledVertices];
            Vector3 sectionEnd;
            if (((int)traveledVertices + 1) < smoothVertices.length - 1) {
                sectionEnd = smoothVertices[(int)traveledVertices + 1];
            } else {
                sectionEnd = sectionStart;
            }
            float tdec = traveledVertices - ((int)traveledVertices);    //the decimal part of traveledVertices
//            Vector3 result = new Vector3(sectionStart).mulAdd(new Vector3(sectionEnd).sub(sectionStart), tdec);
            Vector3 result = new Vector3(
                    sectionStart.x + (sectionEnd.x - sectionStart.x) * tdec,
                    sectionStart.y + (sectionEnd.y - sectionStart.y) * tdec,
                    sectionStart.z + (sectionEnd.z - sectionStart.z) * tdec
            );
            return result;
        } else {
            return null;
        }
    }

    /**
     * Returns elapsed time since trajectory start at specified position
     * @param position value from interval [0,1] excluding 1
     * @return 0 for invalid position
     */
    public float elapsedTimeAt(float position) {
        return getValueAtPosition(times, position);
    }

    /**
     * Returns spacecraft pitch at specified position
     * @param position value from interval [0,1] excluding 1
     * @return 0 for invalid position or pitch history not set
     */
    public float pitchAt(float position) {
        return getValueAtPosition(pitchHistory, position);
    }

    /**
     * Returns spacecraft mass at specified position
     * @param position value from interval [0,1] excluding 1
     * @return 0 for invalid position of mass history not set
     */
    public float massAt(float position) {
        return getValueAtPosition(massHistory, position);
    }

    /**
     * Returns spacecraft vertical velocity at specified position
     * @param position value from interval [0,1] excluding 1
     * @return 0 for invalid position or vertical velocity history not set
     */
    public float verticalVelocityAt(float position) {
        return getValueAtPosition(vrHistory, position);
    }

    /**
     * Returns spacecraft horizontal velocity at specified position
     * @param position value from interval [0,1] excluding 1
     * @return 0 for invalid position or horizontal velocity history not set
     */
    public float horizontalVelocityAt(float position) {
        return getValueAtPosition(vthHistory, position);
    }

    /**
     * Get value from ordered array proportional to position between 0 and 1
     * @param array the ordered array
     * @param position the position between 0 and 1
     * @return value from array
     */
    private float getValueAtPosition(float[] array, float position) {
        if (array == null) {
            return 0;
        } else if (array.length == 1) {
            return array[0];
        } else if (position >= 0 && position < 1) {
            float floatIndex = position * (array.length - 1);
            float valueBefore = array[(int) floatIndex];
            float valueAfter = array[(int) floatIndex + 1];
            float decimalpartOfIndex = floatIndex - ((int) floatIndex);    //the decimal part of traveledVertices
            return valueBefore + (valueAfter - valueBefore) * decimalpartOfIndex;
        } else if (position == 1) {
            return array[array.length - 1];
        } else {
            return 0;
        }
    }

    /**
     * Computes distance across surface of Moon between current point and finish in meters
     * @param position position in trajectory from 0 to 1
     * @return 0 if position is invalid
     */
    public float downrangeDistFromTargetAt(float position) {
        Vector3 target = controlVertices[controlVertices.length-1];
        Vector3 current = vertexAt(position);
        if (current != null) {
            float thetaTarget = (float)Math.atan2(target.y, target.x);
            float thetaCurrent = (float)Math.atan2(current.y, current.x);
            if (thetaTarget < 0) thetaTarget += 2*Math.PI;
            if (thetaCurrent < 0) thetaCurrent += 2*Math.PI;

//            if (thetaTarget > thetaCurrent) {
                return Constants.MOON_RADIUS * Math.abs(thetaTarget - thetaCurrent);
//            } else {
//                return Constants.MOON_RADIUS * (thetaCurrent - thetaTarget);
//            }
        } else {
            return 0;
        }
    }

    /**
     * Builds Mesh representing trajectory
     * @return Mesh
     */
    public Mesh buildMesh() {
        return getMesh(smoothVertices);
    }

    /**
     * Builds Mesh representing raw unsmoothed trajectory
     * @return Mesh
     */
    public Mesh buildMeshControl() {
        return getMesh(controlVertices);
    }

    private Mesh getMesh(Vector3[] points) {
        MeshBuilder meshBuilder = new MeshBuilder();
        meshBuilder.begin(Usage.Position | Usage.Normal, GL20.GL_LINES);
        for (int i = 0; i < points.length - 1; i++) {
            meshBuilder.line(points[i], points[i+1]);
        }
        return meshBuilder.end();
    }

    /**
     * Builds model representing trajectory
     * if the trajectory has too many vertices, it is split into multiple Meshes
     * @param attributes material of the model
     * @return model
     */
    public Model buildModel(Material attributes) {
        if (attributes == null) attributes = new Material(ColorAttribute.createDiffuse(Color.WHITE));
        Vector3[] modelVertices = smoothVertices;

        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();

        modelBuilder.part("skelet", getMesh(controlVertices), GL20.GL_LINES, new Material(ColorAttribute.createDiffuse(Color.GREEN)));

        int lastStart = 0;
        for (int i = 1; i < modelVertices.length; i++) {

            // Max vertices in Mesh can be Short.MAX_VALUE (32672). We are creating lines, so almost every vertex is counted twice.
            if (i % (Short.MAX_VALUE/2) == 0 || i == modelVertices.length - 1) {
                System.out.printf("Building mesh for vertices %d-%d\n", lastStart, i);
                modelBuilder.part("catmull_" + lastStart, getMesh(Arrays.copyOfRange(modelVertices, lastStart, i)), GL20.GL_LINES, attributes);
                lastStart = i;
            }

        }

        return modelBuilder.end();
    }
}
