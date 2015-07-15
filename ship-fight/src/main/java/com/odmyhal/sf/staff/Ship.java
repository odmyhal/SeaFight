package com.odmyhal.sf.staff;


import java.util.prefs.Preferences;

import org.bircks.entierprise.model.ModelStorage;
import org.bricks.annotation.EventHandle;
import org.bricks.annotation.OverlapCheck;
import org.bricks.core.entity.Fpoint;
import org.bricks.core.entity.Point;
import org.bricks.core.help.ConvexityApproveHelper;
import org.bricks.core.help.PointHelper;
import org.bricks.core.help.VectorHelper;
import org.bricks.core.help.WalkerHelper;
import org.bricks.engine.event.BaseEvent;
import org.bricks.engine.event.EventSource;
import org.bricks.engine.event.PrintOverlapEvent;
import org.bricks.engine.event.check.DurableRouteChecker.DurableRoutedWalker;
import org.bricks.engine.event.check.EventChecker;
import org.bricks.engine.event.check.OverlapChecker;
import org.bricks.engine.event.overlap.BrickOverlapAlgorithm;
import org.bricks.engine.event.overlap.OverlapStrategy;
import org.bricks.engine.event.overlap.SmallEventStrategy;
import org.bricks.engine.item.MultiWalkRoller2D;
import org.bricks.engine.neve.ContainsEntityPrint;
import org.bricks.engine.neve.PlanePointsPrint;
import org.bricks.engine.neve.WalkPrint;
import org.bricks.engine.processor.Processor;
import org.bricks.engine.staff.Subject;
import org.bricks.engine.staff.Walker;
import org.bricks.engine.tool.Origin;
import org.bricks.engine.tool.Origin2D;
import org.bricks.exception.Validate;
import org.bricks.extent.debug.SkeletonDebug;
import org.bricks.extent.debug.SpaceDebug;
import org.bricks.extent.entity.CameraSatellite;
import org.bricks.extent.event.ExtentEventGroups;
import org.bricks.extent.event.FireEvent;
import org.bricks.extent.processor.tbroll.Butt;
import org.bricks.extent.rewrite.Matrix4Safe;
import org.bricks.extent.space.Origin3D;
import org.bricks.extent.space.Roll3D;
import org.bricks.extent.space.SSPlanePrint;
import org.bricks.extent.space.SpaceSubject;
import org.bricks.extent.space.SpaceSubjectOperable;
import org.bricks.extent.space.overlap.MarkPoint;
import org.bricks.extent.space.overlap.SkeletonWithPlane;
import org.bricks.extent.subject.model.ModelBrick;
import org.bricks.extent.subject.model.ModelBrickOperable;
import org.bricks.extent.subject.model.ModelBrickSubject;
import org.bricks.extent.subject.model.NodeOperator;
import org.bricks.extent.tool.ModelHelper;
import org.bricks.extent.tool.SkeletonDataStore;
import org.bricks.extent.tool.SkeletonHelper;
import org.bricks.engine.tool.Roll;
import org.bricks.utils.Cache;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.odmyhal.sf.model.Island;
import com.odmyhal.sf.model.ShipSubject;
import com.odmyhal.sf.process.ShipGunHRollProcessor;
import com.odmyhal.sf.process.ShipGunHRollSuperProcessor;
import com.odmyhal.sf.process.ShipGunVRollProcessor;
import com.odmyhal.sf.process.ShipGunVRollSuperProcessor;
import com.odmyhal.sf.process.ShipSinkProcessor;

