package cz.vutbr.fit.xkarpi06.bt.controller;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import cz.vutbr.fit.xkarpi06.bt.model.Simulation;

/**
 * Processes additional keyboard input
 * @author xkarpi06
 * created: 02-05-2020, xkarpi06
 * updated:
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
//                sim.setShipPosition(sim.getPosition() - 0.001f);
                leftArrowPressed = true;
                break;
            case Input.Keys.RIGHT:
//                sim.setShipPosition(sim.getPosition() + 0.001f);
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
