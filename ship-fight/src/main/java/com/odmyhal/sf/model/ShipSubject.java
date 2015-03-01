package com.odmyhal.sf.model;

import org.bricks.core.entity.type.Brick;
import org.bricks.exception.Validate;
import org.bricks.extent.entity.mesh.ModelSubjectOperable;
import org.bricks.extent.entity.mesh.ModelSubjectPrint;
import org.bricks.extent.entity.mesh.NodeOperator;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.odmyhal.sf.staff.Ship;

public class ShipSubject extends ModelSubjectOperable<Ship, ModelSubjectPrint>{

	
	public ShipSubject(Brick brick, ModelInstance modelInstance){
		super(brick, modelInstance);
//		NodeOperator pushka_operator = this.addNodeOperator("pushka", "Dummypushkagarmaty", "Dummypushkaosnova");
		NodeOperator pushka_operator = this.addNodeOperator("pushka", "pushka_garmaty", "pushka_osnova");
		Validate.isFalse(pushka_operator == null, "Ship model should comprise \"pushka_garmaty\" and \"pushka_osnova\" nodes");
		pushka_operator.setPoint(new Vector3(10f, 10f, 11f));
		pushka_operator.setSpin(new Vector3(0f, 10f, 0f));
		pushka_operator.setRotatedRadians((float) Math.PI / 2);
//		System.out.println("Ship subject created");
	}
}
