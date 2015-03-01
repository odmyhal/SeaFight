package com.odmyhal.sf.model;

import org.bircks.entierprise.model.ModelStorage;
import org.bricks.core.entity.impl.PointSetBrick;
import org.bricks.core.entity.type.Brick;
import org.bricks.engine.item.Stone;
import org.bricks.engine.neve.EntityPrint;
import org.bricks.extent.entity.mesh.ModelSubjectOperable;
import org.bricks.extent.entity.mesh.ModelSubjectPrint;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.odmyhal.sf.model.construct.IslandConstructor;

public class Island extends Stone<ModelSubjectOperable, EntityPrint> implements RenderableProvider{
	
	public static final String ISLAND_SF_SOURCE = "IslandSource@sf.odmyhal.com";

	private Island(ModelSubjectOperable s) {
		super(s);
	}
	
	public static Island instance(String name){
		Brick brick = new PointSetBrick(IslandConstructor.getVertexData(name));
		ModelInstance shield = ModelStorage.instance().getModelInstance(name);
		ModelSubjectOperable<Island, ModelSubjectPrint> ms = new ModelSubjectOperable<Island, ModelSubjectPrint>(brick, shield);
		return new Island(ms);
	}

	public String sourceType() {
		return ISLAND_SF_SOURCE;
	}

	public void getRenderables(Array<Renderable> renderables,
			Pool<Renderable> pool) {
		for(ModelSubjectOperable subject: getStaff()){
			subject.getRenderables(renderables, pool);
		}
	}

}
