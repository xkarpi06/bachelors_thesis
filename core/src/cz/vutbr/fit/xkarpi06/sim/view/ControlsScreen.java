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
import cz.vutbr.fit.xkarpi06.sim.MoonLanding;

public class ControlsScreen implements Screen {

    private final MoonLanding game;
    private OrthographicCamera camera;

    /* disposables */
    private Stage stage;

    private String[][] controls = new String[][] {
            {"'Left Arrow'","step simulation backwards"},
            {"'Right Arrow'","step simulation forwards"},
            {"'space'","pause/play simulation"},
            {"'r'","change simulation flow backwards/forwards"},
    };

    private TextButton backButton;

    public ControlsScreen(MoonLanding moonLanding) {
        game = moonLanding;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 640, 480);
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        for (int i = 0; i < controls.length; i++) {
            
        }

        backButton = new TextButton("back", game.skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
                dispose();
            }
        });
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
