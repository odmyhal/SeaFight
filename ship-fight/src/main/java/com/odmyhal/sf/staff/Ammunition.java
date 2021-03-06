package com.odmyhal.sf.staff;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.prefs.Preferences;

import org.bircks.entierprise.model.ModelStorage;
import org.bricks.annotation.EventHandle;
import org.bricks.annotation.OverlapCheck;
import org.bricks.engine.Engine;
import org.bricks.engine.Motor;
import org.bricks.engine.event.PrintOverlapEvent;
import org.bricks.engine.event.check.OverlapChecker;
import org.bricks.engine.event.overlap.OverlapStrategy;
import org.bricks.engine.item.OriginMover;
import org.bricks.engine.neve.EntityPrint;
import org.bricks.engine.neve.OriginMovePrint;
import org.bricks.engine.staff.Subject;
import org.bricks.engine.tool.Accelerator;
import org.bricks.engine.tool.Origin;
import org.bricks.engine.tool.Walk;
import org.bricks.enterprise.inform.Informator;
import org.bricks.extent.effects.BricksParticleSystem;
import org.bricks.extent.effects.EffectSubject;
import org.bricks.extent.effects.TemporaryEffect;
import org.bricks.extent.processor.CheckInWorldProcessor;
import org.bricks.extent.space.Accelerator3D;
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
import com.odmyhal.sf.effects.DustEffect;
import com.odmyhal.sf.effects.WaterBallEffect;
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
import org.bricks.utils.Cache;
import org.bricks.utils.Cache.DataProvider;


public class Ammunition extends OriginMover<SpaceSubject<?, ?, Vector3, Roll3D, ?>, OriginMovePrint<?, Vector3>, Vector3, Roll3D> implements RenderableProvider{
	
	public static final Preferences prefs = Preferences.userRoot().node("sf.ship.ammunition");
	public static final String SHIP_AMMUNITION_TYPE = "ShipAmmunitio@sf.myhal.com";
	public static final String DUST_CACHE_NAME = "dust-cache";
	public static final String WATER_BALL_CACHE_NAME = "water-ball-cache";
	
	private static final CheckInWorldProcessor<Ammunition> inWorldProcessor
		= new CheckInWorldProcessor<Ammunition>(Preferences.userRoot().node("engine.settings").getInt("world.altitude.min", -100), 
				Preferences.userRoot().node("engine.settings").getInt("world.altitude.max", 10000));
	public static final float accelerationZ = prefs.getFloat("ship.ammo1.acceleration.z", 0f);
	
	public static BlabKeeper blabKeeper;
	
	public static final AtomicInteger counter = new AtomicInteger();

	public static final AtomicInteger dustInstances = new AtomicInteger();

	static{
		Cache.registerCache(Ammunition.class, new Cache.DataProvider<Ammunition>() {

			@Override
			public Ammunition provideNew() {
				return new Ammunition();
			}
		});

		Cache.registerTransferCache(EffectSubject.Transfered.class, DUST_CACHE_NAME, new DataProvider<EffectSubject.Transfered>(){

			@Override
			public EffectSubject.Transfered provideNew() {
				dustInstances.incrementAndGet();
				TemporaryEffect effect = new DustEffect(BricksParticleSystem.particleSystem());
				effect.init();
				return new EffectSubject.Transfered(effect, DUST_CACHE_NAME);
//				System.out.println(System.currentTimeMillis() + " " + Thread.currentThread().getName() + " -- new Dust instance created : " + r + ", with effect : " + effect);
//				return r;
			}
			
		});
		
		Cache.registerTransferCache(EffectSubject.Transfered.class, WATER_BALL_CACHE_NAME, new DataProvider<EffectSubject.Transfered>(){

			@Override
			public EffectSubject.Transfered provideNew() {
				dustInstances.incrementAndGet();
				TemporaryEffect effect = new WaterBallEffect(BricksParticleSystem.particleSystem());
				effect.init();
				return new EffectSubject.Transfered(effect, WATER_BALL_CACHE_NAME);
			}
			
		});
	}
	
	
	private SpaceSubject<SpaceWalker, SSPrint, Vector3, Roll3D, ModelBrick> subject;
	private Origin<Vector3> tmpOrigin = new Origin3D();
	private Ship myShip;
	
//	private static final DBProcessor dropBubbleProcessor = new DBProcessor(CheckerType.registerCheckerType());
	//
//	public Vector3 previousOrigin = new Vector3();
	
	private Ammunition(){
		Vector3 one = new Vector3(-24f, 0f, 0f);
		Vector3 two = new Vector3(36f, 0f, 0f);
		subject = new SpaceSubject(new MBSVehicle.Space(), ModelStorage.instance().getModelInstance("ship_ammunition"), new Vector3(), one, two);
		this.addSubject(subject);
		this.registerEventChecker(inWorldProcessor);
		this.registerEventChecker(new FaceWaterEventChecker());
		this.registerEventChecker(OverlapChecker.instance());
		counter.incrementAndGet();
	}
	
	public static Ammunition get(){
		return Cache.get(Ammunition.class);
	}
	
