package com.odmyhal.sf.staff;

import org.bricks.engine.event.BaseEvent;
import org.bricks.engine.event.EventSource;
import org.bricks.extent.event.ExtentEventGroups;

public class GetOnSightEvent extends BaseEvent{
	
	private Ship sight;

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
	
	public Ship getSight(){
		return sight;
	}
	
	public void setSight(Ship sight){
		this.sight = sight;
	}
}
