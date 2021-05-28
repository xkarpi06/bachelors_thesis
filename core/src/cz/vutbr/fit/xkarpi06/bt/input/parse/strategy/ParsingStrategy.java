package cz.vutbr.fit.xkarpi06.bt.input.parse.strategy;

/**
 * Strategy interface for strategy design pattern
 * @author xkarpi06
 * created: 19-4-2020, xkarpi06
 */
public interface ParsingStrategy {
    Object parseLine(String line);
}
