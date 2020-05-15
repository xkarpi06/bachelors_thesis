package cz.vutbr.fit.xkarpi06.bt.input.load;

import com.badlogic.gdx.math.Vector3;
import cz.vutbr.fit.xkarpi06.bt.output.MyLog;
import cz.vutbr.fit.xkarpi06.bt.model.Trajectory3D;
import cz.vutbr.fit.xkarpi06.bt.input.parse.StringParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Loads data from input directory. Each city must have its own subdirectory.
 * @author xkarpi06
 * @version 1.0
 * @since 1.0
 * created: 22-3-2020, xkarpi06
 * updated: 29-3-2020, xkarpi06
 */
public class Trajectory3DLoader {

    /** Logger instance */
    public static final Logger LOGGER = MyLog.getLogger( Trajectory3DLoader.class.getName() );

    /** Source directory of data */
    private static String dir;

    /**
     * Loads trajectory from given directory
     * @return Trajectory3D if successful, null otherwise
     */
    public static Trajectory3D load(String directory) {
        Trajectory3DLoader.dir = directory;
        LOGGER.log(Level.FINE,"Trying to load data from {0}", directory);
        Trajectory3D loadedTrajectory = null;
        File dataDirectory = new File(dir);
        if (!dataDirectory.isDirectory()) {
            LOGGER.log(Level.SEVERE, "Directory does not exist: {0}", dir);
        } else {
            loadedTrajectory = getTrajectory(dataDirectory);
        }
        return loadedTrajectory;
    }

    /**
     * Creates a trajectory from files in directory
     * @param sourceDirectory input directory with the trajectory files
     * @return Trajectory3D, or null
     */
    private static Trajectory3D getTrajectory(File sourceDirectory) {
//        City newCity = City.defaultInstance(cityName);
        Map<String, File> sourceFiles = new HashMap<>();
        for (File directoryItem : sourceDirectory.listFiles()) {
            if (ProjectFiles.SRC_FILES.contains(directoryItem.getName())) {
                sourceFiles.put(directoryItem.getName(), directoryItem);
            }
            LOGGER.log(Level.FINE, "Data directory contents: {0}", directoryItem.getName());
        }

        Vector3[] controlVertices = loadVertices(sourceFiles);
        float[] times = loadTimes(sourceFiles);
        int smoothFactor = 3;
        Trajectory3D trajectory = Trajectory3D.create(controlVertices, times, smoothFactor);

        if (trajectory != null) {
            trajectory.addPitchHistory(loadPitchHistory(sourceFiles));
            trajectory.addMassHistory(loadMassHistory(sourceFiles));
            trajectory.addVrHistory(loadVrHistory(sourceFiles));
            trajectory.addVthHistory(loadVthHistory(sourceFiles));
        }

        return trajectory;
    }

    /**
     * Creates array of vertices from source file, if present
     * @param sourceFiles source files
     * @return array of vertices or null if sourceFile is missing
     */
    private static Vector3[] loadVertices(Map<String, File> sourceFiles) {
        String sourceFile;
        if (sourceFiles.containsKey(ProjectFiles.SRC_TRAJECTORY_POLAR)) {
            sourceFile = ProjectFiles.SRC_TRAJECTORY_POLAR;
            LOGGER.log(Level.FINE, "Loading polar vertices from {0}/{1}", new Object[]{ dir, sourceFile });
        } else if (sourceFiles.containsKey(ProjectFiles.SRC_TRAJECTORY_CARTESIAN)) {
            sourceFile = ProjectFiles.SRC_TRAJECTORY_CARTESIAN;
            LOGGER.log(Level.FINE, "Loading cartesian vertices from {0}/{1}", new Object[]{ dir, sourceFile });
        } else {
            LOGGER.log(Level.FINE, "Trajectory source is missing! Provide {0} or {1} ",
                    new Object[]{ ProjectFiles.SRC_TRAJECTORY_CARTESIAN, ProjectFiles.SRC_TRAJECTORY_POLAR});
            System.err.printf("Missing source files. Provide one of: %s or %s.\n", ProjectFiles.SRC_TRAJECTORY_POLAR, ProjectFiles.SRC_TRAJECTORY_CARTESIAN);
            return null;
        }
        Scanner sc = getScannerInstance(sourceFiles.get(sourceFile));
        List<Vector3> vertices = new ArrayList<>();
        Vector3 parsedPolarVector;
        if (sc != null) {
            int lineCount = 1;
            while (sc.hasNextLine()) {
                parsedPolarVector = (Vector3) parseNextLine(sc, sourceFile, lineCount);
                if (parsedPolarVector != null) {
                    if (sourceFile.equals(ProjectFiles.SRC_TRAJECTORY_CARTESIAN)) {
                        vertices.add(parsedPolarVector);
                    } else {
                        vertices.add(toCartesian(parsedPolarVector));
                    }
                }
                lineCount++;
            }
        } else {
            LOGGER.log(Level.FINE, "Cannot load file {0}", sourceFile);
        }
        Vector3[] result = new Vector3[vertices.size()];
        vertices.toArray(result);
        return result;
    }

