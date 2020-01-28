package com.darrenswhite.rs.ironquest.path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.darrenswhite.rs.ironquest.player.HiscoreService;
import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.QuestEntry;
import com.darrenswhite.rs.ironquest.player.QuestPriority;
import com.darrenswhite.rs.ironquest.player.QuestStatus;
import com.darrenswhite.rs.ironquest.player.RuneMetricsService;
import com.darrenswhite.rs.ironquest.player.Skill;
import com.darrenswhite.rs.ironquest.quest.Quest.Builder;
import com.darrenswhite.rs.ironquest.quest.QuestAccessFilter;
import com.darrenswhite.rs.ironquest.quest.QuestService;
import com.darrenswhite.rs.ironquest.quest.QuestTypeFilter;
import com.darrenswhite.rs.ironquest.quest.Quests;
import com.darrenswhite.rs.ironquest.quest.requirement.QuestPointsRequirement;
import com.darrenswhite.rs.ironquest.quest.requirement.QuestRequirement;
import com.darrenswhite.rs.ironquest.quest.requirement.QuestRequirements;
import com.darrenswhite.rs.ironquest.quest.reward.LampReward;
import com.darrenswhite.rs.ironquest.quest.reward.LampType;
import com.darrenswhite.rs.ironquest.quest.reward.QuestRewards;
import com.darrenswhite.rs.ironquest.util.MapBuilder;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

public class PathFinderTest {

  private QuestService questService;
  private HiscoreService hiscoreService;
  private RuneMetricsService runeMetricsService;
  private PathFinder pathFinder;

  @Before
  public void setUp() {
    questService = mock(QuestService.class);
    hiscoreService = mock(HiscoreService.class);
    runeMetricsService = mock(RuneMetricsService.class);
    pathFinder = new PathFinder(questService, hiscoreService, runeMetricsService);
  }

  @Test
  public void find() throws BestQuestNotFoundException {
    Quests quests = new Quests(Collections.emptySet());

    when(questService.getQuests()).thenReturn(quests);

    Path path = pathFinder.find(null, QuestAccessFilter.ALL, false, false, Collections.emptySet(),
        Collections.emptyMap(), QuestTypeFilter.ALL);

    assertEquals(0, path.getActions().size());
    assertEquals(0, path.getStats().getPercentComplete(), 0);
  }

  @Test
  public void findForPlayer() throws BestQuestNotFoundException {
    QuestEntry questNotStarted = new QuestEntry(
        new Builder().withId(0).withTitle("questNotStarted").build(), QuestStatus.NOT_STARTED,
        QuestPriority.NORMAL);
    QuestEntry questCompleted = new QuestEntry(
        new Builder().withId(1).withTitle("questCompleted").build(), QuestStatus.COMPLETED,
        QuestPriority.NORMAL);
    QuestEntry questInProgress = new QuestEntry(
        new Builder().withId(2).withTitle("questInProgress").build(), QuestStatus.IN_PROGRESS,
        QuestPriority.NORMAL);
    Player player = new Player.Builder()
        .withQuests(new HashSet<>(Arrays.asList(questNotStarted, questCompleted, questInProgress)))
        .build();

    Path path = pathFinder.findForPlayer(player);

    assertEquals(2, path.getActions().size());
    assertEquals("questNotStarted", path.getActions().get(0).getMessage());
    assertEquals("questInProgress", path.getActions().get(1).getMessage());
    assertEquals(33, path.getStats().getPercentComplete(), 0);
  }

  @Test
  public void findForPlayer_ProcessFutureActions() throws BestQuestNotFoundException {
    QuestEntry questWithXpLampReward = new QuestEntry(
        new Builder().withId(0).withTitle("questWithXpLampReward").withRewards(
            new QuestRewards.Builder().withLamps(Collections.singleton(
                new LampReward.Builder().withType(LampType.XP).withXp(1000).withRequirements(
                    new MapBuilder<Set<Skill>, Integer>()
                        .put(Collections.singleton(Skill.ATTACK), 2).build()).build())).build())
            .build(), QuestStatus.NOT_STARTED, QuestPriority.NORMAL);
    QuestEntry questCompleted = new QuestEntry(
        new Builder().withId(1).withTitle("questCompleted").build(), QuestStatus.COMPLETED,
        QuestPriority.NORMAL);
    QuestEntry questWithQuestRequirementAndXpReward = new QuestEntry(
        new Builder().withId(2).withTitle("questWithXpReward").withRequirements(
            new QuestRequirements.Builder().withQuests(
                Collections.singleton(new QuestRequirement(questWithXpLampReward.getQuest())))
                .build()).withRewards(new QuestRewards.Builder()
            .withXp(new MapBuilder<Skill, Double>().put(Skill.ATTACK, 500d).build()).build())
            .build(), QuestStatus.NOT_STARTED, QuestPriority.NORMAL);
    Player player = new Player.Builder().withQuests(new HashSet<>(
        Arrays.asList(questWithXpLampReward, questCompleted, questWithQuestRequirementAndXpReward)))
        .build();

    Path path = pathFinder.findForPlayer(player);

    assertEquals(3, path.getActions().size());
    assertEquals("questWithXpLampReward", path.getActions().get(0).getMessage());
    assertEquals("questWithXpReward", path.getActions().get(1).getMessage());
    assertEquals("questWithXpLampReward: Use XP Lamp on Attack to gain 1k xp",
        path.getActions().get(2).getMessage());
    assertEquals(33, path.getStats().getPercentComplete(), 0);
  }

