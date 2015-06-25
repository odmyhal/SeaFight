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
		pushkaOperator.setPoint(new Vector3(380f, 0f, 113f));
		pushkaOperator.setSpin(new Vector3(0f, 0f, 999f));
		pushkaOperator.setRotatedRadians((float) Math.PI / 2);
		
		NodeOperator stvolOperator = this.modelBrick.addNodeOperator("stvol", "Dummypushka1/pushka_garmaty1");
		stvolOperator.setPoint(new Vector3(19.917027f, 7.8888726f, 6.5233417f));
		stvolOperator.setSpin(new Vector3(0f, 0f, -999f));
//		stvolOperator.setPoint(new Vector3(18.792099f, 7.9068465f, 6.8015304f));
//		stvolOperator.setSpin(new Vector3(-37.032307f, -3.395703f, 106.724014f));
		stvolOperator.setRotatedRadians((float) Math.PI );
		
		
		NodeOperator backPushkaOperator = this.modelBrick.addNodeOperator("backPushka", "Dummypushka2");
//		backPushkaOperator.setPoint(new Vector3(-470f, 0f, 110f));
		backPushkaOperator.setPoint(new Vector3(-455f, 0f, 110f));
		backPushkaOperator.setSpin(new Vector3(0f, 0f, 999f));
		backPushkaOperator.setRotatedRadians((float) -Math.PI / 2);
		
		NodeOperator backStvolOperator = this.modelBrick.addNodeOperator("backStvol", "Dummypushka2/pushka_garmaty2");
		backStvolOperator.setPoint(new Vector3(-174.66116f, 69.33785f, 69.88556f));
//		backStvolOperator.setPoint(new Vector3(-5.157566E7f, -1.1712775E7f, 8465533.0));
		backStvolOperator.setSpin(new Vector3(0f, -999f, 0f));
		backStvolOperator.setRotatedRadians((float) Math.PI );

		this.setPrintFactory(SSPlanePrint.printFactory);
//		this.modelBrick.adjustCurrentPrint();
		
	}
}
