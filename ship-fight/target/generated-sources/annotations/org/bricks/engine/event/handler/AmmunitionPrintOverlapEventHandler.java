package org.bricks.engine.event.handler;

import org.bricks.engine.event.PrintOverlapEvent;
import com.odmyhal.sf.staff.Ammunition;

public class AmmunitionPrintOverlapEventHandler implements EventHandler<Ammunition, PrintOverlapEvent> {
	public void processEvent(Ammunition target, PrintOverlapEvent event) {
		target.hitShip(event);
	}
}