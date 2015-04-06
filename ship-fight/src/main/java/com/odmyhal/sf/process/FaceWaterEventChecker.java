package com.odmyhal.sf.process;

import org.bricks.engine.event.Event;
import org.bricks.engine.event.check.CheckerType;
import org.bricks.engine.event.check.CommonEventChecker;

import com.badlogic.gdx.math.Vector3;
import com.odmyhal.sf.staff.Ammunition;

public class FaceWaterEventChecker extends CommonEventChecker<Ammunition>{
	
	private static final CheckerType CHECK_TYPE = CheckerType.registerCheckerType();
//	private static final FaceWaterEventChecker instance = new FaceWaterEventChecker(CHECK_TYPE);
	private Vector3 checkerOrigin = new Vector3();
	
	public FaceWaterEventChecker(){
		super(CHECK_TYPE);
	}
	
	public FaceWaterEventChecker(CheckerType ch){
		super(ch);
	}
/*	
	public static FaceWaterEventChecker instance(){
		return instance;
	}
*/
	@Override
	protected Event produceEvent(Ammunition ammo) {
/*		float dz = ammo.previousOrigin.z / ammo.previousOrigin.z - ammo.origin().source.z;
		float x = ammo.previousOrigin.x + (ammo.origin().source.x -  ammo.previousOrigin.x) * dz;
		float y = ammo.previousOrigin.y - (ammo.origin().source.y - ammo.previousOrigin.y) * dz;*/
		Vector3 org = ammo.origin().source;
		float dz = org.z / ammo.lastMove.source.z;
		if(dz == 0){
			return new FaceWaterEvent(ammo, new Vector3(org.x, org.y, 0f));
		}
		float x = org.x - ammo.lastMove.source.x * dz;
		float y = org.y - ammo.lastMove.source.y * dz;
		return new FaceWaterEvent(ammo, new Vector3(x, y, 0f));
	}

	@Override
	protected boolean ready(Ammunition ammo) {
		if(this.checkerOrigin.equals(ammo.origin().source)){
			return false;
		}
		Vector3 src = ammo.origin().source;
		checkerOrigin.set(src);
		if(src.z == 0){
			return true;
		}else if(src.z > 0 && ammo.lastMove.source.z > src.z){
			return true;
		}else if(src.z < 0 && ammo.lastMove.source.z < src.z){
			return true;
		}
		return false;
/*		if(ammo.origin().source.z <= 0 && ammo.previousOrigin.z > 0){
			res = true;
		}
		ammo.previousOrigin.set(ammo.origin().source);
		return res;*/
	}

}
