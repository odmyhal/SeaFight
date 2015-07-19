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
import com.badlogic.gdx.math.Matrix4;

@ConstructModel({"ship_ammunition"})
public class AmmunitionConstructor implements ModelConstructor{
	
	private static final AmmunitionConstructor instance = new AmmunitionConstructor();
	
	private AmmunitionConstructor(){};
	
	public static ModelConstructor instance(){
		return instance;
	}

	public void construct(ModelConstructTool modelBuilder, String... partName) {
		modelBuilder.node(partName[0]);
		MeshPartBuilder meshBuilder = modelBuilder.part("gilse", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.ORANGE)));
		Matrix4 tmGls = new Matrix4();
		tmGls.setToRotation(0f, 0f, 10f, -90f);
		meshBuilder.setVertexTransform(tmGls);
		meshBuilder.cylinder(7f, 48f, 7f, 10);
		
		meshBuilder = modelBuilder.part("cone", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.ORANGE)));
	    Matrix4 tmCone = new Matrix4();
	    tmCone.setToRotation(0f, 0f, 10f, -90f);
		tmCone.trn(30f, 0f, 0f);;
		meshBuilder.setVertexTransform(tmCone);
		meshBuilder.cone(7, 12, 7, 10);
	}

}
