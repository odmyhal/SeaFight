package com.odmyhal.sf.model.shader;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.CubemapAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader.Config;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.odmyhal.sf.model.ShaderWaver;

public class WaveShaderProvider extends DefaultShaderProvider{
	
	private Camera camera;
	
	public WaveShaderProvider(Camera camera){
		this.camera = camera;
	}
	
	@Override
	protected Shader createShader (final Renderable renderable) {
		if(renderable.userData != null && renderable.userData instanceof ShaderWaver.WaveData){
			return new WaveNativeShader(renderable, camera);
		}
		return new FogShader(renderable, camera);
//		return super.createShader(renderable);
//		return new DefaultShader(renderable, config, createPrefixWithoutTextures(renderable, config));
	}

//disable texture coordinates
	private static final boolean and (final long mask, final long flag) {
		return (mask & flag) == flag;
	}

	private static final boolean or (final long mask, final long flag) {
		return (mask & flag) != 0;
	}
	
	public static String createPrefixWithoutTextures (final Renderable renderable, final Config config) {
		String prefix = "";
		final long mask = renderable.material.getMask();
		final long attributes = renderable.mesh.getVertexAttributes().getMask();
		if (and(attributes, Usage.Position)) prefix += "#define positionFlag\n";
		if (or(attributes, Usage.ColorUnpacked | Usage.ColorPacked)) prefix += "#define colorFlag\n";
		if (and(attributes, Usage.BiNormal)) prefix += "#define binormalFlag\n";
		if (and(attributes, Usage.Tangent)) prefix += "#define tangentFlag\n";
		if (and(attributes, Usage.Normal)) prefix += "#define normalFlag\n";
		if (and(attributes, Usage.Normal) || and(attributes, Usage.Tangent | Usage.BiNormal)) {
			if (renderable.environment != null) {
				prefix += "#define lightingFlag\n";
				prefix += "#define ambientCubemapFlag\n";
				prefix += "#define numDirectionalLights " + config.numDirectionalLights + "\n";
				prefix += "#define numPointLights " + config.numPointLights + "\n";
				if (renderable.environment.has(ColorAttribute.Fog)) {
					prefix += "#define fogFlag\n";
				}
				if (renderable.environment.shadowMap != null) prefix += "#define shadowMapFlag\n";
				if (renderable.material.has(CubemapAttribute.EnvironmentMap)
					|| renderable.environment.has(CubemapAttribute.EnvironmentMap)) prefix += "#define environmentCubemapFlag\n";
			}
		}
		final int n = renderable.mesh.getVertexAttributes().size();
		for (int i = 0; i < n; i++) {
			final VertexAttribute attr = renderable.mesh.getVertexAttributes().get(i);
			if (attr.usage == Usage.BoneWeight)
				prefix += "#define boneWeight" + attr.unit + "Flag\n";
		}
		if ((attributes & Usage.Tangent) == Usage.Tangent) prefix += "#define tangentFlag\n";
		if ((attributes & Usage.BiNormal) == Usage.BiNormal) prefix += "#define binormalFlag\n";
		if ((mask & BlendingAttribute.Type) == BlendingAttribute.Type) prefix += "#define " + BlendingAttribute.Alias + "Flag\n";
		
		if ((mask & ColorAttribute.Diffuse) == ColorAttribute.Diffuse)
			prefix += "#define " + ColorAttribute.DiffuseAlias + "Flag\n";
		if ((mask & ColorAttribute.Specular) == ColorAttribute.Specular)
			prefix += "#define " + ColorAttribute.SpecularAlias + "Flag\n";
		if ((mask & ColorAttribute.Emissive) == ColorAttribute.Emissive)
			prefix += "#define " + ColorAttribute.EmissiveAlias + "Flag\n";
		if ((mask & ColorAttribute.Reflection) == ColorAttribute.Reflection)
			prefix += "#define " + ColorAttribute.ReflectionAlias + "Flag\n";
		if ((mask & FloatAttribute.Shininess) == FloatAttribute.Shininess)
			prefix += "#define " + FloatAttribute.ShininessAlias + "Flag\n";
		if ((mask & FloatAttribute.AlphaTest) == FloatAttribute.AlphaTest)
			prefix += "#define " + FloatAttribute.AlphaTestAlias + "Flag\n";
		if (renderable.bones != null && config.numBones > 0) prefix += "#define numBones " + config.numBones + "\n";
		return prefix;
	}
	
}
