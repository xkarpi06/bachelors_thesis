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

    // other variables
    private boolean simWasRunningBeforeSliderWasDragged = true;
    DateTimeFormatter elapsedTimeFormat = DateTimeFormatter.ofPattern("H:mm:ss");

    /**
     * Constructor
     * @param sim Simulation for setting up UI actions
     */
    public UserInterface(Simulation sim) {
        this.sim = sim;
        stage = new Stage();
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // main table
        Table table = new Table();
        table.setFillParent(true);
        table.align(Align.topLeft);

        stage.addActor(table);

        Label.LabelStyle labelStyle = createLabelStyle();

        Table stateVariables = createStateVarTable(labelStyle);
        table.add(stateVariables).padLeft(5f).padTop(5f);

        Table simControls = createSimControlsTable(labelStyle);
        table.add(simControls).top().padLeft(20f).padTop(5f);

//        table.setDebug(true);   // TODO
//        stateVariables.setDebug(true);
//        simControls.setDebug(true);
    }

    public void render () {
        // update state labels
        altitudeLabel.setText(String.format("Altitude: %.0f m", sim.getAltitude()));
        distanceLabel.setText(String.format("Distance: %.1f km", sim.getDownrangeDistance()/1000f));
        pitchLabel.setText(String.format("Pitch: %.1f deg", sim.getPitch()));
        elapsedTimeLabel.setText(String.format("Elapsed time: %s", LocalTime.of(0,0).plus(sim.getElapsedTime()).format(elapsedTimeFormat)));
        massLabel.setText(String.format("Mass: %.0f kg", sim.getMass()));
        verticalVelLabel.setText(String.format("v(vertical): %.1f m/s", sim.getVerticalVelocity()));
        horizontalVelLabel.setText(String.format("v(horizontal): %.1f m/s", sim.getHorizontalVelocity()));

        // update pause button
        String pauseButtonText = sim.isRunning() ? "Pause" : "Play";
        pauseButton.setText(pauseButtonText);

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
        altitudeLabel = new Label("Altitude: 210000 m", labelStyle);
        distanceLabel = new Label("Distance: 5000 km", labelStyle);
        pitchLabel = new Label("Pitch: -90 deg", labelStyle);
        elapsedTimeLabel = new Label("Elapsed time: 0:00", labelStyle);
        massLabel = new Label("Mass: 200 kg", labelStyle);
        verticalVelLabel = new Label("v(vertical): 2000 m/s", labelStyle);
        horizontalVelLabel = new Label("v(horizontal): 3000 m/s", labelStyle);

        Table stateVariables = new Table();

        stateVariables.columnDefaults(0).left().minWidth(125);
        stateVariables.add(altitudeLabel);
        stateVariables.row();
        stateVariables.add(distanceLabel);
        stateVariables.row();
        stateVariables.add(pitchLabel);
        stateVariables.row();
        stateVariables.add(elapsedTimeLabel);
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

        createSlider();
        simControls.add(slider).padTop(TEXT_PAD);

        Label speedLabel = new Label("speed:", labelStyle);
        simControls.add(speedLabel).padTop(TEXT_PAD);

        speedValueLabel = new Label("1x", labelStyle);
        simControls.add(speedValueLabel).left().padTop(TEXT_PAD).minWidth(28);

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
        slider = new Slider(0, 1, 0.001f, false, skin);
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

}
