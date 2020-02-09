package com.darrenswhite.rs.ironquest.quest.requirement;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.QuestEntry;
import com.darrenswhite.rs.ironquest.player.QuestPriority;
import com.darrenswhite.rs.ironquest.player.QuestStatus;
import com.darrenswhite.rs.ironquest.quest.Quest;
import com.darrenswhite.rs.ironquest.quest.reward.QuestRewards;
import java.util.Collections;
import java.util.stream.Stream;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class QuestPointsRequirementTest {

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class TestPlayer {

    @ParameterizedTest
    @MethodSource("shouldMeetRequirement")
    void shouldMeetRequirement(int questPointsRequired, int playerQuestPoints) {
      QuestEntry quest = new QuestEntry(new Quest.Builder()
          .withRewards(new QuestRewards.Builder().withQuestPoints(playerQuestPoints).build())
          .build(), QuestStatus.COMPLETED, QuestPriority.NORMAL);
      Player player = new Player.Builder().withQuests(Collections.singleton(quest)).build();

      QuestPointsRequirement questPointsRequirement = new QuestPointsRequirement.Builder(
          questPointsRequired).build();

      assertThat(questPointsRequirement.testPlayer(player), equalTo(true));
    }

    Stream<Arguments> shouldMeetRequirement() {
      return Stream
          .of(Arguments.of(0, 0), Arguments.of(0, 1), Arguments.of(99, 99), Arguments.of(100, 101),
              Arguments.of(0, 300));
    }

    @ParameterizedTest
    @MethodSource("shouldNotMeetRequirement")
    void shouldNotMeetRequirement(int questPointsRequired, int playerQuestPoints) {
      QuestEntry quest = new QuestEntry(new Quest.Builder()
          .withRewards(new QuestRewards.Builder().withQuestPoints(playerQuestPoints).build())
          .build(), QuestStatus.COMPLETED, QuestPriority.NORMAL);
      Player player = new Player.Builder().withQuests(Collections.singleton(quest)).build();

      QuestPointsRequirement questPointsRequirement = new QuestPointsRequirement.Builder(
          questPointsRequired).build();

      assertThat(questPointsRequirement.testPlayer(player), equalTo(false));
    }

    Stream<Arguments> shouldNotMeetRequirement() {
      return Stream
          .of(Arguments.of(1, 0), Arguments.of(2, 1), Arguments.of(100, 99), Arguments.of(102, 101),
              Arguments.of(500, 300));
    }
  }

  @Nested
  class Equals {

    @Test
    void shouldVerifyEqualsAndHashCode() {
      EqualsVerifier.forClass(QuestPointsRequirement.class).verify();
    }
  }
}
