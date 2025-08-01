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
package com.watabou.pixeldungeon.actors;


import com.nikita22007.multiplayer.utils.Log;
import com.watabou.noosa.Game;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.HeroHelp;
import com.watabou.pixeldungeon.PixelDungeon;
import com.watabou.pixeldungeon.Settings;
import com.watabou.pixeldungeon.actors.blobs.Blob;
import com.watabou.pixeldungeon.actors.buffs.Buff;
import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.actors.mobs.Mob;
import com.watabou.pixeldungeon.levels.Level;
import com.watabou.pixeldungeon.network.SendData;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.watabou.pixeldungeon.network.SendData.sendActorRemoving;

public abstract class Actor implements Bundlable {

	public static final float TICK	= 1f;
	private static final float CRITICAL_TIME = Float.MAX_VALUE / 2;

	private volatile float time;

	private volatile int id = 0;

	protected abstract boolean act();
	public static final int MAX_TRIES = 20; //  to free mutex

	public Level level;
	protected void spend( float time ) {
		this.time += time;
		if (this.time >= CRITICAL_TIME) {
			Actor.fixTime();
		}
	}

	protected void postpone( float time ) {
		if (this.time < now + time) {
			this.time = now + time;
		}
	}

	protected float cooldown() {
		return time - now;
	}

	protected void diactivate() {
		time = Float.MAX_VALUE;
	}

	protected void onAdd() {
		SendData.sendActor(this);
	}

	protected void onRemove() {}

	private static final String TIME	= "time";
	private static final String ID		= "id";
	private static final String LAST_LEVEL_ID = "last_level_id";

	@Override
	public void storeInBundle( Bundle bundle ) {
		bundle.put( TIME, time );
		bundle.put( ID, id );
		if( level!= null) {
			bundle.put(LAST_LEVEL_ID, level.levelID);
		}
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		time = bundle.getFloat( TIME );
		if (this instanceof Hero) {
			time = now + 1;
		}
		id = bundle.getInt( ID );
		if (bundle.contains(LAST_LEVEL_ID)){
			level = Dungeon.getLevelByID(bundle.getString(LAST_LEVEL_ID));
		}
	}

	public int id() {
		Collection<Actor> actors = all.get(level);
		if (id > 0) {
			ids.put(id, this);
			return id;
		} else {
			int max = 0;
			synchronized (all) {
				if (id > 0) {
					ids.put(id, this);
					return id;
				}
				if (actors == null || !actors.contains(this)){
					return -1;
				}
				for (Actor a : actors) {
					if (a.id > max) {
						max = a.id;
					}
				}
				 Log.i("ACTOR", String.format("ACTOR %s GOTTEN ID %d", this, max+1));
				ids.put(id, this);
				return (id = max + 1);
			}
		}
	}

	// **********************
	// *** Static members ***

	//Do not use default java Set because collection because Actor is mutable:
	//https://stackoverflow.com/questions/43553806/hashset-contains-returns-false-when-it-shouldnt
	private static final HashMap<Level, Collection<Actor>> all = new HashMap<>();
	private volatile static Actor current;

	private static float timeForAct = 2;

	private static HashMap<Integer, Actor> ids = new HashMap<>();

	private static float now = 0;

	private static com.watabou.pixeldungeon.actors.Char[] chars = new com.watabou.pixeldungeon.actors.Char[Level.LENGTH];
	public static void clear() {
		synchronized (all) {
			now = 0;

			Arrays.fill(chars, null);
			all.clear();

			ids.clear();
		}
	}

	// because "time: int32, we can have overflow of time counter
	public static void fixTime() {
		/*if (Dungeon.hero != null && all.contains( Dungeon.hero )) {
			Statistics.duration += now;
		}*/
		//TODO: fix this
//		synchronized (all) {
//			float min = Float.MAX_VALUE;
//			for (Actor a : all) {
//				if (a.time < min) {
//					min = a.time;
//				}
//			}
//			for (Actor a : all) {
//				a.time -= min;
//			}
//			now = 0;
//		}
	}

	public static void clearTime() {
		/*if (Dungeon.hero != null && all.contains( Dungeon.hero )) {
			Statistics.duration += now;
		}*/
		//Check if this is needed
//		synchronized (all) {
//			for (Actor a : all) {
//				a.time = 0;
//			}
//			now = 0;
//		}
	}

