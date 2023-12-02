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
import com.watabou.pixeldungeon.effects.Speck;
import com.watabou.pixeldungeon.effects.particles.ShadowParticle;

public class SuccubusSprite extends MobSprite {

	public SuccubusSprite() {
		super();
		texture( Assets.SUCCUBUS );
		idle = new Animation( 8, true );
		run = new Animation( 15, true );
		attack = new Animation( 12, false );
		die = new Animation( 10, false );
		play( idle );
	}

	@Override
	public void die() {
		super.die();
		emitter().burst( Speck.factory( Speck.HEART ), 6 );
		emitter().burst( ShadowParticle.UP, 8 );
	}
}
