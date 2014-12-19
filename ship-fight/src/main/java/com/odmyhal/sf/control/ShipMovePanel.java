package com.odmyhal.sf.control;

import org.bircks.enterprise.control.panel.AnimationRisePanel;
import org.bircks.enterprise.control.panel.Skinner;
import org.bricks.enterprise.control.widget.tool.FlowTouchPad;
import org.bricks.enterprise.control.widget.tool.FlowWidgetProvider;
import org.bricks.extent.control.RollEntityAction;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.odmyhal.sf.staff.Ship;

public class ShipMovePanel extends AnimationRisePanel{
	
	private Ship ship;
	static{
		Texture base = new Texture(Gdx.files.internal("pictures/panel/base.png"));
		Texture nord = new Texture(Gdx.files.internal("pictures/panel/nord.png"));
		Texture direction = new Texture(Gdx.files.internal("pictures/panel/direction.png"));
		Skinner.instance().skin().add("movePanelBase", new TextureRegion(base));
		Skinner.instance().skin().add("movePanelNord", new TextureRegion(nord));
		Skinner.instance().skin().add("movePanelDirection", new TextureRegion(direction));
	}

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
		
		RollEntityAction shipRollAction = new RollEntityAction(ship, ship.initializeCamera(), 0.5f);
		FlowTouchPad ftp = FlowWidgetProvider.produceFlowTouchPad(shipRollAction, "movePanelBase", "movePanelNord", "movePanelDirection");
		Cell cell = controlPanel.add(ftp);
		cell.pad(3).width((float)(Math.min(width, height) * 0.7)).height((float)(Math.min(width, height) * 0.7));
//		controlPanel.setDebug(true);
		return controlPanel;
	}
}
