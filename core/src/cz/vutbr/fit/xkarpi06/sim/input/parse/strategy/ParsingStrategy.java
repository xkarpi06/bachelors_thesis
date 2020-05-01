package cz.vutbr.fit.xkarpi06.sim.input.parse.strategy;

/**
 * Strategy interface for strategy design pattern
 * @author xkarpi06
 * @version 1.0
 * @since 1.0
 * created: 19-4-2020, xkarpi06
 */
public interface ParsingStrategy {
    public Object parseLine(String line);
}
