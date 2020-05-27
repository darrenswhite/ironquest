package com.darrenswhite.rs.ironquest.path.algorithm;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.QuestPriority;
import com.darrenswhite.rs.ironquest.quest.Quest;
import com.darrenswhite.rs.ironquest.quest.Quest.Builder;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class DefaultAlgorithmTest {

  static final DefaultAlgorithm algorithm = new DefaultAlgorithm();

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class NextQuest {

    @ParameterizedTest
    @MethodSource("shouldReturnHighestPriorityQuest")
    void shouldReturnHighestPriorityQuest(Quest first, QuestPriority firstPriority, Quest second,
        QuestPriority secondPriority, int expected) {
      Player player = new Player.Builder().withQuests(Set.of(first, second)).build();

      player.setQuestPriority(first, firstPriority);
      player.setQuestPriority(second, secondPriority);

      int compare = algorithm.comparator(player).compare(first, second);

      assertThat(compare, equalTo(expected));
    }

    Stream<Arguments> shouldReturnHighestPriorityQuest() {
      Quest first = new Builder(0).build();
      Quest second = new Builder(1).build();

      return Stream.of(Arguments.of(first, QuestPriority.MINIMUM, second, QuestPriority.MAXIMUM, 4),
          Arguments.of(first, QuestPriority.LOW, second, QuestPriority.MAXIMUM, 3),
          Arguments.of(first, QuestPriority.NORMAL, second, QuestPriority.MAXIMUM, 2),
          Arguments.of(first, QuestPriority.HIGH, second, QuestPriority.MAXIMUM, 1),

          Arguments.of(first, QuestPriority.MAXIMUM, second, QuestPriority.MINIMUM, -4),
          Arguments.of(first, QuestPriority.MAXIMUM, second, QuestPriority.LOW, -3),
          Arguments.of(first, QuestPriority.MAXIMUM, second, QuestPriority.NORMAL, -2),
          Arguments.of(first, QuestPriority.MAXIMUM, second, QuestPriority.HIGH, -1),

          Arguments.of(first, QuestPriority.MINIMUM, second, QuestPriority.MINIMUM, 0),
          Arguments.of(first, QuestPriority.LOW, second, QuestPriority.LOW, 0),
          Arguments.of(first, QuestPriority.NORMAL, second, QuestPriority.NORMAL, 0),
          Arguments.of(first, QuestPriority.HIGH, second, QuestPriority.HIGH, 0),
          Arguments.of(first, QuestPriority.MAXIMUM, second, QuestPriority.MAXIMUM, 0));
    }
  }
}