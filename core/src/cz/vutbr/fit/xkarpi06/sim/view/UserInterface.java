package cz.vutbr.fit.xkarpi06.sim.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.Align;
import cz.vutbr.fit.xkarpi06.sim.model.Simulation;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * https://github.com/libgdx/libgdx/blob/master/tests/gdx-tests/src/com/badlogic/gdx/tests/UISimpleTest.java#L37
 */
public class UserInterface {

    // model
    private Simulation sim;
    private MoonScene ms;

    // disposables
    private Stage stage;
    private Skin skin;
    private BitmapFont font;

    // default view
    private static final float W_WIDTH = 640;
    private static final float W_HEIGHT = 480;
    private static final float TS_HEIGHT = 150;

    // changing elements
    // state labels
    private Label altitudeLabel;
    private Label distanceLabel;
    private Label pitchLabel;
    private Label elapsedTimeLabel;
    private Label massLabel;
    private Label verticalVelLabel;
    private Label horizontalVelLabel;

    // ui elements
    private TextButton pauseButton;
    private TextButton reverseButton;
    private Slider slider;
    private Label speedValueLabel;
    private TextButton minusButton;
    private TextButton plusButton;

    private TextButton resetCamButton;

    // other variables
    private boolean simWasRunningBeforeSliderWasDragged = true;
    DateTimeFormatter elapsedTimeFormat = DateTimeFormatter.ofPattern("H:mm:ss");

    /**
     * Constructor
     * @param sim Simulation for setting up UI actions
     */
    public UserInterface(Simulation sim, MoonScene moonScene) {
        this.sim = sim;
        this.ms = moonScene;
        stage = new Stage();
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        final float EDGE_PADDING = 5f;

        // aligning tables
        Table topLeftAlignment = new Table();
        topLeftAlignment.setFillParent(true);
        topLeftAlignment.align(Align.topLeft);

        Table bottomLeftAlignment = new Table();
        bottomLeftAlignment.setFillParent(true);
        bottomLeftAlignment.align(Align.bottomLeft);

        Table topRightAlignment = new Table();
        topRightAlignment.setFillParent(true);
        topRightAlignment.align(Align.topRight);

        stage.addActor(topLeftAlignment);
        stage.addActor(bottomLeftAlignment);
        stage.addActor(topRightAlignment);

        // label style for labels
        Label.LabelStyle labelStyle = createLabelStyle();

        Table stateVariables = createStateVarTable(labelStyle);
        topLeftAlignment.add(stateVariables).pad(EDGE_PADDING);

        Table simControls = createSimControlsTable(labelStyle);
        bottomLeftAlignment.add(simControls).pad(EDGE_PADDING);

        createResetCamButton();
        topRightAlignment.add(resetCamButton).pad(EDGE_PADDING);

//        stateVariables.setDebug(true);
//        simControls.setDebug(true);
    }

    public void render () {
        // update state labels
        altitudeLabel.setText(String.format("Altitude: %.0f m", sim.getAltitude()));
        distanceLabel.setText(String.format("Dist-rem: %.1f km", sim.getDownrangeDistance()/1000f));
        pitchLabel.setText(String.format("Pitch: %.1f deg", sim.getPitch()));
        massLabel.setText(String.format("Mass: %.1f kg", sim.getMass()));
        verticalVelLabel.setText(String.format("V-vert: %.1f m/s", sim.getVerticalVelocity()));
        horizontalVelLabel.setText(String.format("V-horiz: %.1f m/s", sim.getHorizontalVelocity()));

        // update pause button
        String pauseButtonText = sim.isRunning() ? "Pause" : "Play";
        pauseButton.setText(pauseButtonText);

        elapsedTimeLabel.setText(LocalTime.of(0,0).plus(sim.getElapsedTime()).format(elapsedTimeFormat));

        // update slider
        slider.setValue(sim.getPosition());

        // update sim speed value
        String speedFormat = (sim.getSpeed() < 1) ? "%.1f" : "%.0f";
        speedValueLabel.setText(String.format(speedFormat + "x", sim.getSpeed()));

        stage.act();
        stage.draw();
    }

