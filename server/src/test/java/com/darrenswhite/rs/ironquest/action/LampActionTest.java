package com.darrenswhite.rs.ironquest.action;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.darrenswhite.rs.ironquest.dto.LampActionDTO;
import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.QuestEntry;
import com.darrenswhite.rs.ironquest.player.QuestPriority;
import com.darrenswhite.rs.ironquest.player.QuestStatus;
import com.darrenswhite.rs.ironquest.player.Skill;
import com.darrenswhite.rs.ironquest.quest.Quest;
import com.darrenswhite.rs.ironquest.quest.reward.LampReward;
import com.darrenswhite.rs.ironquest.quest.reward.LampType;
import com.darrenswhite.rs.ironquest.util.MapBuilder;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.junit.Test;

public class LampActionTest {

  @Test
  public void getType() {
    Quest quest = new Quest.Builder().build();
    QuestEntry entry = new QuestEntry(quest, QuestStatus.NOT_STARTED, QuestPriority.NORMAL);
    Player player = new Player.Builder().build();
    LampReward lampReward = new LampReward.Builder().build();
    Set<Skill> skills = Collections.emptySet();

    LampAction lampAction = new LampAction(player, false, entry, lampReward, skills);

    assertThat(lampAction.getType(), equalTo(ActionType.LAMP));
  }

  @Test
  public void getMessage() {
    String title = "title";
    Quest quest = new Quest.Builder().withTitle(title).build();
    QuestEntry entry = new QuestEntry(quest, QuestStatus.NOT_STARTED, QuestPriority.NORMAL);
    Player player = new Player.Builder().build();
    LampReward lampReward = new LampReward.Builder().withType(LampType.XP).withXp(500).build();
    Set<Skill> skills = Collections.singleton(Skill.ATTACK);

    LampAction lampAction = new LampAction(player, false, entry, lampReward, skills);

    assertThat(lampAction.getMessage(), equalTo(title + ": Use XP Lamp on Attack to gain 500 xp"));
    assertThat(lampAction.toString(), equalTo(lampAction.getMessage()));
  }

  @Test
  public void getMessage_Future() {
    String title = "title";
    Quest quest = new Quest.Builder().withTitle(title).build();
    QuestEntry entry = new QuestEntry(quest, QuestStatus.NOT_STARTED, QuestPriority.NORMAL);
    Player player = new Player.Builder().build();
    LampReward lampReward = new LampReward.Builder().withType(LampType.XP).withXp(1000).build();
    Set<Skill> skills = Collections.emptySet();

    LampAction lampAction = new LampAction(player, true, entry, lampReward, skills);

    assertThat(lampAction.getMessage(),
        equalTo(title + ": Use XP Lamp to gain 1k xp (when requirements are met)"));
    assertThat(lampAction.toString(), equalTo(lampAction.getMessage()));
  }

  @Test
  public void getMessage_SmallXp() {
    String title = "title";
    Quest quest = new Quest.Builder().withTitle(title).build();
    QuestEntry entry = new QuestEntry(quest, QuestStatus.NOT_STARTED, QuestPriority.NORMAL);
    Map<Skill, Double> skillXps = new MapBuilder<Skill, Double>().put(Skill.DEFENCE, 50000d)
        .build();
    Player player = new Player.Builder().withSkillXps(skillXps).build();
    LampReward lampReward = new LampReward.Builder().withType(LampType.SMALL_XP).build();
    Set<Skill> skills = Collections.singleton(Skill.DEFENCE);

    LampAction lampAction = new LampAction(player, false, entry, lampReward, skills);

    assertThat(lampAction.getMessage(),
        equalTo(title + ": Use Small XP Lamp on Defence to gain 784 xp"));
    assertThat(lampAction.toString(), equalTo(lampAction.getMessage()));
  }

