package cz.vutbr.fit.xkarpi06.bt;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import cz.vutbr.fit.xkarpi06.bt.model.Simulation;
import cz.vutbr.fit.xkarpi06.bt.model.Trajectory3D;
import cz.vutbr.fit.xkarpi06.bt.view.MainMenuScreen;

/**
 * Main application entry
 *
 * Developed using libGDX game engine under the Apache 2.0 licence
 *
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 * @author xkarpi06
 * created: 03-05-2020
 * updated:
 */
public class MoonLanding extends Game {

//    public Skin skin;
//    public BitmapFont font;
    public FileChooser fileChooser;
    public AssetManager assets;
    public Trajectory3D trajectory;
    public Simulation sim;
    public String trajectoryDir;
    public boolean trajectoryDirWasChanged = false;
    public String errMsg;

    /**
     * application entry, called after start of Application
     */
    @Override
    public void create() {
        VisUI.load();
//        skin = new Skin(Gdx.files.internal(ProjectFiles.SKIN_PATH));
//        font = skin.getFont(ProjectFiles.FONT_PATH);
        assets = new AssetManager();
        sim = new Simulation();
        this.setScreen(new MainMenuScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
//        font.dispose();
//        skin.dispose();
        assets.dispose();
        if (trajectory != null) { trajectory.dispose(); }
        VisUI.dispose();
    }

    public void setTrajectory(Trajectory3D t) {
        trajectory = t;
        sim.setTrajectory(trajectory);
    }
}
