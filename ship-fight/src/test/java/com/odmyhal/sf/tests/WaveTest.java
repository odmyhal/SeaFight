package com.odmyhal.sf.tests;

import org.bricks.core.help.AlgebraHelper;
import org.junit.Test;

import com.badlogic.gdx.math.Vector2;

public class WaveTest {

	private float xLen = 2500f;
	private float yLen = 2500f;
	private float maxDiffLen = 1770f;
	private float maxDiffQuad = maxDiffLen * maxDiffLen;
	
	private Vector2 lenVector(Vector2 center, Vector2 pos){
		Vector2 res = new Vector2(Math.abs(pos.x - center.x), Math.abs(pos.y - center.y));
		if(res.x > xLen / 2){
			res.x = xLen - res.x;
		}
		if(res.y > yLen / 2){
			res.y = yLen - res.y;
		}
		return res;
	}
	
	private float len(Vector2 v2){
		return (float) Math.sqrt(v2.x * v2.x + v2.y * v2.y);
	}
	
	private float altitude(float amplitude, float len){
		return AlgebraHelper.pow(len - maxDiffLen, 2) * amplitude / maxDiffQuad;
	}
	
	private void testPoint(Vector2 wave, float amplitude, Vector2 point){
		Vector2 lVector = lenVector(wave, point);
		float len = len(lVector);
		float altitude = altitude(amplitude, len);
		System.out.println("Point " + point + ": lenVector " + lVector + ", len " + len + ", altitude " + altitude);
	}
	
	@Test
	public void waveTest(){
		float amplitude = 310;
		Vector2 wave = new Vector2(2495f, 20f);
		Vector2 corner1 = new Vector2(0f, 0f);
		Vector2 corner2 = new Vector2(0f, 2500f);
		Vector2 corner3 = new Vector2(2500f, 2500f);
		Vector2 corner4 = new Vector2(2500f, 0f);
		Vector2 side1 = new Vector2(1333f, 2500f);
		Vector2 side2 = new Vector2(1333f, 0f);
		testPoint(wave, amplitude, corner1);
		testPoint(wave, amplitude, corner2);
		testPoint(wave, amplitude, corner3);
		testPoint(wave, amplitude, corner4);
		System.out.println("------------sides----------------");
		testPoint(wave, amplitude, side1);
		testPoint(wave, amplitude, side2);
		
		String s = "Formatted value is %.2f";
		System.out.println(String.format(s, 26544f));
	}
}
