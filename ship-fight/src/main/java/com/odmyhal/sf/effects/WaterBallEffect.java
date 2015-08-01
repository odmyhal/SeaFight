package com.odmyhal.sf.effects;

import org.bricks.exception.Validate;
import org.bricks.extent.effects.BricksParticleSystem.NonContiniousEmitter;
import org.bricks.extent.effects.ModelStorageInfluencer;
import org.bricks.extent.effects.SubChannelRenderer;
import org.bricks.extent.effects.TemporaryEffect;
import org.bricks.utils.Cache;

import com.badlogic.gdx.graphics.g3d.particles.ParticleChannels;
import com.badlogic.gdx.graphics.g3d.particles.ParticleController;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import com.badlogic.gdx.graphics.g3d.particles.ParallelArray.FloatChannel;
import com.badlogic.gdx.graphics.g3d.particles.batches.ParticleBatch;
import com.badlogic.gdx.graphics.g3d.particles.influencers.ColorInfluencer;
import com.badlogic.gdx.graphics.g3d.particles.influencers.DynamicsInfluencer;
import com.badlogic.gdx.graphics.g3d.particles.influencers.DynamicsModifier;
import com.badlogic.gdx.graphics.g3d.particles.influencers.ModelInfluencer;
import com.badlogic.gdx.graphics.g3d.particles.influencers.ScaleInfluencer;
import com.badlogic.gdx.graphics.g3d.particles.renderers.ParticleControllerRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.odmyhal.sf.model.Ball;
import com.odmyhal.sf.model.bubble.BlabKeeper;
import com.odmyhal.sf.staff.Ammunition;

public class WaterBallEffect extends TemporaryEffect{

	public WaterBallEffect(ParticleSystem particleSystem) {
		super(particleSystem);
	}
	
	public void finish(){
		ParticleController controller = this.getControllers().get(0);
		BlabKeeper.Blab bubble = Ammunition.blabKeeper.emptyBlub();
		bubble.init(controller.transform.val[Matrix4.M03], controller.transform.val[Matrix4.M13], 0.3f, 2500l, 65f, 250f);
		Ammunition.blabKeeper.pushBlab(bubble);
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
				positionChannel.data[offset + ParticleChannels.XOffset] = dustTranslation.x;
				positionChannel.data[offset + ParticleChannels.YOffset] = dustTranslation.y;
				positionChannel.data[offset + ParticleChannels.ZOffset] = dustTranslation.z + i * 20;
			}
		}
		Cache.put(dustTranslation);
	}

	@Override
	protected SubChannelRenderer provideRenderer() {
		return new SubChannelRenderer.SubChannelModelInstanceRenderer();
	}

	@Override
	protected NonContiniousEmitter provideEmitter() {
		NonContiniousEmitter emitter = new NonContiniousEmitter();
		emitter.maxParticleCount = 20;
		emitter.emissionValue.setLow(45f, 50f);
		emitter.emissionValue.setHigh(25f, 30f);
		
		emitter.durationValue.setLow(300f, 400f);
		
		emitter.lifeValue.setLow(700f, 1000f);
		emitter.lifeValue.setHigh(1100f, 1200f);
		return emitter;
	}

	@Override
	protected ParticleController provideController(
			ParticleSystem particleSystem, NonContiniousEmitter emitter,
			ParticleControllerRenderer renderer) {
		
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
		ModelInfluencer modelInfluencer = new ModelStorageInfluencer(Ball.modelStoneExploit);
		
		ScaleInfluencer scaleInfluencer = new ScaleInfluencer();
		scaleInfluencer.value.setLow(1f);
		scaleInfluencer.value.setHigh(25f);
		scaleInfluencer.value.setTimeline(new float[]{0f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 0.95f, 1f});
		scaleInfluencer.value.setScaling(new float[]{0f, 0.60f, 0.55f, 0.7f, 0.65f, 0.8f, 0.75f, 0.9f, 0.85f, 1f, 0.1f});
//		scaleInfluencer.value.setTimeline(new float[]{0f, 0.3f, 1f});
//		scaleInfluencer.value.setScaling(new float[]{0f, 0.60f, 1f});
	
		ColorInfluencer.Single colorInfluencer = new ColorInfluencer.Single();
		colorInfluencer.alphaValue.setTimeline(new float[]{0f, 1f});
		colorInfluencer.alphaValue.setScaling(new float[]{0.6f, 0f});
		colorInfluencer.colorValue.setTimeline(new float[]{0f, 1f});
		colorInfluencer.colorValue.setColors(new float[]{0.357f, 0.765f, 0.863f, 0.357f, 0.765f, 0.863f});
		
		DynamicsModifier.CentripetalAcceleration moveModifier = new CentrapetialSafeDynamicAcceleration();
		moveModifier.strengthValue.setLow(-190f, -195f);
		moveModifier.strengthValue.setHigh(-200f, -220f);
		moveModifier.strengthValue.setTimeline(new float[]{0f, 0.1f, 0.2f, 1f});
		moveModifier.strengthValue.setScaling(new float[]{0f, 1f, 0f, 0f});
		DynamicsInfluencer moveInfluencer = new DynamicsInfluencer(moveModifier);
		
		return new ParticleController("waterBall", emitter, renderer, modelInfluencer, scaleInfluencer, colorInfluencer, moveInfluencer);
	}

}
