package com.odmyhal.sf;

import java.io.IOException;
import java.util.Collection;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

import org.bircks.enterprise.control.panel.InvisiblePanel;
import org.bircks.enterprise.control.panel.PanelManager;
import org.bircks.enterprise.control.panel.camera.CameraPanel;
import org.bircks.entierprise.model.ModelStorage;
import org.bricks.core.entity.Fpoint;
import org.bricks.core.help.AlgebraHelper;
import org.bricks.engine.Engine;
import org.bricks.engine.Motor;
import org.bricks.engine.event.check.AccelerateToSpeedProcessorChecker;
import org.bricks.engine.event.check.RouteChecker;
import org.bricks.engine.tool.Origin2D;
import org.bricks.engine.tool.Roll;
import org.bricks.extent.auto.RollNodeToEntityHProcessor;
import org.bricks.extent.debug.ShapeDebugger;
import org.bricks.extent.entity.CameraSatellite;
import org.bricks.extent.space.SpaceSubjectOperable;
import org.bricks.extent.subject.model.ModelBrickOperable;
import org.bricks.extent.tool.ModelHelper;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.odmyhal.sf.control.ShipMovePanel;
import com.odmyhal.sf.control.WeaponPanel;
import com.odmyhal.sf.model.Island;
import com.odmyhal.sf.model.ShaderWaver;
import com.odmyhal.sf.model.construct.ShipConstructor;
import com.odmyhal.sf.model.shader.WaveShaderProvider;
import com.odmyhal.sf.process.ShipGunVRollProcessor;
import com.odmyhal.sf.staff.Ship;

public class SeaFight extends ApplicationAdapter {
	
	private Engine<RenderableProvider> engine;
	private PanelManager panelManager;
	private ModelBatch modelBatch;
	private Environment environment;
	private Camera camera;
	CameraSatellite cameraSatellite;
	
	ShapeDebugger debug;
	private static final boolean DEBUG_ENABLED = false;
	private static final boolean SPACE_DEBUG_ENABLED = false;
	
	private Ship ship, testo;

	private float totalDeltaTime = 0;
	private int totalFramesCount = 0;
	
//	ModelInstance ship1/*, ship2*/;
	AssetManager assets = new AssetManager();
	
