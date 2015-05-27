package com.odmyhal.sf.bot;

import org.bricks.engine.event.check.CheckerType;
import org.bricks.engine.processor.ApproveProcessor;
import org.bricks.engine.processor.tool.Approver;
import org.bricks.engine.processor.tool.TimerApprover;

import com.odmyhal.sf.staff.Ship;

public class ShipFightProcessor extends ApproveProcessor<Ship> {
	
	private static final CheckerType CHECKER_TYPE = CheckerType.registerCheckerType();
	private TimerApprover<Ship> timer;

	public ShipFightProcessor(TimerApprover<Ship> timer, Approver<Ship>... approvers) {
		super(CHECKER_TYPE, approvers);
		this.timer = timer;
		this.addApprover(timer);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void process(Ship ship, long curTime) {
		ship.fire(curTime);
		timer.setCheckTime(curTime);
	}

}
