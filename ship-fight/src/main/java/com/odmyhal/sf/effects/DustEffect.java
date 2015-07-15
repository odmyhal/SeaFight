package com.odmyhal.sf.effects;

import java.util.concurrent.atomic.AtomicInteger;

import org.bircks.entierprise.model.ModelStorage;
import org.bricks.exception.Validate;
import org.bricks.extent.effects.BricksParticleSystem.NonContiniousEmitter;
import org.bricks.extent.effects.ModelStorageInfluencer;
import org.bricks.extent.effects.SubChannelRenderer;
import org.bricks.extent.effects.TemporaryEffect;
import org.bricks.utils.Cache;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
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
import com.badlogic.gdx.graphics.g3d.particles.influencers.RegionInfluencer;
import com.badlogic.gdx.graphics.g3d.particles.influencers.ScaleInfluencer;
import com.badlogic.gdx.graphics.g3d.particles.influencers.RegionInfluencer.Single;
import com.badlogic.gdx.graphics.g3d.particles.renderers.ModelInstanceRenderer;
import com.badlogic.gdx.graphics.g3d.particles.renderers.ParticleControllerRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.odmyhal.sf.model.Ball;


public class DustEffect extends TemporaryEffect{
	
	public static final Texture dustTexture = new Texture("pictures/test3.png");
//	public static final Texture dustTexture = new Texture("pictures/dust.png");
//	public static final Texture dustTexture = new Texture("pictures/panel/base.png");
	
	public DustEffect(ParticleSystem system){
		super(system);
	}
/*	
	public void update(){
		System.out.println("Dust effect updateing " + this.getControllers().get(0).particles.size);
		super.update();
	}
	
	public void draw(){
		System.out.println("Dust effect drawing " + this.getControllers().get(0).particles.size);
		super.draw();
	}
*/	
	protected NonContiniousEmitter provideEmitter(){
		NonContiniousEmitter emitter = new NonContiniousEmitter();
		emitter.maxParticleCount = 10;
		emitter.emissionValue.setLow(5f, 6f);
		emitter.emissionValue.setHigh(11f, 13f);
		
		emitter.durationValue.setLow(1600f, 2300f);
		emitter.lifeValue.setLow(2500f, 3000f);
		emitter.lifeValue.setHigh(3500f, 4000f);
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
		
//		ModelInfluencer modelInfluencer = new ModelStorageInfluencer(Ball.modelStoneExploit);
		RegionInfluencer.AspectTextureRegion atr = new RegionInfluencer.AspectTextureRegion();
		atr.u = 0f;
		atr.v = 0f;
		atr.u2 = 1f;
		atr.v2 = 1f;
		atr.halfInvAspectRatio = 0.5f;
		RegionInfluencer.Single dustInfluencer = new RegionInfluencer.Single(dustTexture);
		
		ScaleInfluencer scaleInfluencer = new ScaleInfluencer();
		scaleInfluencer.value.setLow(100f);
		scaleInfluencer.value.setHigh(550f, 600f);
		scaleInfluencer.value.setTimeline(new float[]{0f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1f});
		scaleInfluencer.value.setScaling(new float[]{0f, 0.1f, 0.2f, 0.4f, 0.35f, 0.7f, 0.5f, 0.85f, 0.8f,  1f, 0.9f});
	
		ColorInfluencer.Single colorInfluencer = new ColorInfluencer.Single();
//		colorInfluencer.alphaValue.setTimeline(new float[]{0f, 1f});
//		colorInfluencer.alphaValue.setScaling(new float[]{1f, 1f});
//		colorInfluencer.colorValue.setTimeline(new float[]{0f, 1f});
//		colorInfluencer.colorValue.setColors(new float[]{0.25f, 0.25f, 0.25f, 0f, 0f, 0f});
		
		colorInfluencer.alphaValue.setTimeline(new float[]{0f, 0.15f, 0.4f, 0.9f, 1f});
		colorInfluencer.alphaValue.setScaling(new float[]{0.7f, 0.8f, 0.6f, 0.6f, 0f});
		colorInfluencer.colorValue.setTimeline(new float[]{0f, 0.2f, 0.9f, 1f});
		colorInfluencer.colorValue.setColors(new float[]{1, 0, 0, 0.20f, 0.20f, 0.20f, 0.25f, 0.25f, 0.25f, 0.22f, 0.22f, 0.22f});

		DynamicsModifier.CentripetalAcceleration moveModifier = new DynamicsModifier.CentripetalAcceleration();
		moveModifier.strengthValue.setLow(90f, 110f);
		moveModifier.strengthValue.setHigh(150f, 190f);
		DynamicsInfluencer moveInfluencer = new DynamicsInfluencer(moveModifier);
		
		return new ParticleController("dust", emitter, renderer, dustInfluencer, scaleInfluencer, colorInfluencer, moveInfluencer);
	}

	public void setToTranslation(Vector3 translation){
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
	
	

	@Override
	protected SubChannelRenderer provideRenderer() {
		return new SubChannelRenderer.SubChannelTextureRenderer();//.SubChannelModelInstanceRenderer();
	}
}

