package com.odmyhal.sf.model.construct;

import org.bircks.entierprise.model.ModelConstructTool;
import org.bircks.entierprise.model.ModelConstructor;
import org.bricks.annotation.ConstructModel;
import org.bricks.extent.effects.EffectSystem;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.odmyhal.sf.model.Ball;

@ConstructModel({Ball.modelWaterName, Ball.modelStoneExploit/*, EffectSystem.dustModelName*/})
public class WaterBallConstructor implements ModelConstructor{
	
	private static WaterBallConstructor instance = new WaterBallConstructor();
	
	public static final WaterBallConstructor instance(){
		return instance;
	}
	
	private WaterBallConstructor(){}

	public void construct(ModelConstructTool modelBuilder, String... partName) {
		for(String pName: partName){
			modelBuilder.node(pName);
			if(pName.equals(Ball.modelWaterName)){
				produceWaterBall(modelBuilder, pName);
			}else if(pName.equals(Ball.modelStoneExploit)){
				produceExploitBall(modelBuilder, pName);
			}else{
				produceExploitBall(modelBuilder, pName);
			}
		}
	}
	
	private void produceWaterBall(ModelBuilder modelBuilder, String name){
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

	private void produceExploitBall(ModelBuilder modelBuilder, String name){
		String[] pName = name.split("_");
		Color ballColor = Color.DARK_GRAY;
		ballColor.a = 0.5f;
		BlendingAttribute ba = new BlendingAttribute();
		ba.opacity = 0.99f;
		MeshPartBuilder meshBuilder = modelBuilder.part(pName[0], GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, new Material(ColorAttribute.createDiffuse(ballColor), ba));
		float d = Float.parseFloat(pName[1]);
		meshBuilder.sphere(d, d, d, 20, 20/*, 0, 180, 0, 180*/);
	}
}
