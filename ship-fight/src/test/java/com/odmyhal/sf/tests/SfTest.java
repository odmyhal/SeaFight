package com.odmyhal.sf.tests;


import org.junit.Test;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class SfTest {
	
//	@Test
	public void rotateTest(){
		
		Vector3 point = new Vector3(0f, 1f, 0f);
		Matrix4 matrix = new Matrix4();
		
		Quaternion rotation = new Quaternion();
		Vector3 translation = new Vector3();
		Vector3 scale = new Vector3();
		
		rotation.setFromAxis(0f, 0f, 10f, -90f);
		translation.set(1f, 1f, 0f);
//		scale.set(1f, 1f, 1f);
		scale.set(3f, 2f, 537f);
		
		matrix.set(translation, rotation, scale);
		point.mul(matrix);
		
		System.out.println("Result point: " + Math.sqrt(80.0 * 80.0 * 2));
	}
	
	@Test
	public void floatTest(){
		float one = 5.5f;
		float two = -5.5f;
		System.out.println("One: " + one + ", " + ((int)one));
		System.out.println("Two: " + two + ", " + ((int)two));
	}
}
