package com.odmyhal.sf.process;

import org.bricks.core.entity.Fpoint;
import org.bricks.engine.help.RotationHelper;
import org.bricks.engine.neve.WalkPrint;
import org.bricks.engine.tool.Roll;
import org.bricks.extent.engine.processor.RollNodeToEntityHProcessor;
import org.bricks.extent.space.SpaceSubjectOperable;
import org.bricks.extent.subject.model.ModelBrickOperable;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.odmyhal.sf.staff.Ship;

public class ShipGunHRollProcessor extends RollNodeToEntityHProcessor<Ship, Ship>{
	
	private static final float stepBack = 150f;
	private final Vector3 rollPoint = new Vector3();
	private Matrix4 transform;

	public ShipGunHRollProcessor(Ship target) {
		super(target, "pushka");
		SpaceSubjectOperable<?, ?, Fpoint, Roll, ModelBrickOperable> sso = target.getStaff().get(0);
		transform = sso.modelBrick.linkTransform();
		this.setRotationSpeed(Ship.prefs.getFloat("ship.roll.speed.radians", 0.5f));
	}

	@Override
	protected void fetchButtPoint(Ship butt, Fpoint buttCentral) {
		WalkPrint<?, Fpoint> sp = butt.getSafePrint();
		Fpoint shipCenter = sp.getOrigin().source;
		double rotation = sp.getRotation();
		buttCentral.setX(shipCenter.getFX() - stepBack * (float) Math.cos(rotation));
		buttCentral.setY(shipCenter.getFY() - stepBack * (float) Math.sin(rotation));
		sp.free();
	}

	@Override
	protected void fetchRollOrigin(Fpoint rollCenter) {
		rollPoint.set(this.nodeOperator.linkPoint());
		rollPoint.mul(transform);
		rollCenter.set(rollPoint.x, rollPoint.y);
	}

	@Override
	protected float provideAbsoluteCurrentRotation() {
//		System.out.println("ship rotation " + this.checkEntity.getRotation() + ", pushka rotation: " + nodeOperator.rotatedRadians());
		float res = this.checkEntity.getRotation() + this.nodeOperator.rotatedRadians() - (float) Math.PI / 2;
		while(res < 0){
			res += RotationHelper.rotationCycle;
		}
		while(res >= RotationHelper.rotationCycle){
			res -= RotationHelper.rotationCycle;
		}
		return res;
	}

}
