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

import com.nikita22007.multiplayer.noosa.audio.Sample;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.pixeldungeon.Assets;
import com.watabou.pixeldungeon.Badges;
import com.watabou.pixeldungeon.GamesInProgress;
import com.watabou.pixeldungeon.PixelDungeon;
import com.watabou.pixeldungeon.actors.hero.HeroClass;
import com.watabou.pixeldungeon.effects.BannerSprites;
import com.watabou.pixeldungeon.effects.BannerSprites.Type;
import com.watabou.pixeldungeon.ui.ExitButton;
import com.watabou.pixeldungeon.ui.RedButton;
import com.watabou.pixeldungeon.utils.Utils;
import com.watabou.utils.Callback;

import java.awt.*;
import java.util.HashMap;

import static com.watabou.pixeldungeon.BuildConfig.DEBUG;

public class StartScene extends com.watabou.pixeldungeon.scenes.PixelScene {			//client  Scene

	private static final float BUTTON_HEIGHT	= 24;
	private static final float GAP				= 2;

	private static final String TXT_LOAD	= "Load Game";
	private static final String TXT_NEW		= "New Game";

	private static final String TXT_ERASE		= "Erase current game";
	private static final String TXT_DPTH_LVL	= "Depth: %d, level: %d";

	private static final String TXT_REALLY	= "Do you really want to start new game?";
	private static final String TXT_WARNING	= "Your current game progress will be erased.";
	private static final String TXT_YES		= "Yes, start new game";
	private static final String TXT_NO		= "No, return to main menu";

	private static final float WIDTH_P	= 116;
	private static final float HEIGHT_P	= 220;

	private static final float WIDTH_L	= 224;
	private static final float HEIGHT_L	= 124;

	private static HashMap<HeroClass, ClassShield> shields = new HashMap<HeroClass, ClassShield>();

	private float buttonX;
	private float buttonY;

	private GameButton btnLoad;
	private GameButton btnNewGame;

	public static HeroClass curClass;

	@Override
	public void create() {

		super.create();

		Badges.loadGlobal();



		float width, height;
		if (PixelDungeon.landscape()) {
			width = WIDTH_L;
			height = HEIGHT_L;
		} else {
			width = WIDTH_P;
			height = HEIGHT_P;
		}


		Image title = BannerSprites.get( Type.SELECT_YOUR_HERO );


		btnNewGame = new GameButton( TXT_NEW );

		btnLoad = new GameButton( TXT_LOAD );

		float centralHeight = buttonY - title.y - title.height();

		HeroClass[] classes = {
			HeroClass.WARRIOR, HeroClass.MAGE, HeroClass.ROGUE, HeroClass.HUNTRESS
		};
		for (HeroClass cl : classes) {
			ClassShield shield = new ClassShield( cl );
			shields.put( cl, shield );
		}
		if (PixelDungeon.landscape()) {
			float shieldW = width / 4;
			float shieldH = Math.min( centralHeight, shieldW );
			for (int i=0; i < classes.length; i++) {
				ClassShield shield = shields.get( classes[i] );
			}

			ChallengeButton challenge = new ChallengeButton();

		} else {
			float shieldW = width / 2;
			float shieldH = Math.min( centralHeight / 2, shieldW * 1.2f );
			for (int i=0; i < classes.length; i++) {
				ClassShield shield = shields.get( classes[i] );
			}

			ChallengeButton challenge = new ChallengeButton();
		}

		ExitButton btnExit = new ExitButton();

		curClass = null;
		updateClass( HeroClass.values()[PixelDungeon.lastClass()] );

		fadeIn();

		Badges.loadingListener = new Callback() {
			@Override
			public void call() {
				if (Game.scene() == StartScene.this) {
					PixelDungeon.switchNoFade( StartScene.class );
				}
			}
		};
	}

	@Override
	public void destroy() {

		Badges.saveGlobal();
		Badges.loadingListener = null;

		super.destroy();
	}

	private void updateClass( HeroClass cl ) {

		if (curClass == cl) {
			return;
		}

		if (curClass != null) {
			shields.get(curClass).highlight(false);
		}
		shields.get(curClass = cl).highlight(true);

		GamesInProgress.Info info = GamesInProgress.check(curClass);
		if (DEBUG && (info != null)) {

			btnLoad.secondary(Utils.format(TXT_DPTH_LVL, info.depth, info.level), info.challenges);

			btnNewGame.secondary(TXT_ERASE, false);



		} else {

			btnNewGame.secondary(null, false);
		}
	}





	private static class GameButton extends RedButton {

		private static final int SECONDARY_COLOR_N	= 0xCACFC2;
		private static final int SECONDARY_COLOR_H	= 0xFFFF88;


		public GameButton( String primary ) {
			super( primary );

		}

		@Override
		protected void createChildren() {
			super.createChildren();

		}

		@Override
        public void layout() {
			super.layout();
		}

		public void secondary( String text, boolean highlighted ) {
		}
	}

	private class ClassShield extends Button {

		private static final float MIN_BRIGHTNESS	= 0.6f;

		private static final int BASIC_NORMAL		= 0x444444;
		private static final int BASIC_HIGHLIGHTED	= 0xCACFC2;

		private static final int MASTERY_NORMAL		= 0x666644;
		private static final int MASTERY_HIGHLIGHTED= 0xFFFF88;

		private static final int WIDTH	= 24;
		private static final int HEIGHT	= 28;
		private static final int SCALE	= 2;

		private HeroClass cl;

		private Image avatar;

		private float brightness;

		private int normal;
		private int highlighted;

		public ClassShield( HeroClass cl ) {
			super();

			this.cl = cl;

			avatar.frame( cl.ordinal() * WIDTH, 0, WIDTH, HEIGHT );
			avatar.scale.set( SCALE );

			if (Badges.isUnlocked( cl.masteryBadge() )) {
				normal = MASTERY_NORMAL;
				highlighted = MASTERY_HIGHLIGHTED;
			} else {
				normal = BASIC_NORMAL;
				highlighted = BASIC_HIGHLIGHTED;
			}

			brightness = MIN_BRIGHTNESS;
			updateBrightness();
		}


		@Override
		public void layout() {

			super.layout();

		}


		public void highlight( boolean value ) {
			if (value) {
				brightness = 1.0f;
			} else {
				brightness = 0.999f;
			}

			updateBrightness();
		}

		private void updateBrightness() {
			avatar.gm = avatar.bm = avatar.rm = avatar.am = brightness;
		}
	}

	private class ChallengeButton extends Button {

		private Image image;

		public ChallengeButton() {
			super();
			image.am = Badges.isUnlocked( Badges.Badge.VICTORY ) ? 1.0f : 0.5f;
		}

		@Override
		public void layout() {

			super.layout();

		}
		protected void onTouchDown() {
			Sample.INSTANCE.play( Assets.SND_CLICK );
		}
	}
}
