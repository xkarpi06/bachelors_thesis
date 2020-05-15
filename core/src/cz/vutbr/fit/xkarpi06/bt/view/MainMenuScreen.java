package cz.vutbr.fit.xkarpi06.bt.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.SingleFileChooserListener;
import cz.vutbr.fit.xkarpi06.bt.MoonLanding;
import cz.vutbr.fit.xkarpi06.bt.input.load.ProjectFiles;

import java.io.File;

/**
 * Main menu view
 * @author xkarpi06
 * created: 03-05-2020
 * updated:
 */
public class MainMenuScreen implements Screen {

    public final MoonLanding game;
    public OrthographicCamera camera;

    Table dirSelect;
    VisLabel errLine;
    VisTextField dirTextField;

    /* disposables */
    private Stage stage;

    public MainMenuScreen(MoonLanding moonLanding) {
        game = moonLanding;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 960, 540);
        stage = new Stage(new ExtendViewport(960, 540, camera));
        Gdx.input.setInputProcessor(stage);

        setFileChooser();

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        VisLabel title = new VisLabel("Moon Landing Visualizer", Color.WHITE);
        title.setFontScale(1.5f);

        createDirSelectUI();

        VisTextButton startButton = createStartButton();
        VisTextButton controlsButton = createControlsButton();
        VisTextButton exitButton = createExitButton();

        table.defaults().minWidth(150).padBottom(10);
        table.add(title).padBottom(60);
        table.row();
        table.add(dirSelect).padBottom(60);
        table.row();
        table.add(startButton);
        table.row();
        table.add(controlsButton);
        table.row();
        table.add(exitButton);
    }

    private void setFileChooser() {
        if (game.fileChooser == null) {
            FileChooser.setDefaultPrefsName(MainMenuScreen.class.getPackage().toString() + ".filechooser");
            System.out.println(MainMenuScreen.class.getPackage().toString());
            game.fileChooser = new FileChooser(FileChooser.Mode.OPEN);
            game.fileChooser.setSelectionMode(FileChooser.SelectionMode.DIRECTORIES);
            game.fileChooser.setSize(stage.getWidth()*0.9f, stage.getHeight()*0.7f);
            game.fileChooser.setDirectory(ProjectFiles.WORKING_DIRECTORY);
        }
    }

    /**
     * Creates start button with its listener
     * @return the button
     */
    private VisTextButton createStartButton() {
        VisTextButton startButton = new VisTextButton("Start");
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!resolveTrajectoryDir()) {
                    game.errMsg = "Not a directory.";
                    showErrors();
                } else {
                    game.setScreen(new LoadingScreen(game));
                    dispose();
                }
            }
        });
        return startButton;
    }

    /**
     * creates controls button with its listener
     * @return the button
     */
    private VisTextButton createControlsButton() {
        VisTextButton controlsButton = new VisTextButton("Controls");
        controlsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new ControlsScreen(game));
                dispose();
            }
        });
        return controlsButton;
    }

    /**
     * Creates exit button and its listener
     * @return created button
     */
    private VisTextButton createExitButton() {
        VisTextButton exitButton = new VisTextButton("Exit");
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dispose();
                Gdx.app.exit();
            }
        });
        return exitButton;
    }

    /**
     * Creates part of UI, that displays selected directory, search button and eventual errors
     * @return table
     */
    private Table createDirSelectUI() {
        dirSelect = new Table();

        VisLabel dirLabel = new VisLabel("Load trajectory from:");

        dirTextField = new VisTextField();
        dirTextField.setText(game.trajectoryDir);
        game.fileChooser.setListener(new SingleFileChooserListener() {
            @Override
            protected void selected(FileHandle file) {
                dirTextField.setText(file.file().getAbsolutePath());
            }
        });
        VisTextButton selectButton = new VisTextButton("...");
        selectButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stage.addActor(game.fileChooser.fadeIn());
            }
        });
        dirSelect.add(dirLabel).colspan(2).padBottom(5);
        dirSelect.row();
        dirSelect.add(dirTextField).width(stage.getWidth()*0.7f);
        dirSelect.add(selectButton).padLeft(5);

        showErrors();

        return dirSelect;
    }

    /**
     * Checks if trajectory dir was changed since last time
     * @return true if app dir is set, false if no dir is set
     */
    private boolean resolveTrajectoryDir() {
        if (game.trajectoryDir == null || !game.trajectoryDir.equals(dirTextField.getText())) {
            game.trajectoryDir = dirTextField.getText();
            game.trajectoryDirWasChanged = true;
        }

        return game.trajectoryDir == null || new File(game.trajectoryDir).isDirectory();
    }

    /**
     * Checks if any errors have ocurred recently and prints it below selection textbox
     */
    private void showErrors(){
        if (game.errMsg != null) {
            if (errLine == null) {
                errLine = new VisLabel(game.errMsg, Color.RED);
                dirSelect.row();
                dirSelect.add(errLine).colspan(2).padTop(5);
            } else {
                errLine.setText(game.errMsg);
            }
            game.errMsg = null;
        }
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
