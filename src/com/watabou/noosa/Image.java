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

package com.watabou.noosa;

import java.nio.FloatBuffer;


public class Image extends Visual {


	public boolean flipHorizontal;
	public boolean flipVertical;

	protected float[] vertices;
	protected FloatBuffer verticesBuffer;

	protected boolean dirty;

	public Image() {
		super( 0, 0, 0, 0 );

		vertices = new float[16];
	}

	public Image( Image src ) {
		this();
		copy( src );
	}

	public Image( Object tx ) {
		this();
		texture( tx );
	}

	public Image( Object tx, int left, int top, int width, int height ) {
		this( tx );
	}

	public void texture( Object tx ) {
	}

	public void frame( Object frame ) {
		updateFrame();
		updateVertices();
	}

	public void frame( int left, int top, int width, int height ) {
	}

	public Object frame() {
		return new Object();
	}

	public void copy( Image other ) {

		width = other.width;
		height = other.height;

		updateFrame();
		updateVertices();
	}

	protected void updateFrame() {
		dirty = true;
	}

	protected void updateVertices() {

		vertices[0] 	= 0;
		vertices[1] 	= 0;

		vertices[4] 	= width;
		vertices[5] 	= 0;

		vertices[8] 	= width;
		vertices[9] 	= height;

		vertices[12]	= 0;
		vertices[13]	= height;

		dirty = true;
	}

	@Override
	public void draw() {

		super.draw();

		if (dirty) {
			verticesBuffer.position( 0 );
			verticesBuffer.put( vertices );
			dirty = false;
		}

	}
}
