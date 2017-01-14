package com.darrenswhite.rs.ironquest.gui;

import com.darrenswhite.rs.ironquest.IronQuest;
import com.darrenswhite.rs.ironquest.action.Action;
import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.Skill;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.util.LinkedHashSet;
import java.util.Optional;
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
	 * ListView for all Actions
	 */
	private ListView<Action> listActions;

	/**
	 * List to hold current action information
	 */
	private ObservableList<String> info;

	/**
	 * ListView for current Action information
	 */
	private ListView<String> listInfo;

	/**
	 * Username text field
	 */
	private TextField textRSN;

	/**
	 * Skills to choose from to override lamp choices
	 */
	private ObservableList<SkillWrapper> lampSkills;

	/**
	 * Force lamp skill - first choice
	 */
	private ComboBox<SkillWrapper> lampSkill1;

	/**
	 * Force lamp skill - second choice
	 */
	private ComboBox<SkillWrapper> lampSkill2;

	/**
	 * The run button
	 */
	private Button btnRun;

	/**
	 * Creates a new MainPane
	 */
	public MainPane() {
		init();
	}

	private void actionClick(MouseEvent e) {
		// Get selected Action
		Action act = listActions.getSelectionModel().getSelectedItem();

		if (act != null) {
			// Each Action can have different events for click
			// Pass the Scene and MouseEvent to the Action
			act.onClick(getScene(), e);
		}
	}

	/**
	 * Notify to close the MainPane
	 */
	public void close() {
		// Shutdown the executor as the primary stage is closing
		executor.shutdownNow();
	}

	/**
	 * Update the info list with the current action information
	 *
	 * @param observable The {@code ObservableValue} which value changed
	 * @param oldValue   The old value
	 * @param newValue   The new value
	 */
	private void getInfo(ObservableValue<? extends Action> observable,
	                     Action oldValue,
	                     Action newValue) {
		if (newValue == null) {
			return;
		}

		// Clear old information
		info.clear();

		// Get the Player for this Action
		Player p = newValue.getPlayer();

		// Combat level
		double cmb = p.getCombatLevel();
		info.add("Combat level: " + cmb);

		// Total quest points
		int qp = p.getQuestPoints();
		info.add("Quest points: " + qp);

		// Total skill level
		int totalLvl = p.getTotalLevel();
		info.add("Total level: " + totalLvl);

		// Skill levels/xp
		p.getXPs()
				.forEach((s, xp) -> info.add(s + ": " + s.getLevelAt(xp) +
						" (" + Skill.formatXP(xp) + " xp)"));
	}

	/**
	 * Initializes this pane and its components
	 */
	private void init() {
		// Create a new instance
		IronQuest quest = IronQuest.getInstance();

		// Set padding & gaps to 10px
		setPadding(new Insets(10));
		setHgap(10);
		setVgap(10);

		textRSN = new TextField();
		// Add tooltip info
		textRSN.setTooltip(new Tooltip("Enter your RuneScape name to " +
				"retrieve quest and skill information."));
		// Add prompt text
		textRSN.setPromptText("Username");

		lampSkills = FXCollections.observableArrayList();
		// Add a "None" option
		lampSkills.add(new SkillWrapper(null));

		// Add all Skills
		for (Skill s : Skill.values()) {
			lampSkills.add(new SkillWrapper(s));
		}

		lampSkill1 = new ComboBox<>(lampSkills);
		// Add tooltip info
		lampSkill1.setTooltip(new Tooltip("Force skill lamps to be " +
				"used on a chosen skill if possible."));
		// Always fill width
		lampSkill1.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(lampSkill1, Priority.ALWAYS);

		lampSkill2 = new ComboBox<>(lampSkills);
		// Disable second choice by default
		lampSkill2.setDisable(true);
		// Second choice is only enabled if first choice is valid
		lampSkill2.disableProperty()
				.bind(Bindings.createBooleanBinding(() -> {
					SkillWrapper item = lampSkill1.getSelectionModel()
							.getSelectedItem();

					return item == null || !item.getSkill().isPresent();
				}, lampSkill1.getSelectionModel().selectedIndexProperty()));
		// Add tooltip info
		lampSkill2.setTooltip(new Tooltip("Force skill lamps to be " +
				"used on a chosen skill if possible. Fallback choice if " +
				"the first cannot be used."));
		// Always fill width
		lampSkill2.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(lampSkill2, Priority.ALWAYS);

		listActions = new ListView<>();
		// Change the current action information on selection
		listActions.setOnMouseClicked(this::actionClick);
		// Show information for selected Action
		listActions.getSelectionModel().selectedItemProperty()
				.addListener(this::getInfo);
		// Only one Action can be selected at a time
		listActions.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		// Bind the actions to the ListView
		listActions.itemsProperty().bind(Bindings.createObjectBinding(
				quest::getActions, quest.getActions()));
		// Add tooltip info
		listActions.setTooltip(new Tooltip("The recommended order to " +
				"complete quests. Shows training information and skill " +
				"lamp choices."));

		listInfo = new ListView<>();
		info = FXCollections.observableArrayList();
		// Bind the information for the current Action to the ListView
		listInfo.itemsProperty().bind(
				Bindings.createObjectBinding(() -> info, info));
		// Add tooltip info
		listInfo.setTooltip(new Tooltip("Shows player information " +
				"after completing the selected quest."));

		btnRun = new Button("Run");
		// Don't focus run button
		btnRun.setFocusTraversable(false);
		// Run when the button is pressed
		btnRun.setOnAction(this::run);
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
		add(listActions, 0, 1, (int) (columns * 0.5),
				1);
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
	 * Event executed when the run button is clicked
	 *
	 * @param e The ActionEvent invoked
	 */
	private void run(ActionEvent e) {
		// Disable the button so it can only run one at a time
		btnRun.setDisable(true);

		// Run in the background
		executor.submit(() -> {
			try {
				// Get the instance
				IronQuest quest = IronQuest.getInstance();
				// Get lamp skill first choice
				SkillWrapper skill1 = lampSkill1.getSelectionModel()
						.getSelectedItem();

				// Check a Skill is selected
				if (skill1 != null && skill1.getSkill().isPresent()) {
					// Get lamp skill second choice
					SkillWrapper skill2 = lampSkill2.getSelectionModel()
							.getSelectedItem();
					// The Skill choices
					Set<Skill> lampChoices = new LinkedHashSet<>();

					// Add the first choice
					lampChoices.add(skill1.getSkill().get());

					// Check if there is a 2nd skill
					if (skill2 != null && skill2.getSkill().isPresent()) {
						// Add the second choice
						lampChoices.add(skill2.getSkill().get());
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
	}

	/**
	 * Skill wrapper for ComboBox - to allow null values
	 */
	private static class SkillWrapper {

		/**
		 * The Skill option
		 */
		private final Skill skill;

		/**
		 * Wraps a Skill to be used for a ComboBox
		 *
		 * @param skill The Skill to wrap
		 */
		private SkillWrapper(Skill skill) {
			this.skill = skill;
		}

		/**
		 * Gets this Skill
		 *
		 * @return A Skill
		 */
		public Optional<Skill> getSkill() {
			return Optional.ofNullable(skill);
		}

		@Override
		public String toString() {
			return skill != null ? skill.toString() : "";
		}
	}
}