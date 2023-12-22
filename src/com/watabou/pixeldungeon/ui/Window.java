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
package com.watabou.pixeldungeon.ui;


import  com.nikita22007.multiplayer.utils.Log;
import com.watabou.noosa.Group;
import com.watabou.pixeldungeon.HeroHelp;
import com.watabou.pixeldungeon.Settings;
import com.watabou.pixeldungeon.actors.hero.Hero;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public abstract class Window {
	//todo: memory leak. Remove entries when hero removes
	private static final Map<Integer, Map<Integer, Window>> windows = new HashMap<>(Settings.maxPlayers);
	private static final Map<Integer, Integer> idCounter = new HashMap<>(Settings.maxPlayers); // contains last used Window.id for each hero

	private Hero ownerHero;
	//Each window CURRENTLY open for ownerHero has a unique id. Two windows can have the same id only with different ownerHero.
	private int id;

	public static final int TITLE_COLOR = 0xFFFF44;

	public Window() {
		throw new AssertionError("Window without hero");
	}

	public Window(Hero hero) {
		attachToHero(hero);
	}

	protected synchronized final void attachToHero(Hero hero) {
		if (getId() > 0) {
			if (hero != getOwnerHero()) {
				throw new AssertionError("Attaching window to different heroes");
			}
			return;
		}
		int heroId = HeroHelp.getHeroID(hero);

		setOwnerHero(hero);
		if (!idCounter.containsKey(heroId)) {
			idCounter.put(heroId, 0);
		}
		if (!windows.containsKey(heroId)) {
			windows.put(heroId, new HashMap<>(3));
		}
		setId(idCounter.get(heroId) + 1);
		idCounter.put(heroId, getId());
		windows.get(heroId).put(getId(), this);
	}

	public static void OnButtonPressed(Hero hero, int ID, int button,  JSONObject res) {
		try {

		if (button == -1){
			windows.get(hero).get(ID).onBackPressed();
		} else  {
			windows.get(hero).get(ID).onSelect(button, res);
		}
		} catch (NullPointerException e){
			Log.i("Window", "No such window.");
		}

	}


	public void hide() {
		destroy();
	}

	public void destroy() {
		if (getOwnerHero() != null) {
			Window removed = windows.get(HeroHelp.getHeroID(getOwnerHero())).remove(getId());
			if ((removed != null) && (removed != this)) {
				throw new AssertionError("Removed window is not current Window");
			}
		}
	}



	public void onBackPressed() {
		hide();
	}

	public void onSelect(int button, JSONObject args){
		onSelect(button);
	}

	protected void onSelect(int button){}


	// network synchronization

	public final Hero getOwnerHero() {
		return ownerHero;
	}

	private final void setOwnerHero(Hero ownerHero) {
		this.ownerHero = ownerHero;
	}

	public final int getId() {
		return id;
	}

	private final void setId(int id) {
		this.id = id;
	}

	public  static boolean hasWindow(Hero hero) {
		Map<Integer, Window> heroWindows = windows.getOrDefault(HeroHelp.getHeroID(hero), null);
		return (heroWindows != null) && !heroWindows.isEmpty();
	}
}
