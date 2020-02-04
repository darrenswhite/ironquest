package com.darrenswhite.rs.ironquest.path;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.darrenswhite.rs.ironquest.player.HiscoreService;
import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.QuestEntry;
import com.darrenswhite.rs.ironquest.player.QuestPriority;
import com.darrenswhite.rs.ironquest.player.QuestStatus;
import com.darrenswhite.rs.ironquest.player.RuneMetricsService;
import com.darrenswhite.rs.ironquest.player.Skill;
import com.darrenswhite.rs.ironquest.quest.Quest;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class PathFinderTest {

  private QuestService questService;
  private HiscoreService hiscoreService;
  private RuneMetricsService runeMetricsService;
  private PathFinder pathFinder;

  @BeforeEach
  void setUp() {
    questService = mock(QuestService.class);
    hiscoreService = mock(HiscoreService.class);
    runeMetricsService = mock(RuneMetricsService.class);
    pathFinder = new PathFinder(questService, hiscoreService, runeMetricsService);
  }

  @Nested
  class Find {

    @Test
    void shouldCreatePlayerAndFindEmptyPathWhenGivenNoQuests() throws BestQuestNotFoundException {
      Quests quests = new Quests(Collections.emptySet());

      when(questService.getQuests()).thenReturn(quests);

      Path path = pathFinder.find(null, QuestAccessFilter.ALL, false, false, Collections.emptySet(),
          Collections.emptyMap(), QuestTypeFilter.ALL);

      assertThat(path.getActions(), empty());
      assertThat(path.getStats().getPercentComplete(), equalTo(0D));
    }
  }

  @Nested
  class FindForPlayer {

    @Test
    void findForPlayer() throws BestQuestNotFoundException {
      QuestEntry questNotStarted = new QuestEntry(
          new Quest.Builder().withId(0).withTitle("questNotStarted").build(),
          QuestStatus.NOT_STARTED, QuestPriority.NORMAL);
      QuestEntry questCompleted = new QuestEntry(
          new Quest.Builder().withId(1).withTitle("questCompleted").build(), QuestStatus.COMPLETED,
          QuestPriority.NORMAL);
      QuestEntry questInProgress = new QuestEntry(
          new Quest.Builder().withId(2).withTitle("questInProgress").build(),
          QuestStatus.IN_PROGRESS, QuestPriority.NORMAL);
      Player player = new Player.Builder().withQuests(
          new HashSet<>(Arrays.asList(questNotStarted, questCompleted, questInProgress))).build();

      Path path = pathFinder.findForPlayer(player);

      assertThat(path.getActions(), hasSize(2));
      assertThat(path.getActions().get(0).getMessage(), equalTo("questNotStarted"));
      assertThat(path.getActions().get(1).getMessage(), equalTo("questInProgress"));
      assertThat(path.getStats().getPercentComplete(), equalTo(33D));
    }

    @Test
    void shouldProcessFutureActions() throws BestQuestNotFoundException {
      QuestEntry questWithXpLampReward = new QuestEntry(
          new Quest.Builder().withId(0).withTitle("questWithXpLampReward").withRewards(
              new QuestRewards.Builder().withLamps(Collections.singleton(
                  new LampReward.Builder().withType(LampType.XP).withXp(1000).withRequirements(
                      new MapBuilder<Set<Skill>, Integer>()
                          .put(Collections.singleton(Skill.ATTACK), 2).build()).build())).build())
              .build(), QuestStatus.NOT_STARTED, QuestPriority.NORMAL);
      QuestEntry questCompleted = new QuestEntry(
          new Quest.Builder().withId(1).withTitle("questCompleted").build(), QuestStatus.COMPLETED,
          QuestPriority.NORMAL);
      QuestEntry questWithQuestRequirementAndXpReward = new QuestEntry(
          new Quest.Builder().withId(2).withTitle("questWithXpReward").withRequirements(
              new QuestRequirements.Builder().withQuests(Collections.singleton(
                  new QuestRequirement.Builder(questWithXpLampReward.getQuest()).build())).build())
              .withRewards(new QuestRewards.Builder()
                  .withXp(new MapBuilder<Skill, Double>().put(Skill.ATTACK, 500d).build()).build())
              .build(), QuestStatus.NOT_STARTED, QuestPriority.NORMAL);
      Player player = new Player.Builder().withQuests(new HashSet<>(Arrays
          .asList(questWithXpLampReward, questCompleted, questWithQuestRequirementAndXpReward)))
          .build();

      Path path = pathFinder.findForPlayer(player);

      assertThat(path.getActions(), hasSize(3));
      assertThat(path.getActions().get(0).getMessage(), equalTo("questWithXpLampReward"));
      assertThat(path.getActions().get(1).getMessage(), equalTo("questWithXpReward"));
      assertThat(path.getActions().get(2).getMessage(),
          equalTo("questWithXpLampReward: Use XP Lamp on Attack to gain 1k xp"));
      assertThat(path.getStats().getPercentComplete(), equalTo(33D));
    }

    @Test
    void shouldAddFutureActions() throws BestQuestNotFoundException {
      QuestEntry questWithXpLampReward = new QuestEntry(
          new Quest.Builder().withId(0).withTitle("questWithXpLampReward").withRewards(
              new QuestRewards.Builder().withLamps(Collections.singleton(
                  new LampReward.Builder().withType(LampType.XP).withXp(1000).withRequirements(
                      new MapBuilder<Set<Skill>, Integer>()
                          .put(Collections.singleton(Skill.ATTACK), 2).build()).build())).build())
              .build(), QuestStatus.NOT_STARTED, QuestPriority.NORMAL);
      QuestEntry questCompleted = new QuestEntry(
          new Quest.Builder().withId(1).withTitle("questCompleted").build(), QuestStatus.COMPLETED,
          QuestPriority.NORMAL);
      QuestEntry questNotStarted = new QuestEntry(
          new Quest.Builder().withId(2).withTitle("questNotStarted").build(),
          QuestStatus.NOT_STARTED, QuestPriority.NORMAL);
      Player player = new Player.Builder().withQuests(
          new HashSet<>(Arrays.asList(questWithXpLampReward, questCompleted, questNotStarted)))
          .build();

      Path path = pathFinder.findForPlayer(player);

      assertThat(path.getActions(), hasSize(3));
      assertThat(path.getActions().get(0).getMessage(), equalTo("questWithXpLampReward"));
      assertThat(path.getActions().get(1).getMessage(), equalTo("questNotStarted"));
      assertThat(path.getActions().get(2).getMessage(),
          equalTo("questWithXpLampReward: Use XP Lamp to gain 1k xp (when requirements are met)"));
      assertThat(path.getActions().get(2).isFuture(), equalTo(true));
      assertThat(path.getStats().getPercentComplete(), equalTo(33D));
    }

    @Test
    void shouldCompletePlaceholderQuests() throws BestQuestNotFoundException {
      QuestEntry placeholderQuestWithQuestPointReward = new QuestEntry(
          new Quest.Builder().withId(-1).withTitle("placeholderQuestWithQuestPointReward")
              .withRewards(new QuestRewards.Builder().withQuestPoints(1).build()).build(),
          QuestStatus.NOT_STARTED, QuestPriority.NORMAL);
      Player player = new Player.Builder().withQuests(
          new HashSet<>(Collections.singletonList(placeholderQuestWithQuestPointReward))).build();

      Path path = pathFinder.findForPlayer(player);

      assertThat(path.getActions(), empty());
      assertThat(path.getStats().getPercentComplete(), equalTo(0D));
      assertThat(player.getQuestPoints(), equalTo(1));
    }

    @Test
    void shouldThrowExceptionWhenBestQuestNotFound() {
      QuestEntry questWithQuestPointRequirement = new QuestEntry(
          new Quest.Builder().withId(0).withTitle("questWithQuestPointRequirement")
              .withRequirements(new QuestRequirements.Builder()
                  .withQuestPoints(new QuestPointsRequirement.Builder(4).build()).build()).build(),
          QuestStatus.NOT_STARTED, QuestPriority.NORMAL);
      Player player = new Player.Builder()
          .withQuests(new HashSet<>(Collections.singletonList(questWithQuestPointRequirement)))
          .build();

      assertThrows(BestQuestNotFoundException.class, () -> pathFinder.findForPlayer(player));
    }
  }

  @Nested
  class CreatePlayer {

    @Test
    void shouldCreateQuestEntriesAndLoadPlayerDataFromHiscoresAndRuneMetricsWhenGivenUsername() {
      Quests quests = mock(Quests.class);
      Map<Integer, QuestPriority> questPriorities = Collections.emptyMap();
      QuestAccessFilter accessFilter = QuestAccessFilter.ALL;
      QuestTypeFilter typeFilter = QuestTypeFilter.ALL;
      String name = "username";

      when(questService.getQuests()).thenReturn(quests);

      Player player = pathFinder
          .createPlayer(name, accessFilter, false, false, Collections.emptySet(), questPriorities,
              typeFilter);

      assertThat(player.getName(), equalTo(name));
      verify(quests).createQuestEntries(questPriorities, accessFilter, typeFilter);
      verify(hiscoreService).load(name);
      verify(runeMetricsService).load(name);
    }

    @Test
    void shouldNotLoadPlayerDataFromHiscoresAndRuneMetricsWhenUsernameIsNull() {
      Quests quests = mock(Quests.class);
      Map<Integer, QuestPriority> questPriorities = Collections.emptyMap();
      QuestAccessFilter accessFilter = QuestAccessFilter.ALL;
      QuestTypeFilter typeFilter = QuestTypeFilter.ALL;

      when(questService.getQuests()).thenReturn(quests);

      pathFinder
          .createPlayer(null, accessFilter, false, false, Collections.emptySet(), questPriorities,
              typeFilter);

      verify(hiscoreService, never()).load(any());
      verify(runeMetricsService, never()).load(any());
    }

    @Test
    void shouldNotLoadPlayerDataFromHiscoresAndRuneMetricsWhenUsernameIsEmpty() {
      Quests quests = mock(Quests.class);
      Map<Integer, QuestPriority> questPriorities = Collections.emptyMap();
      QuestAccessFilter accessFilter = QuestAccessFilter.ALL;
      QuestTypeFilter typeFilter = QuestTypeFilter.ALL;

      when(questService.getQuests()).thenReturn(quests);

      pathFinder
          .createPlayer("", accessFilter, false, false, Collections.emptySet(), questPriorities,
              typeFilter);

      verify(hiscoreService, never()).load(any());
      verify(runeMetricsService, never()).load(any());
    }
  }
}
