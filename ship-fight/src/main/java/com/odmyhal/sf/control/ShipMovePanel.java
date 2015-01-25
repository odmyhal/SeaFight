package com.odmyhal.sf.control;

import java.util.prefs.Preferences;

import org.bircks.enterprise.control.panel.AnimationRisePanel;
import org.bircks.enterprise.control.panel.Skinner;
import org.bricks.enterprise.control.widget.draw.DrawableRoll;
import org.bricks.enterprise.control.widget.tool.FlowMutableAction;
import org.bricks.enterprise.control.widget.tool.FlowSlider;
import org.bricks.enterprise.control.widget.tool.FlowTouchListener;
import org.bricks.enterprise.control.widget.tool.FlowTouchPad;
import org.bricks.enterprise.control.widget.tool.FlowWidgetProvider;
import org.bricks.enterprise.control.widget.tool.RotationDependAction.RotationProvider;
import org.bricks.extent.control.AccelerateToSpeedEntityAction;
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

import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;

public class ShipMovePanel extends AnimationRisePanel{
	
	private Ship ship;
	private final Preferences shipPreferences;
	
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
		shipPreferences =  Preferences.userRoot().node("sf.ship.defaults");
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
		
		FlowTouchPad ftp = createRollPad(ship);
		Cell cell = controlPanel.add(ftp);
		cell.pad(3).width((float)(Math.min(width, height) * 0.7)).height((float)(Math.min(width, height) * 0.7));

		int panelHeight = (int)(Math.min(width, height) * 0.7);
		FlowSlider speedSlider = createSpeedSlider(ship, panelHeight);
		controlPanel.add(speedSlider).height(panelHeight).padLeft(20f);
		//		controlPanel.setDebug(true);
		return controlPanel;
	}
	
	private FlowTouchPad createRollPad(Ship ship){
		RotationProvider rotationProvider = ship.initializeCamera();
		float rotationSpeed = shipPreferences.getFloat("ship.roll.speed.radians", 0.1f);
		ShipRollAction shipRollAction = new ShipRollAction(ship, rotationProvider, rotationSpeed);
		
		DrawableRoll base = new DrawableRoll(Skinner.instance().skin().getRegion("movePanelBase"));
		DrawableRoll nord = new DrawableRoll(Skinner.instance().skin().getRegion("movePanelNord"));
		DrawableRoll direction = new DrawableRoll(Skinner.instance().skin().getRegion("movePanelDirection"));
		
		nord.consumeRotation((float)(Math.PI / 2) - rotationProvider.provideRotation());
		shipRollAction.nord = nord;
		shipRollAction.direction = direction;
		
		return FlowWidgetProvider.produceFlowTouchPad(shipRollAction, base, nord, direction);
	}
	
	private FlowSlider createSpeedSlider(Ship ship, int height){
		String padName = "shipSpeedBackgorund-" + height;
		if(!Skinner.instance().hasFrame(padName)){
			Color background = Color.BLUE;
			background.a = 0.3f;
			Color border = Color.OLIVE;
			Skinner.instance().putFrame(height / 8, height, 2, background, border, padName);
		}
		String knobName = "shipSpeedKnob-" + height;
		if(!Skinner.instance().hasFrame(knobName)){
			Color background = Color.CYAN;
			Skinner.instance().putFrame(20, 5, 0, background, background, knobName);
		}
		SliderStyle tps = new SliderStyle(Skinner.instance().skin().getDrawable(padName), Skinner.instance().skin().getDrawable(knobName));
		
		float acceleration = shipPreferences.getFloat("ship.acceleration.directional", 50f);
		FlowMutableAction speedAction = new AccelerateToSpeedEntityAction(ship, acceleration);
		FlowTouchListener<FlowSlider> listener = new FlowTouchListener(speedAction);
		
		float maxSpeed = shipPreferences.getFloat("ship.speed.directional.max", 30f);
		float minSpeed = shipPreferences.getFloat("ship.speed.directional.min", -10f);
		FlowSlider slider = new FlowSlider(minSpeed, maxSpeed, true, tps, listener);
		slider.setValue(0f);
		return slider;
	}
}