public class Ship extends MultiWalkRoller2D<SpaceSubjectOperable<?, ?, Fpoint, Roll, ModelBrickOperable>, WalkPrint<?, Fpoint>> 
	implements RenderableProvider, SpaceDebug, DurableRoutedWalker<WalkPrint<?, Fpoint>>, Butt {
	
	public static final Preferences prefs = Preferences.userRoot().node("sf.ship.defaults");
	public static final Preferences cameraPrefs = Preferences.userRoot().node("sf.camera.defaults");
	
	private static final float stepBack = 150f;
	
	public static final String SHIP_SOURCE_TYPE = "ShipSource@sf.odmyhal.com";
	private CameraSatellite cameraSatellite;
	
	
	private SkeletonDebug skeletonDebug;
	
	
//	private NodeOperator pushkaOperator, stvolOperator, backPushkaOperator, backStvolOperator;
	private int healthPoint = Integer.MAX_VALUE;
	private boolean isLiveing = true;
	public final Gun mainGun, backGun;

	private boolean gotShipHit = false;
	private Processor<Ship>[] beforeGetOut;

	public Ship(AssetManager assets) {
//		System.out.println("---Creating ship");
		if(!assets.update()){
			throw new RuntimeException("Accets not ready yet");
		}
		ModelInstance modelInstance = fetchModel(assets);
//		System.out.println("Creating ship of modelInstance: " + modelInstance);
		
		Quaternion q = new Quaternion();
		q.setFromAxis(1000f, 0f, 0f, 90f);
		Matrix4 rMatrix = new Matrix4();
		q.toMatrix(rMatrix.val);
		for(Node node : modelInstance.nodes){
			if(node.id.equals("Dummypushka1")){
//				continue;
				node.translation.add(-135f, -98f, 0f);
			}
			if(node.id.equals("Dummypushka2")){
//				continue;
				node.translation.add(145f, -100f, 135f);
			}
			node.translation.add(0f, 30f, -68f);
			node.translation.mul(rMatrix);
			node.rotation.mulLeft(q);
			node.calculateTransforms(true);
		}
//		ModelSubjectOperable<Ship, ModelSubjectPrint, ModelBrickOperable> subject = new ShipSubject(brick, modelInstance);
		SpaceSubjectOperable<Ship, SSPlanePrint, Fpoint, Roll, ModelBrickOperable> subject = new ShipSubject(modelInstance);
		
		this.addSubject(subject);
		enrichSkeleton(subject);
		
		NodeOperator pushkaOperator = subject.modelBrick.getNodeOperator("pushka");
		NodeOperator stvolOperator = subject.modelBrick.getNodeOperator("stvol");

		mainGun = new Gun(this, pushkaOperator, stvolOperator,
			new Vector3(33.8f,  152.7f,  3.8f),
			new Vector3(33.8f,  154.729126f,  5.85f),
			new Vector3(33.8f, (152.7f + 154.729126f) / 2, (3.8f + 5.85f) / 2)
		);
		
		NodeOperator backPushkaOperator = subject.modelBrick.getNodeOperator("backPushka");
		Validate.isFalse(backPushkaOperator == null);
		NodeOperator backStvolOperator = subject.modelBrick.getNodeOperator("backStvol");
		Validate.isFalse(backStvolOperator == null);
/*		Vector3 tmpOrig = new Vector3(-490f, 0f, 118f);
		Vector3 tmp1 = new Vector3(-490f, -14.5f, 118f);
		Vector3 tmp2 = new Vector3(-490f, 11.5f, 118f);
		Matrix4 A = new Matrix4(subject.modelBrick.linkTransform());
		Matrix4 B = new Matrix4(backPushkaOperator.getNodeData().linkTransform());
		Matrix4 C = new Matrix4(backStvolOperator.getNodeData().linkTransform());
		A.mul(B).mul(C).inv();
		tmpOrig.mul( A );
		tmp1.mul(A);
		tmp2.mul(A);
		System.out.println("Calculated backPushkaOrigin: " + tmpOrig);
		System.out.println("Calculated gun 1 : " + tmp1);
		System.out.println("Calculated gun 2 : " + tmp2);*/
		backGun = new Gun(this, backPushkaOperator, backStvolOperator, 
				new Vector3(21.184765f, 152.92584f, 3.8840432f),
				new Vector3(21.184765f, 152.92584f, 5.807572f),
				new Vector3(21.184765f, 152.92584f, 4.9567804f));
		
		registerEventChecker(OverlapChecker.instance());
		this.adjustCurrentPrint();
		SSPlanePrint sspp = subject.getSafePrint();
		ConvexityApproveHelper.applyConvexity(sspp);
		sspp.free();
	}
	
	//use in motor thread
	public void fire(long fireTime){
		this.mainGun.fire(fireTime);
		this.backGun.fire(fireTime);
	}
	
	public void setHealth(int hp){
		this.healthPoint = hp;
	}
	
	public void setBeforeGetOut(Processor<Ship>... processors){
		this.beforeGetOut = processors;
	}
	
	private void enrichSkeleton(ModelBrickSubject mbs){
		ModelBrick mb = mbs.linkModelBrick();
		ModelInstance mi = mb.linkModelInstance();
		String dataName = "SHIP.DEBUG";
		SkeletonWithPlane swp = new SkeletonWithPlane(SkeletonDataStore.getIndexes(dataName), SkeletonDataStore.getVertexes(dataName), 
				SkeletonDataStore.getPlaneIndexes(dataName), SkeletonDataStore.getPlaneCenterIndex(dataName));
		mb.applySkeletonWithPlane(swp);
		Matrix4Safe nodeMatrix = new Matrix4Safe();
		Node node = ModelHelper.findNode("Dummyship1/ship1", mi.nodes);
		nodeMatrix.set(node.globalTransform);
		swp.addTransform(nodeMatrix);
		
		ModelInstance debugModel = ModelStorage.instance().getModelInstance(dataName);
		Validate.isFalse(debugModel == null, "Could not find model " + dataName);
		
		Node debugNode = ModelHelper.findNode(dataName, debugModel.nodes);
		debugNode.globalTransform.set(nodeMatrix);
		skeletonDebug = new SkeletonDebug(debugModel, mb);
	}
	
	private ModelInstance fetchModel(AssetManager assets){
		Model shipModel = assets.get("models/ship11.g3db", Model.class);
		ModelInstance ship1 = new ModelInstance(shipModel);
		return ship1;
	}

	public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
		for(SpaceSubjectOperable subject: getStaff()){
			subject.getRenderables(renderables, pool);
		}
	}

	public String sourceType() {
		return SHIP_SOURCE_TYPE;
	}
	
	public CameraSatellite initializeCamera(){
		if(this.cameraSatellite == null){
			Camera camera = new PerspectiveCamera(37f, 1250f, 750f);
			camera.far = cameraPrefs.getFloat("camera.far", 40000f);
			System.out.println("Set camera far to " + camera.far);
			camera.near = 10;
			Point origin = this.origin().source;
			double rotation = this.getRotation();
			camera.translate(origin.getFX(), origin.getFY(), 2500f);
			camera.up.rotateRad((float)(rotation - Math.PI / 2), 0f, 0f, 100f);
			camera.lookAt(new Vector3(11500f, 6000f, 250f));
			camera.update();
			CameraSatellite cameraSatelliteK = new CameraSatellite(camera, getRotation());
			addSatellite(cameraSatelliteK);
			this.cameraSatellite = cameraSatelliteK;
		}
		return cameraSatellite;
	}

	@EventHandle(eventType = Island.ISLAND_SF_SOURCE)
	@OverlapCheck(algorithm = BrickOverlapAlgorithm.class, 
		sourceType = Island.ISLAND_SF_SOURCE, 
		strategyClass = OverlapStrategy.TrueOverlapStrategy.class, 
		extractor = Subject.SubjectPrintExtractor.class, 
		producer = PrintOverlapEvent.PrintOverlapEventExtractor.class)
	public void hitCannon(PrintOverlapEvent e){
		this.rollBack(e.getEventTime());
//		this.removeHistory(BaseEvent.touchEventCode);
	}

	@Override
	public void outOfWorld(){
		this.addEvent(new ShipOutOfWorld());
	}
