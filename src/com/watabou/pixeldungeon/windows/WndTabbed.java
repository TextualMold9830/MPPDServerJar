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
import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.ui.Window;

import java.awt.*;
import java.util.ArrayList;

public class WndTabbed extends Window {

	protected ArrayList<Tab> tabs = new ArrayList<Tab>();
	protected Tab selected;

	public WndTabbed(Hero hero) {
		super(hero);
	}

	protected Tab add( Tab tab ) {


		tabs.add( tab );

		return tab;
	}

	public void select( int index ) {
		select( tabs.get( index ) );
	}

	public void select( Tab tab ) {
		if (tab != selected) {
			for (Tab t : tabs) {
				if (t == selected) {
					t.select( false );
				} else if (t == tab) {
					t.select( true );
				}
			}

			selected = tab;
		}
	}

	protected int tabHeight() {
		return 25;
	}

	protected void onClick( Tab tab ) {
		select( tab );
	}

	protected class Tab extends Button {

		protected final int CUT = 5;

		protected boolean selected;

		protected void select( boolean value ) {

			active = !(selected = value);


			layout();
		}

		protected void onClick() {
			Sample.INSTANCE.play( Assets.SND_CLICK, 0.7f, 0.7f, 1.2f );
			WndTabbed.this.onClick( this );
		}
	}

	protected class LabeledTab extends Tab {


		public LabeledTab( String label ) {

			super();

		}

		protected void createChildren() {
		}

		@Override
		public void layout() {
			super.layout();

			if (!selected) {
			}
		}

		@Override
		protected void select( boolean value ) {
			super.select( value );
		}
	}

}