	public void disappear(){
		super.disappear();
		tmpOrigin.set(this.origin());
		tmpOrigin.mult(-1);
		this.translate(tmpOrigin);
		this.setToRotation(0f);
		this.subject.linkModelBrick().reset();
		this.adjustCurrentPrint();
//		startTime = 0;
//		hooks = 0;
		Cache.put(this);
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
	
/*	public static void setBlabKeeper(BlabKeeper bk){
		blabKeeper = bk;
	}*/
	public String sourceType() {
		return SHIP_AMMUNITION_TYPE;
	}
	
	public void setMyShip(Ship ship){
		this.myShip = ship;
	}
	
	public Ship owner(){
		return myShip;
	}
	
	public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
		for(SpaceSubject subject: getStaff()){
			subject.getRenderables(renderables, pool);
		}
	}

	@EventHandle(eventType = SHIP_AMMUNITION_TYPE)
	public void faceWater(FaceWaterEvent event){
		EffectSubject.Transfered effect = Cache.get(EffectSubject.Transfered.class, WATER_BALL_CACHE_NAME);
		effect.setToTranslation(event.touchPoint());
		effect.applyEngine(this.getEngine());
//		Informator.log(String.format("Face water in %s, vector: %s", event.touchPoint(), this.getVector().source));
//		Informator.log(String.format("FaceWaterData FlyTime: %d, hooks: %d, lastMove: %s", event.getEventTime() - this.startTime, this.hooks, this.lastMove.source));
	}
	
	@EventHandle(eventType = Ship.SHIP_SOURCE_TYPE)
	@OverlapCheck(algorithm = LineCrossMBAlgorithm.class, 
		sourceType = Ship.SHIP_SOURCE_TYPE, 
		strategyClass = OverlapStrategy.TrueOverlapStrategy.class, 
		extractor = Subject.SubjectPrintExtractor.class, 
		producer = PrintOverlapEvent.PrintOverlapEventExtractor.class)
	public void hitShip(PrintOverlapEvent<?, SSPrint<?, EntityPrint, ?>, Vector3, ?> event){
		if(event.getSourcePrint().linkEntityPrint().getTarget().equals(myShip)){
			this.disappear();
		}else{
//			Informator.log(String.format("Face  ship in %s, vector: %s", event.getTouchPoint(), this.getVector().source));
//			Informator.log(String.format("FaceShipData  FlyTime: %d, hooks: %d, lastMove: %s", event.getEventTime() - this.startTime, this.hooks, this.lastMove.source));
//			Informator.log(" MaxHookPer: " + this.maxHookPer);
			hitStone(event);
		}
	}
	
	@EventHandle(eventType = Island.ISLAND_SF_SOURCE)
	@OverlapCheck(algorithm = LineCrossMBAlgorithm.class, 
		sourceType = Island.ISLAND_SF_SOURCE, 
		strategyClass = OverlapStrategy.TrueOverlapStrategy.class, 
		extractor = Subject.SubjectPrintExtractor.class, 
		producer = PrintOverlapEvent.PrintOverlapEventExtractor.class)
	public void hitStone(PrintOverlapEvent<?, ?, Vector3, ?> event){
/*		Ball wb = Ball.DustBall.get();
		tmpOrigin.source.set(event.getTouchPoint());
		wb.translate(tmpOrigin);
		wb.applyEngine(this.getEngine());*/
//		EffectSystem.addDustEffect(event.getTouchPoint());
//		Vector3 touchPoint = event.getTouchPoint();
		EffectSubject.Transfered effect = Cache.get(EffectSubject.Transfered.class, DUST_CACHE_NAME);
		effect.setToTranslation(event.getTouchPoint());
//		System.out.println(System.currentTimeMillis() + " " + Thread.currentThread().getName() + " -- new Dust instance " + effect + ", translated to " + event.getTouchPoint());
		effect.applyEngine(this.getEngine(), Motor.getCurrentMotor());
//		EffectSystem.addEffect(DustEffect.class, touchPoint.x, touchPoint.y, touchPoint.z);
		this.disappear();
	}

	@Override
	protected Walk<Vector3> provideInitialLegs() {
		return new Walk3D(/*this*/);
	}

	@Override
	public Origin<Vector3> provideInitialOrigin() {
		return new Origin3D();
	}

	@Override
	protected Roll3D initializeRoll() {
		return new Roll3D();
	}

	@Override
	protected Accelerator<Vector3> provideAcceleration() {
		return new Accelerator3D();
	}
/*
	public long startTime = 0;
	public int hooks = 0;
	public long lastTime = 0;
	public long hookPer = 0;
	public long maxHookPer = 0;
	protected void innerProcess(long currentTime){
		++hooks;
		if(lastTime == currentTime){
			hookPer++;
			maxHookPer = Math.max(maxHookPer, hookPer);
		}else{
			hookPer = 0;
			lastTime = currentTime;
		}
		super.innerProcess(currentTime);
	}
	*/
//	private static final CheckerType chType = CheckerType.registerCheckerType(); 

}
