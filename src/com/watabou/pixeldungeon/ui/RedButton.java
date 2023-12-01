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

import com.watabou.noosa.Image;
import com.nikita22007.multiplayer.noosa.audio.Sample;
import com.watabou.pixeldungeon.Assets;

import java.awt.*;

// red buton with white  text what  used in windows
public class RedButton extends Button {

	protected Image icon;

	public RedButton( String label ) {
		super();

	}

	protected void createChildren() {
	}

	@Override
	public void layout() {

		super.layout();


	}

	protected void onTouchDown() {
		Sample.INSTANCE.play( Assets.SND_CLICK );
	};

	protected void onTouchUp() {
	};

	public void enable( boolean value ) {
	}

	public void text( String value ) {
		layout();
	}

	public void icon( Image icon ) {
		if (this.icon != null) {
		}
		this.icon = icon;
		if (this.icon != null) {
			layout();
		}
	}

}
