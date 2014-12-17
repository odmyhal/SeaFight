package com.odmyhal.sf.control;

import org.bircks.enterprise.control.panel.AnimationRisePanel;
import org.bircks.enterprise.control.panel.Skinner;
import org.bricks.enterprise.control.widget.tool.FlowTouchPad;
import org.bricks.enterprise.control.widget.tool.FlowWidgetProvider;
import org.bricks.extent.control.MarkRollAction;
import org.bricks.extent.control.RollEntityAction;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.odmyhal.sf.staff.Ship;

public class ShipMovePanel extends AnimationRisePanel{
	
	private Ship ship;

	public ShipMovePanel(Ship ship){
		this.ship = ship;
	}
	
	@Override
	protected void initStage(){
		super.initStage();
		stack.add(controlPanel());
	}

	private Table controlPanel(){
		Table controlPanel = new Table();
		controlPanel.left().top();
		controlPanel.pad(10f);
		Label l = new Label("Moveing control panel", Skinner.instance().skin(), "default");
		controlPanel.add(l).pad(5).top().left();
		
//		MarkRollAction<Ship, FlowTouchPad> shipRollAction = new MarkRollAction<Ship, FlowTouchPad>(ship, 0.3f);
		RollEntityAction shipRollAction = new RollEntityAction(ship, 0.5f);
		FlowTouchPad ftp = FlowWidgetProvider.produceFlowTouchPad(shipRollAction, "CannonRollPad", (int)(Math.min(width, height) * 0.7));
		controlPanel.add(ftp).pad(3);
		return controlPanel;
	}
}