	public static void init(Level level) {
		for (Hero hero:Dungeon.heroes) {
			if (hero!=null && hero.level == level) {
				addDelayed(hero, -Float.MIN_VALUE, level);
			}
		}

		for (Mob mob : level.mobs) {
			add( mob, level );
		}

		for (Blob blob : level.blobs.values()) {
			add( blob, level );
		}

		current = null;
	}

	public static void occupyCell( com.watabou.pixeldungeon.actors.Char ch ) {
		synchronized (chars) {
			chars[ch.pos] = ch;
		}
	}

	public static void freeCell( int pos ) {
		synchronized (chars) {
			chars[pos] = null;
		}
	}

	/*protected*/public void next() {
		if (current == this) {
			current = null;
		}
	}

	public static Actor currentActor() {
		return current;
	}

	public static void process() {
		if (PixelDungeon.requestedReset()){
			return;
		}
		boolean busy_hero = false;
		if (current instanceof Hero) {
			Hero hero = (Hero) current;
			timeForAct -= Game.elapsed;
			//GLog.i("%f",timeForAct);
			if (hero.networkID == -1) {
				hero.rest(false);
			} else {
				if (hero.hasWindow())
				{
					timeForAct = Settings.timeForAct;
				} else if (timeForAct < 0) {

					hero.rest(false);
					timeForAct = Settings.timeForAct;
				}
			}
			//busy_hero = !hero.getReady();
		}
		if ((!busy_hero)&&(current != null)) {
			return;
		}

		boolean doNext;
		int tries = 0;
		do {
			synchronized (all) {
			synchronized (chars) {
					if (all.isEmpty()){
						now = 0.0F;
						return;
					}
					now = Float.MAX_VALUE;
					current = null;

					Arrays.fill(chars, null);
					for (Collection<Actor> actors: all.values()) {
						for (Actor actor : actors) {
							if (actor.time < now) {
								now = actor.time;
								current = actor;
							}

							if (actor instanceof com.watabou.pixeldungeon.actors.Char) {
								com.watabou.pixeldungeon.actors.Char ch = (com.watabou.pixeldungeon.actors.Char) actor;
								chars[ch.pos] = ch;
							}
						}
					}

					if (current != null) {
						doNext = current.act();
						if (doNext && !HeroHelp.haveAliveHero()) {
							doNext = false;
							current = null;
						}
					} else {
						doNext = false;
					}
			}
		}
			tries += 1;
			if (tries >= MAX_TRIES){
				doNext = false;
				current = null;
			}
			if (doNext)
			{
				timeForAct = Settings.timeForAct;
			}
			if (PixelDungeon.requestedReset()) {
				doNext = false;
			}
		} while (doNext);
	}

	/*
    use Actor.add() it as early as possible
    Actor.add() MUST be used BEFORE using actor.id()
    This function adds actor to Actor.all array
    If actor is not in Actor.all and gets id, some data can be lost
    */

	public static void add(@NotNull Actor actor, Level level ) {
		add( actor, now, level );
	}
	
	public static void addDelayed(@NotNull Actor actor, float delay, Level level ) {
		add( actor, now + delay, level );
	}
	
	private static void add( @NotNull Actor actor, float time, Level level ) {
		Collection<Actor> actors = all.get(level);
		if (actors == null) {
			actors = new LinkedList<>();
		}
		synchronized (all) {
			actor.level = level;
			if (actors.contains(actor)) {
				return;
			}

			if (actor.id > 0) {
				ids.put(actor.id, actor);
			}

			actors.add(actor);
			actor.time += time;
			actor.onAdd();

			if (actor instanceof com.watabou.pixeldungeon.actors.Char) {
				com.watabou.pixeldungeon.actors.Char ch = (com.watabou.pixeldungeon.actors.Char) actor;
				chars[ch.pos] = ch;
				for (Buff buff : ch.buffs()) {
					if (!actors.contains(buff)) {
						actors.add(buff);
						buff.onAdd();
					}
				}
			}
			all.put(level, actors);
		}

		SendData.sendActor(actor);
	}

	public static void remove( Actor actor ) {
		synchronized (all) {
			if (actor != null) {
				all.remove(actor);
				actor.onRemove();

				if (actor.id > 0) {
					ids.remove(actor.id);
				}
				if (current == actor){
					current = null;
				}
				sendActorRemoving(actor);
			}
		}
	}

	public static Char findChar(int pos ) {
		return chars[pos];
	}

	public static Actor findById( int id ) {
		return ids.get( id );
	}

	public static HashMap<Level, Collection<Actor>> all() {
		return all;
	}
}
