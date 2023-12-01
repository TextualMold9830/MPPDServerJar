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
package com.watabou.pixeldungeon.actors.mobs;

import com.watabou.pixeldungeon.actors.Char;
import com.watabou.pixeldungeon.actors.mobs.Yog.BurningFist;
import com.watabou.pixeldungeon.actors.mobs.Yog.RottingFist;
import com.watabou.utils.Random;

public class Bestiary {

	public static com.watabou.pixeldungeon.actors.mobs.Mob mob(int depth ) {
		@SuppressWarnings("unchecked")
		Class<? extends com.watabou.pixeldungeon.actors.mobs.Mob> cl = (Class<? extends com.watabou.pixeldungeon.actors.mobs.Mob>)mobClass( depth );
		try {
			return cl.newInstance();
		} catch (Exception e) {
			return null;
		}
	}

	public static com.watabou.pixeldungeon.actors.mobs.Mob mutable(int depth ) {
		@SuppressWarnings("unchecked")
		Class<? extends com.watabou.pixeldungeon.actors.mobs.Mob> cl = (Class<? extends Mob>)mobClass( depth );

		if (Random.Int( 30 ) == 0) {
			if (cl == com.watabou.pixeldungeon.actors.mobs.Rat.class) {
				cl = Albino.class;
			} else if (cl == com.watabou.pixeldungeon.actors.mobs.Thief.class) {
				cl = Bandit.class;
			} else if (cl == com.watabou.pixeldungeon.actors.mobs.Brute.class) {
				cl = Shielded.class;
			} else if (cl == com.watabou.pixeldungeon.actors.mobs.Monk.class) {
				cl = Senior.class;
			} else if (cl == com.watabou.pixeldungeon.actors.mobs.Scorpio.class) {
				cl = Acidic.class;
			}
		}

		try {
			return cl.newInstance();
		} catch (Exception e) {
			return null;
		}
	}

