/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015  Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2016 Evan Debenham
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

package com.watabou.noosa.audio;

import java.util.HashMap;
import java.util.LinkedList;


public enum Sample{

	INSTANCE;

	public static final int MAX_STREAMS = 8;


	protected HashMap<Object, Integer> ids =
			new HashMap<>();

	private boolean enabled = true;
	private float volume = 1f;

	private LinkedList<String> loadingQueue = new LinkedList<>();

	public void reset() {

		ids.clear();
		loadingQueue = new LinkedList<>();

	}





	public int play( Object id ) {
		return play( id, 1 );
	}

	public int play( Object id, float volume ) {
		return play( id, volume, volume, 1 );
	}

	public int play( Object id, float leftVolume, float rightVolume, float rate ) {
		return 0;
	}

	public void enable( boolean value ) {
		enabled = value;
	}

	public void volume( float value ) {
		this.volume = value;
	}

	public boolean isEnabled() {
		return enabled;
	}

}
