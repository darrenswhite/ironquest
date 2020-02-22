package com.darrenswhite.rs.ironquest.player;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.darrenswhite.rs.ironquest.quest.QuestAccess;
import com.darrenswhite.rs.ironquest.quest.QuestAccessFilter;
import com.darrenswhite.rs.ironquest.quest.QuestService;
import com.darrenswhite.rs.ironquest.quest.QuestType;
import com.darrenswhite.rs.ironquest.quest.QuestTypeFilter;
import com.darrenswhite.rs.ironquest.util.MapBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Objects;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.InputStreamResource;

class PlayerServiceTest {

  static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  static final String QUESTS_FILE = "quests-minimal.json";

  static QuestService questService;
  static HiscoreService hiscoreService;
  static RuneMetricsService runeMetricsService;
  static PlayerService playerService;

  @BeforeAll
  static void beforeAll() throws IOException {
    InputStream in = PlayerServiceTest.class.getClassLoader().getResourceAsStream(QUESTS_FILE);

    questService = new QuestService(new InputStreamResource(Objects.requireNonNull(in)),
        OBJECT_MAPPER);
    hiscoreService = mock(HiscoreService.class);
    runeMetricsService = mock(RuneMetricsService.class);

    playerService = new PlayerService(questService, hiscoreService, runeMetricsService);
  }

  @Nested
  class CreatePlayer {

    @Test
    void shouldSetQuestPriority() {
      QuestPriority priorityA = QuestPriority.MAXIMUM;
      QuestPriority priorityB = QuestPriority.NORMAL;
      QuestPriority priorityC = QuestPriority.LOW;
      QuestPriority priorityD = QuestPriority.HIGH;

      Player player = playerService
          .createPlayer(null, QuestAccessFilter.ALL, QuestTypeFilter.ALL, false, false,
              Collections.emptySet(),
              new MapBuilder<Integer, QuestPriority>().put(-1, priorityA).put(0, priorityB)
                  .put(1, priorityC).put(2, priorityD).build());

      assertThat(player.getQuestPriority(-1), equalTo(priorityA));
      assertThat(player.getQuestPriority(0), equalTo(priorityB));
      assertThat(player.getQuestPriority(1), equalTo(priorityC));
      assertThat(player.getQuestPriority(2), equalTo(priorityD));
    }

    @Test
    void shouldFilterByFreeQuests() {
      Player player = playerService
          .createPlayer(null, QuestAccessFilter.FREE, QuestTypeFilter.ALL, false, false,
              Collections.emptySet(), Collections.emptyMap());

      assertThat(player.getQuests(), notNullValue());
      assertThat(player.getQuests(), hasSize(2));
      assertThat(player.getQuests(), everyItem(hasProperty("access", equalTo(QuestAccess.FREE))));
    }

    @Test
    void shouldFilterByMembersQuests() {
      Player player = playerService
          .createPlayer(null, QuestAccessFilter.MEMBERS, QuestTypeFilter.ALL, false, false,
              Collections.emptySet(), Collections.emptyMap());

      assertThat(player.getQuests(), notNullValue());
      // quest requirements are also included
      assertThat(player.getQuests(), hasSize(3));
      assertThat(player.getQuests(), containsInAnyOrder(
          allOf(hasProperty("id", equalTo(0)), hasProperty("access", equalTo(QuestAccess.FREE))),
          allOf(hasProperty("id", equalTo(1)), hasProperty("access", equalTo(QuestAccess.MEMBERS))),
          allOf(hasProperty("id", equalTo(2)),
              hasProperty("access", equalTo(QuestAccess.MEMBERS)))));
    }

    @Test
    void shouldFilterByMiniquests() {
      Player player = playerService
          .createPlayer(null, QuestAccessFilter.ALL, QuestTypeFilter.MINIQUESTS, false, false,
              Collections.emptySet(), Collections.emptyMap());

      assertThat(player.getQuests(), notNullValue());
      // quest requirements are also included
      assertThat(player.getQuests(), hasSize(3));
      assertThat(player.getQuests(), containsInAnyOrder(
          allOf(hasProperty("id", equalTo(0)), hasProperty("type", equalTo(QuestType.SAGA))),
          allOf(hasProperty("id", equalTo(1)), hasProperty("type", equalTo(QuestType.SAGA))),
          allOf(hasProperty("id", equalTo(2)), hasProperty("type", equalTo(QuestType.MINIQUEST)))));
    }

    @Test
    void shouldFilterBySagas() {
      Player player = playerService
          .createPlayer(null, QuestAccessFilter.ALL, QuestTypeFilter.SAGAS, false, false,
              Collections.emptySet(), Collections.emptyMap());

      assertThat(player.getQuests(), notNullValue());
      assertThat(player.getQuests(), hasSize(2));
      assertThat(player.getQuests(), containsInAnyOrder(
          allOf(hasProperty("id", equalTo(0)), hasProperty("type", equalTo(QuestType.SAGA))),
          allOf(hasProperty("id", equalTo(1)), hasProperty("type", equalTo(QuestType.SAGA)))));
    }

    @Test
    void shouldFilterByQuests() {
      Player player = playerService
          .createPlayer(null, QuestAccessFilter.ALL, QuestTypeFilter.QUESTS, false, false,
              Collections.emptySet(), Collections.emptyMap());

      assertThat(player.getQuests(), notNullValue());
      assertThat(player.getQuests(), hasSize(1));
      assertThat(player.getQuests(), containsInAnyOrder(
          allOf(hasProperty("id", equalTo(-1)), hasProperty("type", equalTo(QuestType.QUEST)))));
    }

    @Test
    void shouldLoadPlayerDataFromHiscoresAndRuneMetricsWhenGivenUsername() {
      playerService
          .createPlayer("username", QuestAccessFilter.ALL, QuestTypeFilter.ALL, false, false,
              Collections.emptySet(), Collections.emptyMap());

      verify(hiscoreService).load("username");
      verify(runeMetricsService).load("username");
    }

    @Test
    void shouldNotLoadPlayerDataFromHiscoresAndRuneMetricsWhenUsernameIsNull() {
      playerService.createPlayer(null, QuestAccessFilter.ALL, QuestTypeFilter.ALL, false, false,
          Collections.emptySet(), Collections.emptyMap());

      verify(hiscoreService, never()).load(any());
      verify(runeMetricsService, never()).load(any());
    }

    @Test
    void shouldNotLoadPlayerDataFromHiscoresAndRuneMetricsWhenUsernameIsEmpty() {
      playerService.createPlayer("", QuestAccessFilter.ALL, QuestTypeFilter.ALL, false, false,
          Collections.emptySet(), Collections.emptyMap());

      verify(hiscoreService, never()).load(any());
      verify(runeMetricsService, never()).load(any());
    }
  }
}