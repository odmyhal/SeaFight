package org.bricks.engine.event.handler;

public class EventHandlerRegistrator implements EventHandleRegistrator{

	public void registerEventHandlers() {
			EventHandlerManager.registerHandler(com.odmyhal.sf.staff.Ship.class, org.bricks.engine.event.PrintOverlapEvent.class, "ShipSource@sf.odmyhal.com", new ShipPrintOverlapEventHandler_2());
			EventHandlerManager.registerHandler(com.odmyhal.sf.staff.Ship.class, org.bricks.engine.event.PrintOverlapEvent.class, "ShipAmmunitio@sf.myhal.com", new ShipPrintOverlapEventHandler_1());
			EventHandlerManager.registerHandler(com.odmyhal.sf.staff.Ammunition.class, org.bricks.engine.event.PrintOverlapEvent.class, "IslandSource@sf.odmyhal.com", new AmmunitionPrintOverlapEventHandler_1());
			EventHandlerManager.registerHandler(com.odmyhal.sf.staff.Ammunition.class, com.odmyhal.sf.process.FaceWaterEvent.class, "ShipAmmunitio@sf.myhal.com", new AmmunitionFaceWaterEventHandler());
			EventHandlerManager.registerHandler(com.odmyhal.sf.staff.Ship.class, com.odmyhal.sf.staff.GetOnSightEvent.class, "UserSourceType@extent.bricks.org", new ShipGetOnSightEventHandler());
			EventHandlerManager.registerHandler(com.odmyhal.sf.staff.Ammunition.class, org.bricks.engine.event.PrintOverlapEvent.class, "ShipSource@sf.odmyhal.com", new AmmunitionPrintOverlapEventHandler());
			EventHandlerManager.registerHandler(com.odmyhal.sf.staff.Ship.class, com.odmyhal.sf.staff.Ship.ShipOutOfWorld.class, "ShipSource@sf.odmyhal.com", new ShipShipOutOfWorldHandler());
			EventHandlerManager.registerHandler(com.odmyhal.sf.staff.Ship.class, org.bricks.extent.event.FireEvent.class, "UserSourceType@extent.bricks.org", new ShipFireEventHandler());
			EventHandlerManager.registerHandler(com.odmyhal.sf.staff.Ship.class, org.bricks.engine.event.PrintOverlapEvent.class, "IslandSource@sf.odmyhal.com", new ShipPrintOverlapEventHandler());
		}
}