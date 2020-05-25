package com.darrenswhite.rs.ironquest.path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.QuestPriority;
import com.darrenswhite.rs.ironquest.quest.Quest;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class DefaultPathFinderAlgorithmTest {

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class NextQuest {

    @ParameterizedTest
    @MethodSource("shouldReturnHighestPriorityQuest")
    void shouldReturnHighestPriorityQuest(Map<Quest, QuestPriority> quests, int expectedId) {
      Player player = new Player.Builder().withQuests(quests.keySet()).build();
      DefaultPathFinderAlgorithm algorithm = new DefaultPathFinderAlgorithm(player);

      quests.forEach(player::setQuestPriority);

      assertThat(algorithm.hasNext(), equalTo(true));

      Quest next = algorithm.next();

      assertThat(next, notNullValue());
      assertThat(next.getId(), equalTo(expectedId));
    }

    Stream<Arguments> shouldReturnHighestPriorityQuest() {
      return Stream.of(Arguments.of(Map
          .of(new Quest.Builder(0).build(), QuestPriority.MINIMUM, new Quest.Builder(1).build(),
              QuestPriority.MAXIMUM), 1), Arguments.of(Map
          .of(new Quest.Builder(0).build(), QuestPriority.LOW, new Quest.Builder(1).build(),
              QuestPriority.MAXIMUM), 1), Arguments.of(Map
          .of(new Quest.Builder(0).build(), QuestPriority.NORMAL, new Quest.Builder(1).build(),
              QuestPriority.MAXIMUM), 1), Arguments.of(Map
          .of(new Quest.Builder(0).build(), QuestPriority.HIGH, new Quest.Builder(1).build(),
              QuestPriority.MAXIMUM), 1));
    }
  }
}