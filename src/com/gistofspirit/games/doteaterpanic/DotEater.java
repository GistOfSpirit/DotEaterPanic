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
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;

//Our hero
public class DotEater {
	// Constants
	// Degrees his feet move at most
	private static final double FEET_ANGLE = Math.PI / 2;
	// How big DotEater is
	private static final double BODY_RADIUS = 20.0;
	// Foot width
	private static final double FOOT_WIDTH = 12.0;
	// Foot height
	private static final double FOOT_HEIGHT = 6.0;
	// Eye size
	private static final double EYE_RADIUS = 3.0;
	// His normal colour
	private static final Paint PAINT_NORMAL = Color.CYAN;
	// His colour when fast
	private static final Paint PAINT_FAST = Color.RED;
	// His eye colour
	private static final Paint PAINT_EYE = Color.BLUE;
	// His normal speed
	private static final double SPEED_NORMAL = 2;
	// His speed when fast
	private static final double SPEED_FAST = 6;
	// How quickly he dies
	private static final double SPEED_DIE = Math.PI / 12;
	// Times to turn when dying
	private static final int TURNS_DIE = 2;

	// Initial coordinates
	private double mInitX;
	private double mInitY;
	// Initial direction
	private Direction mInitDirection;
	// Current coordinates
	private double mX;
	private double mY;
	// Current direction theta
	private Direction mDirection;
	// Current body colour
	private Paint mBodyPaint;
	// Current speed
	private double mSpeed;
	// Current feet degrees
	private double mFeetAngle;
	// Whether his feet are moving back or forth
	private int mFeetMoveDirection;
	// Whether he's in the process of dying
	private boolean mIsDying = false;
	// Initial direction theta when dying
	private double mDieDirInit = 0.0;
	// Current direction when dying
	private double mDieDirNow = 0.0;

	// Parts that make him
	private Arc2D.Double mBody;
	private Arc2D.Double mEyeLeft;
	private Arc2D.Double mEyeRight;
	private Rectangle2D.Double mFootFrontLeft;
	private Rectangle2D.Double mFootFrontRight;
	private Rectangle2D.Double mFootBackLeft;
	private Rectangle2D.Double mFootBackRight;

	// Very simple listener implementation to tell the game when DotEater is dead
	public abstract interface DieListener {
		abstract void onDotEaterDie();
	}

	// Who listens
	private DieListener dieListener;

	// Assign a listener

	public void setDieListener(DieListener listener) {
		dieListener = listener;
	}

	// Constructor
	public DotEater(double initX, double initY, Direction initDirection) {
		// We want him to be drawn at (0,0) so the rectangle containing him
		// must be centred there.
		// The initial "start" and "extent" parameters don't matter
		mBody = new Arc2D.Double(-BODY_RADIUS, -BODY_RADIUS,
				BODY_RADIUS * 2, BODY_RADIUS * 2, 0, 360, Arc2D.PIE);

		double eyeFromFront = BODY_RADIUS - (EYE_RADIUS * 2);
		mEyeLeft = new Arc2D.Double(eyeFromFront, -(EYE_RADIUS * 3),
				EYE_RADIUS * 2, EYE_RADIUS * 2, 0, 360, Arc2D.PIE);
		mEyeRight = new Arc2D.Double(eyeFromFront, EYE_RADIUS,
				EYE_RADIUS * 2, EYE_RADIUS * 2, 0, 360, Arc2D.PIE);

		double footX = BODY_RADIUS * Math.cos(-45);
		double footY = BODY_RADIUS * Math.sin(-45);

		mFootFrontLeft = new Rectangle2D.Double(
				footX, footY,
				FOOT_WIDTH, FOOT_HEIGHT);
		mFootFrontRight = new Rectangle2D.Double(
				footX, -footY - FOOT_HEIGHT,
				FOOT_WIDTH, FOOT_HEIGHT);
		mFootBackLeft = new Rectangle2D.Double(
				-footX, footY,
				FOOT_WIDTH, FOOT_HEIGHT);
		mFootBackRight = new Rectangle2D.Double(
				-footX, -footY - FOOT_HEIGHT,
				FOOT_WIDTH, FOOT_HEIGHT);

		// Set initial coordinates and direction
		mInitX = initX;
		mInitY = initY;
		mInitDirection = initDirection;

		reset();
	}

