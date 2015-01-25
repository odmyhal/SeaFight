package com.odmyhal.sf.control;

import org.bricks.engine.staff.Roller;
import org.bricks.engine.tool.Roll;
import org.bricks.enterprise.control.widget.tool.RotationDependAction.RotationConsumer;
import org.bricks.enterprise.control.widget.tool.RotationDependAction.RotationProvider;
import org.bricks.extent.control.RollEntityAction;

public class ShipRollAction extends RollEntityAction{

	private static float halfPI = (float) Math.PI / 2;
	
	public RotationConsumer nord, direction;
	
	private boolean initialized = false;

	public ShipRollAction(Roller target, RotationProvider rotationProvider,
			float rotationSpeed) {
		super(target, rotationProvider, rotationSpeed);
	}

	@Override
	public boolean act(float delta) {
		boolean stop = false;
		if(initialized){
			super.act(delta);
			initialized = false;
		}
		float curRad = rotationProvider.provideRotation();
		float diffRad = curTargetRad - curRad;
		
		
		if(curSpeedRad >= 0){
			if(diffRad >= Math.PI){
				diffRad -= Roll.rotationCycle;
			}
			if(diffRad <= 0){
				stop = true;;
			}
		}
		if(curSpeedRad < 0){
			if(diffRad <= -Math.PI){
				diffRad += Roll.rotationCycle;
			}
			if(diffRad >= 0){
				stop = true;
			}
		}
		nord.consumeRotation(halfPI - curRad);
		direction.consumeRotation(diffRad);
		return stop;
	}
	
	@Override
	protected void initNewRotation(float targetRad, float currentRad){
		float curDiffRad = targetRad - halfPI;
		targetRad = currentRad + curDiffRad;
		while(targetRad >= rotationCycle){
			targetRad -= rotationCycle;
		}
		while(targetRad < 0){
			targetRad += rotationCycle;
		}
		super.initNewRotation(targetRad, currentRad);
		initialized = true;
	}

}
