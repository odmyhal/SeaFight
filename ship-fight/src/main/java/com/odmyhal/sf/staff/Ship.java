package com.odmyhal.sf.staff;


import java.util.prefs.Preferences;

import org.bircks.entierprise.model.ModelStorage;
import org.bricks.core.entity.Fpoint;
import org.bricks.core.entity.Point;
import org.bricks.core.help.ConvexityApproveHelper;
import org.bricks.core.help.PointHelper;
import org.bricks.core.help.VectorHelper;
import org.bricks.core.help.WalkerHelper;
import org.bricks.engine.event.BaseEvent;
import org.bricks.engine.event.EventSource;
import org.bricks.engine.event.OverlapEvent;
import org.bricks.engine.event.check.DurableRouteChecker.DurableRoutedWalker;
import org.bricks.engine.event.check.OverlapChecker;
import org.bricks.engine.event.overlap.BrickOverlapAlgorithm;
import org.bricks.engine.event.overlap.OverlapStrategy;
import org.bricks.engine.event.overlap.SmallEventStrategy;
import org.bricks.engine.item.MultiWalkRoller;
import org.bricks.engine.item.MultiWalkRoller2D;
import org.bricks.engine.neve.ContainsEntityPrint;
import org.bricks.engine.neve.PlanePointsPrint;
import org.bricks.engine.neve.WalkPrint;
import org.bricks.engine.staff.Walker;
import org.bricks.engine.tool.Origin;
import org.bricks.engine.tool.Origin2D;
import org.bricks.exception.Validate;
import org.bricks.extent.debug.SkeletonDebug;
import org.bricks.extent.debug.SpaceDebug;
import org.bricks.extent.entity.CameraSatellite;
import org.bricks.extent.entity.mesh.ModelSubjectOperable;
import org.bricks.extent.entity.mesh.ModelSubjectPrint;
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
import org.bricks.extent.space.overlap.Skeleton;
import org.bricks.extent.space.overlap.SkeletonWithPlane;
import org.bricks.extent.subject.model.ModelBrick;
import org.bricks.extent.subject.model.ModelBrickOperable;
import org.bricks.extent.subject.model.ModelBrickSubject;
import org.bricks.extent.subject.model.NodeOperator;
import org.bricks.extent.tool.ModelHelper;
import org.bricks.extent.tool.SkeletonDataStore;
import org.bricks.extent.tool.SkeletonHelper;
import org.bricks.annotation.EventHandle;
import org.bricks.annotation.OverlapCheck;
import org.bricks.engine.tool.Roll;

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

