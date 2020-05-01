package cz.vutbr.fit.xkarpi06.sim.output;

import cz.vutbr.fit.xkarpi06.sim.input.load.ProjectFiles;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Manages creating log files and setting Logger.
 * @author xkarpi06
 * created: 19-4-2020
 */
public class MyLog {

    /** Level determines what to log */
    public static final Level LOG_LEVEL = Level.ALL;

    private static FileHandler fileHandler;

    /**
     * Sets Logger properties in whole project
     */
    public static Logger getLogger(String name) {
        if (fileHandler == null) {
            initFileHandler();
        }
        Logger logger = Logger.getLogger(name);
        logger.setLevel(LOG_LEVEL);
        logger.addHandler(fileHandler);
        return logger;
    }

    /**
     * Initializes file handler for saving logs
     */
    private static void initFileHandler() {
        // create log directory
        File logDir = new File(ProjectFiles.LOGS_DIRECTORY);
        logDir.mkdirs();
        try {
            fileHandler = new FileHandler(ProjectFiles.LOGS_DIRECTORY.concat("/").concat(ProjectFiles.LOGS_FILE));
            // ensures human readable logs, alternative (and default) is new XMLFormatter()
            fileHandler.setFormatter(new SimpleFormatter());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



