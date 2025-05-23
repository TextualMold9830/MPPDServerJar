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
package com.watabou.pixeldungeon.actors.hero;

import com.nikita22007.multiplayer.noosa.Camera;
import com.nikita22007.multiplayer.noosa.audio.Sample;
import com.nikita22007.multiplayer.server.effects.Flare;
import com.nikita22007.multiplayer.server.ui.AttackIndicator;
import com.watabou.pixeldungeon.*;
import com.watabou.pixeldungeon.actors.Actor;
import com.watabou.pixeldungeon.actors.Alignment;
import com.watabou.pixeldungeon.actors.Char;
import com.watabou.pixeldungeon.actors.buffs.*;
import com.watabou.pixeldungeon.actors.mobs.Mob;
import com.watabou.pixeldungeon.actors.mobs.npcs.NPC;
import com.watabou.pixeldungeon.effects.CheckedCell;
import com.watabou.pixeldungeon.effects.Speck;
import com.watabou.pixeldungeon.items.*;
import com.watabou.pixeldungeon.items.Heap.Type;
import com.watabou.pixeldungeon.items.armor.Armor;
import com.watabou.pixeldungeon.items.keys.GoldenKey;
import com.watabou.pixeldungeon.items.keys.IronKey;
import com.watabou.pixeldungeon.items.keys.Key;
import com.watabou.pixeldungeon.items.keys.SkeletonKey;
import com.watabou.pixeldungeon.items.potions.Potion;
import com.watabou.pixeldungeon.items.potions.PotionOfHealing;
import com.watabou.pixeldungeon.items.potions.PotionOfMight;
import com.watabou.pixeldungeon.items.potions.PotionOfStrength;
import com.watabou.pixeldungeon.items.rings.*;
import com.watabou.pixeldungeon.items.scrolls.*;
import com.watabou.pixeldungeon.items.wands.Wand;
import com.watabou.pixeldungeon.items.weapon.melee.MeleeWeapon;
import com.watabou.pixeldungeon.items.weapon.missiles.MissileWeapon;
import com.watabou.pixeldungeon.levels.Level;
import com.watabou.pixeldungeon.levels.Terrain;
import com.watabou.pixeldungeon.levels.features.AlchemyPot;
import com.watabou.pixeldungeon.levels.features.Chasm;
import com.watabou.pixeldungeon.levels.features.Sign;
import com.watabou.pixeldungeon.network.SendData;
import com.watabou.pixeldungeon.network.Server;
import com.watabou.pixeldungeon.plants.Earthroot;
import com.watabou.pixeldungeon.scenes.CellSelector;
import com.watabou.pixeldungeon.scenes.GameScene;
import com.watabou.pixeldungeon.scenes.InterLevelSceneServer;
import com.watabou.pixeldungeon.sprites.CharSprite;
import com.watabou.pixeldungeon.sprites.HeroSprite;
import com.watabou.pixeldungeon.ui.Window;
import com.watabou.pixeldungeon.utils.GLog;
import com.watabou.pixeldungeon.windows.WndMessage;
import com.watabou.pixeldungeon.windows.WndResurrect;
import com.watabou.pixeldungeon.windows.WndTradeItem;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import textualmold9830.Preferences;
import textualmold9830.plugins.events.HeroDoActionEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;

import static com.watabou.pixeldungeon.network.SendData.*;

public class Hero extends Char {

	private static final String TXT_LEAVE = "One does not simply leave Pixel Dungeon.";
	private static final String TXT_NO_RETURN_ALLOWED = "You'd swear there were up stairs  here.";
	private int gold;

	private static final String TXT_LEVEL_UP = "level up!";
	private static final String TXT_NEW_LEVEL =
		"Welcome to level %d! Now you are healthier and more focused. " +
		"It's easier for you to hit enemies and dodge their attacks.";

	public static final String TXT_YOU_NOW_HAVE	= "You now have %s";
	public static final String TXT_SB_NOW_HAVE	= "%s now has %s";

	private static final String TXT_SOMETHING_ELSE	= "There is something else here";
	private static final String TXT_LOCKED_CHEST	= "This chest is locked and you don't have matching key";
	private static final String TXT_LOCKED_DOOR		= "You don't have a matching key";
	private static final String TXT_NOTICED_SMTH	= "You noticed something";

	private static final String TXT_WAIT	= "...";
	private static final String TXT_SEARCH	= "search";

	public static final int STARTING_STR = 10;

	private static final float TIME_TO_REST		= 1f;
	private static final float TIME_TO_SEARCH	= 2f;

	public HeroClass heroClass = HeroClass.ROGUE;
	public HeroSubClass subClass = HeroSubClass.NONE;

	private int attackSkill = 10;
	private int defenseSkill = 5;

    public static AttackIndicator attackIndicator;

	private boolean ready = false;

	public HeroAction curAction = null;
	public HeroAction lastAction = null;

	private Char enemy;

	public Armor.Glyph killerGlyph = null;

	private Item theKey;

	public boolean restoreHealth = false;

	public MissileWeapon rangedWeapon = null;
	public Belongings belongings;

	protected int STR;
	//public boolean weakened = false;

	public float awareness;

	public int lvl = 1;
	public int exp = 0;

	public int networkID = -1;

