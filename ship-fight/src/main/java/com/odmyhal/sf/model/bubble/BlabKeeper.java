package com.odmyhal.sf.model.bubble;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.prefs.Preferences;

import org.bricks.engine.tool.Quarantine;
import org.bricks.exception.Validate;

public class BlabKeeper implements Iterable<BlabKeeper.Blab>{
	
	public static final Preferences prefs = Preferences.userRoot().node("sf.ship.bubble");
	public static final int BLAB_COUNT_TOTAL = prefs.getInt("ship.bubble.amount.total", 100);
	
	private Quarantine<Blab> quarantine = new Quarantine<Blab>(prefs.getInt("blab.keeper.quarantine.size", 10));
	private List<Blab> bubbles = new LinkedList<Blab>();
	private long checkTime = 0;
	private int size;
	
	public void pushBlab(Blab blab){
		quarantine.push(blab);
	}
	
	public void processBubbles(long currentTime){
		for(Blab b : quarantine){
			bubbles.add(b);
			++size;
			Validate.isFalse(size > BLAB_COUNT_TOTAL, "BlabKeeper exceeded maximum amound of bubbles..." );
		}
		Iterator<Blab> iterator = bubbles.iterator();
		long diffTime = currentTime - checkTime;
		if(diffTime > 20){
			while(iterator.hasNext()){
				Blab blab = iterator.next();
				if( !blab.live(diffTime) ){
					iterator.remove();
					--size;
				}
			}
			checkTime = currentTime;
		}
	}
	
	public int size(){
		return size;
	}
	
	public void setCheckTime(long checkTime){
		this.checkTime = checkTime;
	}

	public class Blab{

		private long timeToLive;
		private long timeLeft;
		private float maxAmplitude, minRadius, maxRadius;
		public float amplitude, radius, x, y;
		
		public Blab(){
			//do nothing
		}
		
		public Blab(float x, float y, float amplitude, long timeToLive, float minRadius, float maxRadius){
			init(x, y, amplitude, timeToLive, minRadius, maxRadius);
		}
		
		public void setShaderData(float x, float y, float amplitude, float radius){
			this.amplitude = amplitude;
			this.radius = radius;
			this.x = x;
			this.y = y;
		}
		
		public void init(float x, float y, float amplitude, long timeToLive, float minRadius, float maxRadius){
			this.x = x;
			this.y = y;
			this.timeToLive = timeToLive;
			this.timeLeft = timeToLive;
			this.maxAmplitude = this.amplitude = amplitude;
			this.minRadius = this.radius = minRadius;
			this.maxRadius = maxRadius;
		}
		
		public boolean live(long diffTime){
			timeLeft -= diffTime;
			if(timeLeft > 0){
				double k = (double) diffTime / timeToLive;
				amplitude -= maxAmplitude * k;
				radius += (maxRadius - minRadius) * k;
				return true;
			}
			return false;
		}
		
		public String toString(){
			return String.format("Blab[x=%.2f, y=%.2f, amplitude=%.2f, radius=%.2f, timeToLIve=%d, timeLeft=%d]", x, y, amplitude, radius, timeToLive, timeLeft);
		}
	}

	public Iterator<Blab> iterator() {
		return bubbles.iterator();
	}
}
