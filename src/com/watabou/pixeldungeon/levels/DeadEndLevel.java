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

import java.util.Arrays;

import com.watabou.pixeldungeon.Assets;
import com.watabou.utils.Random;

public class DeadEndLevel extends Level {

	protected final int SIZE;

	{
		color1 = 0x534f3e;
		color2 = 0xb9d661;
	}
	protected final int center;

	public DeadEndLevel() {
		this(5);
	}

	public DeadEndLevel(int size) {
		SIZE = size;
		center = (SIZE / 2 + 1) * (WIDTH + 1);
	}

	@Override
	public String tilesTex() {
		return Assets.TILES_CAVES;
	}

	@Override
	public String waterTex() {
		return Assets.WATER_HALLS;
	}

	@Override
	protected boolean build() {

		Arrays.fill( map, com.watabou.pixeldungeon.levels.Terrain.WALL );

		for (int i=2; i < SIZE; i++) {
			for (int j=2; j < SIZE; j++) {
				map[i * WIDTH + j] = com.watabou.pixeldungeon.levels.Terrain.EMPTY;
			}
		}

		for (int i=1; i <= SIZE; i++) {
			map[WIDTH + i] =
			map[WIDTH * SIZE + i] =
			map[WIDTH * i + 1] =
			map[WIDTH * i + SIZE] =
				com.watabou.pixeldungeon.levels.Terrain.WATER;
		}

		entrance = SIZE * WIDTH + SIZE / 2 + 1;
		map[entrance] = com.watabou.pixeldungeon.levels.Terrain.ENTRANCE;

		exit = -1;


		map[(SIZE / 2 + 1) * (WIDTH + 1)] = com.watabou.pixeldungeon.levels.Terrain.SIGN;

		return true;
	}

	@Override
	protected void decorate() {
		for (int i=0; i < LENGTH; i++) {
			if (map[i] == com.watabou.pixeldungeon.levels.Terrain.EMPTY && Random.Int( 10 ) == 0) {
				map[i] = com.watabou.pixeldungeon.levels.Terrain.EMPTY_DECO;
			} else if (map[i] == com.watabou.pixeldungeon.levels.Terrain.WALL && Random.Int( 8 ) == 0) {
				map[i] = Terrain.WALL_DECO;
			}
		}
	}

	@Override
	protected void createMobs() {
	}

	@Override
	protected void createItems() {
	}

	@Override
	public int randomRespawnCell() {
		return -1;
	}

}
