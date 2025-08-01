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
package com.watabou.pixeldungeon.items;

import java.util.ArrayList;

import com.nikita22007.multiplayer.noosa.audio.Sample;
import com.watabou.pixeldungeon.Assets;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.actors.Actor;
import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.items.wands.WandOfBlink;
import com.watabou.pixeldungeon.levels.Level;
import com.watabou.pixeldungeon.scenes.InterLevelSceneServer;
import com.watabou.pixeldungeon.sprites.ItemSpriteGlowing;
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.pixeldungeon.utils.GLog;
import com.watabou.pixeldungeon.utils.Utils;
import com.watabou.utils.Bundle;

public class LloydsBeacon extends Item {

	private static final String TXT_PREVENTING =
		"Strong magic aura of this place prevents you from using the lloyd's beacon!";

	private static final String TXT_CREATURES =
		"Psychic aura of neighbouring creatures doesn't allow you to use the lloyd's beacon at this moment.";

	private static final String TXT_RETURN =
		"The lloyd's beacon is successfully set at your current location, now you can return here anytime.";

	private static final String TXT_INFO =
		"Lloyd's beacon is an intricate magic device, that allows you to return to a place you have already been.";

	private static final String TXT_SET =
		"\n\nThis beacon was set somewhere on the level %d of Pixel Dungeon.";

	public static final float TIME_TO_USE = 1;

	public static final String AC_SET		= "SET";
	public static final String AC_RETURN	= "RETURN";

	private String returnLevelID = null;
	private int returnPos;

	{
		name = "lloyd's beacon";
		image(ItemSpriteSheet.BEACON);

		unique = true;
	}

	private static final String DEPTH	= "depth";
	private static final String POS		= "pos";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( DEPTH, getReturnLevelID());
		if (getReturnLevelID() != null) {
			bundle.put( POS, returnPos );
		}
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle(bundle);
		setReturnLevelID(bundle.getString( DEPTH ));
		returnPos	= bundle.getInt( POS );
	}

	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( AC_SET );
		if (getReturnLevelID() != null) {
			actions.add( AC_RETURN );
		}
		return actions;
	}

	@Override
	public void execute( Hero hero, String action ) {

		if (action == AC_SET || action == AC_RETURN) {

			if (Dungeon.bossLevel(Dungeon.depth)) {
				hero.spend( LloydsBeacon.TIME_TO_USE );
				GLog.w( TXT_PREVENTING );
				return;
			}

			for (int i=0; i < Level.NEIGHBOURS8.length; i++) {
				if (Actor.findChar( hero.pos + Level.NEIGHBOURS8[i] ) != null) {
					GLog.w( TXT_CREATURES );
					return;
				}
			}
		}

		if (action == AC_SET) {

			setReturnLevelID(hero.level.levelID);
			returnPos = hero.pos;

			hero.spend( LloydsBeacon.TIME_TO_USE );
			hero.busy();

			hero.getSprite().operate( hero.pos );
			Sample.INSTANCE.play( Assets.SND_BEACON );

			GLog.i( TXT_RETURN );

			SendSelfUpdate();

		} else if (action == AC_RETURN) {

			if (getReturnLevelID() == hero.level.levelID) {
				reset();
				WandOfBlink.appear( hero, returnPos );
				hero.level.press( returnPos, hero );
				Dungeon.observeAll();
			} else {
				InterLevelSceneServer.returnTo(getReturnLevelID(), returnPos, hero );
				reset();
			}

			SendSelfUpdate();
		} else {

			super.execute( hero, action );

		}
	}

	public void reset() {
		setReturnLevelID(null);
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	private static final ItemSpriteGlowing WHITE = new ItemSpriteGlowing( 0xFFFFFF );

	public void updateGlowing() {
		setGlowing(getReturnLevelID() != null ? WHITE : null);
	}

	@Override
	public String info() {
		return TXT_INFO + (getReturnLevelID() == null ? "" : Utils.format( TXT_SET, getReturnLevelID()) );
	}

	private String getReturnLevelID() {
		return returnLevelID;
	}

	private void setReturnLevelID(String returnLevelID) {
		this.returnLevelID = returnLevelID;
		updateGlowing();
	}
}
