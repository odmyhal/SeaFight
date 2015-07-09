package org.bricks.engine.event.handler;

import org.bricks.engine.event.PrintOverlapEvent;
import com.odmyhal.sf.staff.Ship;

public class ShipPrintOverlapEventHandler implements EventHandler<Ship, PrintOverlapEvent> {
	public void processEvent(Ship target, PrintOverlapEvent event) {
		target.hitCannon(event);
	}
}