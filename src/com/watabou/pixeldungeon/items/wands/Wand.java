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
package com.watabou.pixeldungeon.items.wands;

import com.nikita22007.multiplayer.noosa.audio.Sample;
import com.nikita22007.multiplayer.server.effects.MagicMissile;
import com.watabou.pixeldungeon.Assets;
import com.watabou.pixeldungeon.Badges;
import com.watabou.pixeldungeon.actors.Char;
import com.watabou.pixeldungeon.actors.buffs.Buff;
import com.watabou.pixeldungeon.actors.buffs.Invisibility;
import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.actors.hero.HeroClass;
import com.watabou.pixeldungeon.items.Item;
import com.watabou.pixeldungeon.items.ItemStatusHandler;
import com.watabou.pixeldungeon.items.KindOfWeapon;
import com.watabou.pixeldungeon.items.bags.Bag;
import com.watabou.pixeldungeon.items.rings.RingOfPower.Power;
import com.watabou.pixeldungeon.mechanics.Ballistica;
import com.watabou.pixeldungeon.network.Server;
import com.watabou.pixeldungeon.scenes.CellSelector;
import com.watabou.pixeldungeon.scenes.GameScene;
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.pixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import textualmold9830.plugins.events.HeroUseWandEvent;

import java.util.ArrayList;

public abstract class Wand extends KindOfWeapon {

	private static final int USAGES_TO_KNOW	= 40;

	public static final String AC_ZAP	= "ZAP";

	private static final String TXT_WOOD	= "This thin %s wand is warm to the touch. Who knows what it will do when used?";
	private static final String TXT_DAMAGE	= "When this wand is used as a melee weapon, its average damage is %d points per hit.";
	private static final String TXT_WEAPON	= "You can use this wand as a melee weapon.";

	private static final String TXT_FIZZLES		= "your wand fizzles; it must be out of charges for now";
	private static final String TXT_SELF_TARGET	= "You can't target yourself";

	private static final String TXT_IDENTIFY	= "You are now familiar enough with your %s.";

	private static final float TIME_TO_ZAP	= 1f;

	public int maxCharges = initialCharges();
	private int curCharges = maxCharges;

	protected Charger charger;

	private boolean curChargeKnown = false;

	private int usagesToKnow = USAGES_TO_KNOW;

	protected boolean hitChars = true;

	private static final Class<?>[] wands = {
		WandOfTeleportation.class,
		WandOfSlowness.class,
		WandOfFirebolt.class,
		WandOfPoison.class,
		WandOfRegrowth.class,
		WandOfBlink.class,
		WandOfLightning.class,
		WandOfAmok.class,
		WandOfReach.class,
		WandOfFlock.class,
		WandOfDisintegration.class,
		WandOfAvalanche.class
	};
	private static final String[] woods =
		{"holly", "yew", "ebony", "cherry", "teak", "rowan", "willow", "mahogany", "bamboo", "purpleheart", "oak", "birch"};
	private static final Integer[] images = {
		ItemSpriteSheet.WAND_HOLLY,
		ItemSpriteSheet.WAND_YEW,
		ItemSpriteSheet.WAND_EBONY,
		ItemSpriteSheet.WAND_CHERRY,
		ItemSpriteSheet.WAND_TEAK,
		ItemSpriteSheet.WAND_ROWAN,
		ItemSpriteSheet.WAND_WILLOW,
		ItemSpriteSheet.WAND_MAHOGANY,
		ItemSpriteSheet.WAND_BAMBOO,
		ItemSpriteSheet.WAND_PURPLEHEART,
		ItemSpriteSheet.WAND_OAK,
		ItemSpriteSheet.WAND_BIRCH};

	private static ItemStatusHandler<Wand> handler;

	private String wood;

	{
		defaultAction = AC_ZAP;
	}

	@SuppressWarnings("unchecked")
	public static void initWoods() {
		handler = new ItemStatusHandler<Wand>( (Class<? extends Wand>[])wands, woods, images );
	}

	public static void save( Bundle bundle ) {
		handler.save( bundle );
	}

	@SuppressWarnings("unchecked")
	public static void restore( Bundle bundle ) {
		handler = new ItemStatusHandler<Wand>( (Class<? extends Wand>[])wands, woods, images, bundle );
	}

