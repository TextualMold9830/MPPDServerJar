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


import com.watabou.noosa.Game;
import com.watabou.pixeldungeon.actors.Actor;
import com.watabou.pixeldungeon.actors.Char;
import com.watabou.pixeldungeon.actors.buffs.Amok;
import com.watabou.pixeldungeon.actors.buffs.Light;
import com.watabou.pixeldungeon.actors.buffs.Rage;
import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.actors.hero.HeroClass;
import com.watabou.pixeldungeon.actors.mobs.npcs.Blacksmith;
import com.watabou.pixeldungeon.actors.mobs.npcs.Ghost;
import com.watabou.pixeldungeon.actors.mobs.npcs.Imp;
import com.watabou.pixeldungeon.actors.mobs.npcs.Wandmaker;
import com.watabou.pixeldungeon.items.Ankh;
import com.watabou.pixeldungeon.items.Item;
import com.watabou.pixeldungeon.items.potions.Potion;
import com.watabou.pixeldungeon.items.rings.Ring;
import com.watabou.pixeldungeon.items.scrolls.Scroll;
import com.watabou.pixeldungeon.items.wands.Wand;
import com.watabou.pixeldungeon.levels.*;
import com.watabou.pixeldungeon.network.SendData;
import com.watabou.pixeldungeon.network.Server;
import com.watabou.pixeldungeon.scenes.InterLevelSceneServer;
import com.watabou.pixeldungeon.scenes.StartScene;
import com.watabou.pixeldungeon.utils.BArray;
import com.watabou.pixeldungeon.utils.Utils;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import textualmold9830.plugins.PluginManager;
import textualmold9830.plugins.events.DungeonGenerateLevelEvent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

import static com.watabou.pixeldungeon.BuildConfig.DEBUG;
import static com.watabou.pixeldungeon.HeroHelp.getHeroID;
import static com.watabou.pixeldungeon.network.SendData.*;

public class Dungeon {

	public static int potionOfStrength;
	public static int scrollsOfUpgrade;
	public static int scrollsOfEnchantment;
	public static boolean dewVial;		// true if the dew vial can be spawned

	public static int challenges;
	@Nullable public static Hero[] heroes;
	/**
	 * Change here to use custom level
	 */
	public static Level level;

	public static int depth;
	//public static int gold;
	// Reason of death
	public static String resultDescription;

	public static boolean nightMode;

	public static HashMap<Integer, ArrayList<Item>> droppedItems;

	public static void init() {
		Dungeon.heroes = new Hero[Settings.maxPlayers];

		challenges = PixelDungeon.challenges();

		Actor.clear();

		PathFinder.setMapSize( Level.WIDTH, Level.HEIGHT );

		Scroll.initLabels();
		Potion.initColors();
		Wand.initWoods();
		Ring.initGems();

		Statistics.reset();
		Journal.reset();

		depth = -1;

		droppedItems = new HashMap<>();

		potionOfStrength = 0;
		scrollsOfUpgrade = 0;
		scrollsOfEnchantment = 0;
		dewVial = true;

		Ghost.Quest.reset();
		Wandmaker.Quest.reset();
		Blacksmith.Quest.reset();
		Imp.Quest.reset();

		Room.shuffleTypes();

		com.watabou.pixeldungeon.Badges.reset();

	}

	public static boolean isChallenged( int mask ) {
		return (challenges & mask) != 0;
	}

	public static Level newLevel() {

		Dungeon.level = null;
		Actor.clear();

		depth++;
		if (depth > Statistics.deepestFloor) {
			Statistics.deepestFloor = depth;

			if (Statistics.qualifiedForNoKilling) {
				Statistics.completedWithNoKilling = true;
			} else {
				Statistics.completedWithNoKilling = false;
			}
		}

		for (Hero hero: heroes) {
			if (hero != null) {
				Arrays.fill(hero.fieldOfView, false);
			}
		}

		Level level;
		switch (depth) {
		case 0:
			level = new LobbyLevel();
			break;
		case 1:
		case 2:
		case 3:
		case 4:
			level = new SewerLevel();
			break;
		case 5:
			level = new SewerBossLevel();
			break;
		case 6:
		case 7:
		case 8:
		case 9:
			level = new PrisonLevel();
			break;
		case 10:
			level = new PrisonBossLevel();
			break;
		case 11:
		case 12:
		case 13:
		case 14:
			level = new CavesLevel();
			break;
		case 15:
			level = new CavesBossLevel();
			break;
		case 16:
		case 17:
		case 18:
		case 19:
			level = new CityLevel();
			break;
		case 20:
			level = new CityBossLevel();
			break;
		case 21:
			level = new LastShopLevel();
			break;
		case 22:
		case 23:
		case 24:
			level = new HallsLevel();
			break;
		case 25:
			level = new HallsBossLevel();
			break;
		case 26:
			level = new LastLevel();
			break;
		default:
			level = new DeadEndLevel();
			Statistics.deepestFloor--;
		}
		DungeonGenerateLevelEvent event = new DungeonGenerateLevelEvent(depth, level);
		Server.pluginManager.fireEvent(event);
		level = event.level;
		level.create();
		Statistics.qualifiedForNoKilling = !bossLevel(depth);

		return level;
	}

