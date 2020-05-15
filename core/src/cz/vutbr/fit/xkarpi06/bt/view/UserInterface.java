package cz.vutbr.fit.xkarpi06.bt.view;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSlider;
import com.kotcrab.vis.ui.widget.VisTextButton;
import cz.vutbr.fit.xkarpi06.bt.model.Simulation;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Displays interactive layer on top of Visualization screen
 * @author xkarpi06
 * created: 27-04-2020, xkarpi06
 * updated: 14-05-2020, added VisUi
 */
public class UserInterface {

    // model
    private Simulation sim;
    private VisualizationScreen simScreen;

    // disposables
    private Stage stage;

    // changing elements
    // state labels
    private VisLabel altitudeLabel;
    private VisLabel distanceLabel;
    private VisLabel pitchLabel;
    private VisLabel elapsedTimeLabel;
    private VisLabel massLabel;
    private VisLabel verticalVelLabel;
    private VisLabel horizontalVelLabel;

    // ui elements
    private VisTextButton pauseButton;
    private VisTextButton reverseButton;
    private VisSlider slider;
    private VisLabel speedValueLabel;
    private VisTextButton minusButton;
    private VisTextButton plusButton;

    private VisTextButton resetCamButton;
    private VisTextButton backToMenuButton;

    // other variables
    private boolean simWasRunningBeforeSliderWasDragged = true;
    DateTimeFormatter elapsedTimeFormat = DateTimeFormatter.ofPattern("H:mm:ss");
    private final float FONT_SCALE = 1;

    /**
     * Constructor
     * @param sim Simulation for setting up UI actions
     */
    public UserInterface(Simulation sim, VisualizationScreen visualizationScreen) {
        this.sim = sim;
        this.simScreen = visualizationScreen;
        stage = new Stage();

        final float EDGE_PADDING = 5f;

        // aligning tables
        Table topLeftAlignment = new Table();
        topLeftAlignment.setFillParent(true);
        topLeftAlignment.top().left().pad(EDGE_PADDING);

        Table bottomAlignment = new Table();
        bottomAlignment.setFillParent(true);
        bottomAlignment.bottom().pad(EDGE_PADDING);

        Table topRightAlignment = new Table();
        topRightAlignment.setFillParent(true);
        topRightAlignment.top().right().pad(EDGE_PADDING);

        stage.addActor(topLeftAlignment);
        stage.addActor(bottomAlignment);
        stage.addActor(topRightAlignment);

        Table stateVariables = createStateVarTable();
        topLeftAlignment.add(stateVariables);

        Table simControls = createSimControlsTable();
        bottomAlignment.add(simControls);

        Table topRightControls = createTopRightControlsTable();
        topRightAlignment.add(topRightControls);
    }

    /**
     * Updates state labels, pause/play button, slider value, speed label and renders ui
     */
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
    }

    public void dispose() {
        stage.dispose();
    }

    public Stage getStage() {
        return stage;
    }

    /**
     * Creates table with state displaying labels in top left corner
     */
    private Table createStateVarTable() {
        altitudeLabel = new VisLabel("");
        distanceLabel = new VisLabel("");
        pitchLabel = new VisLabel("");
        massLabel = new VisLabel("");
        verticalVelLabel = new VisLabel("");
        horizontalVelLabel = new VisLabel("");

        Table stateVariables = new Table();

        stateVariables.columnDefaults(0).left();
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

    /**
     * Creates table with ui elements at the bottom - visualization controls
     */
    private Table createSimControlsTable() {
        final float GAP = 5f;
        final float TEXT_PAD = 2f;

        Table simControls = new Table();
        simControls.defaults().padLeft(GAP);   // sets default cell alignment left padding

        createPauseButton();
        simControls.add(pauseButton).padLeft(0).minWidth(60*FONT_SCALE);

        createReverseButton();
        simControls.add(reverseButton);

        elapsedTimeLabel = new VisLabel("");
        simControls.add(elapsedTimeLabel).minWidth(60*FONT_SCALE);

        createSlider();
//        simControls.add(slider).padBottom(TEXT_PAD).minWidth(362);
        simControls.add(slider).padBottom(TEXT_PAD).width(695 - FONT_SCALE*50);

        speedValueLabel = new VisLabel("1x");
        simControls.add(speedValueLabel).left().padBottom(TEXT_PAD).padLeft(2*GAP).minWidth(38*FONT_SCALE);

        createMinusButton();
        simControls.add(minusButton).width(minusButton.getHeight());

        createPlusButton();
        simControls.add(plusButton).width(plusButton.getHeight());

        return simControls;
    }

    /**
     * Creates table with ui elements in top right corner
     */
    private Table createTopRightControlsTable() {
        final float GAP = 5f;
        Table table = new Table();
        table.defaults().padLeft(GAP);

        createResetCamButton();
        createBackToMenuButton();

        table.add(resetCamButton);
        table.add(backToMenuButton);
        return table;
    }

    /**
     * Creates pause/play button with its listener
     */
    private void createPauseButton() {
        pauseButton = new VisTextButton("Pause");
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

    /**
     * Creates reverse button with its listener for reversing the visualzation "direction"
     */
    private void createReverseButton() {
        reverseButton = new VisTextButton("Reverse");
        reverseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sim.reverse();
            }
        });
    }

    /**
     * Creates slider with its listeners
     */
    private void createSlider() {
        slider = new VisSlider(0, 1, 0.0001f, false);
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

    /**
     * Creates minus button with its listener
     */
    private void createMinusButton() {
        minusButton = new VisTextButton("-");
        minusButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sim.speedDown();
            }
        });
    }

    /**
     * Creates plus button with its listener
     */
    private void createPlusButton() {
        plusButton = new VisTextButton("+");
        plusButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sim.speedUp();
            }
        });
    }

    /**
     * Creates reset camera button with its listener
     */
    private void createResetCamButton() {
        resetCamButton = new VisTextButton("Reset camera");
        resetCamButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                simScreen.resetCamera();
            }
        });
    }

    /**
     * Creates back to menu button with its listener
     */
    private void createBackToMenuButton() {
        backToMenuButton = new VisTextButton("Back to menu");
        backToMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                simScreen.backToMainMenu();
            }
        });
    }

}
