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

import com.watabou.noosa.Image;
import com.watabou.pixeldungeon.items.Item;

import java.awt.*;

public class IconTitle extends Component {

	private static final float FONT_SIZE = 9;

	private static final float GAP = 2;

	protected Image imIcon;

	public IconTitle() {
		super();
	}

	public IconTitle( Item item ) {

	}

	public IconTitle( Image icon, String label ) {
		super();

		icon( icon );
		label( label );
	}


	@Override
	public void layout() {

	}

	public void icon( Image icon ) {
	}

	public void label( String label ) {
	}

	public void label( String label, int color ) {
	}

	public void color( int color ) {
	}

	public void health( float value ) {
		layout();
	}
}
