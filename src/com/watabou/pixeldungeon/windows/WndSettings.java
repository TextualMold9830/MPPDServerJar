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


import com.nikita22007.multiplayer.noosa.audio.Sample;
import com.watabou.pixeldungeon.Assets;
import com.watabou.pixeldungeon.PixelDungeon;
import com.watabou.pixeldungeon.ui.CheckBox;
import com.watabou.pixeldungeon.ui.RedButton;
import com.watabou.pixeldungeon.ui.Window;

import java.awt.*;

public class WndSettings extends Window {

	private static final String TXT_ZOOM_IN			= "+";
	private static final String TXT_ZOOM_OUT		= "-";
	private static final String TXT_ZOOM_DEFAULT	= "Default Zoom";

	private static final String TXT_SCALE_UP		= "Scale up UI";
	private static final String TXT_IMMERSIVE		= "Immersive mode";

	private static final String TXT_MUSIC	= "Music";

	private static final String TXT_SOUND	= "Sound FX";

	private static final String TXT_RELAY = "Online multiplayer";
	private static final String TXT_SET_SERVER_NAME = "Set server name";
	private static final String TXT_RELAY_SETTINGS = "Configure Relay";

	private static final String TXT_SWITCH_PORT	= "Switch to portrait";
	private static final String TXT_SWITCH_LAND	= "Switch to landscape";

	private static final int WIDTH		= 112;
	private static final int BTN_HEIGHT	= 20;
	private static final int GAP 		= 2;

	private RedButton btnZoomOut;
	private RedButton btnZoomIn;

	public WndSettings(boolean inGame ) {
		super();

		Button configureRelay = null;

		if (inGame) {
			updateEnabled();

		} else {

			CheckBox btnScaleUp = new CheckBox( TXT_SCALE_UP ) {
				@Override
				protected void onClick() {
					super.onClick();
					PixelDungeon.scaleUp( checked() );
				}
			};
			btnScaleUp.checked( PixelDungeon.scaleUp() );

			CheckBox btnImmersive = null;
			btnImmersive = new CheckBox( TXT_IMMERSIVE ) {
				@Override
				protected void onClick() {
					super.onClick();
					PixelDungeon.immerse( checked() );
				}
			};
			btnImmersive.checked( PixelDungeon.immersed() );




			CheckBox btnRelay;
			btnRelay = new CheckBox( TXT_RELAY ) {
				@Override
				protected void onClick() {
					super.onClick();
					PixelDungeon.onlineMode(!PixelDungeon.onlineMode());
					Sample.INSTANCE.play( Assets.SND_CLICK );
				}
			};
			btnRelay.checked(PixelDungeon.onlineMode());

			configureRelay = new RedButton(TXT_RELAY_SETTINGS);

		}

		CheckBox btnMusic = new CheckBox( TXT_MUSIC ) {
			@Override
			protected void onClick() {
				super.onClick();
				PixelDungeon.music( checked() );
			}
		};
		btnMusic.checked( PixelDungeon.music() );

		if (inGame) {

		} else {
		}
	}

	private void updateEnabled() {
	}

}
