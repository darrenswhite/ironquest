package com.darrenswhite.rs.ironquest.gui;

import com.darrenswhite.rs.ironquest.quest.Quest;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.awt.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Darren White
 */
public class QuestDetail extends Stage {

	/**
	 * The logger
	 */
	private static final Logger log =
			Logger.getLogger(QuestDetail.class.getName());

	/**
	 * The title of the window
	 */
	private static final String STAGE_NAME = "Quest Details";

	private static final String URL_RUNE_WIKI =
			"http://runescape.wikia.com/wiki/";

	/**
	 * The Quest to show detailed information
	 */
	private final Quest quest;

	/**
	 * The GridPane which contains all components
	 */
	private GridPane grid;

	/**
	 * ScrollPane which wraps the GridPane
	 */
	private ScrollPane scroll;

	/**
	 * The Scene for this Stage
	 */
	private Scene scene;

	/**
	 * The Quest name
	 */
	private Label lblName;

	/**
	 * Creates a new QuestDetail Stage
	 *
	 * @param owner The parent Window
	 * @param quest The Quest to show details of
	 */
	public QuestDetail(Window owner, Quest quest) {
		Objects.requireNonNull(owner);

		this.quest = Objects.requireNonNull(quest);

		// Use window modality so the main window cannot be used while
		// this window is open
		initModality(Modality.WINDOW_MODAL);
		// Set the owner as the main window
		initOwner(owner);
		// No need for the task details to be resizable
		setResizable(false);
		// Set the title of the window
		setTitle(STAGE_NAME);

		init();
	}

	private void init() {
		grid = new GridPane();
		// Set padding & gaps to 10px
		grid.setPadding(new Insets(10));
		grid.setHgap(10);
		grid.setVgap(10);

		// Wrap GridPane in ScrollPane
		scroll = new ScrollPane(grid);
		// Set the maximum size that the scrollpane should be
		scroll.setMaxSize(500, 500);
		// Don't display horizontal scrollbar as we don't need it
		scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

		// Create a new Scene containing the ScrollPane
		scene = new Scene(scroll);

		lblName = new Label(quest.getDisplayName());
		// Open RS wiki page on double click
		lblName.setOnMouseClicked(this::openQuestUrl);
		// Center the name label
		GridPane.setHalignment(lblName, HPos.CENTER);

		// Add components to the grid
		grid.add(lblName, 0, 0);

		// Set the scene
		sizeToScene();
		setScene(scene);
	}

	/**
	 * Opens the Quest page on RS wiki
	 *
	 * @param e The MouseEvent
	 */
	private void openQuestUrl(MouseEvent e) {
		// Check for double click and if we can open URLs
		if (e.getClickCount() != 2 || !Desktop.isDesktopSupported()) {
			return;
		}

		try {
			// Replace spaces with underscores
			String name = quest.getDisplayName().replace(' ', '_');
			// Create the URL
			String url = URL_RUNE_WIKI + name;

			// Open the URL
			Desktop.getDesktop().browse(new URI(url));
		} catch (UnsupportedEncodingException ex) {
			log.log(Level.SEVERE, "Unsupported encoding: ", ex);
		} catch (IOException | URISyntaxException ex) {
			log.log(Level.SEVERE, "Unable to open quest URL: ", ex);
		}
	}
}