	public Wand() {
		super();

		try {
			image(handler.image( this ));
			wood = handler.label( this );
		} catch (Exception e) {
			// Wand of Magic Missile
		}
	}

	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		if (getCurCharges() > 0 || !curChargeKnown) {
			actions.add( AC_ZAP );
		}
		if (hero.heroClass != HeroClass.MAGE) {
			actions.remove( AC_EQUIP );
			actions.remove( AC_UNEQUIP );
		}
		return actions;
	}

	@Override
	public boolean doUnequip( Hero hero, boolean collect, boolean single ) {
		onDetach();
		return super.doUnequip( hero, collect, single );
	}

	@Override
	public void activate( Hero hero ) {
		charge( hero );
	}

	@Override
	public void execute( Hero hero, String action ) {
		if (action.equals( AC_ZAP )) {

			curUser = hero;
			curItem = this;
			GameScene.selectCell(hero, zapper );
			SendSelfUpdate(hero);
		} else {

			super.execute( hero, action );

		}
	}

	protected abstract void onZap( int cell );

	@Override
	public boolean collect( Bag container ) {
		if (super.collect( container )) {
			if (container.owner != null) {
				charge( container.owner );
			}
			return true;
		} else {
			return false;
		}
	};

	public void charge( Char owner ) {
		if (charger == null) {
			(charger = new Charger()).attachTo( owner );
		}
	}

	@Override
	public void onDetach( ) {
		stopCharging();
	}

	public void stopCharging() {
		if (charger != null) {
			Charger oldCharger = charger;
			charger = null;
			oldCharger.detach();
		}
	}

	public int power() {
		int eLevel = effectiveLevel();
		if (charger != null) {
			Power power = charger.target.buff( Power.class );
			return power == null ? eLevel : Math.max( eLevel + power.level, 0 );
		} else {
			return eLevel;
		}
	}

	public boolean isKnown() {
		return handler.isKnown( this );
	}

	public void setKnown() {
		if (!isKnown()) {
			handler.know( this );
		}

		SendSelfUpdate();
		Badges.validateAllWandsIdentified();
	}

	@Override
	public Item identify() {

		setKnown();
		curChargeKnown = true;
		super.identify();

		SendSelfUpdate();

		return this;
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder( super.toString() );

		String status = status();
		if (status != null) {
			sb.append( " (" + status +  ")" );
		}

		if (isBroken()) {
			sb.insert( 0, "broken " );
		}

		return sb.toString();
	}

	@Override
	public String name() {
		return isKnown() ? name : wood + " wand";
	}

	@Override
	public String info(Hero hero) {
		StringBuilder info = new StringBuilder( isKnown() ? desc() : String.format( TXT_WOOD, wood ) );
		if (hero.heroClass == HeroClass.MAGE) {
			info.append( "\n\n" );
			if (levelKnown) {
				int min = min();
				info.append( String.format( TXT_DAMAGE, min + (max() - min) / 2 ) );
			} else {
				info.append(  String.format( TXT_WEAPON ) );
			}
		}
		return info.toString();
	}

	@Override
	public boolean isIdentified() {
		return super.isIdentified() && isKnown() && curChargeKnown;
	}

	@Override
	public String status() {
		if (levelKnown) {
			return (curChargeKnown ? getCurCharges() : "?") + "/" + maxCharges;
		} else {
			return null;
		}
	}

	@Override
	public Item upgrade() {

		super.upgrade();

		updateLevel();
		setCurCharges(Math.min( getCurCharges() + 1, maxCharges ));
		SendSelfUpdate();

		return this;
	}

	@Override
	public Item degrade() {
		super.degrade();

		updateLevel();
		SendSelfUpdate();
		return this;
	}

	@Override
	public int maxDurability( int lvl ) {
		return 6 * (lvl < 16 ? 16 - lvl : 1);
	}

	protected void updateLevel() {
		maxCharges = Math.min( initialCharges() + level(), 9 );
		setCurCharges(Math.min(getCurCharges(), maxCharges ));
	}

	protected int initialCharges() {
		return 2;
	}

	@Override
	public int min() {
		int tier = 1 + effectiveLevel() / 3;
		return tier;
	}

	@Override
	public int max() {
		int level = effectiveLevel();
		int tier = 1 + level / 3;
		return (tier * tier - tier + 10) / 2 + level;
	}

	protected void fx( int cell ) {
		MagicMissile.blueLight( curUser.pos, cell );
		Sample.INSTANCE.play( Assets.SND_ZAP );
	}

	protected void wandUsed(Hero curUser) {

		setCurCharges(getCurCharges() - 1);
		if (!isIdentified() && --usagesToKnow <= 0) {
			identify();
			GLog.w( TXT_IDENTIFY, name() );
		} else {
			SendSelfUpdate();
		}

		use(curUser);

		curUser.spendAndNext( TIME_TO_ZAP );
	}

	@Override
	public Item random() {
		if (Random.Float() < 0.5f) {
			upgrade();
			if (Random.Float() < 0.15f) {
				upgrade();
			}
		}

		return this;
	}

	public static boolean allKnown() {
		return handler.known().size() == wands.length;
	}

	@Override
	public int price() {
		return considerState( 50 );
	}

	private static final String UNFAMILIRIARITY		= "unfamiliarity";
	private static final String MAX_CHARGES			= "maxCharges";
	private static final String CUR_CHARGES			= "curCharges";
	private static final String CUR_CHARGE_KNOWN	= "curChargeKnown";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( UNFAMILIRIARITY, usagesToKnow );
		bundle.put( MAX_CHARGES, maxCharges );
		bundle.put( CUR_CHARGES, getCurCharges());
		bundle.put( CUR_CHARGE_KNOWN, curChargeKnown );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		if ((usagesToKnow = bundle.getInt( UNFAMILIRIARITY )) == 0) {
			usagesToKnow = USAGES_TO_KNOW;
		}
		maxCharges = bundle.getInt( MAX_CHARGES );
		setCurCharges(bundle.getInt( CUR_CHARGES ));
		curChargeKnown = bundle.getBoolean( CUR_CHARGE_KNOWN );
	}

	protected static CellSelector.Listener zapper = new  CellSelector.Listener() {

		@Override
		public void onSelect( Integer target ) {

			if (target != null) {

				if (target == curUser.pos) {
					GLog.i( TXT_SELF_TARGET );
					return;
				}

				Wand curWand = (Wand)Wand.curItem;

				curWand.setKnown();

				int cell = Ballistica.cast( curUser.pos, target, true, curWand.hitChars );
				curUser.getSprite().zap( cell );

				if (curWand.getCurCharges() > 0) {
					HeroUseWandEvent event = new HeroUseWandEvent(curUser, curWand, cell);
					Server.pluginManager.fireEvent(event);
					if (!event.isCancelled()) {
						curUser = event.hero;
						curWand = event.wand;
						cell = event.cell;
						curUser.busy();
						curWand.fx(cell);

						Invisibility.dispel(curUser);

						curWand.onZap(cell);
						curWand.wandUsed(curUser);
					}
				} else {

					curUser.spendAndNext( TIME_TO_ZAP );
					GLog.w( TXT_FIZZLES );
					curWand.levelKnown = true;

					curWand.SendSelfUpdate(curUser);
				}

			}
		}

		@Override
		public String prompt() {
			return "Choose direction to zap";
		}
	};

	public int getCurCharges() {
		return curCharges;
	}

	public void setCurCharges(int curCharges) {
		this.curCharges = curCharges;
		SendSelfUpdate();
	}

	protected class Charger extends Buff {

		private static final float TIME_TO_CHARGE = 40f;

		@Override
		public boolean attachTo( Char target ) {
			super.attachTo( target );
			delay();

			return true;
		}

		@Override
		public void detach() {
			if (target == null) {
				Wand.this.stopCharging();
			} else {
				super.detach();
			}
		}

		@Override
		public boolean act() {

			if (getCurCharges() < maxCharges) {
				setCurCharges(getCurCharges() + 1);
			}

			delay();

			return true;
		}

		protected void delay() {
			float time2charge = ((Hero)target).heroClass == HeroClass.MAGE ?
				TIME_TO_CHARGE / (float)Math.sqrt( 1 + effectiveLevel() ) :
				TIME_TO_CHARGE;
			spend( time2charge );
		}
	}
}
