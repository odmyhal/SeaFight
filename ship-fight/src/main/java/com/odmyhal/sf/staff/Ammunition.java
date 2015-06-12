package com.odmyhal.sf.staff;

import java.util.prefs.Preferences;

import org.bircks.entierprise.model.ModelStorage;
import org.bricks.annotation.EventHandle;
import org.bricks.annotation.OverlapCheck;
import org.bricks.engine.event.OverlapEvent;
import org.bricks.engine.event.check.CheckerType;
import org.bricks.engine.event.check.ChunkEventChecker;
import org.bricks.engine.event.check.OverlapChecker;
import org.bricks.engine.event.overlap.BrickOverlapAlgorithm;
import org.bricks.engine.event.overlap.OverlapStrategy;
import org.bricks.engine.processor.GetOutProcessor;
import org.bricks.engine.processor.SingleActProcessor;
import org.bricks.engine.item.OriginMover;
import org.bricks.engine.neve.EntityPrint;
import org.bricks.engine.neve.OriginMovePrint;
import org.bricks.engine.tool.Origin;
import org.bricks.engine.tool.Walk;
import org.bricks.extent.engine.processor.CheckInWorldProcessor;
import org.bricks.extent.engine.processor.NodeScaleProcessor;
import org.bricks.extent.space.Origin3D;
import org.bricks.extent.space.Roll3D;
import org.bricks.extent.space.SpaceSubject;
import org.bricks.extent.space.SpaceWalker;
import org.bricks.extent.space.Walk3D;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.odmyhal.sf.model.Island;
import com.odmyhal.sf.model.Ball;
import com.odmyhal.sf.model.bubble.BlabKeeper;
import com.odmyhal.sf.model.bubble.BlabKeeper.Blab;
import com.odmyhal.sf.process.FaceWaterEvent;
import com.odmyhal.sf.process.FaceWaterEventChecker;

import org.bricks.extent.space.SSPrint;
import org.bricks.extent.space.overlap.LineCrossMBAlgorithm;
import org.bricks.extent.subject.model.MBSVehicle;
import org.bricks.extent.subject.model.ModelBrick;


public class Ammunition extends OriginMover<SpaceSubject<?, ?, Vector3, Roll3D, ?>, OriginMovePrint<?, Vector3>, Vector3, Roll3D> implements RenderableProvider{
	
	public static final Preferences prefs = Preferences.userRoot().node("sf.ship.ammunition");
	public static final String SHIP_AMMUNITION_TYPE = "ShipAmmunitio@sf.myhal.com";
	private static final CheckInWorldProcessor<Ammunition> inWorldProcessor
		= new CheckInWorldProcessor<Ammunition>(prefs.getInt("ship.ammo1.world.min", -100), 
				prefs.getInt("ship.ammo1.world.max", 10000));
	public static final float accelerationZ = prefs.getFloat("ship.ammo1.acceleration.z", 0f);
	
	private SpaceSubject<SpaceWalker, SSPrint, Vector3, Roll3D, ModelBrick> subject;
	private Origin<Vector3> tmpOrigin = new Origin3D();
	private Ship myShip;
	
	private static BlabKeeper blabKeeper;
	private static final DBProcessor dropBubbleProcessor = new DBProcessor(CheckerType.registerCheckerType());
	//
//	public Vector3 previousOrigin = new Vector3();
	
	public Ammunition(){
		Vector3 one = new Vector3(-24f, 0f, 0f);
		Vector3 two = new Vector3(36f, 0f, 0f);
		subject = new SpaceSubject(new MBSVehicle.Space(), ModelStorage.instance().getModelInstance("ship_ammunition"), new Vector3(), one, two);
		this.addSubject(subject);
		this.registerEventChecker(inWorldProcessor);
		this.registerEventChecker(new FaceWaterEventChecker());
		this.registerEventChecker(OverlapChecker.instance());
	}
/*	
	public Vector3 maxPoint = new Vector3();
	public long maxPointTime = 0;
	public long creationTime = 0;
	protected void innerProcess(long currentTime){
		super.innerProcess(currentTime);
		Vector3 myOrigin = origin().source;
		if(myOrigin.z > maxPoint.z){
			maxPoint.set(myOrigin);
			maxPointTime = currentTime;
		}
	}
*/
	
