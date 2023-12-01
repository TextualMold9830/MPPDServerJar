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
import com.watabou.pixeldungeon.actors.Char;
import com.watabou.pixeldungeon.actors.mobs.npcs.Imp;
import com.watabou.pixeldungeon.effects.Speck;

public class ImpSprite extends MobSprite {

	public ImpSprite() {
		super();

		texture( Assets.IMP );


		idle = new Animation( 10, true );
		run = new Animation( 20, true );

		die = new Animation( 10, false );

		play( idle );
	}

	@Override
	public void link( Char ch ) {
		super.link( ch );

		if (ch instanceof Imp) {
			alpha( 0.4f );
		}
	}

	@Override
	public void onComplete( Animation anim ) {
		if (anim == die) {

			emitter().burst( Speck.factory( Speck.WOOL ), 15 );
			killAndErase();

		} else {
			super.onComplete( anim );
		}
	}
}
