package cz.vutbr.fit.xkarpi06.bt.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTextButton;
import cz.vutbr.fit.xkarpi06.bt.MoonLanding;

/**
 * Controls info view
 * @author xkarpi06
 * created: 03-05-2020
 * updated:
 */
public class ControlsScreen implements Screen {

    private final MoonLanding game;
    private OrthographicCamera camera;

    /* disposables */
    private Stage stage;

    private String[][] controls = new String[][] {
            {"left arrow","step simulation backwards"},
            {"right arrow","step simulation forwards"},
            {"space bar","pause/play simulation"},
            {"r","change simulation flow backwards/forwards"},
            {"left mouse","change camera angle"},
            {"mouse scroll","zoom in/out"},
    };

    public ControlsScreen(MoonLanding moonLanding) {
        game = moonLanding;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 640, 480);
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);
        table.top().padTop(50);
        table.defaults().padBottom(20);
        stage.addActor(table);

        table.columnDefaults(0).padLeft(100);
        table.columnDefaults(1).expandX().left().padLeft(30);

        VisLabel controlHead = new VisLabel("Key:", Color.WHITE);
        VisLabel effectHead= new VisLabel("Effect:", Color.GRAY);
//        controlHead.setFontScale(1.4f);
//        effectHead.setFontScale(1.4f);

        table.add(controlHead);
        table.add(effectHead);
        table.row().padTop(25);

        for (int i = 0; i < controls.length; i++) {
            VisLabel control = new VisLabel(controls[i][0], Color.WHITE);
            VisLabel effect = new VisLabel(controls[i][1], Color.GRAY);
//            control.setFontScale(1.2f);
//            effect.setFontScale(1.2f);
            table.add(control);
            table.add(effect);
            table.row();
        }

        VisTextButton backButton = new VisTextButton("back");
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
                dispose();
            }
        });

        Table bottom = new Table();
        bottom.setFillParent(true);
        bottom.bottom();
        stage.addActor(bottom);
        bottom.add(backButton).width(150).padBottom(50);
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
