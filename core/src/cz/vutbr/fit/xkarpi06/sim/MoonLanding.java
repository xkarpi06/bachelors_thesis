package cz.vutbr.fit.xkarpi06.sim;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import cz.vutbr.fit.xkarpi06.sim.input.load.ProjectFiles;
import cz.vutbr.fit.xkarpi06.sim.input.load.Trajectory3DLoader;
import cz.vutbr.fit.xkarpi06.sim.model.Trajectory3D;
import cz.vutbr.fit.xkarpi06.sim.view.LoadingScreen;
import cz.vutbr.fit.xkarpi06.sim.view.MainMenuScreen;

/**
 *
 */
public class MoonLanding extends Game {

    public SpriteBatch batch;
    public Skin skin;
    public BitmapFont font;

    @Override
    public void create() {
        // load trajectory
        //Trajectory3D trajectory = Trajectory3DLoader.load(ProjectFiles.DATA_DIRECTORY);
        //Trajectory3D trajectory = Trajectory3DCreator.create();
//        if (trajectory != null) {
//			new Lwjgl3Application(new MoonScene(trajectory), config);
//        }
        batch = new SpriteBatch();
        skin = new Skin(Gdx.files.internal(ProjectFiles.SKIN_PATH));
        font = skin.getFont("default-font");
        this.setScreen(new MainMenuScreen(this));
//        this.setScreen(new MainMenuScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        skin.dispose();
    }
}
