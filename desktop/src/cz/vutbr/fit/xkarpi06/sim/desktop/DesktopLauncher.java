package cz.vutbr.fit.xkarpi06.sim.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import cz.vutbr.fit.xkarpi06.sim.input.load.Trajectory3DCreator;
import cz.vutbr.fit.xkarpi06.sim.view.MoonScene;
import cz.vutbr.fit.xkarpi06.sim.model.Trajectory3D;
import cz.vutbr.fit.xkarpi06.sim.input.load.ProjectFiles;
import cz.vutbr.fit.xkarpi06.sim.input.load.Trajectory3DLoader;

import java.awt.*;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("Lunar landing simulation");
//		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//		config.setWindowSizeLimits(800, 480, (int) screenSize.getWidth(), (int) screenSize.getHeight());
		config.setWindowSizeLimits(960, 540, 3840, 2160);

		// load trajectory
		final float SCALE = 0.001f;
		Trajectory3D trajectory = Trajectory3DLoader.load(ProjectFiles.DATA_DIRECTORY);
//        Trajectory3D trajectory = Trajectory3DCreator.create();
		if (trajectory != null) {
			new Lwjgl3Application(new MoonScene(trajectory, SCALE), config);
		}
	}
}
