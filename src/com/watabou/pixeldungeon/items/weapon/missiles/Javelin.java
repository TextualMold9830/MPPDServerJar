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
package com.watabou.pixeldungeon.items.weapon.missiles;

import com.watabou.pixeldungeon.actors.Char;
import com.watabou.pixeldungeon.actors.buffs.Buff;
import com.watabou.pixeldungeon.actors.buffs.Cripple;
import com.watabou.pixeldungeon.items.Item;
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class Javelin extends MissileWeapon {

	{
		name = "javelin";
		image(ItemSpriteSheet.JAVELIN);

		STR = 15;
	}

	public Javelin() {
		this( 1 );
	}

	public Javelin( int number ) {
		super();
		setQuantity(number);
	}

	@Override
	public int min() {
		return 2;
	}

	@Override
	public int max() {
		return 15;
	}

	@Override
	public void proc( Char attacker, Char defender, int damage ) {
		super.proc( attacker, defender, damage );
		Buff.prolong( defender, Cripple.class, Cripple.DURATION );
	}

	@Override
	public String desc() {
		return
			"This length of metal is weighted to keep the spike " +
			"at its tip foremost as it sails through the air.";
	}

	@Override
	public Item random() {
		setQuantity(Random.Int( 5, 15 ));
		return this;
	}

	@Override
	public int price() {
		return 15 * getQuantity();
	}
}