	@Override
	public void create(){
/*
        try {
			System.setOut(new PrintStream(new FileOutputStream("/home/oleh/sf_log.txt")));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
*/		
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		
		panelManager = new PanelManager();
/*		
		camera = new PerspectiveCamera(37f, 1250f, 750f);
		camera.far = 30000;

		
		camera.translate(600, -400, 300);
		camera.lookAt(600, 300, 0);
		
		camera.update();
		
		
		CameraPanel tp = new CameraPanel(camera);
		tp.setActive(true);
		tp.inputControl();
		
		panelManager.addPanel(tp);*/
		panelManager.addPanel(new InvisiblePanel());
		
		environment = new Environment();
 //       environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
 //       environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, 1f, 0.8f, 0.2f));
        
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
        
        
		
		FileHandle fh = Gdx.files.internal("config/engine.prefs.xml");
		FileHandle confHandle = Gdx.files.internal("config/sf.config-defaults.xml");
		Preferences prefs = Preferences.userRoot().node("engine.settings");
		try {
			prefs.clear();
		} catch (BackingStoreException e1) {
			Gdx.app.error("ERROR", "Could not clear preferences engine.settings");
		}
		try {
			Preferences.importPreferences(fh.read());
			Preferences.importPreferences(confHandle.read());
		} catch (IOException e) {
			Gdx.app.error("ERROR", "Could not read file " + fh.path(), e);
		} catch (InvalidPreferencesFormatException e) {
			Gdx.app.error("ERROR", "Could not parse file " + fh.path(), e);
		}
		
		engine = new Engine<RenderableProvider>();
		engine.init(prefs);
		
		assets.load("models/ship_4.g3db", Model.class);
		assets.load("models/ship11.g3db", Model.class);
		assets.load("models/ship_11.g3db", Model.class);
		assets.load("models/ship9.g3db", Model.class);
		assets.finishLoading();
		if(assets.update()){
			Gdx.app.debug("Sea fight", "Models are loaded");
		}
		ShipConstructor.setModel(new ModelInstance(assets.get("models/ship11.g3db", Model.class)));
		ModelStorage.instance().init(prefs.get("model.construct.tool", null));
		
		ShaderWaver waver = new ShaderWaver();
		engine.getWorld().addDecor(waver);
		Motor m1 = engine.getLazyMotor();
		m1.addLiver(waver);
		
		Origin2D tmp2Origin = new Origin2D();

		Island island1 = Island.instance("island_1");
		tmp2Origin.source.set(8000f, 10000f);
		island1.translate(tmp2Origin);
		ModelHelper.setToRotation(89, island1);
		island1.applyEngine(engine);
//		System.out.println("Island on center: " + island1.getStaff().get(0).getCenter());

		Island island2 = Island.instance("island_1");
		tmp2Origin.source.set(17000f, 15000f);
		island2.translate(tmp2Origin);
		ModelHelper.setToRotation(180, island2);
		island2.applyEngine(engine);
//		System.out.println("Island2 on center: " + island2.getStaff().get(0).getCenter());
		
		Island island3 = Island.instance("island_1");
		tmp2Origin.source.set(20500f, 7000f);
		island3.translate(tmp2Origin);
		ModelHelper.setToRotation(254, island3);
		island3.applyEngine(engine);
//		System.out.println("Island3 on center: " + island3.getStaff().get(0).getCenter());

		

		initTestShip();
		
/*		
		Ship kr = new Ship(assets);
		tmpOrigin.set(10000, 5800);
		kr.translate(tmpOrigin);
		kr.setToRotation((float) Math.PI / 2 );
		kr.applyEngine(engine);
*/
		ship = new Ship(assets);
		
		
		tmp2Origin.set(3595, 6000);
		ship.translate(tmp2Origin);
		cameraSatellite = ship.initializeCamera();
		camera = cameraSatellite.camera;
//		ship.setVector(new Origin2D(new Fpoint(50f, 0f)));
//Testing RollToEntityHProcessor:
		
		SpaceSubjectOperable<?, ?, Fpoint, Roll, ModelBrickOperable> sso = ship.getStaff().get(0);
		RollNodeToEntityHProcessor<Ship, Fpoint> rntp = 
				new RollNodeToEntityHProcessor.FpointProcessor<Ship>(ship, "pushka", 0d,
						sso.modelBrick.linkTransform(), 
						sso.modelBrick.getNodeOperator("pushka").getNodeData().linkTransform());
		rntp.setButt(testo);
		rntp.setRotationSpeed(1f);
		ship.registerEventChecker(rntp);
//Testing RollToEntityVProcessor:
		
		ShipGunVRollProcessor sgvrp = new ShipGunVRollProcessor(ship, "stvol");
		sgvrp.setButt(testo);
		ship.registerEventChecker(sgvrp);

//End processors testing.

		ship.applyEngine(engine);
		initRover();
		CameraPanel cp = new CameraPanel(camera, cameraSatellite, "panel.defaults", "sf.camera.defaults");
		panelManager.addPanel(cp);
	
		ShipMovePanel smp = new ShipMovePanel(ship);
		smp.setActive(true);
		smp.inputControl();
		panelManager.addPanel(smp);
		
		WeaponPanel wp = new WeaponPanel(ship);
		panelManager.addPanel(wp);

		modelBatch = new ModelBatch(new WaveShaderProvider());
//		myTest();

		debug = new ShapeDebugger();
		engine.start();
		
        Gdx.app.debug("Sea Fight", "game create passed");
        System.out.println(Gdx.gl.getClass().getCanonicalName());
        System.out.println("GL30 - " + Gdx.gl30.getClass().getCanonicalName());
        
        
	}
	
	private void initRover(){
		Origin2D tmpOrigin = new Origin2D();
		Ship rover = new Ship(assets);
		tmpOrigin.set(24000, 24000);
		rover.translate(tmpOrigin);
		rover.setToRotation((float) Math.PI * 5 / 4);
		rover.registerEventChecker(new AccelerateToSpeedProcessorChecker(20f, 600f));
		rover.registerEventChecker(new RouteChecker((float) Math.PI/7, 100, new Fpoint(22000f, 22000f),
				new Fpoint(8000f, 19000f), new Fpoint(15000f, 9000f), new Fpoint(21000f, 15000f)));
		
		SpaceSubjectOperable<?, ?, Fpoint, Roll, ModelBrickOperable> sso = rover.getStaff().get(0);
		RollNodeToEntityHProcessor<Ship, Fpoint> rntp = 
				new RollNodeToEntityHProcessor.FpointProcessor<Ship>(rover, "pushka", 0d,
						sso.modelBrick.linkTransform(), 
						sso.modelBrick.getNodeOperator("pushka").getNodeData().linkTransform());
		rntp.setButt(ship);
		rntp.setRotationSpeed(1.8f);
		rover.registerEventChecker(rntp);
		
		rover.applyEngine(engine);
		
	}
	
