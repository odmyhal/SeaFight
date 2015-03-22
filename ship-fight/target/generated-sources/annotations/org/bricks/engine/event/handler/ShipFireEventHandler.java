package org.bricks.engine.event.handler;

import org.bricks.extent.event.FireEvent;
import com.odmyhal.sf.staff.Ship;

public class ShipFireEventHandler implements EventHandler<Ship, FireEvent> {
	public void processEvent(Ship target, FireEvent event) {
		target.shoot(event);
	}
}