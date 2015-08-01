package com.odmyhal.sf.model.shader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.odmyhal.sf.staff.Ship;

public class FogShader extends DefaultShader{
	
	private static DefaultShader.Config fog_config;
	static {
		String vert = Gdx.files.internal("shaders/fog.vertex.glsl").readString();
		float cameraFar = Ship.cameraPrefs.getFloat("camera.far", 40000f);
		vert = String.format(vert, cameraFar);
        String frag = Gdx.files.internal("shaders/fog.fragment.glsl").readString();
        fog_config = new DefaultShader.Config(vert, frag);
	}
	
	private final Setter cameraPositionSetter = new BaseShader.Setter() {
		
		public boolean isGlobal (BaseShader shader, int inputID) {
			return true;
		}

		public void set (BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
			shader.set(inputID, camera.position);
		}
	};
	
	private Camera camera;
	
	public FogShader(final Renderable renderable, Camera camera){
		this(renderable, fog_config, /*"#version 330\n" + */DefaultShader.createPrefix(renderable, fog_config), camera);
	}

	protected FogShader(final Renderable renderable, final Config config, final String prefix, Camera camera){
		super(renderable, config, prefix);
		this.camera = camera;
		register(new Uniform("u_camera"), cameraPositionSetter);
	}
/*	
	@Override
	public void init () {
		super.init();
	}*/
}
