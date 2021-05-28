package cz.vutbr.fit.xkarpi06.bt.input.parse;

import cz.vutbr.fit.xkarpi06.bt.input.load.ProjectFiles;
import cz.vutbr.fit.xkarpi06.bt.input.parse.strategy.FloatParser;
import cz.vutbr.fit.xkarpi06.bt.input.parse.strategy.ParsingStrategy;
import cz.vutbr.fit.xkarpi06.bt.input.parse.strategy.TrajectoryParser;

/**
 * Context for strategy design pattern
 * @author xkarpi06
 * created: 19-4-2020, xkarpi06
 */
public class StringParser {

    private ParsingStrategy strategy;

    /**
     * Constructor
     * @param strategy input
     */
    public StringParser(ParsingStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Smart Constructor
     * Chooses strategy based on file name
     * @param fileName input
     */
    public StringParser(String fileName) {
        switch (fileName) {
            case ProjectFiles.SRC_TRAJECTORY_POLAR:
            case ProjectFiles.SRC_TRAJECTORY_CARTESIAN:
                this.strategy = new TrajectoryParser();
                break;
            case ProjectFiles.SRC_TIMES:
            case ProjectFiles.SRC_PITCH:
            case ProjectFiles.SRC_MASS:
            case ProjectFiles.SRC_VR:
            case ProjectFiles.SRC_VTH:
                this.strategy = new FloatParser();
                break;

            default:
                this.strategy = null;
                break;
        }
    }

    /**
     * Executes particular strategy how to parse line to Object
     * @param line input
     * @return Object based on the strategy, can be null
     */
    public Object exectueStrategy(String line) {
        return (this.strategy == null) ? null : this.strategy.parseLine(line);
    }
}
