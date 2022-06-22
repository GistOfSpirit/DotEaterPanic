package com.gistofspirit.games.doteaterpanic;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

//The "dots" that DotEater eats. "Pill" =  big dot
public class Dot extends Ellipse2D.Double {
	private static final long serialVersionUID = 2036685032521659429L;

	// Constants
	private static final double RADIUS_DOT = 5;
	private static final double RADIUS_PILL = 10;
	private static final Paint PAINT = Color.WHITE;

	// Coordinates
	private double mX;
	private double mY;
	// Size
	private double mRadius;
	// Status
	private DotStatus mStatus;

	// Initialise it as a simple dot
	public Dot(double x, double y) {
		super();
		mX = x;
		mY = y;
		setStatus(DotStatus.NORMAL);
	}

	// Change size depending on the status
	public void setStatus(DotStatus status) {
		mStatus = status;
		mRadius = Dot.getRadius(mStatus);
		setFrame(-mRadius, -mRadius, mRadius * 2, mRadius * 2);
	}

	// Return status
	public DotStatus getStatus() {
		return mStatus;
	}

	// Draw the dot
	public void draw(Graphics2D g) {
		AffineTransform at = g.getTransform();

		g.translate(mX, mY);
		g.setPaint(PAINT);
		g.fill(this);

		g.setTransform(at);
	}

	// Return dot's size and location
	public Circle getCircle() {
		return new Circle(mX, mY, mRadius);
	}

	// Static; correlate status with size
	public static double getRadius(DotStatus status) {
		switch (status) {
		case NORMAL:
			return RADIUS_DOT;
		case PILL:
			return RADIUS_PILL;
		default:
			return 0;
		}
	}
}