	private ArrayList<Char> visibleEnemies;
	private String uuid = UUID.randomUUID().toString();
	private int lastDepth = Dungeon.depth;

	public Hero() {
		super();
		alignment = Alignment.ALLY;
		final  Hero hero =  this;
		defaultCellListener= new CellSelector.Listener() { //client
			@Override
			public void onSelect( Integer cell ) {
				if(hero.ready) {
					if (hero.handle(cell)) {
						hero.next();
					}
				}
			}
			@Override
			public String prompt() {
				return null;
			}
		};
		attackIndicator=new AttackIndicator(this);
		name = "you";

		setHP(setHT(20));
		STR = STARTING_STR;
		awareness = 0.1f;

		belongings = new Belongings( this );

		visibleEnemies = new ArrayList<Char>();
		cellSelector = new CellSelector(this);
		setSprite(new HeroSprite(this));
	}
	public Hero(String uuid) {
		this();
		this.uuid = uuid;
	}
	public String getUUID(){
		return uuid;
	}

	public int STR() {
		return this.buff(Weakness.class) != null ? STR - 2 : STR; //it was "weakened", but this is more easy
	}

	private static final String ATTACK		= "attackSkill";
	private static final String DEFENSE		= "defenseSkill";
	private static final String STRENGTH	= "STR";
	private static final String GOLD        = "gold";
	private static final String LEVEL		= "lvl";
	private static final String EXPERIENCE	= "exp";
	private static final String SECRET = "uuid";
	private static final String LAST_DEPTH = "last_depth";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );

		heroClass.storeInBundle( bundle );
		subClass.storeInBundle( bundle );

		bundle.put( ATTACK, attackSkill );
		bundle.put( DEFENSE, defenseSkill );

		bundle.put( STRENGTH, STR );

		bundle.put( LEVEL, lvl );
		bundle.put( EXPERIENCE, exp );

		bundle.put( GOLD, getGold());
		bundle.put(SECRET, uuid);
		bundle.put(LAST_DEPTH, Dungeon.depth);
		belongings.storeInBundle( bundle );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );

		heroClass = HeroClass.restoreInBundle( bundle );
		subClass = HeroSubClass.restoreInBundle( bundle );

		attackSkill = bundle.getInt( ATTACK );
		defenseSkill = bundle.getInt( DEFENSE );

		STR = bundle.getInt( STRENGTH );
		updateAwareness();

		lvl = bundle.getInt( LEVEL );
		exp = bundle.getInt( EXPERIENCE );
		uuid = bundle.getString(SECRET);
		belongings.restoreFromBundle( bundle );
		lastDepth = bundle.getInt(LAST_DEPTH);
		if (lastDepth != Dungeon.depth) {
			pos = Dungeon.level.entrance;
		}
	}

	public static void preview( GamesInProgress.Info info, Bundle bundle ) {
		info.level = -1;
	}

	public String className() {
		return subClass == null || subClass == HeroSubClass.NONE ? heroClass.title() : subClass.title();
	}

	public void live() {
		Buff.affect( this, Regeneration.class );
		Buff.affect( this, Hunger.class );
	}

	public int tier() {
		return belongings.getArmor() == null ? 0 : belongings.getArmor().tier;
	}

	public boolean shoot( Char enemy, MissileWeapon wep ) {

		rangedWeapon = wep;
		boolean result = attack( enemy );
		rangedWeapon = null;

		return result;
	}

	@Override
	public int attackSkill( Char target ) {

		int bonus = 0;
		for (Buff buff : buffs( RingOfAccuracy.Accuracy.class )) {
			bonus += ((RingOfAccuracy.Accuracy)buff).level;
		}
		float accuracy = (bonus == 0) ? 1 : (float)Math.pow( 1.4, bonus );
		if (rangedWeapon != null && Level.distance( pos, target.pos ) == 1) {
			accuracy *= 0.5f;
		}

		KindOfWeapon wep = rangedWeapon != null ? rangedWeapon : belongings.getWeapon();
		if (wep != null) {
			return (int)(attackSkill * accuracy * wep.acuracyFactor( this ));
		} else {
			return (int)(attackSkill * accuracy);
		}
	}

	@Override
	public int defenseSkill( Char enemy ) {

		int bonus = 0;
		for (Buff buff : buffs( RingOfEvasion.Evasion.class )) {
			bonus += ((RingOfEvasion.Evasion)buff).level;
		}
		float evasion = bonus == 0 ? 1 : (float)Math.pow( 1.2, bonus );
		if (paralysed) {
			evasion /= 2;
		}

		int aEnc = belongings.getArmor() != null ? belongings.getArmor().STR - STR() : 0;

		if (aEnc > 0) {
			return (int)(defenseSkill * evasion / Math.pow( 1.5, aEnc ));
		} else {

			if (heroClass == HeroClass.ROGUE) {

				if (curAction != null && subClass == HeroSubClass.FREERUNNER && !isStarving()) {
					evasion *= 2;
				}

				return (int)((defenseSkill - aEnc) * evasion);
			} else {
				return (int)(defenseSkill * evasion);
			}
		}
	}

	@Override
	public int dr() {
		int dr = belongings.getArmor() != null ? Math.max( belongings.getArmor().DR(), 0 ) : 0;
		Barkskin barkskin = buff( Barkskin.class );
		if (barkskin != null) {
			dr += barkskin.level();
		}
		return dr;
	}

	@Override
	public int damageRoll() {
		KindOfWeapon wep = rangedWeapon != null ? rangedWeapon : belongings.getWeapon();
		int dmg;
		if (wep != null) {
			dmg = wep.damageRoll( this );
		} else {
			dmg = STR() > 10 ? Random.IntRange( 1, STR() - 9 ) : 1;
		}
		return buff( Fury.class ) != null ? (int)(dmg * 1.5f) : dmg;
	}

	@Override
	public float speed() {

		int aEnc = belongings.getArmor() != null ? belongings.getArmor().STR - STR() : 0;
		if (aEnc > 0) {

			return (float)(super.speed() * Math.pow( 1.3, -aEnc ));

		} else {

			float speed = super.speed();
			return ((HeroSprite) getSprite()).sprint( subClass == HeroSubClass.FREERUNNER && !isStarving() ) ? 1.6f * speed : speed;

		}
	}

	public float attackDelay() {
		KindOfWeapon wep = rangedWeapon != null ? rangedWeapon : belongings.getWeapon();
		if (wep != null) {

			return wep.speedFactor( this );

		} else {
			return 1f;
		}
	}

	@Override
	public void spend( float time ) {
		int hasteLevel = 0;
		for (Buff buff : buffs( RingOfHaste.Haste.class )) {
			hasteLevel += ((RingOfHaste.Haste)buff).level;
		}
		super.spend( hasteLevel == 0 ? time : (float)(time * Math.pow( 1.1, -hasteLevel )) );
	};

	public void spendAndNext( float time ) {
		busy();
		spend( time );
		next();
	}

	@Override
	public boolean act() {

		if (networkID < 0){
			rest(false);
			return true;
		}

		super.act();

		if (paralysed) {

			curAction = null;

			spendAndNext( TICK );
			return false;
		}

		checkVisibleEnemies();
		attackIndicator.updateState();

		if (curAction == null) {

			if (restoreHealth) {
				if (isStarving() || getHP() >= getHT()) {
					restoreHealth = false;
				} else {
					spend( TIME_TO_REST ); next();
					return false;
				}
			}

			ready();
			return false;

		} else {

			restoreHealth = false;
			setReady(false);
			HeroDoActionEvent event = new HeroDoActionEvent(this, curAction);
			Server.pluginManager.fireEvent(event);
			curAction = event.action;
			if (!event.isCancelled()) {
				if (curAction instanceof HeroAction.Move) {
					return actMove((HeroAction.Move) curAction);

				} else if (curAction instanceof HeroAction.Interact) {

					return actInteract((HeroAction.Interact) curAction);

				} else if (curAction instanceof HeroAction.Buy) {

					return actBuy((HeroAction.Buy) curAction);

				} else if (curAction instanceof HeroAction.PickUp) {

					return actPickUp((HeroAction.PickUp) curAction);

				} else if (curAction instanceof HeroAction.OpenChest) {

					return actOpenChest((HeroAction.OpenChest) curAction);

				} else if (curAction instanceof HeroAction.Unlock) {

					return actUnlock((HeroAction.Unlock) curAction);

				} else if (curAction instanceof HeroAction.Descend) {

					return actDescend((HeroAction.Descend) curAction);

				} else if (curAction instanceof HeroAction.Ascend) {

					return actAscend((HeroAction.Ascend) curAction);

				} else if (curAction instanceof HeroAction.Attack) {

					return actAttack((HeroAction.Attack) curAction);

				} else if (curAction instanceof HeroAction.Cook) {

					return actCook((HeroAction.Cook) curAction);

				}
			}
		}

		return false;
	}

	public void busy() {
		setReady(false);
	}

	private void ready() {
		getSprite().idle();
		curAction = null;
		setReady(true);

		GameScene.ready(this);
	}

	private void setReady(boolean ready){
		this.ready = ready;
		SendData.sendHeroReady(this.networkID,ready);
	}

	public void resendReady(){
		setReady(getReady());
	}


	public boolean getReady(){
		return ready;
	}

	public void interrupt() {
		if (isAlive() && curAction != null && curAction.dst != pos) {
			lastAction = curAction;
			sendResumeButtonVisible(this.networkID, true);
		}
		curAction = null;
	}

	public void resume() {
		if (isAlive() /*&& curAction==null && lastAction != null*/ ) {
			curAction = lastAction;
			lastAction = null;
			act();
		}
		sendResumeButtonVisible(this.networkID, false);
	}

	private boolean actMove( HeroAction.Move action ) {

		if (getCloser( action.dst )) {

			return true;

		} else {
			if (Dungeon.level.map[pos] == Terrain.SIGN) {
				Sign.read(pos, this);
			}
			ready();

			return false;
		}
	}

	private boolean actInteract( HeroAction.Interact action ) {

		NPC npc = action.npc;

		if (Level.adjacent( pos, npc.pos )) {

			ready();
			getSprite().turnTo( pos, npc.pos );
			npc.interact(this);
			return false;

		} else {

			if (this.fieldOfView[npc.pos] && getCloser( npc.pos )) {

				return true;

			} else {
				ready();
				return false;
			}

		}
	}

	private boolean actBuy( HeroAction.Buy action ) {
		int dst = action.dst;
		if (pos == dst || Level.adjacent( pos, dst )) {

			ready();

			Heap heap = Dungeon.level.heaps.get( dst );
			if (heap != null && heap.type == Type.FOR_SALE && heap.size() == 1) {
				GameScene.show( new WndTradeItem( heap, true, this ) );
			}

			return false;

		} else if (getCloser( dst )) {

			return true;

		} else {
			ready();
			return false;
		}
	}

	private boolean actCook( HeroAction.Cook action ) {
		int dst = action.dst;
		if (this.fieldOfView[pos]) {

			ready();
			AlchemyPot.operate( this, dst );
			return false;

		} else if (getCloser( dst )) {

			return true;

		} else {
			ready();
			return false;
		}
	}

	private boolean actPickUp( HeroAction.PickUp action ) {
		int dst = action.dst;
		if (pos == dst) {

			Heap heap = Dungeon.level.heaps.get( pos );
			if (heap != null) {
				Item item = heap.pickUp();
				if (item.doPickUp( this )) {

					if (item instanceof Dewdrop) {
						// Do nothing
					} else {
						boolean important =
							((item instanceof ScrollOfUpgrade || item instanceof ScrollOfEnchantment) && ((Scroll)item).isKnown()) ||
							((item instanceof PotionOfStrength || item instanceof PotionOfMight) && ((Potion)item).isKnown());
						if (important) {
							GLog.pWithTarget(networkID, TXT_YOU_NOW_HAVE, item.name() );
							for (int i = 0; i < Dungeon.heroes.length; i++)
							if (Preferences.itemCollectedMessageMode == Preferences.ITEM_COLLECTED_MESSAGE_MODE.SHOW_FOR_ALL_NOT_IMPORTANT)
							{
								important = false;
							}
						} else {
							GLog.iWithTarget(networkID, TXT_YOU_NOW_HAVE, item.name() );
						}
						if (Preferences.itemCollectedMessageMode != Preferences.ITEM_COLLECTED_MESSAGE_MODE.DONT_SHOW_FOR_ALL)
						{
							if (important)
							{
								GLog.pExceptTarget(networkID, TXT_SB_NOW_HAVE, this.name, item.name());
							} else {
								GLog.iExceptTarget(networkID, TXT_SB_NOW_HAVE, this.name, item.name());
							}
						}
					}

					if (!heap.isEmpty()) {
						GLog.i( TXT_SOMETHING_ELSE );
					}
					curAction = null;
				} else {
					Dungeon.level.drop( item, pos );
					ready();
				}
			} else {
				ready();
			}

			return false;

		} else if (getCloser( dst )) {

			return true;

		} else {
			ready();
			return false;
		}
	}

	private boolean actOpenChest( HeroAction.OpenChest action ) {
		int dst = action.dst;
		if (Level.adjacent( pos, dst ) || pos == dst) {

			Heap heap = Dungeon.level.heaps.get( dst );
			if (heap != null && (heap.type != Type.HEAP && heap.type != Type.FOR_SALE)) {

				theKey = null;

				if (heap.type == Type.LOCKED_CHEST || heap.type == Type.CRYSTAL_CHEST) {

					theKey = belongings.getKey( GoldenKey.class, Dungeon.depth );

					if (theKey == null) {
						GLog.wWithTarget(HeroHelp.getHeroID(this), TXT_LOCKED_CHEST );
						ready();
						return false;
					}
				}

				switch (heap.type) {
				case TOMB:
					Sample.INSTANCE.play( Assets.SND_TOMB );
					Camera.shake( 1, 0.5f );
					break;
				case SKELETON:
					break;
				default:
					Sample.INSTANCE.play( Assets.SND_UNLOCK );
				}

				spend( Key.TIME_TO_UNLOCK );
				getSprite().operate( dst );

			} else {
				ready();
			}

			return false;

		} else if (getCloser( dst )) {

			return true;

		} else {
			ready();
			return false;
		}
	}

	private boolean actUnlock( HeroAction.Unlock action ) {
		int doorCell = action.dst;
		if (Level.adjacent( pos, doorCell )) {

			theKey = null;
			int door = Dungeon.level.map[doorCell];

			if (door == Terrain.LOCKED_DOOR) {

				theKey = belongings.getKey( IronKey.class, Dungeon.depth );

			} else if (door == Terrain.LOCKED_EXIT) {

				theKey = belongings.getKey( SkeletonKey.class, Dungeon.depth );

			}

			if (theKey != null) {

				spend( Key.TIME_TO_UNLOCK );
				getSprite().operate( doorCell );

				Sample.INSTANCE.play( Assets.SND_UNLOCK );

			} else {
				GLog.w( TXT_LOCKED_DOOR );
				ready();
			}

			return false;

		} else if (getCloser( doorCell )) {

			return true;

		} else {
			ready();
			return false;
		}
	}

	private boolean actDescend( HeroAction.Descend action ) {
		int stairs = action.dst;
		if (pos == stairs && pos == Dungeon.level.exit) {

			curAction = null;

			Hunger hunger = buff( Hunger.class );
			if (hunger != null && !hunger.isStarving()) {
				hunger.satisfy( -Hunger.STARVING / 10 );
			}

			//InterlevelScene.mode = InterlevelScene.Mode.DESCEND;
			InterLevelSceneServer.descend(this);
			//Game.switchScene( InterlevelScene.class );

			return false;

		} else if (getCloser( stairs )) {

			return true;

		} else {
			ready();
			return false;
		}
	}

	private boolean actAscend( HeroAction.Ascend action ) {
		int stairs = action.dst;
		if (pos == stairs && pos == Dungeon.level.entrance) {
			if (Settings.returnDisabled && (!BuildConfig.DEBUG)){
				GameScene.show(new WndMessage(TXT_NO_RETURN_ALLOWED, this));
				ready();
				return false;
			}
			if (Dungeon.depth <= 1) {

				if (belongings.getItem( Amulet.class ) == null) {
					GameScene.show(new WndMessage(TXT_LEAVE, this));
					ready();
				} else {
					Dungeon.win( ResultDescriptions.WIN );
				}

			} else {

				curAction = null;

				Hunger hunger = buff( Hunger.class );
				if (hunger != null && !hunger.isStarving()) {
					hunger.satisfy( -Hunger.STARVING / 10 );
				}

				InterLevelSceneServer.ascend(this);
			}

			return false;

		} else if (getCloser( stairs )) {

			return true;

		} else {
			ready();
			return false;
		}
	}

	private boolean actAttack( HeroAction.Attack action ) {

		enemy = action.target;

		if (Level.adjacent( pos, enemy.pos ) && enemy.isAlive() && !isCharmedBy( enemy )) {

			spend( attackDelay() );
			getSprite().attack( enemy.pos );

			return false;

		} else {

			if (this.fieldOfView[enemy.pos] && getCloser( enemy.pos )) {

				return true;

			} else {
				ready();
				return false;
			}

		}
	}

	public void rest( boolean tillHealthy ) {
		spendAndNext( TIME_TO_REST );
		if (!tillHealthy) {
			getSprite().showStatus( CharSprite.DEFAULT, TXT_WAIT );
		}
		restoreHealth = tillHealthy;
	}

	@Override
	public int attackProc( Char enemy, int damage ) {
		KindOfWeapon wep = rangedWeapon != null ? rangedWeapon : belongings.getWeapon();
		if (wep != null) {

			wep.proc( this, enemy, damage );

			switch (subClass) {
			case GLADIATOR:
				if (wep instanceof MeleeWeapon) {
					damage += Buff.affect( this, Combo.class ).hit( enemy, damage );
				}
				break;
			case BATTLEMAGE:
				if (wep instanceof Wand) {
					Wand wand = (Wand)wep;
					if (wand.getCurCharges() >= wand.maxCharges) {

						wand.use(this);

					} else if (damage > 0) {

						wand.setCurCharges(wand.getCurCharges() + 1);

						ScrollOfRecharging.charge( this );
					}
					damage += wand.getCurCharges();
				}
			case SNIPER:
				if (rangedWeapon != null) {
					Buff.prolong( this, SnipersMark.class, attackDelay() * 1.1f ).object = enemy.id();
				}
				break;
			default:
			}
		}

		return damage;
	}

	@Override
	public int defenseProc( Char enemy, int damage ) {

		RingOfThorns.Thorns thorns = buff( RingOfThorns.Thorns.class );
		if (thorns != null) {
			int dmg = Random.IntRange( 0, damage );
			if (dmg > 0) {
				enemy.damage( dmg, thorns );
			}
		}

		Earthroot.Armor armor = buff( Earthroot.Armor.class );
		if (armor != null) {
			damage = armor.absorb( damage );
		}

		if (belongings.getArmor() != null) {
			damage = belongings.getArmor().proc( enemy, this, damage );
		}

		return damage;
	}

	@Override
	public void damage( int dmg, Object src ) {
		restoreHealth = false;
		super.damage( dmg, src );

		if (subClass == HeroSubClass.BERSERKER && 0 < getHP() && getHP() <= getHT() * Fury.LEVEL) {
			Buff.affect( this, Fury.class );
		}
	}

	private void checkVisibleEnemies() {
		ArrayList<Char> visible = new ArrayList<>();

		boolean newEnemy = false;
		for (Actor actor: Actor.all()) {
			if (actor instanceof Char) {
				Char possibleEnemy = (Char) actor;
				if ( fieldOfView[ possibleEnemy.pos ] && Alignment.isHostile(possibleEnemy.alignment, alignment)){
					visible.add(possibleEnemy);
					if (!visibleEnemies.contains( possibleEnemy )) {
						newEnemy = true;
					}
				};
			}
		}
		if (newEnemy) {
			interrupt();
			restoreHealth = false;
		}

		visibleEnemies = visible;
	}

	public int visibleEnemies() {
		return visibleEnemies.size();
	}

	public Char visibleEnemy(int index ) {
		return visibleEnemies.get( index % visibleEnemies.size() );
	}

	private boolean getCloser( final int target ) {

		if (rooted) {
			Camera.shake( 1, 1f );
			return false;
		}

		int step = -1;

		if (Level.adjacent( pos, target )) {

			if (Actor.findChar( target ) == null) {
				if (Level.pit[target] && !flying && !Chasm.jumpConfirmed) {
					Chasm.heroJump( this );
					interrupt();
					return false;
				}
				if (Level.passable[target] || Level.avoid[target]) {
					step = target;
				}
			}

		} else {

			int len = Level.LENGTH;
			boolean[] p = Level.passable;
			boolean[] v = Dungeon.level.visited;
			boolean[] m = Dungeon.level.mapped;
			boolean[] passable = new boolean[len];
			for (int i=0; i < len; i++) {
				passable[i] = p[i] && (v[i] || m[i]);
			}

			step = Dungeon.findPath( this, pos, target, passable, this.fieldOfView );
		}

		if (step != -1) {

			int oldPos = pos;
			move( step );
			getSprite().move( oldPos, pos );
			spend( 1 / speed() );

			return true;

		} else {

			return false;

		}

	}

	public boolean handle( Integer cell ) {

		if (cell == null) {
			return false;
		}
		if (cell < 0 ){
			return false;
		}
		if (cell >= Dungeon.level.LENGTH){
			return false;
		}

		Char ch;
		Heap heap;

		if (Dungeon.level.map[cell] == Terrain.ALCHEMY && cell != pos) {

			curAction = new HeroAction.Cook( cell );

		} else if (this.fieldOfView[cell] && (ch = Actor.findChar( cell )) != null && ch != this) {

			if (ch instanceof NPC) {
				curAction = new HeroAction.Interact( (NPC)ch );
			} else if (Alignment.isHostile(alignment, ch.alignment)){

				curAction = new HeroAction.Attack( ch );
			}

		} else if (this.fieldOfView[cell] && (heap = Dungeon.level.heaps.get( cell )) != null && heap.type != Type.HIDDEN) {

			switch (heap.type) {
			case HEAP:
				curAction = new HeroAction.PickUp( cell );
				break;
			case FOR_SALE:
				curAction = heap.size() == 1 && heap.peek().price() > 0 ?
					new HeroAction.Buy( cell ) :
					new HeroAction.PickUp( cell );
				break;
			default:
				curAction = new HeroAction.OpenChest( cell );
			}

		} else if (Dungeon.level.map[cell] == Terrain.LOCKED_DOOR || Dungeon.level.map[cell] == Terrain.LOCKED_EXIT) {

			curAction = new HeroAction.Unlock( cell );

		} else if (cell == Dungeon.level.exit) {

			curAction = new HeroAction.Descend( cell );

		} else if (cell == Dungeon.level.entrance) {

			curAction = new HeroAction.Ascend( cell );

		} else  {

			curAction = new HeroAction.Move( cell );
			lastAction = null;

		}

		return act();
	}

	@Override
	public void updateSpriteState() {
		super.updateSpriteState();
	}

	public void earnExp(int exp ) {

		this.exp += exp;

		boolean levelUp = false;
		while (this.exp >= maxExp()) {
			this.exp -= maxExp();
			lvl++;

			setHT(getHT() + 5);
			setHP(getHP() + 5);
			attackSkill++;
			defenseSkill++;

			if (lvl < 10) {
				updateAwareness();
			}

			levelUp = true;
		}

		SendHeroLevel(networkID, lvl, exp);

		if (levelUp) {

			GLog.p( TXT_NEW_LEVEL, lvl );
			getSprite().showStatus( CharSprite.POSITIVE, TXT_LEVEL_UP );
			Sample.INSTANCE.play( Assets.SND_LEVELUP );

			Badges.validateLevelReached(this);
		}

		if (subClass == HeroSubClass.WARLOCK) {

			int value = Math.min( getHT() - getHP(), 1 + (Dungeon.depth - 1) / 5 );
			if (value > 0) {
				setHP(getHP() + value);
				getSprite().emitter().burst( Speck.factory( Speck.HEALING ), 1 );
			}

			(buff( Hunger.class )).satisfy( 10 );
		}
	}

	public int maxExp() {
		return 5 + lvl * 5;
	}

	void updateAwareness() {
		awareness = (float)(1 - Math.pow(
			(heroClass == HeroClass.ROGUE ? 0.85 : 0.90),
			(1 + Math.min( lvl,  9 )) * 0.5
		));
	}

	public boolean isStarving() {
		return (buff( Hunger.class )).isStarving();
	}

	@Override
	public void add( Buff buff ) {
		super.add( buff );

		if (getSprite() != null) {
			if (buff instanceof Burning) {
				GLog.w( "You catch fire!" );
				interrupt();
			} else if (buff instanceof Paralysis) {
				GLog.w( "You are paralysed!" );
				interrupt();
			} else if (buff instanceof Poison) {
				GLog.w( "You are poisoned!" );
				interrupt();
			} else if (buff instanceof Ooze) {
				GLog.w( "Caustic ooze eats your flesh. Wash away it!" );
			} else if (buff instanceof Roots) {
				GLog.w( "You can't move!" );
			} else if (buff instanceof Weakness) {
				GLog.w( "You feel weakened!" );
			} else if (buff instanceof Blindness) {
				GLog.w( "You are blinded!" );
			} else if (buff instanceof Fury) {
				GLog.w( "You become furious!" );
				getSprite().showStatus( CharSprite.POSITIVE, "furious" );
			} else if (buff instanceof Charm) {
				GLog.w( "You are charmed!" );
			}  else if (buff instanceof Cripple) {
				GLog.w( "You are crippled!" );
			} else if (buff instanceof Bleeding) {
				GLog.w( "You are bleeding!" );
			} else if (buff instanceof Vertigo) {
				GLog.w( "Everything is spinning around you!" );
				interrupt();
			}

			else if (buff instanceof Light) {
				getSprite().add( CharSprite.State.ILLUMINATED );
			}
		}

	}

	@Override
	public void remove( Buff buff ) {
		super.remove( buff );

		if (buff instanceof Light) {
			getSprite().remove( CharSprite.State.ILLUMINATED );
		}
	}

	@Override
	public int stealth() {
		int stealth = super.stealth();
		for (Buff buff : buffs( RingOfShadows.Shadows.class )) {
			stealth += ((RingOfShadows.Shadows)buff).level;
		}
		return stealth;
	}

	@Override
	public void die( Object cause  ) {

		curAction = null;

		if (cause == GodPunishment.INSTANCE) {
			Actor.fixTime();
			super.die( cause );
			//reallyDie(cause);
			return;
		}
		DewVial.autoDrink( this );
		if (isAlive()) {
			new Flare( 8, 32 ).color( 0xFFFF66, true ).show(pos, 2f ) ;
			return;
		}

		Actor.fixTime();
		super.die( cause );

		Ankh ankh = belongings.getItem( Ankh.class );
		if (ankh == null) {

			this.reallyDie( cause );

		} else {

			//Dungeon.deleteGame( this.heroClass, false );
			GameScene.show( new WndResurrect( ankh, this, cause ) );

		}
	}

	public void reallyDie( Object cause ) {

		int length = Level.LENGTH;
		int[] map = Dungeon.level.map;
		boolean[] visited = Dungeon.level.visited;
		boolean[] discoverable = Level.discoverable;

		for (int i=0; i < length; i++) {

			int terr = map[i];

			if (discoverable[i]) {

				visited[i] = true;
				if ((Terrain.flags[terr] & Terrain.SECRET) != 0) {
					Level.set( i, Terrain.discover( terr ) );
					GameScene.updateMap( i );
				}
			}
		}

		Bones.leave(this);

		Dungeon.observeAll();

		this.belongings.identify();

		int pos = this.pos;

		ArrayList<Integer> passable = new ArrayList<Integer>();
		for (Integer ofs : Level.NEIGHBOURS8) {
			int cell = pos + ofs;
			if ((Level.passable[cell] || Level.avoid[cell]) && Dungeon.level.heaps.get( cell ) == null) {
				passable.add( cell );
			}
		}
		Collections.shuffle( passable );

		ArrayList<Item> items = new ArrayList<Item>( this.belongings.backpack.items );
		//These must drop;
		ArrayList<Item> mustDropItems = new ArrayList<Item>();
		for(int i = 0; i < items.size(); i++) {
			Item item = Random.element(mustDropItems);
			//Every important item should drop
			//Keys are important but also every item that would prevent others from completing the game
			boolean isImportant = false;
			if (item instanceof Key) {
				isImportant = true;
			}
			//Limited drops are important
			if (item instanceof PotionOfStrength || item instanceof ScrollOfUpgrade){
				isImportant = true;
			}
			//Rare consumables are too
			if (item instanceof PotionOfMight || item instanceof ScrollOfEnchantment){
				isImportant = true;
			}
			//Boss drops are important
			if (item instanceof LloydsBeacon || item instanceof TomeOfMastery || item instanceof RingOfThorns || item instanceof ArmorKit || item instanceof Amulet) {
				isImportant = true;
			}
			//Potion of healing is debatable
			if (item instanceof PotionOfHealing) {
				isImportant = true;
			}
			if (isImportant) {
				mustDropItems.add(item);
			}
		}
		for(Item importantItem : mustDropItems){
			 Dungeon.level.drop(importantItem , Random.element(passable)).sendDropVisualAction(pos);
			 //We do not want duplicates
			 items.remove(importantItem);
		}
		for (Integer cell : passable) {
			if (items.isEmpty()) {
				break;
			}

			Item item = Random.element( items );
			Dungeon.level.drop( item, cell ).sendDropVisualAction(pos);
			items.remove( item );
		}

		GameScene.gameOver(this);

		if (cause instanceof Doom) {
			((Doom)cause).onDeath();
		}
		freeCell(pos);
		//Dungeon.deleteGame( this.heroClass, true );
	}

	@Override
	public void move( int step ) {
		super.move( step );

		if (!flying) {

			if (Level.water[pos]) {
				Sample.INSTANCE.play( Assets.SND_WATER, 1, 1, Random.Float( 0.8f, 1.25f ) );
			} else {
				Sample.INSTANCE.play( Assets.SND_STEP );
			}
			Dungeon.level.press( pos, this );
		}
	}

	@Override
	public void onMotionComplete() {
		Dungeon.observeAll();
		search( false );

		super.onMotionComplete();
	}

	@Override
	public void onAttackComplete() {

		attackIndicator.target( enemy );

		attack( enemy );
		curAction = null;

		Invisibility.dispel(this);

		super.onAttackComplete();
	}

	@Override
	public void onOperateComplete() {

		if (curAction instanceof HeroAction.Unlock) {

			if (theKey != null) {
				theKey.detach( belongings.backpack );
				theKey = null;
			}

			int doorCell = ((HeroAction.Unlock)curAction).dst;
			int door = Dungeon.level.map[doorCell];

			Level.set( doorCell, door == Terrain.LOCKED_DOOR ? Terrain.DOOR : Terrain.UNLOCKED_EXIT );
			GameScene.updateMap( doorCell );

		} else if (curAction instanceof HeroAction.OpenChest) {

			if (theKey != null) {
				theKey.detach( belongings.backpack );
				theKey = null;
			}

			Heap heap = Dungeon.level.heaps.get( ((HeroAction.OpenChest)curAction).dst );
			if (heap.type == Type.SKELETON) {
				Sample.INSTANCE.play( Assets.SND_BONES );
			}
			heap.open( this );
		}
		curAction = null;

		super.onOperateComplete();
	}

	public boolean search( boolean intentional ) {

		boolean smthFound = false;

		int positive = 0;
		int negative = 0;
		for (Buff buff : buffs( RingOfDetection.Detection.class )) {
			int bonus = ((RingOfDetection.Detection)buff).level;
			if (bonus > positive) {
				positive = bonus;
			} else if (bonus < 0) {
				negative += bonus;
			}
		}
		int distance = 1 + positive + negative;

		float level = intentional ? (2 * awareness - awareness * awareness) : awareness;
		if (distance <= 0) {
			level /= 2 - distance;
			distance = 1;
		}

		int cx = pos % Level.WIDTH;
		int cy = pos / Level.WIDTH;
		int ax = cx - distance;
		if (ax < 0) {
			ax = 0;
		}
		int bx = cx + distance;
		if (bx >= Level.WIDTH) {
			bx = Level.WIDTH - 1;
		}
		int ay = cy - distance;
		if (ay < 0) {
			ay = 0;
		}
		int by = cy + distance;
		if (by >= Level.HEIGHT) {
			by = Level.HEIGHT - 1;
		}

		for (int y = ay; y <= by; y++) {
			for (int x = ax, p = ax + y * Level.WIDTH; x <= bx; x++, p++) {

				if (this.fieldOfView[p]) {

					if (intentional) {
						CheckedCell.SendCheckedCell(p, this);
					}

					if (Level.secret[p] && (intentional || Random.Float() < level)) {

						int oldValue = Dungeon.level.map[p];

						GameScene.discoverTile( p, oldValue );

						Level.set( p, Terrain.discover( oldValue ) );

						GameScene.updateMap( p );

						ScrollOfMagicMapping.discover( p );

						smthFound = true;
					}

					if (intentional) {
						Heap heap = Dungeon.level.heaps.get( p );
						if (heap != null && heap.type == Type.HIDDEN) {
							heap.open( this );
							smthFound = true;
						}
					}
				}
			}
		}


		if (intentional) {
			getSprite().showStatus( CharSprite.DEFAULT, TXT_SEARCH );
			getSprite().operate( pos );
			if (smthFound) {
				spendAndNext( Random.Float() < level ? TIME_TO_SEARCH : TIME_TO_SEARCH * 2 );
			} else {
				spendAndNext( TIME_TO_SEARCH );
			}

		}

		if (smthFound) {
			GLog.w( TXT_NOTICED_SMTH );
			Sample.INSTANCE.play( Assets.SND_SECRET );
			interrupt();
		}

		return smthFound;
	}

	public void resurrect( int resetLevel ) {

		setHP(getHT());
		setGold(0);
		exp = 0;

		belongings.resurrect( resetLevel );

		live();
	}

	@Override
	public HashSet<Class<?>> resistances() {
		RingOfElements.Resistance r = buff( RingOfElements.Resistance.class );
		return r == null ? super.resistances() : r.resistances();
	}

	@Override
	public HashSet<Class<?>> immunities() {
		GasesImmunity buff = buff( GasesImmunity.class );
		return buff == null ? super.immunities() : GasesImmunity.IMMUNITIES;
	}

	@Override
	public void next() {
		super.next();
	}


	public CellSelector cellSelector;
	public CellSelector.Listener defaultCellListener;

	public int getSTR() {
		return STR;
	}

	public void setSTR(int STR) {
		this.STR = STR;
		SendHeroStrength(networkID,this.STR);
	}

	public boolean hasWindow() {
		return Window.hasWindow(this);
	}

	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
		sendHeroGold(networkID, gold);
	}

	public static interface Doom {
		public void onDeath();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Hero hero = (Hero) o;

		if (STR != hero.STR) return false;
		if (lvl != hero.lvl) return false;
		if (exp != hero.exp) return false;
		if (networkID != hero.networkID) return false;
		if (heroClass != hero.heroClass) return false;
        return subClass == hero.subClass;
    }

	@Override
	public int hashCode() {
		int result = heroClass != null ? heroClass.hashCode() : 0;
		result = 31 * result + (subClass != null ? subClass.hashCode() : 0);
		result = 31 * result + STR;
		result = 31 * result + lvl;
		result = 31 * result + exp;
		result = 31 * result + networkID;
		return result;
	}
}
