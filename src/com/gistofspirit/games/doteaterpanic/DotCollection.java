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

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.Random;

//All the dots on the screen
public class DotCollection {
	// Area to scatter the dots in
	private int mAreaWidth;
	private int mAreaHeight;
	// Dots will be placed in a grid with this many columns and rows
	private int mDotColumns;
	private int mDotRows;
	// How many dots will be pills
	private int mMaxPills;

	// The array of dots
	private Dot[] mDots;
	// How many dots are still there (to avoid recalculating it often)
	private int mDotsRemaining;

	// Very simple listener implementation, to let the game know when a dot has
	// been eaten and allow this class to know where DotEater is.
	public abstract interface EatListener {
		abstract void onDotEaten(DotStatus status);

		abstract void onAllDotsEaten();

		abstract Circle getDotEaterLocation();
	}

	// Who listens
	private EatListener eatListener;

	// Assign a listener
	public void setEatenListener(EatListener listener) {
		eatListener = listener;
	}

	// Simple constructor which calls the initialiser
	public DotCollection(int width, int height, int columns, int rows, int maxPills) {
		mAreaWidth = width;
		mAreaHeight = height;
		mDotColumns = columns;
		mDotRows = rows;
		mMaxPills = maxPills;

		init();
	}

	public void init() {
		// Create dots in random positions, away from each other
		Random dotsRandom = new Random();

		// Size of each grid cell
		int cellWidth = mAreaWidth / mDotColumns;
		int cellHeight = mAreaHeight / mDotRows;

		// Total number of dots
		mDotsRemaining = mDotColumns * mDotRows;

		// The maximum radius a dot could have
		double radius = Dot.getRadius(DotStatus.PILL);

		// Initialise array
		mDots = new Dot[mDotsRemaining];

		for (int c = 0; c < mDotColumns; c++) {
			for (int r = 0; r < mDotRows; r++) {
				// Each cell has one dot; randomise its position within the cell
				// but keep it away from the edges
				double x =
					Utils.getRandomBetween(dotsRandom, (int) (c * cellWidth + radius), (int) ((c + 1) * cellWidth - radius));
				double y =
					Utils.getRandomBetween(dotsRandom, (int) (r * cellHeight + radius), (int) ((r + 1) * cellHeight - radius));
				mDots[c * mDotColumns + r] = new Dot(x, y);
			}
		}

		// Select random dots and make them pills
		for (int i = 1; i <= mMaxPills; i++) {
			boolean isOK;
			int x;
			do {
				isOK = true;
				x = Utils.getRandomBetween(dotsRandom, 0, mDots.length - 1);
				isOK = mDots[x].getStatus() != DotStatus.PILL;
				// Make sure the randomly chosen dot is not already a pill
			} while (!isOK);
			mDots[x].setStatus(DotStatus.PILL);
		}
	}

	// Draw all dots
	public void draw(Graphics2D g) {
		AffineTransform at = g.getTransform();

		for (Dot d : mDots) {
			d.draw(g);
		}

		g.setTransform(at);
	}

	// Check if DotEater is touching any dots and tell the listener what kind of
	// dot it was
	public void checkCollisions() {
		if (eatListener != null) {
			Circle dotEater = eatListener.getDotEaterLocation();
			for (Dot d : mDots) {
				// Check DotEater's distance from each dot
				if (Utils.getDistanceBetweenCircles(dotEater, d.getCircle()) <= 0) {
					DotStatus oldStatus = d.getStatus();
					if (oldStatus != DotStatus.EATEN) {
						// We don't care about already eaten dots
						d.setStatus(DotStatus.EATEN);
						mDotsRemaining--;
						eatListener.onDotEaten(oldStatus);
						if (mDotsRemaining <= 0) {
							// All dots eaten now
							eatListener.onAllDotsEaten();
						}
					}
				}
			}
		}
	}
}
