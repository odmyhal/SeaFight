package com.odmyhal.sf.model;

import org.apache.commons.lang.Validate;
import org.bricks.core.entity.Fpoint;
import org.bricks.engine.staff.AvareTimer;
import org.bricks.engine.tool.Walk2D;

import com.badlogic.gdx.math.Vector2;

public class Wave implements AvareTimer{

	private Fpoint vector = new Fpoint();
	private Walk2D legs = new Walk2D();
	private Vector2 position = new Vector2();
	
	private float sectorLenX, sectorLenY, initialAmplitude, amplitude;
	private long bornTime, timeToLive;
	
	public void initialize(float directionX, float directionY, long timeToLive, float amplitude, float positionX, float positionY, float maxX, float maxY){
		vector.set(directionX, directionY);
		this.timeToLive = timeToLive;
		this.initialAmplitude = amplitude;
		this.position.set(positionX, positionY);
		this.amplitude = 0;
		this.sectorLenX = maxX;
		this.sectorLenY = maxY;
		Validate.isTrue(sectorLenX > 0 && sectorLenY > 0);
		Validate.isTrue(position.x >= 0 && position.x < sectorLenX);
		Validate.isTrue(position.y >= 0 && position.y < sectorLenY);
	}
	
	public boolean leave(long curTime){
		float diffTime = curTime - bornTime;
		if(legs.move(curTime, vector)){
			Fpoint movement = legs.lastMove().source;
			position.add(movement.x, movement.y);
			coercePosition();
			amplitude = (float) (Math.sin(Math.PI * diffTime / timeToLive) * initialAmplitude);
		}
		return diffTime < timeToLive;
	}
	
	private void coercePosition(){
		while(position.x >= sectorLenX){
			position.x -= sectorLenX;
		}
		while(position.x < 0){
			position.x += sectorLenX;
		}
		while(position.y >= sectorLenY){
			position.y -= sectorLenY;
		}
		while(position.y < 0){
			position.y += sectorLenY;
		}
	}
	
	public Vector2 getPositon(){
		return position;
	}
	
	public float getAmplitude(){
		return amplitude;
	}

	@Override
	public void timerSet(long time) {
		bornTime = time;
		legs.timerSet(time);
	}

	@Override
	public void timerAdd(long time) {
		bornTime += time;
		legs.timerAdd(time);
	}
}
