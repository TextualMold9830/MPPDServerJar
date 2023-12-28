/*
 * Pixel Dungeon Multiplayer
 * * Copyright (C) 2021-2023 Nikita Shaposhnikov
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

import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.actors.mobs.npcs.NPC;
import com.watabou.pixeldungeon.network.SendData;
import com.watabou.pixeldungeon.sprites.CharSprite;
import com.watabou.pixeldungeon.utils.Utils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.List;

public class WndQuest extends WndOptions {

    public WndQuest(Hero owner, NPC questgiver, String text, String... options) {
        super(owner);
        CharSprite questgiverSprite = questgiver.sprite();
        String title = Utils.capitalize(questgiver.name);

        WndOptionsParams params = createCommon(title, text, options);

        params.charSprite = questgiverSprite;
        sendWnd(params);
    }

    protected WndOptionsParams createCommon(@NotNull String title, @NotNull String text, @NotNull String... options) {
        WndOptionsParams params = new WndOptionsParams();
        params.title = title;
        params.message = text;
        params.titleColor = null;
        params.options = List.of(options);
        return params;
    }

    @Override
    protected void onSelect(int index) {
    }
}
