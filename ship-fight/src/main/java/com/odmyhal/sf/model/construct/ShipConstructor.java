package com.odmyhal.sf.model.construct;

import java.util.ArrayList;
import java.util.Collection;
import org.bircks.entierprise.model.ModelConstructTool;
import org.bircks.entierprise.model.ModelConstructor;
import org.bricks.core.entity.Ipoint;
import org.bricks.core.entity.Point;
import org.bricks.core.entity.Tuple;
import org.bricks.exception.Validate;
import org.bricks.extent.tool.ModelHelper;
import org.bricks.extent.tool.SkeletonConstructor;
import org.bricks.extent.tool.SkeletonDataStore;
import org.bricks.extent.tool.SkeletonHelper;
import org.bricks.annotation.ConstructModel;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

@ConstructModel({"models/ship11.g3db"})
public class ShipConstructor extends SkeletonConstructor {
	
	private static ModelInstance shipModelInstance;
	private static final ShipConstructor instance = new ShipConstructor();
	
	private ShipConstructor(){};
	
	public static ModelConstructor instance(){
		return instance;
	}
	
	public static void setModel(ModelInstance modelInstance){
		shipModelInstance = modelInstance;
	}

	public void construct(ModelConstructTool modelBuilder, String... arg1) {
		Validate.isFalse(shipModelInstance == null, "You should set shipModel instanse into ship Constructor first");
		String nodePath = "Dummyship1/ship1";
		Tuple tpl = SkeletonHelper.fetchSkeletonDataFromModel(shipModelInstance, nodePath);
		int[] intData = (int[]) tpl.getFirst();
		Vector3[] vertexData = (Vector3[]) tpl.getSecond();
		
		Node node = ModelHelper.findNode("Dummyship1", shipModelInstance.nodes);
		Quaternion q = new Quaternion();
		q.setFromAxis(1000f, 0f, 0f, 90f);
		Matrix4 rMatrix = new Matrix4();
		q.toMatrix(rMatrix.val);
		node.translation.add(0f, 30f, -68f);
		node.translation.mul(rMatrix);
		node.rotation.mulLeft(q);
		node.calculateTransforms(true);
		
		Node node2 = ModelHelper.findNode(nodePath, shipModelInstance.nodes);
		Matrix4 transMatrix = new Matrix4();
		transMatrix.set(node2.globalTransform);
		
		
		Collection<Point> rawNativePoints = nativePoints();
		int[] planeIndexData = new int[rawNativePoints.size()];
		int planeIndex = 0;
		Vector3 hVector = new Vector3();
		for(Point p : rawNativePoints){
			float sqrLen = Float.MAX_VALUE;
			int curPlane = -1;
			for(int i = 0; i < vertexData.length; i++){
				hVector.set(vertexData[i]);
				hVector.mul(transMatrix);
				float qlen = quad(p.getFX() - hVector.x) + quad(p.getFY() - hVector.y);
				if(sqrLen > qlen){
					sqrLen = qlen;
					curPlane = i;
				}
			}
//			System.out.println("Closest point to " + p + " is " + vertexData[curPlane]);
			Validate.isTrue(curPlane > -1);
			planeIndexData[planeIndex++] = curPlane;
		}
		
		String dataName = "SHIP.DEBUG";
		Vector3[] withCenterVertexData = new Vector3[vertexData.length + 1];
		System.arraycopy(vertexData, 0, withCenterVertexData, 0, vertexData.length);
		int centralIndex = withCenterVertexData.length - 1;
		Vector3 centralPoint = new Vector3(-108f, 0f, 0f); 
		withCenterVertexData[centralIndex] = centralPoint;
		centralPoint.mul(transMatrix.inv());
		System.out.println("Found central point for ship: " + centralPoint);
		SkeletonDataStore.registerPlaneSkeletonData(dataName, withCenterVertexData, intData, planeIndexData, centralIndex);
//		SkeletonDataStore.registerSkeletonData(dataName, vertexData, intData);
		constructDebug(modelBuilder, dataName, vertexData, intData);
	}
	
	private float quad(float a){
		return a * a;
	}

	private Collection<Point> nativePoints(){
		Collection<Point> points = new ArrayList<Point>();
/*		points.add(new Ipoint(844, 0));
		points.add(new Ipoint(665, 40));
		points.add(new Ipoint(531, 63));
		points.add(new Ipoint(397, 84));
		points.add(new Ipoint(129, 115));
		points.add(new Ipoint(-3, 122));
		points.add(new Ipoint(-135, 127));
		points.add(new Ipoint(-273, 123));
		points.add(new Ipoint(-417, 117));
		points.add(new Ipoint(-554, 110));
		points.add(new Ipoint(-713, 83));
		points.add(new Ipoint(-809, 49));
		
		points.add(new Ipoint(-857, 0));
		
		points.add(new Ipoint(-809, -49));
		points.add(new Ipoint(-714, -79));
		points.add(new Ipoint(-555, -106));
		points.add(new Ipoint(-418, -115));
		points.add(new Ipoint(-272, -123));
		points.add(new Ipoint(-135, -127));
		points.add(new Ipoint(-3, -122));
		points.add(new Ipoint(129, -115));
		points.add(new Ipoint(263, -101));
		points.add(new Ipoint(397, -84));
		points.add(new Ipoint(531, -63));
		points.add(new Ipoint(665, -40));*/
		
		points.add(new Ipoint(844, 0));
//		points.add(new Ipoint(665, 40));
		points.add(new Ipoint(531, 63));
//		points.add(new Ipoint(397, 84));
//		points.add(new Ipoint(263, 96));
		points.add(new Ipoint(129, 114));
		points.add(new Ipoint(-3, 122));
		points.add(new Ipoint(-135, 127));
//		points.add(new Ipoint(-273, 117));
//		points.add(new Ipoint(-417, 117));
		points.add(new Ipoint(-553, 110));
		points.add(new Ipoint(-712, 83));
		points.add(new Ipoint(-809, 49));
		
		points.add(new Ipoint(-857, 0));
		
		points.add(new Ipoint(-809, -49));
		points.add(new Ipoint(-714, -79));
		points.add(new Ipoint(-555, -106));
//		points.add(new Ipoint(-418, -115));
//		points.add(new Ipoint(-272, -123));
		points.add(new Ipoint(-135, -127));
		points.add(new Ipoint(-3, -122));
		points.add(new Ipoint(129, -115));
//		points.add(new Ipoint(263, -101));
//		points.add(new Ipoint(397, -84));
		points.add(new Ipoint(531, -63));
//		points.add(new Ipoint(665, -40));
		
		
//		ConvexityApproveHelper.applyConvexity(points);
		return points;
	}
}
