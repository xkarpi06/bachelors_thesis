package cz.vutbr.fit.xkarpi06.sim.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import cz.vutbr.fit.xkarpi06.sim.MoonLanding;

/**
 * Loading screen view, displayed when loading assets
 * @author xkarpi06
 * created: 03-05-2020
 * updated:
 */
public class LoadingScreen implements Screen {

    final MoonLanding game;
    OrthographicCamera camera;

    private float loadingPhase = 0;

    public LoadingScreen(final MoonLanding moonLanding) {
        game = moonLanding;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 640, 480);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);    // background color of main menu
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        String loadingStr = "Loading assets";
        loadingPhase += 2*delta;
        if ((int)loadingPhase % 3 == 0) { loadingStr += "."; }
        else if ((int)loadingPhase % 3 == 1) { loadingStr += ".."; }
        else if ((int)loadingPhase % 3 == 2) { loadingStr += "..."; }

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.font.draw(game.batch, loadingStr, 50, 50);
        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {

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

    }
}
