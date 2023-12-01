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

import com.watabou.noosa.Group;
import com.watabou.pixeldungeon.Assets;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.actors.mobs.npcs.Blacksmith;
import com.watabou.pixeldungeon.levels.Room.Type;
import com.watabou.pixeldungeon.levels.painters.Painter;
import com.watabou.utils.Random;
import com.watabou.utils.Rect;
import javafx.scene.Scene;

public class CavesLevel extends RegularLevel {

	{
		color1 = 0x534f3e;
		color2 = 0xb9d661;

		viewDistance = 6;
	}

	@Override
	public String tilesTex() {
		return Assets.TILES_CAVES;
	}

	@Override
	public String waterTex() {
		return Assets.WATER_CAVES;
	}

	protected boolean[] water() {
		return com.watabou.pixeldungeon.levels.Patch.generate( feeling == Feeling.WATER ? 0.60f : 0.45f, 6 );
	}

	protected boolean[] grass() {
		return Patch.generate( feeling == Feeling.GRASS ? 0.55f : 0.35f, 3 );
	}

	@Override
	protected void assignRoomType() {
		super.assignRoomType();

		Blacksmith.Quest.spawn( rooms );
	}

	@Override
	protected void decorate() {

		for (com.watabou.pixeldungeon.levels.Room room : rooms) {
			if (room.type != Type.STANDARD) {
				continue;
			}

			if (room.width() <= 3 || room.height() <= 3) {
				continue;
			}

			int s = room.square();

			if (Random.Int( s ) > 8) {
				int corner = (room.left + 1) + (room.top + 1) * WIDTH;
				if (map[corner - 1] == com.watabou.pixeldungeon.levels.Terrain.WALL && map[corner - WIDTH] == com.watabou.pixeldungeon.levels.Terrain.WALL) {
					map[corner] = com.watabou.pixeldungeon.levels.Terrain.WALL;
				}
			}

			if (Random.Int( s ) > 8) {
				int corner = (room.right - 1) + (room.top + 1) * WIDTH;
				if (map[corner + 1] == com.watabou.pixeldungeon.levels.Terrain.WALL && map[corner - WIDTH] == com.watabou.pixeldungeon.levels.Terrain.WALL) {
					map[corner] = com.watabou.pixeldungeon.levels.Terrain.WALL;
				}
			}

			if (Random.Int( s ) > 8) {
				int corner = (room.left + 1) + (room.bottom - 1) * WIDTH;
				if (map[corner - 1] == com.watabou.pixeldungeon.levels.Terrain.WALL && map[corner + WIDTH] == com.watabou.pixeldungeon.levels.Terrain.WALL) {
					map[corner] = com.watabou.pixeldungeon.levels.Terrain.WALL;
				}
			}

			if (Random.Int( s ) > 8) {
				int corner = (room.right - 1) + (room.bottom - 1) * WIDTH;
				if (map[corner + 1] == com.watabou.pixeldungeon.levels.Terrain.WALL && map[corner + WIDTH] == com.watabou.pixeldungeon.levels.Terrain.WALL) {
					map[corner] = com.watabou.pixeldungeon.levels.Terrain.WALL;
				}
			}

			for (com.watabou.pixeldungeon.levels.Room n : room.connected.keySet()) {
				if ((n.type == Type.STANDARD || n.type == Type.TUNNEL) && Random.Int( 3 ) == 0) {
					Painter.set( this, room.connected.get( n ), com.watabou.pixeldungeon.levels.Terrain.EMPTY_DECO );
				}
			}
		}

		for (int i=WIDTH + 1; i < LENGTH - WIDTH; i++) {
			if (map[i] == com.watabou.pixeldungeon.levels.Terrain.EMPTY) {
				int n = 0;
				if (map[i+1] == com.watabou.pixeldungeon.levels.Terrain.WALL) {
					n++;
				}
				if (map[i-1] == com.watabou.pixeldungeon.levels.Terrain.WALL) {
					n++;
				}
				if (map[i+WIDTH] == com.watabou.pixeldungeon.levels.Terrain.WALL) {
					n++;
				}
				if (map[i-WIDTH] == com.watabou.pixeldungeon.levels.Terrain.WALL) {
					n++;
				}
				if (Random.Int( 6 ) <= n) {
					map[i] = com.watabou.pixeldungeon.levels.Terrain.EMPTY_DECO;
				}
			}
		}

		for (int i=0; i < LENGTH; i++) {
			if (map[i] == com.watabou.pixeldungeon.levels.Terrain.WALL && Random.Int( 12 ) == 0) {
				map[i] = com.watabou.pixeldungeon.levels.Terrain.WALL_DECO;
			}
		}

		while (true) {
			int pos = roomEntrance.random();
			if (pos != entrance) {
				map[pos] = com.watabou.pixeldungeon.levels.Terrain.SIGN;
				break;
			}
		}

		if (Dungeon.bossLevel( Dungeon.depth + 1 )) {
			return;
		}

		for (com.watabou.pixeldungeon.levels.Room r : rooms) {
			if (r.type == Type.STANDARD) {
				for (Room n : r.neigbours) {
					if (n.type == Type.STANDARD && !r.connected.containsKey( n )) {
						Rect w = r.intersect( n );
						if (w.left == w.right && w.bottom - w.top >= 5) {

							w.top += 2;
							w.bottom -= 1;

							w.right++;

							Painter.fill( this, w.left, w.top, 1, w.height(), com.watabou.pixeldungeon.levels.Terrain.CHASM );

						} else if (w.top == w.bottom && w.right - w.left >= 5) {

							w.left += 2;
							w.right -= 1;

							w.bottom++;

							Painter.fill( this, w.left, w.top, w.width(), 1, com.watabou.pixeldungeon.levels.Terrain.CHASM );
						}
					}
				}
			}
		}
	}