  @Test
  public void getMessage_MediumXp() {
    String title = "title";
    Quest quest = new Quest.Builder().withTitle(title).build();
    QuestEntry entry = new QuestEntry(quest, QuestStatus.NOT_STARTED, QuestPriority.NORMAL);
    Map<Skill, Double> skillXps = new MapBuilder<Skill, Double>().put(Skill.MAGIC, 750000d).build();
    Player player = new Player.Builder().withSkillXps(skillXps).build();
    LampReward lampReward = new LampReward.Builder().withType(LampType.MEDIUM_XP).build();
    Set<Skill> skills = Collections.singleton(Skill.MAGIC);

    LampAction lampAction = new LampAction(player, false, entry, lampReward, skills);

    assertThat(lampAction.getMessage(),
        equalTo(title + ": Use Medium XP Lamp on Magic to gain 5.185k xp"));
    assertThat(lampAction.toString(), equalTo(lampAction.getMessage()));
  }

  @Test
  public void getMessage_LargeXp() {
    String title = "title";
    Quest quest = new Quest.Builder().withTitle(title).build();
    QuestEntry entry = new QuestEntry(quest, QuestStatus.NOT_STARTED, QuestPriority.NORMAL);
    Map<Skill, Double> skillXps = new MapBuilder<Skill, Double>().put(Skill.RANGED, 1000000d)
        .build();
    Player player = new Player.Builder().withSkillXps(skillXps).build();
    LampReward lampReward = new LampReward.Builder().withType(LampType.LARGE_XP).build();
    Set<Skill> skills = Collections.singleton(Skill.RANGED);

    LampAction lampAction = new LampAction(player, false, entry, lampReward, skills);

    assertThat(lampAction.getMessage(),
        equalTo(title + ": Use Large XP Lamp on Ranged to gain 11.786k xp"));
    assertThat(lampAction.toString(), equalTo(lampAction.getMessage()));
  }

  @Test
  public void getMessage_HugeXp() {
    String title = "title";
    Quest quest = new Quest.Builder().withTitle(title).build();
    QuestEntry entry = new QuestEntry(quest, QuestStatus.NOT_STARTED, QuestPriority.NORMAL);
    Map<Skill, Double> skillXps = new MapBuilder<Skill, Double>().put(Skill.THIEVING, 5000000d)
        .build();
    Player player = new Player.Builder().withSkillXps(skillXps).build();
    LampReward lampReward = new LampReward.Builder().withType(LampType.HUGE_XP).build();
    Set<Skill> skills = Collections.singleton(Skill.THIEVING);

    LampAction lampAction = new LampAction(player, false, entry, lampReward, skills);

    assertThat(lampAction.getMessage(),
        equalTo(title + ": Use Huge XP Lamp on Thieving to gain 47.38k xp"));
    assertThat(lampAction.toString(), equalTo(lampAction.getMessage()));
  }

  @Test
  public void getMessage_Dragonkin() {
    String title = "title";
    Quest quest = new Quest.Builder().withTitle(title).build();
    QuestEntry entry = new QuestEntry(quest, QuestStatus.NOT_STARTED, QuestPriority.NORMAL);
    Map<Skill, Double> skillXps = new MapBuilder<Skill, Double>().put(Skill.HERBLORE, 9000000d)
        .build();
    Player player = new Player.Builder().withSkillXps(skillXps).build();
    LampReward lampReward = new LampReward.Builder().withType(LampType.DRAGONKIN).build();
    Set<Skill> skills = Collections.singleton(Skill.HERBLORE);

    LampAction lampAction = new LampAction(player, false, entry, lampReward, skills);

    assertThat(lampAction.getMessage(),
        equalTo(title + ": Use Dragonkin Lamp on Herblore to gain 41.115k xp"));
    assertThat(lampAction.toString(), equalTo(lampAction.getMessage()));
  }

  @Test
  public void getMessage_MultipleSkills() {
    String title = "title";
    Quest quest = new Quest.Builder().withTitle(title).build();
    QuestEntry entry = new QuestEntry(quest, QuestStatus.NOT_STARTED, QuestPriority.NORMAL);
    Player player = new Player.Builder().build();
    LampReward lampReward = new LampReward.Builder().withType(LampType.XP).withXp(150).build();
    Set<Skill> skills = new LinkedHashSet<>(Arrays.asList(Skill.SUMMONING, Skill.DIVINATION));

    LampAction lampAction = new LampAction(player, false, entry, lampReward, skills);

    assertThat(lampAction.getMessage(),
        equalTo(title + ": Use XP Lamp on Summoning, Divination to gain 150 xp"));
    assertThat(lampAction.toString(), equalTo(lampAction.getMessage()));
  }