	private void initTestShip(){
		Origin2D tmpOrigin = new Origin2D();
		testo = new Ship(assets);
		tmpOrigin.set(12000, 6000);
		testo.translate(tmpOrigin);
//		testo.setToRotation((float) Math.PI * 5 / 4);
		testo.setToRotation((float) Math.PI / 2);
		testo.applyEngine(engine);
	}
	
	@Override
	public void render(){

		this.totalDeltaTime += Gdx.graphics.getDeltaTime();
		this.totalFramesCount++;
		
		Gdx.gl.glClearColor(0.9f, 0.9f, 1f, 0.5f);
//		Gdx.gl30.glClearColor(0.9f, 0.9f, 1f, 0.5f);
//		Gdx.gl30.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
//		DataPool<RenderableProvider> entitiesPool = engine.getWorld().getRenderEntities();
		Collection<RenderableProvider> entitiesPool = engine.getWorld().getRenderEntities();
		modelBatch.begin(camera);
		for(RenderableProvider entity : entitiesPool){
			modelBatch.render(entity, environment);
		}
		if(SPACE_DEBUG_ENABLED){
			debug.drawSpaceShapes(modelBatch, entitiesPool);
		}
//		System.out.println("Trying to render system");
		cameraSatellite.applyUpdates();
		modelBatch.end();
		if(DEBUG_ENABLED){
			debug.drawEntityShapes(entitiesPool, camera.combined);
			debug.drawSectors(engine, camera.combined);
/*			
			ship.gunMark.calculateTransforms();
			debug.shR.setProjectionMatrix(camera.combined);
			debug.shR.begin(ShapeType.Line);
			debug.shR.setColor(Color.GREEN);
//			debug.shR.line(ship.gunMark.getMark(0).x, ship.gunMark.getMark(0).y, ship.gunMark.getMark(1).x, ship.gunMark.getMark(1).y);
//			System.out.format("Dubug print1 line x1=%.2f, y1=%.2f, x2=%.2f, y2=%.2f\n",  ship.gunMark.getMark(0).x, ship.gunMark.getMark(0).y, ship.gunMark.getMark(1).x, ship.gunMark.getMark(1).y);
			debug.shR.line(ship.gunMark.getMark(2).x, ship.gunMark.getMark(2).y, ship.gunMark.getMark(3).x, ship.gunMark.getMark(3).y);
//			System.out.format("Dubug print2 line x1=%.2f, y1=%.2f, x2=%.2f, y2=%.2f\n",  ship.gunMark.getMark(2).x, ship.gunMark.getMark(2).y, ship.gunMark.getMark(3).x, ship.gunMark.getMark(3).y);
			debug.shR.end();*/
		}
//		entitiesPool.free();
		
		panelManager.render(Gdx.graphics.getDeltaTime());
	}
	
	@Override
	public void resize(int width, int height) {
		camera.viewportWidth = width;
		camera.viewportHeight = height;
		camera.update();
		
		panelManager.resizeViewport(width, height);
	}
	
	@Override
	public void dispose(){
		modelBatch.dispose();
		debug.dispose();
		Gdx.app.debug("OLEH-TEST", "Average frame time: " + (this.totalDeltaTime / this.totalFramesCount));
	}

	private void myTest(){
		Quaternion helpQ = new Quaternion();
		helpQ.setFromAxis(new Vector3(0f, 0f, 99f), 90);
		Vector3 trn = new Vector3(2f, 0f, 0f);
		Vector3 scl = new Vector3(1f, 1f, 1f);
		Matrix4 matr = new Matrix4(trn, helpQ, scl);
		
		Vector3 check = new Vector3(1f, 0f, 0f);
		System.out.println("OLEH-TEST after one rotation result: " + check.mul(matr));
		
		Vector3 nroll = new Vector3(5f, 1f, 0f);
		Vector3 check2 = new Vector3(1f, 0f, 0f);
		Quaternion helpQ2 = new Quaternion();
		helpQ2.setFromAxis(new Vector3(0f, 0f, 99f), -90);
		helpQ.mulLeft(helpQ2);
		helpQ2.toMatrix(matr.val);
		trn.sub(nroll);
		trn.mul(matr);
		trn.sub(nroll);
		
		Matrix4 matr2 = new Matrix4(trn, helpQ, scl);
		System.out.println("OLEH-TEST after two rotation result: " + check2.mul(matr2));
	}
}
