package com.odmyhal.sf.model.shader;

import java.nio.ShortBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class WaveMesh extends Mesh{

	public WaveMesh(VertexDataType type, boolean isStatic, int maxVertices, int maxIndices, VertexAttribute[] attributes) {
		super(type, isStatic, maxVertices, maxIndices, attributes);
	}

	public WaveMesh(boolean isStatic, int maxVertices, int maxIndices, VertexAttributes attributes) {
		super(isStatic, maxVertices, maxIndices, attributes);
	}

	public WaveMesh(boolean isStatic, int maxVertices, int maxIndices, VertexAttribute[] attributes) {
		super(isStatic, maxVertices, maxIndices, attributes);
	}

	public WaveMesh(boolean staticVertices, boolean staticIndices, int maxVertices, int maxIndices, VertexAttributes attributes) {
		super(staticVertices, staticIndices, maxVertices, maxIndices, attributes);
	}

	@Override
	public void render (ShaderProgram shader, int primitiveType, int offset, int count, boolean autoBind) {
		
	}
}
