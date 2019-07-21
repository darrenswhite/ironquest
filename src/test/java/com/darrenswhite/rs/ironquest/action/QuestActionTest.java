package com.darrenswhite.rs.ironquest.action;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.darrenswhite.rs.ironquest.dto.QuestActionDTO;
import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.QuestEntry;
import com.darrenswhite.rs.ironquest.player.QuestPriority;
import com.darrenswhite.rs.ironquest.player.QuestStatus;
import com.darrenswhite.rs.ironquest.player.Skill;
import com.darrenswhite.rs.ironquest.quest.Quest;
import com.darrenswhite.rs.ironquest.quest.reward.QuestRewards;
import com.darrenswhite.rs.ironquest.quest.reward.QuestRewards.Builder;
import com.darrenswhite.rs.ironquest.util.MapBuilder;
import java.util.Map;
import org.junit.Test;

public class QuestActionTest {

  @Test
  public void getType() {
    Quest quest = new Quest.Builder().build();
    QuestEntry entry = new QuestEntry(quest, QuestStatus.NOT_STARTED, QuestPriority.NORMAL);
    Player player = new Player.Builder().build();

    QuestAction questAction = new QuestAction(player, entry);

    assertEquals(ActionType.QUEST, questAction.getType());
  }

  @Test
  public void getMessage() {
    String title = "title";
    Quest quest = new Quest.Builder().withTitle(title).build();
    QuestEntry entry = new QuestEntry(quest, QuestStatus.NOT_STARTED, QuestPriority.NORMAL);
    Player player = new Player.Builder().build();

    QuestAction questAction = new QuestAction(player, entry);

    assertEquals(title, questAction.getMessage());
    assertEquals(questAction.getMessage(), questAction.toString());
  }

  @Test
  public void meetsRequirements() {
    Quest quest = mock(Quest.class);
    QuestEntry entry = new QuestEntry(quest, QuestStatus.NOT_STARTED, QuestPriority.NORMAL);
    Player player = new Player.Builder().build();

    QuestAction questAction = new QuestAction(player, entry);

    questAction.meetsRequirements(player);

    verify(quest).meetsAllRequirements(player);
  }

  @Test
  public void process() {
    Map<Skill, Double> xp = new MapBuilder<Skill, Double>().put(Skill.ATTACK, 5000d)
        .put(Skill.AGILITY, 2500d).put(Skill.COOKING, 100d).build();
    QuestRewards rewards = new Builder().withXp(xp).build();
    Quest quest = new Quest.Builder().withRewards(rewards).build();
    QuestEntry entry = new QuestEntry(quest, QuestStatus.NOT_STARTED, QuestPriority.NORMAL);
    Player player = new Player.Builder().build();

    QuestAction questAction = new QuestAction(player, entry);

    questAction.process(player);

    assertEquals(QuestStatus.COMPLETED, entry.getStatus());
    assertEquals(5000, player.getXp(Skill.ATTACK), 0);
    assertEquals(2500, player.getXp(Skill.AGILITY), 0);
    assertEquals(100, player.getXp(Skill.COOKING), 0);
  }

  @Test
  public void createDTO() {
    String title = "title";
    Quest quest = new Quest.Builder().withTitle(title).build();
    QuestEntry entry = new QuestEntry(quest, QuestStatus.NOT_STARTED, QuestPriority.NORMAL);
    Player player = new Player.Builder().build();

    QuestAction questAction = new QuestAction(player, entry);

    QuestActionDTO dto = questAction.createDTO();

    assertEquals(questAction.getMessage(), dto.getMessage());
    assertEquals(quest.createDTO(), dto.getQuest());
    assertEquals(player.createDTO(), dto.getPlayer());
    assertEquals(ActionType.QUEST, dto.getType());
  }

  @Test
  public void copyForPlayer() {
    Quest quest = new Quest.Builder().build();
    QuestEntry entry = new QuestEntry(quest, QuestStatus.NOT_STARTED, QuestPriority.NORMAL);
    Player player = new Player.Builder().withName("original").build();
    Player playerToCopy = new Player.Builder().withName("copy").build();

    QuestAction questAction = new QuestAction(player, entry);

    QuestAction copied = questAction.copyForPlayer(playerToCopy);

    assertEquals(entry, copied.getQuestEntry());
    assertEquals(playerToCopy, copied.getPlayer());
  }
}
