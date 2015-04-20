package com.odmyhal.sf.staff;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.bircks.entierprise.model.ModelStorage;
import org.bricks.core.entity.Ipoint;
import org.bricks.core.entity.Point;
import org.bricks.core.entity.impl.PointSetBrick;
import org.bricks.core.entity.type.Brick;
import org.bricks.core.help.ConvexityApproveHelper;
import org.bricks.engine.event.BaseEvent;
import org.bricks.engine.event.OverlapEvent;
import org.bricks.engine.event.check.OverlapChecker;
import org.bricks.engine.event.overlap.BrickOverlapAlgorithm;
import org.bricks.engine.event.overlap.OverlapStrategy;
import org.bricks.engine.item.MultiWalkRoller;
import org.bricks.engine.neve.WalkPrint;
import org.bricks.engine.tool.Origin;
import org.bricks.exception.Validate;
import org.bricks.extent.debug.SkeletonDebug;
import org.bricks.extent.debug.SpaceDebug;
import org.bricks.extent.entity.CameraSatellite;
import org.bricks.extent.entity.mesh.ModelSubjectOperable;
import org.bricks.extent.entity.mesh.ModelSubjectPrint;
import org.bricks.extent.event.ExtentEventGroups;
import org.bricks.extent.event.FireEvent;
import org.bricks.extent.space.Origin3D;
import org.bricks.extent.space.Roll3D;
import org.bricks.extent.space.overlap.MarkPoint;
import org.bricks.extent.space.overlap.Skeleton;
import org.bricks.extent.subject.model.ModelBrick;
import org.bricks.extent.subject.model.ModelBrickOperable;
import org.bricks.extent.subject.model.ModelBrickSubject;
import org.bricks.extent.tool.ModelHelper;
import org.bricks.extent.tool.SkeletonDataStore;
import org.bricks.extent.tool.SkeletonHelper;
import org.bricks.annotation.EventHandle;
import org.bricks.annotation.OverlapCheck;

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

public class Ship extends MultiWalkRoller<ModelSubjectOperable<?, ?, ModelBrickOperable>, WalkPrint> implements RenderableProvider, SpaceDebug {
	
	public static final String SHIP_SOURCE_TYPE = "ShipSource@sf.odmyhal.com";
	private CameraSatellite cameraSatellite;
	private MarkPoint gunMark;
	
	private SkeletonDebug skeletonDebug;
	
	private Origin<Vector3> fireOrigin = new Origin3D();
	private Vector3 helpVector = new Vector3();
	private Vector3 h2V = new Vector3();
	private Quaternion helpQ = new Quaternion();

	public Ship(AssetManager assets) {
		if(!assets.update()){
			throw new RuntimeException("Accets not ready yet");
		}
		Brick brick = produceBrick();
		ModelInstance modelInstance = fetchModel(assets);
		
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
		ModelSubjectOperable<Ship, ModelSubjectPrint, ModelBrickOperable> subject = new ShipSubject(brick, modelInstance);
		
		this.addSubject(subject);
		enrichSkeleton(subject);
		gunMark = new MarkPoint(
			new Vector3(45f,  152.7f,  3.8f), 
			new Vector3(33.8f,  152.7f,  3.8f),
			new Vector3(45f,  154.729126f,  5.85f), 
			new Vector3(33.8f,  154.729126f,  5.85f)
		);
		gunMark.addTransform(subject.modelBrick.linkTransform());
		gunMark.addTransform(subject.modelBrick.getNodeOperator("pushka").getNodeData("Dummypushka1").linkTransform());
		gunMark.addTransform(subject.modelBrick.getNodeOperator("stvol").getNodeData("pushka_garmaty1").linkTransform());
		
		registerEventChecker(OverlapChecker.instance());
	}
	
	private void enrichSkeleton(ModelBrickSubject mbs){
		ModelBrick mb = mbs.linkModelBrick();
		ModelInstance mi = mb.linkModelInstance();
		String dataName = "SHIP.DEBUG";
		Skeleton skeleton = mb.initSkeleton(SkeletonDataStore.getVertexes(dataName), SkeletonDataStore.getIndexes(dataName));
		Matrix4 nodeMatrix = new Matrix4();
		Node node = ModelHelper.findNode("Dummyship1/ship1", mi.nodes);
		nodeMatrix.set(node.globalTransform);
		skeleton.addTransform(nodeMatrix);
		
		ModelInstance debugModel = ModelStorage.instance().getModelInstance(dataName);
		Validate.isFalse(debugModel == null, "Could not find model " + dataName);
		
		Node debugNode = ModelHelper.findNode(dataName, debugModel.nodes);
		debugNode.globalTransform.set(nodeMatrix);
		skeletonDebug = new SkeletonDebug(debugModel, mb);
		
	}
	
	private ModelInstance fetchModel(AssetManager assets){
		Model shipModel = assets.get("models/ship11.g3db", Model.class);
//		Model shipModel = assets.get("models/ship11.g3db", Model.class);
		ModelInstance ship1 = new ModelInstance(shipModel);
		return ship1;
	}
	
