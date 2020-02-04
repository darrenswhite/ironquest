package com.darrenswhite.rs.ironquest.action;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import com.darrenswhite.rs.ironquest.dto.TrainActionDTO;
import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.Skill;
import org.junit.jupiter.api.Test;

class TrainActionTest {

  @Test
  void getType() {
    Player player = new Player.Builder().build();

    TrainAction trainAction = new TrainAction(player, Skill.CONSTITUTION, 40000, 55250);

    assertThat(trainAction.getType(), equalTo(ActionType.TRAIN));
  }

  @Test
  void getMessage() {
    Player player = new Player.Builder().build();

    TrainAction trainAction = new TrainAction(player, Skill.CONSTITUTION, 40000, 55250);

    assertThat(trainAction.getMessage(),
        equalTo("Train Constitution to level 43, requiring 15.25k xp"));
    assertThat(trainAction.toString(), equalTo(trainAction.getMessage()));
  }

  @Test
  void meetsRequirements() {
    Player player = new Player.Builder().build();

    TrainAction trainAction = new TrainAction(player, Skill.PRAYER, 0, 100);

    assertThat(trainAction.meetsRequirements(player), equalTo(true));
  }

  @Test
  void process() {
    Player player = new Player.Builder().build();

    TrainAction trainAction = new TrainAction(player, Skill.MAGIC, 0, 10000);

    trainAction.process(player);

    assertThat(player.getXp(Skill.MAGIC), equalTo(10000D));
  }

  @Test
  void createDTO() {
    Player player = new Player.Builder().build();

    TrainAction trainAction = new TrainAction(player, Skill.RANGED, 0, 100);

    TrainActionDTO dto = trainAction.createDTO();

    assertThat(dto.getMessage(), equalTo(trainAction.getMessage()));
    assertThat(dto.getPlayer(), equalTo(player.createDTO()));
    assertThat(dto.getType(), equalTo(ActionType.TRAIN));
    assertThat(dto.isFuture(), equalTo(false));
  }

  @Test
  void copyForPlayer() {
    Player player = new Player.Builder().withName("original").build();
    Player playerToCopy = new Player.Builder().withName("copy").build();

    TrainAction trainAction = new TrainAction(player, Skill.DIVINATION, 1500, 5000);

    TrainAction copied = trainAction.copyForPlayer(playerToCopy);

    assertThat(copied.getSkill(), equalTo(trainAction.getSkill()));
    assertThat(copied.getStartXp(), equalTo(trainAction.getStartXp()));
    assertThat(copied.getEndXp(), equalTo(trainAction.getEndXp()));
    assertThat(copied.getPlayer(), equalTo(playerToCopy));
  }
}
