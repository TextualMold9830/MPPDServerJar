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
package com.watabou.pixeldungeon.sprites;

import com.watabou.pixeldungeon.Assets;
import com.watabou.pixeldungeon.actors.mobs.Shaman;
import com.nikita22007.multiplayer.server.effects.Lightning;

public class ShamanSprite extends MobSprite {

	private int[] points = new int[2];

	public ShamanSprite() {
		super();
		texture( Assets.SHAMAN );
		idle = new Animation( 2, true );
		play( idle );
	}

	public void zap( int pos ) {

		points[0] = ch.pos;
		points[1] = pos;
		Lightning.showLightning( points, 2, (Shaman)ch );

		turnTo( ch.pos, pos );
		play( zap );
	}
}
