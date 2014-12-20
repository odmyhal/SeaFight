package com.odmyhal.sf.control;

import org.bricks.engine.staff.Roller;
import org.bricks.engine.tool.Roll;
import org.bricks.enterprise.control.widget.tool.RotationDependAction.RotationConsumer;
import org.bricks.enterprise.control.widget.tool.RotationDependAction.RotationProvider;
import org.bricks.extent.control.RollEntityAction;

public class ShipRollAction extends RollEntityAction{
	
	public RotationConsumer nord, direction;
	
	private boolean inProgress = false, initialized = false;
	private float startRotationRad;

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
				inProgress = false;
				stop = true;;
			}
		}
		if(curSpeedRad < 0){
			if(diffRad <= -Math.PI){
				diffRad += Roll.rotationCycle;
			}
			if(diffRad >= 0){
				inProgress = false;
				stop = true;
			}
		}
		nord.consumeRotation((float)(Math.PI / 2) - curRad);
		direction.consumeRotation(diffRad);
		return stop;
	}
	
	@Override
	protected void initNewRotation(float targetRad, float currentRad){
		if(inProgress){
			targetRad -= currentRad - startRotationRad;
		}else{
			inProgress = true;
		}
		startRotationRad = currentRad;
		super.initNewRotation(targetRad, currentRad);
		initialized = true;
	}
}
