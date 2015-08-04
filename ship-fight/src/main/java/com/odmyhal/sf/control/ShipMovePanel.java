package com.odmyhal.sf.control;

import java.util.prefs.Preferences;

import org.bircks.enterprise.control.panel.AnimationRisePanel;
import org.bircks.enterprise.control.panel.Skinner;
import org.bircks.enterprise.control.panel.camera.CameraDrawableRollAction;
import org.bircks.enterprise.control.panel.camera.CameraMoveAction;
import org.bircks.enterprise.control.panel.camera.CameraPanel;
import org.bircks.enterprise.control.panel.camera.CameraRollAction;
import org.bricks.core.help.VectorHelper;
import org.bricks.enterprise.control.widget.draw.DrawableRoll;
import org.bricks.enterprise.control.widget.tool.DrugMoveTouchPad;
import org.bricks.enterprise.control.widget.tool.FlowMutableAction;
import org.bricks.enterprise.control.widget.tool.FlowSlider;
import org.bricks.enterprise.control.widget.tool.FlowTouchListener;
import org.bricks.enterprise.control.widget.tool.FlowTouchPad;
import org.bricks.enterprise.control.widget.tool.FlowWidgetProvider;
import org.bricks.enterprise.control.widget.tool.RotationDependAction.RotationProvider;
import org.bricks.extent.control.AccelerateToSpeedEntityAction;
import org.bricks.extent.control.RiseConstEventButton;
import org.bricks.extent.event.FireEvent;
import com.odmyhal.sf.control.ShipRollAction;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.odmyhal.sf.staff.CameraShip;
import com.odmyhal.sf.staff.Ship;

