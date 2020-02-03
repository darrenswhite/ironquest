package com.darrenswhite.rs.ironquest.action;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
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

    assertThat(questAction.getType(), equalTo(ActionType.QUEST));
  }

  @Test
  public void getMessage() {
    String title = "title";
    Quest quest = new Quest.Builder().withTitle(title).build();
    QuestEntry entry = new QuestEntry(quest, QuestStatus.NOT_STARTED, QuestPriority.NORMAL);
    Player player = new Player.Builder().build();

    QuestAction questAction = new QuestAction(player, entry);

    assertThat(questAction.getMessage(), equalTo(title));
    assertThat(questAction.toString(), equalTo(questAction.getMessage()));
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

    assertThat(entry.getStatus(), equalTo(QuestStatus.COMPLETED));
    assertThat(player.getXp(Skill.ATTACK), equalTo(5000D));
    assertThat(player.getXp(Skill.AGILITY), equalTo(2500D));
    assertThat(player.getXp(Skill.COOKING), equalTo(100D));
  }

  @Test
  public void createDTO() {
    String title = "title";
    Quest quest = new Quest.Builder().withTitle(title).build();
    QuestEntry entry = new QuestEntry(quest, QuestStatus.NOT_STARTED, QuestPriority.NORMAL);
    Player player = new Player.Builder().build();

    QuestAction questAction = new QuestAction(player, entry);

    QuestActionDTO dto = questAction.createDTO();

    assertThat(dto.getMessage(), equalTo(questAction.getMessage()));
    assertThat(dto.getQuest(), equalTo(quest.createDTO()));
    assertThat(dto.getPlayer(), equalTo(player.createDTO()));
    assertThat(dto.getType(), equalTo(ActionType.QUEST));
    assertThat(dto.isFuture(), equalTo(false));
  }

  @Test
  public void copyForPlayer() {
    Quest quest = new Quest.Builder().build();
    QuestEntry entry = new QuestEntry(quest, QuestStatus.NOT_STARTED, QuestPriority.NORMAL);
    Player player = new Player.Builder().withName("original").build();
    Player playerToCopy = new Player.Builder().withName("copy").build();

    QuestAction questAction = new QuestAction(player, entry);

    QuestAction copied = questAction.copyForPlayer(playerToCopy);

    assertThat(copied.getQuestEntry(), equalTo(entry));
    assertThat(copied.getPlayer(), equalTo(playerToCopy));
  }
}
