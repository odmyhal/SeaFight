package com.odmyhal.sf.interact;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.collision.Ray;

public class Interactive extends InputAdapter{
	

	public boolean touchDown (int screenX, int screenY, int pointer, int button) {
		Ray r;
		System.out.format("Interactive  touchDown x = %d, y = %d, pointer = %d\n", screenX, screenY, pointer);
		return true;
	}

	public boolean touchUp (int screenX, int screenY, int pointer, int button) {
		System.out.format("Interactive  touchUp x = %d, y = %d, pointer = %d\n", screenX, screenY, pointer);
		return true;
	}

	public boolean touchDragged (int screenX, int screenY, int pointer) {
		System.out.format("Interactive  touchDragged x = %d, y = %d, pointer = %d\n", screenX, screenY, pointer);
		return true;
	}


}
