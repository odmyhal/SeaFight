package com.odmyhal.sf.staff;

import org.bricks.core.entity.Point;
import org.bricks.extent.entity.CameraSatellite;
import org.bricks.extent.space.SpaceSubjectOperable;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class CameraShip extends Ship{

	private CameraSatellite cameraSatellite;
	
	public CameraShip(AssetManager assets) {
		super(assets);
	}
	
	public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
		super.getRenderables(renderables, pool);
		cameraSatellite.applyUpdates();
	}
	
	public CameraSatellite initializeCamera(){
		if(this.cameraSatellite == null){
			Camera camera = new PerspectiveCamera(37f, 1250f, 750f);
			camera.far = cameraPrefs.getFloat("camera.far", 40000f);
			System.out.println("Set camera far to " + camera.far);
			camera.near = 10;
			Point origin = this.origin().source;
			double rotation = this.getRotation();
			camera.translate(origin.getFX(), origin.getFY(), 2000f);
			camera.up.rotateRad((float)(rotation - Math.PI / 2), 0f, 0f, 100f);
			camera.lookAt(new Vector3(11500f, 6000f, 250f));
			camera.update();
			CameraSatellite cameraSatelliteK = new CameraSatellite(camera, getRotation());
			addSatellite(cameraSatelliteK);
			this.cameraSatellite = cameraSatelliteK;
		}
		return cameraSatellite;
	}

}
