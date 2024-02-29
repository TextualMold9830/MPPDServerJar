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

import com.nikita22007.multiplayer.noosa.particles.Emitter;
import com.nikita22007.multiplayer.noosa.particles.Emitter.Factory;
import com.watabou.pixeldungeon.Assets;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

import static com.watabou.pixeldungeon.network.SendData.sendCharSpriteAction;

public class GooSprite extends MobSprite {

	private Animation pump;
	private Animation jump;

	private Emitter spray;

	public GooSprite() {
		super();
		texture( Assets.GOO );
		idle = new Animation( 10, true );
		run = new Animation( 10, true );
		pump = new Animation( 20, true );
		jump = new Animation( 1, true );
		attack = new Animation( 10, false );
		die = new Animation( 10, false );
		play( idle );
	}

	public void pumpUp() {
		sendCharSpriteAction(ch.id(), "pump", null, null);
		play( pump );
	}

	@Override
	public void play( Animation anim, boolean force ) {
		super.play( anim, force );

		if (anim == pump) {
			spray = centerEmitter();
			spray.pour( GooParticle.FACTORY, 0.04f );
		} else if (spray != null) {
			spray.on = false;
			spray = null;
		}
	}

	@Override
	public int blood() {
		return 0xFF000000;
	}

	public static class GooParticle {

		public static final Factory FACTORY = new Factory() {

			@Override
			public String factoryName() {
				return "goo";
			}
		};

		public GooParticle() {
			super();
		}

		public void reset( float x, float y ) {
		}

		public void update() {
		}
	}
}
