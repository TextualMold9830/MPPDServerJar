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


import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.utils.Callback;

public class HeroSprite extends CharSprite {

	private static final int FRAME_WIDTH	= 12;
	private static final int FRAME_HEIGHT	= 15;

	private static final int RUN_FRAMERATE	= 20;


	private Animation fly;
	private Animation read;

	public HeroSprite(Hero hero) {
		super();

		link( hero );

		texture( hero.heroClass.spritesheet() );
		updateArmor();

		idle();
	}

	public void updateArmor() {
		idle = new Animation( 1, true );
	}

	@Override
	public void place( int p ) {
		super.place( p );
	}

	@Override
	public void move( int from, int to ) {
		super.move( from, to );
		if (ch.flying) {
			play( fly );
		}
	}

	@Override
	public void jump( int from, int to, Callback callback ) {
		super.jump( from, to, callback );
		play( fly );
	}

	public void read() {
		super.read();
		animCallback = new Callback() {
			@Override
			public void call() {
				idle();
				ch.onOperateComplete();
			}
		};
		play( read );
	}

	@Override
	public void update() {
		sleeping = ((Hero)ch).restoreHealth;

		super.update();
	}

	public boolean sprint( boolean on ) {
		run.delay = on ? 0.625f / RUN_FRAMERATE : 1f / RUN_FRAMERATE;
		return on;
	}

	//returns texture split on horizontal lines. One line - one tier
}
