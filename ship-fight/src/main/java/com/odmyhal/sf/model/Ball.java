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
import org.bricks.extent.space.Origin3D;
import org.bricks.extent.space.Roll3D;
import org.bricks.extent.space.SSPrint;
import org.bricks.extent.space.SpaceSubject;
import org.bricks.extent.space.SpaceSubjectOperable;
import org.bricks.extent.subject.model.MBSVehicle;
import org.bricks.extent.subject.model.ModelBrickOperable;
import org.bricks.extent.subject.model.NodeOperator;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class Ball extends MultiLiver<SpaceSubjectOperable, EntityPrint, Vector3> implements RenderableProvider{
	
	private SpaceSubjectOperable<Ball, SSPrint, Vector3, Roll3D, ModelBrickOperable> subject;
	public static final String modelWaterName = "water-ball_10";
	public static final String modelStoneExploit = "stone-exploit_10";
	public static final CheckerType WATER_BALL_CH_TYPE = CheckerType.registerCheckerType();
	
	public Ball(String modelName){
		ModelInstance waterBall = ModelStorage.instance().getModelInstance(modelName);
		subject = new SpaceSubjectOperable(new MBSVehicle.Space(), waterBall, new String[]{modelName}, new Vector3());
		NodeOperator nodeOperator = subject.modelBrick.getNodeOperator(modelName);
		Validate.isFalse(nodeOperator == null, "NodeOperatior has not been initialized :-(");
		this.addSubject(subject);
	}

	public String sourceType() {
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
