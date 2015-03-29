package com.odmyhal.sf.model;

import java.util.Map;

import org.bircks.entierprise.model.ModelStorage;
import org.bricks.engine.event.check.CheckerType;
import org.bricks.engine.event.check.ChunkEventChecker;
import org.bricks.engine.event.overlap.OverlapStrategy;
import org.bricks.engine.event.processor.GetOutProcessor;
import org.bricks.engine.item.MultiLiver;
import org.bricks.engine.neve.EntityPrint;
import org.bricks.engine.tool.Origin;
import org.bricks.exception.Validate;
import org.bricks.extent.engine.checker.NodeScaleProcessor;
import org.bricks.extent.space.Origin3D;
import org.bricks.extent.space.SSPrint;
import org.bricks.extent.space.SpaceSubject;
import org.bricks.extent.space.SpaceSubjectOperable;
import org.bricks.extent.subject.model.ModelBrickOperable;
import org.bricks.extent.subject.model.NodeOperator;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class WaterBall extends MultiLiver<SpaceSubjectOperable, EntityPrint, Vector3> implements RenderableProvider{
	
	private SpaceSubjectOperable<WaterBall, SSPrint, ModelBrickOperable> subject;
	public static final String modelNodeName = "water-ball_10";
	public static final CheckerType WATER_BALL_CH_TYPE = CheckerType.registerCheckerType();
	
	public WaterBall(){
		ModelInstance waterBall = ModelStorage.instance().getModelInstance(modelNodeName);
		subject = new SpaceSubjectOperable(waterBall, new String[]{modelNodeName}, new Vector3());
		NodeOperator nodeOperator = subject.modelBrick.getNodeOperator(modelNodeName);
		Validate.isFalse(nodeOperator == null, "NodeOperatior has not been initialized :-(");
		this.addSubject(subject);

		NodeScaleProcessor NSProcessor1 = new NodeScaleProcessor(this, WaterBall.modelNodeName);
		NSProcessor1.init(13f, 13f, 50f, 500L);
		
		NodeScaleProcessor NSProcessor2 = new NodeScaleProcessor(this, WaterBall.modelNodeName);
		NSProcessor2.init(13f, 13f, 0.1f, 1200L);
		
		ChunkEventChecker<WaterBall> chck = new ChunkEventChecker<WaterBall>(WaterBall.WATER_BALL_CH_TYPE, NSProcessor1, NSProcessor2, GetOutProcessor.instance());
		
		this.registerEventChecker(chck);
	}

	public String sourceType() {
		return null;
	}

	public Map<String, OverlapStrategy> initOverlapStrategy() {
		return null;
	}

	@Override
	public Origin<Vector3> provideInitialOrigin() {
		return new Origin3D();
	}

	public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
		for(SpaceSubjectOperable subject: getStaff()){
			subject.getRenderables(renderables, pool);
		}
	}

}
