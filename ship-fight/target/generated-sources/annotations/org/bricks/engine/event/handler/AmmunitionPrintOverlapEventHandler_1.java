package org.bricks.engine.event.handler;

import org.bricks.engine.event.PrintOverlapEvent;
import com.odmyhal.sf.staff.Ammunition;

public class AmmunitionPrintOverlapEventHandler_1 implements EventHandler<Ammunition, PrintOverlapEvent> {
	public void processEvent(Ammunition target, PrintOverlapEvent event) {
		target.hitStone(event);
	}
}