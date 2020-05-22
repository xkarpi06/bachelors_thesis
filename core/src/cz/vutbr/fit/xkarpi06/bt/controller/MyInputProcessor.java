package cz.vutbr.fit.xkarpi06.bt.controller;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import cz.vutbr.fit.xkarpi06.bt.model.Simulation;

/**
 * Processes additional keyboard input
 *
 * If left or right arrow key is pressed, this boolean variable is changed and
 * render() method in visualization screen will see, that this variable is true
 * and tells the simulation to change its state (every render that it is pressed,
 * until it is released and switched to false in this class again)
 *
 * @author xkarpi06
 * created: 02-05-2020, xkarpi06
 */
public class MyInputProcessor extends InputAdapter {

    private Simulation sim;

    public boolean leftArrowPressed = false;
    public boolean rightArrowPressed = false;

    public MyInputProcessor(Simulation sim) {
        this.sim = sim;
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.SPACE:
                if (sim.isRunning())
                    sim.stop();
                else
                    sim.run();
                break;
            case Input.Keys.LEFT:
                leftArrowPressed = true;
                break;
            case Input.Keys.RIGHT:
                rightArrowPressed = true;
                break;
            case Input.Keys.R:
                sim.reverse();
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Input.Keys.LEFT:
                leftArrowPressed = false;
                break;
            case Input.Keys.RIGHT:
                rightArrowPressed = false;
                break;

            default:
                return false;
        }
        return true;
    }
}
