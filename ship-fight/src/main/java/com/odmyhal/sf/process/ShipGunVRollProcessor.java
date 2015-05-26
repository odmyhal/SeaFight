package com.odmyhal.sf.process;

import org.bricks.core.entity.Fpoint;
import org.bricks.exception.Validate;
import org.bricks.extent.engine.processor.RollNodeToEntityVProcessor;

import com.badlogic.gdx.math.Vector3;
import com.odmyhal.sf.staff.Ammunition;
import com.odmyhal.sf.staff.Ship;

public class ShipGunVRollProcessor extends RollNodeToEntityVProcessor<Ship, Fpoint>{

	public ShipGunVRollProcessor(Ship target, String nodeOperatorName/*, Matrix4... linkMatrices*/) {
		super(target, nodeOperatorName/*, linkMatrices*/);
		this.setBulletSpeed(Ammunition.prefs.getFloat("ship.ammo1.speed.directional", 1f));
		this.setBulletAcceleration(Ammunition.prefs.getFloat("ship.ammo1.acceleration.z", 0f));
		this.setRotationSpeed(0.5f);
	}

	@Override
	public float convertToTargetRotation(double calcRotation) {
		Validate.isTrue(calcRotation >= 0 && calcRotation < Math.PI / 2);
		return (float) (Math.PI - calcRotation);
	}

	@Override
	public void fetchButtPoint(Fpoint buttOrigin, Vector3 buttCentral) {
		buttCentral.set(buttOrigin.x, buttOrigin.y, 20f);
	}

	@Override
	public Vector3 provideStartPoint(Ship ship, long processTime) {
		return ship.getGunPoint(2, processTime);
	}

}
