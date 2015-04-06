package org.bricks.engine.event.handler;

import org.bricks.engine.event.OverlapEvent;
import com.odmyhal.sf.staff.Ammunition;

public class AmmunitionOverlapEventHandler implements EventHandler<Ammunition, OverlapEvent> {
	public void processEvent(Ammunition target, OverlapEvent event) {
		target.hitStone(event);
	}
}