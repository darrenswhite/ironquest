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
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Window;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Darren White
 */
public class MainPane extends GridPane {

    /**
     * The logger
     */
    private static final Logger log =
            Logger.getLogger(IronQuest.class.getName());

    /**
     * Executor for running IronQuest
     */
    private final ExecutorService executor = Executors.newCachedThreadPool();

    /**
     * ListView for all Actions
     */
    private ListView<Action> lstActions;

    /**
     * List to hold current action information
     */
    private ObservableList<String> info;

    /**
     * ListView for current Action information
     */
    private ListView<String> lstInfo;

    /**
     * Username text field
     */
    private TextField txtRSN;

    /**
     * Button to change force lamp skills
     */
    private Button btnLampSkills;

    /**
     * Button to toggle ironman mode
     */
    private ToggleButton btnIronman;

    /**
     * Button to toggle recommended mode
     */
    private ToggleButton btnRecommended;

    /**
     * Button to toggle members/free quests
     */
    private ComboBox<MembersSelection> cmbMembers;

    /**
     * The run button
     */
    private Button btnRun;

    /**
     * Search text for finding quest position
     */
    private TextField txtSearch;

    private void actionClick(MouseEvent e) {
        // Get selected Action
        Action act = lstActions.getSelectionModel().getSelectedItem();

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
     * Initializes this pane and its components
     */
    public void init() {
        // Create a new instance
        IronQuest quest = IronQuest.getInstance();

        // Set padding & gaps to 10px
        setPadding(new Insets(10));
        setHgap(10);
        setVgap(10);

        txtRSN = new TextField();
        // Set the username if already set
        quest.getPlayer()
                .ifPresent(p -> p.getName()
                        .ifPresent(n -> txtRSN.setText(n)));
        // Add tooltip info
        txtRSN.setTooltip(new Tooltip("Enter your RuneScape name to " +
                "retrieve quest and skill information."));
        // Add prompt text
        txtRSN.setPromptText("Username");

        txtSearch = new TextField();
        // Add tooltip info
        txtSearch.setTooltip(new Tooltip("Search for quests to find " +
                "when they will be completed"));
        // Add prompt text
        txtSearch.setPromptText("Search for quests");
        // Filter quests on keyboard event
        txtSearch.setOnKeyReleased(this::selectAction);

        btnLampSkills = new Button("Lamp Skills");
        // Show LampSkillsChoice on click
        btnLampSkills.setOnAction(this::showLampSkills);
        // Always fill width/height
        btnLampSkills.setMaxHeight(Double.MAX_VALUE);
        btnLampSkills.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btnLampSkills, Priority.ALWAYS);

        btnIronman = new ToggleButton("Ironman");
        // Set current state
        btnIronman.setSelected(IronQuest.getInstance().isIronman());
        // Toggle ironman mode
        btnIronman.setOnAction(e -> IronQuest.getInstance()
                .setIronman(btnIronman.isSelected()));
        // Always fill width/height
        btnIronman.setMaxHeight(Double.MAX_VALUE);
        btnIronman.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btnIronman, Priority.ALWAYS);

