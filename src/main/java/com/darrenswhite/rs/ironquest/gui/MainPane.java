package com.darrenswhite.rs.ironquest.gui;

import com.darrenswhite.rs.ironquest.IronQuest;
import com.darrenswhite.rs.ironquest.action.Action;
import com.darrenswhite.rs.ironquest.action.LampAction;
import com.darrenswhite.rs.ironquest.action.QuestAction;
import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.QuestEntry;
import com.darrenswhite.rs.ironquest.player.QuestPriority;
import com.darrenswhite.rs.ironquest.player.Skill;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Window;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Darren S. White
 */
public class MainPane extends GridPane {

  private static final Logger LOG = LogManager.getLogger(IronQuest.class);

  private final ExecutorService executor = Executors.newCachedThreadPool();
  private ListView<Action> lstActions;
  private ObservableList<String> info;
  private ComboBox<MembersSelection> cmbMembers;
  private Button btnRun;

  public void init() {
    IronQuest quest = IronQuest.getInstance();
    Player player = quest.getPlayer();

    setPadding(new Insets(10));
    setHgap(10);
    setVgap(10);

    TextField txtRSN = new TextField(player.getName());
    txtRSN.setTooltip(
        new Tooltip("Enter your RuneScape name to retrieve quest and skill information."));
    txtRSN.setPromptText("Username");
    txtRSN.setOnKeyReleased(e -> player.setName(txtRSN.getText()));

    TextField txtSearch = new TextField();
    txtSearch.setTooltip(new Tooltip("Search for quests to find when they will be completed"));
    txtSearch.setPromptText("Search for quests");
    txtSearch.setOnKeyReleased(e -> selectAction(txtSearch.getText()));

    Button btnLampSkills = new Button("Lamp Skills");
    btnLampSkills
        .setTooltip(new Tooltip("Choose which skills lamps should be used on (must run again)"));
    btnLampSkills.setOnAction(e -> showLampSkills());
    btnLampSkills.setMaxHeight(Double.MAX_VALUE);
    btnLampSkills.setMaxWidth(Double.MAX_VALUE);
    HBox.setHgrow(btnLampSkills, Priority.ALWAYS);

    ToggleButton btnIronman = new ToggleButton("Ironman");
    btnIronman.setTooltip(new Tooltip("Toggle ironman requirements for quests (must run again)"));
    btnIronman.setSelected(player.isIronman());
    btnIronman.setOnAction(e -> player.setIronman(btnIronman.isSelected()));
    btnIronman.setMaxHeight(Double.MAX_VALUE);
    btnIronman.setMaxWidth(Double.MAX_VALUE);
    HBox.setHgrow(btnIronman, Priority.ALWAYS);

    ToggleButton btnRecommended = new ToggleButton("Recommended");
    btnRecommended
        .setTooltip(new Tooltip("Toggle recommended requirements for quests (must run again)"));
    btnRecommended.setSelected(player.isRecommended());
    btnRecommended.setOnAction(e -> player.setRecommended(btnRecommended.isSelected()));
    btnRecommended.setMaxHeight(Double.MAX_VALUE);
    btnRecommended.setMaxWidth(Double.MAX_VALUE);
    HBox.setHgrow(btnRecommended, Priority.ALWAYS);

    cmbMembers = new ComboBox<>(FXCollections.observableArrayList(MembersSelection.values()));
    cmbMembers.setTooltip(new Tooltip("Filter free/members quests"));
    if (player.isFree() && player.isMembers()) {
      cmbMembers.getSelectionModel().select(MembersSelection.BOTH);
    } else if (player.isFree()) {
      cmbMembers.getSelectionModel().select(MembersSelection.FREE);
    } else if (player.isMembers()) {
      cmbMembers.getSelectionModel().select(MembersSelection.MEMBERS);
    }
    cmbMembers.setOnAction(e -> setMembersFree());
    cmbMembers.setMaxHeight(Double.MAX_VALUE);
    cmbMembers.setMaxWidth(Double.MAX_VALUE);
    HBox.setHgrow(cmbMembers, Priority.ALWAYS);

    lstActions = new ListView<>();
    lstActions.setOnMouseClicked(this::actionClick);
    lstActions.getSelectionModel().selectedItemProperty()
        .addListener((observable, oldValue, newValue) -> {
          if (newValue != null) {
            updateInfo(newValue.getPlayer());
          }
        });
    lstActions.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    lstActions.itemsProperty()
        .bind(Bindings.createObjectBinding(quest::getActions, quest.getActions()));
    lstActions.setTooltip(new Tooltip(
        "The recommended order to complete quests. Shows training information and skill lamp choices."));

    ListView<String> lstInfo = new ListView<>();
    info = FXCollections.observableArrayList();
    lstInfo.itemsProperty().bind(Bindings.createObjectBinding(() -> info, info));
    lstInfo
        .setTooltip(new Tooltip("Shows player information after completing the selected quest."));

    btnRun = new Button("Run");
    btnRun.setFocusTraversable(false);
    btnRun.setOnAction(e -> run());
    btnRun.setDefaultButton(true);
    btnRun.setMaxHeight(Double.MAX_VALUE);
    btnRun.setMaxWidth(Double.MAX_VALUE);
    HBox.setHgrow(btnRun, Priority.ALWAYS);

    Button btnResetQuestPriorities = new Button("Reset Custom Priorities");
    btnResetQuestPriorities
        .setTooltip(new Tooltip(("Resets all user-set quest priorities to NORMAL")));

    btnResetQuestPriorities.setOnAction(e -> {

      Alert alert = new Alert(AlertType.CONFIRMATION);
      alert.setTitle("Confirmation Dialog");
      alert.setHeaderText("This button will reset all custom quest priorities to NORMAL");
      alert.setContentText("Are you sure you want to reset ALL priorities?");

      Optional<ButtonType> result = alert.showAndWait();
      if (result.isPresent() && result.get() == ButtonType.OK) {
        resetQuestPriorities();
      }
    });

    btnResetQuestPriorities.setMaxHeight(Double.MAX_VALUE);
    btnResetQuestPriorities.setMaxWidth(Double.MAX_VALUE);
    HBox.setHgrow(btnResetQuestPriorities, Priority.ALWAYS);

    int columns = 10;

    add(txtRSN, 0, 0, (int) (columns * 0.4), 1);
    add(txtSearch, 0, 1, (int) (columns * 0.4), 1);

    add(btnIronman, (int) (columns * 0.4), 0, (int) (columns * 0.2), 1);
    add(btnRecommended, (int) (columns * 0.4), 1, (int) (columns * 0.3), 1);

    add(btnLampSkills, (int) (columns * 0.6), 0, (int) (columns * 0.2), 1);
    add(cmbMembers, (int) (columns * 0.7), 1, (int) (columns * 0.3), 1);

    add(btnResetQuestPriorities, (int) (columns * 0.8), 0, (int) (columns * 0.2), 1);

    add(lstActions, 0, 2, (int) (columns * 0.5), 1);
    add(lstInfo, (int) (columns * 0.5), 2, (int) (columns * 0.5), 1);

    add(btnRun, 0, 3, columns, 1);

    for (int i = 0; i < columns; i++) {
      ColumnConstraints cc = new ColumnConstraints();

      cc.setPercentWidth(100.0 / columns);

      getColumnConstraints().add(cc);
    }

    RowConstraints rc0 = new RowConstraints();
    RowConstraints rc1 = new RowConstraints();
    RowConstraints rc2 = new RowConstraints();
    RowConstraints rc3 = new RowConstraints();

    rc2.setFillHeight(true);
    rc2.setVgrow(Priority.ALWAYS);

    getRowConstraints().addAll(rc0, rc1, rc2, rc3);

    Platform.runLater(lstActions::requestFocus);
  }

