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
package com.watabou.pixeldungeon.scenes;

import com.nikita22007.multiplayer.noosa.audio.Sample;
import com.nikita22007.multiplayer.noosa.particles.Emitter;
import com.nikita22007.multiplayer.server.ui.Banner;
import com.nikita22007.multiplayer.utils.Log;
import com.watabou.noosa.Scene;
import com.watabou.noosa.audio.Music;
import com.watabou.pixeldungeon.*;
import com.watabou.pixeldungeon.actors.Actor;
import com.watabou.pixeldungeon.actors.blobs.Blob;
import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.actors.mobs.Mob;
import com.watabou.pixeldungeon.effects.BannerSprites;
import com.watabou.pixeldungeon.effects.BlobEmitter;
import com.watabou.pixeldungeon.items.Heap;
import com.watabou.pixeldungeon.items.Item;
import com.watabou.pixeldungeon.items.potions.Potion;
import com.watabou.pixeldungeon.levels.Level;
import com.watabou.pixeldungeon.levels.LobbyLevel;
import com.watabou.pixeldungeon.levels.RegularLevel;
import com.watabou.pixeldungeon.network.SendData;
import com.watabou.pixeldungeon.network.Server;
import com.watabou.pixeldungeon.plants.Plant;
import com.watabou.pixeldungeon.sprites.CharSprite;
import com.watabou.pixeldungeon.sprites.HeroSprite;
import com.watabou.pixeldungeon.ui.Window;
import com.watabou.pixeldungeon.windows.WndBag;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import textualmold9830.Preferences;

import java.io.IOException;
import java.util.ArrayList;

import static com.watabou.noosa.Game.timeTotal;

public class GameScene extends PixelScene {     //only client, exclude static

	//todo SEND THIS
	private static final String TXT_WELCOME			= "Welcome to the level %d of Pixel Dungeon!";
	private static final String TXT_WELCOME_BACK	= "Welcome back to the level %d of Pixel Dungeon!";
	private static final String TXT_NIGHT_MODE		= "Be cautious, since the dungeon is even more dangerous at night!";

	private static final String TXT_CHASM	= "Your steps echo across the dungeon.";
	private static final String TXT_WATER	= "You hear the water splashing around you.";
	private static final String TXT_GRASS	= "The smell of vegetation is thick in the air.";
	private static final String TXT_SECRETS	= "The atmosphere hints that this floor hides many secrets.";

	static GameScene scene;

	@Override
	public void create() {
		super.create();
		init();

		scene = this;

		Server.startServerStepLoop();
		Log.i("GameScene created");
		//todo
	}
	public void init() {
		Music.INSTANCE.play( Assets.TUNE, true );
		Music.INSTANCE.volume( 1f );

		//PixelDungeon.lastClass( Dungeon.hero.heroClass.ordinal() );



//		for (Mob mob : level.mobs) {
//			addMobSprite( mob );
//			if (Statistics.amuletHeroID>-1) {
//				mob.beckon( Dungeon.heroes[Statistics.amuletHeroID].pos );
//			}
//		}



//		for (Blob blob : level.blobs.values()) {
//			blob.emitter = null;
//			addBlobSprite( blob );
//		}


		for ( Hero heroobj:Dungeon.heroes) {
			if (heroobj == null){
				continue;
			}
			HeroSprite hero;
			hero = new HeroSprite(heroobj);
			hero.place( heroobj.pos );
			hero.updateArmor();
		}

		ArrayList<Item> dropped = Dungeon.droppedItems.get( Dungeon.depth );
//		if (dropped != null) {
//			for (Item item : dropped) {
//				int pos = level.randomRespawnCell();
//				if (item instanceof Potion) {
//					((Potion)item).shatter( pos );
//				} else if (item instanceof Plant.Seed) {
//					level.plant( (Plant.Seed)item, pos );
//				} else {
//					level.drop( item, pos );
//				}
//			}
//			Dungeon.droppedItems.remove( Dungeon.depth );
//		}

	}

	public void destroy() {

		scene = null;
		Badges.saveGlobal();

		super.destroy();
	}

	public static final double PING_TIME = 2.0;
	private double lastPingTime = 0;

	public synchronized void update() {

		boolean parsedAnything = Server.parseActions();

		//boolean hasConnectedHero = false;
		for (Hero hero : Dungeon.heroes) {
			if (hero != null) {
				if (hero.networkID >= 0) {
		//			hasConnectedHero = true;
					break;
				}
			}
		}
		//TODO: check this. multi level support means levels will be unloaded
//		if (!hasConnectedHero) {
//			switch (Preferences.noConnectedHeroBehaviour) {
//
//				case STOP_SERVER: {
//					if (!(level instanceof LobbyLevel)) {
//						Server.stopServer();
//						return;
//					}
//					break;
//				}
//				case PAUSE_ACTORS: {
//					waitNewJson();
//					return;
//				}
//				case PROCESS_ACTORS: {
//					//continue
//					break;
//				}
//			}
//		}

		if (!PixelDungeon.requestedReset()){
			Actor.process();
		}

		if (timeTotal - lastPingTime >= PING_TIME) {
			for (Hero hero : Dungeon.heroes) {
				if (hero == null) {
					continue;
				}
				lastPingTime = timeTotal;
				hero.resendReady();
			}
		}

		boolean isHeroActing = Actor.currentActor() instanceof Hero;

		for (Hero hero : Dungeon.heroes) {
			if (hero == null) {
				continue;
			}
			if (hero.cellSelector == null) {
				continue;
			}
			hero.cellSelector.enabled = hero.getReady();
		}

		if (!isHeroActing){
			return;
		};
		if (!parsedAnything){
			waitNewJson();
		}
	}

