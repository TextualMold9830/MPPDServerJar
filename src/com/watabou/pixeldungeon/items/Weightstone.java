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
package com.watabou.pixeldungeon.items;

import com.nikita22007.multiplayer.noosa.audio.Sample;
import com.watabou.pixeldungeon.Assets;
import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.items.weapon.Weapon;
import com.watabou.pixeldungeon.scenes.GameScene;
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.pixeldungeon.ui.Window;
import com.watabou.pixeldungeon.utils.GLog;
import com.watabou.pixeldungeon.windows.IconTitle;
import com.watabou.pixeldungeon.windows.WndBag;

import java.util.ArrayList;

public class Weightstone extends com.watabou.pixeldungeon.items.Item {
	//small fucking hack
	private Hero owner;

	private static final String TXT_SELECT_WEAPON = "Select a weapon to balance";
	private static final String TXT_FAST = "you balanced your %s to make it faster";
	private static final String TXT_ACCURATE = "you balanced your %s to make it more accurate";

	private static final float TIME_TO_APPLY = 2;

	private static final String AC_APPLY = "APPLY";

	{
		name = "weightstone";
		image(ItemSpriteSheet.WEIGHT);

		stackable = true;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_APPLY);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (action == AC_APPLY) {
			owner = hero;
			curUser = hero;
			GameScene.selectItem(hero, itemSelector, WndBag.Mode.WEAPON, TXT_SELECT_WEAPON);

		} else {

			super.execute(hero, action);

		}
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	private void apply(Weapon weapon, boolean forSpeed) {

		detach(curUser.belongings.backpack);

		weapon.fix();
		if (forSpeed) {
			weapon.imbue = Weapon.Imbue.SPEED;
			GLog.p(TXT_FAST, weapon.name());
		} else {
			weapon.imbue = Weapon.Imbue.ACCURACY;
			GLog.p(TXT_ACCURATE, weapon.name());
		}

		curUser.getSprite().operate(curUser.pos);
		Sample.INSTANCE.play(Assets.SND_MISS);

		curUser.spend(TIME_TO_APPLY);
		curUser.busy();
	}

	@Override
	public int price() {
		return 40 * getQuantity();
	}

	@Override
	public String info() {
		return
				"Using a weightstone, you can balance your melee weapon to increase its speed or accuracy.";
	}

	private final WndBag.Listener itemSelector = new WndBag.Listener() {
		@Override
		public void onSelect(Item item) {
			if (item != null) {
				GameScene.show(new WndBalance((Weapon) item, owner));
			}
		}
	};

	public class WndBalance extends Window {

		private static final String TXT_CHOICE = "How would you like to balance your %s?";

		private static final String TXT_SPEED = "For speed";
		private static final String TXT_ACCURACY = "For accuracy";
		private static final String TXT_CANCEL = "Never mind";

		private static final int WIDTH = 120;
		private static final int MARGIN = 2;
		private static final int BUTTON_WIDTH = WIDTH - MARGIN * 2;
		private static final int BUTTON_HEIGHT = 20;

		public WndBalance(final Weapon weapon, Hero hero) {
			super(hero);

			IconTitle titlebar = new IconTitle(weapon);

			if (weapon.imbue != Weapon.Imbue.SPEED) {

				if (weapon.imbue != Weapon.Imbue.ACCURACY) {
				}


			}
		}
		protected void onSelect ( int index ){
			}
		}

	}
