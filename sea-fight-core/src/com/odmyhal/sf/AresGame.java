package com.odmyhal.ares;

import java.io.IOException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

import org.bircks.enterprise.control.panel.InteractiveController;
import org.bircks.enterprise.control.panel.InvisiblePanel;
import org.bircks.enterprise.control.panel.camera.CameraPanel;
import org.bircks.entierprise.model.ModelStorage;
import org.bricks.core.entity.Fpoint;
import org.bricks.engine.Engine;
import org.bricks.engine.Motor;
import org.bricks.engine.event.check.AccelerateToSpeedProcessorChecker;
import org.bricks.engine.event.check.DurableRouteChecker;
import org.bricks.engine.item.Stone;
import org.bricks.engine.pool.World;
import org.bricks.engine.processor.Processor;
import org.bricks.engine.processor.SingleActProcessor;
import org.bricks.engine.processor.tool.TimerApprover;
import org.bricks.engine.tool.Origin2D;
import org.bricks.extent.debug.ShapeDebugger;
import org.bricks.extent.effects.BricksParticleSystem;
import org.bricks.extent.entity.CameraSatellite;
import org.bricks.extent.tool.CameraHelper;
import org.bricks.extent.tool.ModelHelper;
import org.bricks.utils.Cache;
import org.bricks.extent.interact.SpaceInteract;
import org.bricks.extent.interact.InteractiveHandler;

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
import com.badlogic.gdx.graphics.g3d.particles.batches.ModelInstanceParticleBatch;
import com.badlogic.gdx.graphics.g3d.particles.batches.PointSpriteParticleBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.odmyhal.sf.bot.ShipFightProcessor;
import com.odmyhal.sf.control.ShipMovePanel;
import com.odmyhal.sf.effects.DustEffect;
import com.odmyhal.sf.interact.ShipTouchHandler;
import com.odmyhal.sf.interact.StoneTouchHandler;
import com.odmyhal.sf.model.Island;
import com.odmyhal.sf.model.ShaderWaver;
import com.odmyhal.sf.model.bubble.BlabKeeper;
import com.odmyhal.sf.model.construct.ShipConstructor;
import com.odmyhal.sf.model.shader.WaveShaderProvider;
import com.odmyhal.sf.process.DropBubbleProcessor;
import com.odmyhal.sf.process.ShipGunHRollProcessor;
import com.odmyhal.sf.process.ShipGunVRollProcessor;
import com.odmyhal.sf.staff.Ammunition;
import com.odmyhal.sf.staff.CameraShip;
import com.odmyhal.sf.staff.Ship;

public class AresGame extends ApplicationAdapter {
	private Engine engine;
	private InteractiveController interactiveController;
	private ModelBatch modelBatch;
	private Environment environment;
	private DirectionalLight dirLight;
	private Vector3 tmpDirection = new Vector3();
	private Camera camera;
	ShaderWaver waver;
	CameraSatellite cameraSatellite;
	
	ShapeDebugger debug;
	private static final boolean DEBUG_ENABLED = false;
	private static final boolean SPACE_DEBUG_ENABLED = false;
	
//	private ParticleSystem particleSystem;
	
	private CameraShip ship;
	private Ship testo;

	private float totalDeltaTime = 0;
	private int totalFramesCount = 0;
	
//	ModelInstance ship1/*, ship2*/;
	AssetManager assets = new AssetManager();
	
