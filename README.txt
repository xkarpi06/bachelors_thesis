********************************************************************************
*                                                                              *
*   Project: Bachelor's thesis 2020, Lunar Landing Simulation                  *
*   Author: Jakub Karpíšek, xkarpi06@stud.fit.vutbr.cz                         *
*   Contents: source files of Moonlanding Visualization App                    *
*   Target platform: Desktop                                                   *
*   OS: Linux, Windows                                                         *
*   Dependencies: java 1.8                                                     *
*                                                                              *
********************************************************************************

********************************************************************************
*   Application description:                                                   *
********************************************************************************

The general idea of the application is to provide users a tool, which would help
them to easily understand a lunar descent trajectory. The application displays
Moon and Beresheet spacecraft in 3D space, loads a trajectory from provided
source, and plays an animation of the spacecraft following the trajectory from 
start to end. Users can interact with the animation to for example change its
pace, jump into different point in time, etc. State variables in top left corner 
provide additional trajectory information to create broad picture for users.

********************************************************************************
*   Application usage:                                                         *
********************************************************************************

Run:
	linux:		run script in root directory	"linux-run"
	windows:	run file in root directorz		"win-run"
				or acces .exe directly at 		/bin/MoonLanding.exe

Build: 
	in root directory run: ./gradlew build
	class files will be created in:
	/core/build/classes/java/main
	/desktop/build/classes/java/main

Jar distribution:
	in root directory run: ./gradlew desktop:dist
	jar file will be created:
	/desktop/build/libs/desktop-1.0.jar

********************************************************************************
*   Trajectory dataset:                                                        *
********************************************************************************

Trajectory is stored in a single directory containing from 2 to 6 source files.
Naming of the files and following inner formats of each file is mandatory.
Information is separated by new line, format represents syntax of one line

Mandatory files: (times + one of polar/cartesian)

timeline.txt - times corresponding to coordinates, including start and finish
    format: value

trajectory_polar.txt - polar coordinates of trajectory in one plane
    format: r,theta,phi		(decimal dot, phi=0 for 2D trajectory in x-y plane)

trajectory_cartesian.txt - cartesian coordinates of trajectory in one plane
    format: x,y,z			(decimal dot, z=0 for 2D trajectory in x-y plane)

Optional files:

pitch.txt - defines pitch history in time
    format: value

mass.txt - defines mass history in time
    format: value

velocity_horizontal.txt - defines horizontal velocity history in time
    format: value

velocity_vertical.txt - defines vertical velocity history in time
    format: value

********************************************************************************
*   End of file                                                                *
********************************************************************************





