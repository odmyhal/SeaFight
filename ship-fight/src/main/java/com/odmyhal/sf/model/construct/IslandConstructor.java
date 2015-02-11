package com.odmyhal.sf.model.construct;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.bircks.entierprise.model.ModelConstructTool;
import org.bircks.entierprise.model.ModelConstructor;
import org.bricks.annotation.ConstructModel;
import org.bricks.core.entity.Ipoint;
import org.bricks.enterprise.d3.help.ModelConstructHelper;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

@ConstructModel({"island_1"})
public class IslandConstructor implements ModelConstructor{
	
	private static final IslandConstructor instance = new IslandConstructor();
	private static final HashMap<String, Collection<Ipoint>> idata = new HashMap<String, Collection<Ipoint>>();

	private IslandConstructor(){};
	
	public static ModelConstructor instance(){
		return instance;
	}
	
	public void construct(ModelConstructTool arg0, String... arg1) {
		constructOne(arg0, arg1[0]);
	}
	
	private void applyVertexData(String dataName, Collection<Vector3> data){
		applyVertexData(dataName, data, new Ipoint(0, 0));
	}
	
	private void applyVertexData(String dataName, Collection<Vector3> data, Ipoint transform){
		Collection<Ipoint> tdata = new LinkedList<Ipoint>();
		for(Vector3 v : data){
			tdata.add(new Ipoint((int)v.x + transform.getX(), (int)v.y + transform.getY()));
		}
		idata.put(dataName, tdata);
	}
	
	public static Collection<Ipoint> getVertexData(String dataName){
		return idata.get(dataName);
	}
	
	private void constructOne(ModelConstructTool modelBuilder, String islandName){
		modelBuilder.node(islandName);
		
//		Ipoint translate = new Ipoint(-200, -200);
		Ipoint translate = new Ipoint(0, 0);
		
		List<Vector3> vertexes = new ArrayList<Vector3>();
		vertexes.add(new Vector3(50f, 0f, -15f));
		vertexes.add(new Vector3(250f, 50f, -15f));
		vertexes.add(new Vector3(400f, 200f, -15f));
		vertexes.add(new Vector3(400f, 300f, -15f));
//		vertexes.add(new Vector3(350f, 400f, -15f));
		vertexes.add(new Vector3(350f, 350f, -15f));
		vertexes.add(new Vector3(300f, 350f, -15f));
		vertexes.add(new Vector3(150f, 300f, -15f));
		vertexes.add(new Vector3(50f, 250f, -15f));
		vertexes.add(new Vector3(0f, 150f, -15f));
		
		
		
		vertexes.add(new Vector3(100f, 100f, 50f));
		vertexes.add(new Vector3(200f, 100f, 60f));
		vertexes.add(new Vector3(250f, 150f, 65f));
		vertexes.add(new Vector3(300f, 250f, 85f));
		vertexes.add(new Vector3(200f, 200f, 70f));
		for(Vector3 v3 : vertexes){
			v3.add(-225f, -175f, 0);
		}
		applyVertexData(islandName, vertexes.subList(0, 9), translate);
		Color color = new Color((float)241 / 256, (float)177 / 256, (float)106 / 256, 1f);
		
		MeshPartBuilder meshBuilder = modelBuilder.part("tower", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, new Material(ColorAttribute.createDiffuse(color)));

		Matrix4 m4 = new Matrix4();
		m4.translate(translate.getFX(), translate.getFY(), 0f);
		meshBuilder.setVertexTransform(m4);
		
		ModelConstructHelper.applyRect(vertexes.get(0), vertexes.get(9), vertexes.get(1), vertexes.get(10), meshBuilder);
		ModelConstructHelper.applyRect(vertexes.get(1), vertexes.get(10), vertexes.get(2), vertexes.get(11), meshBuilder);
		ModelConstructHelper.applyTriangleNC(vertexes.get(2), vertexes.get(12), vertexes.get(11), meshBuilder);
		ModelConstructHelper.applyTriangleNC(vertexes.get(2), vertexes.get(3), vertexes.get(12), meshBuilder);
		ModelConstructHelper.applyTriangleNC(vertexes.get(3), vertexes.get(4), vertexes.get(12), meshBuilder);
		ModelConstructHelper.applyTriangleNC(vertexes.get(4), vertexes.get(5), vertexes.get(12), meshBuilder);
		ModelConstructHelper.applyRect(vertexes.get(5), vertexes.get(12), vertexes.get(6), vertexes.get(13), meshBuilder);
		ModelConstructHelper.applyTriangleNC(vertexes.get(6), vertexes.get(7), vertexes.get(13), meshBuilder);
		ModelConstructHelper.applyRect(vertexes.get(7), vertexes.get(13), vertexes.get(8), vertexes.get(9), meshBuilder);
		ModelConstructHelper.applyTriangleNC(vertexes.get(8), vertexes.get(0), vertexes.get(9), meshBuilder);
		
		ModelConstructHelper.applyRect(vertexes.get(9), vertexes.get(13), vertexes.get(10), vertexes.get(11), meshBuilder);
		ModelConstructHelper.applyTriangleNC(vertexes.get(11), vertexes.get(12), vertexes.get(13), meshBuilder);
	}

}
