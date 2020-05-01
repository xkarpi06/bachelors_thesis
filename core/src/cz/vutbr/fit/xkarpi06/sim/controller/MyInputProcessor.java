package cz.vutbr.fit.xkarpi06.sim.controller;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import cz.vutbr.fit.xkarpi06.sim.model.Simulation;

public class MyInputProcessor extends InputAdapter {

    private Simulation sim;

    public MyInputProcessor(Simulation sim) {
        this.sim = sim;
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.SPACE:
                sim.setShipPosition(sim.getPosition() + 0.04f);
                break;
            default:
                return false;
        }
        return true;
    }
}
