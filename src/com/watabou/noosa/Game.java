package com.watabou.noosa;

import com.nikita22007.multiplayer.noosa.audio.Sample;
import com.watabou.utils.SystemTime;
import javafx.scene.Scene;

import javax.naming.NameNotFoundException;
import javax.swing.text.View;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

public class Game {

	public static Game instance;

	// Actual size of the screen
	public static int width;
	public static int height;

	// Density: mdpi=1, hdpi=1.5, xhdpi=2...
	public static float density = 1;

	public static String version;
	public static int versionCode;

	// Current scene
	protected Scene scene;
	// New scene we are going to switch to
	protected Scene requestedScene;
	// true if scene switch is requested
	protected boolean requestedReset = true;
	// callback to perform logic during scene change
	protected SceneChangeCallback onChange;
	// New scene class
	protected Class<? extends Scene> sceneClass;

	// Current time in milliseconds
	protected long now;
	// Milliseconds passed since previous update
	protected long step;

	public static float timeScale = 1f;
	public static float elapsed = 0f;
	public static float timeTotal = 0f;


	// Accumulated touch events

	// Accumulated key events

	public Game( Class<? extends Scene> c ) {
		super();
		sceneClass = c;
	}





	public void onDestroy() {
		destroyGame();

	}








	protected void destroyGame() {

	}


	public static void switchScene(Class<? extends Scene> c) {
		switchScene(c, null);
	}

	public static void switchScene(Class<? extends Scene> c, SceneChangeCallback callback) {
		instance.sceneClass = c;
		instance.requestedReset = true;
		instance.onChange = callback;
	}

	public static Scene scene() {
		return instance.scene;
	}

	protected void step() {

		if (requestedReset) {
			requestedReset = false;

			try {
				requestedScene = sceneClass.newInstance();
				switchScene();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InstantiationException e){
				e.printStackTrace();
			}


		}

		update();
	}


	protected void switchScene() {


		scene = requestedScene;
		if (onChange != null) onChange.beforeCreate();
		if (onChange != null) onChange.afterCreate();
		onChange = null;

		Game.elapsed = 0f;
		Game.timeScale = 1f;
		Game.timeTotal = 0f;
	}

	protected void update() {
		Game.elapsed = Game.timeScale * step * 0.001f;
		Game.timeTotal += Game.elapsed;

	}

	public interface SceneChangeCallback{
		void beforeCreate();
		void afterCreate();
	}
	public void server_step(){
		SystemTime.tick();
		long rightNow = SystemTime.now;
		step = (now == 0 ? 0 : rightNow - now);
		now = rightNow;

		step();
	}
}
