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
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.odmyhal.sf.model.bubble.BlabKeeper;

public class ShaderWaver implements RenderableProvider, Motorable{
	
	private static final float PI2 = (float) (Math.PI * 2);
	
	private ModelInstance waveInstance;
	private float x = 0, y = 0, z = 0;
	private WaveData waveData;
	private float radSpeed, add;
	private long lastCheckTime;
	
	private float translateX, translateY;
//	private int rowsCount, colsCount;
	private int waverPerSectorX, waverPerSectorY;
	private Iterable<District> renderDistricts;
	
	private Camera camera;
	public final BlabKeeper blabKeeper = new BlabKeeper();
	
	public ShaderWaver(){
		waveData = new WaveData();
		waveInstance = ModelStorage.instance().getModelInstance("shader-wave");
		waveInstance.userData = waveData;
		float waveCycle = Engine.preferences.getFloat("waver.animate.cycle.sectime", 20f) * 1000;
		float waveCount = Engine.preferences.getFloat("waver.net.wave.count", 1f);
		radSpeed = PI2 * waveCount / waveCycle;
		
		int length = Engine.preferences.getInt("waver.net.length", 50);
		translateX = length * Engine.preferences.getFloat("waver.net.step.x", 4);
		translateY = length * Engine.preferences.getFloat("waver.net.step.y", 4);
		
//		rowsCount = Engine.preferences.getInt("world.rows.count", 1);
//		colsCount = Engine.preferences.getInt("world.cols.count", 1);
		
		float sectorLength = Engine.preferences.getFloat("sector.length", 5000f);
		waverPerSectorX = (int) (sectorLength / translateX);
		waverPerSectorY = (int) (sectorLength / translateY);
	}
	
	public void setCamera(Camera camera){
		this.camera = camera;
	}
	
	public void setRenderDistricts(Iterable<District> districts){
		this.renderDistricts = districts;
	}

	public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
		
//		waveInstance.getRenderables(renderables, pool);
		waveInstance.transform.setToTranslation(x, y, z);
		synchronized(this){
			for(District d : renderDistricts){
				Point corner = d.getCorner();
				for(int ix = 0; ix < waverPerSectorX; ix++){
					for(int jy = 0; jy < waverPerSectorY; jy++){
						waveInstance.transform.setToTranslation(corner.getFX() + translateX * ix, corner.getFY() + translateY * jy, z);
						waveInstance.getRenderables(renderables, pool);
					}
				}
			}
		}
/*		synchronized(this){
			for(int i = 0; i < colsCount; i++){
				for(int ix = 0; ix < waverPerSectorX; ix++){
					for(int j = 0; j < rowsCount; j++){
						for(int jy = 0; jy < waverPerSectorY; jy++){
							waveInstance.transform.setToTranslation( (i * waverPerSectorX + ix) * translateX, 
									(j * waverPerSectorY + jy) * translateY, z);
							waveInstance.getRenderables(renderables, pool);
						}
					}
				}
			}
		}*/
		
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
			waveData.start += radSpeed * diffTime;
			if(waveData.start > PI2){
				waveData.start -= PI2;
			}
			blabKeeper.processBubbles(curTime);
			waveData.adjustCurrentPrint();
			lastCheckTime = curTime;
		}
	}
	
	
	
	public class WaveData extends PrintableBase<WaveDataPrint>{
		
		private float amplitude;
		private float lenX, lenY;
		private float start;
//		private Color color;
		
		private WaveData(){
			int length = Engine.preferences.getInt("waver.net.length", 50);
			this.lenX = Engine.preferences.getFloat("waver.net.step.x", 4) * length;
			this.lenY = Engine.preferences.getFloat("waver.net.step.y", 4) * length;
			this.amplitude = Engine.preferences.getInt("waver.net.amplitude", 3);
			this.start = 0f;//(float) Math.PI / 2;
			this.initPrintStore();
//			this.color = new Color(0.357f, 0.765f, 0.863f, 1f);
		}
		
		public float amplitude(){
			return this.amplitude;
		}
		
		public float lenX(){
			return this.lenX;
		}
		
		public float lenY(){
			return this.lenY;
		}
		
		public float start(){
			return this.start;
		}

		public WaveDataPrint print() {
			return new WaveDataPrint(this.printStore);
		}

	}

	public class WaveDataPrint extends BasePrint<WaveData>{
		
		public float start;
		public BlabKeeper.Blab[] bubbles = new BlabKeeper.Blab[BlabKeeper.SHADER_BLAB_COUNT_TOTAL];
		public int size;

		public WaveDataPrint(PrintStore ps) {
			super(ps);
			for(int i = 0; i < bubbles.length; i++){
				bubbles[i] = blabKeeper.emptyBlub();
			}
		}

		public void init() {
			this.start = this.getTarget().start;
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
			size = i + 1;
		}

	}

	@Override
	public void timerSet(long time) {
		lastCheckTime = time;
		blabKeeper.timerSet(time);
	}

	@Override
	public void timerAdd(long time) {
		lastCheckTime += time;
		blabKeeper.timerSet(time);
	}
}
