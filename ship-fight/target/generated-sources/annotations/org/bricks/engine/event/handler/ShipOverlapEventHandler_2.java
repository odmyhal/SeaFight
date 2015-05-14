package org.bricks.engine.event.handler;

import org.bricks.engine.event.OverlapEvent;
import com.odmyhal.sf.staff.Ship;

public class ShipOverlapEventHandler_2 implements EventHandler<Ship, OverlapEvent> {
	public void processEvent(Ship target, OverlapEvent event) {
		target.hitAnotherShip(event);
	}
}