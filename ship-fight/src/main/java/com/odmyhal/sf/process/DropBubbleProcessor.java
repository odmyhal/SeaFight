package com.odmyhal.sf.process;

import org.bricks.core.entity.Fpoint;
import org.bricks.core.help.VectorHelper;
import org.bricks.engine.event.check.CheckerType;
import org.bricks.engine.processor.Processor;

import com.odmyhal.sf.model.bubble.BlabKeeper;
import com.odmyhal.sf.staff.Ship;

public class DropBubbleProcessor extends Processor<Ship> {
	
	private static final CheckerType DBP_CHECKER_TYPE = CheckerType.registerCheckerType();
	private static final float maxSpeed = Ship.prefs.getFloat("ship.speed.directional.max", 1000f);
	private static final float stepBack = 750f;
	private static final long maxBubbleInterval = BlabKeeper.prefs.getLong("ship.bubble.time.interval.max", 1000),
			minBubbleInterval = BlabKeeper.prefs.getLong("ship.bubble.time.interval.min", 200),
			timeToLive = BlabKeeper.prefs.getLong("ship.bubble.time.live", 4000);
	private static final float maxBubbleRadius = BlabKeeper.prefs.getFloat("ship.bubble.radius.max", 800f), 
			minBubbleRadius = BlabKeeper.prefs.getFloat("ship.bubble.radius.min", 400f), 
			startBubbleRadius = BlabKeeper.prefs.getFloat("ship.bubble.radius.start", 200f);
	private static final float maxBubbleAmplitude = BlabKeeper.prefs.getFloat("ship.bubble.amplitude.max", 0.2f), 
			minBubbleAmplitude = BlabKeeper.prefs.getFloat("ship.bubble.amplitude.min", 0.1f);
	
	private BlabKeeper blabKeeper;
	private long bubbleTime;
	
	private Fpoint tmpVector = new Fpoint();

	public DropBubbleProcessor(BlabKeeper bk) {
		super(DBP_CHECKER_TYPE);
		this.blabKeeper = bk;
	}

	@Override
	public void process(Ship ship, long currentTime) {
		float diffTime = currentTime - bubbleTime;
		Fpoint shipVector = ship.getVector().source;
		float vectorLen = (float) VectorHelper.vectorLen(shipVector);
		float intervalTime = maxBubbleInterval - (maxBubbleInterval - minBubbleInterval) * vectorLen / maxSpeed;
		if(diffTime > intervalTime){
			Fpoint shipCenter = ship.origin().source;
	
			double rotation = ship.getRotation();
			tmpVector.setX(shipCenter.getFX() - stepBack * (float) Math.cos(rotation));
			tmpVector.setY(shipCenter.getFY() - stepBack * (float) Math.sin(rotation));
			
			float maxRadius = minBubbleRadius + (maxBubbleRadius - minBubbleRadius) * vectorLen / maxSpeed;
			float amplitude = minBubbleAmplitude + (maxBubbleAmplitude - minBubbleAmplitude) * vectorLen / maxSpeed;
			BlabKeeper.Blab bubble = blabKeeper.new Blab(tmpVector.x, tmpVector.y,
					amplitude, timeToLive, startBubbleRadius, maxRadius);
			
			blabKeeper.pushBlab(bubble);
			
			bubbleTime = currentTime;
		}
		
	}

	public void activate(Ship target, long curTime){
		super.activate(target, curTime);
		bubbleTime = curTime;
		System.out.println("Sets checkTime to blabKeeper " + curTime);
		this.blabKeeper.setCheckTime(curTime);
	}
}
