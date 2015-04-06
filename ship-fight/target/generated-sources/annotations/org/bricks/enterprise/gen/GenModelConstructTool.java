package org.bricks.enterprise.gen;

import org.bircks.entierprise.model.ModelConstructTool;

public class GenModelConstructTool extends ModelConstructTool{
	
	protected void constructModels(){
				com.odmyhal.sf.model.construct.WaterBallConstructor.instance().construct(this, "water-ball_10", "stone-exploit_10");
				com.odmyhal.sf.model.construct.BufferWaverConstructor.instance().construct(this, "water");
				com.odmyhal.sf.model.construct.AmmunitionConstructor.instance().construct(this, "ship_ammunition");
				com.odmyhal.sf.model.construct.IslandConstructor.instance().construct(this, "island_1");
				com.odmyhal.sf.model.construct.ShaderWaveConstructor.instance().construct(this, "shader-wave");
			}
}