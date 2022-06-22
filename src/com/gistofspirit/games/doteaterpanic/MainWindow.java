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
