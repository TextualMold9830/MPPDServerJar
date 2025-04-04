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
package com.watabou.pixeldungeon.actors.mobs.npcs;

import com.watabou.pixeldungeon.actors.Char;
import com.watabou.pixeldungeon.actors.buffs.Buff;

import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.sprites.RatKingSprite;

public class RatKing extends NPC {

	{
		name = "rat king";
		spriteClass = RatKingSprite.class;

		setState(SLEEPEING);
	}

	@Override
	public int defenseSkill( Char enemy ) {
		return 1000;
	}

	@Override
	public float speed() {
		return 2f;
	}

	@Override
	protected Char chooseEnemy() {
		return null;
	}

	@Override
	public void damage( int dmg, Object src ) {
	}

	@Override
	public void add( Buff buff ) {
	}

	@Override
	public boolean reset() {
		return true;
	}

	@Override
	public void interact(Hero hero) {
		getSprite().turnTo( pos, hero.pos );
		if (getState() == SLEEPEING) {
			notice();
			yell( "I'm not sleeping!" );
			setState(WANDERING);
		} else {
			yell( "What is it? I have no time for this nonsense. My kingdom won't rule itself!" );
		}
	}

	@Override
	public String description() {
		return
			"This rat is a little bigger than a regular marsupial rat " +
			"and it's wearing a tiny crown on its head.";
	}
}