  @Test
  public void findForPlayer_AddFutureActions() throws BestQuestNotFoundException {
    QuestEntry questWithXpLampReward = new QuestEntry(
        new Builder().withId(0).withTitle("questWithXpLampReward").withRewards(
            new QuestRewards.Builder().withLamps(Collections.singleton(
                new LampReward.Builder().withType(LampType.XP).withXp(1000).withRequirements(
                    new MapBuilder<Set<Skill>, Integer>()
                        .put(Collections.singleton(Skill.ATTACK), 2).build()).build())).build())
            .build(), QuestStatus.NOT_STARTED, QuestPriority.NORMAL);
    QuestEntry questCompleted = new QuestEntry(
        new Builder().withId(1).withTitle("questCompleted").build(), QuestStatus.COMPLETED,
        QuestPriority.NORMAL);
    QuestEntry questNotStarted = new QuestEntry(
        new Builder().withId(2).withTitle("questNotStarted").build(), QuestStatus.NOT_STARTED,
        QuestPriority.NORMAL);
    Player player = new Player.Builder().withQuests(
        new HashSet<>(Arrays.asList(questWithXpLampReward, questCompleted, questNotStarted)))
        .build();

    Path path = pathFinder.findForPlayer(player);

    assertEquals(3, path.getActions().size());
    assertEquals("questWithXpLampReward", path.getActions().get(0).getMessage());
    assertEquals("questNotStarted", path.getActions().get(1).getMessage());
    assertEquals("questWithXpLampReward: Use XP Lamp to gain 1k xp (when requirements are met)",
        path.getActions().get(2).getMessage());
    assertTrue(path.getActions().get(2).isFuture());
    assertEquals(33, path.getStats().getPercentComplete(), 0);
  }

  @Test
  public void findForPlayer_PlaceholderQuests() throws BestQuestNotFoundException {
    QuestEntry placeholderQuestWithQuestPointReward = new QuestEntry(
        new Builder().withId(-1).withTitle("placeholderQuestWithQuestPointReward")
            .withRewards(new QuestRewards.Builder().withQuestPoints(1).build()).build(),
        QuestStatus.NOT_STARTED, QuestPriority.NORMAL);
    Player player = new Player.Builder()
        .withQuests(new HashSet<>(Collections.singletonList(placeholderQuestWithQuestPointReward)))
        .build();

    Path path = pathFinder.findForPlayer(player);

    assertEquals(0, path.getActions().size());
    assertEquals(0, path.getStats().getPercentComplete(), 0);
    assertEquals(1, player.getQuestPoints());
  }

  @Test(expected = BestQuestNotFoundException.class)
  public void findForPlayer_NoBestQuest() throws BestQuestNotFoundException {
    QuestEntry questWithQuestPointRequirement = new QuestEntry(
        new Builder().withId(0).withTitle("questWithQuestPointRequirement").withRequirements(
            new QuestRequirements.Builder().withQuestPoints(new QuestPointsRequirement(4)).build())
            .build(), QuestStatus.NOT_STARTED, QuestPriority.NORMAL);
    Player player = new Player.Builder()
        .withQuests(new HashSet<>(Collections.singletonList(questWithQuestPointRequirement)))
        .build();

    pathFinder.findForPlayer(player);
  }

  @Test
  public void createPlayer() {
    Quests quests = mock(Quests.class);
    Map<Integer, QuestPriority> questPriorities = Collections.emptyMap();
    QuestAccessFilter accessFilter = QuestAccessFilter.ALL;
    QuestTypeFilter typeFilter = QuestTypeFilter.ALL;
    String name = "username";

    when(questService.getQuests()).thenReturn(quests);

    Player player = pathFinder
        .createPlayer(name, accessFilter, false, false, Collections.emptySet(), questPriorities,
            typeFilter);

    assertEquals(name, player.getName());
    verify(quests).createQuestEntries(questPriorities, accessFilter, typeFilter);
    verify(hiscoreService).load(name);
    verify(runeMetricsService).load(name);
  }
}
