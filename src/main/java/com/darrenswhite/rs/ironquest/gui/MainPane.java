package com.darrenswhite.rs.ironquest.gui;

import com.darrenswhite.rs.ironquest.IronQuest;
import com.darrenswhite.rs.ironquest.action.Action;
import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.Skill;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Darren White
 */
public class MainPane extends GridPane {

	/**
	 * Executor for running IronQuest
	 */
	private final ExecutorService executor = Executors.newCachedThreadPool();

	/**
	 * Creates a new MainPane
	 */
	public MainPane() {
		init();
	}

	/**
	 * Notify to close the MainPane
	 */
	public void close() {
		// Shutdown the executor as the primary stage is closing
		executor.shutdownNow();
	}

	/**
	 * Initializes this pane and its components
	 */
	private void init() {
		// Create a new instance
		IronQuest quest = IronQuest.getInstance();

		// Set the id for css
		setId("overview");

		// Set padding & gaps to 10px
		setPadding(new Insets(10));
		setHgap(10);
		setVgap(10);

		// List to hold current action information
		ObservableList<String> info = FXCollections.observableArrayList();

		TextField textRSN = new TextField();

		// Don't focus username
		textRSN.setFocusTraversable(false);
		// Add prompt text
		textRSN.setPromptText("Username");

		// ListView for all Actions
		ListView<Action> listActions = new ListView<>();

		// Change the current action information on selection
		listActions.getSelectionModel().selectedItemProperty().addListener(
				(observable, oldValue, newValue) -> {
					if (newValue == null) {
						return;
					}

					// Clear old information
					info.clear();

					// Get the Player for this Action
					Player p = newValue.getPlayer();

					// Combat level
					info.add("Combat level: " + p.getCombatLevel());

					// Total quest points
					info.add("Quest points: " + p.getQuestPoints());

					// Total skill level
					info.add("Total level: " + p.getSkillLevels().values()
							.stream().mapToInt(Integer::intValue).sum());

					// Skill levels/xp
					p.getSkillXPs().forEach((s, xp) ->
							info.add(s + ": " + s.getLevelAt(xp) +
									" (" + Skill.formatXP(xp) + " xp)"));
				});
		// Only one Action can be selected at a time
		listActions.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		// Bind the actions to the ListView
		listActions.itemsProperty().bind(Bindings.createObjectBinding(
				quest::getActions, quest.getActions()));

		// ListView for current Action information
		ListView<String> listInfo = new ListView<>();

		// Bind the information for the current Action to the ListView
		listInfo.itemsProperty().bind(Bindings.createObjectBinding(
				() -> info, info));

		// The run button
		Button btnRun = new Button("Run");

		// Don't focus run button
		btnRun.setFocusTraversable(false);
		// Run when the button is pressed
		btnRun.setOnAction(e -> {
			// Disable the button so it can only run one at a time
			btnRun.setDisable(true);

			// Run in the background
			executor.submit(() -> {
				try {
					// Set player name
					quest.setPlayer(textRSN.getText());
					// Run the algorithm
					quest.run();

					Platform.runLater(() -> {
						// Focus the action list and select the first action
						listActions.requestFocus();
						listActions.getSelectionModel().select(0);
					});
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				// Enable the button again
				Platform.runLater(() -> btnRun.setDisable(false));
			});
		});
		// Set as default button
		btnRun.setDefaultButton(true);
		// Always fill width/height
		btnRun.setMaxHeight(Double.MAX_VALUE);
		btnRun.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(btnRun, Priority.ALWAYS);

		// Add nodes to grid
		add(textRSN, 0, 0, 2, 1);
		add(listActions, 0, 1);
		add(listInfo, 1, 1);
		add(btnRun, 0, 2, 2, 1);

		// Resize columns
		ColumnConstraints cc0 = new ColumnConstraints();
		ColumnConstraints cc1 = new ColumnConstraints();

		cc0.setPercentWidth(50);
		cc1.setPercentWidth(50);

		getColumnConstraints().addAll(cc0, cc1);

		// Resize rows
		RowConstraints rc0 = new RowConstraints();
		RowConstraints rc1 = new RowConstraints();
		RowConstraints rc2 = new RowConstraints();

		// Middle row to fill height
		rc1.setFillHeight(true);
		rc1.setVgrow(Priority.ALWAYS);

		getRowConstraints().addAll(rc0, rc1, rc2);

		// Start focus on action list
		Platform.runLater(listActions::requestFocus);
	}
}