package cz.vutbr.fit.xkarpi06.bt.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.kotcrab.vis.ui.widget.VisLabel;
import cz.vutbr.fit.xkarpi06.bt.MoonLanding;
import cz.vutbr.fit.xkarpi06.bt.input.load.ProjectFiles;
import cz.vutbr.fit.xkarpi06.bt.input.load.Trajectory3DLoader;

/**
 * Loading screen view, displayed when loading assets
 * @author xkarpi06
 * created: 03-05-2020
 * updated:
 */
public class LoadingScreen implements Screen {

    public final MoonLanding game;
    private OrthographicCamera camera;
    private Stage stage;
    private VisLabel loadingLabel;

    private LoadingPhase loadingPhase;
    private float loadingIterator = 0;

    public LoadingScreen(final MoonLanding moonLanding) {
        game = moonLanding;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 640, 480);

        stage = new Stage();
        Table table = new Table().left().padLeft(stage.getWidth()*0.43f);
        table.setFillParent(true);
        loadingLabel = new VisLabel("");
        stage.addActor(table);
        table.add(loadingLabel);

        // load assets if not loaded yet
        try {
            game.assets.get(ProjectFiles.MOON_SOURCE_FILE, Model.class);
        } catch (GdxRuntimeException e) {
            game.assets.load(ProjectFiles.MOON_SOURCE_FILE, Model.class);
        }
        try {
            game.assets.get(ProjectFiles.SHIP_SOURCE_FILE, Model.class);
        } catch (GdxRuntimeException e) {
            game.assets.load(ProjectFiles.SHIP_SOURCE_FILE, Model.class);
        }
        loadingPhase = LoadingPhase.ASSETS;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);    // background color of main menu
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        drawLoading(delta);

        switch (loadingPhase) {
            case TRAJECTORY:
                if (game.trajectory == null || game.trajectoryDirWasChanged) {
                    game.setTrajectory(Trajectory3DLoader.load(game.trajectoryDir));
                    game.trajectoryDirWasChanged = false;
                }
                if (game.trajectory == null) {  // failed to load trajectory
                    game.errMsg = "Could not load trajectory.";
                    game.setScreen(new MainMenuScreen(game));
                    dispose();
                }
                loadingPhase = LoadingPhase.FINISHED;
                break;
            case ASSETS:
                if (game.assets.update()) {
//                    loadTrajectoryInOtherThread();
                    loadingPhase = LoadingPhase.TRAJECTORY;
                }
                break;
            case FINISHED:
                game.setScreen(new VisualizationScreen(game));
                dispose();
                break;
            case START:
            default: break;
        }
    }

    /**
     * Draws loading string onto screen.
     * @param delta time delta
     */
    private void drawLoading(float delta) {
        loadingLabel.setText(getLoadingString(delta));
        stage.act();
        stage.draw();
    }

    /**
     * Determines loading string based on loading phase
     * @param delta time delta
     * @return string
     */
    private String getLoadingString(float delta) {
        String loadingStr;
        switch (loadingPhase) {
            case START:
                loadingStr = "Loading is starting";
                break;
            case TRAJECTORY:
                loadingStr = "Loading trajectory";
                break;
            case ASSETS:
                loadingStr = "Loading assets";
                break;
            case FINISHED:
                loadingStr = "Starting application";
                break;
            default:
                loadingStr = "";
                break;
        }
        loadingIterator += 2*delta;
        if ((int) loadingIterator % 3 == 0) { loadingStr += "."; }
        else if ((int) loadingIterator % 3 == 1) { loadingStr += ".."; }
        else if ((int) loadingIterator % 3 == 2) { loadingStr += "..."; }
        return loadingStr;
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    /**
     * Loading phases
     */
    public enum LoadingPhase {
        START,
        TRAJECTORY,
        ASSETS,
        FINISHED
    }

}
