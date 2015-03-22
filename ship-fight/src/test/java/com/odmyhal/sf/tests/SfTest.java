package com.odmyhal.sf.tests;


import org.bricks.core.entity.Fpoint;
import org.bricks.core.entity.Ipoint;
import org.bricks.core.entity.Point;
import org.junit.Test;

public class SfTest {

	@Test
	public void sectorChoose(){
		int sectorLen = 5000;
		Point point = new Fpoint(9999.71191f, 5580.43652f);
		int rowNumber = (int) Math.floor(point.getFY() / sectorLen);
		int colNumber = (int) Math.floor(point.getFX() / sectorLen);
	}
	
	private static int provideSectorMask(Point point){
		int curMask = 0;
		Ipoint dimm = new Ipoint(5000, 5000);
		Ipoint corner = new Ipoint(5000, 5000);
		int luft = 2000;
		
		if(point.getX() < corner.getX()){
			curMask += 64;
		}else if(point.getX() < corner.getX() + luft){
			curMask += 4;
		}else if(point.getX() >= corner.getX() + dimm.getFX()){
			curMask += 16;
		}else if(point.getX() >= corner.getX() + dimm.getFX() - luft){
			curMask += 1;
		}
		
		if(point.getY() < corner.getY()){
			curMask += 128;
		}else if(point.getY() < corner.getY() + luft){
			curMask += 8;
		}else if(point.getY() >= corner.getY() + dimm.getY()){
			curMask += 32;
		}else if(point.getY() >= corner.getY() + dimm.getY() - luft){
			curMask += 2;
		}
		return curMask;
	}
}
