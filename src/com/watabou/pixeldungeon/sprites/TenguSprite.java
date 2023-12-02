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

import com.nikita22007.multiplayer.server.sprites.MissileSprite;
import com.watabou.pixeldungeon.Assets;
import com.watabou.pixeldungeon.items.weapon.missiles.Shuriken;
import com.watabou.pixeldungeon.levels.Level;
import com.watabou.pixeldungeon.scenes.GameScene;
import com.watabou.utils.Callback;

public class TenguSprite extends MobSprite {

	private Animation cast;

	public TenguSprite() {
		super();
		texture( Assets.TENGU );
		idle = new Animation( 2, true );
		run = new Animation( 15, false );
		attack = new Animation( 15, false );
		cast  = new Animation( 15, false );
		die = new Animation( 8, false );
	}

	@Override
	public void move( int from, int to ) {

		place( to );

		play( run );
		turnTo( from , to );

		if (Level.water[to]) {
			GameScene.ripple( to );
		}

		ch.onMotionComplete();
	}

	@Override
	public void attack( int cell ) {
		if (!Level.adjacent( cell, ch.pos )) {

			MissileSprite.
				reset( ch.pos, cell, new Shuriken(), new Callback() {
					@Override
					public void call() {
						ch.onAttackComplete();
					}
				} );

			play( cast );
			turnTo( ch.pos , cell );

		} else {

			super.attack( cell );

		}
	}

	@Override
	public void onComplete( Animation anim ) {
		if (anim == run) {
			idle();
		} else {
			super.onComplete( anim );
		}
	}
}
