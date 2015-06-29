package com.odmyhal.sf.interact;

import org.bricks.engine.item.Stone;
import org.bricks.extent.interact.InteractiveHandler.Interactive;
import org.bricks.extent.interact.SpaceInteract.DefaultHandler;
import org.bricks.extent.processor.tbroll.Vector3Butt;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.odmyhal.sf.staff.GetOnSightEvent;
import com.odmyhal.sf.staff.Ship;

public class StoneTouchHandler implements Interactive<Stone>, DefaultHandler {
	
	private Ship ship;
	private float cameraFar;
	
	public StoneTouchHandler(Ship ship, float cameraFar){
		this.ship = ship;
		this.cameraFar = cameraFar;
	}

	@Override
	public void handleTap(Stone entity, Vector3 touchPoint) {
		GetOnSightEvent event = new GetOnSightEvent();
		Vector3Butt v3b = new Vector3Butt(touchPoint);
		event.setButt(v3b);
		ship.addEvent(event);
	}

	@Override
	public void handleTap(Ray ray) {
		float len = cameraFar;
		if(ray.direction.z * ray.origin.z < 0){
			len = Math.abs(ray.origin.z / ray.direction.z);
		}
		Vector3Butt v3b = new Vector3Butt(ray.origin.x + ray.direction.x * len, ray.origin.y + ray.direction.y * len, ray.origin.z + ray.direction.z * len);
		GetOnSightEvent event = new GetOnSightEvent();
		event.setButt(v3b);
		ship.addEvent(event);
	}

}
