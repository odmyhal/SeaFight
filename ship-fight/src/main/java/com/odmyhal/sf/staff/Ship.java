package com.odmyhal.sf.staff;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import org.bricks.core.entity.Ipoint;
import org.bricks.core.entity.impl.PointSetBrick;
import org.bricks.core.entity.type.Brick;
import org.bricks.engine.event.check.OwnEventChecker;
import org.bricks.engine.event.overlap.OverlapStrategy;
import org.bricks.engine.item.MultiWalkRoller;
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
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class Ship extends MultiWalkRoller<ModelSubject> implements RenderableProvider {
	
	public static final String SHIP_SOURCE_TYPE = "ShipSource@sf.odmyhal.com";
	private CameraSatellite cameraSatellite;

	public Ship(AssetManager assets) {
		if(!assets.update()){
			throw new RuntimeException("Accets not ready yet");
		}
		Brick brick = produceBrick();
		ModelInstance modelInstance = fetchModel(assets);
		for(Node node : modelInstance.nodes){
			Matrix4 helpMatrix = new Matrix4();
			helpMatrix.idt().setToRotation(10f, 0f, 0f, -90f).tra();
			node.globalTransform.mulLeft(helpMatrix);
		}
		ModelSubject<Ship> subject = new ModelSubject<Ship>(brick, modelInstance);
		this.addSubject(subject);
		this.registerEventChecker(OwnEventChecker.instance());
	}
	
	private ModelInstance fetchModel(AssetManager assets){
		Model shipModel = assets.get("models/ship1.g3db", Model.class);
		ModelInstance ship1 = new ModelInstance(shipModel);
		return ship1;
	}
	
	private Brick produceBrick(){
		Collection<Ipoint> points = new LinkedList<Ipoint>();
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
		points.add(new Ipoint(59, -2));
		return new PointSetBrick(points);
	}

	public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
		for(ModelSubject subject: getStaff()){
			subject.getRenderables(renderables, pool);
		}
	}

	public Map<String, OverlapStrategy> initOverlapStrategy() {
		// TODO Auto-generated method stub
		return null;
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

}
