package com.odmyhal.sf;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class GL30Try extends ApplicationAdapter{
	
	private ShapeRenderer shR;
	private Camera camera;
	private int width, height;
//	private SpriteBatch spriteBatch= new SpriteBatch();
	
	public GL30Try(int width, int height){
		this.width = width;
		this.height = height;
	}
	
	@Override
	public void create(){
		shR = new ShapeRenderer();
		camera = new OrthographicCamera(width, height);
	}
	
	@Override
	public void render(){
		Gdx.gl.glClearColor(0.9f, 0.9f, 1f, 0.5f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		shR.setProjectionMatrix(camera.combined);
		shR.begin(ShapeType.Line);
		shR.setColor(Color.RED);
		for(float r = 10f; r < 500; r += 20f ){
			shR.circle(0f, 0f, r);
		}
		shR.end();
	}

}
