package org.bricks.engine.event.handler;

public class EventHandlerRegistrator implements EventHandleRegistrator{

	public void registerEventHandlers() {
			EventHandlerManager.registerHandler(com.odmyhal.sf.staff.Ship.class, org.bricks.engine.event.OverlapEvent.class, "ShipSource@sf.odmyhal.com", new ShipOverlapEventHandler_2());
			EventHandlerManager.registerHandler(com.odmyhal.sf.staff.Ammunition.class, org.bricks.engine.event.OverlapEvent.class, "IslandSource@sf.odmyhal.com", new AmmunitionOverlapEventHandler_1());
			EventHandlerManager.registerHandler(com.odmyhal.sf.staff.Ammunition.class, org.bricks.engine.event.OverlapEvent.class, "ShipSource@sf.odmyhal.com", new AmmunitionOverlapEventHandler());
			EventHandlerManager.registerHandler(com.odmyhal.sf.staff.Ship.class, org.bricks.engine.event.OverlapEvent.class, "ShipAmmunitio@sf.myhal.com", new ShipOverlapEventHandler_1());
			EventHandlerManager.registerHandler(com.odmyhal.sf.staff.Ammunition.class, com.odmyhal.sf.process.FaceWaterEvent.class, "ShipAmmunitio@sf.myhal.com", new AmmunitionFaceWaterEventHandler());
			EventHandlerManager.registerHandler(com.odmyhal.sf.staff.Ship.class, com.odmyhal.sf.staff.GetOnSightEvent.class, "UserSourceType@extent.bricks.org", new ShipGetOnSightEventHandler());
			EventHandlerManager.registerHandler(com.odmyhal.sf.staff.Ship.class, org.bricks.engine.event.OverlapEvent.class, "IslandSource@sf.odmyhal.com", new ShipOverlapEventHandler());
			EventHandlerManager.registerHandler(com.odmyhal.sf.staff.Ship.class, org.bricks.extent.event.FireEvent.class, "UserSourceType@extent.bricks.org", new ShipFireEventHandler());
		}
}