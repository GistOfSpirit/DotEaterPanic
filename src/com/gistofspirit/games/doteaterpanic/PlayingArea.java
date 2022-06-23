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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

//The game's main class.
public class PlayingArea extends JPanel implements KeyListener,
		DotEater.DieListener, DotCollection.EatListener {
	private static final long serialVersionUID = -6927068956073636390L;

	// Messages to print to the user (in a terminal)
	public static final String EXIT_TEXT_QUIT = "You quit!";
	public static final String EXIT_TEXT_LOSE = "You lose!";
	// Playing area's size
	public static final int AREA_WIDTH = 800;
	public static final int AREA_HEIGHT = AREA_WIDTH;
	// Status bar's height
	public static final int BAR_HEIGHT = 30;
	// Background colour
	public static final Color COLOUR_BG = Color.BLACK;
	// Bar's colour
	private static final Paint PAINT_BAR = Color.WHITE;
	// How long DotEater remains fast, in nanoseconds
	private static final long DURATION_FAST = 5000000000l; // 5 seconds
	// How many columns and rows of dots
	private static final int DOT_COLUMNS = 8;
	private static final int DOT_ROWS = 8;
	// How many dots will be pills
	// MAX_PILLS <= DOT_COLUMNS * DOT_ROWS
	private static final int MAX_PILLS = 10;
	// Scoring system
	// Score you gain for eating a dot
	private static final int SCORE_DOT = 50;
	// Score you gain for eating a pill
	private static final int SCORE_PILL = 100;
	// Score you lose if you're hit
	private static final int SCORE_LOSE = 200;
	// Score you gain if you eat all dots
	private static final int SCORE_WIN = 150;
	// Lives you gain if you eat all dots
	private static final int LIVES_BONUS = 1;
	// Lives you begin the game with
	private static final int INIT_LIVES = 3;
	// How to draw on-screen messages
	// Font to use for the score
	private static final Font FONT_SCORE = new Font("Serif", Font.PLAIN, 24);
	// Font to use for the lives
	private static final Font FONT_LIVES = FONT_SCORE;
	// Font to use for the welcome message
	private static final Font FONT_WELCOME = FONT_SCORE;
	// How the score is displayed
	private static final String FORMAT_SCORE = "Score: %d";
	// How the lives are displayed
	private static final String FORMAT_LIVES = "Lives: %d";
	// The welcome message
	private static final String TEXT_WELCOME = "Press SPACE to start";
	// Font colour for score
	private static final Color COLOUR_SCORE = Color.BLACK;
	// Font colour for lives
	private static final Color COLOUR_LIVES = COLOUR_SCORE;
	// Font colour for the welcome message
	private static final Color COLOUR_WELCOME = Color.CYAN;
	// Where to draw the welcome message
	private static final int WELCOME_X = AREA_WIDTH / 2 - 100;
	private static final int WELCOME_Y = AREA_HEIGHT / 2 + 100;

	// Last time a pill was eaten
	private long mLastPillTime;

	// Whether the game loop is running
	private boolean mIsLoopRunning = false;

	// An instance of our hero
	private DotEater mDotEater;
	// An array of walls
	private Wall[] mWalls;
	// The collection of dots on the screen
	private DotCollection mDotCollection;
	// Current score
	private int mScore;
	// Current lives
	private int mLives;
	// Whether the loop should stop running
	private boolean mIsPaused;
	// Whether the game is controllable right now
	private boolean mIsGameOn;
	// Whether the welcome message has been dismissed
	private boolean mShownWelcome;
	// This ensures "game over" only happens once
	private boolean mGameOverRun;

	// Constructor
	public PlayingArea() {
		setPreferredSize(new Dimension(AREA_WIDTH, AREA_HEIGHT + BAR_HEIGHT));
		// Initially place DotEater at the centre
		mDotEater = new DotEater(AREA_WIDTH / 2, AREA_HEIGHT / 2, Direction.RIGHT);
		// Create two walls. One going left-to-right, the other going up-to-down
		mWalls = new Wall[2];
		mWalls[0] = new Wall(Direction.RIGHT, AREA_WIDTH, 0, AREA_HEIGHT);
		mWalls[1] = new Wall(Direction.DOWN, AREA_HEIGHT, 0, AREA_WIDTH);
		// Create the collection of dots
		mDotCollection = new DotCollection(AREA_WIDTH, AREA_HEIGHT,
				DOT_COLUMNS, DOT_ROWS, MAX_PILLS);

		// Initialise score and lives
		mScore = 0;
		mLives = INIT_LIVES;
		// Everything is stopped at first
		mIsPaused = true;
		mIsGameOn = false;
		// Show the welcome message
		mShownWelcome = false;
		mGameOverRun = false;

		setBackground(COLOUR_BG);

		// Set the listeners. For keys, DotEater and dots.
		addKeyListener(this);
		mDotEater.setDieListener(this);
		mDotCollection.setEatenListener(this);
	}

	// Start a new round (after DotEater dies)
	private void initRound() {
		mIsPaused = true;
		mIsGameOn = false;
		mDotEater.reset();
		for (Wall w : mWalls) {
			w.reset();
		}
		goSlow();
	}

	// Start a new game (after DotEater eats all dots)
	private void initGame() {
		mDotCollection.init();
		initRound();
	}

	// Get things going!
	private void startGame() {
		mShownWelcome = true; // Don't show the welcome text again
		mIsPaused = false; // Loop should run
		mIsGameOn = true; // DotEater can be controlled

		// Start a new loop if one is not running
		if (!mIsLoopRunning) {
			Thread loop = new Thread() {
				@Override
				public void run() {
					gameLoop();
				}
			};
			loop.start();
		}
	}

	// Turn controls off and play DotEater's dying animation
	private void stopGame() {
		mIsGameOn = false;
		mDotEater.startDying();
	}

	// Check if the game should stop
	private void update() {
		if (checkLoseCollisions()) {
			stopGame();
		}
	}

	// You lose if DotEater touches a wall or the playing area's edges
	private boolean checkLoseCollisions() {
		// Think of DotEater as a rectangle for this
		Rectangle2D pacArea = mDotEater.getBounds2D();
		if ((pacArea.getX() <= 0) || (pacArea.getY() <= 0)
				|| (pacArea.getMaxX() >= AREA_WIDTH)
				|| (pacArea.getMaxY() >= AREA_HEIGHT)) {
			// DotEater touches the playing area's edges
			return true;
		}
		for (Wall w : mWalls) {
			if (pacArea.intersects(w.getBounds2D())) {
				// DotEater touches a wall
				return true;
			}
		}
		return false;
	}

	// Draw everything
	@Override
	public void paint(Graphics g) {
		Graphics2D g2D = (Graphics2D) g;

		// Clear the screen
		g2D.setBackground(COLOUR_BG);
		g2D.clearRect(0, 0, AREA_WIDTH, AREA_HEIGHT + BAR_HEIGHT);

		// Draw the status bar
		g2D.setPaint(PAINT_BAR);
		g2D.fillRect(0, AREA_HEIGHT, AREA_WIDTH, BAR_HEIGHT);

		// Show score and lives
		updateScore(g2D);
		updateLives(g2D);

		// Show DotEater
		mDotEater.draw(g2D);

		// Show the walls
		for (Wall w : mWalls) {
			w.draw(g2D);
		}

		// Show the welcome message
		if (!mShownWelcome) {
			g2D.setFont(FONT_WELCOME);
			g2D.setPaint(COLOUR_WELCOME);
			g2D.drawString(TEXT_WELCOME, WELCOME_X, WELCOME_Y);
		}

		// Show the dots
		mDotCollection.draw(g2D);
	}

	// Draw score text
	private void updateScore(Graphics2D g) {
		g.setFont(FONT_SCORE);
		g.setPaint(COLOUR_SCORE);
		g.drawString(String.format(FORMAT_SCORE, mScore), 5, AREA_HEIGHT
				+ BAR_HEIGHT - 5);
	}

	// Draw lives text
	private void updateLives(Graphics2D g) {
		g.setFont(FONT_LIVES);
		g.setPaint(COLOUR_LIVES);
		g.drawString(String.format(FORMAT_LIVES, mLives), AREA_WIDTH / 2 + 5,
				AREA_HEIGHT + BAR_HEIGHT - 5);
	}

	// Handle key presses
	@Override
	public void keyPressed(KeyEvent e) {
		if (mIsGameOn) {
			// If DotEater is controllable, set his direction
			if (e.getKeyCode() == KeyEvent.VK_UP) {
				mDotEater.setDirection(Direction.UP);
			} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				mDotEater.setDirection(Direction.DOWN);
			} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				mDotEater.setDirection(Direction.RIGHT);
			} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				mDotEater.setDirection(Direction.LEFT);
			}
		}

		// Start the game
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			startGame();
		}

		// Exit the game
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			gameOver(EXIT_TEXT_QUIT);
		}
	}

	// Not handled
	@Override
	public void keyReleased(KeyEvent e) {
		// Do nothing
	}

	// Not handled
	@Override
	public void keyTyped(KeyEvent e) {
		// Do nothing
	}

	// What to do when DotEater dies
	@Override
	public void onDotEaterDie() {
		// Subtract from the score
		subtractScore(SCORE_LOSE);
		if (mLives > 0) {
			// You've got more lives; start a new round
			mLives--;
			initRound();
		} else {
			// No more lives. Game over.
			gameOver(EXIT_TEXT_LOSE);
		}
	}

	// DotEater has eaten a dot. Add score depending on its size.
	@Override
	public void onDotEaten(DotStatus status) {
		switch (status) {
			case NORMAL:
				mScore += SCORE_DOT;
				break;
			case PILL:
				mScore += SCORE_PILL;
				goFast();
				break;
			default:
				break;
		}
	}

	// You won! Start a new game.
	@Override
	public void onAllDotsEaten() {
		mScore += SCORE_WIN;
		mLives += LIVES_BONUS;
		initGame();
	}

	// Tell the dot collection where DotEater is
	@Override
	public Circle getDotEaterLocation() {
		return mDotEater.getCircle();
	}

	// Make DotEater and walls go fast
	private void goFast() {
		mDotEater.goFast();
		for (Wall w : mWalls) {
			w.goFast();
		}
		// Record when this began
		mLastPillTime = System.nanoTime();
	}

	// Make DotEater and walls go slow
	private void goSlow() {
		mDotEater.goSlow();
		for (Wall w : mWalls) {
			w.goSlow();
		}
	}

	// Exit the game
	private void gameOver(String reason) {
		// Ensure this only runs once
		if (!mGameOverRun) {
			mGameOverRun = true;
			System.out.println(reason);
			System.out.println(String.format("Score: %d", mScore));
			try {
				// Close the window
				MainWindow mainWindow = (MainWindow) this.getTopLevelAncestor();
				mainWindow.askToClose();
			} catch (ClassCastException ex) {
				// Do nothing, if the window is not the one I designed
				// (shouldn't
				// happen)
			}
		}
	}

	// Subtract from the score but don't allow negatives
	private void subtractScore(int scoreToSubtract) {
		mScore -= scoreToSubtract;
		if (mScore < 0) {
			mScore = 0;
		}
	}

	// The main game loop (runs on a separate thread)
	// Followed advice from http://www.java-gaming.org/index.php/topic,24220.0
	private void gameLoop() {
		mIsLoopRunning = true;

		// Initialise variable
		mLastPillTime = System.nanoTime();

		// When the loop ran last
		long lastLoopTime = System.nanoTime();
		final long OPTIMAL_TIME = 1000000000 / 60; // 60 frames per second

		// Stop the loop if mIsPaused == true
		while (!mIsPaused) {
			long now = System.nanoTime();
			long updateLength = now - lastLoopTime;
			lastLoopTime = now;
			// Ideally, the loop would run 60 times a second so this will be
			// equal to 1. If not, things will move more or less to keep the
			// animation smooth-looking.
			double delta = updateLength / (double) OPTIMAL_TIME;

			// If enough time has passed since the last pill was eaten, things
			// can slow down.
			if (now > mLastPillTime + DURATION_FAST) {
				goSlow();
			}

			// Move DotEater and the walls based on the delta
			mDotEater.move(delta);
			if (mIsGameOn) {
				for (Wall w : mWalls) {
					w.move(delta);
				}
			}

			// Check for collisions with dots
			mDotCollection.checkCollisions();
			// Check for losing condition
			this.update();

			// Draw everything again
			repaint();

			// Try to ensure that
			try {
				Thread.sleep(10 - (System.nanoTime() - lastLoopTime) / 1000000);
				// Thread.sleep(Math.abs(lastLoopTime - System.nanoTime()) /
				// 1000000 + 10);
			} catch (InterruptedException ex) {
				// Do nothing.
			} catch (IllegalArgumentException ex) {
				// Do nothing. This might happen if the calculation above
				// returns a negative number.
			}
		}

		// If we're here, the loop isn't running.
		mIsLoopRunning = false;
	}
}
