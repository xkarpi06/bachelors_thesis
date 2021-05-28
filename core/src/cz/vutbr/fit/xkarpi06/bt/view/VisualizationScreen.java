package cz.vutbr.fit.xkarpi06.bt.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import cz.vutbr.fit.xkarpi06.bt.MoonLanding;
import cz.vutbr.fit.xkarpi06.bt.input.load.ProjectFiles;
import cz.vutbr.fit.xkarpi06.bt.model.Constants;
import cz.vutbr.fit.xkarpi06.bt.output.MyLog;
import cz.vutbr.fit.xkarpi06.bt.controller.MyInputProcessor;
import cz.vutbr.fit.xkarpi06.bt.model.Simulation;
import cz.vutbr.fit.xkarpi06.bt.model.Trajectory3D;

import java.util.logging.Logger;

/**
 * Takes care of whole 3D scene with Moon, ship and trajectory
 * @author xkarpi06
 * created: 16-04-2020, xkarpi06
 * updated:
 */
public class VisualizationScreen implements Screen {

    /** Logger instance */
    private static final Logger LOGGER = MyLog.getLogger( VisualizationScreen.class.getName() );

    /** Game */
    public MoonLanding game;

    /** Camera variables */
    private PerspectiveCamera cam;
    public Viewport viewport;
    public float SCENE_SCALE = 0.001f;  // 1 for everything in meters, 0.001 for everything in kilometers
    private Vector3 CAMERA_DEFAULT = new Vector3(0, 0, 20f).scl(SCENE_SCALE);
    private Vector3 CAMERA_DEFAULT_LOOK_AT = new Vector3(0, 0, 0).scl(SCENE_SCALE);

    /** Controllers */
    private UserInterface userInterface;
    private CameraInputController camController;
    private MyInputProcessor myInputProcessor;

    /** rendering variables */
    private ModelBatch modelBatch;
    private Environment environment;

    /** Model Instances hold information about model representation on screen */
    private ModelInstance shipInstance;
    private ModelInstance moonInstance;
    private ModelInstance trajectoryInstance;

    /** Variables for loading 3D models */
    private AssetManager assets;
    private Trajectory3D trajectory;
    private Simulation sim;

    /** Render framerate variables */
    private float fps = 30;
    private float dt = 1/fps;
    private float accumulator = 0;

    /**
     * Constructor
     * @param game assumed not null
     */
    public VisualizationScreen(MoonLanding game) {
        this.game = game;
        this.assets = game.assets;
        this.trajectory = game.trajectory;
        this.sim = game.sim;
        this.sim.setScreen(this);

        initializeRenderVariables();
        initializeCamera();
        initializeControllers();
        createModelInstances();
        initializeScene();
        sim.run();
    }

    /**
     * Called periodically forever
     */
    @Override
    public void render (float delta) {
        clearScreen();
        renderScene();
        processUserKeyboardInput();
        camController.update(); // update camera
        userInterface.render(); // render last, or it will be behind moon

        accumulator += delta;
        if(accumulator >= dt) {
            updateShipPosition(dt);
//            updateShipPosition(delta);
            accumulator -= Math.max(delta, dt);
        }
    }

    @Override
    public void dispose () {
        modelBatch.dispose();
        userInterface.dispose();
    }

    @Override
    public void resume () {
    }

    @Override
    public void hide() {

    }

    @Override
    public void show() {

    }

    @Override
    public void resize (int width, int height) {
        viewport.update(width, height);
        userInterface.resize(width, height);
    }

    @Override
    public void pause () {
    }

