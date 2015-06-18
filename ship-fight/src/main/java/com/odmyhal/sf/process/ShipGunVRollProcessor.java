package com.odmyhal.sf.process;

import org.bricks.core.entity.Fpoint;
import org.bricks.engine.neve.WalkPrint;
import org.bricks.exception.Validate;
import org.bricks.extent.processor.tbroll.RollNodeToEntityVProcessor;

import com.badlogic.gdx.math.Vector3;
import com.odmyhal.sf.staff.Ammunition;
import com.odmyhal.sf.staff.Ship;

public class ShipGunVRollProcessor extends RollNodeToEntityVProcessor<Ship>{
	
	private static final float stepBack = 150f;

	public ShipGunVRollProcessor(Ship target) {
		super(target, "stvol");
		this.setBulletSpeed(Ammunition.prefs.getFloat("ship.ammo1.speed.directional", 1f));
		this.setBulletAcceleration(Ammunition.prefs.getFloat("ship.ammo1.acceleration.z", 0f));
		this.setRotationSpeed(Ship.prefs.getFloat("ship.roll.speed.radians", 0.5f));
	}

	@Override
	public float convertToTargetRotation(double calcRotation) {
		Validate.isTrue(calcRotation > -Math.PI / 2 && calcRotation < Math.PI / 2);
		return (float) (Math.PI - calcRotation);
	}
/*
	@Override
	public void fetchButtPoint(Ship butt, Vector3 buttCentral) {
		WalkPrint<?, Fpoint> sp = butt.getSafePrint();
		Fpoint shipCenter = sp.getOrigin().source;
		double rotation = sp.getRotation();
		buttCentral.x = shipCenter.getFX() - stepBack * (float) Math.cos(rotation);
		buttCentral.y = shipCenter.getFY() - stepBack * (float) Math.sin(rotation);
		buttCentral.z = 20f;
		sp.free();
	}
*/
	@Override
	public Vector3 provideStartPoint(Ship ship, long processTime) {
		return ship.getGunPoint(2, processTime);
	}

}
