package com.odmyhal.sf.control;

import org.bircks.enterprise.control.panel.AnimationRisePanel;
import org.bircks.enterprise.control.panel.Skinner;
import org.bricks.enterprise.control.widget.tool.FlowTouchPad;
import org.bricks.enterprise.control.widget.tool.FlowWidgetProvider;
import org.bricks.exception.Validate;
import org.bricks.extent.control.NodeRollAction;
import org.bricks.extent.control.NodeRollProcessorAction;
import org.bricks.extent.entity.subject.ModelSubject;

import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.odmyhal.sf.staff.Ship;

public class WeaponPanel extends AnimationRisePanel{
	
	private Ship ship;
//	private Node tower, gun;

	public WeaponPanel(Ship ship){
		this.ship = ship;
/*		for(ModelSubject ms : ship.getStaff()){
			if(this.tower == null){
				this.tower = ms.getNodeByName("pushka_osnova");
			}
			if(this.gun == null){
				this.gun = ms.getNodeByName("pushka_garmaty");
			}
			if(!(this.gun == null || this.tower == null)){
				break;
			}
		}
		Validate.isFalse(this.gun == null || this.tower == null, "Both nodes pushka_osnova and pushka_garmaty must exists in model of ship");
		*/
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
		
//		NodeRollAction<Ship, FlowTouchPad> nodeRollAction = new NodeRollAction<Ship, FlowTouchPad>(ship, new Vector3(0f, 0f, -10f), 
//				new Vector3(15f, 0f, 0f), (float) Math.PI / 2, 0.5f, tower, gun);
		NodeRollProcessorAction<Ship, FlowTouchPad> nodeRollAction = new NodeRollProcessorAction(ship, "pushka", 0.5f);
		FlowTouchPad ftp = FlowWidgetProvider.produceFlowTouchPad(nodeRollAction, "NodeRollPad", (int)(Math.min(width, height) * 0.7));
		controlPanel.add(ftp).pad(5);

		return controlPanel;
	}
}
