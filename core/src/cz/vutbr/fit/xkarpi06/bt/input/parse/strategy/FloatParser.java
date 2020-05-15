package cz.vutbr.fit.xkarpi06.bt.input.parse.strategy;

import java.util.logging.Logger;

/**
 * Strategy for strategy design pattern
 * reads line and returns Float
 * @author xkarpi06
 * created: 30-4-2020, xkarpi06
 * updated:
 */
public class FloatParser implements ParsingStrategy {

    // format: float

    /**
     * Amount of variables per line
     */
    public static final int VARIABLE_COUNT = 1;

    /**
     * Delimiter
     */
    public static final String DELIMITER = ";";

    /**
     * Order in input file line
     */
    public static final int FL_INDEX = 0;

    @Override
    public Float parseLine(String line) {
        Float result = null;
        String[] variables = line.split(DELIMITER);
        if (variables.length == VARIABLE_COUNT) {
            try {
                result = Float.parseFloat(variables[FL_INDEX]);
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
                return null;
            }
        }
        return result;
    }
}
