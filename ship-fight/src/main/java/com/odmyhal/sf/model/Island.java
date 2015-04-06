package com.odmyhal.sf.model;

import org.bircks.entierprise.model.ModelStorage;
import org.bricks.core.entity.impl.PointSetBrick;
import org.bricks.core.entity.type.Brick;
import org.bricks.engine.item.Stone;
import org.bricks.engine.neve.EntityPrint;
import org.bricks.exception.Validate;
import org.bricks.extent.debug.SkeletonDebug;
import org.bricks.extent.debug.SpaceDebug;
import org.bricks.extent.entity.mesh.ModelSubject;
import org.bricks.extent.entity.mesh.ModelSubjectPrint;
import org.bricks.extent.subject.model.ModelBrick;
import org.bricks.extent.tool.SkeletonDataStore;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.odmyhal.sf.model.construct.IslandConstructor;

public class Island extends Stone<ModelSubject, EntityPrint> implements RenderableProvider, SpaceDebug{
	
	public static final String ISLAND_SF_SOURCE = "IslandSource@sf.odmyhal.com";
	private SkeletonDebug skeletonDebug;

	private Island(ModelSubject<Island, ModelSubjectPrint, ModelBrick> s, String name) {
		super(s);
		ModelBrick mb = s.linkModelBrick();
//		String debugModelKey = name + ".DEBUG";
		mb.initSkeleton(SkeletonDataStore.getVertexes(name), SkeletonDataStore.getIndexes(name));
		ModelInstance debugModel = ModelStorage.instance().getModelInstance(name + ".DEBUG");
		Validate.isFalse(debugModel == null, "Could not found debug model by name '" + name + ".DEBUG'...");
		skeletonDebug = new SkeletonDebug(debugModel, mb);
	}
	
	public static Island instance(String name){
		Brick brick = new PointSetBrick(IslandConstructor.getVertexData(name));
		ModelInstance shield = ModelStorage.instance().getModelInstance(name);
		ModelSubject<Island, ModelSubjectPrint, ModelBrick> ms = new ModelSubject<Island, ModelSubjectPrint, ModelBrick>(brick, shield);

		

		return new Island(ms, name);
	}

	public String sourceType() {
		return ISLAND_SF_SOURCE;
	}

	public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
		for(ModelSubject subject: getStaff()){
			subject.getRenderables(renderables, pool);
		}
	}

	public RenderableProvider debugModel() {
		return skeletonDebug;
	}

}
