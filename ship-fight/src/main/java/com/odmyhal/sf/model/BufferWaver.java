package com.odmyhal.sf.model;

import java.nio.FloatBuffer;
import java.util.Arrays;

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

public class BufferWaver implements RenderableProvider, Motorable{
	
	private ModelInstance waveInstance = ModelStorage.instance().getModelInstance("water");
	private float x = 0, y = 0, z = 0;
	private long updateWaveTime;
	private long lastCheckTime = System.currentTimeMillis();
	float[] tmpBuffer1 = new float[36], tmpBuffer2 = new float[36];
	private int waveLen;
	float stepX, stepY;
	private int translateX, translateY;
	
	public BufferWaver(){
		waveLen = Engine.preferences.getInt("waver.net.length", 50);
		float waveCycle = Engine.preferences.getFloat("waver.animate.cycle.sectime", 20f) * 1000;
		updateWaveTime = (long) (waveCycle / waveLen);
		stepX = Engine.preferences.getFloat("waver.net.step.x", 4);
		stepY = Engine.preferences.getFloat("waver.net.step.y", 4);
		
		translateX = (int) (stepX * waveLen);
		translateY = (int) (stepY * waveLen);
	}

	public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
		
		waveInstance.getRenderables(renderables, pool);
/*		
		waveInstance.transform.setToTranslation(x, y, z);
		synchronized(this){
			for(int i = 0; i < 2; i++){
				for(int j = 0; j < 2; j++){
					waveInstance.transform.setToTranslation(j * translateX, i * translateY, z);
					waveInstance.getRenderables(renderables, pool);
				}
			}
		}
*/		
		
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
		int step = (int) ((curTime - lastCheckTime) / updateWaveTime);
		while(step > 0){
			synchronized(this){
				FloatBuffer vertexBuffer = waveInstance.nodes.get(0).parts.get(0).meshPart.mesh.getVerticesBuffer();
				
				vertexBuffer.get(tmpBuffer1, 0, 36);
				for(int k = 0; k < 6; k++){
					tmpBuffer1[k * 6] += (waveLen - 1) * stepX;
				}
				vertexBuffer.compact();
				for(int j=0; j<waveLen; j++){
					for(int i=0; i<waveLen-1; i++){
						for(int k=0; k<6; k++){
							int num = ((j * waveLen + i) * 6 + k) * 6;
							vertexBuffer.put(num, vertexBuffer.get(num) - stepX);
						}
					}
				}
				
				for(int j = waveLen; j > 1; j--){
					vertexBuffer.position((( j - 1 ) * waveLen - 1 ) * 36);
					vertexBuffer.get(tmpBuffer2,  0, 36);
					for(int k = 0; k < 6; k++){
						tmpBuffer2[k * 6] += (waveLen - 1) * stepX;
					}
					vertexBuffer.position(( j * waveLen - 1) * 36);
					vertexBuffer.put(tmpBuffer2, 0, 36);
				}
				vertexBuffer.position((waveLen - 1) * 36);
				vertexBuffer.put(tmpBuffer1, 0, 36);
				
				vertexBuffer.rewind();
			}
			step--;
			lastCheckTime += updateWaveTime;
		}
	}
	
	public void showBuffer(){
//		FloatBuffer vertexBuffer = waveInstance.nodes.get(0).parts.get(0).meshPart.mesh.getVerticesBuffer();
		
		System.out.println("Nodes count: " + waveInstance.nodes.size);
		System.out.println("Parts cound: " + waveInstance.nodes.get(0).parts.size);
	}
	
	public Renderable showRenderable(){
		Renderable result = new Renderable();
		Node node = waveInstance.nodes.get(0);
		NodePart nodePart = node.parts.get(0);
		return waveInstance.getRenderable(result, node, nodePart);
	}
	

}
