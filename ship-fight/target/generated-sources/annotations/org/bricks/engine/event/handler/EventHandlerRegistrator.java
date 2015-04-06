package org.bricks.engine.event.handler;

public class EventHandlerRegistrator implements EventHandleRegistrator{

	public void registerEventHandlers() {
			EventHandlerManager.registerHandler(com.odmyhal.sf.staff.Ammunition.class, com.odmyhal.sf.process.FaceWaterEvent.class, "ShipAmmunitio@sf.myhal.com", new AmmunitionFaceWaterEventHandler());
			EventHandlerManager.registerHandler(com.odmyhal.sf.staff.Ship.class, org.bricks.engine.event.OverlapEvent.class, "IslandSource@sf.odmyhal.com", new ShipOverlapEventHandler());
			EventHandlerManager.registerHandler(com.odmyhal.sf.staff.Ship.class, org.bricks.extent.event.FireEvent.class, "UserSourceType@extent.bricks.org", new ShipFireEventHandler());
			EventHandlerManager.registerHandler(com.odmyhal.sf.staff.Ammunition.class, org.bricks.engine.event.OverlapEvent.class, "IslandSource@sf.odmyhal.com", new AmmunitionOverlapEventHandler());
		}
}