	private Brick produceBrick(){

		
		Collection<Ipoint> points = new LinkedList<Ipoint>();
		points.add(new Ipoint(844, 0));
		points.add(new Ipoint(665, 40));
		points.add(new Ipoint(531, 63));
		points.add(new Ipoint(397, 84));
		points.add(new Ipoint(129, 115));
		points.add(new Ipoint(-3, 122));
		points.add(new Ipoint(-135, 127));
		points.add(new Ipoint(-273, 123));
		points.add(new Ipoint(-417, 117));
		points.add(new Ipoint(-554, 110));
		points.add(new Ipoint(-713, 83));
		points.add(new Ipoint(-809, 49));
		
		points.add(new Ipoint(-857, 0));
		
		points.add(new Ipoint(-809, -49));
		points.add(new Ipoint(-714, -79));
		points.add(new Ipoint(-555, -106));
		points.add(new Ipoint(-418, -115));
		points.add(new Ipoint(-272, -123));
		points.add(new Ipoint(-135, -127));
		points.add(new Ipoint(-3, -122));
		points.add(new Ipoint(129, -115));
		points.add(new Ipoint(263, -101));
		points.add(new Ipoint(397, -84));
		points.add(new Ipoint(531, -63));
		points.add(new Ipoint(665, -40));
		ConvexityApproveHelper.applyConvexity(points);
		return new PointSetBrick(points);
	}

	public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
		for(ModelSubjectOperable subject: getStaff()){
			subject.getRenderables(renderables, pool);
		}
	}

	public String sourceType() {
		return SHIP_SOURCE_TYPE;
	}
	
	public CameraSatellite initializeCamera(){
		if(this.cameraSatellite == null){
			Camera camera = new PerspectiveCamera(37f, 1250f, 750f);
			camera.far = 50000;
			Point origin = this.origin().source;
			double rotation = this.getRotation();
			camera.translate(origin.getFX(), origin.getFY(), 2500f);
//			camera.translate(origin.getFX() - 2500, origin.getFY(), 1000f);
			camera.up.rotateRad((float)(rotation - Math.PI / 2), 0f, 0f, 100f);
//			camera.direction.rotate(80, 0f, -100f, 0f);*/
			camera.update();
			CameraSatellite cameraSatelliteK = new CameraSatellite(camera, getRotation());
			addSatellite(cameraSatelliteK);
			this.cameraSatellite = cameraSatelliteK;
		}
		return cameraSatellite;
	}
/*	
	public Map<String, OverlapStrategy> initOverlapStrategy() {
		Map<String, OverlapStrategy> ballStrategy = new HashMap<String, OverlapStrategy>();
//		ballStrategy.put(Island.ISLAND_SF_SOURCE, OverlapStrategy.TRUE);
		ballStrategy.put(SHIP_SOURCE_TYPE, OverlapStrategy.FALSE);
		ballStrategy.put(Ammunition.SHIP_AMMUNITION_TYPE, OverlapStrategy.FALSE);
		return ballStrategy;
	}
*/	
	@EventHandle(eventType = Island.ISLAND_SF_SOURCE)
	@OverlapCheck(algorithm = BrickOverlapAlgorithm.class, sourceType = Island.ISLAND_SF_SOURCE, strategyClass = OverlapStrategy.TrueOverlapStrategy.class)
	public void hitCannon(OverlapEvent e){
		this.rollBack(e.getEventTime());
	}

	@Override
	public void rollBack(long curTime){
		super.rollBack(curTime);
		this.removeHistory(BaseEvent.touchEventCode);
	}
	
	@Override
	public void outOfWorld(){
		super.outOfWorld();
		System.out.println(this.getlog());
	}
	
	private boolean que = true;
	
	@EventHandle(eventType = ExtentEventGroups.USER_SOURCE_TYPE)
	public void shoot(FireEvent e){
		if(que = !que){
			this.fire(e, 1, 0);
		}else{
			this.fire(e, 3, 2);
		}
	}
	
	@EventHandle(eventType = Ammunition.SHIP_AMMUNITION_TYPE)
	public void ammoHurt(OverlapEvent<?, ?, Vector3> event){
		System.out.println("Ship got ammo hurt: " + event.getTouchPoint());
	}
	
	private void fire(FireEvent e, int base, int cone){
		Ammunition ammo = new Ammunition();
		this.gunMark.calculateTransforms();
		Vector3 one = this.gunMark.getMark(cone);
		Vector3 two = this.gunMark.getMark(base);
		fireOrigin.source.set(two);
		ammo.translate(fireOrigin);
		helpVector.set(one.x - two.x, one.y - two.y, one.z - two.z);
		
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
		roll.setSpin(h2V, e.getEventTime());
		roll.setRotation((float) rad);
		ammo.applyRotation();
		
		//Sets ammo acceleration
		float ammoSpeed = Ammunition.prefs.getFloat("ship.ammo1.speed.directional", 1f);
		helpVector.nor().scl(ammoSpeed);
		ammo.getVector().source.set(helpVector);
		fireOrigin.source.set(0f, 0f, Ammunition.accelerationZ);
		ammo.setAcceleration(fireOrigin, e.getEventTime());
		
		//Sets ammo ratation
		h2V.set(helpVector.x, helpVector.y, helpVector.z + Ammunition.accelerationZ);
		scalar_mult = helpVector.x * h2V.x + helpVector.y * h2V.y + helpVector.z * h2V.z;
		cos = scalar_mult / (h2V.len() * helpVector.len());
		ammo.setRotationSpeed((float) Math.acos(cos));
		helpVector.crs(h2V);
		roll.setSpin(helpVector, e.getEventTime());
		//Set previous origin to current
//		ammo.previousOrigin.set(ammo.origin().source);
		ammo.applyEngine(this.getEngine());
	}

	public RenderableProvider debugModel() {
		return skeletonDebug;
	}
	
}