    /**
     * Creates vector in cartesian form from polar form vector
     * @param polar input vector
     * @return cartesian vector
     */
    private static Vector3 toCartesian(Vector3 polar) {
        float r = polar.x;
        float th = polar.y;
        float phi = polar.z;
        Vector3 cartesian = new Vector3();
        cartesian.x = (float) (r * Math.cos(th) * Math.cos(phi));
        cartesian.y = (float) (r * Math.sin(th) * Math.cos(phi));
        cartesian.z = (float) (r * Math.sin(phi));
        return cartesian;
    }

    /**
     * Creates discrete-times history from source file, if present
     * @param sourceFiles provided source files
     * @return time history
     */
    private static float[] loadTimes(Map<String, File> sourceFiles) {
        float[] times = loadFloats(sourceFiles, ProjectFiles.SRC_TIMES);
        if (times.length == 0) {
            System.err.printf("Source file missing. Provide file: %s\n", ProjectFiles.SRC_TIMES);
        } else if (times.length < 2) {
            System.err.printf("Trajectory NOT CREATED. Not enough discrete times in %s: less than 2.\n", ProjectFiles.SRC_TIMES);
        }
        return times;
    }

    /**
     * Creates pitch history from source file, if present
     * @param sourceFiles provided source files
     * @return pitch history
     */
    private static float[] loadPitchHistory(Map<String, File> sourceFiles) {
        return loadFloats(sourceFiles, ProjectFiles.SRC_PITCH);
    }

    /**
     * Creates mass history from source file, if present
     * @param sourceFiles provided source files
     * @return mass history
     */
    private static float[] loadMassHistory(Map<String, File> sourceFiles) {
        return loadFloats(sourceFiles, ProjectFiles.SRC_MASS);
    }

    /**
     * Creates vertical velocity history from source file, if present
     * @param sourceFiles provided source files
     * @return vertical velocity history
     */
    private static float[] loadVrHistory(Map<String, File> sourceFiles) {
        return loadFloats(sourceFiles, ProjectFiles.SRC_VR);
    }

    /**
     * Creates horizontal velocity history from source file, if present
     * @param sourceFiles provided source files
     * @return horizontal velocity history
     */
    private static float[] loadVthHistory(Map<String, File> sourceFiles) {
        return loadFloats(sourceFiles, ProjectFiles.SRC_VTH);
    }

    /**
     * Loads list of floats from given file, if the file is among sourcefiles
     * @param sourceFiles given files
     * @param sourceFile filename of the file we will load from
     * @return list of floats
     */
    private static float[] loadFloats(Map<String, File> sourceFiles, String sourceFile) {
        Scanner sc = getScannerInstance(sourceFiles.get(sourceFile));
        List<Float> floats = new ArrayList<>();
        Float parsedPitch;
        if (sc != null) {
            int lineCount = 1;
            while (sc.hasNextLine()) {
                parsedPitch = (Float) parseNextLine(sc, sourceFile, lineCount);
                if (parsedPitch != null) {
                    floats.add(parsedPitch);
                }
                lineCount++;
            }
        } else {
            LOGGER.log(Level.FINE, "Cannot load file {0}", sourceFile);
        }
        float[] result = new float[floats.size()];
        for (int i = 0; i < floats.size(); i++) {
            result[i] = floats.get(i);
        }
        return result;
    }

    /**
     * Creates scanner instance if possible
     * @param file input
     * @return Scanner or null
     */
    private static Scanner getScannerInstance(File file) {
        Scanner sc = null;
        if (file != null) {
            try {
                sc = new Scanner(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return sc;
    }

    /**
     * Reads next line from scanner and returns result as object
     *
     * Object type is based on fileName, each fileName has different
     * strategy for parsing line into variables. This strategy is chosen
     * in creator of StringParser
     * @param sc scanner, assumed existing and hasNextLine
     * @param fileName name of source file for logs and strategy
     * @param lineCount counter for logs
     * @return null if line is invalid, instance if everything is correct.
     */
    private static Object parseNextLine(Scanner sc, String fileName, int lineCount) {
        Object result = null;
        StringParser sp = new StringParser(fileName);
        try {
            String line = sc.nextLine();
            if (!line.trim().isEmpty()) {
                result = sp.exectueStrategy(line);
                if (result == null) {
                    LOGGER.log(Level.FINE, "Invalid line in {0}/{1}, line {2}", new Object[]{dir, fileName, lineCount});
                    System.err.printf("Invalid line in %s/%s, line %d\n", dir, fileName, lineCount);
                }
            }
        } catch (NoSuchElementException|IllegalStateException e) {
            e.printStackTrace();
        }
        return result;
    }

}