	@Override
	public String tileName( int tile ) {
		switch (tile) {
		case com.watabou.pixeldungeon.levels.Terrain.GRASS:
			return "Fluorescent moss";
		case com.watabou.pixeldungeon.levels.Terrain.HIGH_GRASS:
			return "Fluorescent mushrooms";
		case com.watabou.pixeldungeon.levels.Terrain.WATER:
			return "Freezing cold water.";
		default:
			return super.tileName( tile );
		}
	}

	@Override
	public String tileDesc( int tile ) {
		switch (tile) {
		case com.watabou.pixeldungeon.levels.Terrain.ENTRANCE:
			return "The ladder leads up to the upper depth.";
		case com.watabou.pixeldungeon.levels.Terrain.EXIT:
			return "The ladder leads down to the lower depth.";
		case com.watabou.pixeldungeon.levels.Terrain.HIGH_GRASS:
			return "Huge mushrooms block the view.";
		case com.watabou.pixeldungeon.levels.Terrain.WALL_DECO:
			return "A vein of some ore is visible on the wall. Gold?";
		case com.watabou.pixeldungeon.levels.Terrain.BOOKSHELF:
			return "Who would need a bookshelf in a cave?";
		default:
			return super.tileDesc( tile );
		}
	}

	@Override
	public void addVisuals( Scene scene ) {
		super.addVisuals( scene );
		addVisuals( this, scene );
	}

	public static void addVisuals(Level level, Scene scene ) {
		for (int i=0; i < LENGTH; i++) {
			if (level.map[i] == Terrain.WALL_DECO) {
			}
		}
	}

	//todo send this
	private static class Vein extends Group {

		private int pos;

		private float delay;

		public Vein( int pos ) {
			super();

			this.pos = pos;

			delay = Random.Float( 2 );
		}

		@Override
		public void update() {

		}
	}

	public static final class Sparkle {

		public void reset( float x, float y ) {
		}

		public void update() {
		}
	}
}
