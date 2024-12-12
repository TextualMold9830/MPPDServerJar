/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015  Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2016 Evan Debenham
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

package com.watabou.noosa.ui;

import com.watabou.noosa.Game;


public class Button extends Component {

	public static float longClick = 1f;
	

	protected boolean pressed;
	protected float pressTime;
	
	protected boolean processed;
	

	@Override
	public void update() {
		super.update();
		

		if (pressed) {
			if ((pressTime += Game.elapsed) >= longClick) {
				pressed = false;
				if (onLongClick()) {

					processed = true;
					onTouchUp();
					
				}
			}
		}
	}
	
	protected void onTouchDown() {};
	protected void onTouchUp() {};
	protected void onClick() {};
	
	protected boolean onLongClick() {
		return false;
	};
	
	@Override
	protected void layout() {
		
	}
}