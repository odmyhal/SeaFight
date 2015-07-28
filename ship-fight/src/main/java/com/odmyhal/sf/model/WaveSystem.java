package com.odmyhal.sf.model;

import java.util.Iterator;
import java.util.prefs.Preferences;

import org.bricks.core.entity.Fpoint;
import org.bricks.engine.Engine;
import org.bricks.engine.staff.AvareTimer;
import org.bricks.utils.Cache;
import org.bricks.utils.Cache.DataProvider;
import org.bricks.utils.LinkLoop;
import com.odmyhal.sf.model.Wave;

public class WaveSystem implements AvareTimer, Iterable<Wave>{
	
	public static final Preferences prefs = Preferences.userRoot().node("sf.sea.wave");
	
	static{
		Cache.registerCache(Wave.class, new DataProvider<Wave>(){
			@Override
			public Wave provideNew() {
				return new Wave();
			}
		});
	}
	
	private int maxWaveCount;
	
	private long checkTime;
	private long waveLifeSpan;
	private float waveAmplitude, sectorLenX, sectorLenY, waveLifeVolatility, waveAmplitudeVolatility, moveVolatility;
	private Fpoint waveMoveDirection = new Fpoint();
	
	private long emitTime = 0L;
	private int waveSize = 0;
	
	private LinkLoop<Wave> waves = new LinkLoop<Wave>();
	
	public WaveSystem(){
		this(prefs);
	}
	
	public WaveSystem(Preferences prefs){
		init(prefs);
	}
	
	public int getMaxWaveCount(){
		return this.maxWaveCount;
	}
	
	public int getWaveCount(){
		return this.waveSize;
	}

	public void init(Preferences prefs){
		this.maxWaveCount = prefs.getInt("wave.count.max", 5);
		this.waveLifeSpan = prefs.getLong("wave.lifespan.mills", 10000L);
		this.waveLifeVolatility = prefs.getFloat("wave.lifespan.volatility", 0.5f);
		this.waveAmplitude = prefs.getFloat("wave.amplitude", 100f);
		this.waveAmplitudeVolatility = prefs.getFloat("wave.amplitude.volatility", 0.5f);
		this.waveMoveDirection.x = prefs.getFloat("wave.move.x", 1000f);
		this.waveMoveDirection.y = prefs.getFloat("wave.move.y", 1000f);
		this.moveVolatility = prefs.getFloat("wave.move.volatility", 0.5f);
		
		int length = Engine.preferences.getInt("waver.net.length", 50);
		this.sectorLenX = Engine.preferences.getFloat("waver.net.step.x", 4) * length;
		this.sectorLenY = Engine.preferences.getFloat("waver.net.step.y", 4) * length;
		
		emitTime = this.waveLifeSpan / maxWaveCount;
	}
	
	public void procesWaves(long curTime){
		long diffTime = curTime - checkTime;
		if(diffTime > emitTime && waveSize < maxWaveCount){
			Wave wave = Cache.get(Wave.class);
			wave.initialize((float) (waveMoveDirection.x * (1 + 2 * moveVolatility * (0.5f - Math.random()))), 
					(float) (waveMoveDirection.y * (1 + 2 * moveVolatility * (0.5f - Math.random()))), 
					(long) (waveLifeSpan * (1 + 2 * waveLifeVolatility * (0.5f - Math.random()))), 
					(float) (waveAmplitude * (1 + 2 * waveAmplitudeVolatility * (0.5f - Math.random()))), 
					(float) (sectorLenX * Math.random()), (float) (sectorLenY * Math.random()),
					sectorLenX, sectorLenY);
/*			wave.initialize(0f, 
					0f, 
					(long) (waveLifeSpan), 
					(float) (waveAmplitude), 
					prefs.getFloat("test.wave.pos.x", 1000f), prefs.getFloat("test.wave.pos.y", 1000f),
					sectorLenX, sectorLenY);*/
			wave.timerSet(curTime);
//			System.out.println("Wave(" + wave + "), thread(" + Thread.currentThread().getName() + ") set time: " + curTime);
			waves.add(wave);
			checkTime += emitTime;
			++waveSize;
		}
		Iterator<Wave> iterator = waves.iterator();
		int k = 0;
		while(iterator.hasNext()){
			Wave wave = iterator.next();
//			System.out.println("check wave(" + wave + "), thread(" + Thread.currentThread().getName() + ") leave in time: " + curTime);
			if(wave.leave(curTime)){
				continue;
			}
			iterator.remove();
			Cache.put(wave);
			--waveSize;
		}
	}

	@Override
	public void timerSet(long time) {
		checkTime = time;
		for(Wave wave : waves){
			wave.timerSet(time);
		}
	}

	@Override
	public void timerAdd(long time) {
		checkTime += time;
		for(Wave wave : waves){
			wave.timerAdd(time);
		}
	}

	@Override
	public Iterator<Wave> iterator() {
		return waves.iterator();
	}

}
