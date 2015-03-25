package com.odmyhal.sf.model;

import org.bricks.core.entity.type.Brick;
import org.bricks.exception.Validate;
import org.bricks.extent.entity.mesh.ModelSubjectOperable;
import org.bricks.extent.entity.mesh.ModelSubjectPrint;
import org.bricks.extent.subject.model.ModelBrickOperable;
import org.bricks.extent.subject.model.NodeOperator;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Vector3;
import com.odmyhal.sf.staff.Ship;

public class ShipSubject extends ModelSubjectOperable<Ship, ModelSubjectPrint, ModelBrickOperable>{

	
	public ShipSubject(Brick brick, ModelInstance modelInstance){
		super(brick, modelInstance);
		NodeOperator pushkaOperator = this.modelBrick.addNodeOperator("pushka", "Dummypushka1");
		pushkaOperator.setPoint(new Vector3(380f, 0f, 0f));
		pushkaOperator.setSpin(new Vector3(0f, 0f, 99f));
		pushkaOperator.setRotatedRadians((float) Math.PI / 2);
		
		NodeOperator stvolOperator = this.modelBrick.addNodeOperator("stvol", "Dummypushka1/pushka_garmaty1");
		stvolOperator.setPoint(new Vector3(20f, 8f, 0f));
		stvolOperator.setSpin(new Vector3(0f, 0f, -99f));
		stvolOperator.setRotatedRadians((float) Math.PI );
		this.modelBrick.adjustCurrentPrint();
	}
}
