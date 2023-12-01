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



public class NinePatch extends Visual {


	protected float[] vertices;
	protected FloatBuffer verticesBuffer;


	protected int marginLeft;
	protected int marginRight;
	protected int marginTop;
	protected int marginBottom;

	protected float nWidth;
	protected float nHeight;

	protected boolean flipHorizontal;
	protected boolean flipVertical;

	public NinePatch( Object tx, int margin ) {
		this( tx, margin, margin, margin, margin );
	}

	public NinePatch( Object tx, int left, int top, int right, int bottom ) {
		this( tx, 0, 0, 0, 0, left, top, right, bottom );
	}

	public NinePatch( Object tx, int x, int y, int w, int h, int margin ) {
		this( tx, x, y, w, h, margin, margin, margin, margin );
	}

	public NinePatch( Object tx, int x, int y, int w, int h, int left, int top, int right, int bottom ) {
		super( 0, 0, 0, 0 );


		nWidth = width = w;
		nHeight = height = h;

		vertices = new float[16];

		marginLeft	= left;
		marginRight	= right;
		marginTop	= top;
		marginBottom= bottom;


		updateVertices();
	}

	protected void updateVertices() {

		verticesBuffer.position( 0 );

		float right = width - marginRight;
		float bottom = height - marginBottom;
		verticesBuffer.put( vertices );
	}

	public int marginLeft() {
		return marginLeft;
	}

	public int marginRight() {
		return marginRight;
	}

	public int marginTop() {
		return marginTop;
	}

	public int marginBottom() {
		return marginBottom;
	}

	public int marginHor() {
		return marginLeft + marginRight;
	}

	public int marginVer() {
		return marginTop + marginBottom;
	}

	public float innerWidth() {
		return width - marginLeft - marginRight;
	}

	public float innerHeight() {
		return height - marginTop - marginBottom;
	}

	public float innerRight() {
		return width - marginRight;
	}

	public float innerBottom() {
		return height - marginBottom;
	}

	public void flipHorizontal(boolean value) {
		flipHorizontal = value;
		updateVertices();
	}

	public void flipVertical(boolean value) {
		flipVertical = value;
		updateVertices();
	}

	public void size( float width, float height ) {
		this.width = width;
		this.height = height;
		updateVertices();
	}

	@Override
	public void draw() {

		super.draw();


	}
}
