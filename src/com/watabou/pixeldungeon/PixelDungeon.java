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

import com.nikita22007.multiplayer.noosa.audio.Sample;
import com.watabou.noosa.Game;
import com.watabou.noosa.Scene;
import com.watabou.pixeldungeon.network.Server;
import com.watabou.pixeldungeon.scenes.PixelScene;
import com.watabou.utils.Bundle;
import textualmold9830.Preferences;

public class PixelDungeon extends Game {

	public PixelDungeon( Class<? extends Scene> scene) {
		super(scene);
	}

	protected void onCreate( Bundle savedInstanceState ) {
		updateImmersiveMode();
		Sample.INSTANCE.load(
			com.watabou.pixeldungeon.Assets.SND_CLICK,
			com.watabou.pixeldungeon.Assets.SND_BADGE,
			com.watabou.pixeldungeon.Assets.SND_GOLD,

			com.watabou.pixeldungeon.Assets.SND_DESCEND,
			com.watabou.pixeldungeon.Assets.SND_STEP,
			com.watabou.pixeldungeon.Assets.SND_WATER,
			com.watabou.pixeldungeon.Assets.SND_OPEN,
			com.watabou.pixeldungeon.Assets.SND_UNLOCK,
			com.watabou.pixeldungeon.Assets.SND_ITEM,
			com.watabou.pixeldungeon.Assets.SND_DEWDROP,
			com.watabou.pixeldungeon.Assets.SND_HIT,
			com.watabou.pixeldungeon.Assets.SND_MISS,
			com.watabou.pixeldungeon.Assets.SND_EAT,
			com.watabou.pixeldungeon.Assets.SND_READ,
			com.watabou.pixeldungeon.Assets.SND_LULLABY,
			com.watabou.pixeldungeon.Assets.SND_DRINK,
			com.watabou.pixeldungeon.Assets.SND_SHATTER,
			com.watabou.pixeldungeon.Assets.SND_ZAP,
			com.watabou.pixeldungeon.Assets.SND_LIGHTNING,
			com.watabou.pixeldungeon.Assets.SND_LEVELUP,
			com.watabou.pixeldungeon.Assets.SND_DEATH,
			com.watabou.pixeldungeon.Assets.SND_CHALLENGE,
			com.watabou.pixeldungeon.Assets.SND_CURSED,
			com.watabou.pixeldungeon.Assets.SND_EVOKE,
			com.watabou.pixeldungeon.Assets.SND_TRAP,
			com.watabou.pixeldungeon.Assets.SND_TOMB,
			com.watabou.pixeldungeon.Assets.SND_ALERT,
			com.watabou.pixeldungeon.Assets.SND_MELD,
			com.watabou.pixeldungeon.Assets.SND_BOSS,
			com.watabou.pixeldungeon.Assets.SND_BLAST,
			com.watabou.pixeldungeon.Assets.SND_PLANT,
			com.watabou.pixeldungeon.Assets.SND_RAY,
			com.watabou.pixeldungeon.Assets.SND_BEACON,
			com.watabou.pixeldungeon.Assets.SND_TELEPORT,
			com.watabou.pixeldungeon.Assets.SND_CHARMS,
			com.watabou.pixeldungeon.Assets.SND_MASTERY,
			com.watabou.pixeldungeon.Assets.SND_PUFF,
			com.watabou.pixeldungeon.Assets.SND_ROCKS,
			com.watabou.pixeldungeon.Assets.SND_BURNING,
			com.watabou.pixeldungeon.Assets.SND_FALLING,
			com.watabou.pixeldungeon.Assets.SND_GHOST,
			com.watabou.pixeldungeon.Assets.SND_SECRET,
			com.watabou.pixeldungeon.Assets.SND_BONES,
			com.watabou.pixeldungeon.Assets.SND_BEE,
			com.watabou.pixeldungeon.Assets.SND_DEGRADE,
			Assets.SND_MIMIC );
	}


	@Override
	public void onDestroy(){
		Server.stopServer();
		super.onDestroy();
	}

	public static void switchNoFade( Class<? extends PixelScene> c ) {
		PixelScene.noFade = true;
		switchScene( c );
	}

	/*
	 * ---> Prefernces
	 */

	public static boolean requestedReset() {
		if (PixelDungeon.instance == null){
			return false;
		}
		return PixelDungeon.instance.requestedReset;
	}
	public static void landscape( boolean value ) {
	}

	public static boolean landscape() {
		return width > height;
	}

	// *** IMMERSIVE MODE ****

	private static boolean immersiveModeChanged = false;

    //@SuppressLint("NewApi")  //now is not new
	public static void immerse( boolean value ) {

	}



	//@SuppressLint("NewApi")  //now is not new
	public static void updateImmersiveMode() {

	}

	public static boolean immersed() {
		return false;
	}

	// *****************************

	public static boolean scaleUp() {
		return true;
	}

	public static void onlineMode( boolean value ) {
		textualmold9830.Preferences.onlineMode = value;
	}

	public static boolean onlineMode() {
		return textualmold9830.Preferences.onlineMode;
	}

	public static void serverName( String value ) {
	textualmold9830.Preferences.serverName = value;
	}

	public static String serverName() {
		return textualmold9830.Preferences.serverName;
	}

	public static boolean useCustomRelay() {
		return textualmold9830.Preferences.useCustomRelay;
	}
	public static void useCustomRelay(boolean value) {
		textualmold9830.Preferences.useCustomRelay = value;
	}

	public static String customRelayAddress(){
		return textualmold9830.Preferences.customRelayAddress;
	}
	public static void customRelayAddress(String value) {
		textualmold9830.Preferences.customRelayAddress = value;
	}

	public static int customRelayPort(){
		return textualmold9830.Preferences.customRelayPort;
	}
	public static void customRelayPort(int value) {
		textualmold9830.Preferences.customRelayPort = value;
	}


	public static void challenges( int value ) {
		Preferences.challenges = value;
	}

	public static int challenges() {
		return textualmold9830.Preferences.challenges;
	}


	/*
	 * <--- Preferences
	 */

	public static void reportException( Throwable tr ) {
		tr.printStackTrace();
	}




}
