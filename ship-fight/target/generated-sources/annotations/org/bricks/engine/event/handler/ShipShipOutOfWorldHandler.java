package org.bricks.engine.event.handler;

import com.odmyhal.sf.staff.Ship.ShipOutOfWorld;
import com.odmyhal.sf.staff.Ship;

public class ShipShipOutOfWorldHandler implements EventHandler<Ship, ShipOutOfWorld> {
	public void processEvent(Ship target, ShipOutOfWorld event) {
		target.shipOutOfWorld(event);
	}
}