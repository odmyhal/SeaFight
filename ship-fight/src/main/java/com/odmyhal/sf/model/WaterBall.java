package com.odmyhal.sf.model;

import java.util.Map;

import org.bircks.entierprise.model.ModelStorage;
import org.bricks.engine.event.overlap.OverlapStrategy;
import org.bricks.engine.item.MultiLiver;
import org.bricks.engine.neve.EntityPrint;
import org.bricks.engine.tool.Origin;
import org.bricks.extent.space.Origin3D;
import org.bricks.extent.space.SSPrint;
import org.bricks.extent.space.SpaceSubject;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

public class WaterBall extends MultiLiver<SpaceSubject, EntityPrint, Vector3>{
	
	private SpaceSubject<WaterBall, SSPrint> subject;
	
	public WaterBall(){
		ModelInstance waterBall = ModelStorage.instance().getModelInstance("water_ball");
		subject = new SpaceSubject(waterBall, new Vector3());
		this.addSubject(subject);
	}

	public String sourceType() {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, OverlapStrategy> initOverlapStrategy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Origin<Vector3> provideInitialOrigin() {
		return new Origin3D();
	}

}
