package com.gistofspirit.games.doteaterpanic;

//This class is there simply to help with some calculations
public class Circle {
	// Coordinates
	private double mX;
	private double mY;
	// Radius
	private double mRad;

	// Simple constructor
	public Circle(double x, double y, double rad) {
		mX = x;
		mY = y;
		mRad = rad;
	}

	// The following functions return values
	public double getX() {
		return mX;
	}

	public double getY() {
		return mY;
	}

	public double getRad() {
		return mRad;
	}
}
