package com.odmyhal.sf.process;

import org.bricks.engine.event.Event;
import org.bricks.engine.event.check.CheckerType;
import org.bricks.engine.event.check.CommonEventChecker;

import com.badlogic.gdx.math.Vector3;
import com.odmyhal.sf.staff.Ammunition;

public class FaceWaterEventChecker extends CommonEventChecker<Ammunition>{
	
	private static final CheckerType CHECK_TYPE = CheckerType.registerCheckerType();
	private static final FaceWaterEventChecker instance = new FaceWaterEventChecker(CHECK_TYPE);
	
	private FaceWaterEventChecker(CheckerType ch){
		super(ch);
	}
	
	public static FaceWaterEventChecker instance(){
		return instance;
	}

	@Override
	protected Event produceEvent(Ammunition ammo) {
		float dz = ammo.previousOrigin.z / ammo.previousOrigin.z - ammo.origin().source.z;
		float x = ammo.previousOrigin.x + (ammo.origin().source.x -  ammo.previousOrigin.x) * dz;
		float y = ammo.previousOrigin.y - (ammo.origin().source.y - ammo.previousOrigin.y) * dz;
		return new FaceWaterEvent(ammo, new Vector3(x, y, 0f));
	}

	@Override
	protected boolean ready(Ammunition ammo) {
		boolean res = false;
		if(ammo.origin().source.z <= 0 && ammo.previousOrigin.z > 0){
			res = true;
		}
		ammo.previousOrigin.set(ammo.origin().source);
		return res;
	}

}
