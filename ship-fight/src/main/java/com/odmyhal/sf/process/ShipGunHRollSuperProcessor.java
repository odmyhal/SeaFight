package com.odmyhal.sf.process;

import org.bricks.core.entity.Fpoint;
import com.odmyhal.sf.staff.Ship;

public class ShipGunHRollSuperProcessor extends ShipGunHRollProcessor {
	
	private final ShipGunVRollSuperProcessor verticalProcessor;

	public ShipGunHRollSuperProcessor(Ship target, ShipGunVRollSuperProcessor verticalProcessor) {
		super(target);
		this.verticalProcessor = verticalProcessor;
	}

	@Override
	protected void fetchButtPoint(Ship butt, Fpoint buttCentral) {
		Fpoint accessiblePoint = verticalProcessor.accessiblePoint();
		buttCentral.x = accessiblePoint.x;
		buttCentral.y = accessiblePoint.y;
	}

}
