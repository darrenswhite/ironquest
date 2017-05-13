package com.darrenswhite.rs.ironquest;

import com.darrenswhite.rs.ironquest.gui.MainPane;
import com.darrenswhite.rs.ironquest.log.LogFormatter;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Darren White
 */
public class MainFX extends Application {

	/**
	 * The title of the application window
	 */
	public static final String STAGE_NAME = "Project IronQuest by Ramus";

	/**
	 * The main window width
	 */
	private static final double APP_WIDTH = 800;

	/**
	 * The main window height
	 */
	private static final double APP_HEIGHT = 600;

	/**
	 * The logger
	 */
	private static final Logger log =
			Logger.getLogger(Main.class.getName());

	/**
	 * Get the resource at the path
	 *
	 * @param path The relative path for the resource
	 * @return The absolute URL for the resource
	 */
	public static URL getResource(String path) {
		// Try and find the resource locally (relative in the JAR file)
		URL in = MainFX.class.getResource('/' + path);

		// Not found so we get the resource relative to the working directory
		if (in == null) {
			try {
				in = Paths.get(path).toUri().toURL();
			} catch (MalformedURLException ex) {
				log.log(Level.SEVERE, "Unable to find resource: ", ex);
			}
		}

		return in;
	}

	/**
	 * Main application entry point
	 *
	 * @param args The program arguments
	 */
	public static void main(String[] args) {
		// Setup logging
		setupLogger(Level.INFO);
		// Launch the application
		launch(args);
	}

	/**
	 * Setup logging
	 */
	private static void setupLogger(Level level) {
		// Get the root logger
		Logger root = Logger.getLogger("");
		// Create a new ConsoleHandler
		ConsoleHandler ch = new ConsoleHandler();

		// Set the handler log level
		ch.setLevel(level);

		// Set the custom log formatter
		ch.setFormatter(new LogFormatter());

		// Remove all handlers from the root logger
		for (Handler handler : root.getHandlers()) {
			root.removeHandler(handler);
		}

		// Add our log handler to root
		root.addHandler(ch);
		// Set the root log level
		root.setLevel(level);
	}

	@Override
	public void start(Stage stage) throws Exception {
		// Initialize and show main app
		MainPane pane = new MainPane();

		// Close the pane and save settings
		stage.setOnCloseRequest(e -> {
			pane.close();
			IronQuest.getInstance().save();
		});
		// Load settings, initialize the pane, and
		// run the program
		stage.setOnShowing(e -> {
			IronQuest.getInstance().load();
			pane.init();
			pane.run();
		});

		// Create a new scene with the default width & height
		Scene scene = new Scene(pane, APP_WIDTH, APP_HEIGHT);

		// Set the primary stage scene and the default title
		stage.setScene(scene);
		stage.setTitle(STAGE_NAME);
		// Show main overview window
		stage.show();
	}
}