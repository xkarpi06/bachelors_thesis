package cz.vutbr.fit.xkarpi06.sim.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import cz.vutbr.fit.xkarpi06.sim.MoonLanding;

/**
 * Main menu view
 * @author xkarpi06
 * created: 03-05-2020
 * updated:
 */
public class MainMenuScreen implements Screen {

    private final MoonLanding game;
    private OrthographicCamera camera;

    /* disposables */
    private Stage stage;

    private TextButton startButton;
    private TextButton controlsButton;
    private TextButton exitButton;

    public MainMenuScreen(MoonLanding moonLanding) {
        game = moonLanding;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 640, 480);
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Label title = new Label("Moon Landing Simulator", new Label.LabelStyle(game.font, Color.WHITE));
        title.setFontScale(1.5f);

        startButton = new TextButton("Start", game.skin);
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new LoadingScreen(game));
                dispose();
            }
        });
        controlsButton = new TextButton("Controls", game.skin);
        controlsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new ControlsScreen(game));
                dispose();
            }
        });
        exitButton = new TextButton("Exit", game.skin);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dispose();
                Gdx.app.exit();
            }
        });

        table.defaults().minWidth(100);
        table.add(title);
        table.row();
        table.add(startButton);
        table.row();
        table.add(controlsButton);
        table.row();
        table.add(exitButton);
        table.setDebug(true);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);    // background color of main menu
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
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
}
