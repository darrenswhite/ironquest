package com.darrenswhite.rs.ironquest.gui;

import com.darrenswhite.rs.ironquest.player.QuestEntry;
import com.darrenswhite.rs.ironquest.player.QuestPriority;
import com.darrenswhite.rs.ironquest.player.Skill;
import com.darrenswhite.rs.ironquest.quest.Quest;
import com.darrenswhite.rs.ironquest.quest.requirement.QuestRequirement;
import com.darrenswhite.rs.ironquest.quest.requirement.SkillRequirement;
import java.awt.Desktop;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Darren S. White
 */
public class QuestDetail extends Stage {

  private static final Logger LOG = LogManager.getLogger(QuestDetail.class);

  private static final String STAGE_NAME = "Quest Details";

  /**
   * URL of the RS wiki
   */
  private static final String URL_WIKI = "http://runescape.wikia.com/wiki/";

  private final QuestEntry questEntry;

  QuestDetail(Window owner, QuestEntry questEntry) {
    this.questEntry = questEntry;

    initModality(Modality.WINDOW_MODAL);
    initOwner(owner);
    setResizable(false);
    setTitle(STAGE_NAME);

    init();
  }

  private void init() {
    Quest quest = questEntry.getQuest();

    GridPane grid = new GridPane();

    grid.setPadding(new Insets(10));
    grid.setHgap(10);
    grid.setVgap(10);

    ScrollPane scroll = new ScrollPane(grid);
    scroll.setMaxSize(500, 500);
    scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

    Scene scene = new Scene(scroll);

    Label lblName = new Label(quest.getDisplayName());
    lblName.setFont(Font.font(null, FontWeight.BOLD, Font.getDefault().getSize()));
    lblName.setOnMouseClicked(this::openQuestUrl);
    GridPane.setHalignment(lblName, HPos.CENTER);

    Label lblMembers = new Label("Members");
    Label lblMembersValue = new Label(quest.isMembers() ? "Yes" : "No");

    Label lblSkillReqs = new Label("Skill requirements");
    GridPane.setValignment(lblSkillReqs, VPos.TOP);

    Set<String> skillReqStrings = quest.getSkillRequirements().stream()
        .map(SkillRequirement::toString).collect(Collectors.toSet());
    String skillReqs = String.join("\n", skillReqStrings);
    if (quest.getSkillRequirements().isEmpty()) {
      skillReqs = "None";
    }
    Label lstSkillReqs = new Label(skillReqs);

    Label lblQuestReqs = new Label("Quest requirements");
    GridPane.setValignment(lblQuestReqs, VPos.TOP);

    Set<String> questReqStrings = quest.getQuestRequirements().stream()
        .map(QuestRequirement::toString).collect(Collectors.toSet());
    String questReqs = String.join("\n", questReqStrings);
    if (quest.getQuestRequirements().isEmpty()) {
      questReqs = "None";
    }
    Label lstQuestReqs = new Label(questReqs);

    Label lblCombatReq = new Label("Combat requirement");
    GridPane.setValignment(lblCombatReq, VPos.TOP);

    String combatReq =
        quest.getCombatRequirement() != null ? quest.getCombatRequirement().toString() : "None";
    Label lblCombatReqValue = new Label(combatReq);

    Label lblQuestPointsReq = new Label("Quest points requirement");
    GridPane.setValignment(lblQuestPointsReq, VPos.TOP);

    String questPointsReq =
        quest.getQuestPointsRequirement() != null ? quest.getQuestPointsRequirement().toString()
            : "None";
    Label lblQuestPointsReqValue = new Label(questPointsReq);

    Label lblQuestPoints = new Label("Quest points");
    Label lblQuestPointsValue = new Label(Integer.toString(quest.getQuestPointsReward()));

    Label lblSkillRewards = new Label("Skill rewards");

    Label lblUserPriority = new Label("User priority");

    ComboBox<QuestPriority> cmbPriority = new ComboBox<>(
        FXCollections.observableArrayList(QuestPriority.values()));
    cmbPriority.setTooltip(new Tooltip("Set custom priority for quest"));

    cmbPriority.setOnAction(
        e -> questEntry.setPriority(cmbPriority.getSelectionModel().getSelectedItem()));

    cmbPriority.getSelectionModel().select(questEntry.getPriority());
    GridPane.setValignment(lblSkillRewards, VPos.TOP);

    Set<String> skillRewardStrings = quest.getXpRewards().entrySet().stream()
        .map(e -> Skill.formatXp(e.getValue()) + " " + e.getKey()).collect(Collectors.toSet());
    String skillRewards = String.join("\n", skillRewardStrings);
    if (quest.getXpRewards().isEmpty()) {
      skillRewards = "None";
    }
    Label lstSkillRewards = new Label(skillRewards);

    Label lblLampRewards = new Label("Lamp rewards");
    GridPane.setValignment(lblLampRewards, VPos.TOP);

    String lampRewards = formatLampRewards(quest);
    Label lstLampRewards = new Label(lampRewards);

    grid.add(lblName, 0, 0, 2, 1);
    grid.add(lblUserPriority, 0, 1);
    grid.add(cmbPriority, 1, 1);
    grid.add(lblMembers, 0, 2);
    grid.add(lblMembersValue, 1, 2);
    grid.add(lblSkillReqs, 0, 3);
    grid.add(lstSkillReqs, 1, 3);
    grid.add(lblQuestReqs, 0, 4);
    grid.add(lstQuestReqs, 1, 4);
    grid.add(lblCombatReq, 0, 5);
    grid.add(lblCombatReqValue, 1, 5);
    grid.add(lblQuestPointsReq, 0, 6);
    grid.add(lblQuestPointsReqValue, 1, 6);
    grid.add(lblQuestPoints, 0, 7);
    grid.add(lblQuestPointsValue, 1, 7);
    grid.add(lblSkillRewards, 0, 8);
    grid.add(lstSkillRewards, 1, 8);
    grid.add(lblLampRewards, 0, 9);
    grid.add(lstLampRewards, 1, 9);

    sizeToScene();
    setScene(scene);
  }

