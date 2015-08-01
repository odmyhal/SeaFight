package com.odmyhal.sf.effects;

import com.badlogic.gdx.graphics.g3d.particles.ParticleChannels;
import com.badlogic.gdx.graphics.g3d.particles.ParallelArray.FloatChannel;
import com.badlogic.gdx.graphics.g3d.particles.influencers.DynamicsModifier;
import com.badlogic.gdx.graphics.g3d.particles.influencers.DynamicsModifier.CentripetalAcceleration;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class CentrapetialSafeDynamicAcceleration extends DynamicsModifier.CentripetalAcceleration{
	
	FloatChannel accelerationChannel;
	FloatChannel positionChannel;
	Vector3 TMP_SAFE_V3 = new Vector3();
	
	public CentrapetialSafeDynamicAcceleration(){}
	
	public CentrapetialSafeDynamicAcceleration(CentrapetialSafeDynamicAcceleration csda){
		super(csda);
	}
	
	@Override
	public void allocateChannels() {
		super.allocateChannels();
		positionChannel = controller.particles.getChannel(ParticleChannels.Position);
		accelerationChannel = controller.particles.getChannel(ParticleChannels.Acceleration);
	}

	@Override
	public void update () {
		float cx = 0, cy = 0, cz = 0;
		if(!isGlobal){
			float[] val = controller.transform.val;
			cx = val[Matrix4.M03]; 
			cy = val[Matrix4.M13]; 
			cz = val[Matrix4.M23];
		}
		
		int lifeOffset=ParticleChannels.LifePercentOffset, strengthOffset = 0, positionOffset = 0, forceOffset = 0;
		for(int 	i=0,  c= controller.particles.size; i < c; ++i,  
			positionOffset += positionChannel.strideSize,
			strengthOffset += strengthChannel.strideSize, 
			forceOffset +=accelerationChannel.strideSize, 
			lifeOffset += lifeChannel.strideSize){
		
			float 	strength = 	strengthChannel.data[strengthOffset + ParticleChannels.VelocityStrengthStartOffset] + 
												strengthChannel.data[strengthOffset + ParticleChannels.VelocityStrengthDiffOffset]* strengthValue.getScale(lifeChannel.data[lifeOffset]);
			TMP_SAFE_V3.set(	positionChannel.data[positionOffset +ParticleChannels.XOffset] -cx, 
										positionChannel.data[positionOffset +ParticleChannels.YOffset] -cy, 
										positionChannel.data[positionOffset +ParticleChannels.ZOffset] -cz)
							.nor().scl(strength);
//			System.out.println("Thread " + Thread.currentThread().getName() + " adding to position: " + TMP_SAFE_V3);
			accelerationChannel.data[forceOffset +ParticleChannels.XOffset] += TMP_SAFE_V3.x;
			accelerationChannel.data[forceOffset +ParticleChannels.YOffset] += TMP_SAFE_V3.y;
			accelerationChannel.data[forceOffset +ParticleChannels.ZOffset] += TMP_SAFE_V3.z;
		}
	}
	
	@Override
	public CentrapetialSafeDynamicAcceleration copy () {
		return new CentrapetialSafeDynamicAcceleration(this);
	}
}
