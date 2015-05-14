package com.odmyhal.sf.model;

import org.bircks.entierprise.model.ModelStorage;
import org.bricks.core.entity.Fpoint;
import org.bricks.core.entity.impl.PointSetBrick;
import org.bricks.core.entity.type.Brick;
import org.bricks.engine.item.Stone;
import org.bricks.engine.neve.EntityPrint;
import org.bricks.engine.tool.Origin;
import org.bricks.engine.tool.Origin2D;
import org.bricks.engine.tool.Roll;
import org.bricks.exception.Validate;
import org.bricks.extent.debug.SkeletonDebug;
import org.bricks.extent.debug.SpaceDebug;
import org.bricks.extent.entity.mesh.ModelSubject;
import org.bricks.extent.entity.mesh.ModelSubjectPrint;
import org.bricks.extent.space.Origin3D;
import org.bricks.extent.space.SSPlanePrint;
import org.bricks.extent.space.SpaceSubject;
import org.bricks.extent.space.overlap.SkeletonPlanePrint;
import org.bricks.extent.space.overlap.SkeletonWithPlane;
import org.bricks.extent.subject.model.MBSVehicle;
import org.bricks.extent.subject.model.ModelBrick;
import org.bricks.extent.tool.SkeletonDataStore;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.odmyhal.sf.model.construct.IslandConstructor;

public class Island extends Stone<SpaceSubject<?, SSPlanePrint, Fpoint, Roll, ?>, EntityPrint, Fpoint> implements RenderableProvider, SpaceDebug{
	
	public static final String ISLAND_SF_SOURCE = "IslandSource@sf.odmyhal.com";
	private SkeletonDebug skeletonDebug;

	private Island(SpaceSubject<Island, SSPlanePrint, Fpoint, Roll, ModelBrick> s, String name) {
		super(s);
		ModelBrick mb = s.linkModelBrick();
//		String debugModelKey = name + ".DEBUG";
		SkeletonWithPlane swp = new SkeletonWithPlane(SkeletonDataStore.getIndexes(name), SkeletonDataStore.getVertexes(name), 
				SkeletonDataStore.getPlaneIndexes(name), SkeletonDataStore.getPlaneCenterIndex(name));
//		mb.initSkeleton(SkeletonDataStore.getVertexes(name), SkeletonDataStore.getIndexes(name));
		mb.applySkeletonWithPlane(swp);
		ModelInstance debugModel = ModelStorage.instance().getModelInstance(name + ".DEBUG");
		Validate.isFalse(debugModel == null, "Could not found debug model by name '" + name + ".DEBUG'...");
		skeletonDebug = new SkeletonDebug(debugModel, mb);
	}
	
	public static Island instance(String name){
//		Brick brick = new PointSetBrick(IslandConstructor.getVertexData(name));
//		System.out.println("Island brick center is: " + brick.getCenter());
		ModelInstance shield = ModelStorage.instance().getModelInstance(name);
//		ModelSubject<Island, ModelSubjectPrint, ModelBrick> ms = new ModelSubject<Island, ModelSubjectPrint, ModelBrick>(brick, shield);
		SpaceSubject<Island, SSPlanePrint, Fpoint, Roll, ModelBrick> ss = 
				new SpaceSubject<Island, SSPlanePrint, Fpoint, Roll, ModelBrick>(new MBSVehicle.Plane(), shield, new Vector3(-254f, 98f, 0f));
		ss.setPrintFactory(SSPlanePrint.printFactory);

		return new Island(ss, name);
	}

	public String sourceType() {
		return ISLAND_SF_SOURCE;
	}

	public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
		for(SpaceSubject subject: getStaff()){
			subject.getRenderables(renderables, pool);
		}
	}

	public RenderableProvider debugModel() {
		return skeletonDebug;
	}

	@Override
	public Origin<Fpoint> provideInitialOrigin() {
		return new Origin2D();
	}

}
