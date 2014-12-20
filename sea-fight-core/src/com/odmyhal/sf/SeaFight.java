package com.odmyhal.sf;

import java.io.IOException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

import org.bircks.enterprise.control.panel.InvisiblePanel;
import org.bircks.enterprise.control.panel.PanelManager;
import org.bircks.enterprise.control.panel.camera.CameraPanel;
import org.bircks.entierprise.model.ModelStorage;
import org.bricks.engine.Engine;
import org.bricks.engine.Motor;
import org.bricks.engine.view.DataPool;
import org.bricks.extent.debug.ShapeDebugger;
import org.bricks.extent.entity.CameraSatellite;

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
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.odmyhal.sf.control.ShipMovePanel;
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
	
//	ModelInstance ship1/*, ship2*/;
	AssetManager assets = new AssetManager();
	
	@Override
	public void create(){
		
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
		
		Island island1 = Island.instance("island_1");
		island1.translate(1000, 1000);
		island1.applyEngine(engine);
		
		assets.load("models/ship1.g3db", Model.class);
		assets.load("models/ship1_old.g3db", Model.class);
		assets.finishLoading();
		if(assets.update()){
			Gdx.app.debug("Sea fight", "Models are loaded");
		}
		
		Ship one = new Ship(assets);
		one.translate(850, 500);
		one.setVector(30, 0);
//		one.setRotationSpeed(0.3f);
		cameraSatellite = one.initializeCamera();
		camera = cameraSatellite.camera;
		one.applyEngine(engine);
		
		CameraPanel cp = new CameraPanel(camera, cameraSatellite, "panel.defaults", "sf.camera.defaults");
		panelManager.addPanel(cp);
	
		ShipMovePanel smp = new ShipMovePanel(one);
		smp.setActive(true);
		smp.inputControl();
		panelManager.addPanel(smp);

		modelBatch = new ModelBatch(new WaveShaderProvider());
		
		debug = new ShapeDebugger();
		engine.start();
		
        Gdx.app.debug("Sea Fight", "game create passed");
        System.out.println(Gdx.gl.getClass().getCanonicalName());
	}
	
	@Override
	public void render(){
		Gdx.gl.glClearColor(0.9f, 0.9f, 1f, 0.5f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		DataPool<RenderableProvider> entitiesPool = engine.getWorld().getRenderEntities();
		modelBatch.begin(camera);
		for(RenderableProvider entity : entitiesPool.getEntities()){
			modelBatch.render(entity, environment);
		}
		cameraSatellite.applyUpdates();
		modelBatch.end();
		if(DEBUG_ENABLED){
			debug.drawEntityShapes(entitiesPool.getEntities(), camera.combined);
			debug.drawSectors(engine, camera.combined);
		}
		entitiesPool.free();
		
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
