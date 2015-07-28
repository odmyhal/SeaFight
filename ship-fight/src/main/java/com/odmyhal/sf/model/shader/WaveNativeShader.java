package com.odmyhal.sf.model.shader;

import org.bricks.engine.Engine;
import org.bricks.exception.Validate;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader.Setter;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.odmyhal.sf.model.ShaderWaver;
import com.odmyhal.sf.model.WaveSystem;
import com.odmyhal.sf.model.bubble.BlabKeeper;

public class WaveNativeShader extends DefaultShader{
	
	private int bubblesLoc, bubblesCountLoc, bubbleSize;
	private int bubble0ind, bubble1ind, bubblesCountInd;
	
	private int wavesLoc, waveSize;
	private int wave0ind, wave1ind;
	
	private ShaderWaver.WaveData waveData;
	
	private final Setter waveUniformSetter = new BaseShader.Setter() {
		
		public boolean isGlobal (BaseShader shader, int inputID) {
//			System.out.println("shader setter check if global...");
			return true;
		}

		public void set (BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {

			ShaderWaver.WaveDataPrint waveDataPrint = waveData.getSafePrint();
			
			shader.set(inputID, waveDataPrint.wavesCount);
			for(int i=0; i<waveDataPrint.wavesCount; i++){
				shader.program.setUniformf(wavesLoc + waveSize * i, waveDataPrint.waves[i]);
			}
			
			shader.program.setUniformi(bubblesCountLoc, waveDataPrint.blabSize);
			for(int i=0; i<waveDataPrint.blabSize; i++){
				shader.program.setUniformf(bubblesLoc + bubbleSize * i,
//						i * 5000f + 2500f, 2500f, 
						waveDataPrint.bubbles[i].x, waveDataPrint.bubbles[i].y,
						waveDataPrint.bubbles[i].amplitude, waveDataPrint.bubbles[i].radius);
			}
			waveDataPrint.free();
		}
	};
	
	private static DefaultShader.Config native_config;
	static {
		String vert = Gdx.files.internal("shaders/wave.vertex.glsl").readString();
		int length = Engine.preferences.getInt("waver.net.length", 50);
		float lenX = Engine.preferences.getFloat("waver.net.step.x", 4f) * length;
		float lenY = Engine.preferences.getFloat("waver.net.step.y", 4f) * length;
		float waveCount = Engine.preferences.getFloat("waver.net.wave.count", 1f);
		float amplitude = Engine.preferences.getFloat("waver.net.amplitude", 4);
//		System.out.println("Amplitude is : " + amplitude);
		int maxWaveCount = WaveSystem.prefs.getInt("wave.count.max", 10);
		System.out.println("WaveNativeshader set maxX = " + lenX + ", maxY = " + lenY );
		vert = String.format(vert, BlabKeeper.SHADER_BLAB_COUNT_TOTAL, maxWaveCount, lenX, lenY);
        String frag = Gdx.files.internal("shaders/native.fragment.glsl").readString();
        native_config = new DefaultShader.Config(vert, frag);
	}
	

	public WaveNativeShader(Renderable renderable) {
		super(renderable, native_config, /*"#version 330\n" + */DefaultShader.createPrefix(renderable, native_config));
//		String vert = super.program.getVertexShaderSource();
//		System.out.println("--------------Vertex fragment---------------------------");
//		System.out.println(vert);
//		System.out.println("--------------------------------------------------------");
		
		this.waveData = (ShaderWaver.WaveData) renderable.userData;
		Validate.isFalse(waveData == null);
		System.out.println("Created new WaveNativeshadder");
		register(new Uniform("u_count_waves"), waveUniformSetter);
		register(new Uniform("u_waves"));
		wave0ind = register(new Uniform("u_waves[0]"));
		wave1ind = register(new Uniform("u_waves[1]"));
		
		register(new Uniform("u_bubbles"));
		bubble0ind = register(new Uniform("u_bubbles[0]"));
		bubble1ind = register(new Uniform("u_bubbles[1]"));
		bubblesCountInd = register(new Uniform("u_count_bubbles"));
		System.out.println("Wave naticel shader created...");
	}
	
	@Override
	public void init () {
		super.init();
		bubblesLoc = loc(bubble0ind);
		bubbleSize = loc(bubble1ind) - bubblesLoc;
		bubblesCountLoc = loc(bubblesCountInd);
		
		wavesLoc = loc(wave0ind);
		waveSize = loc(wave1ind) - wavesLoc;
//		wavesCountLoc = loc(wavesCountInd);
	}

	public boolean canRender(Renderable instance) {
		return (instance.userData != null) && (instance.userData instanceof ShaderWaver.WaveData);
	}
	
	public void render (Renderable renderable, final Attributes combinedAttributes) {
		int meshPartSize = renderable.meshPartSize;
		renderable.meshPartSize = 0;
		super.render(renderable, combinedAttributes);
//		Gdx.gl30.glDrawElements(renderable.primitiveType, meshPartSize, GL30.GL_UNSIGNED_SHORT, renderable.meshPartOffset * 2);
		Gdx.gl20.glDrawElements(renderable.primitiveType, meshPartSize, GL20.GL_UNSIGNED_SHORT, renderable.meshPartOffset * 2);
//		Gdx.gl30.glDrawElementsInstanced(renderable.primitiveType, meshPartSize, GL20.GL_UNSIGNED_SHORT, renderable.meshPartOffset * 2, 10);
//		renderable.mesh.render(program, renderable.primitiveType, renderable.meshPartOffset, renderable.meshPartSize, false);
	}
/*	
	@Override
	public void begin (final Camera camera, final RenderContext context) {
		super.begin(camera, context);
		context.setBlending(true, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
	}*/
}