	private static Class<?> mobClass( int depth ) {

		float[] chances;
		Class<?>[] classes;

		switch (depth) {
		case 1:
			chances = new float[]{ 1 };
			classes = new Class<?>[]{ com.watabou.pixeldungeon.actors.mobs.Rat.class };
			break;
		case 2:
			chances = new float[]{ 1, 1 };
			classes = new Class<?>[]{ com.watabou.pixeldungeon.actors.mobs.Rat.class, com.watabou.pixeldungeon.actors.mobs.Gnoll.class };
			break;
		case 3:
			chances = new float[]{ 1, 2, 1,   0.02f };
			classes = new Class<?>[]{ com.watabou.pixeldungeon.actors.mobs.Rat.class, com.watabou.pixeldungeon.actors.mobs.Gnoll.class, com.watabou.pixeldungeon.actors.mobs.Crab.class,   com.watabou.pixeldungeon.actors.mobs.Swarm.class };
			break;
		case 4:
			chances = new float[]{ 1, 2, 3,   0.02f, 0.01f, 0.01f };
			classes = new Class<?>[]{ Rat.class, com.watabou.pixeldungeon.actors.mobs.Gnoll.class, Crab.class,   com.watabou.pixeldungeon.actors.mobs.Swarm.class, Skeleton.class, com.watabou.pixeldungeon.actors.mobs.Thief.class };
			break;

		case 5:
			chances = new float[]{ 1 };
			classes = new Class<?>[]{ com.watabou.pixeldungeon.actors.mobs.Goo.class };
			break;

		case 6:
			chances = new float[]{ 4, 2, 1,   0.2f };
			classes = new Class<?>[]{ Skeleton.class, com.watabou.pixeldungeon.actors.mobs.Thief.class, com.watabou.pixeldungeon.actors.mobs.Swarm.class,   com.watabou.pixeldungeon.actors.mobs.Shaman.class };
			break;
		case 7:
			chances = new float[]{ 3, 1, 1, 1 };
			classes = new Class<?>[]{ Skeleton.class, com.watabou.pixeldungeon.actors.mobs.Shaman.class, com.watabou.pixeldungeon.actors.mobs.Thief.class, com.watabou.pixeldungeon.actors.mobs.Swarm.class };
			break;
		case 8:
			chances = new float[]{ 3, 2, 1, 1, 1,   0.02f };
			classes = new Class<?>[]{ Skeleton.class, com.watabou.pixeldungeon.actors.mobs.Shaman.class, Gnoll.class, com.watabou.pixeldungeon.actors.mobs.Thief.class, com.watabou.pixeldungeon.actors.mobs.Swarm.class,   com.watabou.pixeldungeon.actors.mobs.Bat.class };
			break;
		case 9:
			chances = new float[]{ 3, 3, 1, 1,   0.02f, 0.01f };
			classes = new Class<?>[]{ Skeleton.class, com.watabou.pixeldungeon.actors.mobs.Shaman.class, Thief.class, Swarm.class,   com.watabou.pixeldungeon.actors.mobs.Bat.class, com.watabou.pixeldungeon.actors.mobs.Brute.class };
			break;

		case 10:
			chances = new float[]{ 1 };
			classes = new Class<?>[]{ com.watabou.pixeldungeon.actors.mobs.Tengu.class };
			break;

		case 11:
			chances = new float[]{ 1,   0.2f };
			classes = new Class<?>[]{ com.watabou.pixeldungeon.actors.mobs.Bat.class,   com.watabou.pixeldungeon.actors.mobs.Brute.class };
			break;
		case 12:
			chances = new float[]{ 1, 1,   0.2f };
			classes = new Class<?>[]{ com.watabou.pixeldungeon.actors.mobs.Bat.class, com.watabou.pixeldungeon.actors.mobs.Brute.class,   com.watabou.pixeldungeon.actors.mobs.Spinner.class };
			break;
		case 13:
			chances = new float[]{ 1, 3, 1, 1,   0.02f };
			classes = new Class<?>[]{ com.watabou.pixeldungeon.actors.mobs.Bat.class, com.watabou.pixeldungeon.actors.mobs.Brute.class, com.watabou.pixeldungeon.actors.mobs.Shaman.class, com.watabou.pixeldungeon.actors.mobs.Spinner.class,    Elemental.class };
			break;
		case 14:
			chances = new float[]{ 1, 3, 1, 4,    0.02f, 0.01f };
			classes = new Class<?>[]{ Bat.class, Brute.class, Shaman.class, Spinner.class,    Elemental.class, com.watabou.pixeldungeon.actors.mobs.Monk.class };
			break;

		case 15:
			chances = new float[]{ 1 };
			classes = new Class<?>[]{ com.watabou.pixeldungeon.actors.mobs.DM300.class };
			break;

		case 16:
			chances = new float[]{ 1, 1,   0.2f };
			classes = new Class<?>[]{ Elemental.class, com.watabou.pixeldungeon.actors.mobs.Warlock.class,    com.watabou.pixeldungeon.actors.mobs.Monk.class };
			break;
		case 17:
			chances = new float[]{ 1, 1, 1 };
			classes = new Class<?>[]{ Elemental.class, com.watabou.pixeldungeon.actors.mobs.Monk.class, com.watabou.pixeldungeon.actors.mobs.Warlock.class };
			break;
		case 18:
			chances = new float[]{ 1, 2, 1, 1 };
			classes = new Class<?>[]{ Elemental.class, com.watabou.pixeldungeon.actors.mobs.Monk.class, com.watabou.pixeldungeon.actors.mobs.Golem.class, com.watabou.pixeldungeon.actors.mobs.Warlock.class };
			break;
		case 19:
			chances = new float[]{ 1, 2, 3, 1,    0.02f };
			classes = new Class<?>[]{ Elemental.class, Monk.class, Golem.class, Warlock.class,    Succubus.class };
			break;

		case 20:
			chances = new float[]{ 1 };
			classes = new Class<?>[]{ com.watabou.pixeldungeon.actors.mobs.King.class };
			break;

		case 22:
			chances = new float[]{ 1, 1 };
			classes = new Class<?>[]{ Succubus.class, com.watabou.pixeldungeon.actors.mobs.Eye.class };
			break;
		case 23:
			chances = new float[]{ 1, 2, 1 };
			classes = new Class<?>[]{ Succubus.class, com.watabou.pixeldungeon.actors.mobs.Eye.class, com.watabou.pixeldungeon.actors.mobs.Scorpio.class };
			break;
		case 24:
			chances = new float[]{ 1, 2, 3 };
			classes = new Class<?>[]{ Succubus.class, com.watabou.pixeldungeon.actors.mobs.Eye.class, Scorpio.class };
			break;

		case 25:
			chances = new float[]{ 1 };
			classes = new Class<?>[]{ com.watabou.pixeldungeon.actors.mobs.Yog.class };
			break;

		default:
			chances = new float[]{ 1 };
			classes = new Class<?>[]{ Eye.class };
		}

		return classes[ Random.chances( chances )];
	}

	public static boolean isBoss( Char mob ) {
		return
			mob instanceof Goo ||
			mob instanceof Tengu ||
			mob instanceof DM300 ||
			mob instanceof King ||
			mob instanceof Yog || mob instanceof BurningFist || mob instanceof RottingFist;
	}
}
