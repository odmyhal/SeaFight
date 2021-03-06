package org.bricks.engine.overlap;

import org.bricks.engine.event.overlap.OverlapStrategyRegistrator;
import org.bricks.engine.event.overlap.OSRegister;

public class OSRegistrator implements OverlapStrategyRegistrator{

	public void registerStrategies() {
			OSRegister.registerStrategy(com.odmyhal.sf.staff.Ammunition.class, "ShipSource@sf.odmyhal.com", 
			   new org.bricks.engine.event.overlap.OverlapStrategy.TrueOverlapStrategy(new org.bricks.extent.space.overlap.LineCrossMBAlgorithm(), new org.bricks.engine.staff.Subject.SubjectPrintExtractor(), new org.bricks.engine.event.PrintOverlapEvent.PrintOverlapEventExtractor()));
			OSRegister.registerStrategy(com.odmyhal.sf.staff.Ship.class, "ShipSource@sf.odmyhal.com", 
			   new org.bricks.engine.event.overlap.SmallEventStrategy(new org.bricks.engine.event.overlap.BrickOverlapAlgorithm(), new org.bricks.engine.staff.Subject.SubjectPrintExtractor(), new org.bricks.engine.event.PrintOverlapEvent.PrintOverlapEventExtractor()));
			OSRegister.registerStrategy(com.odmyhal.sf.staff.Ship.class, "IslandSource@sf.odmyhal.com", 
			   new org.bricks.engine.event.overlap.OverlapStrategy.TrueOverlapStrategy(new org.bricks.engine.event.overlap.BrickOverlapAlgorithm(), new org.bricks.engine.staff.Subject.SubjectPrintExtractor(), new org.bricks.engine.event.PrintOverlapEvent.PrintOverlapEventExtractor()));
			OSRegister.registerStrategy(com.odmyhal.sf.staff.Ammunition.class, "IslandSource@sf.odmyhal.com", 
			   new org.bricks.engine.event.overlap.OverlapStrategy.TrueOverlapStrategy(new org.bricks.extent.space.overlap.LineCrossMBAlgorithm(), new org.bricks.engine.staff.Subject.SubjectPrintExtractor(), new org.bricks.engine.event.PrintOverlapEvent.PrintOverlapEventExtractor()));
		}
}