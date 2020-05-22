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
 * and VisUi user interface toolkit for libGDX under the Apache 2.0 licence.
 *
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 * libGDX: https://libgdx.badlogicgames.com/index.html
 * VisUi: https://github.com/kotcrab/vis-ui
 *
 * @author xkarpi06
 * created: 03-05-2020
 * updated:
 */
public class MoonLanding extends Game {

    /** FileChooser instance so it is not created again for every main menu entrance */
    public FileChooser fileChooser;

    public AssetManager assets;
    public Trajectory3D trajectory;
    public Simulation sim;

    /** Directory with trajectory data */
    public String trajectoryDir;

    /** True if trajectoryDir is changed in main menu */
    public boolean trajectoryDirWasChanged = false;

    /** err msg to display in main menu */
    public String errMsg;

    /**
     * application entry, called after start of Application
     */
    @Override
    public void create() {
        VisUI.load();
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
        assets.dispose();
        if (trajectory != null) { trajectory.dispose(); }
        VisUI.dispose();
    }

    /**
     * Sets game trajectory object and also sets game.sim's trajectory
     * @param t new trajectory
     */
    public void setTrajectory(Trajectory3D t) {
        trajectory = t;
        sim.setTrajectory(trajectory);
    }
}
