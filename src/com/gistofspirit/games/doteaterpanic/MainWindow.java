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

import java.awt.Toolkit;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

//The game's window
public class MainWindow extends JFrame {
	private static final long serialVersionUID = 376648160501715758L;

	// Main function
	public static void main(String[] args)
	{
		// Simply show the window
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				MainWindow mw = new MainWindow();
				mw.setVisible(true);
			}
		});
	}

	private static final String WIN_TITLE = "Dot Eater Panic";

	// Constructor
	public MainWindow() {
		setTitle(WIN_TITLE);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		// Add the playing area and give it focus
		PlayingArea pa = new PlayingArea();
		pa.setFocusable(true);
		add(pa);

		// We want the size to be determined by the playing area.
		// "pack" sizes the window based on it.
		pack();
		// Center the window on the screen
		setLocationRelativeTo(null);
	}

	// Tell the window to close
	public void askToClose() {
		WindowEvent ev = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
		Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(ev);
	}
}
