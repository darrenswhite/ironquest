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
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.LinkedHashSet;
import java.util.Set;
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

		// Username text field
		TextField textRSN = new TextField();

		// Add tooltip info
		textRSN.setTooltip(new Tooltip("Enter your RuneScape name to " +
				"retrieve quest and skill information."));
		// Add prompt text
		textRSN.setPromptText("Username");

		// Skills to choose from to override lamp choices
		ObservableList<ComboSkillWrapper> lampSkills =
				FXCollections.observableArrayList();

		// Add a "None" option
		lampSkills.add(new ComboSkillWrapper(null));

		// Add all Skills
		for (Skill s : Skill.values()) {
			lampSkills.add(new ComboSkillWrapper(s));
		}

		// Force lamp skill - first choice
		ComboBox<ComboSkillWrapper> lampSkill1 = new ComboBox<>(lampSkills);
		// Force lamp skill - second choice
		ComboBox<ComboSkillWrapper> lampSkill2 = new ComboBox<>(lampSkills);

		// Second choice is only enabled if first choice is used
		lampSkill1.getSelectionModel().selectedItemProperty().addListener(
				(observable, oldValue, newValue) ->
						lampSkill2.setDisable(newValue == null ||
								newValue.getSkill() == null));
		// Add tooltip info
		lampSkill1.setTooltip(new Tooltip("Force skill lamps to be " +
				"used on a chosen skill if possible."));
		// Always fill width
		lampSkill1.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(lampSkill1, Priority.ALWAYS);

		// Disable second choice by default
		lampSkill2.setDisable(true);
		// Add tooltip info
		lampSkill2.setTooltip(new Tooltip("Force skill lamps to be " +
				"used on a chosen skill if possible. Fallback choice if " +
				"the first cannot be used."));
		// Always fill width
		lampSkill2.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(lampSkill2, Priority.ALWAYS);

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
		// Add tooltip info
		listActions.setTooltip(new Tooltip("The recommended order to " +
				"complete quests. Shows training information and skill " +
				"lamp choices."));

		// ListView for current Action information
		ListView<String> listInfo = new ListView<>();

		// Bind the information for the current Action to the ListView
		listInfo.itemsProperty().bind(Bindings.createObjectBinding(
				() -> info, info));
		// Add tooltip info
		listInfo.setTooltip(new Tooltip("Shows player information " +
				"after completing the selected quest."));

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
					// Get lamp skill first choice
					ComboSkillWrapper skill1 =
							lampSkill1.getSelectionModel().getSelectedItem();

					// Check a Skill is selected
					if (skill1 != null && skill1.getSkill() != null) {
						// Get lamp skill second choice
						ComboSkillWrapper skill2 = lampSkill2
								.getSelectionModel().getSelectedItem();
						// The Skill choices
						Set<Skill> lampChoices = new LinkedHashSet<>();

						// Add the first choice
						lampChoices.add(skill1.getSkill());

						// Check if there is a 2nd skill
						if (skill2 != null && skill2.getSkill() != null) {
							// Add the second choice
							lampChoices.add(skill2.getSkill());
						}

						// Set the lamp skills
						quest.setForceLampSkills(lampChoices);
					} else {
						// Reset force skills
						quest.setForceLampSkills(new LinkedHashSet<>());
					}

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

		// Max rows & columns
		int columns = 8, rows = 3;

		// Add nodes to grid
		add(textRSN, 0, 0,
				(int) (columns * 0.5), 1);
		add(lampSkill1, (int) (columns * 0.5),
				0, (int) (columns * 0.25), 1);
		add(lampSkill2, (int) (columns * 0.75), 0,
				(int) (columns * 0.25), 1);
		add(listActions, 0, 1, (int) (columns * 0.5), 1);
		add(listInfo, (int) (columns * 0.5), 1,
				(int) (columns * 0.5), 1);
		add(btnRun, 0, 2,
				columns, 1);

		// Resize columns evenly
		for (int i = 0; i < columns; i++) {
			ColumnConstraints cc = new ColumnConstraints();

			cc.setPercentWidth(100.0 / columns);

			getColumnConstraints().add(cc);
		}

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

	/**
	 * Skill wrapper for ComboBox
	 */
	private static class ComboSkillWrapper {

		/**
		 * The Skill option
		 */
		private final Skill skill;

		/**
		 * Wraps a Skill to be used for a ComboBox
		 *
		 * @param skill The Skill to wrap
		 */
		private ComboSkillWrapper(Skill skill) {
			this.skill = skill;
		}

		/**
		 * Gets this Skill
		 *
		 * @return A Skill
		 */
		public Skill getSkill() {
			return skill;
		}

		@Override
		public String toString() {
			return skill != null ? skill.toString() : "";
		}
	}
}