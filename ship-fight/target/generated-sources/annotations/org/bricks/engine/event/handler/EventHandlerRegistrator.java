package org.bricks.engine.event.handler;

public class EventHandlerRegistrator implements EventHandleRegistrator{

	public void registerEventHandlers() {
			EventHandlerManager.registerHandler(com.odmyhal.sf.staff.Ship.class, org.bricks.engine.event.OverlapEvent.class, "IslandSource@sf.odmyhal.com", new ShipOverlapEventHandler());
		}
}