package cz.vutbr.fit.xkarpi06.sim.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import cz.vutbr.fit.xkarpi06.sim.MoonLanding;
import cz.vutbr.fit.xkarpi06.sim.input.load.Trajectory3DCreator;
import cz.vutbr.fit.xkarpi06.sim.view.MoonScene;
import cz.vutbr.fit.xkarpi06.sim.model.Trajectory3D;
import cz.vutbr.fit.xkarpi06.sim.input.load.ProjectFiles;
import cz.vutbr.fit.xkarpi06.sim.input.load.Trajectory3DLoader;

import java.awt.*;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("Beresheet lunar descent simulation");
		config.setWindowSizeLimits(960, 540, 3840, 2160);

		new Lwjgl3Application(new MoonLanding(), config);
	}
}
