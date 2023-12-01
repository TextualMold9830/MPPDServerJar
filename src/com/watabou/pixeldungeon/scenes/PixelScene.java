/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.watabou.pixeldungeon.scenes;


import com.nikita22007.multiplayer.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Visual;
import com.watabou.pixeldungeon.Assets;
import com.watabou.pixeldungeon.Badges;
import com.watabou.pixeldungeon.PixelDungeon;
import com.watabou.pixeldungeon.effects.BadgeBanner;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class PixelScene extends Scene {

	// Minimum virtual display size for portrait orientation
	public static final float MIN_WIDTH_P		= 128;
	public static final float MIN_HEIGHT_P		= 224;

	// Minimum virtual display size for landscape orientation
	public static final float MIN_WIDTH_L		= 224;
	public static final float MIN_HEIGHT_L		= 160;

	public static float defaultZoom = 0;
	public static float minZoom;
	public static float maxZoom;

	public static Camera uiCamera;

	public PixelScene(Parent root) {
		super(root);
	}



	public void create() {


		GameScene.scene = null;

		float minWidth, minHeight;
		if (PixelDungeon.landscape()) {
			minWidth = MIN_WIDTH_L;
			minHeight = MIN_HEIGHT_L;
		} else {
			minWidth = MIN_WIDTH_P;
			minHeight = MIN_HEIGHT_P;
		}

		defaultZoom = (int)Math.ceil( Game.density * 2.5 );
		while ((
			Game.width / defaultZoom < minWidth ||
			Game.height / defaultZoom < minHeight
			) && defaultZoom > 1) {

			defaultZoom--;
		}

		if (PixelDungeon.scaleUp()) {
			while (
				Game.width / (defaultZoom + 1) >= minWidth &&
				Game.height / (defaultZoom + 1) >= minHeight) {

				defaultZoom++;
			}
		}
		minZoom = 1;
		maxZoom = defaultZoom * 2;


		float uiZoom = defaultZoom;
		}


	public void destroy() {
	}

	public static float scale;

	public static void chooseFont( float size ) {
		chooseFont( size, defaultZoom );
	}

	public static void chooseFont( float size, float zoom ) {

		float pt = size * zoom;

		if (pt >= 19) {

			scale = pt / 19;
			if (1.5 <= scale && scale < 2) {
				scale = (int)(pt / 14);
			} else {
				scale = (int)scale;
			}

		} else if (pt >= 14) {

			scale = pt / 14;
			if (1.8 <= scale && scale < 2) {
				scale = (int)(pt / 12);
			} else {
				scale = (int)scale;
			}

		} else if (pt >= 12) {

			scale = pt / 12;
			if (1.7 <= scale && scale < 2) {
				scale = (int)(pt / 10);
			} else {
				scale = (int)scale;
			}

		} else if (pt >= 10) {

			scale = pt / 10;
			if (1.4 <= scale && scale < 2) {
				scale = (int)(pt / 7);
			} else {
				scale = (int)scale;
			}

		} else {

			scale = Math.max( 1, (int)(pt / 7) );

		}

		scale /= zoom;
	}

	public static Object createText( float size ) {
		return createText( null, size );
	}

	public static Object createText( String text, float size ) {

		chooseFont( size );


		return new Object();
	}

	public static Object createMultiline( float size ) {
		return createMultiline( null, size );
	}

	public static Object createMultiline( String text, float size ) {

		chooseFont( size );

		return new Object();
	}

	public static float align( Camera camera, float pos ) {
		return 0f;
	}

	// This one should be used for UI elements
	public static float align( float pos ) {
		return ((int)(pos * defaultZoom)) / defaultZoom;
	}

	public static void align( Visual v ) {
		Camera c = v.camera();
		v.x = align( c, v.x );
		v.y = align( c, v.y );
	}

	public static boolean noFade = false;
	protected void fadeIn() {
		if (noFade) {
			noFade = false;
		} else {
			fadeIn( 0xFF000000, false );
		}
	}

	protected void fadeIn( int color, boolean light ) {

	}

	public static void showBadge( Badges.Badge badge ) {
		BadgeBanner banner = BadgeBanner.show( badge.image );
		banner.camera = uiCamera;
	}

	protected static class Fader{

		private static float FADE_TIME = 1f;

		private boolean light;

		private float time;

		public Fader( int color, boolean light ) {

			this.light = light;

			time = FADE_TIME;
		}

		public void update() {

		}

		public void draw() {
		}
	}

	private static class PixelCamera extends Camera {
		public PixelCamera( float zoom ) {
		}
	}
}
