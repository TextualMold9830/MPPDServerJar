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
package com.watabou.pixeldungeon.levels;

import com.watabou.pixeldungeon.Assets;
import com.watabou.pixeldungeon.Bones;
import com.watabou.pixeldungeon.actors.Actor;
import com.watabou.pixeldungeon.actors.mobs.npcs.Imp;
import com.watabou.pixeldungeon.items.Heap;
import com.watabou.pixeldungeon.items.Item;
import com.watabou.pixeldungeon.levels.Room.Type;
import com.watabou.utils.Graph;
import com.watabou.utils.Random;
import javafx.scene.Scene;

import java.util.List;

public class LastShopLevel extends RegularLevel {

	{
		color1 = 0x4b6636;
		color2 = 0xf2f2f2;
	}

	@Override
	public String tilesTex() {
		return Assets.TILES_CITY;
	}

	@Override
	public String waterTex() {
		return Assets.WATER_CITY;
	}

	@Override
	protected boolean build() {

		initRooms();

		int distance;
		int retry = 0;
		int minDistance = (int)Math.sqrt( rooms.size() );
		do {
			int innerRetry = 0;
			do {
				if (innerRetry++ > 10) {
					return false;
				}
				roomEntrance = Random.element( rooms );
			} while (roomEntrance.width() < 4 || roomEntrance.height() < 4);

			innerRetry = 0;
			do {
				if (innerRetry++ > 10) {
					return false;
				}
				roomExit = Random.element( rooms );
			} while (roomExit == roomEntrance || roomExit.width() < 6 || roomExit.height() < 6 || roomExit.top == 0);

			Graph.buildDistanceMap( rooms, roomExit );
			distance = Graph.buildPath( rooms, roomEntrance, roomExit ).size();

			if (retry++ > 10) {
				return false;
			}

		} while (distance < minDistance);

		roomEntrance.type = Type.ENTRANCE;
		roomExit.type = Type.EXIT;

		Graph.buildDistanceMap( rooms, roomExit );
		List<com.watabou.pixeldungeon.levels.Room> path = Graph.buildPath( rooms, roomEntrance, roomExit );

		Graph.setPrice( path, roomEntrance.distance );

		Graph.buildDistanceMap( rooms, roomExit );
		path = Graph.buildPath( rooms, roomEntrance, roomExit );

		com.watabou.pixeldungeon.levels.Room room = roomEntrance;
		for (com.watabou.pixeldungeon.levels.Room next : path) {
			room.connect( next );
			room = next;
		}

		com.watabou.pixeldungeon.levels.Room roomShop = null;
		int shopSquare = 0;
		for (com.watabou.pixeldungeon.levels.Room r : rooms) {
			if (r.type == Type.NULL && r.connected.size() > 0) {
				r.type = Type.PASSAGE;
				if (r.square() > shopSquare) {
					roomShop = r;
					shopSquare = r.square();
				}
			}
		}

		if (roomShop == null || shopSquare < 30) {
			return false;
		} else {
			roomShop.type = Imp.Quest.isCompleted() ? Type.SHOP : Type.STANDARD;
		}

		paint();

		paintWater();
		paintGrass();

		return true;
	}

	@Override
	protected void decorate() {

		for (int i=0; i < LENGTH; i++) {
			if (map[i] == com.watabou.pixeldungeon.levels.Terrain.EMPTY && Random.Int( 10 ) == 0) {

				map[i] = com.watabou.pixeldungeon.levels.Terrain.EMPTY_DECO;

			} else if (map[i] == com.watabou.pixeldungeon.levels.Terrain.WALL && Random.Int( 8 ) == 0) {

				map[i] = com.watabou.pixeldungeon.levels.Terrain.WALL_DECO;

			} else if (map[i] == com.watabou.pixeldungeon.levels.Terrain.SECRET_DOOR) {

				map[i] = com.watabou.pixeldungeon.levels.Terrain.DOOR;

			}
		}

		if (Imp.Quest.isCompleted()) {
			while (true) {
				int pos = roomEntrance.random();
				if (pos != entrance) {
					map[pos] = com.watabou.pixeldungeon.levels.Terrain.SIGN;
					break;
				}
			}
		}
	}

	@Override
	protected void createMobs() {
	}

	public Actor respawner() {
		return null;
	}

	@Override
	protected void createItems() {
		Item item = Bones.get();
		if (item != null) {
			int pos;
			do {
				pos = roomEntrance.random();
			} while (pos == entrance || map[pos] == com.watabou.pixeldungeon.levels.Terrain.SIGN);
			drop( item, pos ).type = Heap.Type.SKELETON;
		}
	}

	@Override
	public int randomRespawnCell() {
		return -1;
	}

	@Override
	public String tileName( int tile ) {
		switch (tile) {
		case com.watabou.pixeldungeon.levels.Terrain.WATER:
			return "Suspiciously colored water";
		case com.watabou.pixeldungeon.levels.Terrain.HIGH_GRASS:
			return "High blooming flowers";
		default:
			return super.tileName( tile );
		}
	}

	@Override
	public String tileDesc(int tile) {
		switch (tile) {
		case com.watabou.pixeldungeon.levels.Terrain.ENTRANCE:
			return "A ramp leads up to the upper depth.";
		case com.watabou.pixeldungeon.levels.Terrain.EXIT:
			return "A ramp leads down to the Inferno.";
		case com.watabou.pixeldungeon.levels.Terrain.WALL_DECO:
		case com.watabou.pixeldungeon.levels.Terrain.EMPTY_DECO:
			return "Several tiles are missing here.";
		case Terrain.EMPTY_SP:
			return "Thick carpet covers the floor.";
		default:
			return super.tileDesc( tile );
		}
	}

	@Override
	protected boolean[] water() {
		return com.watabou.pixeldungeon.levels.Patch.generate( 0.35f, 4 );
	}

	@Override
	protected boolean[] grass() {
		return Patch.generate( 0.30f, 3 );
	}

	@Override
	public void addVisuals( Scene scene ) {
		CityLevel.addVisuals( this, scene );
	}
}
