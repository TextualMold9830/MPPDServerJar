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

public class WndRelaySettings extends Window {

    public static final String TXT_CLOSE = "Close";
    ;
    private static final String TXT_USE_CUSTOM_RELAY = "Use custom relay";
    private static final String TXT_SET_RELAY_ADDR = "Custom relay address";
    private static final String TXT_SET_RELAY_PORT = "Custom relay port";

    private static final int WIDTH = 112;
    private static final int BTN_HEIGHT = 20;
    private static final int GAP = 2;

    public WndRelaySettings() {
        super();

        CheckBox btnCustomRelay = new CheckBox(TXT_USE_CUSTOM_RELAY) {
            @Override
            protected void onClick() {
                super.onClick();
                PixelDungeon.useCustomRelay(!PixelDungeon.useCustomRelay());
                Sample.INSTANCE.play(Assets.SND_CLICK);
            }
        };
        btnCustomRelay.checked(PixelDungeon.useCustomRelay());

        Button btnCustomRelayAddr = new RedButton(TXT_SET_RELAY_ADDR) {
            protected void onClick() {

                hide();
                //GameScene.show( new WndSetServerName() );
            }
        };

    }
}
