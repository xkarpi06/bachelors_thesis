package cz.vutbr.fit.xkarpi06.bt.input.load;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Keeps track of location for neccesary files and directories
 * @author xkarpi06
 * @version 1.0
 * @since 1.0
 * created: 19-4-2020, xkarpi06
 * updated:
 */
public class ProjectFiles {

    /** Current working directory */
    public static final String WORKING_DIRECTORY = System.getProperty("user.dir");

    /** Directory containing input trajectory data */
    public static final String DATA_DIRECTORY = WORKING_DIRECTORY.concat("/").concat("data");

    /** Directory for saving logs */
    public static final String LOGS_DIRECTORY = WORKING_DIRECTORY.concat("/").concat("log");

    /** File for saving logs */
    public static final String LOGS_FILE = "log.txt";

    /** Name of source file for trajectory times */
    public static final String SRC_TIMES = "timeline.txt";

    /** Name of source file for trajectory in polar coordinates */
    public static final String SRC_TRAJECTORY_POLAR = "trajectory_polar.txt";

    /** Name of source file for trajectory in cartesian coordinates */
    public static final String SRC_TRAJECTORY_CARTESIAN = "trajectory_cartesian.txt";

    /** Name of source file for pitch history */
    public static final String SRC_PITCH = "pitch.txt";

    /** Name of source file for mass history */
    public static final String SRC_MASS = "mass.txt";

    /** Name of source file for vertical velocity history */
    public static final String SRC_VR = "velocity_vertical.txt";

    /** Name of source file for horizontal velocity history */
    public static final String SRC_VTH = "velocity_horizontal.txt";

    /** Name of source file for 3D model of spacecraft */
    public static final String SHIP_SOURCE_FILE = "Beresheet_centered_mass_5K_v2.g3dj";

    /** Name of source file for 3D model of moon */
    public static final String MOON_SOURCE_FILE = "Moon5K_v6.g3dj";

    public static final Set<String> SRC_FILES = new HashSet<>(Arrays.asList(
            SRC_TIMES,
            SRC_TRAJECTORY_POLAR,
            SRC_TRAJECTORY_CARTESIAN,
            SRC_PITCH,
            SRC_MASS,
            SRC_VR,
            SRC_VTH
    ));

}
