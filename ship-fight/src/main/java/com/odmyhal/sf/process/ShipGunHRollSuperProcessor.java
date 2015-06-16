package com.odmyhal.sf.process;

import org.bricks.core.entity.Fpoint;
import org.bricks.extent.processor.tbroll.Butt;

import com.badlogic.gdx.math.Vector3;
import com.odmyhal.sf.staff.Ship;

public class ShipGunHRollSuperProcessor extends ShipGunHRollProcessor {
	
	private final ShipGunVRollSuperProcessor verticalProcessor;

	public ShipGunHRollSuperProcessor(Ship target, ShipGunVRollSuperProcessor verticalProcessor) {
		super(target);
		this.verticalProcessor = verticalProcessor;
	}

	@Override
	protected void fetchButtPoint(Butt butt, Vector3 buttCentral) {
		Fpoint accessiblePoint = verticalProcessor.accessiblePoint();
		buttCentral.x = accessiblePoint.x;
		buttCentral.y = accessiblePoint.y;
		//Z has no matter in this case...
		buttCentral.z = 20f;
	}
	

}
