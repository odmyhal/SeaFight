package org.bricks.engine.event.handler;

import org.bricks.engine.event.PrintOverlapEvent;
import com.odmyhal.sf.staff.Ship;

public class ShipPrintOverlapEventHandler_1 implements EventHandler<Ship, PrintOverlapEvent> {
	public void processEvent(Ship target, PrintOverlapEvent event) {
		target.ammoHurt(event);
	}
}