        btnRecommended = new ToggleButton("Recommended");
        // Set current state
        btnRecommended.setSelected(IronQuest.getInstance().isRecommended());
        // Toggle recommended mode
        btnRecommended.setOnAction(e -> IronQuest.getInstance()
                .setRecommended(btnRecommended.isSelected()));
        // Always fill width/height
        btnRecommended.setMaxHeight(Double.MAX_VALUE);
        btnRecommended.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btnRecommended, Priority.ALWAYS);

        cmbMembers = new ComboBox<>(
                FXCollections.observableArrayList(MembersSelection.values()));
        // Set selected value
        if (quest.isFree() && quest.isMembers()) {
            cmbMembers.getSelectionModel().select(MembersSelection.BOTH);
        } else if (quest.isFree()) {
            cmbMembers.getSelectionModel().select(MembersSelection.FREE);
        } else if (quest.isMembers()) {
            cmbMembers.getSelectionModel().select(MembersSelection.MEMBERS);
        }
        // Toggle recommended mode
        cmbMembers.setOnAction(this::setMembersFree);
        // Always fill width/height
        cmbMembers.setMaxHeight(Double.MAX_VALUE);
        cmbMembers.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(cmbMembers, Priority.ALWAYS);

        lstActions = new ListView<>();
        // Change the current action information on selection
        lstActions.setOnMouseClicked(this::actionClick);
        // Show information for selected Action
        lstActions.getSelectionModel().selectedItemProperty()
                .addListener(this::updateInfo);
        // Only one Action can be selected at a time
        lstActions.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        // Bind the actions to the ListView
        lstActions.itemsProperty().bind(Bindings.createObjectBinding(
                quest::getActions, quest.getActions()));
        // Add tooltip info
        lstActions.setTooltip(new Tooltip("The recommended order to " +
                "complete quests. Shows training information and skill " +
                "lamp choices."));

        lstInfo = new ListView<>();
        info = FXCollections.observableArrayList();
        // Bind the information for the current Action to the ListView
        lstInfo.itemsProperty().bind(
                Bindings.createObjectBinding(() -> info, info));
        // Add tooltip info
        lstInfo.setTooltip(new Tooltip("Shows player information " +
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

        // Max columns
        int columns = 10;

        // Add nodes to grid
        add(txtRSN, 0, 0, (int) (columns * 0.4), 1);
        add(txtSearch, 0, 1, (int) (columns * 0.4), 1);

        add(btnIronman, (int) (columns * 0.4), 0, (int) (columns * 0.3), 1);
        add(btnRecommended, (int) (columns * 0.4), 1, (int) (columns * 0.3), 1);

        add(btnLampSkills, (int) (columns * 0.7), 0, (int) (columns * 0.3), 1);
        add(cmbMembers, (int) (columns * 0.7), 1, (int) (columns * 0.3), 1);

        add(lstActions, 0, 2, (int) (columns * 0.5),
                1);
        add(lstInfo, (int) (columns * 0.5), 2,
                (int) (columns * 0.5), 1);

        add(btnRun, 0, 3,
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
        RowConstraints rc3 = new RowConstraints();

        // Middle row to fill height
        rc2.setFillHeight(true);
        rc2.setVgrow(Priority.ALWAYS);

        getRowConstraints().addAll(rc0, rc1, rc2, rc3);

        // Start focus on action list
        Platform.runLater(lstActions::requestFocus);
    }

    /**
     * Simulate the run button - runs the main algorithm
     */
    public void run() {
        run(null);
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

                // Set player name
                if (txtRSN.getText() != null && !txtRSN.getText()
                        .isEmpty()) {
                    quest.setPlayer(txtRSN.getText());
                } else {
                    quest.setPlayer(null);
                }

                // Run the algorithm
                quest.run();

                Platform.runLater(() -> {
                    // Focus the action list
                    lstActions.requestFocus();

                    // Update the player info if no actions are present
                    quest.getPlayer().ifPresent(p -> {
                        Action action = new Action(p) {

                            @Override
                            public String getMessage() {
                                return null;
                            }
                        };
                        updateInfo(null, null, action);
                    });

                    // Select the first action
                    lstActions.getSelectionModel().select(0);
                });
            } catch (Exception ex) {
                log.log(Level.SEVERE, "Unable to run: ", ex);
            }

            // Enable the button again
            Platform.runLater(() -> btnRun.setDisable(false));
        });
    }

    private void selectAction(KeyEvent e) {
        String query = txtSearch.getText();
        if (query == null || query.trim().isEmpty()) {
            return;
        }

        query = query.trim().toLowerCase();

        for (Action a : lstActions.getItems()) {
            String msg = a.getMessage().toLowerCase();
            if (msg.contains(query) || msg.startsWith(query) ||
                    msg.endsWith(query) || msg.equalsIgnoreCase(query)) {
                // Select & highlight the action
                lstActions.getSelectionModel().select(a);
                lstActions.scrollTo(a);
                break;
            }
        }
    }

    private void setMembersFree(ActionEvent e) {
        IronQuest quest = IronQuest.getInstance();
        MembersSelection selection = cmbMembers.getSelectionModel()
                .getSelectedItem();
        switch (selection) {
            case BOTH:
                quest.setFree(true);
                quest.setMembers(true);
                break;
            case FREE:
                quest.setFree(true);
                quest.setMembers(false);
                break;
            case MEMBERS:
                quest.setFree(false);
                quest.setMembers(true);
                break;
        }
    }

    /**
     * Shows the stage to choose Skills to use Lamps on
     *
     * @param e The ActionEvent invoked
     */
    private void showLampSkills(ActionEvent e) {
        Window owner = getScene().getWindow();
        LampSkillsChoice skillsChoice = new LampSkillsChoice(owner);

        skillsChoice.sizeToScene();
        skillsChoice.showAndWait();
    }

    /**
     * Update the info list with the current action information
     *
     * @param observable The {@code ObservableValue} which value changed
     * @param oldValue   The old value
     * @param newValue   The new value
     */
    private void updateInfo(ObservableValue<? extends Action> observable,
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

    private enum MembersSelection {
        BOTH,
        FREE,
        MEMBERS;

        @Override
        public String toString() {
            String str = super.toString();
            return str.toUpperCase().charAt(0) + str.toLowerCase().substring(1);
        }
    }
}