  private String formatLampRewards(Quest quest) {
    Set<String> lampRewardStrings = quest.getLampRewards().stream().map(l -> {
      StringBuilder sb = new StringBuilder();
      Map<Set<Skill>, Integer> reqs = l.getRequirements();
      boolean sameLevel = reqs.values().stream().distinct().limit(2).count() <= 1;

      sb.append(Skill.formatXp(l.getXp())).append(" xp");

      if (reqs.size() == 1) {
        sb.append(" in: ").append(formatLampReward(reqs.entrySet().iterator().next()));
      } else if (reqs.size() == Skill.values().length && sameLevel) {
        sb.append("in any skill with level: ").append(reqs.values().iterator().next());
      } else {
        sb.append(" in one of:");

        for (Map.Entry<Set<Skill>, Integer> entry : reqs.entrySet()) {
          sb.append("\n\t").append(formatLampReward(entry));
        }
      }
      return sb.toString();
    }).collect(Collectors.toSet());

    String lampRewards = String.join("\n", lampRewardStrings);
    if (quest.getLampRewards().isEmpty()) {
      lampRewards = "None";
    }
    return lampRewards;
  }

  private String formatLampReward(Entry<Set<Skill>, Integer> entry) {
    StringBuilder sb = new StringBuilder();
    Set<Skill> skills = entry.getKey();
    int level = entry.getValue();

    if (skills.size() == Skill.values().length) {
      // TODO Handle all skills
    } else {
      sb.append(String.join(", ", skills.stream().map(Skill::toString).collect(Collectors.toSet())))
          .append(": ").append(level);
    }

    return sb.toString();
  }

  private void openQuestUrl(MouseEvent e) {
    if (e.getClickCount() != 2 || !Desktop.isDesktopSupported()) {
      return;
    }

    try {
      String name = questEntry.getQuest().getDisplayName().replace(' ', '_');
      String url = URL_WIKI + name;

      Desktop.getDesktop().browse(new URI(url));
    } catch (UnsupportedEncodingException ex) {
      LOG.error("Unsupported encoding", ex);
    } catch (IOException | URISyntaxException ex) {
      LOG.error("Unable to open questEntry URL", ex);
    }
  }
}
