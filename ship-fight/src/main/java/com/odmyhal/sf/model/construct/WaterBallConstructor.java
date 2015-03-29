package com.odmyhal.sf.model.construct;

import org.bircks.entierprise.model.ModelConstructTool;
import org.bircks.entierprise.model.ModelConstructor;
import org.bricks.annotation.ConstructModel;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.odmyhal.sf.model.WaterBall;

@ConstructModel({WaterBall.modelNodeName})
public class WaterBallConstructor implements ModelConstructor{
	
	private static WaterBallConstructor instance = new WaterBallConstructor();
	
	public static final WaterBallConstructor instance(){
		return instance;
	}
	
	private WaterBallConstructor(){}

	public void construct(ModelConstructTool modelBuilder, String... partName) {
		for(String pName: partName){
			modelBuilder.node(pName);
			produceBall(modelBuilder, pName);
		}
	}
	
	private void produceBall(ModelBuilder modelBuilder, String name){
		String[] pName = name.split("_");
		Color ballColor = new Color(0.357f, 0.765f, 0.863f, 0.3f);
		ballColor.mul(1.13f);
		ballColor.a = 0.2f;
//		Color ballColor = Color.WHITE;
		MeshPartBuilder meshBuilder = modelBuilder.part(pName[0], GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, new Material(ColorAttribute.createDiffuse(ballColor)));
		float d = Float.parseFloat(pName[1]);
//		meshBuilder.sphere(width, height, depth, divisionsU, divisionsV, angleUFrom, angleUTo, angleVFrom, angleVTo)
//		meshBuilder.sphere(d, d, d, 5, 5, 0, 180, 0, 180);
		meshBuilder.sphere(d, d, d, 20, 20, 0, 180, 0, 180);
	}

}
