package com.odmyhal.sf.model.construct;

import java.util.ArrayList;
import java.util.List;
import org.bircks.entierprise.model.ModelConstructTool;
import org.bircks.entierprise.model.ModelConstructor;
import org.bricks.core.entity.Ipoint;
import org.bricks.extent.tool.SkeletonConstructor;
import org.bricks.extent.tool.SkeletonDataStore;
import org.bricks.annotation.ConstructModel;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

@ConstructModel({"island_1"})
public class IslandConstructor extends SkeletonConstructor{
	
	private static final IslandConstructor instance = new IslandConstructor();
//	private static final HashMap<String, Collection<Ipoint>> idata = new HashMap<String, Collection<Ipoint>>();

	private IslandConstructor(){};
	
	public static ModelConstructor instance(){
		return instance;
	}
	
	public void construct(ModelConstructTool arg0, String... arg1) {
		constructOne(arg0, arg1[0]);
	}
/*	
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
*/
	private void constructOne(ModelConstructTool modelBuilder, String islandName){
		modelBuilder.node(islandName);
		
//		Ipoint translate = new Ipoint(-200, -200);
		Ipoint translate = new Ipoint(0, 0);
		
		List<Vector3> vertexes = new ArrayList<Vector3>();
		vertexes.add(new Vector3(500f, 0f, -150f));
		vertexes.add(new Vector3(2500f, 500f, -150f));
		vertexes.add(new Vector3(4000f, 2000f, -150f));
		vertexes.add(new Vector3(4000f, 3000f, -150f));
		vertexes.add(new Vector3(3500f, 3500f, -150f));
		vertexes.add(new Vector3(3000f, 3500f, -150f));
		vertexes.add(new Vector3(1500f, 3000f, -150f));
		vertexes.add(new Vector3(500f, 2500f, -150f));
		vertexes.add(new Vector3(0f, 1500f, -150f));
		
		
		
		vertexes.add(new Vector3(1000f, 1000f, 500f));
		vertexes.add(new Vector3(2000f, 1000f, 600f));
		vertexes.add(new Vector3(2500f, 1500f, 650f));
		vertexes.add(new Vector3(3000f, 2500f, 850f));
		vertexes.add(new Vector3(2000f, 2000f, 700f));
		for(Vector3 v3 : vertexes){
			v3.add(-2250f, -1750f, 0);
		}
		//Adding central point of island (14):
		vertexes.add(new Vector3(-254f, 98f, 0f));
//		applyVertexData(islandName, vertexes.subList(0, 9), translate);
		Color color = new Color((float)241 / 256, (float)177 / 256, (float)106 / 256, 1f);
		
//		BlendingAttribute ba = new BlendingAttribute();
//		ba.opacity = 0.99f;
		MeshPartBuilder meshBuilder = modelBuilder.part("tower", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, new Material(ColorAttribute.createDiffuse(color)/*, ba*/));

		Matrix4 m4 = new Matrix4();
		m4.translate(translate.getFX(), translate.getFY(), 0f);
		meshBuilder.setVertexTransform(m4);
		
		List<Integer> indexData = new ArrayList<Integer>();
		applyRect(meshBuilder, vertexes, indexData, 0, 9, 1, 10);
		applyRect(meshBuilder, vertexes, indexData, 1, 10, 2, 11);
		applyTriangleNC(meshBuilder, vertexes, indexData, 2, 12, 11);
		applyTriangleNC(meshBuilder, vertexes, indexData, 2, 3, 12);
		applyTriangleNC(meshBuilder, vertexes, indexData, 3, 4, 12);
		applyTriangleNC(meshBuilder, vertexes, indexData, 4, 5, 12);
		applyRect(meshBuilder, vertexes, indexData, 5, 12, 6, 13);
		applyTriangleNC(meshBuilder, vertexes, indexData, 6, 7, 13);
		applyRect(meshBuilder, vertexes, indexData, 7, 13, 8, 9);
		applyTriangleNC(meshBuilder, vertexes, indexData, 8, 0, 9);
		applyRect(meshBuilder, vertexes, indexData, 9, 13, 10, 11);
		applyTriangleNC(meshBuilder, vertexes, indexData, 11, 12, 13);
/*		
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
	*/	
		
		for(Vector3 v: vertexes){
			v.mul(m4);
		}
		int[] intData = new int[indexData.size()];
		int[] planeData = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
		for(int i = 0; i < intData.length; i++){
			intData[i] = indexData.get(i);
		}
//		int[] intData = ArrayUtils.toPrimitive(indexData.toArray(new Integer[indexData.size()]));
		Vector3[] vertexData = vertexes.toArray(new Vector3[vertexes.size()]);
//		SkeletonDataStore.registerSkeletonData(islandName, vertexData, intData);
		SkeletonDataStore.registerPlaneSkeletonData(islandName, vertexData, intData, planeData, 14);
		
		constructDebug(modelBuilder, islandName + ".DEBUG", vertexData, intData);
	}


}
