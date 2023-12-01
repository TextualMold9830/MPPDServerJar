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
package com.watabou.pixeldungeon.ui;

public class CheckBox extends RedButton {

	private boolean checked = false;

	public CheckBox( String label ) {
		super( label );

		icon( com.watabou.pixeldungeon.ui.Icons.get( com.watabou.pixeldungeon.ui.Icons.UNCHECKED ) );
	}

	@Override
    public void layout() {
		super.layout();
	}

	public boolean checked() {
		return checked;
	}

	public void checked( boolean value ) {
		if (checked != value) {
			checked = value;
			icon.copy( com.watabou.pixeldungeon.ui.Icons.get( checked ? com.watabou.pixeldungeon.ui.Icons.CHECKED : Icons.UNCHECKED ) );
		}
	}

	protected void onClick() {
		checked( !checked );
	}
}
