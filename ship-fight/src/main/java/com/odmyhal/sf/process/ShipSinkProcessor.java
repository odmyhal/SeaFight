package com.odmyhal.sf.process;

import org.bricks.core.entity.Fpoint;
import org.bricks.engine.event.check.AccelerateToSpeedProcessorChecker;
import org.bricks.engine.event.check.CheckerType;
import org.bricks.engine.event.check.ChunkEventChecker;
import org.bricks.engine.event.check.EventChecker;
import org.bricks.engine.event.check.RollToMarkProcessorChecker;
import org.bricks.engine.event.check.RouteChecker;
import org.bricks.engine.processor.GetOutProcessor;
import org.bricks.engine.processor.ImmediateActProcessor;
import org.bricks.engine.processor.Processor;
import org.bricks.engine.processor.SingleActProcessor;
import org.bricks.engine.tool.Origin;
import org.bricks.engine.tool.Origin2D;
import org.bricks.extent.processor.NodeModifyProcessor;
import org.bricks.extent.processor.RollModelBrickProcessor;
import org.bricks.extent.processor.TranslateModelBrickProcessor;

import com.badlogic.gdx.math.Vector3;
import com.odmyhal.sf.bot.ShipFightProcessor;
import com.odmyhal.sf.staff.Ship;

public class ShipSinkProcessor extends ChunkEventChecker<Ship>{
	
	public static final CheckerType CH_TYPE = CheckerType.registerCheckerType();
	RollModelBrickProcessor<Ship> rmbp = new RollModelBrickProcessor.FpointCentral<Ship>();

	public ShipSinkProcessor(Processor<Ship>... beforeGetOut) {
		super(CH_TYPE);
		this.addAbort(CH_TYPE);
		this.addSupplant(NodeModifyProcessor.CHECKER_TYPE);
		this.addAbort(NodeModifyProcessor.CHECKER_TYPE);
		this.addSupplant(RollToMarkProcessorChecker.CHECKER_TYPE);
		this.addAbort(RollToMarkProcessorChecker.CHECKER_TYPE);
		this.addSupplant(ShipFightProcessor.CHECKER_TYPE);
		this.addAbort(ShipFightProcessor.CHECKER_TYPE);
		this.addSupplant(RouteChecker.CHECKER_TYPE);
		this.addAbort(RouteChecker.CHECKER_TYPE);

		this.addProcessor(new FirstProcessor());

/*		double rad = ship.getRotation() - Math.PI / 2;
		Vector3 hSpeen = new Vector3((float) Math.cos(rad) * 1000f, (float) Math.sin(rad) * 1000f, 0f);
		rmbp.init(hSpeen, (float)(60d * Math.PI / 180), 0.3f);*/
//		this.addProcessor(rmbp);
		
		TranslateModelBrickProcessor<Ship> tmbp = new TranslateModelBrickProcessor<Ship>();
		tmbp.init(new Vector3(0f, 0f, -500f), 3000);
		this.addProcessor(tmbp);
		TranslateModelBrickProcessor<Ship> tmbp2 = new TranslateModelBrickProcessor<Ship>();
		tmbp2.init(new Vector3(0f, 0f, -1500f), 3000);
		this.addProcessor(tmbp2);
		for(Processor<Ship> chk : beforeGetOut){
			this.addProcessor(chk);
		}
		this.addProcessor(GetOutProcessor.instance());
	}
	
	@Override
	public void activate(Ship ship, long curTime){
		double rad = ship.getRotation() - Math.PI / 2;
		Vector3 hSpeen = new Vector3((float) Math.cos(rad) * 1000f, (float) Math.sin(rad) * 1000f, 0f);
		rmbp.init(hSpeen, (float)(40d * Math.PI / 180), 0.2f);
		super.activate(ship, curTime);
	}
	

	private static final Origin<Fpoint> stor = new Origin2D(new Fpoint(0f, 0f));
	private class FirstProcessor extends ImmediateActProcessor<Ship>{
		
		@Override
		protected void processSingle(Ship target, long processTime) {
			target.setRotationSpeed(0f);
			target.setAcceleration(stor, processTime);
			AccelerateToSpeedProcessorChecker asp = new AccelerateToSpeedProcessorChecker();
			asp.init(Ship.prefs.getFloat("ship.acceleration.directional", 50f), 0f);
			target.registerEventChecker(asp);
			target.registerEventChecker(rmbp);
		}
		
		
	}

}