/*	
	@Override
	public void disappear(){
		System.out.println("ship is disappearing...");
	}
*/	
	@EventHandle(eventType = SHIP_SOURCE_TYPE)
	public void shipOutOfWorld(ShipOutOfWorld event){
		this.rollBack(event.getEventTime());
	}

	@EventHandle(eventType = ExtentEventGroups.USER_SOURCE_TYPE)
	public void getOnSight(GetOnSightEvent e){
		Butt butt = e.getButt();
		if(butt == null){
			Gdx.app.debug("WARNING", "(Ship) Have got OnSightEvent without butt...");
		}else if(butt instanceof Walker){
			ShipGunVRollSuperProcessor sgvrp = new ShipGunVRollSuperProcessor(this, mainGun, "stvol");
			sgvrp.setButt(butt);
			registerEventChecker(sgvrp);

			ShipGunHRollSuperProcessor sgrp = new ShipGunHRollSuperProcessor(this, "pushka", sgvrp);
			sgrp.setButt(butt);
			registerEventChecker(sgrp);
			
			ShipGunVRollSuperProcessor sgvrp_back = new ShipGunVRollSuperProcessor(this, backGun, "backStvol");
			sgvrp_back.setButt(butt);
			registerEventChecker(sgvrp_back);

			ShipGunHRollSuperProcessor sgrp_back = new ShipGunHRollSuperProcessor(this, "backPushka", sgvrp);
			sgrp_back.setButt(butt);
			registerEventChecker(sgrp_back);
		}else{
			ShipGunHRollProcessor sgrp = new ShipGunHRollProcessor(this, "pushka");
			sgrp.setButt(butt);
			this.registerEventChecker(sgrp);
			
			ShipGunVRollProcessor sgvrp = new ShipGunVRollProcessor(this, mainGun, "stvol");
			sgvrp.setButt(butt);
			this.registerEventChecker(sgvrp);
			
			ShipGunHRollProcessor sgrp_back = new ShipGunHRollProcessor(this, "backPushka");
			sgrp_back.setButt(butt);
			this.registerEventChecker(sgrp_back);
			
			ShipGunVRollProcessor sgvrp_back = new ShipGunVRollProcessor(this, backGun, "backStvol");
			sgvrp_back.setButt(butt);
			this.registerEventChecker(sgvrp_back);
		}
	}
	
	@EventHandle(eventType = ExtentEventGroups.USER_SOURCE_TYPE)
	public void shoot(FireEvent e){
		this.fire(e.getEventTime());
	}
	
	@EventHandle(eventType = Ammunition.SHIP_AMMUNITION_TYPE)
	public void ammoHurt(PrintOverlapEvent<?, ?, Vector3, ?> event){
		if( this.equals(((Ammunition)event.getEventSource() ).owner()) ){
			return;
		}
		if(isLiveing && --healthPoint <= 0){
			EventChecker<Ship> sinkProcessor = null;
			if(beforeGetOut == null){
				sinkProcessor = new ShipSinkProcessor();
			}else{
				sinkProcessor = new ShipSinkProcessor(beforeGetOut);
			}
			this.registerEventChecker(sinkProcessor);
			isLiveing = false;
		}
	}
	
	
	private Origin<Fpoint> templateOrigin = new Origin2D();
	
	@EventHandle(eventType = Ship.SHIP_SOURCE_TYPE)
	@OverlapCheck(algorithm = BrickOverlapAlgorithm.class, 
		sourceType = Ship.SHIP_SOURCE_TYPE, 
		strategyClass = SmallEventStrategy.class, 
		extractor = Subject.SubjectPrintExtractor.class, 
		producer = PrintOverlapEvent.PrintOverlapEventExtractor.class)
	public void hitAnotherShip(PrintOverlapEvent<PlanePointsPrint<? extends SpaceSubject>, PlanePointsPrint<? extends SpaceSubject<?, ?, ?, ?, ?>>, Fpoint, ?> event){

		ContainsEntityPrint<?, WalkPrint<?, Fpoint>> ssPrint = 
				(ContainsEntityPrint<?, WalkPrint<?, Fpoint>>) event.getSourcePrint();
		WalkPrint<?, Fpoint> walker = ssPrint.linkEntityPrint();
		Fpoint hitVector = new Fpoint();
		Point touchPoint = event.getTouchPoint();
		
		WalkerHelper.generalPointVector(walker, touchPoint, hitVector);
		
		ContainsEntityPrint<?, WalkPrint<?, Fpoint>> mySubjectPrint = 
				(ContainsEntityPrint<?, WalkPrint<?, Fpoint>>) event.getTargetPrint();
		

		WalkPrint<?, Fpoint> myPrint = mySubjectPrint.linkEntityPrint();
		
		if(!replyShipHit(hitVector, touchPoint, myPrint.getOrigin().source, myPrint.getRotation())){
			hitVector.x *= -1;
			hitVector.y *= -1;
			Validate.isTrue(replyShipHit(hitVector, touchPoint, myPrint.getOrigin().source, myPrint.getRotation()), "Can't reply to ship hit");
		}
		
		this.setUpdate();
//		this.removeHistory(BaseEvent.touchEventCode);
		gotShipHit = true;
	}
	
	private boolean replyShipHit(Fpoint hitVector, Point touchPoint, Fpoint myOrigin, double myRotation){
		
		Fpoint moveVector = Cache.get(Fpoint.class);
		moveVector.set(myOrigin.x - touchPoint.getFX(), myOrigin.y - touchPoint.getFY());
		double len = VectorHelper.vectorLen(moveVector);
		
		Fpoint helpVector = Cache.get(Fpoint.class);
		helpVector.set(-moveVector.x, -moveVector.y);
		double rad = Math.PI / 2 - myRotation;
		Fpoint rollVector = Cache.get(Fpoint.class);
		PointHelper.rotatePointByZero(helpVector, Math.sin(rad), Math.cos(rad), rollVector);
		
		int rollK = 1;
		if(rollVector.x * rollVector.y < 0){
			rollVector = PointHelper.rotatePointByZero(helpVector, -1d, 0d, rollVector);
			rollK = -1;
		}else{
			rollVector = PointHelper.rotatePointByZero(helpVector, 1d, 0d, rollVector);
		}
		
		Fpoint moveProjection = VectorHelper.vectorProjection(hitVector, moveVector, Cache.get(Fpoint.class));
		Fpoint rollProjection = VectorHelper.vectorProjection(hitVector, rollVector, Cache.get(Fpoint.class));
		
		boolean reply = false;
		if(VectorHelper.hasSameDirection(moveProjection, moveVector)){
			templateOrigin.source.set(moveProjection.x / 100, moveProjection.y / 100);
			this.translate(templateOrigin, false);
			reply = true;
		}
		
		if(VectorHelper.hasSameDirection(rollProjection, rollVector)){
			float rotation = (float) (VectorHelper.vectorLen(rollProjection) / len) * rollK;
			this.setToRotation(this.getRotation() + rotation / 500);
			reply = true;
		}
		Cache.put(moveVector);
		Cache.put(helpVector);
		Cache.put(rollVector);
		Cache.put(moveProjection);
		Cache.put(rollProjection);
		return reply;
	}

	public RenderableProvider debugModel() {
		return skeletonDebug;
	}

	public boolean correctRoute() {
		if(gotShipHit){
			gotShipHit = false;
			return true;
		}
		return false;
	}
	
	public class ShipOutOfWorld extends BaseEvent{
		
		public int getEventGroupCode() {
			return BaseEvent.touchEventCode;
		}

		@Override
		public String sourceType() {
			return Ship.this.sourceType();
		}

		@Override
		public EventSource getEventSource() {
			return Ship.this;
		}
	}
	

	public void fetchOrigin(Vector3 dest){
		WalkPrint<?, Fpoint> wp = this.getSafePrint();
		Fpoint shipCenter = wp.getOrigin().source;
		double rotation = wp.getRotation();
		dest.x = shipCenter.getFX() - stepBack * (float) Math.cos(rotation);
		dest.y = shipCenter.getFY() - stepBack * (float) Math.sin(rotation);
		dest.z = 50f;
		wp.free();
	}
}
