package com.odmyhal.sf.process;

import org.bricks.engine.event.BaseEvent;
import org.bricks.extent.event.ExtentEventGroups;

import com.badlogic.gdx.math.Vector3;
import com.odmyhal.sf.staff.Ammunition;

public class FaceWaterEvent extends BaseEvent<Ammunition>{
	
	private Ammunition ammo;
	private Vector3 touchPoint;
	
	public FaceWaterEvent(Ammunition ammo, Vector3 tp){
		this.ammo = ammo;
		this.touchPoint = tp;
	}

	public int getEventGroupCode() {
		return ExtentEventGroups.FIRE_EV_GROUP;
	}

	public String sourceType() {
		return ammo.sourceType();
	}

	@Override
	public Ammunition getEventSource() {
		return ammo;
	}
	
	public Vector3 touchPoint(){
		return touchPoint;
	}

}
