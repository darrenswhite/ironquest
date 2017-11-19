package com.darrenswhite.rs.ironquest;

import com.darrenswhite.rs.ironquest.action.Action;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Darren White
 */
public class Main {

	/**
	 * The logger
	 */
	private static final Logger log = Logger.getLogger(Main.class.getName());

	/**
	 * Main entry point
	 *
	 * @param args The program arguments
	 */
	public static void main(String[] args) {
		// Setup logging
		MainFX.setupLogger(Level.INFO);

		// Initialize JavaFX toolkit
		new JFXPanel();

		// Create a new instance
		IronQuest quest = IronQuest.getInstance();

		// First arg is player name
		if (args.length == 1) {
			String name = args[0];

			log.fine("Setting player name as: " + name);

			quest.setPlayer(name);
		}

		// Run on FX thread
		Platform.runLater(() -> {
			quest.run();

			// Print the actions
			for (Action a : quest.getActions()) {
				log.info(a.getMessage());
			}
		});

		// Terminate JavaFX
		Platform.exit();
	}
}