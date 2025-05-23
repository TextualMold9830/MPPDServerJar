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

import com.watabou.pixeldungeon.actors.hero.HeroClass;
import com.watabou.pixeldungeon.utils.Utils;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.SystemTime;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public enum Rankings {

	INSTANCE;

	public static final int TABLE_SIZE	= 6;

	public static final String RANKINGS_FILE = "save/rankings.dat";
	public static final String DETAILS_FILE = "game_%d.dat";

	public ArrayList<Record> records;
	public int lastRecord;
	public int totalNumber;
	public int wonNumber;

	public void submit( boolean win ) { //Fixme?

		load();

		Record rec = new Record();

		rec.info	= com.watabou.pixeldungeon.Dungeon.resultDescription;
		rec.win		= win;
		rec.heroClass	= com.watabou.pixeldungeon.Dungeon.heroes[0].heroClass;
		rec.armorTier	= com.watabou.pixeldungeon.Dungeon.heroes[0].tier();
		rec.score	= score( win );

		String gameFile = Utils.format( DETAILS_FILE, SystemTime.now );
		try {
			com.watabou.pixeldungeon.Dungeon.saveGame( );
			rec.gameFile = gameFile;
		} catch (IOException e) {
			rec.gameFile = "";
		}

		records.add( rec );

		Collections.sort( records, scoreComparator );

		lastRecord = records.indexOf( rec );
		int size = records.size();
		if (size > TABLE_SIZE) {

			Record removedGame;
			if (lastRecord == size - 1) {
				removedGame = records.remove( size - 2 );
				lastRecord--;
			} else {
				removedGame = records.remove( size - 1 );
			}

			if (removedGame.gameFile.length() > 0) {
				try {
					Files.deleteIfExists(Paths.get(removedGame.gameFile));
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}

		totalNumber++;
		if (win) {
			wonNumber++;
		}

		Badges.validateGamesPlayed();

		save();
	}

	private int score( boolean win ) {
		return (Statistics.goldCollected + Dungeon.heroes[0].lvl * Statistics.deepestFloor * 100) * (win ? 2 : 1);
	}

	private static final String RECORDS	= "records";
	private static final String LATEST	= "latest";
	private static final String TOTAL	= "total";
	private static final String WON		= "won";

	public void save() {
		Bundle bundle = new Bundle();
		bundle.put( RECORDS, records );
		bundle.put( LATEST, lastRecord );
		bundle.put( TOTAL, totalNumber );
		bundle.put( WON, wonNumber );

		try {
			OutputStream output = Files.newOutputStream(Paths.get(RANKINGS_FILE));
			Bundle.write( bundle, output );
			output.close();
		} catch (Exception e) {
		}
	}

	public void load() {

		if (records != null) {
			return;
		}

		records = new ArrayList<>();

		try {
			InputStream input = Files.newInputStream(Paths.get(RANKINGS_FILE));
			Bundle bundle = Bundle.read( input );
			input.close();

			for (Bundlable record : bundle.getCollection( RECORDS )) {
				records.add( (Record)record );
			}
			lastRecord = bundle.getInt( LATEST );

			totalNumber = bundle.getInt( TOTAL );
			if (totalNumber == 0) {
				totalNumber = records.size();
			}

			wonNumber = bundle.getInt( WON );
			if (wonNumber == 0) {
				for (Record rec : records) {
					if (rec.win) {
						wonNumber++;
					}
				}
			}

		} catch (Exception e) {
		}
	}

	public static class Record implements Bundlable {

		private static final String REASON	= "reason";
		private static final String WIN		= "win";
		private static final String SCORE	= "score";
		private static final String TIER	= "tier";
		private static final String GAME	= "gameFile";

		public String info;
		public boolean win;

		public HeroClass heroClass;
		public int armorTier;

		public int score;

		public String gameFile;

		@Override
		public void restoreFromBundle( Bundle bundle ) {

			info	= bundle.getString( REASON );
			win		= bundle.getBoolean( WIN );
			score	= bundle.getInt( SCORE );

			heroClass	= HeroClass.restoreInBundle( bundle );
			armorTier	= bundle.getInt( TIER );

			gameFile	= bundle.getString( GAME );
		}

		@Override
		public void storeInBundle( Bundle bundle ) {

			bundle.put( REASON, info );
			bundle.put( WIN, win );
			bundle.put( SCORE, score );

			heroClass.storeInBundle( bundle );
			bundle.put( TIER, armorTier );

			bundle.put( GAME, gameFile );
		}
	}

	private static final Comparator<Record> scoreComparator = new Comparator<Record>() {
		@Override
		public int compare( Record lhs, Record rhs ) {
			return (int)Math.signum( rhs.score - lhs.score );
		}
	};
}
