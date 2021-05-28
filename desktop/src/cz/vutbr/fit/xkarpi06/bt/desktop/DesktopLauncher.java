package cz.vutbr.fit.xkarpi06.bt.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import cz.vutbr.fit.xkarpi06.bt.MoonLanding;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("Beresheet lunar descent visualization");
		config.setWindowSizeLimits(960, 540, 3840, 2160);

		new Lwjgl3Application(new MoonLanding(), config);
	}
}
