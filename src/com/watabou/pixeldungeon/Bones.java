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
package com.watabou.pixeldungeon;

import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.items.Gold;
import com.watabou.pixeldungeon.items.Item;
import com.watabou.pixeldungeon.items.rings.Ring;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Bones {

	private static final String BONES_FILE	= "save/bones.dat";

	private static final String LEVEL	= "level";
	private static final String ITEM	= "item";

	private static int depth = -1;
	private static Item item;

	public static void leave(Hero hero) {

		item = null;
		switch (Random.Int( 4 )) {
		case 0:
			item = hero.belongings.getWeapon();
			break;
		case 1:
			item = hero.belongings.getArmor();
			break;
		case 2:
			item = hero.belongings.getRing1();
			break;
		case 3:
			item = hero.belongings.getRing2();
			break;
		}
		if (item == null) {
			if (hero.getGold() > 0) {
				item = new Gold( Random.IntRange( 1, hero.getGold()) );
			} else {
				item = new Gold( 1 );
			}
		}

		depth = Dungeon.depth;

		Bundle bundle = new Bundle();
		bundle.put( LEVEL, depth );
		bundle.put( ITEM, item );

		try {
			OutputStream output = Files.newOutputStream(Paths.get(BONES_FILE));
			Bundle.write( bundle, output );
			output.close();
		} catch (IOException e) {

		}
	}

	public static Item get() {
		if (depth == -1) {

			try {
				InputStream input = Files.newInputStream(Paths.get(BONES_FILE));
				Bundle bundle = Bundle.read( input );
				input.close();

				depth = bundle.getInt( LEVEL );
				item = (Item)bundle.get( ITEM );

				return get();

			} catch (IOException e) {
				return null;
			}

		} else {
			if (depth == Dungeon.depth) {
				try {
					Files.deleteIfExists(Paths.get(BONES_FILE));
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				depth = 0;

				if (!item.stackable) {
					item.cursed = true;
					item.cursedKnown = true;
					if (item.isUpgradable()) {
						int lvl = (Dungeon.depth - 1) * 3 / 5 + 1;
						if (lvl < item.level()) {
							item.degrade( item.level() - lvl );
						}
						item.levelKnown = false;
					}
				}

				if (item instanceof Ring) {
					((Ring)item).syncGem();
				}

				return item;
			} else {
				return null;
			}
		}
	}
}
