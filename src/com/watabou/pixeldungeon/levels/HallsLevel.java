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
import com.watabou.pixeldungeon.items.Torch;
import com.watabou.utils.Random;
import javafx.scene.Scene;

public class HallsLevel extends RegularLevel {

	{
		minRoomSize = 6;

		viewDistance = Math.max( 25 - Dungeon.depth, 1 );

		color1 = 0x801500;
		color2 = 0xa68521;
	}

	@Override
	public void create() {
		addItemToSpawn( new Torch() );
		super.create();
	}

	@Override
	public String tilesTex() {
		return Assets.TILES_HALLS;
	}

	@Override
	public String waterTex() {
		return Assets.WATER_HALLS;
	}

	protected boolean[] water() {
		return com.watabou.pixeldungeon.levels.Patch.generate( feeling == Feeling.WATER ? 0.55f : 0.40f, 6 );
	}

	protected boolean[] grass() {
		return Patch.generate( feeling == Feeling.GRASS ? 0.55f : 0.30f, 3 );
	}

	@Override
	protected void decorate() {

		for (int i=WIDTH + 1; i < LENGTH - WIDTH - 1; i++) {
			if (map[i] == com.watabou.pixeldungeon.levels.Terrain.EMPTY) {

				int count = 0;
				for (int j=0; j < NEIGHBOURS8.length; j++) {
					if ((com.watabou.pixeldungeon.levels.Terrain.flags[map[i + NEIGHBOURS8[j]]] & com.watabou.pixeldungeon.levels.Terrain.PASSABLE) > 0) {
						count++;
					}
				}

				if (Random.Int( 80 ) < count) {
					map[i] = com.watabou.pixeldungeon.levels.Terrain.EMPTY_DECO;
				}

			} else
			if (map[i] == com.watabou.pixeldungeon.levels.Terrain.WALL &&
				map[i-1] != com.watabou.pixeldungeon.levels.Terrain.WALL_DECO && map[i-WIDTH] != com.watabou.pixeldungeon.levels.Terrain.WALL_DECO &&
				Random.Int( 20 ) == 0) {

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
	}

	@Override
	public String tileName( int tile ) {
		switch (tile) {
		case com.watabou.pixeldungeon.levels.Terrain.WATER:
			return "Cold lava";
		case com.watabou.pixeldungeon.levels.Terrain.GRASS:
			return "Embermoss";
		case com.watabou.pixeldungeon.levels.Terrain.HIGH_GRASS:
			return "Emberfungi";
		case com.watabou.pixeldungeon.levels.Terrain.STATUE:
		case com.watabou.pixeldungeon.levels.Terrain.STATUE_SP:
			return "Pillar";
		default:
			return super.tileName( tile );
		}
	}

	@Override
	public String tileDesc(int tile) {
		switch (tile) {
		case com.watabou.pixeldungeon.levels.Terrain.WATER:
			return "It looks like lava, but it's cold and probably safe to touch.";
		case com.watabou.pixeldungeon.levels.Terrain.STATUE:
		case com.watabou.pixeldungeon.levels.Terrain.STATUE_SP:
			return "The pillar is made of real humanoid skulls. Awesome.";
		case Terrain.BOOKSHELF:
			return "Books in ancient languages smoulder in the bookshelf.";
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
			if (level.map[i] == 63) {
			}
		}
	}

	//TODO send this
	private static class Stream extends Group {

		private int pos;

		private float delay;

		public Stream( int pos ) {
			super();

			this.pos = pos;

			delay = Random.Float( 2 );
		}

		@Override
		public void update() {

		}

		@Override
		public void draw() {
			super.draw();
		}
	}

	public static class FireParticle {

		public FireParticle() {
			super();
		}

		public void reset( float x, float y ) {
		}

		public void update() {
		}
	}
}
