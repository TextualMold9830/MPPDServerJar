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
package com.watabou.pixeldungeon.sprites;

import com.watabou.pixeldungeon.actors.mobs.Mob;
import com.watabou.pixeldungeon.texturepack.TexturePackManager;
import org.jetbrains.annotations.Nullable;

public class MobSprite extends CharSprite {

	@Override
	public void update() {
		setSleeping(ch != null && ((Mob) ch).getState() == ((Mob)ch).SLEEPEING);
		super.update();
	}

	@Override
	public void onComplete( Animation anim ) {

		super.onComplete( anim );

		if (anim == die) {
			MobSprite.this.killAndErase();
		}
	}

	public void fall() {
		this.killAndErase();
	}

	@Override
	public @Nullable String getSpriteAsset() {
		return TexturePackManager.getMobAnimation(getClass());
	}
}