public class Ship extends MultiWalkRoller2D<SpaceSubjectOperable<?, ?, Fpoint, Roll, ModelBrickOperable>, WalkPrint<?, Fpoint>> 
	implements RenderableProvider, SpaceDebug, DurableRoutedWalker<WalkPrint<?, Fpoint>>, Butt {
	
	public static final Preferences prefs = Preferences.userRoot().node("sf.ship.defaults");
	
	private static final float stepBack = 150f;
	
	public static final String SHIP_SOURCE_TYPE = "ShipSource@sf.odmyhal.com";
	private CameraSatellite cameraSatellite;
	private MarkPoint gunMark;
	
	private SkeletonDebug skeletonDebug;
	
	private Origin<Vector3> fireOrigin = new Origin3D();
	private Vector3 helpVector = new Vector3();
	private Vector3 h2V = new Vector3();
	private Quaternion helpQ = new Quaternion();
	private long gunMarkTime = 0;
	private NodeOperator pushkaOperator, stvolOperator;
	

	private boolean fireQue = true, gotShipHit = false;

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
				node.translation.add(/*-125f*/-135f, -98f, 0f);
			}
			if(node.id.equals("Dummypushka2")){
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
		gunMark = new MarkPoint(
			new Vector3(33.8f,  152.7f,  3.8f),
			new Vector3(33.8f,  154.729126f,  5.85f),
			new Vector3(33.8f, (152.7f + 154.729126f) / 2, (3.8f + 5.85f) / 2)
		);
		pushkaOperator = subject.modelBrick.getNodeOperator("pushka");
		stvolOperator = subject.modelBrick.getNodeOperator("stvol");
		gunMark.addTransform(subject.modelBrick.linkTransform());
		gunMark.addTransform(pushkaOperator.getNodeData().linkTransform());
		gunMark.addTransform(stvolOperator.getNodeData().linkTransform());
		
		registerEventChecker(OverlapChecker.instance());
//		System.out.println("Before ship adjustCurrentPrint");
		this.adjustCurrentPrint();
		SSPlanePrint sspp = subject.getSafePrint();
		ConvexityApproveHelper.applyConvexity(sspp);
		sspp.free();
		System.out.println("Created ship " + this);
	}
	
	//use in motor thread
	public Vector3 getGunPoint(int num, long procTime){
		if(gunMarkTime < procTime){
			gunMark.calculateTransforms();
			gunMarkTime = procTime;
		}
		return gunMark.getMark(num);
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
			camera.far = 40000;
			camera.near = 10;
			Point origin = this.origin().source;
			double rotation = this.getRotation();
			camera.translate(origin.getFX(), origin.getFY(), 2500f);
			camera.up.rotateRad((float)(rotation - Math.PI / 2), 0f, 0f, 100f);
			camera.update();
			CameraSatellite cameraSatelliteK = new CameraSatellite(camera, getRotation());
			addSatellite(cameraSatelliteK);
			this.cameraSatellite = cameraSatelliteK;
		}
		return cameraSatellite;
	}

	@EventHandle(eventType = Island.ISLAND_SF_SOURCE)
	@OverlapCheck(algorithm = BrickOverlapAlgorithm.class, sourceType = Island.ISLAND_SF_SOURCE, strategyClass = OverlapStrategy.TrueOverlapStrategy.class)
	public void hitCannon(OverlapEvent e){
		this.rollBack(e.getEventTime());
		this.removeHistory(BaseEvent.touchEventCode);
	}

	@Override
	public void outOfWorld(){
//		System.out.println("Adding out of world event");
		this.addEvent(new ShipOutOfWorld());
//		this.rollBack(;)
//		super.outOfWorld();
//		System.out.println(this.getlog());
	}
	
	@Override
	public void disappear(){
		System.out.println("ship is disappearing...");
	}
	
	@EventHandle(eventType = SHIP_SOURCE_TYPE)
	public void shipOutOfWorld(ShipOutOfWorld event){
//		System.out.println("Trying to rollBack ship");
		this.rollBack(event.getEventTime());
	}
	