  @Test
  public void getMessage_Multiplier() {
    String title = "title";
    Quest quest = new Quest.Builder().withTitle(title).build();
    QuestEntry entry = new QuestEntry(quest, QuestStatus.NOT_STARTED, QuestPriority.NORMAL);
    Player player = new Player.Builder().build();
    LampReward lampReward = new LampReward.Builder().withType(LampType.XP).withXp(125)
        .withMultiplier(1.5).build();
    Set<Skill> skills = Collections.singleton(Skill.CONSTRUCTION);

    LampAction lampAction = new LampAction(player, false, entry, lampReward, skills);

    assertThat(lampAction.getMessage(),
        equalTo(title + ": Use XP Lamp on Construction to gain 187.5 xp"));
    assertThat(lampAction.toString(), equalTo(lampAction.getMessage()));
  }

  @Test
  public void meetsRequirements() {
    Quest quest = new Quest.Builder().build();
    QuestEntry entry = new QuestEntry(quest, QuestStatus.NOT_STARTED, QuestPriority.NORMAL);
    Player player = new Player.Builder().build();
    LampReward lampReward = mock(LampReward.class);
    Set<Skill> skills = Collections.emptySet();

    LampAction lampAction = new LampAction(player, false, entry, lampReward, skills);

    lampAction.meetsRequirements(player);

    verify(lampReward).meetsRequirements(player);
  }

  @Test
  public void process() {
    Quest quest = new Quest.Builder().build();
    QuestEntry entry = new QuestEntry(quest, QuestStatus.NOT_STARTED, QuestPriority.NORMAL);
    Player player = new Player.Builder().build();
    LampReward lampReward = new LampReward.Builder().withType(LampType.XP).withXp(1000).build();
    Set<Skill> skills = new HashSet<>(Arrays.asList(Skill.DEFENCE, Skill.STRENGTH));

    LampAction lampAction = new LampAction(player, false, entry, lampReward, skills);

    lampAction.process(player);

    assertThat(player.getXp(Skill.DEFENCE), equalTo(1000D));
    assertThat(player.getXp(Skill.STRENGTH), equalTo(1000D));
  }

  @Test
  public void createDTO() {
    String title = "title";
    Quest quest = new Quest.Builder().withTitle(title).build();
    QuestEntry entry = new QuestEntry(quest, QuestStatus.NOT_STARTED, QuestPriority.NORMAL);
    Player player = new Player.Builder().build();
    LampReward lampReward = new LampReward.Builder().withType(LampType.XP).withXp(500).build();
    Set<Skill> skills = Collections.singleton(Skill.PRAYER);

    LampAction lampAction = new LampAction(player, false, entry, lampReward, skills);

    LampActionDTO dto = lampAction.createDTO();

    assertThat(dto.getMessage(), equalTo(lampAction.getMessage()));
    assertThat(dto.getQuest(), equalTo(quest.createDTO()));
    assertThat(dto.getPlayer(), equalTo(player.createDTO()));
    assertThat(dto.getType(), equalTo(ActionType.LAMP));
    assertThat(dto.isFuture(), equalTo(false));
  }

  @Test
  public void copyForPlayer() {
    Quest quest = new Quest.Builder().build();
    QuestEntry entry = new QuestEntry(quest, QuestStatus.NOT_STARTED, QuestPriority.NORMAL);
    Player player = new Player.Builder().withName("original").build();
    Player playerToCopy = new Player.Builder().withName("copy").build();

    LampReward lampReward = new LampReward.Builder().build();
    Set<Skill> skills = Collections.singleton(Skill.ATTACK);

    LampAction lampAction = new LampAction(player, false, entry, lampReward, skills);

    LampAction copied = lampAction.copyForPlayer(playerToCopy);

    assertThat(copied.getQuestEntry(), equalTo(entry));
    assertThat(copied.getLampReward(), equalTo(lampReward));
    assertThat(copied.getSkills(), equalTo(skills));
    assertThat(copied.getPlayer(), equalTo(playerToCopy));
  }
}