	@Override
	public void create(){
		
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		
		registerCachedClasses();
		
		environment = new Environment();
        
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
//        dirLight = new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f);
        dirLight = new DirectionalLight().set(0.8f, 0.8f, 0.8f, 1f, 0f, -0.4f);
        environment.add(dirLight);
        
        
		
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
		
		engine = new Engine();
		engine.init(prefs);
		
		assets.load("models/ship_4.g3db", Model.class);
		assets.load("models/ship11.g3db", Model.class);
		assets.load("models/ship_11.g3db", Model.class);
		assets.load("models/ship9.g3db", Model.class);
		assets.load("models/ship11.g3dj", Model.class);
		assets.finishLoading();
		if(assets.update()){
			Gdx.app.debug("Sea fight", "Models are loaded");
		}
		ShipConstructor.setModel(new ModelInstance(assets.get("models/ship11.g3dj", Model.class)));
		ModelStorage.instance().init(prefs.get("model.construct.tool", null));
		
//		Gdx.app.debug("INFO", "Already loaded dust texture " + DustEffect.dustTexture);
//		engine.getWorld().addDecor(waver);
		waver = new ShaderWaver();
		Motor m1 = engine.getLazyMotor();
		m1.addLiver(waver);

		initIslands();
		
//		olehTest();
		initTestShip();

//		Ball.setBlabKeeper(waver.blabKeeper);
		Ammunition.blabKeeper = waver.blabKeeper;
		
		try {
			Thread.currentThread().sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		pausa();
		ship = new CameraShip(assets);
		ship.registerEventChecker(new DropBubbleProcessor(waver.blabKeeper));
		
		Origin2D tmp2Origin = new Origin2D();
		tmp2Origin.set(3595, 6000);
		ship.translate(tmp2Origin);
		cameraSatellite = ship.initializeCamera();
		camera = cameraSatellite.camera;
		
//		initParticleSystem();

		BricksParticleSystem.addBatch(new ModelInstanceParticleBatch());
		PointSpriteParticleBatch pointSpriteBatch = new PointSpriteParticleBatch();
		pointSpriteBatch.setTexture(DustEffect.dustTexture);
		pointSpriteBatch.setCamera(camera);
		BricksParticleSystem.addBatch(pointSpriteBatch);
		

		waver.setCamera(camera);
		ship.applyEngine(engine);
		
		initTouchInteract(ship, camera, engine.getWorld());
		
		interactiveController = new InteractiveController(SpaceInteract.instance());

		interactiveController.addPanel(new InvisiblePanel());
		
		CameraPanel cp = new CameraPanel(camera, cameraSatellite, "panel.defaults", "sf.camera.defaults");
		interactiveController.addPanel(cp);
	
		ShipMovePanel smp = new ShipMovePanel(ship);
		interactiveController.addPanel(smp);
		smp.setActive(true);
//		smp.setActive(true);

		modelBatch = new ModelBatch(new WaveShaderProvider(camera));

		debug = new ShapeDebugger();
		engine.start();

		pausa();
		initBotShips();
        Gdx.app.debug("Sea Fight", "game create passed");
        
        
	}
	
	private void pausa(){
		try {
			Thread.currentThread().sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void initTouchInteract(Ship ship, Camera camera, World world){
		StoneTouchHandler sth = new StoneTouchHandler(ship, camera.far);
		SpaceInteract.init(camera, world, sth);
		InteractiveHandler.registerHandler(Stone.class, sth);
		InteractiveHandler.registerHandler(Ship.class, new ShipTouchHandler(ship));
	}
	
	private void initBotShips(){
		initRover(24000f, 24000f, waver.blabKeeper, new Fpoint(22000f, 22000f),
				new Fpoint(8000f, 19000f), new Fpoint(15000f, 9000f), new Fpoint(21000f, 15000f));
		pausa();
		initRover(24000f, 800f, waver.blabKeeper, new Fpoint(24000f, 2000f),
				new Fpoint(23500f, 9000f), new Fpoint(13000, 11000), new Fpoint(10000f, 8000f), new Fpoint(4000f, 3000f));

		initRover(-91000f, -89000f, waver.blabKeeper, new Fpoint(-90000f, -85000f),
				new Fpoint(-70500f, -30500f), new Fpoint(-45000, 33000), new Fpoint(-80500f, 59000f));
		
		initRover(-51000f, -27400f, waver.blabKeeper, new Fpoint(-50000f, -26000f),
				new Fpoint(-80500f, 9000f), new Fpoint(-91000, 31000), new Fpoint(-64000f, 48000f), new Fpoint(-54000f, 51000f));
		
		initRover(55000f, 56000f, waver.blabKeeper, new Fpoint(57000f, 58000f),
				new Fpoint(36000f, 63000f), new Fpoint(-15000f, 25000f), new Fpoint(-42000f, -5000f),
				new Fpoint(-12000f, -25000f), new Fpoint(28000f, -31000f), new Fpoint(68000f, 8000f), 
				new Fpoint(72000f, 38000f));

		initRover(-87000f, 69000f, waver.blabKeeper, new Fpoint(-90000f, 53000f),
				new Fpoint(70500f, 60500f), new Fpoint(52000, 61000), new Fpoint(-500f, 59000f));
		
		initRover(-82000f, -69000f, waver.blabKeeper, new Fpoint(-90000f, -85000f),
				new Fpoint(-50500f, -40500f), new Fpoint(-22000, -61000), new Fpoint(-500f, -75000f),
				new Fpoint(33000f, -5000f), new Fpoint(70900f, -80100f));
		
		initRover(-33000f, -33000f, waver.blabKeeper, new Fpoint(-49000f, -49000f),
				new Fpoint(-49000f, 49000f), new Fpoint(49000f, 49000f), new Fpoint(49000f, -49000f));
		
		initRover(-33000f, -33000f, waver.blabKeeper, new Fpoint(0f, -64000f),
				new Fpoint(64000f, 0f), new Fpoint(0f, 64000f), new Fpoint(-64000f, 0f));
		
		initRover(-53000f, -53000f, waver.blabKeeper, new Fpoint(5600f, -2900f),
				new Fpoint(69000f, -51000f), new Fpoint(61000f, 0f), new Fpoint(38000f, 49000f),
				new Fpoint(-48000f, 63000f), new Fpoint(-81000f, -47700f));
	}
	
	private Ship initRover(final float tX, final float tY, final BlabKeeper bk, final Fpoint... route){
		Origin2D tmpOrigin = new Origin2D();
		Ship rover = new Ship(assets);
		tmpOrigin.set(tX, tY);
		rover.translate(tmpOrigin);
		rover.setHealth(100);
		rover.setToRotation((float) Math.PI * 5 / 4);
		rover.registerEventChecker(new AccelerateToSpeedProcessorChecker(20f, 600f));
		
		rover.registerEventChecker(new DurableRouteChecker<Ship>((float) Math.PI/7, 700f, route));

		rover.applyEngine(engine);
		Thread.currentThread().yield();
		
		ShipGunHRollProcessor sgrp = new ShipGunHRollProcessor(rover, "pushka");
		sgrp.setButt(ship);
		rover.registerEventChecker(sgrp);
		
		ShipGunVRollProcessor sgvrp = new ShipGunVRollProcessor(rover, rover.mainGun, "stvol");
		sgvrp.setButt(ship);
		rover.registerEventChecker(sgvrp);
		
		ShipGunHRollProcessor sgrp_back = new ShipGunHRollProcessor(rover, "backPushka");
		sgrp_back.setButt(ship);
		rover.registerEventChecker(sgrp_back);
		
		ShipGunVRollProcessor sgvrp_back = new ShipGunVRollProcessor(rover, rover.backGun, "backStvol");
		sgvrp_back.setButt(ship);
		rover.registerEventChecker(sgvrp_back);
		
/*	
		ShipFightProcessor sfp = new ShipFightProcessor(new TimerApprover<Ship>(500), sgrp, sgvrp);
		rover.registerEventChecker(sfp);
*/	

		Processor<Ship> initNewRover = new SingleActProcessor<Ship>(){

			@Override
			protected boolean ready(Ship target, long processTime) {
				return true;
			}

			@Override
			protected void processSingle(Ship target, long processTime) {
				AresGame.this.initRover(tX, tY,  bk, route);
			}
			
		};
		rover.setBeforeGetOut(initNewRover);
		
		rover.registerEventChecker(new DropBubbleProcessor(bk));
		
		
		return rover;
	}
	
	private void initIslands(){
		
		Origin2D tmp2Origin = new Origin2D();

		initIsland(8000f, 10300f, 89, tmp2Origin);
		initIsland(17000f, 15000f, 180f, tmp2Origin);
		initIsland(20500f, 7600f, 254f, tmp2Origin);
		initIsland(-50230f, 51200f, 55f, tmp2Origin);
		initIsland(-30230f, -25600f, 330f, tmp2Origin);
		initIsland(65230f, 42200f, 130f, tmp2Origin);
		initIsland(46230f, -42000f, 230f, tmp2Origin);
		initIsland(-78236, -420f, 23f, tmp2Origin);
		initIsland(33236, 40200f, 23f, tmp2Origin);
		initIsland(236, 44200f, 23f, tmp2Origin);
		initIsland(72000, 71800f, 210f, tmp2Origin);
		initIsland(35000, 30900f, 290f, tmp2Origin);
		initIsland(-40000, 5600f, 163f, tmp2Origin);
		initIsland(-20000, -71600f, 143f, tmp2Origin);
		initIsland(52000, -7600f, 143f, tmp2Origin);
		initIsland(-77000, -76000f, 143f, tmp2Origin);
	}
	
	private void initIsland(float x, float y, float rad, Origin2D tmp2Origin){
		Island island3 = Island.instance("island_1");
		tmp2Origin.source.set(x, y);
		island3.translate(tmp2Origin);
		ModelHelper.setToRotation(rad, island3);
		island3.applyEngine(engine);
	}
	
	private void initTestShip(){
		Origin2D tmpOrigin = new Origin2D();
		testo = new Ship(assets);
		tmpOrigin.set(12000, 6000);
		testo.translate(tmpOrigin);
		testo.setHealth(20);
//		testo.setToRotation((float) Math.PI * 5 / 4);
		testo.setToRotation((float) Math.PI / 2);
		testo.applyEngine(engine);
	}
	
	@Override
	public void render(){

		this.totalDeltaTime += Gdx.graphics.getDeltaTime();
		this.totalFramesCount++;
		
//		Gdx.gl.glEnable(Gdx.gl20.GL_POLYGON_OFFSET_FACTOR);
//		Gdx.gl.glPolygonOffset(1.0f, 1.0f);
		
//		Gdx.gl.glDepthFunc(GL20.GL_GREATER);
		
		Gdx.gl.glClearColor(0.9f, 0.9f, 1f, 0.5f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		Gdx.gl.glDepthFunc(Gdx.gl20.GL_LESS);
//		DataPool<RenderableProvider> entitiesPool = engine.getWorld().getRenderEntities();
		
		tuneDirection();

		CameraHelper.tuneWorldCamera(engine.getWorld(), camera);
		Iterable<RenderableProvider> entitiesPool = CameraHelper.getCameraRenderables();
		waver.setRenderDistricts(CameraHelper.getInCameraDistricts());
		modelBatch.begin(camera);
/*		 particleSystem.update();
         particleSystem.begin();
         particleSystem.draw();
         particleSystem.end();
         modelBatch.render(particleSystem);*/
		modelBatch.render(waver, environment);
		BricksParticleSystem.begin();
		BricksParticleSystem.drawEffects(CameraHelper.getCameraEffects());
		BricksParticleSystem.end();
		modelBatch.render(BricksParticleSystem.particleSystem(), environment);
		BricksParticleSystem.freeEffects(CameraHelper.getCameraEffects());
		for(RenderableProvider entity : entitiesPool){
			modelBatch.render(entity, environment);
		}
		if(SPACE_DEBUG_ENABLED){
			debug.drawSpaceShapes(modelBatch, entitiesPool);
		}
//		cameraSatellite.applyUpdates();
		modelBatch.end();
		if(DEBUG_ENABLED){
			debug.drawEntityShapes(entitiesPool, camera.combined);
			debug.drawSectors(engine, camera.combined, CameraHelper.getInCameraDistricts());
		}
		CameraHelper.end();
		interactiveController.render(Gdx.graphics.getDeltaTime());
	}
	
	@Override
	public void resize(int width, int height) {
		camera.viewportWidth = width;
		camera.viewportHeight = height;
		camera.update();
		
		interactiveController.resizeViewport(width, height);
	}
	
	@Override
	public void dispose(){
		engine.stop();
		modelBatch.dispose();
		debug.dispose();
		ModelStorage.instance().dispose();
		Gdx.app.debug("OLEH-TEST", "Average frame time: " + (this.totalDeltaTime / this.totalFramesCount));
		Gdx.app.debug("OLEH-TEST", "Created " + Ammunition.counter.get() + " ammunition instances");
		Gdx.app.debug("OLEH-TEST", "Dust instances: " + Ammunition.dustInstances.get());
	}

	private void registerCachedClasses(){

		Cache.registerCache(Vector3.class, new Cache.DataProvider<Vector3>(){
			@Override
			public Vector3 provideNew() {
				return new Vector3();
			}
			
		});
		
	}
	private void tuneDirection(){
		tmpDirection.set(camera.direction.x, camera.direction.y, 0f);
		tmpDirection.nor();
		tmpDirection.z = dirLight.direction.z;
		tmpDirection.nor();
		dirLight.direction.set(tmpDirection);
	}
/*	
	private void initParticleSystem(){
		particleSystem = ParticleSystem.get();
		PointSpriteParticleBatch pointSpriteBatch = new PointSpriteParticleBatch();
		pointSpriteBatch.setTexture(DustEffect.dustTexture);
		pointSpriteBatch.setCamera(camera);
		particleSystem.add(pointSpriteBatch);
		
		DustEffect effect = new DustEffect(particleSystem);
		effect.init();
		particleSystem.add(effect);
		effect.setToTranslation(new Vector3(11500f, 6000f, 250f));
		effect.start();
	}
	*/
/*	private void olehTest(){
		Vector3 translation = new Vector3(0f, 0f, 0f);
		Vector3 rotation = new Vector3(0f, 0f, 0f);
		Quaternion q = new Quaternion(rotation, 1f);
		Vector3 scale = new Vector3(1f, 1f, 1f);
		Matrix4 m4 = new Matrix4(translation, q, scale);
		System.out.println("OLEH-TEST new Point " + m4);
	}*/
}
