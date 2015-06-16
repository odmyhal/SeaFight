package com.odmyhal.sf.staff;

import org.bricks.engine.event.BaseEvent;
import org.bricks.engine.event.EventSource;
import org.bricks.extent.event.ExtentEventGroups;
import org.bricks.extent.processor.tbroll.Butt;

public class GetOnSightEvent extends BaseEvent{
	
	private Butt butt;

	public int getEventGroupCode() {
		return ExtentEventGroups.FIRE_EV_GROUP;
	}

	public String sourceType() {
		return ExtentEventGroups.USER_SOURCE_TYPE;
	}

	@Override
	public EventSource getEventSource() {
		return null;
	}
	
	public Butt getButt(){
		return butt;
	}
	
	public void setButt(Butt butt){
		this.butt = butt;
	}
}
