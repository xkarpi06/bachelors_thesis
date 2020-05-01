package cz.vutbr.fit.xkarpi06.sim.view;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import cz.vutbr.fit.xkarpi06.sim.input.load.ProjectFiles;
import cz.vutbr.fit.xkarpi06.sim.output.MyLog;
import cz.vutbr.fit.xkarpi06.sim.controller.MyInputProcessor;
import cz.vutbr.fit.xkarpi06.sim.model.Simulation;
import cz.vutbr.fit.xkarpi06.sim.model.Trajectory3D;

import java.util.Arrays;
import java.util.logging.Logger;

/**
 * Takes care of the view part, periodically renders scene
 * @author xkarpi06
 * created: 16-04-2020, xkarpi06
 * updated:
 */
public class MoonScene implements ApplicationListener {

    public final float MOON_RADIUS = 1738100f;

    /** Logger instance */
    private static final Logger LOGGER = MyLog.getLogger( MoonScene.class.getName() );

    /** Camera variables */
    private PerspectiveCamera cam;
    private CameraInputController camController;
    private Viewport viewport;
    public float SCENE_SCALE;  // 1 for everything in meters, 0.001 for everything in kilometers

    /** rendering variables */
    private ModelBatch modelBatch;
    private Environment environment;

    /** Variables for loading 3D spacecraft */
    private AssetManager assets;
    private boolean loading;

    /** Models hold information about models' dimensions */
    private Model shipModel;
    private Model moonModel;
    private Model trajectoryModel;

    /** Model Instances hold information about model representation on screen */
    private ModelInstance shipInstance;
    private ModelInstance moonInstance;
    private ModelInstance trajectoryInstance;

    /** Custom model and controller classes */
    private Trajectory3D trajectory;
    private Simulation sim;

    /** User interface */
    private UserInterface userInterface;

    /**
     * Constructor
     * @param trajectory assumed not null
     * @param sceneScale scale of scene, default 1 for units in meters
     */
    public MoonScene(Trajectory3D trajectory, float sceneScale) {
        this.trajectory = trajectory;
        this.sim = new Simulation(this, trajectory);
        this.SCENE_SCALE = sceneScale;
    }

    /**
     * Called once in the beginning
     */
    @Override
    public void create () {
        initializeRenderVariables();
        initializeCamera();
        userInterface = new UserInterface(sim);
        initializeControllers();
        createModels();
//        initializeScene();
        sim.run();
    }

    /**
     * Called periodically forever
     */
    @Override
    public void render () {
        if (loading) {
            if (assets.update()) {
                doneLoading();
            }
        } else {
            clearScreen();
            updateShipPosition();
            camController.update(); // update camera
            renderScene();
            userInterface.render();
        }
    }

    @Override
    public void dispose () {
        modelBatch.dispose();
        assets.dispose();
//        shipModel.dispose();  // shipModel is disposed with assets
        moonModel.dispose();
        trajectoryModel.dispose();
        userInterface.dispose();
    }

    @Override
    public void resume () {
    }

    @Override
    public void resize (int width, int height) {
//        System.out.println("W:" + Gdx.graphics.getWidth() + ", H:" +  Gdx.graphics.getHeight() + ", w:" + width + ", h:" + height);
        viewport.update(width, height);
        userInterface.resize(width, height);
    }

    @Override
    public void pause () {
    }

    private void initializeRenderVariables() {
        modelBatch = new ModelBatch();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
    }

    private void initializeCamera() {
//        System.out.println("W:" + Gdx.graphics.getWidth() + ", H:" +  Gdx.graphics.getHeight());
        cam = new PerspectiveCamera();
//        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0, 0, 7f * SCENE_SCALE);
        cam.lookAt(0,0,0);
        cam.near = 1f * SCENE_SCALE;
        cam.far = 3000000f * SCENE_SCALE;
        cam.update();
        viewport = new ExtendViewport(960,540, cam);
    }

