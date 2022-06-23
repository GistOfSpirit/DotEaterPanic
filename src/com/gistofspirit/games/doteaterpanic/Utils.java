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

import java.util.Random;

//Simply some useful functions
public class Utils {
	// Radians corresponding to each direction
	private static final double DIR_RIGHT = 0.0;
	private static final double DIR_DOWN = Math.PI / 2;
	private static final double DIR_LEFT = Math.PI;
	private static final double DIR_UP = 3 * Math.PI / 2;

	// Return the radians corresponding to the given direction
	public static double getDirectionTheta(Direction dir) {
		switch (dir) {
		case RIGHT:
			return DIR_RIGHT;
		case DOWN:
			return DIR_DOWN;
		case LEFT:
			return DIR_LEFT;
		case UP:
			return DIR_UP;
		default:
			return DIR_RIGHT;
		}
	}

	// With the given generator, generate a number between min and max
	public static int getRandomBetween(Random rand, int min, int max) {
		return rand.nextInt(max - min + 1) + min;
	}

	// Calculate the distance between the edges of two circles
	public static double getDistanceBetweenCircles(Circle c1, Circle c2) {
		double centreDistance = getDistanceBetweenCircleCentres(c1, c2);
		return centreDistance - (c1.getRad() + c2.getRad());
	}

	// Calculate the distance between the centres of two circles
	public static double getDistanceBetweenCircleCentres(Circle c1, Circle c2) {
		double hor = Math.abs(c1.getX() - c2.getX());
		double ver = Math.abs(c1.getY() - c2.getY());
		return Math.sqrt(Math.pow(hor, 2) + Math.pow(ver, 2));
	}
}
