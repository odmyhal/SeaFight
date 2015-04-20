package com.odmyhal.sf.model.construct;

import org.bircks.entierprise.model.ModelConstructTool;
import org.bircks.entierprise.model.ModelConstructor;
import org.bricks.annotation.ConstructModel;
import org.bricks.core.entity.Tuple;
import org.bricks.exception.Validate;
import org.bricks.extent.space.overlap.Skeleton;
import org.bricks.extent.tool.SkeletonConstructor;
import org.bricks.extent.tool.SkeletonDataStore;
import org.bricks.extent.tool.SkeletonHelper;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
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
		Tuple tpl = SkeletonHelper.fetchSkeletonDataFromModel(shipModelInstance, "Dummyship1/ship1");
		int[] intData = (int[]) tpl.getFirst();
		Vector3[] vertexData = (Vector3[]) tpl.getSecond();
		
		
		String dataName = "SHIP.DEBUG";
		SkeletonDataStore.registerSkeletonData(dataName, vertexData, intData);
		constructDebug(modelBuilder, dataName, vertexData, intData);
	}

}