import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class ShipMovePanel extends CameraPanel{
	
	private Runnable r;
	private CameraShip ship;
	private final Preferences shipPreferences;
	static{
		Texture base = new Texture(Gdx.files.internal("pictures/panel/base.png"));
//		Texture base = new Texture(Gdx.files.internal("pictures/dust.png"));
		Texture nord = new Texture(Gdx.files.internal("pictures/panel/nord.png"));
		Texture direction = new Texture(Gdx.files.internal("pictures/panel/direction.png"));
		Texture cameraKnob = new Texture(Gdx.files.internal("pictures/camera_1.png"));
		Skinner.instance().skin().add("movePanelBase", new TextureRegion(base));
		Skinner.instance().skin().add("movePanelNord", new TextureRegion(nord));
		Skinner.instance().skin().add("movePanelDirection", new TextureRegion(direction));
		Skinner.instance().skin().add("cameraKnob", new TextureRegion(cameraKnob));
	}

	public ShipMovePanel(CameraShip ship){
		this(ship, "sf.panel.defaults.portrate", "sf.camera.defaults");
	}
	
	public ShipMovePanel(CameraShip ship, String panelDefaults, String cameraDefaults){
		super(ship.initializeCamera().camera, ship.initializeCamera(), panelDefaults, cameraDefaults);
		this.ship = ship;
		shipPreferences =  Preferences.userRoot().node("sf.ship.defaults");
	}
	
/*	
	@Override
	protected void initStage(){
		super.initStage();
		stack.add(controlPanel());
	}
*/
	protected Table controlPanel(){
		Table controlPanel = new Table();
		controlPanel.left().top();
		controlPanel.pad(5f);
//		Label l = new Label("Moveing control panel", Skinner.instance().skin(), "default");
//		controlPanel.add(l).pad(3).top().left();
//		controlPanel.c
//		controlPanel.row();
		
		Stack stack = new Stack();
//		FlowTouchPad ftp = createRollPad(ship);
		Table lableTable = new Table();
		lableTable.bottom().left().pad(1f).padLeft(6f);
		lableTable.add(new Label("direction", Skinner.instance().skin(), "default"));
		stack.add(lableTable);
		stack.add(createRollPad(ship));
		
//		Cell cell = controlPanel.add(ftp);
		Cell cell = controlPanel.add(stack);
		cell.pad(3).width((float)(Math.min(width, height) * 0.9)).height((float)(Math.min(width, height) * 0.9));

		float acceleration = shipPreferences.getFloat("ship.acceleration.directional", 50f);
		final AccelerateToSpeedEntityAction speedAction = new AccelerateToSpeedEntityAction(ship, acceleration);
		int panelHeight = (int)(Math.min(width, height) * 0.9);
		FlowSlider speedSlider = createSpeedSlider(ship, speedAction, panelHeight);
/*		
		Stack speedStack = new Stack();
		speedStack.rotateBy(90f);
		Table speedTable = new Table();
		speedTable.bottom().left().pad(1f).padLeft(6f);
		speedTable.setRotation(90f);
		Label speedLabel = new Label("speed", Skinner.instance().skin(), "default");
		speedLabel.setRotation(90f);
		speedTable.add(speedLabel);
		speedStack.add(speedSlider);
		speedStack.add(speedTable);
		controlPanel.add(speedStack).height(panelHeight).padLeft(15f);*/
		
		Table speedTable = new Table();
		speedTable.add(new Label("speed", Skinner.instance().skin(), "default"));
		speedTable.row().expand();
		speedTable.add(speedSlider);
		controlPanel.add(speedTable).height(panelHeight).padLeft(15f);
//		controlPanel.add(speedSlider).height(panelHeight).padLeft(15f);
		//		controlPanel.setDebug(true);
		
		Table buttTable = new Table();
		buttTable.left().center();
		buttTable.pad(3);
		

		int buttonWidth = (int)(Math.min(width, height) * 0.55/* * 0.20*/);
		int buttonHeight = (int)(Math.min(width, height) * 0.35/* * 0.15*/);
		buttTable.add(new RiseConstEventButton(ship, new FireEvent(), "FIRE", provideButtonStyle(buttonWidth, buttonHeight))).pad(8);
		buttTable.row();
		
		TextButton stopButton = new TextButton("STOP", provideButtonStyle(buttonWidth, buttonHeight));
		stopButton.addListener(new ClickListener(){
			public void clicked (InputEvent e, float x, float y) {
				speedAction.init(0f);
				speedAction.act(0f);
			}
		});
		buttTable.add(stopButton).pad(3);
		
		controlPanel.add(buttTable).pad(3);
		
/*		controlPanel.padRight(20f);
		DrawableRoll cameraKnob = new DrawableRoll(Skinner.instance().skin().getRegion("cameraKnob"));
		CameraDrawableRollAction cameraRollAction = new CameraDrawableRollAction(camera, cameraKnob, cameraDefaults);
		CameraMoveAction cameraMoveAction = new CameraMoveAction(camera, rotationProvider, cameraDefaults);
		DrawableRoll base = new DrawableRoll(Skinner.instance().skin().getRegion("movePanelBase"));
		final DrugMoveTouchPad cmp = FlowWidgetProvider.produceDrugMoveTouchPad(cameraRollAction, cameraMoveAction, cameraKnob, base);
		cameraRollAction.setListener(cmp.getListener());*/
		
		Stack cstack = new Stack();
//		FlowTouchPad ftp = createRollPad(ship);
		Table clableTable = new Table();
		clableTable.bottom().left().pad(1f).padLeft(6f);
		clableTable.add(new Label("camera", Skinner.instance().skin(), "default"));
		cstack.add(clableTable);
		if(cameraPad == null){
			cameraPad = createCameraPad();
			cstack.add(cameraPad);
			cell = controlPanel.add(cstack);
			cameraPad.setKnobPosition(cell.getMaxWidth() / 2, cell.getMaxHeight() / 2);
		}else{
			cameraPad.rememberKnobPersent();
			cstack.add(cameraPad);
			cell = controlPanel.add(cstack);
		}
		
		final float padWidth = (float)(Math.min(width, height) * 0.9);
		final float padHeight = (float)(Math.min(width, height) * 0.9);
		
		
		cell.pad(4).width(padWidth).height(padHeight);
//		cmp.setKnobPosition(cell.getMaxWidth() / 2, cell.getMaxHeight() / 2);
		r = new Runnable(){

			@Override
			public void run() {
				cameraPad.getListener().simulateDrug(padWidth / 2,  padHeight / 6);
			}
			
		};
		
		return controlPanel;
	}
	
	private DrugMoveTouchPad cameraPad;
	private DrugMoveTouchPad createCameraPad(){
		DrawableRoll cameraKnob = new DrawableRoll(Skinner.instance().skin().getRegion("cameraKnob"));
		CameraDrawableRollAction cameraRollAction = new CameraDrawableRollAction(camera, cameraKnob, cameraDefaults);
		CameraMoveAction cameraMoveAction = new CameraMoveAction(camera, rotationProvider, cameraDefaults);
		DrawableRoll base = new DrawableRoll(Skinner.instance().skin().getRegion("movePanelBase"));
		final DrugMoveTouchPad cmp = FlowWidgetProvider.produceDrugMoveTouchPad(cameraRollAction, cameraMoveAction, cameraKnob, base);
		cameraRollAction.setListener(cmp.getListener());
		return cmp;
	}
	
	private void start(){
		r.run();
	}
	
	boolean needStart = true;
	public void draw(float deltaTime){
		super.draw(deltaTime);
		if(needStart){
			start();
			needStart = false;
		}
	}
	public void resizeViewport(int width, int height){
		if(width > height){
			this.initRatio(Preferences.userRoot().node("sf.panel.defaults.landscape"));
		}else{
			this.initRatio(Preferences.userRoot().node("sf.panel.defaults.portrate"));
		}
		super.resizeViewport(width, height);
	}
	
	public static TextButton.TextButtonStyle provideButtonStyle(int buttonWidth, int buttonHeight){
		TextButton.TextButtonStyle textButtonStyle; 
		if(Skinner.instance().skin().has("fireButton-" + buttonWidth + "-" + buttonHeight, TextButton.TextButtonStyle.class)){
			textButtonStyle = Skinner.instance().skin().get("fireButton", TextButton.TextButtonStyle.class);
		}else{
			Skinner.instance().skin().add("white", Skinner.instance().getFrame(buttonWidth, buttonHeight, 10, new Color(1, 0.78f, 0, 0.5f), Color.RED));
			Skinner.instance().skin().add("default", new BitmapFont());
			
			textButtonStyle = new TextButton.TextButtonStyle();
			textButtonStyle.up = Skinner.instance().skin().newDrawable("white");
			textButtonStyle.down = Skinner.instance().skin().newDrawable("white", Color.DARK_GRAY);
			textButtonStyle.checked = Skinner.instance().skin().newDrawable("white", Color.BLUE);
			textButtonStyle.over = Skinner.instance().skin().newDrawable("white", Color.LIGHT_GRAY);
			textButtonStyle.font = Skinner.instance().skin().getFont("default");
			Skinner.instance().skin().add("fireButton", textButtonStyle);
		}
		return textButtonStyle;
//		return new RiseConstEventButton(liver, event, text, textButtonStyle);
	}
	
	private FlowTouchPad createRollPad(CameraShip ship){
		RotationProvider rotationProvider = ship.initializeCamera();
		float rotationSpeed = shipPreferences.getFloat("ship.roll.speed.radians", 0.1f);
		ShipRollAction shipRollAction = new ShipRollAction(ship, rotationProvider, rotationSpeed);
		
		DrawableRoll base = new DrawableRoll(Skinner.instance().skin().getRegion("movePanelBase"));
		DrawableRoll nord = new DrawableRoll(Skinner.instance().skin().getRegion("movePanelNord"));
		DrawableRoll direction = new DrawableRoll(Skinner.instance().skin().getRegion("movePanelDirection"));
		
		nord.consumeRotation((float)(Math.PI / 2) - rotationProvider.provideRotation());
		shipRollAction.nord = nord;
		shipRollAction.direction = direction;
		
		return FlowWidgetProvider.produceFlowTouchPad(shipRollAction, null, base, nord, direction);
	}
	
	private FlowSlider createSpeedSlider(Ship ship, FlowMutableAction speedAction, int height){
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
		
//		float acceleration = shipPreferences.getFloat("ship.acceleration.directional", 50f);
//		FlowMutableAction speedAction = new AccelerateToSpeedEntityAction(ship, acceleration);
		FlowTouchListener<FlowSlider> listener = new FlowTouchListener(speedAction);
		
		float maxSpeed = shipPreferences.getFloat("ship.speed.directional.max", 30f);
		float minSpeed = shipPreferences.getFloat("ship.speed.directional.min", -10f);
		FlowSlider slider = new FlowSlider(minSpeed, maxSpeed, true, tps, listener);
		float curSpeed = (float) VectorHelper.vectorLen(ship.getVector().source);
		slider.setValue(curSpeed);
		return slider;
	}
}
