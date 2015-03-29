package com.odmyhal.sf.staff;

import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

import org.bircks.entierprise.model.ModelStorage;
import org.bricks.annotation.EventHandle;
import org.bricks.engine.event.check.ChunkEventChecker;
import org.bricks.engine.event.overlap.OverlapStrategy;
import org.bricks.engine.event.processor.GetOutProcessor;
import org.bricks.engine.neve.WalkPrint;
import org.bricks.engine.tool.Origin;
import org.bricks.extent.engine.checker.NodeScaleProcessor;
import org.bricks.extent.event.ExtentEventGroups;
import org.bricks.extent.space.Origin3D;
import org.bricks.extent.space.SpaceSubject;
import org.bricks.extent.space.SpaceWalker;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.odmyhal.sf.model.Island;
import com.odmyhal.sf.model.WaterBall;
import com.odmyhal.sf.process.FaceWaterEvent;
import com.odmyhal.sf.process.FaceWaterEventChecker;

import org.bricks.extent.space.SSPrint;
import org.bricks.extent.space.check.CheckInWorldProcessor;
import org.bricks.extent.subject.model.MBPrint;
import org.bricks.extent.subject.model.ModelBrick;


public class Ammunition extends SpaceWalker<SpaceSubject<?, ?, ?>, WalkPrint<?, Vector3>> implements RenderableProvider{
	
	public static final Preferences prefs = Preferences.userRoot().node("sf.ship.ammunition");
	public static final String SHIP_AMMUNITION_TYPE = "ShipAmmunitio@sf.myhal.com";
	private static final CheckInWorldProcessor<Ammunition> inWorldProcessor
		= new CheckInWorldProcessor<Ammunition>(prefs.getInt("ship.ammo1.world.min", -100), 
				prefs.getInt("ship.ammo1.world.max", 10000));
	public static final float accelerationZ = prefs.getFloat("ship.ammo1.acceleration.z", 0f);
	
	private SpaceSubject<SpaceWalker, SSPrint, ModelBrick> subject;
	Origin<Vector3> tmpOrigin = new Origin3D();
	//
	public Vector3 previousOrigin = new Vector3();
	
	public Ammunition(){
		Vector3 one = new Vector3(-24f, 0f, 0f);
		Vector3 two = new Vector3(36f, 0f, 0f);
		subject = new SpaceSubject(ModelStorage.instance().getModelInstance("ship_ammunition"), new Vector3(), one, two);
		this.addSubject(subject);
		this.registerEventChecker(inWorldProcessor);
		this.registerEventChecker(FaceWaterEventChecker.instance());
	}

	public String sourceType() {
		return SHIP_AMMUNITION_TYPE;
	}
	
	public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
		for(SpaceSubject subject: getStaff()){
			subject.getRenderables(renderables, pool);
		}
	}

	public Map<String, OverlapStrategy> initOverlapStrategy() {
/*		Map<String, OverlapStrategy> overlapStrategy = new HashMap<String, OverlapStrategy>();
		overlapStrategy.put(Island.ISLAND_SF_SOURCE, OverlapStrategy.FALSE);
		overlapStrategy.put(Ship.SHIP_SOURCE_TYPE, OverlapStrategy.FALSE);
		overlapStrategy.put(Ammunition.SHIP_AMMUNITION_TYPE, OverlapStrategy.FALSE);
		return overlapStrategy;*/
		return null;
	}
	
	@EventHandle(eventType = SHIP_AMMUNITION_TYPE)
	public void faceWater(FaceWaterEvent event){
		WaterBall wb = new WaterBall();
		tmpOrigin.source.set(event.touchPoint());
		wb.translate(tmpOrigin);
		wb.applyEngine(this.getEngine());
	}


}
