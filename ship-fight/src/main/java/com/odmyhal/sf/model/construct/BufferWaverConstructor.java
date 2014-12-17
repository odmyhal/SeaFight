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
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.Vector3;

@ConstructModel({"water"})
public class BufferWaverConstructor implements ModelConstructor{
	
	private static BufferWaverConstructor constructor = new BufferWaverConstructor();
	
	public static ModelConstructor instance(){
		return constructor;
	}
	private BufferWaverConstructor(){}
	
	public void construct(ModelConstructTool modelBuilder, String... name){

		int length = Engine.preferences.getInt("waver.net.length", 50);
		float stepX = Engine.preferences.getFloat("waver.net.step.x", 4);
		float stepY = Engine.preferences.getFloat("waver.net.step.y", 4);
		int amplitude = Engine.preferences.getInt("waver.net.amplitude", 3);
		
		List<Vector3> vertexes = new ArrayList<Vector3>();
		for(int x=0; x<=length; x++){
			float sin = (float) Math.sin(Math.PI * 2 * x / length) * amplitude;
			vertexes.add(new Vector3(x * stepX, 0, sin));
		}
		
		for(int y=1; y<=length; y++){
			for(int x=0; x<=length; x++){
				int curNum = (length + 1) * y + x;
				Vector3 mold = vertexes.get(curNum - length);
				vertexes.add(new Vector3(x * stepX, y * stepY, mold.z));
			}
		}

		modelBuilder.node(name[0]);
		Color color = new Color(0.357f, 0.765f, 0.863f, 1f);
		MeshPartBuilder meshBuilder = modelBuilder.part("water", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, new Material(ColorAttribute.createDiffuse(color)));
		
		for(int y=0; y<length; y++){
			for(int x=0; x<length; x++){
				Vector3 v1 = vertexes.get(y * (length + 1) + x);
				Vector3 v2 = vertexes.get((y + 1) * (length + 1) + x);
				Vector3 v3 = vertexes.get(y * (length + 1) + x + 1);
				Vector3 v4 = vertexes.get((y + 1) * (length + 1) + x + 1);
				ModelConstructHelper.apply2TrianglesNormal(v1, v2, v3, v4, meshBuilder);
			}
		}
	}

}
