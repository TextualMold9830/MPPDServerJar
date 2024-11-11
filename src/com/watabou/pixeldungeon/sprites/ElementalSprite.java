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
import org.jetbrains.annotations.NotNull;

public class ElementalSprite extends MobSprite {

	public ElementalSprite() {
		super();
		texture( Assets.ELEMENTAL );
		idle = new Animation( 10, true );
		play( idle );
	}

	@Override
	public void link(@NotNull Char ch ) {
		super.link( ch );
		add( State.BURNING );
	}

	@Override
	public void die() {
		super.die();
		remove( State.BURNING );
	}

	@Override
	public int blood() {
		return 0xFFFF7D13;
	}
}
