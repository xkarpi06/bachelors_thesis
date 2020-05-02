package cz.vutbr.fit.xkarpi06.sim.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import cz.vutbr.fit.xkarpi06.sim.view.MoonScene;

import java.time.Duration;

/**
 * Represents spacecraft position on trajectory represented by value between 0 and 1
 * @author xkarpi06
 * @version 1.0
 * @since 1.0
 * created: 18-4-2020, xkarpi06
 * updated:
 */
public class Simulation {
    private MoonScene moonScene;
    private Trajectory3D trajectory;

    /** Ship position on trajectory from 0 to 1 */
    private float currentPosition;
    private float previousPosition;

    private boolean running = false;
    private float simSpeed = 1.0f;
    private boolean backwards = false;

    public static final float POS_MIN = 0f;
    public static final float POS_MAX = 1f;

    public Simulation(MoonScene ms, Trajectory3D trajectory) {
        this.moonScene = ms;
        this.trajectory = trajectory;
        currentPosition = POS_MIN;
        previousPosition = POS_MIN;
    }

    /**
     * Set current ship position, will update previous position
     * @param value new position
     */
    public void setShipPosition(float value) {
        previousPosition = currentPosition;
        if (value < POS_MIN) {
            currentPosition = POS_MIN;
        } else if (value > POS_MAX) {
            currentPosition = POS_MAX;
        } else {
            currentPosition = value;
        }
        if (currentPosition == POS_MIN || currentPosition == POS_MAX) {
            stop();
        }
        moonScene.updateScene(trajectory.vertexAt(previousPosition), trajectory.vertexAt(currentPosition));
        moonScene.updateShipPitch(trajectory.pitchAt(previousPosition), trajectory.pitchAt(currentPosition));
    }

    /**
     * Change current ship position by delta
     */
    public void updateShipPosition() {
        float delta = Gdx.graphics.getDeltaTime() * simSpeed * trajectory.speedAt(currentPosition);
        if (backwards) {
            delta = -delta;
        }

        if (currentPosition + delta > POS_MAX) {
            setShipPosition(POS_MAX);
        } else if (currentPosition + delta < POS_MIN) {
            setShipPosition(POS_MIN);
        } else {
            setShipPosition(currentPosition + delta);
        }
    }

    public float getPosition() {
        return currentPosition;
    }

    public float getSpeed() {
        return simSpeed;
    }

    /**
     * Changes simulation state to running
     */
    public void run() {
        running = true;
    }

    /**
     * Changes simulation state to paused
     */
    public void stop() {
        running = false;
    }

    /**
     * Changes simulation direction
     */
    public void reverse() {
        backwards = !backwards;
        if (currentPosition == POS_MAX || currentPosition == POS_MIN) {
            run();
        }
    }

    public boolean isRunning() {
        return running;
    }

    /**
     * Slows down simulation
     */
    public void speedDown() {
        if (simSpeed <= 0.3)
            return;
        else if (simSpeed <= 1)
            simSpeed -= 0.2;
        else if (simSpeed <= 5)
            simSpeed -= 1;
        else if (simSpeed <= 30)
            simSpeed -= 5;
        else if (simSpeed <= 80)
            simSpeed -= 10;
        else
            simSpeed -= 20;
    }

    /**
     * Speeds up simulation
     */
    public void speedUp() {
        if (simSpeed < 1)
            simSpeed += 0.2;
        else if (simSpeed < 5)
            simSpeed += 1;
        else if (simSpeed < 30)
            simSpeed += 5;
        else if (simSpeed < 80)
            simSpeed += 10;
        else if (simSpeed < 200)
            simSpeed += 20;
        else
            return;
    }

    /**
     * Computes current altitude from x and y coordinates
     * @return altitude above Moon's surface in meters
     */
    public float getAltitude() {
        Vector3 pos = trajectory.vertexAt(currentPosition);
        return (((float) Math.sqrt(pos.x * pos.x + pos.y * pos.y)) - Constants.MOON_RADIUS * moonScene.SCENE_SCALE)/moonScene.SCENE_SCALE;
    }

    /**
     * Computes current downrange distance from landing site from x and y coordinates
     * @return downrange distance from landing site in meters
     */
    public float getDownrangeDistance() {
        return trajectory.downrangeDistFromTargetAt(currentPosition);
    }

    /**
     * Computes current pitch
     * @return pitch in degrees relative to tangent
     */
    public float getPitch() {
        return (180f/(float) Math.PI) * trajectory.pitchAt(currentPosition);
    }

    /**
     * Returns elapsed time since trajectory beginning at current position
     * @return elapsed time as Duration
     */
    public Duration getElapsedTime() {
        return Duration.ofMillis((long) trajectory.elapsedTimeAt(currentPosition) * 1000);
    }

    /**
     * Computes mass at current simulation state
     * @return mass in kilograms
     */
    public float getMass() {
        return trajectory.massAt(currentPosition);
    }

    /**
     * Computes vertical velocity at current simulation state
     * @return velocity in m/s
     */
    public float getVerticalVelocity() {
        return trajectory.verticalVelocityAt(currentPosition);
    }

    /**
     * Computes horizontal velocity at current simulation state
     * @return velocity in m/s
     */
    public float getHorizontalVelocity() {
        return trajectory.horizontalVelocityAt(currentPosition);
    }
}
