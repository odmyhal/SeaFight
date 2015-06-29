package com.odmyhal.sf.model;

import java.util.Map;

import org.bircks.entierprise.model.ModelStorage;
import org.bricks.engine.event.check.CheckerType;
import org.bricks.engine.event.check.ChunkEventChecker;
import org.bricks.engine.event.overlap.OverlapStrategy;
import org.bricks.engine.processor.GetOutProcessor;
import org.bricks.engine.processor.SingleActProcessor;
import org.bricks.engine.item.MultiLiver;
import org.bricks.engine.neve.EntityPrint;
import org.bricks.engine.tool.Origin;
import org.bricks.exception.Validate;
import org.bricks.extent.processor.NodeScaleProcessor;
import org.bricks.extent.space.Origin3D;
import org.bricks.extent.space.Roll3D;
import org.bricks.extent.space.SSPrint;
import org.bricks.extent.space.SpaceSubject;
import org.bricks.extent.space.SpaceSubjectOperable;
import org.bricks.extent.subject.model.MBSVehicle;
import org.bricks.extent.subject.model.ModelBrickOperable;
import org.bricks.extent.subject.model.NodeOperator;
import org.bricks.utils.Cache;
import org.bricks.utils.Cache.DataProvider;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.odmyhal.sf.model.bubble.BlabKeeper;
import com.odmyhal.sf.model.bubble.BlabKeeper.Blab;

public class Ball extends MultiLiver<SpaceSubjectOperable, EntityPrint, Vector3> implements RenderableProvider{
	
	protected SpaceSubjectOperable<Ball, SSPrint, Vector3, Roll3D, ModelBrickOperable> subject;
	public static final String modelWaterName = "water-ball_10";
	public static final String modelStoneExploit = "stone-exploit_10";
	public static final CheckerType WATER_BALL_CH_TYPE = CheckerType.registerCheckerType();


	private static BlabKeeper blabKeeper;
	private static final DBProcessor dropBubbleProcessor = new DBProcessor(CheckerType.registerCheckerType());
	
	static{
		Cache.registerCache(Ball.WaterBall.class, new DataProvider<Ball.WaterBall>(){

			@Override
			public WaterBall provideNew() {
				return new Ball.WaterBall();
			}
			
		});
		Cache.registerCache(Ball.DustBall.class, new DataProvider<Ball.DustBall>(){

			@Override
			public DustBall provideNew() {
				return new Ball.DustBall();
			}
			
		});
		Cache.registerCache(Origin3D.class, new DataProvider<Origin3D>(){

			@Override
			public Origin3D provideNew() {
				return new Origin3D();
			}
			
		});
/*		Cache.registerCache(Vector3.class, new DataProvider<Vector3>(){
			@Override
			public Vector3 provideNew() {
				return new Vector3();
			}
		});*/
	}
	
	private Ball(String modelName){
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

	public void disappear(){
		super.disappear();
		Origin3D tmpTrn = Cache.get(Origin3D.class);
		tmpTrn.source.set(this.origin().source).scl(-1f);
		this.translate(tmpTrn, false);
		this.subject.linkModelBrick().reset();
		this.adjustCurrentPrint();
		Cache.put(this);
		Cache.put(tmpTrn);
	}

	public static void setBlabKeeper(BlabKeeper bk){
		blabKeeper = bk;
	}
	
	public static class WaterBall extends Ball{
		
		NodeScaleProcessor NSProcessor1, NSProcessor2;
		ChunkEventChecker<Ball> chck;

		private WaterBall() {
			super(modelWaterName);
			NSProcessor1 = new NodeScaleProcessor(this, Ball.modelWaterName);
			NSProcessor2 = new NodeScaleProcessor(this, Ball.modelWaterName);
			chck = new ChunkEventChecker<Ball>(Ball.WATER_BALL_CH_TYPE, 
					NSProcessor1, dropBubbleProcessor, NSProcessor2, GetOutProcessor.instance());
			chck.addSupplant(Ball.WATER_BALL_CH_TYPE);
		}
		
		private void initializeChecker(){
			NSProcessor1.init(13f, 13f, 50f, 500L);
			NSProcessor2.init(13f, 13f, 0.1f, 1200L);
			this.registerEventChecker(chck);
		}
		
		public static WaterBall get(){
			WaterBall wb = Cache.get(Ball.WaterBall.class);
			wb.initializeChecker();
			return wb;
		}
	}
	
	public static class DustBall extends Ball{
		
		NodeScaleProcessor NSProcessor1;
		ChunkEventChecker<Ball> chck;
		
		private DustBall(){
			super(modelStoneExploit);
			NSProcessor1 = new NodeScaleProcessor(this, Ball.modelStoneExploit);
			chck = new ChunkEventChecker<Ball>(Ball.WATER_BALL_CH_TYPE, NSProcessor1, GetOutProcessor.instance());
			chck.addSupplant(Ball.WATER_BALL_CH_TYPE);
		}
		
		private void initializeChecker(){
			NSProcessor1.init(65f, 65f, 65f, 1500L);
			this.registerEventChecker(chck);
		}
		
		public static DustBall get(){
			DustBall wb = Cache.get(Ball.DustBall.class);
			wb.initializeChecker();
			return wb;
		}
	}
	
	private static class DBProcessor extends SingleActProcessor<Ball>{

		private DBProcessor(CheckerType chType) {
			super(chType);
		}

		@Override
		protected void processSingle(Ball ball, long curTime) {
			BlabKeeper.Blab bubble = blabKeeper.emptyBlub();
			bubble.init(ball.origin().source.x, ball.origin().source.y, 0.3f, 2500l, 65f, 250f);
			blabKeeper.pushBlab(bubble);
		}

		@Override
		protected boolean ready(Ball arg0, long arg1) {
			return true;
		}
		
	}
}
