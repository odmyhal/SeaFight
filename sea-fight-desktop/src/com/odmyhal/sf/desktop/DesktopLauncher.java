package com.odmyhal.sf.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.odmyhal.sf.GL30Try;
import com.odmyhal.sf.SeaFight;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Sea Fight";
		config.width = 400;
		config.height = 700;
//		config.useGL30 = true;
		new LwjglApplication(new GL30Try(400, 700), config);
//		new LwjglApplication(new SeaFight(), config);
	}
}