	public static void resetLevel() {

		Actor.clear();

		for (Hero hero: heroes) {
			if (hero != null) {
				Arrays.fill(hero.fieldOfView, false);
			}
		}

		level.reset();
		switchLevelToAll( level, level.entrance );
	}

	public static boolean hasHunger(int depth) {
		return depth != 0;
	}

	public static boolean shopOnLevel(int depth) {
		return depth == 6 || depth == 11 || depth == 16;
	}

	public static boolean bossLevel( int depth ) {
		return depth == 5 || depth == 10 || depth == 15 || depth == 20 || depth == 25;
	}


	public static int GetPosNear(int pos)
	{
		for (int step:level.NEIGHBOURS9) {
			if (Actor.findChar(pos+step)==null){
				return pos+step;
			}
		}
		return -1;
	}

	public static void removeHero(Hero hero){
		if (Settings.killOnDisconnect) {
			if (hero == null) {
				return;
			}
			int ID = Arrays.asList(heroes).indexOf(hero);
			hero.die(Char.GodPunishment.INSTANCE);
			if (ID == -1) {
				return;
			}
		} else {
			hero.next();
		}
		int ID = Arrays.asList(heroes).indexOf(hero);
		if (heroes[ID].isAlive()) {
			saveHero(heroes[ID]);
		}
		Actor.freeCell(heroes[ID].pos);
		Actor.remove(hero);
		heroes[ID] = null;
	}
	public static void saveHero(Hero hero){
        try
		{
			Bundle bundle = new Bundle();
			hero.storeInBundle(bundle);
            OutputStream saveFile = Files.newOutputStream(Path.of("save/heroes",hero.getUUID()), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
			Bundle.write(bundle, saveFile);
			saveFile.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
	public static Optional<Hero> loadHero(String uuid){
		if (uuid != null && !uuid.isBlank()) {
			Path savePath = Path.of("save/heroes/", uuid);
			if (Files.exists(savePath)) {
				try {
					Bundle bundle = Bundle.read(Files.newInputStream(savePath));
					if (!bundle.isNull()) {
						Hero hero = new Hero();
						hero.restoreFromBundle(bundle);
						return Optional.of(hero);
					}
				} catch (IOException e) {
					System.out.println(uuid);
					e.printStackTrace();
					return Optional.empty();
				}
			} else {
				System.out.println("Failed to find hero with UUID: " + uuid);
			}
		}
		return Optional.empty();
	}

	public static void switchLevelToAll(final Level level,int pos ){
        switchLevel(level);
        for (Hero hero:heroes) {
            if (hero!=null){
                switchLevelChangePosition(pos,hero);
            }
        }
	}

	private static void switchLevelChangePosition(int pos, @NotNull Hero hero)
    {
        hero.pos = pos != -1 ? (Level.getNearClearCell(pos)) : Level.getNearClearCell(level.exit);

        sendDepth(hero.networkID, depth);

        Light light = hero.buff( Light.class );
        hero.viewDistance = light == null ? level.viewDistance : Math.max( Light.DISTANCE, level.viewDistance );

        observe(hero);
    }
	public static void switchLevel(final Level level, int pos, @NotNull Hero hero ) {
		switchLevelToAll(level, pos);  //todo change this for multilevels support
		/*
		switchLevel(level);
        swichLevelChangePosition(pos,hero);
		 */
	}

	public static void switchLevel(final Level level) {
		//todo rewrite
		//todo add cheking Hero pos is  clear
		if (Dungeon.level == level) {
			return;
		}

		nightMode = new Date().getHours() < 7;

		Dungeon.level = level;
		Actor.init();

		Actor respawner = level.respawner();
		if (respawner != null) {
			Actor.add(level.respawner());
		}
		for (Hero hero:heroes) {
			if (hero == null){
				continue;
			}
			if (hero.networkID == -1){
				continue;
			}

			sendLevel(level, hero.networkID);
			sendAllChars(hero.networkID);
			sendHeroNewID(hero, hero.networkID);
		}
	}

	public static void dropToChasm( Item item ) {
		int depth = Dungeon.depth + 1;
		ArrayList<Item> dropped = Dungeon.droppedItems.get( depth );
		if (dropped == null) {
			Dungeon.droppedItems.put( depth, dropped = new ArrayList<Item>() );
		}
		dropped.add( item );
	}

	public static boolean posNeeded() {
		int[] quota = {4, 2, 9, 4, 14, 6, 19, 8, 24, 9};
		return chance( quota, potionOfStrength );
	}

	public static boolean souNeeded() {
		int[] quota = {5, 3, 10, 6, 15, 9, 20, 12, 25, 13};
		return chance( quota, scrollsOfUpgrade );
	}

	public static boolean soeNeeded() {
		return Random.Int( 12 * (1 + scrollsOfEnchantment) ) < depth;
	}

	private static boolean chance( int[] quota, int number ) {

		for (int i=0; i < quota.length; i += 2) {
			int qDepth = quota[i];
			if (depth <= qDepth) {
				int qNumber = quota[i + 1];
				return Random.Float() < (float)(qNumber - number) / (qDepth - depth + 1);
			}
		}

		return false;
	}

	public static final String GAME_FILE = "save/game.dat";
	private static final String DEPTH_FILE = "save/depth%d.dat";
	private static final String HERO_DIRECTORY = "save/heroes/";

	/*
	private static final String WR_GAME_FILE	= "warrior.dat";
	private static final String WR_DEPTH_FILE	= "warrior%d.dat";

	private static final String MG_GAME_FILE	= "mage.dat";
	private static final String MG_DEPTH_FILE	= "mage%d.dat";

	private static final String RN_GAME_FILE	= "ranger.dat";
	private static final String RN_DEPTH_FILE	= "ranger%d.dat";

	private static final String HN_GAME_FILE	= "huntress.dat";
	private static final String HN_DEPTH_FILE	= "huntress%d.dat";
	*/
	private static final String VERSION		= "version";
	private static final String CHALLENGES	= "challenges";
	private static final String HEROES		= "heroes";
	private static final String DEPTH		= "depth";
	private static final String LEVEL		= "level";
	private static final String DROPPED		= "dropped%d";
	private static final String POS			= "potionsOfStrength";
	private static final String SOU			= "scrollsOfEnhancement";
	private static final String SOE			= "scrollsOfEnchantment";
	private static final String DV			= "dewVial";
	private static final String CHAPTERS	= "chapters";
	private static final String QUESTS		= "quests";
	private static final String BADGES		= "badges";
	private static final String MAX_PLAYERS_SETTING		= "max_players_count";

	private static String thisGameSaveFile;
	private static String thisGameDepthSaveFile;

	public static String gameFile( HeroClass cl ) {
	/*	switch (cl) {
		case WARRIOR:
			return WR_GAME_FILE;
		case MAGE:
			return MG_GAME_FILE;
		case ROGUE:
			return RN_GAME_FILE;
		case HUNTRESS:
			return HN_GAME_FILE;
		default:
			return GAME_FILE;
		}*/
		return GAME_FILE;
	}

	private static String depthFile( HeroClass cl ) {
		/*switch (cl) {
		case WARRIOR:
			return WR_DEPTH_FILE;
		case MAGE:
			return MG_DEPTH_FILE;
		case ROGUE:
			return RN_DEPTH_FILE;
		case HUNTRESS:
			return HN_DEPTH_FILE;
		default:
			return DEPTH_FILE;
		}*/
		return DEPTH_FILE;
	}

	public static void saveGame() throws IOException { //TODO FIX IT
		try {
			Bundle bundle = new Bundle();

			bundle.put( VERSION, Game.version );
			bundle.put( MAX_PLAYERS_SETTING, Settings.maxPlayers );
			bundle.put( CHALLENGES, challenges );
			for(Hero hero: Dungeon.heroes) {
				if (hero != null) {
					Dungeon.saveHero(hero);
				}
			}
			bundle.put( DEPTH, depth );
			for (int d : droppedItems.keySet()) {
				bundle.put( String.format( DROPPED, d ), droppedItems.get( d ) );
			}

			bundle.put( POS, potionOfStrength );
			bundle.put( SOU, scrollsOfUpgrade );
			bundle.put( SOE, scrollsOfEnchantment );
			bundle.put( DV, dewVial );

			Bundle quests = new Bundle();
			Ghost		.Quest.storeInBundle( quests );
			Wandmaker	.Quest.storeInBundle( quests );
			Blacksmith	.Quest.storeInBundle( quests );
			Imp			.Quest.storeInBundle( quests );
			bundle.put( QUESTS, quests );

			Room.storeRoomsInBundle( bundle );

			Statistics.storeInBundle( bundle );
			Journal.storeInBundle( bundle );

			Scroll.save( bundle );
			Potion.save( bundle );
			Wand.save( bundle );
			Ring.save( bundle );

			Bundle badges = new Bundle();
			com.watabou.pixeldungeon.Badges.saveLocal( badges );
			bundle.put( BADGES, badges );

			OutputStream output = Files.newOutputStream(Path.of(GAME_FILE));
			Bundle.write( bundle, output );
			output.close();

		} catch (Exception e) {

			GamesInProgress.setUnknown( StartScene.curClass );
		}
	}

	public static void saveLevel() throws IOException {
		Bundle bundle = new Bundle();
		bundle.put( LEVEL, level );
		OutputStream output = Files.newOutputStream(Path.of(Utils.format(depthFile(StartScene.curClass), depth)));
		Bundle.write( bundle, output );
		output.close();
	}

	public static void saveAll() throws IOException { //fixme
			Actor.fixTime();
			saveGame();
			saveLevel();

			GamesInProgress.set( null, depth, -1, challenges != 0 );

//		} else if (WndResurrect.instance != null) {
//
//			WndResurrect.instance.hide();
//			/*Hero*/ heroes[0].reallyDie( WndResurrect.causeOfDeath );
//
//		}
	}



	public static void loadGame(boolean fullLoad ) throws IOException {

		Bundle bundle = gameBundle( GAME_FILE );

		Dungeon.challenges = bundle.getInt( CHALLENGES );

		Dungeon.level = null;
		Dungeon.depth = -1;

		if (fullLoad) {
			PathFinder.setMapSize( Level.WIDTH, Level.HEIGHT );
		}

		Scroll.restore( bundle );
		Potion.restore( bundle );
		Wand.restore( bundle );
		Ring.restore( bundle );

		potionOfStrength = bundle.getInt( POS );
		scrollsOfUpgrade = bundle.getInt( SOU );
		scrollsOfEnchantment = bundle.getInt( SOE );
		dewVial = bundle.getBoolean( DV );

		if (fullLoad) {

			Bundle quests = bundle.getBundle( QUESTS );
			if (!quests.isNull()) {
				Ghost.Quest.restoreFromBundle( quests );
				Wandmaker.Quest.restoreFromBundle( quests );
				Blacksmith.Quest.restoreFromBundle( quests );
				Imp.Quest.restoreFromBundle( quests );
			} else {
				Ghost.Quest.reset();
				Wandmaker.Quest.reset();
				Blacksmith.Quest.reset();
				Imp.Quest.reset();
			}

			Room.restoreRoomsFromBundle( bundle );
		}

		Bundle badges = bundle.getBundle( BADGES );
		if (!badges.isNull()) {
			com.watabou.pixeldungeon.Badges.loadLocal( badges );
		} else {
			com.watabou.pixeldungeon.Badges.reset();
		}

		@SuppressWarnings("unused")
		String version = bundle.getString( VERSION );
		depth = bundle.getInt(DEPTH);
		Statistics.restoreFromBundle( bundle );
		Journal.restoreFromBundle( bundle );
		switchLevel(loadLevel());
		droppedItems = new HashMap<>();
		for (int i=2; i <= Statistics.deepestFloor + 1; i++) {
			ArrayList<Item> dropped = new ArrayList<Item>();
			for (Bundlable b : bundle.getCollection( String.format( DROPPED, i ) ) ) {
				dropped.add( (Item)b );
			}
			if (!dropped.isEmpty()) {
				droppedItems.put( i, dropped );
			}
		}
	}

	public static Level loadLevel( HeroClass cl ) throws IOException {

		Dungeon.level = null;
		Actor.clear();

		InputStream input = Files.newInputStream(Paths.get(Utils.format(depthFile(cl), depth))) ;
		Bundle bundle = Bundle.read( input );
		input.close();

		return (Level)bundle.get( "level" );
	}

	public static Level loadLevel() throws IOException {
		return loadLevel(StartScene.curClass);
	};

	public static void deleteGame( HeroClass cl, boolean deleteLevels ) {
		deleteGame(deleteLevels);
	}

	public static void deleteGame( boolean deleteLevels ) {

		try {
			Files.deleteIfExists(Paths.get(GAME_FILE));
			if (Files.exists(Path.of("save/heroes/"))) {
			Files.list(Path.of("save/heroes/")).forEach((path->{
				try {
					Files.delete(path);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}));
			Files.delete(Path.of("save/heroes/"));
			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		if (deleteLevels) {
			int depth = 1;
			while (true) {
				try {
					if (!Files.deleteIfExists(Paths.get(Utils.format(DEPTH_FILE, depth)))) break;
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				depth++;
			}
		}
		GamesInProgress.delete( );
	}

	public static Bundle gameBundle( String fileName ) throws IOException {

		InputStream input = Files.newInputStream(Paths.get(GAME_FILE));
		Bundle bundle = Bundle.read( input );
		input.close();

		return bundle;
	}

	public static void preview( GamesInProgress.Info info, Bundle bundle ) {
		info.depth = bundle.getInt( DEPTH );
		info.challenges = (bundle.getInt( CHALLENGES ) != 0);
		if (info.depth == -1) {
			info.depth = bundle.getInt( "maxDepth" );	// FIXME
		}
		Hero.preview( info, null );
	}

	public static void fail( String desc ) { //todo rewritre it
		resultDescription = desc;
		for (Hero hero : heroes) {
			if (hero!=null &&  !hero.isAlive()) {
				if (hero.belongings.getItem(Ankh.class) == null) {
					Rankings.INSTANCE.submit(false);
				}
			}
		}
	}

	public static void win( String desc ) {
		for (Hero hero:heroes) {
		if  (hero!=null){
				hero.belongings.identify();
			}
		}

		if (challenges != 0) {
			Badges.validateChampion();
		}

		resultDescription = desc;
		Rankings.INSTANCE.submit( true );
		//todo: send win. (Surface scene)
	}

	public static void observeAll() {
		for (Hero hero:heroes) {
			if (hero!=null){
				observe(hero);
			}

		}
	}

	public static void observe(@NotNull Hero hero) {
	observe(hero, true);
	}

	public static void observe(@NotNull Hero hero, boolean send){

		if (level == null) {
			return;
		}

		level.updateFieldOfView( hero );

		boolean[] newVisited;

		newVisited = BArray.or( level.visited, hero.fieldOfView, null );
		boolean[] diff;
		diff = BArray.xor(level.visited, newVisited, null);
				level.visited  = newVisited;
			//todo fix this
			addToSendLevelVisitedState(level, diff);

		if (send) {
			int networkID = getHeroID(hero);
			addToSendHeroVisibleCells(hero.fieldOfView,networkID);
			SendData.flush(networkID);
		}
	}

	public static boolean visibleforAnyHero(int pos) {
		for (Hero hero : heroes) {
			if (hero == null) {
				continue;
			}
			if (hero.fieldOfView[pos]) {
				return true;
			}
		}
		return false;
	}

	public static boolean[] visibleForHeroes(int ...positions) {
		boolean[] result = new boolean[heroes.length];
		for (int pos : positions) {
			for (int i = 0; i < heroes.length; i++) {
				if (heroes[i] != null) {
					result[i] = result[i] || heroes[i].fieldOfView[pos];
				}
			}
		}
		return result;
	}

	private static boolean[] passable = new boolean[Level.LENGTH];

	public static int findPath( Char ch, int from, int to, boolean[] pass, boolean[] visible ) {

		if (Level.adjacent( from, to )) {
			return Actor.findChar( to ) == null && (pass[to] || Level.avoid[to]) ? to : -1;
		}

		if (ch.flying || ch.buff( Amok.class ) != null || ch.buff( Rage.class ) != null) {
			BArray.or( pass, Level.avoid, passable );
		} else {
			System.arraycopy( pass, 0, passable, 0, Level.LENGTH );
		}

		for (Actor actor : Actor.all()) {
			if (actor instanceof Char) {
				int pos = ((Char)actor).pos;
				if (visible[pos]) {
					passable[pos] = false;
				}
			}
		}

		return PathFinder.getStep( from, to, passable );

	}

	public static int flee( Char ch, int cur, int from, boolean[] pass, boolean[] visible ) {

		if (ch.flying) {
			BArray.or( pass, Level.avoid, passable );
		} else {
			System.arraycopy( pass, 0, passable, 0, Level.LENGTH );
		}

		for (Actor actor : Actor.all()) {
			if (actor instanceof Char) {
				int pos = ((Char)actor).pos;
				if (visible[pos]) {
					passable[pos] = false;
				}
			}
		}
		passable[cur] = true;

		return PathFinder.getStepBack( cur, from, passable );

	}
}
