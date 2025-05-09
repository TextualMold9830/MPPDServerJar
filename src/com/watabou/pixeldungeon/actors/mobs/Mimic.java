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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import com.nikita22007.multiplayer.noosa.audio.Sample;
import com.watabou.pixeldungeon.Assets;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.actors.Actor;
import com.watabou.pixeldungeon.actors.Char;
import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.effects.CellEmitter;
import com.watabou.pixeldungeon.effects.Speck;
import com.watabou.pixeldungeon.items.Gold;
import com.watabou.pixeldungeon.items.Item;
import com.watabou.pixeldungeon.items.scrolls.ScrollOfPsionicBlast;
import com.watabou.pixeldungeon.levels.Level;
import com.watabou.pixeldungeon.scenes.GameScene;
import com.watabou.pixeldungeon.sprites.MimicSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import static com.nikita22007.multiplayer.server.effects.Pushing.sendPushing;

public class Mimic extends com.watabou.pixeldungeon.actors.mobs.Mob {

	private int level;

	{
		name = "mimic";
		spriteClass = MimicSprite.class;
	}

	public ArrayList<Item> items;

	private static final String LEVEL	= "level";
	private static final String ITEMS	= "items";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( ITEMS, items );
		bundle.put( LEVEL, level );
	}

	@SuppressWarnings("unchecked")
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		items = new ArrayList<Item>( (Collection<Item>) ((Collection<?>) bundle.getCollection( ITEMS ) ));
		adjustStats( bundle.getInt( LEVEL ) );
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( getHT() / 10, getHT() / 4 );
	}

	@Override
	public int attackSkill( Char target ) {
		return 9 + level;
	}

	@Override
	public int attackProc( Char enemy, int damage ) {
		if (enemy instanceof Hero  && Random.Int( 3 ) == 0) {
			Gold gold = new Gold( Random.Int( ((Hero) enemy).getGold() / 10,  ((Hero) enemy).getGold() / 2 ) );
			if (gold.quantity() > 0) {
				((Hero) enemy).setGold(((Hero) enemy).getGold() - gold.quantity());
				Dungeon.level.drop( gold, enemy.pos);
			}
		}
		return super.attackProc( enemy, damage );
	}

	public void adjustStats( int level ) {
		this.level = level;

		setHT((3 + level) * 4);
		EXP = 2 + 2 * (level - 1) / 5;
		defenseSkill = attackSkill( null ) / 2;

		enemySeen = true;
	}

	@Override
	public void die( Object cause ) {

		super.die( cause );

		if (items != null) {
			for (Item item : items) {
				Dungeon.level.drop( item, pos );
			}
		}
	}

	@Override
	public boolean reset() {
		setState(WANDERING);
		return true;
	}

	@Override
	public String description() {
		return
			"Mimics are magical creatures which can take any shape they wish. In dungeons they almost always " +
			"choose a shape of a treasure chest, because they know how to beckon an adventurer.";
	}
	public static Mimic spawnAt( int pos, List<Item> items ){return spawnAt(pos,items,null);};
	public static Mimic spawnAt( int pos, List<Item> items,  Char enemy ) {
		Char ch = Actor.findChar( pos );
		if (ch != null) {
			ArrayList<Integer> candidates = new ArrayList<Integer>();
			for (int n : Level.NEIGHBOURS8) {
				int cell = pos + n;
				if ((Level.passable[cell] || Level.avoid[cell]) && Actor.findChar( cell ) == null) {
					candidates.add( cell );
				}
			}
			if (candidates.size() > 0) {
				int newPos = Random.element( candidates );
				sendPushing( ch, ch.pos, newPos );

				ch.pos = newPos;
				// FIXME
				if (ch instanceof com.watabou.pixeldungeon.actors.mobs.Mob) {
					Dungeon.level.mobPress( (Mob)ch );
				} else {
					Dungeon.level.press( newPos, ch );
				}
			} else {
				return null;
			}
		}

		Mimic m = new Mimic();
		m.items = new ArrayList<Item>( items );
		m.adjustStats( Dungeon.depth );
		m.setHP(m.getHT());
		m.pos = pos;
		m.setState(m.HUNTING);
		GameScene.add( m, 1 );
		if (!(enemy==null)) {
			m.getSprite().turnTo(pos, enemy.pos);
		}
		else{                       //TODO SEARCH MOB AROUND
			m.getSprite().turnTo(pos, Level.NEIGHBOURS8[Random.Int(7)]);
		}

		CellEmitter.get( pos ).burst( Speck.factory( Speck.STAR ), 10 );
		boolean[] visible = Dungeon.visibleForHeroes(m.pos);
		for (int ID = 0; ID < visible.length; ID++) {
			if (visible[ID]) {
				Sample.INSTANCE.play(Assets.SND_MIMIC, Dungeon.heroes[ID]);
			}
		}

		return m;
	}

	private static final HashSet<Class<?>> IMMUNITIES = new HashSet<Class<?>>();
	static {
		IMMUNITIES.add( ScrollOfPsionicBlast.class );
	}

	@Override
	public HashSet<Class<?>> immunities() {
		return IMMUNITIES;
	}
}
