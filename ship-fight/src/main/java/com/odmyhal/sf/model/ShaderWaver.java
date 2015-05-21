package com.odmyhal.sf.model;

import org.bircks.entierprise.model.ModelStorage;
import org.bricks.engine.Engine;
import org.bricks.engine.item.Motorable;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class ShaderWaver implements RenderableProvider, Motorable{
	
	private static final float PI2 = (float) (Math.PI * 2);
	
	private ModelInstance waveInstance;
	private float x = 0, y = 0, z = 0;
	private WaveData waveData;
	private float radSpeed, add;
	private long lastCheckTime = System.currentTimeMillis();
	
	private float translateX, translateY;
	private int rowsCount, colsCount;
	
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
		
		rowsCount = Engine.preferences.getInt("world.rows.count", 1);
		colsCount = Engine.preferences.getInt("world.cols.count", 1);
	}

	public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
		
//		waveInstance.getRenderables(renderables, pool);
		waveInstance.transform.setToTranslation(x, y, z);
		synchronized(this){
			for(int i = 0; i < colsCount; i++){
				for(int j = 0; j < rowsCount; j++){
					waveInstance.transform.setToTranslation(j * translateX, i * translateY, z);
					waveInstance.getRenderables(renderables, pool);
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
		add = radSpeed * diffTime;
		synchronized(waveData){
			waveData.start += add;
			if(waveData.start > PI2){
				waveData.start -= PI2;
			}
		}
		lastCheckTime += diffTime;
	}
	
	public class WaveData{
		
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
/*		
		public Color color(){
			return this.color;
		}*/
	}
/*
	public Renderable showRenderable(){
		Renderable result = new Renderable();
		Node node = waveInstance.nodes.get(0);
		NodePart nodePart = node.parts.get(0);
		return waveInstance.getRenderable(result, node, nodePart);
	}
	*/
}
