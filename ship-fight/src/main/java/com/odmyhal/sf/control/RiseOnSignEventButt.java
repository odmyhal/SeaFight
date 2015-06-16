package com.odmyhal.sf.control;

import org.bricks.engine.event.Event;
import org.bricks.engine.staff.Liver;
import org.bricks.extent.control.RiseEventButton;

import com.odmyhal.sf.interact.Interactive;
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
		event.setButt(Interactive.activeButt());
		return event;
	}

}
