package com.odmyhal.sf.control;

import org.bircks.enterprise.control.panel.AnimationRisePanel;
import org.bircks.enterprise.control.panel.Skinner;
import org.bricks.engine.item.MultiWalkRoller;
import org.bricks.enterprise.control.widget.tool.FlowTouchPad;
import org.bricks.enterprise.control.widget.tool.FlowWidgetProvider;
import org.bricks.enterprise.control.widget.tool.HalfRTouchPad;
import org.bricks.extent.control.NodeRollProcessorAction;
import org.bricks.extent.control.RiseConstEventButton;
import org.bricks.extent.event.FireEvent;
import org.bricks.extent.subject.model.ModelBrickOperable;
import org.bricks.extent.subject.model.ModelBrickSubject;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.odmyhal.sf.staff.Ship;

public class WeaponPanel extends AnimationRisePanel{
	
	private Ship ship;
//	private Node tower, gun;

	public WeaponPanel(Ship ship){
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
		Label l = new Label("FIGHT", Skinner.instance().skin(), "default");
		controlPanel.add(l).pad(5).top().left();
		controlPanel.row();
		
//Gun horizontal roll pad:
		NodeRollProcessorAction<?, FlowTouchPad> pushkaHRollAction = new NodeRollProcessorAction(ship, Ship.prefs.getFloat("ship.roll.speed.radians", 0.5f), "pushka", "backPushka");
		FlowTouchPad ftp = FlowWidgetProvider.produceFlowTouchPad(pushkaHRollAction, "NodeRollPad", (int)(Math.min(width, height) * 0.7));
		controlPanel.add(ftp).pad(5);
		
//Gun vertical roll pad:		
		NodeRollProcessorAction pushkaVRollAction = new NodeRollProcessorAction(ship, Ship.prefs.getFloat("ship.roll.speed.radians", 0.5f), "stvol", "backStvol");
		HalfRTouchPad hrtp = FlowWidgetProvider.produceFlowHalfRTouchPad(pushkaVRollAction, "Pushka1VRoll", (int)(Math.min(width, height) * 0.7));
		controlPanel.add(hrtp).pad(5);
		

		int buttonWidth = (int)(Math.min(width, height) * 0.5);
		int buttonHeight = (int)(Math.min(width, height) * 0.3);
		controlPanel.add(new RiseConstEventButton(ship, new FireEvent(), "FIRE", ShipMovePanel.provideButtonStyle(buttonWidth, buttonHeight))).pad(8);

		return controlPanel;
	}
}
