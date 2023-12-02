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
package com.watabou.pixeldungeon.ui;

import com.watabou.pixeldungeon.items.Item;
import com.watabou.pixeldungeon.items.armor.Armor;
import com.watabou.pixeldungeon.items.weapon.Weapon;
import com.watabou.pixeldungeon.items.weapon.melee.MeleeWeapon;
import com.watabou.pixeldungeon.sprites.ItemSprite;
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet;

import java.awt.*;

public class ItemSlot extends Button { //UI  only -> Client  Only

	public static final int DEGRADED	= 0xFF4444;
	public static final int UPGRADED	= 0x44FF44;
	public static final int WARNING		= 0xFF8800;

	private static final float ENABLED	= 1.0f;
	private static final float DISABLED	= 0.3f;

	protected ItemSprite icon;

	private static final String TXT_STRENGTH	= ":%d";
	private static final String TXT_TYPICAL_STR	= "%d?";

	private static final String TXT_LEVEL	= "%+d";
	private static final String TXT_CURSED	= "";//"-";

	// Special "virtual items"
	public static final Item CHEST = new Item() {
		public int image() { return ItemSpriteSheet.CHEST; };
	};
	public static final Item LOCKED_CHEST = new Item() {
		public int image() { return ItemSpriteSheet.LOCKED_CHEST; };
	};
	public static final Item TOMB = new Item() {
		public int image() { return ItemSpriteSheet.TOMB; };
	};
	public static final Item SKELETON = new Item() {
		public int image() { return ItemSpriteSheet.BONES; };
	};

	public ItemSlot() {
		super();
	}

	public ItemSlot( Item item ) {
		this();
		item( item );
	}



	@Override
	public void layout() {
		super.layout();
	}

	public void item( Item item ) {
		if (item == null) {


		} else {


			boolean isArmor = item instanceof Armor;
			boolean isWeapon = item instanceof Weapon;
			if (isArmor || isWeapon) {

				if (item.levelKnown || (isWeapon && !(item instanceof MeleeWeapon))) {

					int str = isArmor ? ((Armor) item).STR : ((Weapon) item).STR;

				}

			}

			int level = item.visiblyUpgraded();
			if (level != 0 || (item.cursed && item.cursedKnown)) {
				layout();
			}
		}
	}
	public void enable(boolean value ) {
		float alpha = value ? ENABLED : DISABLED;
	}

}
