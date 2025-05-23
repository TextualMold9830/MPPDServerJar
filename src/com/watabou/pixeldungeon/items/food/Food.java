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
package com.watabou.pixeldungeon.items.food;

import com.nikita22007.multiplayer.noosa.audio.Sample;
import com.watabou.pixeldungeon.Assets;
import com.watabou.pixeldungeon.Badges;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.Statistics;
import com.watabou.pixeldungeon.actors.buffs.Hunger;
import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.effects.Speck;
import com.watabou.pixeldungeon.effects.SpellSprite;
import com.watabou.pixeldungeon.items.Item;
import com.watabou.pixeldungeon.items.scrolls.ScrollOfRecharging;
import com.watabou.pixeldungeon.network.Server;
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.pixeldungeon.utils.GLog;
import textualmold9830.Preferences;
import textualmold9830.plugins.events.HeroEatFoodEvent;

import java.util.ArrayList;
import java.util.Arrays;

public class Food extends Item {

	private static final float TIME_TO_EAT	= 3f;

	public static final String AC_EAT	= "EAT";

	public float energy = Hunger.HUNGRY;
	public String message = "That food tasted delicious!";

	{
		stackable = true;
		name = "ration of food";
		image(ItemSpriteSheet.RATION);
	}

	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( AC_EAT );
		return actions;
	}

	@Override
	public void execute( Hero hero, String action ) {
		if (action.equals( AC_EAT )) {
			HeroEatFoodEvent event = new HeroEatFoodEvent(hero, this, energy);
			Server.pluginManager.fireEvent(event);
			detach( hero.belongings.backpack );
			if (!event.isCancelled()) {
				energy = event.energy;
				if (!Preferences.sharedHunger) {
					hero.buff(Hunger.class).satisfy(energy);
				} else {
					for (Hero heroToSatisfy : Dungeon.heroes) {
						if (heroToSatisfy != null) {
							applyFood(heroToSatisfy);
						}
					}
				}
				GLog.i(message);

				switch (hero.heroClass) {
					case WARRIOR:
						if (hero.getHP() < hero.getHT()) {
							hero.setHP(Math.min(hero.getHP() + 5, hero.getHT()));
							hero.getSprite().emitter().burst(Speck.factory(Speck.HEALING), 1);
						}
						break;
					case MAGE:
						hero.belongings.charge(false);
						ScrollOfRecharging.charge(hero);
						break;
					case ROGUE:
					case HUNTRESS:
						break;
				}

				hero.getSprite().operate(hero.pos);
				hero.busy();
				SpellSprite.show(hero, SpellSprite.FOOD);
				Sample.INSTANCE.play(Assets.SND_EAT);

				hero.spend(TIME_TO_EAT);

				Statistics.foodEaten++;
				Badges.validateFoodEaten();

				SendSelfUpdate(hero);
			}
		} else {

			super.execute( hero, action );

		}
	}

	@Override
	public String info() {
		return
			"Nothing fancy here: dried meat, " +
			"some biscuits - things like that.";
	}

	@Override
	public boolean isUpgradable()  {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public int price() {
		return 10 * getQuantity();
	}
	private void applyFood(Hero hero){
		if (hero != null && hero.isAlive()){
			hero.buff(Hunger.class).satisfy(energy);
		};
	}
}
