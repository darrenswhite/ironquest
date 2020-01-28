package com.darrenswhite.rs.ironquest.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.darrenswhite.rs.ironquest.dto.TrainActionDTO;
import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.Skill;
import org.junit.Test;

public class TrainActionTest {

  @Test
  public void getType() {
    Player player = new Player.Builder().build();

    TrainAction trainAction = new TrainAction(player, Skill.CONSTITUTION, 40000, 55250);

    assertEquals(ActionType.TRAIN, trainAction.getType());
  }

  @Test
  public void getMessage() {
    Player player = new Player.Builder().build();

    TrainAction trainAction = new TrainAction(player, Skill.CONSTITUTION, 40000, 55250);

    assertEquals("Train Constitution to level 43, requiring 15.25k xp", trainAction.getMessage());
    assertEquals(trainAction.getMessage(), trainAction.toString());
  }

  @Test
  public void meetsRequirements() {
    Player player = new Player.Builder().build();

    TrainAction trainAction = new TrainAction(player, Skill.PRAYER, 0, 100);

    assertTrue(trainAction.meetsRequirements(player));
  }

  @Test
  public void process() {
    Player player = new Player.Builder().build();

    TrainAction trainAction = new TrainAction(player, Skill.MAGIC, 0, 10000);

    trainAction.process(player);

    assertEquals(10000, player.getXp(Skill.MAGIC), 0);
  }

  @Test
  public void createDTO() {
    Player player = new Player.Builder().build();

    TrainAction trainAction = new TrainAction(player, Skill.RANGED, 0, 100);

    TrainActionDTO dto = trainAction.createDTO();

    assertEquals(trainAction.getMessage(), dto.getMessage());
    assertEquals(player.createDTO(), dto.getPlayer());
    assertEquals(ActionType.TRAIN, dto.getType());
  }

  @Test
  public void copyForPlayer() {
    Player player = new Player.Builder().withName("original").build();
    Player playerToCopy = new Player.Builder().withName("copy").build();

    TrainAction trainAction = new TrainAction(player, Skill.DIVINATION, 1500, 5000);

    TrainAction copied = trainAction.copyForPlayer(playerToCopy);

    assertEquals(trainAction.getSkill(), copied.getSkill());
    assertEquals(trainAction.getStartXp(), copied.getStartXp(), 0);
    assertEquals(trainAction.getEndXp(), copied.getEndXp(), 0);
    assertEquals(playerToCopy, copied.getPlayer());
  }
}
