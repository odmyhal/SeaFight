package com.odmyhal.sf.model;

import org.bircks.entierprise.model.ModelStorage;
import org.bricks.core.entity.Point;
import org.bricks.engine.Engine;
import org.bricks.engine.item.Motorable;
import org.bricks.engine.neve.BasePrint;
import org.bricks.engine.neve.Imprint;
import org.bricks.engine.neve.PrintStore;
import org.bricks.engine.neve.PrintableBase;
import org.bricks.engine.pool.District;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.odmyhal.sf.model.bubble.BlabKeeper;

public class ShaderWaver implements RenderableProvider, Motorable{
	
	private static final float PI2 = (float) (Math.PI * 2);
	
	private ModelInstance waveInstance;
	private float x = 0, y = 0, z = 0;
	private WaveData waveData;
//	private float radSpeed, add;
	private long lastCheckTime;
	
	private float translateX, translateY;
	private int modelPerSectorX, modelPerSectorY;
	private Iterable<District> renderDistricts;
	
	private Camera camera;
	
	public final BlabKeeper blabKeeper = new BlabKeeper();
	private final WaveSystem waveSystem = new WaveSystem();
	
	public ShaderWaver(){
		waveData = new WaveData();
		waveInstance = ModelStorage.instance().getModelInstance("shader-wave");
		waveInstance.userData = waveData;
/*		float waveCycle = Engine.preferences.getFloat("waver.animate.cycle.sectime", 20f) * 1000;
		float waveCount = Engine.preferences.getFloat("waver.net.wave.count", 1f);
		radSpeed = PI2 * waveCount / waveCycle;
*/	
		int length = Engine.preferences.getInt("waver.net.length", 50);
		translateX = length * Engine.preferences.getFloat("waver.net.step.x", 4);
		translateY = length * Engine.preferences.getFloat("waver.net.step.y", 4);
		
		float sectorLength = Engine.preferences.getFloat("sector.length", 5000f);
		modelPerSectorX = (int) (sectorLength / translateX);
		modelPerSectorY = (int) (sectorLength / translateY);
	}
	
	public void setCamera(Camera camera){
		this.camera = camera;
	}
	
	public void setRenderDistricts(Iterable<District> districts){
		this.renderDistricts = districts;
	}

	public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {

		waveInstance.transform.setToTranslation(x, y, z);
		synchronized(this){
			for(District d : renderDistricts){
				Point corner = d.getCorner();
				for(int ix = 0; ix < modelPerSectorX; ix++){
					for(int jy = 0; jy < modelPerSectorY; jy++){
						waveInstance.transform.setToTranslation(corner.getFX() + translateX * ix, corner.getFY() + translateY * jy, z);
						waveInstance.getRenderables(renderables, pool);
					}
				}
			}
		}
	}

	public void translate(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public boolean alive() {
		return true;
	}
	
	public void motorProcess(long curTime) {
		long diffTime = curTime - lastCheckTime;
		if(diffTime > 20){
/*			waveData.start += radSpeed * diffTime;
			if(waveData.start > PI2){
				waveData.start -= PI2;
			}*/
//			System.out.println("ShaderWaver process at " + curTime + " , thread: " + Thread.currentThread().getName());
			blabKeeper.processBubbles(curTime);
			waveSystem.procesWaves(curTime);
			waveData.adjustCurrentPrint();
			lastCheckTime = curTime;
		}
	}

	@Override
	public void timerSet(long time) {
		lastCheckTime = time;
		blabKeeper.timerSet(time);
		waveSystem.timerSet(time);
	}

	@Override
	public void timerAdd(long time) {
		lastCheckTime += time;
		blabKeeper.timerAdd(time);
		waveSystem.timerAdd(time);
	}
	
	
	
	public class WaveData extends PrintableBase<WaveDataPrint>{

		private WaveData(){
			this.initPrintStore();
		}
		
		public WaveDataPrint print() {
			return new WaveDataPrint(this.printStore);
		}

	}

	public class WaveDataPrint extends BasePrint<WaveData>{
		
//		public float start;
		public BlabKeeper.Blab[] bubbles = new BlabKeeper.Blab[BlabKeeper.SHADER_BLAB_COUNT_TOTAL];
		private final int maxWaveSize = waveSystem.getMaxWaveCount();
		public final Vector3[] waves = new Vector3[maxWaveSize];
		public int blabSize, wavesCount;

		public WaveDataPrint(PrintStore ps) {
			super(ps);
			for(int i = 0; i < bubbles.length; i++){
				bubbles[i] = blabKeeper.emptyBlub();
			}
			for(int j = 0; j < waves.length; j++){
				waves[j] = new Vector3();
			}
		}

		public void init() {
//			System.out.println("Shader waver started initialization");
			int i = -1;
			for(BlabKeeper.Blab source : blabKeeper){
				if(camera.frustum.pointInFrustum(source.x, source.y, 0f)){
					if(++i == BlabKeeper.SHADER_BLAB_COUNT_TOTAL){
						Gdx.app.debug("WARNING", "Exceeded shade buffer (" + BlabKeeper.SHADER_BLAB_COUNT_TOTAL + ") of bubbles");
						--i;
						break;
					}
					bubbles[i].setShaderData(source.x, source.y, source.amplitude, source.radius);
				}
			}
			blabSize = i + 1;
			i = -1;
			for(Wave wave : waveSystem){
				if(++i == maxWaveSize){
					Gdx.app.debug("WARNING", "Exceeded shade buffer (" + maxWaveSize + ") of waves");
					--i;
					break;
				}
				Vector2 wavePosition = wave.getPositon();
				waves[i].x = wavePosition.x;
				waves[i].y = wavePosition.y;
				waves[i].z = wave.getAmplitude();
			}
			wavesCount = i + 1;
//			System.out.println("Shader initialized print with " + wavesCount + " waves");
		}

	}
}
