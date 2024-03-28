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
package com.watabou.pixeldungeon.utils;

import com.watabou.pixeldungeon.network.SendData;
import com.watabou.pixeldungeon.sprites.CharSprite;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class GLog {

	public static final String TAG = "GAME";

	public static final String POSITIVE		= "++ ";
	public static final String NEGATIVE		= "-- ";
	public static final String WARNING		= "** ";
	public static final String HIGHLIGHT	= "@@ ";

	private static final Pattern PUNCTUATION = Pattern.compile( ".*[.,;?! ]$" );

	public static void i( String text, Object... args ) {
		iWithTarget(null, text, args);
	}
	public static void iWithTarget( Integer ID,  String text, Object... args ) {

		if (args.length > 0) {
			text = Utils.format( text, args );
		}

		sendMessage(ID, text);
	}

	public static void p( String text, Object... args ) {
		i( POSITIVE + text, args );
	}

	public static void pWithTarget(Integer ID, String text, Object... args ) {
		iWithTarget(ID, POSITIVE + text, args );
	}
	public static void pExceptTarget(Integer ID, String text, Object... args ) {
		iExceptTarget(ID, POSITIVE + text, args );
	}
	public static void n( String text, Object... args ) {
		i( NEGATIVE + text, args );
	}

	public static void nWithTarget(Integer ID, String text, Object... args ) {
		iWithTarget(ID, NEGATIVE + text, args );
	}
	public static void nExceptTarget(Integer exceptID, String text, Object... args ) {
		nExceptTarget(exceptID, NEGATIVE + text );
	}
	public static void w( String text, Object... args ) {
		i( WARNING + text, args );
	}

	public static void wWithTarget(Integer ID, String text, Object... args ) {
		iWithTarget(ID, WARNING + text, args );
	}

	public static void h( String text, Object... args ) {
		i( HIGHLIGHT + text, args );
	}

	protected static int colorFromMessage(String text)
	{
		int color = CharSprite.DEFAULT;
		if (text.startsWith(GLog.POSITIVE)) {
			color = CharSprite.POSITIVE;
		} else if (text.startsWith(GLog.NEGATIVE)) {
			color = CharSprite.NEGATIVE;
		} else if (text.startsWith(GLog.WARNING)) {
			color = CharSprite.WARNING;
		} else if (text.startsWith(GLog.HIGHLIGHT)) {
			color = CharSprite.NEUTRAL;
		}
		return color;
	}

	protected static String removeColorFromMessage(String text) {
		if (text.startsWith(GLog.POSITIVE)) {
			text = text.substring(GLog.POSITIVE.length());
		} else if (text.startsWith(GLog.NEGATIVE)) {
			text = text.substring(GLog.NEGATIVE.length());
		} else if (text.startsWith(GLog.WARNING)) {
			text = text.substring(GLog.WARNING.length());
		} else if (text.startsWith(GLog.HIGHLIGHT)) {
			text = text.substring(GLog.HIGHLIGHT.length());
		}
		return text;
	}

	protected static String makePretty(String text)
	{
		return Utils.capitalize(text) +
				(PUNCTUATION.matcher(text).matches() ? "" : ".");
	}
	protected static void sendMessage(Integer ID, String text) {
		System.out.printf("%d: %s%n",(ID==null?-1:ID), text);

		int color = colorFromMessage(text);
		text = removeColorFromMessage(text);
		text = makePretty(text);

		if (ID == null) {
			SendData.sendMessageToAll(text, color);
		} else {
			SendData.sendMessage(ID, text, color);
		}

	}

	public static void iExceptTarget(Integer exceptID, String text, Object... args ) {
		if ((args != null) && (args.length > 0)) {
			text = Utils.format(text, args);
		}
		if (exceptID != null) {
			sendMessageExcept(exceptID, text);
		} else {
			sendMessage(null, text);
		}
	}

	protected static void sendMessageExcept(@NotNull Integer ID, String text) {
		System.out.printf("except %d: %s%n",(ID==null?-1:ID), text);

		int color = colorFromMessage(text);
		text = removeColorFromMessage(text);
		text = makePretty(text);

		SendData.sendMessageExcept(ID, text, color);
	}

	public static void wipe() {
		//todo
	}
}