/*	private Ship onSight;
	
	public void tmpSetOnSight(Ship sight){
		this.onSight = sight;
	}
*/	
	@EventHandle(eventType = ExtentEventGroups.USER_SOURCE_TYPE)
	public void getOnSight(GetOnSightEvent e){
		Butt butt = e.getButt();
		if(butt == null){
			Gdx.app.debug("WARNING", "(Ship) Have got OnSightEvent without butt...");
		}else if(butt instanceof Walker){
			ShipGunVRollSuperProcessor sgvrp = new ShipGunVRollSuperProcessor(this);
			sgvrp.setButt(butt);
			registerEventChecker(sgvrp);

			ShipGunHRollSuperProcessor sgrp = new ShipGunHRollSuperProcessor(this, sgvrp);
			sgrp.setButt(butt);
			registerEventChecker(sgrp);
		}else{
			ShipGunHRollProcessor sgrp = new ShipGunHRollProcessor(this);
			sgrp.setButt(butt);
			this.registerEventChecker(sgrp);
			
			ShipGunVRollProcessor sgvrp = new ShipGunVRollProcessor(this);
			sgvrp.setButt(butt);
			this.registerEventChecker(sgvrp);
		}
	}
	
	@EventHandle(eventType = ExtentEventGroups.USER_SOURCE_TYPE)
	public void shoot(FireEvent e){
		fire(e.getEventTime());
	}
	
	@EventHandle(eventType = Ammunition.SHIP_AMMUNITION_TYPE)
	public void ammoHurt(OverlapEvent<?, ?, Vector3> event){
//		System.out.println("Ship got ammo hurt: " + event.getTouchPoint());
	}
	
	
	private Origin<Fpoint> templateOrigin = new Origin2D();
	
	@EventHandle(eventType = Ship.SHIP_SOURCE_TYPE)
	@OverlapCheck(algorithm = BrickOverlapAlgorithm.class, sourceType = Ship.SHIP_SOURCE_TYPE, strategyClass = /*OverlapStrategy.TrueOverlapStrategy.class*/SmallEventStrategy.class)
	public void hitAnotherShip(OverlapEvent<PlanePointsPrint<? extends SpaceSubject>, PlanePointsPrint<? extends SpaceSubject>, Fpoint> event){

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
		
//		this.adjustCurrentPrint();
//		this.adjustInMotorPrint();
		this.setUpdate();
		this.removeHistory(BaseEvent.touchEventCode);
		gotShipHit = true;
	}
	
	private boolean replyShipHit(Fpoint hitVector, Point touchPoint, Fpoint myOrigin, double myRotation){
		
		Fpoint moveVector = new Fpoint(myOrigin.x - touchPoint.getFX(), myOrigin.y - touchPoint.getFY());
		double len = VectorHelper.vectorLen(moveVector);
		
		Fpoint helpVector = new Fpoint(-moveVector.x, -moveVector.y);
		double rad = Math.PI / 2 - myRotation;
		Fpoint rollVector = PointHelper.rotatePointByZero(helpVector, Math.sin(rad), Math.cos(rad), new Fpoint());
		
		int rollK = 1;
		if(rollVector.x * rollVector.y < 0){
			rollVector = PointHelper.rotatePointByZero(helpVector, -1d, 0d, rollVector);
			rollK = -1;
		}else{
			rollVector = PointHelper.rotatePointByZero(helpVector, 1d, 0d, rollVector);
		}
		
		Fpoint moveProjection = VectorHelper.vectorProjection(hitVector, moveVector, new Fpoint());
		Fpoint rollProjection = VectorHelper.vectorProjection(hitVector, rollVector, new Fpoint());
		
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
		return reply;
	}

	public void fire(long fireTime){
		if(fireQue = !fireQue){
			this.fire(0, fireTime);
		}else{
			this.fire(1, fireTime);
		}
	}
	
	private void fire(int base, long fireTime){
		Ammunition ammo = new Ammunition();
		ammo.setMyShip(this);
		this.gunMark.calculateTransforms();
		Vector3 baseVector = this.getGunPoint(base, fireTime);
		
		fireOrigin.source.set(baseVector);
		ammo.translate(fireOrigin);

		double hRad = this.getRotation() + pushkaOperator.rotatedRadians() - Math.PI / 2;
		double vRad = Math.PI - stvolOperator.rotatedRadians();
		helpVector.set(1000f * (float) Math.cos(hRad), 1000f * (float) Math.sin(hRad), 1000f * (float) Math.tan(vRad));
		
		//Little randomazation of direction
		h2V.set(helpVector.y, helpVector.z, helpVector.x);
		helpQ.setFromAxisRad(helpVector, (float) (Math.random() * Math.PI * 2));
		h2V.mul(helpQ);
		helpQ.setFromAxis(h2V, (float) (0.5 - Math.random()));
		helpVector.mul(helpQ);
		//end randomization
		
		float h = 9f;
		h2V.set(h, 0f, 0f);
		h2V.crs(helpVector);
		//Rotates ammo to proper direction
		float scalar_mult = h * helpVector.x;
		double cos = scalar_mult / (helpVector.len() * h);
		double rad = Math.acos(cos);
		Roll3D roll = ammo.linkRoll();
		roll.setSpin(h2V, fireTime);
		roll.setRotation((float) rad);
		ammo.applyRotation();
		
		//Sets ammo acceleration
		float ammoSpeed = Ammunition.prefs.getFloat("ship.ammo1.speed.directional", 1f);
		helpVector.nor();

		
		helpVector.scl(ammoSpeed);
		ammo.getVector().source.set(helpVector);
		fireOrigin.source.set(0f, 0f, Ammunition.accelerationZ);
		ammo.setAcceleration(fireOrigin, fireTime);
		
		//Sets ammo ratation
		h2V.set(helpVector.x, helpVector.y, helpVector.z + Ammunition.accelerationZ);
		scalar_mult = helpVector.x * h2V.x + helpVector.y * h2V.y + helpVector.z * h2V.z;
		cos = scalar_mult / (h2V.len() * helpVector.len());
		ammo.setRotationSpeed((float) Math.acos(cos));
		helpVector.crs(h2V);
		roll.setSpin(helpVector, fireTime);
		//Set previous origin to current
		ammo.applyEngine(this.getEngine());
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