	private void waitNewJson() {

		double sleep_time = (PING_TIME - (timeTotal - lastPingTime)) * 1000;
		int sleep_time_int = (int) sleep_time;
		if (Thread.currentThread() != Server.serverStepThread) return;
		if (sleep_time_int > 0) {
			try {
				this.wait(sleep_time_int);
			} catch (InterruptedException ignored) {
				//also thrown when server should stop
			}
		} else {
			lastPingTime = timeTotal;
		}
	}

	private void addBlobSprite( final Blob gas ) {
		if (gas.emitter == null) {
			//gases.add( new BlobEmitter( gas ) );
			new BlobEmitter( gas );
		}
	}

	private void addMobSprite( Mob mob ) {
		CharSprite sprite = mob.sprite();
		sprite.visible = true;//Dungeon.visible[mob.pos];
		sprite.link( mob );
	}

	public static void addHeroSprite(Hero hero){
		CharSprite sprite  = hero.getSprite();
		sprite.visible = true;
		sprite.link(hero);
	}

	// -------------------------------------------------------

	public static void add( Blob gas, Level level ) {
		Actor.add( gas, level );
		if (scene != null) {
			scene.addBlobSprite( gas );
		}
	}

	public static void add( Heap heap ) {
		return;
	}

	public static void discard( Heap heap ) {
		return;
	}

	public static void add( Mob mob, Level level ) {
		mob.level.mobs.add( mob );
		Actor.add( mob, level );
		Actor.occupyCell( mob );
		scene.addMobSprite( mob );
	}

	public static void add( Mob mob, float delay, Level level ) {
		level.mobs.add( mob );
		Actor.addDelayed( mob, delay, level );
		Actor.occupyCell( mob );
		scene.addMobSprite( mob );
	}

	public static void ripple( int pos ) {
		JSONObject actionObj = new JSONObject();
		try {
			actionObj.put("action_type", "ripple_visual");
			actionObj.put("pos", pos);
		} catch (JSONException ignore) {
		}
		SendData.sendCustomActionForAll(actionObj);
	}

	public static Emitter emitter() {
		if (scene != null) {
			Emitter emitter = new Emitter();
			return emitter;
		} else {
			return null;
		}
	}

	public static void pickUp( Item item ) { }

	public static void updateMap() {
		//todo use this to optimize map updates
		return;
	}

	public static void updateMap( int cell ) {
		//todo use this to optimize map updates
		return;
	}

	public static void discoverTile( int pos, int oldValue ) {
		SendData.sendActionDiscoverTile(pos, oldValue);
	}

	public static void show( Window wnd ) {
		cancelCellSelector(wnd.getOwnerHero());
		if (wnd.getOwnerHero() == null) {
			throw new RuntimeException("Window without owner");
		}
	}

	public static void flash( int color ) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("action_type", "game_scene_flash");
			obj.put("color", color);
			obj.put("light", true);
		} catch (JSONException ignored) {

		}
		SendData.sendCustomActionForAll(obj);
	}

	public static void gameOver(@NotNull Hero hero) {
		Banner.show(hero, BannerSprites.Type.GAME_OVER, 0x000000, 1f);
		Sample.INSTANCE.play(Assets.SND_DEATH);
	}

	public static void bossSlain() {
		for (Hero hero : Dungeon.heroes) {
			if (hero == null) {
				continue;
			}
			Banner.show(hero, BannerSprites.Type.BOSS_SLAIN, 0xFFFFFF, 0.3f, 5f);
		}
		Sample.INSTANCE.play(Assets.SND_BOSS);
	}

	public static void handleCell(Hero hero, int cell ) {
		hero.cellSelector.select( cell );
	}

	public static void selectCell(  Hero hero,  CellSelector.Listener listener ) {
		hero.cellSelector.setListener(listener);
	}

	private static boolean cancelCellSelector(Hero hero) {
		if (hero == null) {
			return true;
		}
		if (hero.cellSelector.getListener() != null && hero.cellSelector.getListener() != hero.defaultCellListener) {
			hero.cellSelector.cancel();
			return true;
		} else {
			return false;
		}
	}

	public static WndBag selectItem(  Hero owner,WndBag.Listener listener, WndBag.Mode mode, String title ) {
		cancelCellSelector(owner);

		WndBag wnd = new WndBag(owner, listener, mode, title );

		return wnd;
	}

	static boolean cancel(final Hero hero) {
		if (hero.curAction != null || hero.restoreHealth) {

			hero.curAction = null;
			hero.restoreHealth = false;
			return true;

		} else {

			return cancelCellSelector(hero);

		}
	}

	public static void ready(@NotNull Hero hero) {
		selectCell(hero, hero.defaultCellListener );
	}
	private synchronized void notifyScene(){
		this.notify();
	}
	public static void notifySelf() {
		if (scene != null) {
			scene.notifyScene();
		}
	}
}
