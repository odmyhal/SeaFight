package com.odmyhal.sf.model.construct;

import java.util.ArrayList;
import java.util.List;

import org.bircks.entierprise.model.ModelConstructTool;
import org.bircks.entierprise.model.ModelConstructor;
import org.bricks.annotation.ConstructModel;
import org.bricks.engine.Engine;
import org.bricks.enterprise.d3.help.ModelConstructHelper;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.Vector3;

@ConstructModel({"shader-wave"})
public class ShaderWaveConstructor implements ModelConstructor{
	
	private static ShaderWaveConstructor constructor = new ShaderWaveConstructor();
	
	public static ModelConstructor instance(){
		return constructor;
	}
	private ShaderWaveConstructor(){}
	
	public void construct(ModelConstructTool modelBuilder, String... name){

		int length = Engine.preferences.getInt("waver.net.length", 50);
		float stepX = Engine.preferences.getFloat("waver.net.step.x", 4);
		float stepY = Engine.preferences.getFloat("waver.net.step.y", 4);
		
		List<Vector3> vertexes = new ArrayList<Vector3>();
		for(int y=0; y<=length; y++){
			for( int x=0; x<=length; x++){
				vertexes.add(new Vector3(x * stepX, y * stepY, 0));
			}
		}

		modelBuilder.node(name[0]);
		Color colorWater = new Color(0.357f, 0.765f, 0.863f, 0.3f);
//		Color colorWater = Color.BLUE;
		
		BlendingAttribute ba = new BlendingAttribute();
		ba.opacity = 0.99f;
		MeshPartBuilder meshBuilder = modelBuilder.part("shader-wave", GL20.GL_TRIANGLES, Usage.Position, new Material(ColorAttribute.createDiffuse(colorWater), ba));
		
		for(int y=0; y<length; y++){
			for(int x=0; x<length; x++){
				Vector3 v1 = vertexes.get(y * (length + 1) + x);
				Vector3 v2 = vertexes.get((y + 1) * (length + 1) + x);
				Vector3 v3 = vertexes.get(y * (length + 1) + x + 1);
				Vector3 v4 = vertexes.get((y + 1) * (length + 1) + x + 1);
				ModelConstructHelper.apply2Triangles(v1, v2, v3, v4, meshBuilder);
			}
		}
	}

}
