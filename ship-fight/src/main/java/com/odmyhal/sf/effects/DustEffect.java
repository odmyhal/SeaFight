package com.odmyhal.sf.effects;

import java.util.concurrent.atomic.AtomicInteger;

import org.bircks.entierprise.model.ModelStorage;
import org.bricks.exception.Validate;
import org.bricks.extent.effects.BricksParticleSystem.NonContiniousEmitter;
import org.bricks.extent.effects.SubChannelRenderer;
import org.bricks.extent.effects.TemporaryEffect;
import org.bricks.utils.Cache;

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
import com.badlogic.gdx.graphics.g3d.particles.renderers.ParticleControllerRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.odmyhal.sf.model.Ball;


public class DustEffect extends TemporaryEffect{
	
	public DustEffect(ParticleSystem system){
		super(system);
	}
	
	protected NonContiniousEmitter provideEmitter(){
		NonContiniousEmitter emitter = new NonContiniousEmitter();
		emitter.maxParticleCount = 10;
//		emitter.maxParticleCount = 1;
		emitter.emissionValue.setLow(5f, 6f);
		emitter.emissionValue.setHigh(11f, 13f);
		
		emitter.durationValue.setLow(1600f, 2300f);
		emitter.lifeValue.setLow(2500f, 3000f);
		emitter.lifeValue.setHigh(3500f, 4000f);
//		emitter.lifeValue.setLow(6000f, 7000f);
//		emitter.lifeValue.setHigh(8000f, 9000f);
		return emitter;
	}
	
	protected ParticleController provideController(ParticleSystem particleSystem, NonContiniousEmitter emitter, ParticleControllerRenderer renderer){
//		SubChannelRenderer.SubChannelModelInstanceRenderer renderer = new SubChannelRenderer.SubChannelModelInstanceRenderer();
		boolean foundBatch = false;
		Array<ParticleBatch<?>> batches = particleSystem.getBatches();
		for(int i = 0; i < batches.size; i++){
			ParticleBatch<?> batch = batches.get(i);
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
		
//		scaleInfluencer.value.setLow(1f);
//		scaleInfluencer.value.setHigh(70f);
//		scaleInfluencer.value.setTimeline(new float[]{0f, 1f});
//		scaleInfluencer.value.setScaling(new float[]{0f, 1f});
		
		ColorInfluencer.Single colorInfluencer = new ColorInfluencer.Single();
		colorInfluencer.alphaValue.setTimeline(new float[]{0f, 0.15f, 0.4f, 0.9f, 1f});
		colorInfluencer.alphaValue.setScaling(new float[]{0.9f, 0.8f, 0.6f, 0.6f, 0f});
		colorInfluencer.colorValue.setTimeline(new float[]{0f, 0.2f, 0.9f, 1f});
		colorInfluencer.colorValue.setColors(new float[]{1, 0, 0, 0.20f, 0.20f, 0.20f, 0.25f, 0.25f, 0.25f, 0.22f, 0.22f, 0.22f});
		
//		colorInfluencer.alphaValue.setTimeline(new float[]{0f, 1f});
//		colorInfluencer.alphaValue.setScaling(new float[]{0.9f, 0f});
		
		DynamicsModifier.CentripetalAcceleration moveModifier = new DynamicsModifier.CentripetalAcceleration();
		moveModifier.strengthValue.setLow(30f, 35f);
		moveModifier.strengthValue.setHigh(50f, 60f);
		DynamicsInfluencer moveInfluencer = new DynamicsInfluencer(moveModifier);
		
		return new ParticleController("dust", emitter, renderer, modelInfluencer, scaleInfluencer, colorInfluencer, moveInfluencer);
	}

	protected void setToTranslation(Vector3 translation){
		Vector3 dustTranslation = Cache.get(Vector3.class);
		dustTranslation.set(translation.x, translation.y, translation.z - 30f);
		Array<ParticleController> controllers = getControllers();
		for (int k = 0, n = controllers.size; k < n; k++){
			ParticleController controller = controllers.get(k);
			controller.transform.idt();
			controller.transform.translate(dustTranslation);
			Validate.isFalse(controller.particles == null, "Controller has no particles");
			FloatChannel positionChannel = controller.particles.getChannel(ParticleChannels.Position);
			for(int i=0, offset = 0; i < controller.emitter.maxParticleCount; ++i, offset +=positionChannel.strideSize){
				positionChannel.data[offset + ParticleChannels.XOffset] = dustTranslation.x + (float) Math.random() * 10 - 5f;
				positionChannel.data[offset + ParticleChannels.YOffset] = dustTranslation.y + (float) Math.random() * 10 - 5f;
				positionChannel.data[offset + ParticleChannels.ZOffset] = dustTranslation.z + 30;
			}
		}
		Cache.put(dustTranslation);
	}
	
	private class DustModelInfluencer extends ModelInfluencer.Single{
		
		public DustModelInfluencer(){}
		
		private DustModelInfluencer(DustModelInfluencer origin){
			super(origin);
		}
		
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
		
		@Override
		public DustModelInfluencer copy () {
			return new DustModelInfluencer(this);
		}
	}

	@Override
	protected SubChannelRenderer provideRenderer() {
		return new SubChannelRenderer.SubChannelModelInstanceRenderer();
	}
}

