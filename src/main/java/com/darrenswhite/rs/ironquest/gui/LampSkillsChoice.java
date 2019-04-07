package com.darrenswhite.rs.ironquest.gui;

import com.darrenswhite.rs.ironquest.IronQuest;
import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.Skill;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Darren S. White
 */
class LampSkillsChoice extends Stage {

  private static final Logger LOG = LogManager.getLogger(QuestDetail.class);

  private static final String STAGE_NAME = "Skill LampReward Choices";

  private ListView<Skill> listSkills;
  private Label lblSkills;

  LampSkillsChoice(Window owner) {
    initModality(Modality.WINDOW_MODAL);
    initOwner(owner);
    setResizable(false);
    setTitle(STAGE_NAME);

    init();
  }

  private void init() {
    GridPane grid = new GridPane();
    grid.setPadding(new Insets(10));
    grid.setHgap(10);
    grid.setVgap(10);

    ScrollPane scroll = new ScrollPane(grid);
    scroll.setMaxSize(500, 500);
    scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

    Scene scene = new Scene(scroll);

    listSkills = new ListView<>(FXCollections.observableArrayList(Skill.values()));

    listSkills.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    listSkills.getSelectionModel().getSelectedItems().addListener(this::updateLampSkills);

    lblSkills = new Label();

    grid.add(lblSkills, 0, 0);
    grid.add(listSkills, 0, 1);

    selectSkills();

    sizeToScene();
    setScene(scene);
  }

  private void selectSkills() {
    Set<Skill> skills = IronQuest.getInstance().getPlayer().getLampSkills();

    for (Skill s : skills) {
      listSkills.getSelectionModel().select(s);
    }

    updateSkillsLabel(skills);
  }

  private void updateLampSkills(ListChangeListener.Change<? extends Skill> c) {
    Player player = IronQuest.getInstance().getPlayer();
    Set<Skill> skills = player.getLampSkills();

    while (c.next()) {
      if (c.wasUpdated()) {
        LOG.debug("LampForceSkills: Updated");
      } else if (c.wasPermutated()) {
        LOG.debug("LampForceSkills: Permutated");
      } else {
        try {
          if (c.wasAdded()) {
            skills.addAll(c.getAddedSubList());
          }

          if (c.wasRemoved()) {
            skills.removeAll(c.getRemoved());
          }
        } catch (IndexOutOfBoundsException ignored) {
          // There is a bug in Java 8
          // See https://bugs.openjdk.java.net/browse/JDK-8138843
          skills.clear();
          skills.addAll(c.getList());
        }
      }
    }

    updateSkillsLabel(skills);
  }

  private void updateSkillsLabel(Set<Skill> skills) {
    StringBuilder sb = new StringBuilder();
    int index = 1;

    for (Skill skill : skills) {
      sb.append(index++).append(". ").append(skill.toString()).append('\n');
    }

    if (sb.length() > 0) {
      sb.deleteCharAt(sb.length() - 1);
    }

    lblSkills.setText(sb.toString());
  }
}