    private void initializeControllers() {
        camController = new CameraInputController(cam);
        camController.scrollFactor *= SCENE_SCALE;
//        camController.translateUnits *= SCENE_SCALE;
//        camController.pinchZoomFactor /= SCENE_SCALE;
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(userInterface.getStage());
        inputMultiplexer.addProcessor(camController);
        inputMultiplexer.addProcessor(new MyInputProcessor(sim));
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    private void createModels() {
        createMoonModel();
        createShipModel();
        createTrajectoryModel();
    }

    private void createMoonModel() {
        final float MOONMODEL_RADIUS = MOON_RADIUS * SCENE_SCALE;
        ModelBuilder modelBuilder = new ModelBuilder();
        moonModel = modelBuilder.createSphere(2* MOONMODEL_RADIUS, 2* MOONMODEL_RADIUS, 2* MOONMODEL_RADIUS,
                120, 120, new Material(ColorAttribute.createDiffuse(Color.GRAY)),
                Usage.Position | Usage.Normal);
        moonInstance = new ModelInstance(moonModel);
    }

    private void createShipModel() {
        assets = new AssetManager();
        assets.load(ProjectFiles.SHIP_SOURCE_FILE, Model.class);
        loading = true;
    }

    /**
     * Called after all assets (just ship here) are loaded
     */
    private void doneLoading() {
        final float SHIP_WIDTH_IN_METERS = 2f;
        shipModel = assets.get(ProjectFiles.SHIP_SOURCE_FILE, Model.class);
        shipModel.materials.add(new Material(ColorAttribute.createDiffuse(Color.GRAY)));
        shipInstance = new ModelInstance(shipModel);
//        scaleModelInstance(shipInstance, SHIP_WIDTH_IN_METERS * SCENE_SCALE);
        shipInstance.transform.scl(0.000003f);
        loading = false;
        initializeScene();
    }

    private void scaleModelInstance(ModelInstance modelInstance, float newWidth) {
        BoundingBox bb = new BoundingBox();
        modelInstance.calculateBoundingBox(bb);
        modelInstance.transform.scl(newWidth * bb.getWidth());
    }

    private void createTrajectoryModel() {
        final Color TRAJECTORY_COLOR = Color.BLUE;

//        ModelBuilder modelBuilder = new ModelBuilder();
//        modelBuilder.begin();
//        modelBuilder.part("skelet", trajectory.buildMeshControl(), GL20.GL_LINES, new Material(ColorAttribute.createDiffuse(Color.GREEN)));
//        modelBuilder.part("traj", trajectory.buildMesh(), GL20.GL_LINES, new Material(ColorAttribute.createDiffuse(Color.BLUE)));
//        trajectoryModel = modelBuilder.end();

        trajectoryModel = trajectory.buildModel(new Material(ColorAttribute.createDiffuse(TRAJECTORY_COLOR)));
        trajectoryInstance = new ModelInstance(trajectoryModel);
        Gdx.gl.glLineWidth(2);
    }

    /**
     * Places spaceship at start of trajectory. (Moves Moon and Trajectory)
     */
    private void initializeScene() {
//        updateScene(new Vector3(0, 0, 0), trajectory.vertexAt(1));
        updateScene(new Vector3(0, 0, 0), trajectory.vertexAt(0));
        updateShipPitch(0, trajectory.pitchAt(0));
    }

    private void clearScreen() {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    }

    private void updateShipPosition() {
        if (sim.isRunning()) {
            sim.updateShipPosition();
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
//    public void updateScene(Vector3 prevShipPosition, Vector3 newShipPosition, Vector3 shipAttitude) {
        Vector3 delta = new Vector3(newShipPosition).sub(prevShipPosition); // copy vector, or it would change it

        // move moon and trajectory opposite way than ship is supposed to move
        trajectoryInstance.transform.translate(-delta.x, -delta.y, -delta.z);
        moonInstance.transform.translate(-delta.x, -delta.y, -delta.z);

        accountForMoonCurvature(prevShipPosition, newShipPosition);
    }

    /**
     * Rotates ship so that bottom is directed to Moon's surface if no other rotations are applied
     * @param prev ship old position
     * @param newp ship new position
     */
    private void accountForMoonCurvature(Vector3 prev, Vector3 newp) {
        // rotate ship bottom to surface based on central angle theta
        float thetaOld = (float) Math.atan2(prev.y, prev.x);
        float thetaNew = (float) Math.atan2(newp.y, newp.x);
        shipInstance.transform.rotateRad(Vector3.Z, thetaNew - thetaOld);

//        float phiOld = (float) Math.asin(prev.z / Math.sqrt(prev.x*prev.x + prev.y*prev.y + prev.z*prev.z));
//        float phiNew = (float) Math.asin(newp.z / Math.sqrt(newp.x*newp.x + newp.y*newp.y + newp.z*newp.z));
    }

    /**
     * Updates spaceship pitch. It will "add up" to current ship rotation accounting for Moon curvature
     * @param prevShipPitch ship old pitch
     * @param newShipPitch ship new pitch
     */
    public void updateShipPitch(float prevShipPitch, float newShipPitch) {
        shipInstance.transform.rotateRad(Vector3.Z, newShipPitch - prevShipPitch);
    }
}
