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
import org.bricks.engine.view.SubjectView;
import org.bricks.engine.view.WalkView;
import org.bricks.exception.Validate;
import org.bricks.extent.entity.CameraSatellite;
import org.bricks.extent.entity.subject.ModelSubject;
import org.bricks.extent.event.ExtentEventGroups;
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

public class Ship extends MultiWalkRoller<ModelSubject> implements RenderableProvider {
	
	public static final String SHIP_SOURCE_TYPE = "ShipSource@sf.odmyhal.com";
	private CameraSatellite cameraSatellite;

	public Ship(AssetManager assets) {
		if(!assets.update()){
			throw new RuntimeException("Accets not ready yet");
		}
		Brick brick = produceBrick();
		ModelInstance modelInstance = fetchModel(assets);
/*		Matrix4 helpMatrix = new Matrix4();
		helpMatrix.idt().setToRotation(10f, 0f, 0f, -90f).tra();
//		Matrix4 pushkaMatrix = new Matrix4();
//		pushkaMatrix.idt().setToRotation(0f, 0f, 10f, 45f).tra();
		for(Node node : modelInstance.nodes){
			node.globalTransform.mulLeft(helpMatrix);
			System.out.println("Transformed node " + node.id);
//			if(node.id.equals("pushka_garmaty") || node.id.equals("pushka_osnova")){
//				node.globalTransform.trn(-14f, 0f, -9f);
//				System.out.println(node.globalTransform);
//				node.globalTransform.trn(-8f, 4f, 0f);
//				node.globalTransform.mulLeft(pushkaMatrix);
//				node.globalTransform.trn(8f, -4f, 0f);
//			}
		}*/

		Quaternion q = new Quaternion();
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
		}
		ModelSubject<Ship> subject = new ShipSubject(brick, modelInstance);
		this.addSubject(subject);
		registerEventChecker(OverlapChecker.instance());
	}
	
	private ModelInstance fetchModel(AssetManager assets){
		Model shipModel = assets.get("models/ship7.g3db", Model.class);
//		Model shipModel = assets.get("models/ship_4.g3db", Model.class);
		ModelInstance ship1 = new ModelInstance(shipModel);
		return ship1;
	}
	
	private Brick produceBrick(){
/*		Collection<Ipoint> points = new LinkedList<Ipoint>();
		points.add(new Ipoint(75, 0));
		points.add(new Ipoint(59, 2));
		points.add(new Ipoint(47, 3));
		points.add(new Ipoint(35, 5));
		points.add(new Ipoint(23, 7));
		points.add(new Ipoint(11, 9));
		points.add(new Ipoint(0, 10));
		points.add(new Ipoint(-11, 10));
		points.add(new Ipoint(-24, 10));
		points.add(new Ipoint(-36, 9));
		points.add(new Ipoint(-48, 9));
		points.add(new Ipoint(-62, 6));
		points.add(new Ipoint(-71, 4));
		points.add(new Ipoint(-75, 0));
		points.add(new Ipoint(-71, -4));
		points.add(new Ipoint(-62, -6));
		points.add(new Ipoint(-48, -9));
		points.add(new Ipoint(-36, -9));
		points.add(new Ipoint(-24, -10));
		points.add(new Ipoint(-11, -10));
		points.add(new Ipoint(0, -10));
		points.add(new Ipoint(11, -9));
		points.add(new Ipoint(23, -7));
		points.add(new Ipoint(35, -5));
		points.add(new Ipoint(47, -3));
		points.add(new Ipoint(59, -2));*/
		
		Collection<Ipoint> points = new LinkedList<Ipoint>();
		points.add(new Ipoint(75, 0));
		points.add(new Ipoint(64, 2));
		points.add(new Ipoint(58, 3));
		points.add(new Ipoint(45, 5));
		points.add(new Ipoint(31, 7));
		points.add(new Ipoint(16, 9));
		points.add(new Ipoint(0, 10));
		points.add(new Ipoint(-24, 10));
		points.add(new Ipoint(-38, 9));
		points.add(new Ipoint(-48, 8));
		points.add(new Ipoint(-62, 6));
		points.add(new Ipoint(-71, 4));
		
		points.add(new Ipoint(-75, 0));
		
		points.add(new Ipoint(-71, -4));
		points.add(new Ipoint(-62, -6));
		points.add(new Ipoint(-48, -8));
		points.add(new Ipoint(-38, -9));
		points.add(new Ipoint(-24, -10));
		points.add(new Ipoint(0, -10));
		points.add(new Ipoint(16, -9));
		points.add(new Ipoint(31, -7));
		points.add(new Ipoint(45, -5));
		points.add(new Ipoint(58, -3));
		points.add(new Ipoint(64, -2));
		ConvexityApproveHelper.applyConvexity(points);
		return new PointSetBrick(points);
	}

	public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
		for(ModelSubject subject: getStaff()){
			subject.getRenderables(renderables, pool);
		}
	}

	public String sourceType() {
		return SHIP_SOURCE_TYPE;
	}
	
	public CameraSatellite initializeCamera(){
		if(this.cameraSatellite == null){
			Camera camera = new PerspectiveCamera(37f, 1250f, 750f);
			camera.far = 5000;
			Ipoint origin = this.getOrigin();
			double rotation = this.getRotation();
			camera.translate(origin.getFX(), origin.getFY(), 250f);
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
}