	public static void setBlabKeeper(BlabKeeper bk){
		blabKeeper = bk;
	}
	public String sourceType() {
		return SHIP_AMMUNITION_TYPE;
	}
	
	public void setMyShip(Ship ship){
		this.myShip = ship;
	}
	
	public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
		for(SpaceSubject subject: getStaff()){
			subject.getRenderables(renderables, pool);
		}
	}

	@EventHandle(eventType = SHIP_AMMUNITION_TYPE)
	public void faceWater(FaceWaterEvent event){
		Ball wb = new Ball(Ball.modelWaterName);

		NodeScaleProcessor NSProcessor1 = new NodeScaleProcessor(wb, Ball.modelWaterName);
		NSProcessor1.init(13f, 13f, 50f, 500L);
		NodeScaleProcessor NSProcessor2 = new NodeScaleProcessor(wb, Ball.modelWaterName);
		NSProcessor2.init(13f, 13f, 0.1f, 1200L);
		
		ChunkEventChecker<Ball> chck = new ChunkEventChecker<Ball>(Ball.WATER_BALL_CH_TYPE, 
				NSProcessor1, dropBubbleProcessor, NSProcessor2, GetOutProcessor.instance());
		wb.registerEventChecker(chck);
		
		tmpOrigin.source.set(event.touchPoint());
		wb.translate(tmpOrigin);
		wb.applyEngine(this.getEngine());
	}
	
	@EventHandle(eventType = Ship.SHIP_SOURCE_TYPE)
	@OverlapCheck(algorithm = LineCrossMBAlgorithm.class, sourceType = Ship.SHIP_SOURCE_TYPE, strategyClass = OverlapStrategy.TrueOverlapStrategy.class)
	public void hitShip(OverlapEvent<?, SSPrint<?, EntityPrint, ?>, Vector3> event){
		if(event.getSourcePrint().linkEntityPrint().getTarget().equals(myShip)){
			this.disappear();
		}else{
			hitStone(event);
		}
	}
	
	@EventHandle(eventType = Island.ISLAND_SF_SOURCE)
	@OverlapCheck(algorithm = LineCrossMBAlgorithm.class, sourceType = Island.ISLAND_SF_SOURCE, strategyClass = OverlapStrategy.TrueOverlapStrategy.class)
	public void hitStone(OverlapEvent<?, ?, Vector3> event){
		Ball wb = new Ball(Ball.modelStoneExploit);
		
		NodeScaleProcessor NSProcessor1 = new NodeScaleProcessor(wb, Ball.modelStoneExploit);
		NSProcessor1.init(65f, 65f, 65f, 1500L);
		ChunkEventChecker<Ball> chck = new ChunkEventChecker<Ball>(Ball.WATER_BALL_CH_TYPE, NSProcessor1, GetOutProcessor.instance());
		wb.registerEventChecker(chck);
		
		tmpOrigin.source.set(event.getTouchPoint());
		wb.translate(tmpOrigin);
		wb.applyEngine(this.getEngine());
		this.disappear();
	}

	@Override
	protected Walk<Vector3> provideInitialLegs() {
		return new Walk3D(this);
	}

	@Override
	public Origin<Vector3> provideInitialOrigin() {
		return new Origin3D();
	}

	@Override
	protected Roll3D initializeRoll() {
		return new Roll3D();
	}

	
//	private static final CheckerType chType = CheckerType.registerCheckerType(); 
	
	private static class DBProcessor extends SingleActProcessor<Ball>{

		private DBProcessor(CheckerType chType) {
			super(chType);
		}

		@Override
		protected void processSingle(Ball ball, long curTime) {
			BlabKeeper.Blab bubble = blabKeeper.new Blab(ball.origin().source.x, ball.origin().source.y,
					0.3f, 2500l, 65f, 250f);
			blabKeeper.pushBlab(bubble);
		}

		@Override
		protected boolean ready(Ball arg0, long arg1) {
			return true;
		}
		
	}
}
