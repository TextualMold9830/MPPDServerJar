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


import com.nikita22007.multiplayer.noosa.audio.Sample;
import com.watabou.pixeldungeon.Assets;
import com.watabou.pixeldungeon.actors.Char;
import com.watabou.pixeldungeon.effects.Halo;
import com.watabou.pixeldungeon.effects.particles.ElmoParticle;
import org.jetbrains.annotations.NotNull;

public class WandmakerSprite extends MobSprite {
	
	private Shield shield;
	
	public WandmakerSprite() {
		super();
		
		texture( Assets.MAKER );
		idle = new Animation( 10, true );
		run = new Animation( 20, true );
		die = new Animation( 20, false );
		play( idle );
	}
	
	@Override
	public void link(@NotNull Char ch ) {
		super.link( ch );
		/*
		if (shield == null) {
			parent.add( shield = new Shield() );
		}
		*/
	}
	
	@Override
	public void die() {
		super.die();
		
		if (shield != null) {
			shield.putOut();
		}
		emitter().start( ElmoParticle.FACTORY, 0.03f, 60 );
		
		if (visible) {
			Sample.INSTANCE.play( Assets.SND_BURNING );
		}
	}
	
	public class Shield extends Halo {
		public Shield() {
			
			super(WandmakerSprite.this, 14, 0xBBAACC, 1f );
		}
	}

}
