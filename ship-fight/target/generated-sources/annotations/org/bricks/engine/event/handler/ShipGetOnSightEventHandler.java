package org.bricks.engine.event.handler;

import com.odmyhal.sf.staff.GetOnSightEvent;
import com.odmyhal.sf.staff.Ship;

public class ShipGetOnSightEventHandler implements EventHandler<Ship, GetOnSightEvent> {
	public void processEvent(Ship target, GetOnSightEvent event) {
		target.getOnSight(event);
	}
}