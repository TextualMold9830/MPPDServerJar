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

import com.nikita22007.multiplayer.noosa.particles.Emitter;
import com.watabou.pixeldungeon.Assets;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.DungeonTilemap;
import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.actors.mobs.npcs.Ghost;
import com.watabou.pixeldungeon.items.DewVial;
import com.watabou.pixeldungeon.network.SendData;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;
import com.watabou.noosa.Scene;
import org.json.JSONException;
import org.json.JSONObject;

public class SewerLevel extends RegularLevel {

	{
		color1 = 0x48763c;
		color2 = 0x59994a;
	}
	
	@Override
	public String tilesTex() {
		return Assets.TILES_SEWERS;
	}
	
	@Override
	public String waterTex() {
		return Assets.WATER_SEWERS;
	}
	
	protected boolean[] water() {
		return Patch.generate( feeling == Feeling.WATER ? 0.60f : 0.45f, 5 );
	}
	
	protected boolean[] grass() {
		return Patch.generate( feeling == Feeling.GRASS ? 0.60f : 0.40f, 4 );
	}
	
	@Override
	protected void decorate() {
		
		for (int i=0; i < WIDTH; i++) {
			if (map[i] == Terrain.WALL &&  
				map[i + WIDTH] == Terrain.WATER &&
				Random.Int( 4 ) == 0) {
				
				map[i] = Terrain.WALL_DECO;
			}
		}
		
		for (int i=WIDTH; i < LENGTH - WIDTH; i++) {
			if (map[i] == Terrain.WALL && 
				map[i - WIDTH] == Terrain.WALL && 
				map[i + WIDTH] == Terrain.WATER &&
				Random.Int( 2 ) == 0) {
				
				map[i] = Terrain.WALL_DECO;
			}
		}
		
		for (int i=WIDTH + 1; i < LENGTH - WIDTH - 1; i++) {
			if (map[i] == Terrain.EMPTY) { 
				
				int count = 
					(map[i + 1] == Terrain.WALL ? 1 : 0) + 
					(map[i - 1] == Terrain.WALL ? 1 : 0) + 
					(map[i + WIDTH] == Terrain.WALL ? 1 : 0) +
					(map[i - WIDTH] == Terrain.WALL ? 1 : 0);
				
				if (Random.Int( 16 ) < count * count) {
					map[i] = Terrain.EMPTY_DECO;
				}
			}
		}
		
		while (true) {
			int pos = roomEntrance.random();
			if (pos != entrance) {
				map[pos] = Terrain.SIGN;
				break;
			}
		}
	}
	
	@Override
	protected void createMobs() {
		super.createMobs();

		Ghost.Quest.spawn( this );
	}
	
	@Override
	protected void createItems() {
		if (Dungeon.dewVial && Random.Int( 4 - Dungeon.depth ) == 0) {
			addItemToSpawn( new DewVial() );
			Dungeon.dewVial = false;
		}
		
		super.createItems();
	}
	
	@Override
	public void addVisuals( Scene scene ) {
		super.addVisuals( scene );
		addVisuals( this, scene );
	}
	
	public static void addVisuals( Level level, Scene scene ) {
		for (int i=0; i < LENGTH; i++) {
			if (level.map[i] == Terrain.WALL_DECO) {
				new Sink( i );
			}
		}
	}
	
	@Override
	public String tileName( int tile ) {
		switch (tile) {
		case Terrain.WATER:
			return "Murky water";
		default:
			return super.tileName( tile );
		}
	}
	
	@Override
	public String tileDesc(int tile) {
		switch (tile) {
		case Terrain.EMPTY_DECO:
			return "Wet yellowish moss covers the floor.";
		case Terrain.BOOKSHELF:
			return "The bookshelf is packed with cheap useless books. Might it burn?";
		default:
			return super.tileDesc( tile );
		}
	}
	
	private static class Sink extends Emitter {
		
		private int pos;
		private float rippleDelay = 0;
		
		private static final Emitter.Factory factory = new Factory() {

			@Override
			public String factoryName() {
				return "sink";
			}
		};
		private static JSONObject sinkObject(int pos){
			JSONObject actionObj = new JSONObject();
			try {
				actionObj.put("action_type", "emitter_decor");
				actionObj.put("type", "sink");
				actionObj.put("pos", pos);
			} catch (JSONException e) {
				throw new RuntimeException(e);
			}
			return actionObj;
		}

		public static void addSink(int pos) {
			SendData.sendCustomActionForAll(sinkObject(pos));
		}
		public static void addSink(int pos, Hero hero) {
			SendData.sendCustomAction(sinkObject(pos), hero);
		}

		public Sink( int pos ) {
			super();
			addSink(pos);
			this.pos = pos;

			PointF p = DungeonTilemap.tileCenterToWorld( pos );
			pos( p.x - 2, p.y + 1, 4, 0 );

			pour( factory, 0.05f );
		}
	}

}
