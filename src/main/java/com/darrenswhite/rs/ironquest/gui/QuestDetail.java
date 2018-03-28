package com.darrenswhite.rs.ironquest.gui;

import com.darrenswhite.rs.ironquest.IronQuest;
import com.darrenswhite.rs.ironquest.player.Skill;
import com.darrenswhite.rs.ironquest.quest.Quest;
import com.darrenswhite.rs.ironquest.quest.requirement.Requirement;
import com.darrenswhite.rs.ironquest.quest.requirement.SkillRequirement;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.awt.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
	 * The skill requirements label
	 */
	private Label lblSkillReqs;

	/**
	 * The list of skill requirements
	 */
	private Label lstSkillReqs;

	/**
	 * The quest requirements label
	 */
	private Label lblQuestReqs;

	/**
	 * The list of quest requirements
	 */
	private Label lstQuestReqs;

	/**
	 * The other requirements label
	 */
	private Label lblOtherReqs;

	/**
	 * The list of other requirements
	 */
	private Label lstOtherReqs;

	/**
	 * The quest points label
	 */
	private Label lblQuestPoints;

	/**
	 * The number of quest points label
	 */
	private Label lblQuestPointsValue;

	/**
	 * The skill rewards label
	 */
	private Label lblSkillRewards;

	/**
	 * The list of skill rewards
	 */
	private Label lstSkillRewards;

	/**
	 * The lamp rewards label
	 */
	private Label lblLampRewards;

	/**
	 * The list of lamp rewards
	 */
	private Label lstLampRewards;

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
		// Make title bold
		lblName.setFont(Font.font(null, FontWeight.BOLD,
				Font.getDefault().getSize()));
		// Open RS wiki page on double click
		lblName.setOnMouseClicked(this::openQuestUrl);
		// Center the name label
		GridPane.setHalignment(lblName, HPos.CENTER);

		lblSkillReqs = new Label("Skill requirements");
		// Align to the top
		GridPane.setValignment(lblSkillReqs, VPos.TOP);

		// Get skill requirements as strings
		Set<String> skillReqStrings = quest.getSkillRequirements()
				.stream()
				.map(SkillRequirement::toString)
				.collect(Collectors.toSet());
		// Join strings with newline character
		String skillReqs = String.join("\n", skillReqStrings);
		// Placeholder in there are no skill requirements
		if (quest.getSkillRequirements().size() == 0) {
			skillReqs = "None";
		}
		lstSkillReqs = new Label(skillReqs);

		lblQuestReqs = new Label("Quest requirements");
		// Align to the top
		GridPane.setValignment(lblQuestReqs, VPos.TOP);

		// Get quest requirements as strings
		Set<String> questReqStrings = quest.getQuestRequirements()
				.stream()
				.map(qr -> IronQuest.getInstance()
						.getQuest(qr.getId())
						.getDisplayName())
				.collect(Collectors.toSet());
		// Join strings with newline character
		String questReqs = String.join("\n", questReqStrings);
		// Placeholder in there are no skill requirements
		if (quest.getQuestRequirements().size() == 0) {
			questReqs = "None";
		}
		lstQuestReqs = new Label(questReqs);

		lblOtherReqs = new Label("Other requirements");
		// Align to the top
		GridPane.setValignment(lblQuestReqs, VPos.TOP);

		// Get skill requirements as strings
		Set<String> otherReqStrings = quest.getOtherRequirements()
				.stream()
				.map(Requirement::toString)
				.collect(Collectors.toSet());
		// Join strings with newline character
		String otherReqs = String.join("\n", otherReqStrings);
		// Placeholder in there are no skill requirements
		if (quest.getOtherRequirements().size() == 0) {
			otherReqs = "None";
		}
		lstOtherReqs = new Label(otherReqs);

		lblQuestPoints = new Label("Quest points");
		lblQuestPointsValue = new Label(Integer.toString(quest.getQuestPoints()));

		lblSkillRewards = new Label("Skill rewards");
		// Align to the top
		GridPane.setValignment(lblSkillRewards, VPos.TOP);

		// Get skill rewards as strings
		Set<String> skillRewardStrings = quest.getSkillRewards()
				.entrySet()
				.stream()
				.map(e -> Skill.formatXP(e.getValue()) + " " + e.getKey())
				.collect(Collectors.toSet());
		// Join strings with newline character
		String skillRewards = String.join("\n", skillRewardStrings);
		// Placeholder if there are no skill rewards
		if (quest.getSkillRewards().size() == 0) {
			skillRewards = "None";
		}
		lstSkillRewards = new Label(skillRewards);

		lblLampRewards = new Label("Lamp rewards");
		// Align to the top
		GridPane.setValignment(lblLampRewards, VPos.TOP);

		// Get lamp rewards as strings
		// TODO add lamp requirements
		Set<String> lampRewardStrings = quest.getLampRewards()
				.stream()
				.map(l -> Skill.formatXP(l.getValue()))
				.collect(Collectors.toSet());
		// Join strings with newline character
		String lampRewards = String.join("\n", lampRewardStrings);
		// Placeholder if there are no lamp rewards
		if (quest.getLampRewards().size() == 0) {
			lampRewards = "None";
		}
		lstLampRewards = new Label(lampRewards);

		// Add components to the grid
		grid.add(lblName, 0, 0, 2, 1);
		grid.add(lblSkillReqs, 0, 1);
		grid.add(lstSkillReqs, 1, 1);
		grid.add(lblQuestReqs, 0, 2);
		grid.add(lstQuestReqs, 1, 2);
		grid.add(lblOtherReqs, 0, 3);
		grid.add(lstOtherReqs, 1, 3);
		grid.add(lblQuestPoints, 0, 4);
		grid.add(lblQuestPointsValue, 1, 4);
		grid.add(lblSkillRewards, 0, 5);
		grid.add(lstSkillRewards, 1, 5);
		grid.add(lblLampRewards, 0, 6);
		grid.add(lstLampRewards, 1, 6);

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