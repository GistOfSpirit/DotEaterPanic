/*
Dot Eater Panic
Copyright (C) 2022  Rea Tasopoulou

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package com.gistofspirit.games.doteaterpanic;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Random;

//The moving walls
public class Wall extends Rectangle2D.Double {
	private static final long serialVersionUID = -3213652628706058818L;

	// Constants
	// The wall's size
	private static final double LENGTH = 300;
	private static final double THICKNESS = 10;
	// The wall's colour
	private static final Paint PAINT = Color.WHITE;
	// The wall's speed when moving slowly
	private static final double SPEED_NORMAL = 6;
	// The wall's speed when moving fast
	private static final double SPEED_FAST = 20;

	// Random generator
	private Random mRandom;

	// The wall's moving direction. Only DOWN and RIGHT have been tested...
	private Direction mDirection;
	// The wall's size as a rectangle
	private double mWidth;
	private double mHeight;
	// Where the wall starts
	private double mMinDistance;
	// The farthest the wall can go
	private double mMaxDistance;
	// What two positions along the edge of the screen the wall might appear at
	private double mMinPoint;
	private double mMaxPoint;

	// How far the wall has currently travelled
	private double mDistance;
	// At which point the wall has appeared
	private double mPoint;
	// The wall's current speed
	private double mSpeed;

	// Constructor
	public Wall(Direction dir, double maxDistance, double minPoint, double maxPoint) {
		super();
		mDirection = dir;

		// Set the wall's size (as a rectangle) and movement limits depending on
		// its direction. Only RIGHT and DOWN have been tested; UP and LEFT
		// likely don't work correctly.
		switch (mDirection) {
			case RIGHT:
				mWidth = LENGTH;
				mHeight = THICKNESS;
				// Initially the wall is outside the screen
				mMinDistance = -LENGTH;
				mMaxDistance = maxDistance;
				break;
			case DOWN:
				mWidth = THICKNESS;
				mHeight = LENGTH;
				mMinDistance = -LENGTH;
				mMaxDistance = maxDistance;
				break;
			case LEFT:
				mWidth = LENGTH;
				mHeight = THICKNESS;
				mMinDistance = maxDistance;
				mMaxDistance = -LENGTH;
				break;
			case UP:
				mWidth = THICKNESS;
				mHeight = LENGTH;
				mMinDistance = maxDistance;
				mMaxDistance = -LENGTH;
				break;
			default:
				break;
		}

		setRect(0, 0, mWidth, mHeight);
		mMinPoint = minPoint;
		mMaxPoint = maxPoint - THICKNESS;

		mRandom = new Random();
		// Initially the wall goes slowly
		goSlow();

		// Initialise the wall
		reset();
	}

	// Arc2D's getBounds2D function would show the wall at (0,0), but here we
	// want to know the location where it appears to be disregarding the current
	// AffineTransformation
	@Override
	public Rectangle2D getBounds2D() {
		switch (mDirection) {
			case RIGHT:
				return new Rectangle2D.Double(mDistance, mPoint, LENGTH, THICKNESS);
			case DOWN:
				return new Rectangle2D.Double(mPoint, mDistance, THICKNESS, LENGTH);
			case LEFT:
				return new Rectangle2D.Double(mMaxDistance - mDistance, mPoint, LENGTH, THICKNESS);
			case UP:
				return new Rectangle2D.Double(mMaxPoint - mPoint, mMaxDistance
						- mDistance, THICKNESS, LENGTH);
			default:
				break;
		}
		return new Rectangle2D.Double(mPoint, mDistance, mWidth, mHeight);
	}

	// Draw the wall at the position we want it
	public void draw(Graphics2D g) {
		AffineTransform at = g.getTransform();

		// Depending on whether the wall is horizontal or vertical, its distance
		// from the starting position and the point it has appeared at are
		// reversed as coordinates.
		switch (mDirection) {
			case RIGHT:
			case LEFT:
				g.translate(mDistance, mPoint);
				break;
			case DOWN:
			case UP:
				g.translate(mPoint, mDistance);
				break;
			default:
				break;
		}
		g.setPaint(PAINT);
		g.fill(this);

		g.setTransform(at);
	}

	// Initialise the wall
	public void reset() {
		// Choose a random point for it to appear, but ensure it's within the
		// playing area
		mPoint = Utils.getRandomBetween(mRandom, (int) mMinPoint, (int) mMaxPoint);
		// Move it back to the beginning
		mDistance = mMinDistance;
	}

	// Make the wall go fast
	public void goFast() {
		mSpeed = SPEED_FAST;
	}

	// Make the wall go slow
	public void goSlow() {
		mSpeed = SPEED_NORMAL;
	}

	// Move the wall. Ideally delta==1.
	public void move(double delta) {
		switch (mDirection) {
			case RIGHT:
			case DOWN:
				// In these two cases, the wall goes away from 0.
				if (mDistance < mMaxDistance) {
					mDistance += mSpeed * delta;
				} else {
					reset();
				}
				return;
			case LEFT:
			case UP:
				// In these two cases, the wall goes toward 0. Not tested.
				if (mDistance > mMaxDistance) {
					mDistance -= mSpeed * delta;
				} else {
					reset();
				}
				return;
			default:
				return;
		}
	}
}
