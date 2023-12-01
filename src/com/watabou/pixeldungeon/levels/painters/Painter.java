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
package com.watabou.pixeldungeon.levels.painters;

import java.util.Arrays;

import com.watabou.pixeldungeon.levels.Level;
import com.watabou.pixeldungeon.levels.Room;
import com.watabou.utils.Point;
import android.graphics.Rect;

public class Painter {

	public static void set( Level level, int cell, int value ) {
		level.map[cell] = value;
	}

	public static void set( Level level, int x, int y, int value ) {
		set( level, x + y * Level.WIDTH, value );
	}

	public static void set( Level level, Point p, int value ) {
		set( level, p.x, p.y, value );
	}

	public static void fill( Level level, int x, int y, int w, int h, int value ) {

		int width = Level.WIDTH;

		int pos = y * width + x;
		for (int i=y; i < y + h; i++, pos += width) {
			Arrays.fill( level.map, pos, pos + w, value );
		}
	}

	public static void fill(Level level, Rect rect, int value ) {
	}

	public static void fill( Level level, Rect rect, int m, int value ) {
	}

	public static void fill( Level level, Rect rect, int l, int t, int r, int b, int value ) {
	}

	public static Point drawInside( Level level, Room room, Point from, int n, int value ) {

		Point step = new Point();
			step.set( +1, 0 );

		Point p = new Point( from ).offset( step );
		for (int i=0; i < n; i++) {
			if (value != -1) {
				set( level, p, value );
			}
			p.offset( step );
		}

		return p;
	}
}
