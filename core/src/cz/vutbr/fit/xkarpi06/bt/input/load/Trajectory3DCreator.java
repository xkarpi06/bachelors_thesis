package cz.vutbr.fit.xkarpi06.bt.input.load;

import com.badlogic.gdx.math.Vector3;
import cz.vutbr.fit.xkarpi06.bt.model.Trajectory3D;

/**
 * Auxiliary class for mocking data
 */
public class Trajectory3DCreator {

    public static Trajectory3D create() {

        float scale = 100000f;
//        float scale = 4000000f;
//        Vector3 start = new Vector3(0, 0, 0);
        Vector3 start = new Vector3(1738100f, 0, 0);
//        Vector3 start = new Vector3(1948100f, 0, 0);
        Vector3[] points = new Vector3[]{
                new Vector3(0,0,0).add(start),
                new Vector3(0,0,scale).add(start),
                new Vector3(0,scale,scale).add(start),
                new Vector3(scale, scale, scale).add(start),
//                new Vector3(scale*1.001f, scale, scale).add(start),
                new Vector3(scale,0,scale).add(start),
                new Vector3(scale,0,0).add(start)
        };
//        Vector3[] points = new Vector3[]{
//                new Vector3(0,0,0).add(start),
//                new Vector3(0,0,scale/2).add(start),
//                new Vector3(0,scale/2,scale/2).add(start),
//                new Vector3(-scale, scale/2, scale/2).add(start),
//                new Vector3(-scale,0,scale/2).add(start),
//                new Vector3(-scale,0,0).add(start)
//        };

        float ts = 2f;
        float[] times = new float[]{0*ts, 2f*ts, 4f*ts, 5f*ts, 8f*ts, 10f*ts};

        return Trajectory3D.create(points, times, 100);
    }

}
