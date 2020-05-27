package com.darrenswhite.rs.ironquest.path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.darrenswhite.rs.ironquest.path.algorithm.DefaultAlgorithm;
import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.quest.Quest;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class QuestIteratorTest {

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class HasNext {

    @ParameterizedTest
    @MethodSource("shouldReturnHighestPriorityQuest")
    void shouldHaveNextQuest(Set<Quest> quests, boolean hasNext) {
      Player player = new Player.Builder().withQuests(quests).build();
      QuestIterator iterator = new QuestIterator(player, new DefaultAlgorithm());

      assertThat(iterator.hasNext(), equalTo(hasNext));
    }

    Stream<Arguments> shouldReturnHighestPriorityQuest() {
      Quest questWithNoRequirements = new Quest.Builder().withId(0)
          .withDisplayName("questWithNoRequirements").build();

      return Stream.of(Arguments.of(Collections.emptySet(), false),
          Arguments.of(Set.of(questWithNoRequirements), true));
    }
  }
}