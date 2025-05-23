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
package com.watabou.pixeldungeon.items.potions;

import com.nikita22007.multiplayer.noosa.audio.Sample;
import com.nikita22007.multiplayer.noosa.tweeners.AlphaTweener;
import com.watabou.pixeldungeon.Assets;
import com.watabou.pixeldungeon.actors.Char;
import com.watabou.pixeldungeon.actors.buffs.Buff;
import com.watabou.pixeldungeon.actors.buffs.Invisibility;
import com.watabou.pixeldungeon.actors.hero.Hero;

public class PotionOfInvisibility extends Potion {

	private static final float ALPHA	= 0.4f;

	{
		name = "Potion of Invisibility";
	}

	@Override
	protected void apply( Hero hero ) {
		setKnown();
		Buff.affect( hero, Invisibility.class, Invisibility.DURATION );
		Sample.INSTANCE.play( Assets.SND_MELD );
	}

	@Override
	public String desc() {
		return
			"Drinking this potion will render you temporarily invisible. While invisible, " +
			"enemies will be unable to see you. Attacking an enemy, as well as using a wand or a scroll " +
			"before enemy's eyes, will dispel the effect.";
	}

	@Override
	public int price() {
		return isKnown() ? 40 * getQuantity() : super.price();
	}

	public static void melt( Char ch ) {
		if( (ch.getSprite() != null) && (ch.id() != -1)) {
			AlphaTweener.showAlphaTweener(ch.getSprite(), ALPHA, 0.4f );
		} else {
			ch.getSprite().alpha( ALPHA );
		}
	}
}
