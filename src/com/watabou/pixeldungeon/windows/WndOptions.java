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

import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.items.Item;
import com.watabou.pixeldungeon.network.SendData;
import com.watabou.pixeldungeon.sprites.CharSprite;
import com.watabou.pixeldungeon.sprites.ItemSpriteGlowing;
import com.watabou.pixeldungeon.ui.RedButton;
import com.watabou.pixeldungeon.ui.Window;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public abstract class WndOptions extends Window {

    protected WndOptions(Hero hero) {
        super(hero);
    }

    public WndOptions(Hero owner, String title, String message, String... options) {
        super(owner);
        WndOptionsParams params = new WndOptionsParams();
        params.title = title;
        params.message = message;
        params.options = List.of(options);
        sendWnd(params);
    }


    protected void sendWnd(@NotNull Item item, @NotNull String title, @Nullable Integer titleColor, @NotNull String message, String... options) {
        WndOptionsParams params = new WndOptionsParams();
        params.title = title;
        params.titleColor = titleColor;
        params.message = message;
        params.item = item;
        sendWnd(params);
    }

    protected void sendWnd(WndOptionsParams params) {
        SendData.sendWindow(getOwnerHero().networkID, "wnd_option", getId(), params.toJSONObject(getOwnerHero()));
    }

    protected static final class WndOptionsParams {
        public @Nullable Item item;
        public @Nullable CharSprite charSprite;
        public @NotNull String title = "Untitled";
        public @Nullable Integer titleColor = null;
        public @NotNull String message = "MissingNo";
        public List<String> options = new ArrayList<String>(3);

        public JSONObject toJSONObject(Hero owner) {
            JSONObject params = new JSONObject();

            try {
                params.put("title", title);
                params.put("title_color", titleColor);
                params.put("message", message);
                JSONArray optionsArr = new JSONArray();
                for (int i = 0; i < options.size(); i += 1) {
                    optionsArr.put(options.get(i));
                }
                params.put("options", optionsArr);
                if (item != null) {
                    params.put("item", item.toJsonObject(owner));
                } else if (charSprite != null) {
                    String spriteAsset = charSprite.getSpriteAsset();
                    if (spriteAsset != null) {
                        params.put("sprite_asset", spriteAsset);
                    } else {
                        params.put("sprite_class", charSprite.spriteName());
                    }
                }
            } catch (JSONException ignored) {
            }
            return params;
        }

    }

    protected abstract void onSelect(int index);
}