    /**
     * Init render variables: modelBatch and environment
     */
    private void initializeRenderVariables() {
        modelBatch = new ModelBatch();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));

        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, 1f, -0.5f, -0.5f)); // slightly from above
    }

    /**
     * Init perspective camera for 3D scene
     */
    private void initializeCamera() {
        cam = new PerspectiveCamera();
//        cam = new PerspectiveCamera(67f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        resetCamera();
        viewport = new ExtendViewport(960,540, cam);
//        viewport = new ExtendViewport(640,480, cam);
    }

    /**
     * Resets camera to original position and direction
     */
    public void resetCamera() {
        cam.position.set(CAMERA_DEFAULT);
        cam.lookAt(CAMERA_DEFAULT_LOOK_AT);
        cam.up.set(Vector3.Y);
        cam.near = 0.1f * SCENE_SCALE;
        cam.far = 2*Constants.MOON_RADIUS * SCENE_SCALE;
        cam.update();
    }

    /**
     * Init controllers of this screen: userInterface, camController, myInputProcessor
     */
    private void initializeControllers() {
        userInterface = new UserInterface(sim, this);
        camController = new CameraInputController(cam);
        camController.scrollFactor *= SCENE_SCALE;
        myInputProcessor = new MyInputProcessor(sim);
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(userInterface.getStage());
        inputMultiplexer.addProcessor(camController);
        inputMultiplexer.addProcessor(myInputProcessor);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    private void createModelInstances() {
        createMoonInstance();
        createShipInstance();
        createTrajectoryInstance();
    }

    private void createMoonInstance() {
        Model moonModel = assets.get(ProjectFiles.MOON_SOURCE_FILE, Model.class);
        moonInstance = new ModelInstance(moonModel);
        moonInstance.transform.scl(2f); // TODO
    }

    private void createShipInstance() {
        final float SHIP_WIDTH_IN_METERS = 2f;
        Model shipModel = assets.get(ProjectFiles.SHIP_SOURCE_FILE, Model.class);
        shipInstance = new ModelInstance(shipModel);
        scaleModelInstance(shipInstance, SHIP_WIDTH_IN_METERS * SCENE_SCALE);
    }

    private void scaleModelInstance(ModelInstance modelInstance, float newWidth) {
        BoundingBox bb = new BoundingBox();
        modelInstance.calculateBoundingBox(bb);
        modelInstance.transform.scl(newWidth/bb.getWidth()); // computes scale, applying the scale the width will change to newWidth
    }

    private void createTrajectoryInstance() {
        trajectory.buildModel();
        trajectoryInstance = new ModelInstance(trajectory.getModel());
        Gdx.gl.glLineWidth(2);
    }

    /**
     * Places spaceship at start of trajectory. (Moves Moon and Trajectory)
     */
    private void initializeScene() {
        updateScene(new Vector3(0, 0, 0), trajectory.vertexAt(sim.getPosition()));
        updateShipPitch(0, trajectory.pitchAt(sim.getPosition()));
    }

    private void clearScreen() {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    }

    private void updateShipPosition(float delta) {
        if (sim.isRunning()) {
            sim.updateShipPosition(delta);
        }
    }

    private void processUserKeyboardInput() {
        if (myInputProcessor.leftArrowPressed) {
            sim.setShipPosition(sim.getPosition() - 0.0001f);
        } else if (myInputProcessor.rightArrowPressed) {
            sim.setShipPosition(sim.getPosition() + 0.0001f);
        }
    }

    private void renderScene() {
        modelBatch.begin(cam);
        modelBatch.render(moonInstance, environment);
        modelBatch.render(shipInstance, environment);
        modelBatch.render(trajectoryInstance, environment);
        modelBatch.end();
    }

    /**
     * Updates spaceship position in scene. (Moves Moon and Trajectory in opposite direction)
     * @param prevShipPosition ship old position
     * @param newShipPosition ship new position
     */
    public void updateScene(Vector3 prevShipPosition, Vector3 newShipPosition) {
        Vector3 delta = new Vector3(newShipPosition).sub(prevShipPosition); // copy vector, or it would change it

        // move moon and trajectory opposite way than ship is supposed to move
        trajectoryInstance.transform.translate(-delta.x, -delta.y, -delta.z);
        moonInstance.transform.translate(-delta.x, -delta.y, -delta.z);

        accountForMoonCurvature(prevShipPosition, newShipPosition);
    }

    /**
     * Rotates ship so that bottom is directed to Moon's surface if no other rotations are applied
     * Only does so for x-y plane
     * @param prev ship old position
     * @param newp ship new position
     */
    private void accountForMoonCurvature(Vector3 prev, Vector3 newp) {
        // rotate ship bottom to surface based on central angle theta
        float thetaOld = (float) Math.atan2(prev.y, prev.x);
        float thetaNew = (float) Math.atan2(newp.y, newp.x);
        shipInstance.transform.rotateRad(Vector3.Z, thetaNew - thetaOld);
    }

    /**
     * Updates spaceship pitch. It will "add up" to current ship rotation accounting for Moon curvature
     * @param prevShipPitch ship old pitch
     * @param newShipPitch ship new pitch
     */
    public void updateShipPitch(float prevShipPitch, float newShipPitch) {
        shipInstance.transform.rotateRad(Vector3.Z, newShipPitch - prevShipPitch);
    }

    /**
     * Pauses simulation and changes screen to main menu screen
     */
    public void backToMainMenu() {
        sim.stop();
        sim.setScreen(null);
//        game.font.getData().setScale(1);
        game.setScreen(new MainMenuScreen(game));
        dispose();
    }
}
