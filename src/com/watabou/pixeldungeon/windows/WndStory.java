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
package com.watabou.pixeldungeon.windows;

import com.watabou.pixeldungeon.ui.Window;

import java.util.HashMap;

public class WndStory extends Window {

	private static final int WIDTH = 120;
	private static final int MARGIN = 6;

	private static final float bgR	= 0.77f;
	private static final float bgG	= 0.73f;
	private static final float bgB	= 0.62f;

	public static final int ID_SEWERS		= 0;
	public static final int ID_PRISON		= 1;
	public static final int ID_CAVES		= 2;
	public static final int ID_METROPOLIS	= 3;
	public static final int ID_HALLS		= 4;

	public static int ID(){
		return 9;
	}

	private static final HashMap<Integer, String> CHAPTERS = new HashMap<>();

	static {
		CHAPTERS.put( ID_SEWERS,
		"The Dungeon lies right beneath the City, its upper levels actually constitute the City's sewer system. " +
		"Being nominally a part of the City, these levels are not that dangerous. No one will call it a safe place, " +
		"but at least you won't need to deal with evil magic here." );

		CHAPTERS.put( ID_PRISON,
		"Many years ago an underground prison was built here for the most dangerous criminals. At the time it seemed " +
		"like a very clever idea, because this place indeed was very hard to escape. But soon dark miasma started to permeate " +
		"from below, driving prisoners and guards insane. In the end the prison was abandoned, though some convicts " +
		"were left locked up here." );

		CHAPTERS.put( ID_CAVES,
		"The caves, which stretch down under the abandoned prison, are sparcely populated. They lie too deep to be exploited " +
		"by the City and they are too poor in minerals to interest the dwarves. In the past there was a trade outpost " +
		"somewhere here on the route between these two states, but it has perished since the decline of Dwarven Metropolis. " +
		"Only omnipresent gnolls and subterranean animals dwell here now." );

		CHAPTERS.put( ID_METROPOLIS,
		"Dwarven Metropolis was once the greatest of dwarven city-states. In its heyday the mechanized army of dwarves " +
		"has successfully repelled the invasion of the old god and his demon army. But it is said, that the returning warriors " +
		"have brought seeds of corruption with them, and that victory was the beginning of the end for the underground kingdom." );

		CHAPTERS.put( ID_HALLS,
		"In the past these levels were the outskirts of Metropolis. After the costly victory in the war with the old god " +
		"dwarves were too weakened to clear them of remaining demons. Gradually demons have tightened their grip on this place " +
		"and now it's called Demon Halls.\n\n" +
		"Very few adventurers have ever descended this far..." );
	};



	public WndStory( String text ) {
	}

	@Override
	public void update() {
		super.update();
	}

}