  public void close() {
    executor.shutdownNow();
  }

  public void run() {
    btnRun.setDisable(true);

    executor.submit(() -> {
      try {
        IronQuest quest = IronQuest.getInstance();

        quest.run();

        Platform.runLater(() -> {
          lstActions.requestFocus();
          updateInfo(quest.getPlayer());
          lstActions.getSelectionModel().select(0);
        });
      } catch (Exception e) {
        LOG.error("Unable to run", e);
      }

      Platform.runLater(() -> btnRun.setDisable(false));
    });
  }

  private void actionClick(MouseEvent e) {
    if (e.getClickCount() == 2) {
      Action action = lstActions.getSelectionModel().getSelectedItem();

      if (action instanceof LampAction) {
        LampAction lampAction = (LampAction) action;
        showQuestDetail(lampAction.getQuestEntry());
      } else if (action instanceof QuestAction) {
        QuestAction questAction = (QuestAction) action;
        showQuestDetail(questAction.getQuestEntry());
      }
    }
  }

  private void showQuestDetail(QuestEntry questEntry) {
    QuestDetail questDetail = new QuestDetail(getScene().getWindow(), questEntry);
    questDetail.sizeToScene();
    questDetail.showAndWait();
  }

  private void selectAction(String query) {
    if (query != null && !query.trim().isEmpty()) {
      query = query.trim().toLowerCase();

      for (Action a : lstActions.getItems()) {
        String msg = a.getMessage().toLowerCase();
        if (msg.contains(query) || msg.startsWith(query) || msg.endsWith(query) || msg
            .equalsIgnoreCase(query)) {
          lstActions.getSelectionModel().select(a);
          lstActions.scrollTo(a);
          break;
        }
      }
    }
  }

  private void resetQuestPriorities() {
    IronQuest.getInstance().getPlayer().getQuests()
        .forEach(e -> e.setPriority(QuestPriority.NORMAL));
  }

  private void setMembersFree() {
    Player player = IronQuest.getInstance().getPlayer();
    MembersSelection selection = cmbMembers.getSelectionModel().getSelectedItem();
    switch (selection) {
      case BOTH:
        player.setFree(true);
        player.setMembers(true);
        break;
      case FREE:
        player.setFree(true);
        player.setMembers(false);
        break;
      case MEMBERS:
        player.setFree(false);
        player.setMembers(true);
        break;
    }
  }

  private void showLampSkills() {
    Window owner = getScene().getWindow();
    LampSkillsChoice skillsChoice = new LampSkillsChoice(owner);

    skillsChoice.sizeToScene();
    skillsChoice.showAndWait();
  }

  private void updateInfo(Player player) {
    info.clear();

    info.add("Combat level: " + player.getCombatLevel());
    info.add("Quest points: " + player.getQuestPoints());
    info.add("Total level: " + player.getTotalLevel());

    player.getSkillXps().forEach(
        (s, xp) -> info.add(s + ": " + s.getLevelAt(xp) + " (" + Skill.formatXp(xp) + " xp)"));
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
