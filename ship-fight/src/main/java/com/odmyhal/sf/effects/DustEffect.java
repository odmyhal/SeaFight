package com.odmyhal.sf.effects;

import java.util.concurrent.atomic.AtomicInteger;

import org.bircks.entierprise.model.ModelStorage;
import org.bricks.exception.Validate;
import org.bricks.extent.effects.EffectSystem;
import org.bricks.utils.Cache;
import org.bricks.utils.Cache.DataProvider;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.particles.ParticleChannels;
import com.badlogic.gdx.graphics.g3d.particles.ParticleController;
import com.badlogic.gdx.graphics.g3d.particles.ParallelArray.FloatChannel;
import com.badlogic.gdx.graphics.g3d.particles.ParallelArray.ObjectChannel;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import com.badlogic.gdx.graphics.g3d.particles.batches.ParticleBatch;
import com.badlogic.gdx.graphics.g3d.particles.influencers.ColorInfluencer;
import com.badlogic.gdx.graphics.g3d.particles.influencers.DynamicsInfluencer;
import com.badlogic.gdx.graphics.g3d.particles.influencers.DynamicsModifier;
import com.badlogic.gdx.graphics.g3d.particles.influencers.ModelInfluencer;
import com.badlogic.gdx.graphics.g3d.particles.influencers.ScaleInfluencer;
import com.badlogic.gdx.graphics.g3d.particles.renderers.ModelInstanceRenderer;
import com.badlogic.gdx.math.Vector3;
import com.odmyhal.sf.model.Ball;


public class DustEffect extends EffectSystem.TemporaryEffect{
	
	
	public DustEffect(ParticleSystem system){
		this.emitter = new EffectSystem.NonContiniousEmitter();
		this.emitter.maxParticleCount = 10;
		this.emitter.emissionValue.setLow(5f, 6f);
		this.emitter.emissionValue.setHigh(18f, 19f);
		
		this.emitter.durationValue.setLow(800f, 1200f);
		this.emitter.lifeValue.setLow(1000f, 1500f);
		this.emitter.lifeValue.setHigh(2000f, 3000f);
		
		ModelInstanceRenderer renderer = new ModelInstanceRenderer();
		boolean foundBatch = false;
		for(ParticleBatch batch : system.getBatches()){
			if(renderer.isCompatible(batch)){
				renderer.setBatch(batch);
				foundBatch = true;
				break;
			}
		}
		Validate.isTrue(foundBatch, "Could not find batch for renderer " + renderer.getClass().getCanonicalName());
		
		ModelInfluencer modelInfluencer = new DustModelInfluencer();
		ScaleInfluencer scaleInfluencer = new ScaleInfluencer();
		scaleInfluencer.value.setLow(1f);
		scaleInfluencer.value.setHigh(60f, 70f);
		scaleInfluencer.value.setTimeline(new float[]{0f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1f});
		scaleInfluencer.value.setScaling(new float[]{0f, 0.1f, 0.2f, 0.4f, 0.35f, 0.7f, 0.5f, 0.85f, 0.8f,  1f, 0.9f});
		
		ColorInfluencer.Single colorInfluencer = new ColorInfluencer.Single();
		colorInfluencer.alphaValue.setTimeline(new float[]{0f, 0.15f, 0.4f, 0.9f, 1f});
		colorInfluencer.alphaValue.setScaling(new float[]{0.9f, 0.8f, 0.6f, 0.6f, 0f});
		colorInfluencer.colorValue.setTimeline(new float[]{0f, 0.2f, 0.9f, 1f});
		colorInfluencer.colorValue.setColors(new float[]{1, 0, 0, 0.20f, 0.20f, 0.20f, 0.25f, 0.25f, 0.25f, 0.4f, 0.4f, 0.4f});
		
		DynamicsModifier.CentripetalAcceleration moveModifier = new DynamicsModifier.CentripetalAcceleration();
		moveModifier.strengthValue.setLow(30f, 35f);
		moveModifier.strengthValue.setHigh(200f, 250f);
		DynamicsInfluencer moveInfluencer = new DynamicsInfluencer(moveModifier);
		
		
		this.controller = new ParticleController("dust", emitter, renderer, modelInfluencer, scaleInfluencer, colorInfluencer, moveInfluencer);
		getControllers().add(controller);
	}

	protected void setToTranslation(Vector3 translation){
		Vector3 dustTranslation = Cache.get(Vector3.class);
		dustTranslation.set(translation.x, translation.y, translation.z - 30f);
		controller.transform.idt();
		translate(dustTranslation);
		FloatChannel positionChannel = controller.particles.getChannel(ParticleChannels.Position);
		for(int 	i=0, offset = 0; i < emitter.maxParticleCount; ++i, offset +=positionChannel.strideSize){
			positionChannel.data[offset + ParticleChannels.XOffset] = dustTranslation.x + (float) Math.random() * 10 - 5f;
			positionChannel.data[offset + ParticleChannels.YOffset] = dustTranslation.y + (float) Math.random() * 10 - 5f;
			positionChannel.data[offset + ParticleChannels.ZOffset] = dustTranslation.z + 30;
		}
		Cache.put(dustTranslation);
	}
	
	private class DustModelInfluencer extends ModelInfluencer.Single{
		
		@Override
		public void init () {
			ObjectChannel<ModelInstance> modelChannel = controller.particles.getChannel(ParticleChannels.ModelInstance);
			Validate.isFalse(modelChannel == null, "ModelChanner is not set.");
			for(int i=0, c = controller.emitter.maxParticleCount; i < c; ++i){
				ModelInstance dustInstance = ModelStorage.instance().getModelInstance(Ball.modelStoneExploit);
				Validate.isFalse(dustInstance == null);
				modelChannel.data[i] = dustInstance;
			}
		}
	}
}

