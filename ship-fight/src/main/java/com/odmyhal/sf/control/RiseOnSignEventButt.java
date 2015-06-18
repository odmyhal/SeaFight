package com.odmyhal.sf.control;

import org.bricks.engine.event.Event;
import org.bricks.engine.staff.Liver;
import org.bricks.extent.control.RiseEventButton;
import org.bricks.extent.interact.SpaceInteract;

import com.odmyhal.sf.staff.GetOnSightEvent;

public class RiseOnSignEventButt extends RiseEventButton{
	
	public RiseOnSignEventButt(Liver<?> liver, String text, TextButtonStyle style) {
		super(liver, text, style);
	}

	public RiseOnSignEventButt(Liver<?> liver, String text) {
		super(liver, text);
	}

	@Override
	protected Event provideEvent() {
		GetOnSightEvent event = new GetOnSightEvent();
//		event.setButt(SpaceInteract.activeButt());
//		System.out.println(Thread.currentThread().getName() + " SignButton set event to Interactive " + event.getButt() + ", of type " + event.getButt().getClass().getCanonicalName());
		return event;
	}

}
