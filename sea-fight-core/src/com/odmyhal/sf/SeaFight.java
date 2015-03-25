package com.odmyhal.sf;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

import org.bircks.enterprise.control.panel.InvisiblePanel;
import org.bircks.enterprise.control.panel.PanelManager;
import org.bircks.enterprise.control.panel.camera.CameraPanel;
import org.bircks.entierprise.model.ModelStorage;
import org.bricks.engine.Engine;
import org.bricks.engine.Motor;
import org.bricks.engine.help.RotationHelper;
import org.bricks.engine.tool.Origin2D;
import org.bricks.extent.debug.ShapeDebugger;
import org.bricks.extent.entity.CameraSatellite;
import org.bricks.extent.tool.ModelHelper;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.odmyhal.sf.control.ShipMovePanel;
import com.odmyhal.sf.control.WeaponPanel;
import com.odmyhal.sf.model.Island;
import com.odmyhal.sf.model.ShaderWaver;
import com.odmyhal.sf.model.shader.WaveShaderProvider;
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
	
	private Ship ship;
	
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
		
		ModelStorage.instance().init(prefs.get("model.construct.tool", null));
		
		ShaderWaver waver = new ShaderWaver();
		engine.getWorld().addDecor(waver);
		Motor m1 = engine.getLazyMotor();
		m1.addSubject(waver);
		
		Origin2D tmpOrigin = new Origin2D();

		Island island1 = Island.instance("island_1");
		tmpOrigin.set(8000, 10000);
		island1.translate(tmpOrigin);
		ModelHelper.setToRotation(89, island1);
		island1.applyEngine(engine);

		Island island2 = Island.instance("island_1");
		tmpOrigin.set(17000, 15000);
		island2.translate(tmpOrigin);
//		RotationHelper.setToRotation(180, island2);
		ModelHelper.setToRotation(180, island2);
		island2.applyEngine(engine);
		
		Island island3 = Island.instance("island_1");
		tmpOrigin.set(20500, 7000);
		island3.translate(tmpOrigin);
		ModelHelper.setToRotation(254, island3);
		island3.applyEngine(engine);
		
//		assets.load("models/ship7.g3db", Model.class);
//		assets.load("models/ship5.g3db", Model.class);
//		assets.load("models/ship_2.g3db", Model.class);
		assets.load("models/ship_4.g3db", Model.class);
		assets.load("models/ship11.g3db", Model.class);
		assets.load("models/ship9.g3db", Model.class);
		assets.finishLoading();
		if(assets.update()){
			Gdx.app.debug("Sea fight", "Models are loaded");
		}
		
		ship = new Ship(assets);
/*		System.out.println("Before calculate: " + ship.gunMark.getMark(2));
		ship.gunMark.calculateTransforms();
		System.out.println("After calculate: " + ship.gunMark.getMark(2));
//		Vector3 tst = new Vector3(38.976746f,  154.729126f,  6.235388f); 
//		Vector3 tst_1 = new Vector3(38.976746f,  154.729126f,  6.235388f);
		Vector3 tst = new Vector3(0f,  0f,  200f); 
		Vector3 tst_1 = new Vector3(0f,  0f,  200f);
		
		Iterator<Matrix4> iter = ship.gunMark.transforms.iterator();
		Matrix4 matr1 = new Matrix4(iter.next());
		matr1.setToRotation(0f, 999f, 0f, -90);
		Matrix4 matr2 = new Matrix4(iter.next());
		matr2.setToRotation(0f, 0f, 99f, -90);
		Matrix4 matr3 = new Matrix4();
		matr3.setToRotation(99f, 0f, 0f, -90);
		
		System.out.println("---------------startVector-----------------");
		System.out.println(tst);
		System.out.println("---------------Vector mul first-----------------");
		System.out.println(tst.mul(matr1));
		System.out.println("---------------Vector mul second-----------------");
		System.out.println(tst.mul(matr2));
		System.out.println("---------------Matrix1-----------------");
		System.out.println(matr1);
		System.out.println("---------------Matrix2-----------------");
		System.out.println(matr2);
		System.out.println("---------------Matrix3-----------------");
		System.out.println(matr3);
		System.out.println("---------------Matrix2xmult-----------------");
		System.out.println(matr1.mul(matr2));
		System.out.println("---------------Vector both matrix-----------------");
		System.out.println(tst_1.mul(matr1));
		
		Matrix4 hMatrix = new Matrix4();
		hMatrix.idt();
		tst.mul(hMatrix);
		for(Matrix4 transform : ship.gunMark.transforms){
			System.out.println(transform);
			System.out.println("***Multi: " + tst.mul(transform));
			System.out.println("Help matrix: " + hMatrix.mul(transform));
			System.out.println("-----------------------------------");
		}
		System.out.println("Hmatrix is: " + hMatrix);
		System.out.println("Help matrix res " + tst_1.mul(hMatrix));
*/		
		tmpOrigin.set(7000, 6000);
		ship.translate(tmpOrigin);
//		one.translate(200, 200);
//		one.translate(108, 0);
		cameraSatellite = ship.initializeCamera();
		camera = cameraSatellite.camera;
		ship.applyEngine(engine);
//		System.out.println("--Ship applyed");
//		rotateTest();
		CameraPanel cp = new CameraPanel(camera, cameraSatellite, "panel.defaults", "sf.camera.defaults");
		panelManager.addPanel(cp);
	
		ShipMovePanel smp = new ShipMovePanel(ship);
		smp.setActive(true);
		smp.inputControl();
		panelManager.addPanel(smp);
		
		WeaponPanel wp = new WeaponPanel(ship);
		panelManager.addPanel(wp);

		modelBatch = new ModelBatch(new WaveShaderProvider());
		
		debug = new ShapeDebugger();
		engine.start();
		
        Gdx.app.debug("Sea Fight", "game create passed");
        System.out.println(Gdx.gl.getClass().getCanonicalName());
        System.out.println("GL30 - " + Gdx.gl30.getClass().getCanonicalName());
        
        
	}
	

	
	@Override
	public void render(){
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
	}

}
