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

import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.pixeldungeon.Assets;
import com.watabou.pixeldungeon.ui.RedButton;
import com.watabou.utils.Random;

public class AmuletScene extends PixelScene {

	private static final String TXT_EXIT	= "Let's call it a day";
	private static final String TXT_STAY	= "I'm not done yet";

	private static final int WIDTH			= 120;
	private static final int BTN_HEIGHT		= 18;
	private static final float SMALL_GAP	= 2;
	private static final float LARGE_GAP	= 8;

	private static final String TXT =
		"You finally hold it in your hands, the Amulet of Yendor. Using its power " +
		"you can take over the world or bring peace and prosperity to people or whatever. " +
		"Anyway, your life will change forever and this game will end here. " +
		"Or you can stay a mere mortal a little longer.";

	public static boolean noText = false;

	private Image amulet;

	@Override
	public void create() {
		super.create();

		if (!noText) {
		}

		amulet = new Image(Assets.AMULET);

		RedButton btnExit = new RedButton(TXT_EXIT);

		RedButton btnStay = new RedButton(TXT_STAY);

	}


	private float timer = 0;

	public void update() {

		if ((timer -= Game.elapsed) < 0) {
			timer = Random.Float( 0.5f, 5f );

		}
	}
}
