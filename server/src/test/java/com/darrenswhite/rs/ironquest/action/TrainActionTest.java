package com.darrenswhite.rs.ironquest.action;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import com.darrenswhite.rs.ironquest.dto.TrainActionDTO;
import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.Skill;
import org.junit.Test;

public class TrainActionTest {

  @Test
  public void getType() {
    Player player = new Player.Builder().build();

    TrainAction trainAction = new TrainAction(player, Skill.CONSTITUTION, 40000, 55250);

    assertThat(ActionType.TRAIN, equalTo(trainAction.getType()));
  }

  @Test
  public void getMessage() {
    Player player = new Player.Builder().build();

    TrainAction trainAction = new TrainAction(player, Skill.CONSTITUTION, 40000, 55250);

    assertThat("Train Constitution to level 43, requiring 15.25k xp",
        equalTo(trainAction.getMessage()));
    assertThat(trainAction.getMessage(), equalTo(trainAction.toString()));
  }

  @Test
  public void meetsRequirements() {
    Player player = new Player.Builder().build();

    TrainAction trainAction = new TrainAction(player, Skill.PRAYER, 0, 100);

    assertThat(trainAction.meetsRequirements(player), equalTo(true));
  }

  @Test
  public void process() {
    Player player = new Player.Builder().build();

    TrainAction trainAction = new TrainAction(player, Skill.MAGIC, 0, 10000);

    trainAction.process(player);

    assertThat(10000D, equalTo(player.getXp(Skill.MAGIC)));
  }

  @Test
  public void createDTO() {
    Player player = new Player.Builder().build();

    TrainAction trainAction = new TrainAction(player, Skill.RANGED, 0, 100);

    TrainActionDTO dto = trainAction.createDTO();

    assertThat(trainAction.getMessage(), equalTo(dto.getMessage()));
    assertThat(player.createDTO(), equalTo(dto.getPlayer()));
    assertThat(ActionType.TRAIN, equalTo(dto.getType()));
    assertThat(dto.isFuture(), equalTo(false));
  }

  @Test
  public void copyForPlayer() {
    Player player = new Player.Builder().withName("original").build();
    Player playerToCopy = new Player.Builder().withName("copy").build();

    TrainAction trainAction = new TrainAction(player, Skill.DIVINATION, 1500, 5000);

    TrainAction copied = trainAction.copyForPlayer(playerToCopy);

    assertThat(trainAction.getSkill(), equalTo(copied.getSkill()));
    assertThat(trainAction.getStartXp(), equalTo(copied.getStartXp()));
    assertThat(trainAction.getEndXp(), equalTo(copied.getEndXp()));
    assertThat(playerToCopy, equalTo(copied.getPlayer()));
  }
}
