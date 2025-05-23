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

import java.util.HashSet;

import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.actors.Char;
import com.watabou.pixeldungeon.actors.buffs.Poison;
import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.actors.mobs.Mob;
import com.watabou.pixeldungeon.sprites.BeeSprite;
import com.watabou.pixeldungeon.utils.Utils;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Bee extends NPC {
	private Hero owner;
	public Bee(Hero owner)
	{
		name = "golden bee";
		spriteClass = BeeSprite.class;

		viewDistance = 4;

		WANDERING = new Wandering();

		this.owner=owner;
		alignment = owner.alignment;
		flying = true;
		setState(WANDERING);
	}

	private int level;

	private static final String LEVEL	= "level";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( LEVEL, level );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		spawn( bundle.getInt( LEVEL ) );
	}

	public void spawn( int level ) {
		this.level = level;

		setHT((3 + level) * 5);
		defenseSkill = 9 + level;
	}

	@Override
	public int attackSkill( Char target ) {
		return defenseSkill;
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( getHT() / 10, getHT() / 4 );
	}

	@Override
	public int attackProc( Char enemy, int damage ) {
		if (enemy instanceof Mob) {
			((Mob)enemy).aggro( this );
		}
		return damage;
	}

	@Override
	protected boolean act() {
		setHP(getHP() - 1);
		if (getHP() <= 0) {
			die( null );
			return true;
		} else {
			return super.act();
		}
	}

	protected Char chooseEnemy() {

		if (enemy == null || !enemy.isAlive()) {
			HashSet<Mob> enemies = new HashSet<Mob>();
			for (Mob mob:Dungeon.level.mobs) {
				if (mob.hostile && this.fieldOfView[mob.pos]) {
					enemies.add( mob );
				}
			}

			return enemies.size() > 0 ? Random.element( enemies ) : null;

		} else {

			return enemy;

		}
	}

	@Override
	public String description() {
		return
			"Despite their small size, golden bees tend " +
			"to protect their master fiercely. They don't live long though.";
	}

	@Override
	public void interact(Hero hero) {

		int curPos = pos;

		moveSprite( pos, hero.pos );
		move( hero.pos );

		hero.getSprite().move( hero.pos, curPos );
		hero.move( curPos );

		hero.spend( 1 / hero.speed() );
		hero.busy();
	}

	private static final HashSet<Class<?>> IMMUNITIES = new HashSet<Class<?>>();
	static {
		IMMUNITIES.add( Poison.class );
	}

	@Override
	public HashSet<Class<?>> immunities() {
		return IMMUNITIES;
	}

	private class Wandering implements AiState {

		@Override
		public boolean act( boolean enemyInFOV, boolean justAlerted ) {
			if (enemyInFOV) {

				enemySeen = true;

				notice();
				setState(HUNTING);
				target = enemy.pos;

			} else {

				enemySeen = false;

				int oldPos = pos;
				if (getCloser( owner.pos )) {
					spend( 1 / speed() );
					return moveSprite( oldPos, pos );
				} else {
					spend( TICK );
				}

			}
			return true;
		}

		@Override
		public String status() {
			return Utils.format( "This %s is wandering", name );
		}
	}
}
