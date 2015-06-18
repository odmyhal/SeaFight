package com.odmyhal.sf.interact;

import org.bricks.extent.interact.InteractiveHandler.Interactive;

import com.badlogic.gdx.math.Vector3;
import com.odmyhal.sf.staff.GetOnSightEvent;
import com.odmyhal.sf.staff.Ship;

public class ShipTouchHandler implements Interactive<Ship>{
	
	private Ship myShip;
	
	public ShipTouchHandler(Ship ship){
		myShip = ship;
	}

	@Override
	public void handleTap(Ship entity, Vector3 touchPoint) {
		if(myShip == entity){
			return;
		}
		GetOnSightEvent event = new GetOnSightEvent();
		event.setButt(entity);
		myShip.addEvent(event);
	}

}
