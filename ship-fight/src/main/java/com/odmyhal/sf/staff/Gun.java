package com.odmyhal.sf.staff;

import org.bricks.engine.tool.Origin;
import org.bricks.enterprise.inform.Informator;
import org.bricks.extent.processor.tbroll.RollNodeToWalkerVProcessor;
import org.bricks.extent.space.Origin3D;
import org.bricks.extent.space.Roll3D;
import org.bricks.extent.space.overlap.MarkPoint;
import org.bricks.extent.subject.model.NodeOperator;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class Gun {
	
	private Origin<Vector3> fireOrigin = new Origin3D();
	private Vector3 helpVector = new Vector3();
	private Vector3 h2V = new Vector3();
	private Quaternion helpQ = new Quaternion();

	private MarkPoint gunMark;
	private long gunMarkTime = 0;
	private boolean fireQue = true;
	private Ship ship;
	private NodeOperator hOperator, vOperator;
	
	public Gun(Ship ship, NodeOperator horizontalOperator, NodeOperator verticalOperator, Vector3 gun1, Vector3 gun2, Vector3 gunOrigin){
		gunMark = new MarkPoint(gun1, gun2, gunOrigin);
		this.ship = ship;
		this.hOperator = horizontalOperator;
		this.vOperator = verticalOperator;
		gunMark.addTransform(ship.getStaff().get(0).modelBrick.linkTransform());
		gunMark.addTransform(hOperator.getNodeData().linkTransform());
		gunMark.addTransform(vOperator.getNodeData().linkTransform());
	}
/*	
	public void addTransform(Matrix4 matrix){
		gunMark.addTransform(matrix);
	}
*/	
	public Vector3 gunOrigin(long procTime){
		return getGunPoint(2, procTime);
	}

	public Vector3 getGunPoint(int num, long procTime){
		if(gunMarkTime < procTime){
			gunMark.calculateTransforms();
			gunMarkTime = procTime;
		}
		return gunMark.getMark(num);
	}
	
	public void fire(long fireTime){
		if(fireQue = !fireQue){
			this.fire(0, fireTime);
		}else{
			this.fire(1, fireTime);
		}
	}
	
	private void fire(int base, long fireTime){
		Ammunition ammo = Ammunition.get();
		ammo.setMyShip(ship);
		this.gunMark.calculateTransforms();
		Vector3 baseVector = this.getGunPoint(base, fireTime);
		
		fireOrigin.source.set(baseVector);
		ammo.translate(fireOrigin);
//		ammo.startTime = fireTime;
//		Informator.log(String.format("AmmoStart: %s, Acceleration: %.5f", fireOrigin.source, Ammunition.accelerationZ));

		double hRad = ship.getRotation() + hOperator.rotatedRadians() - Math.PI / 2;
		double vRad = Math.PI - vOperator.rotatedRadians();
		helpVector.set((float)(1000f * Math.cos(hRad)), (float)(1000 * Math.sin(hRad)), (float)(1000 * Math.tan(vRad)));
		
//		helpVector.set(1000 * Math.cos(hRad) * Math.cos(vRad), 1000f * Math.sin(hRad) * Math.cos(vRad), 1000f * (float) Math.tan(vRad));
		
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
//		String data = String.format("Shoot: direction: %s , speed: %.2f", helpVector, ammoSpeed);
		helpVector.scl(ammoSpeed);
//		data += " , vector " + helpVector;
//		Informator.log(RollNodeToWalkerVProcessor.testTargetVRotation);
//		Informator.log(data);
//		Informator.log("---------------------------------------");
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
		ammo.applyEngine(ship.getEngine());
	}
}
