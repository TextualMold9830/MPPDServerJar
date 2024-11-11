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

import com.watabou.pixeldungeon.Assets;
import com.watabou.pixeldungeon.actors.Char;
import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.actors.mobs.npcs.MirrorImage;
import org.jetbrains.annotations.NotNull;

public class MirrorSprite extends MobSprite {//all sprites client only
	private static final int FRAME_WIDTH	= 12;
	private static final int FRAME_HEIGHT	= 15;

	public MirrorSprite() {
		super();

		texture(Assets.WARRIOR);
		updateArmor( 0 );
		idle();
	}

	@Override
	public void link(@NotNull Char ch ) {
		super.link( ch );
        updateArmor(((MirrorImage) ch).tier, ((MirrorImage) ch).owner);
	}

    public void updateArmor(int tier, Hero hero) {
        texture(hero.heroClass.spritesheet());
    }

	public void updateArmor( int tier ) {

		idle = new Animation( 1, true );

		run = new Animation( 20, true );

		die = new Animation( 20, false );

		attack = new Animation( 15, false );

		idle();
	}
}