	// Initialise DotEater
	public void reset() {
		// Set current coordinates and direction to the initial ones
		mX = mInitX;
		mY = mInitY;
		mDirection = mInitDirection;
		// Set feet to initial pos
		mFeetAngle = 0;
		// When the feet moves they'll be moving back
		mFeetMoveDirection = -1;
		// Set initial speed to slow
		goSlow();
	}

	public void drawFoot(Graphics2D g,
			Rectangle2D.Double foot, double theta) {
		AffineTransform at = g.getTransform();

		g.rotate(theta, foot.getX(), foot.getCenterY());
		g.fill(foot);

		g.setTransform(at);
	}

	// Draw DotEater after rotating him based on his direction
	public void draw(Graphics2D g) {
		AffineTransform at = g.getTransform();

		g.translate(mX, mY);

		if (mDirection != Direction.CUSTOM) {
			g.rotate(Utils.getDirectionTheta(mDirection));
		} else {
			g.rotate(mDieDirNow);
		}

		g.setPaint(mBodyPaint);
		g.fill(mBody);

		drawFoot(g, mFootFrontLeft, -mFeetAngle);
		drawFoot(g, mFootFrontRight, mFeetAngle);
		drawFoot(g, mFootBackLeft, -((Math.PI / 2) + mFeetAngle));
		drawFoot(g, mFootBackRight, (Math.PI / 2) + mFeetAngle);

		g.setPaint(PAINT_EYE);
		g.fill(mEyeLeft);
		g.fill(mEyeRight);

		g.setTransform(at);
	}

	// Start the dying animation
	public void startDying() {
		if (!mIsDying) {
			// Negate pills' effects
			goSlow();
			mIsDying = true;
			mDieDirInit = Utils.getDirectionTheta(mDirection);
			mDieDirNow = mDieDirInit;
			mDirection = Direction.CUSTOM;
		}
	}

	// Make DotEater face a certain direction
	public void setDirection(Direction dir) {
		mDirection = dir;
	}

	// Arc2D's getBounds2D function would show him centred at (0,0), but here
	// we want to know the location where he appears to be disregarding the
	// current AffineTransformation
	public Rectangle2D getBounds2D() {
		return new Rectangle2D.Double(mX - BODY_RADIUS, mY - BODY_RADIUS,
				BODY_RADIUS * 2, BODY_RADIUS * 2);
	}

	// Return DotEater's size and location

	public Circle getCircle() {
		return new Circle(mX, mY, BODY_RADIUS);
	}

	// Set speed and colour to the ones for when he's going fast.

	public void goFast() {
		mSpeed = SPEED_FAST;
		mBodyPaint = PAINT_FAST;
	}

	// Set speed and colour to the ones for when he's going slow.

	public void goSlow() {
		mSpeed = SPEED_NORMAL;
		mBodyPaint = PAINT_NORMAL;
	}

	// Move DotEater. Ideally delta==1
	public void move(double delta) {
		if (!mIsDying) {
			// DotEater is alive and can move around

			// See if his feet needs to move forward or back
			if (mFeetAngle >= FEET_ANGLE) {
				mFeetMoveDirection = -1;// Feet forward
			} else if (mFeetAngle <= 0) {
				mFeetMoveDirection = 1;// Feet back
			}
			mFeetAngle += mFeetMoveDirection * delta / 6;

			double distance = mSpeed * delta; // How far DotEater will move
			// Change his coordinates depending on where he's facing
			switch (mDirection) {
				case RIGHT:
					mX += distance;
					break;
				case DOWN:
					mY += distance;
					break;
				case LEFT:
					mX -= distance;
					break;
				case UP:
					mY -= distance;
					break;
				default:
					break;
			}
		} else {
			// DotEater is dying. His only movement is to turn around twice
			if (mDieDirNow <= mDieDirInit + (Math.PI * 2 * TURNS_DIE)) {
				mDieDirNow += SPEED_DIE * delta;
			} else {
				// DotEater has finished dying; tell the listener.
				mIsDying = false;
				if (dieListener != null) {
					dieListener.onDotEaterDie();
				}
			}
		}
	}
}
