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
