package com.odmyhal.sf.model;

import org.bricks.core.entity.Fpoint;
import org.bricks.engine.tool.Roll;
import org.bricks.extent.space.SSPlanePrint;
import org.bricks.extent.space.SpaceSubjectOperable;
import org.bricks.extent.subject.model.MBSVehicle;
import org.bricks.extent.subject.model.ModelBrickOperable;
import org.bricks.extent.subject.model.NodeOperator;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.odmyhal.sf.staff.Ship;

public class ShipSubject extends SpaceSubjectOperable<Ship, SSPlanePrint, Fpoint, Roll, ModelBrickOperable>{

	
	public ShipSubject(ModelInstance modelInstance){
		super(new MBSVehicle.Plane(), modelInstance, new Vector3(-108f, 0f, 0f));
		NodeOperator pushkaOperator = this.modelBrick.addNodeOperator("pushka", "Dummypushka1");
		pushkaOperator.setPoint(new Vector3(380f, 0f, 0f));
		pushkaOperator.setSpin(new Vector3(0f, 0f, 99f));
		pushkaOperator.setRotatedRadians((float) Math.PI / 2);
		
		NodeOperator stvolOperator = this.modelBrick.addNodeOperator("stvol", "Dummypushka1/pushka_garmaty1");
		stvolOperator.setPoint(new Vector3(20f, 8f, 0f));
		stvolOperator.setSpin(new Vector3(0f, 0f, -99f));
		stvolOperator.setRotatedRadians((float) Math.PI );

		this.setPrintFactory(SSPlanePrint.printFactory);
//		this.modelBrick.adjustCurrentPrint();
		
	}
}