    public void resize (int width, int height) {
        stage.getViewport().update(width, height, true);
//        topSection.setClip(true);
//        topSection.setTransform(true);
//        float tsHeight = (W_HEIGHT/height) * TS_HEIGHT;
//        topSection.setSize(W_WIDTH, tsHeight);
//        topSection.setPosition(0,W_HEIGHT-tsHeight);
    }

    public void dispose() {
        stage.dispose();
        skin.dispose();
        font.dispose();
    }

    public Stage getStage() {
        return stage;
    }

    // Helper methods --v

    private Label.LabelStyle createLabelStyle() {
        // set label style
        font = skin.getFont("default-font");
        font.getData().setScale(0.7f);
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        return labelStyle;
    }
    private Table createStateVarTable(Label.LabelStyle labelStyle) {
        altitudeLabel = new Label("", labelStyle);
        distanceLabel = new Label("", labelStyle);
        pitchLabel = new Label("", labelStyle);

        massLabel = new Label("", labelStyle);
        verticalVelLabel = new Label("", labelStyle);
        horizontalVelLabel = new Label("", labelStyle);

        Table stateVariables = new Table();

        stateVariables.columnDefaults(0).left();
//        stateVariables.columnDefaults(0).left().minWidth(125);
        stateVariables.add(altitudeLabel);
        stateVariables.row();
        stateVariables.add(distanceLabel);
        stateVariables.row();
        stateVariables.add(pitchLabel);
        stateVariables.row();
        stateVariables.add(massLabel);
        stateVariables.row();
        stateVariables.add(verticalVelLabel);
        stateVariables.row();
        stateVariables.add(horizontalVelLabel);
        return stateVariables;
    }

    private Table createSimControlsTable(Label.LabelStyle labelStyle) {
        final float GAP = 5f;
        final float TEXT_PAD = 2f;

        Table simControls = new Table();
        simControls.defaults().padLeft(GAP);   // sets default cell alignment to top and left padding

        createPauseButton();
        simControls.add(pauseButton).padLeft(0).minWidth(43);

        createReverseButton();
        simControls.add(reverseButton);

        elapsedTimeLabel = new Label("", labelStyle);
        simControls.add(elapsedTimeLabel).minWidth(43);

        createSlider();
        simControls.add(slider).padBottom(TEXT_PAD).minWidth(362);

        Label speedLabel = new Label("speed:", labelStyle);
        simControls.add(speedLabel).padBottom(TEXT_PAD);

        speedValueLabel = new Label("1x", labelStyle);
        simControls.add(speedValueLabel).left().padBottom(TEXT_PAD).minWidth(28);

        createMinusButton();
        simControls.add(minusButton);

        createPlusButton();
        simControls.add(plusButton);

        return simControls;
    }

    private void createPauseButton() {
        pauseButton = new TextButton("Pause", skin);
        pauseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (sim.isRunning()) {
                    sim.stop();
                } else {
                    sim.run();
                }
            }
        });
    }

    private void createReverseButton() {
        reverseButton = new TextButton("Reverse", skin);
        reverseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sim.reverse();
            }
        });
    }

    private void createSlider() {
        slider = new Slider(0, 1, 0.0001f, false, skin);
        slider.addListener(new DragListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                simWasRunningBeforeSliderWasDragged = sim.isRunning();
                sim.stop();
                sim.setShipPosition(slider.getValue());
                return true;
            }
            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                sim.setShipPosition(slider.getValue());
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                sim.setShipPosition(slider.getValue());
                if (simWasRunningBeforeSliderWasDragged) {
                    sim.run();
                }
            }
        });
    }

    private void createMinusButton() {
        minusButton = new TextButton("-", skin);
        minusButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sim.speedDown();
            }
        });
    }

    private void createPlusButton() {
        plusButton = new TextButton("+", skin);
        plusButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sim.speedUp();
            }
        });
    }

    private void createResetCamButton() {
        resetCamButton = new TextButton("Reset camera", skin);
        resetCamButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ms.resetCamera();
            }
        });
    }

}
