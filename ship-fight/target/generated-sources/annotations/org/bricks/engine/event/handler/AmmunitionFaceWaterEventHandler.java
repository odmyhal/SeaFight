package org.bricks.engine.event.handler;

import com.odmyhal.sf.process.FaceWaterEvent;
import com.odmyhal.sf.staff.Ammunition;

public class AmmunitionFaceWaterEventHandler implements EventHandler<Ammunition, FaceWaterEvent> {
	public void processEvent(Ammunition target, FaceWaterEvent event) {
		target.faceWater(event);
	}
}