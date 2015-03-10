package com.odmyhal.sf.staff;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.bricks.core.entity.Fpoint;
import org.bricks.core.entity.Ipoint;
import org.bricks.core.entity.Point;
import org.bricks.core.entity.impl.PointSetBrick;
import org.bricks.core.entity.type.Brick;
import org.bricks.core.help.ConvexityApproveHelper;
import org.bricks.engine.event.BaseEvent;
import org.bricks.engine.event.OverlapEvent;
import org.bricks.engine.event.check.OverlapChecker;
import org.bricks.engine.event.check.OwnEventChecker;
import org.bricks.engine.event.overlap.OverlapStrategy;
import org.bricks.engine.help.VectorSwapHelper;
import org.bricks.engine.item.MultiWalkRoller;
import org.bricks.engine.neve.WalkPrint;
import org.bricks.exception.Validate;
import org.bricks.extent.entity.CameraSatellite;
import org.bricks.extent.entity.mesh.ModelSubjectOperable;
import org.bricks.extent.entity.mesh.ModelSubjectPrint;
import org.bricks.extent.event.ExtentEventGroups;
import org.bricks.extent.tool.MarkPoint;
import org.bricks.annotation.EventHandle;

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

public class Ship extends MultiWalkRoller<ModelSubjectOperable, WalkPrint> implements RenderableProvider {
	
	public static final String SHIP_SOURCE_TYPE = "ShipSource@sf.odmyhal.com";
	private CameraSatellite cameraSatellite;
	public MarkPoint gunMark;

	public Ship(AssetManager assets) {
		if(!assets.update()){
			throw new RuntimeException("Accets not ready yet");
		}
		Brick brick = produceBrick();
		ModelInstance modelInstance = fetchModel(assets);


/*		Quaternion q = new Quaternion();
		q.setFromAxis(10f, 0f, 0f, 90f);
		Matrix4 rMatrix = new Matrix4();
		q.toMatrix(rMatrix.val);
		for(Node node : modelInstance.nodes){
			node.translation.mul(rMatrix);
			node.rotation.mul(q);
			if(node.id.equals("pushka_garmaty") || node.id.equals("pushka_osnova")){
				node.translation.add(-14f, 0f, -9f);
			}
			node.calculateTransforms(true);
		}*/
		
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
		ModelSubjectOperable<Ship, ModelSubjectPrint> subject = new ShipSubject(brick, modelInstance);
		this.addSubject(subject);
		
		gunMark = new MarkPoint(
				new Vector3(18.131077f,  157.869476f,  5.421094f), 
				new Vector3(25.742855f,  164.275574f,  5.676825f), 
				new Vector3(38.976746f,  154.729126f,  6.235388f), 
				new Vector3(25.742855f,  157.869461f,  6.266388f));
		gunMark.addTransform(subject.getNodeOperator("stvol").getNodeData("pushka_garmaty1").linkTransform());
		gunMark.addTransform(subject.getNodeOperator("pushka").getNodeData("Dummypushka1").linkTransform());
		gunMark.addTransform(subject.linkTransform());
		
		registerEventChecker(OverlapChecker.instance());
	}
	
	private ModelInstance fetchModel(AssetManager assets){
		Model shipModel = assets.get("models/ship11.g3db", Model.class);
//		Model shipModel = assets.get("models/ship_4.g3db", Model.class);
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
			Ipoint origin = this.getOrigin();
			double rotation = this.getRotation();
			camera.translate(origin.getFX(), origin.getFY(), 2500f);
			camera.up.rotateRad((float)(rotation - Math.PI / 2), 0f, 0f, 100f);
			camera.update();
			CameraSatellite cameraSatelliteK = new CameraSatellite(camera, this);
			addSatellite(cameraSatelliteK);
			this.cameraSatellite = cameraSatelliteK;
		}
		return cameraSatellite;
	}
	
	public Map<String, OverlapStrategy> initOverlapStrategy() {
		Map<String, OverlapStrategy> ballStrategy = new HashMap<String, OverlapStrategy>();
		ballStrategy.put(Island.ISLAND_SF_SOURCE, OverlapStrategy.TRUE);
		ballStrategy.put(SHIP_SOURCE_TYPE, OverlapStrategy.FALSE);
		return ballStrategy;
	}
	
	@EventHandle(eventType = Island.ISLAND_SF_SOURCE)
	public void hitCannon(OverlapEvent e){
		this.rollBack(e.getEventTime());
/*		SubjectView myView = e.getTargetView();
		System.out.println("ship: " + myView.getPoints());
		System.out.println("ship center: " + myView.getCenter());
		
		SubjectView stoneView = e.getSourceView();
		System.out.println("stone: " + stoneView.getPoints());
		System.out.println("Stone center: " + stoneView.getCenter());
		
		System.out.println("Touch: " + e.getTouchPoint());*/
//		this.removeHistory(BaseEvent.touchEventCode);
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
}
