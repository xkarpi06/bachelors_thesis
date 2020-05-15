package cz.vutbr.fit.xkarpi06.bt.input.parse.strategy;

import com.badlogic.gdx.math.Vector3;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Strategy for strategy design pattern
 * reads line and returns Vector3
 * @author xkarpi06
 * created: 19-4-2020, xkarpi06
 * updated:
 */
public class TrajectoryParser implements ParsingStrategy{

    // format: r,theta,phi
    //     or: x,y,z

    /** Amount of variables per line */
    public static final int VARIABLE_COUNT = 3;

    /** Delimiter */
    public static final String DELIMITER = ",";

    /** Order in input file line */
    public static final int R_INDEX = 0;

    /** Order in input file line */
    public static final int THETA_INDEX = 1;

    /** Order in input file line */
    public static final int PHI_INDEX = 2;

    @Override
    public Vector3 parseLine(String line) {
        Vector3 result = null;
        String[] variables = line.split(DELIMITER);
        if (variables.length == VARIABLE_COUNT) {
            float var1;
            float var2;
            float var3;
            try {
                var1 = Float.parseFloat(variables[R_INDEX]);
                var2 = Float.parseFloat(variables[THETA_INDEX]);
                var3 = Float.parseFloat(variables[PHI_INDEX]);
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
                return null;
            }
            if (var1 >= 0) {
                result = new Vector3(var1, var2, var3);
            }
        }
        return result;
    }
}
