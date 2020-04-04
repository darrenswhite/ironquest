package com.darrenswhite.rs.ironquest.player;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.darrenswhite.rs.ironquest.quest.Quest;
import com.darrenswhite.rs.ironquest.quest.Quest.Builder;
import com.darrenswhite.rs.ironquest.quest.requirement.CombatRequirement;
import com.darrenswhite.rs.ironquest.quest.requirement.QuestPointsRequirement;
import com.darrenswhite.rs.ironquest.quest.requirement.QuestRequirement;
import com.darrenswhite.rs.ironquest.quest.requirement.QuestRequirements;
import com.darrenswhite.rs.ironquest.quest.reward.LampReward;
import com.darrenswhite.rs.ironquest.quest.reward.LampType;
import com.darrenswhite.rs.ironquest.util.MapBuilder;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class PlayerTest {

  @Nested
  class Copy {

    @Test
    public void shouldReturnCopyWhichIsEqualButNotSameInstance() {
      Player original = new Player.Builder().withIronman(true).withRecommended(true)
          .withName("username").withLampSkills(Collections.singleton(Skill.HERBLORE))
          .withQuests(Collections.singleton(new Quest.Builder(0).build()))
          .withSkillXps(new MapBuilder<Skill, Double>().put(Skill.PRAYER, 500d).build()).build();

      Player copy = original.copy();

      assertThat(original, equalTo(copy));
      assertThat(original, not(sameInstance(copy)));
    }

    @Test
    public void shouldCopyQuestEntryPriority() {
      Quest quest = new Builder(0).build();
      Player original = new Player.Builder().withQuests(Collections.singleton(quest)).build();

      original.setQuestPriority(quest, QuestPriority.MAXIMUM);

      Player copy = original.copy();

      assertThat(original, equalTo(copy));
      assertThat(original, not(sameInstance(copy)));
      assertThat(copy.getQuestPriority(quest), equalTo(QuestPriority.MAXIMUM));
    }

    @Test
    public void shouldCopyQuestEntryStatus() {
      Quest quest = new Builder(0).build();
      Player original = new Player.Builder().withQuests(Collections.singleton(quest)).build();

      original.setQuestStatus(quest, QuestStatus.COMPLETED);

      Player copy = original.copy();

      assertThat(original, equalTo(copy));
      assertThat(original, not(sameInstance(copy)));
      assertThat(copy.getQuestStatus(quest), equalTo(QuestStatus.COMPLETED));
    }
  }

  @Nested
  class CompleteQuest {

    @Test
    void shouldThrowExceptionWhenQuestAlreadyCompleted() {
      Quest completedQuest = new Quest.Builder(0).build();
      Player player = new Player.Builder().withQuests(Collections.singleton(completedQuest))
          .build();

      player.setQuestStatus(completedQuest, QuestStatus.COMPLETED);

      assertThrows(QuestAlreadyCompletedException.class,
          () -> player.completeQuest(completedQuest));
    }

    @Test
    void shouldThrowExceptionWhenMissingCombatRequirement() {
      Quest questWithCombatRequirement = new Quest.Builder(0).withRequirements(
          new QuestRequirements.Builder().withCombat(new CombatRequirement.Builder(138).build())
              .build()).build();
      Player player = new Player.Builder()
          .withQuests(Collections.singleton(questWithCombatRequirement)).build();

      assertThrows(MissingQuestRequirementsException.class,
          () -> player.completeQuest(questWithCombatRequirement));
    }

    @Test
    void shouldThrowExceptionWhenMissingQuestPointRequirement() {
      Quest questWithQuestPointRequirement = new Quest.Builder(0).withRequirements(
          new QuestRequirements.Builder()
              .withQuestPoints(new QuestPointsRequirement.Builder(300).build()).build()).build();
      Player player = new Player.Builder()
          .withQuests(Collections.singleton(questWithQuestPointRequirement)).build();

      assertThrows(MissingQuestRequirementsException.class,
          () -> player.completeQuest(questWithQuestPointRequirement));
    }

    @Test
    void shouldThrowExceptionWhenMissingQuestRequirement() {
      Quest requiredQuest = new Quest.Builder(0).build();
      Quest questWithQuestRequirement = new Quest.Builder(1).withRequirements(
          new QuestRequirements.Builder().withQuests(
              Collections.singleton(new QuestRequirement.Builder(requiredQuest).build())).build())
          .build();
      Player player = new Player.Builder()
          .withQuests(new HashSet<>(Arrays.asList(requiredQuest, questWithQuestRequirement)))
          .build();

      assertThrows(MissingQuestRequirementsException.class,
          () -> player.completeQuest(questWithQuestRequirement));
    }
  }

  @Nested
  class GetBestLampSkills {

    @Test
    void shouldThrowExceptionWhenRequirementsNotMet() {
      LampReward lampReward = new LampReward.Builder().withRequirements(
          new MapBuilder<Set<Skill>, Integer>().put(Collections.singleton(Skill.CRAFTING), 99)
              .build()).withXp(100).withType(LampType.XP).build();
      Player player = new Player.Builder().build();

      assertThrows(LampSkillsNotFoundException.class,
          () -> player.getBestLampSkills(lampReward, Collections.emptySet()));
    }
  }

  @Nested
  class Equals {

    @Test
    void shouldVerifyEqualsAndHashCode() {
      EqualsVerifier.forClass(Player.class)
          .withPrefabValues(Quest.class, new Quest.Builder(0).build(), new Quest.Builder(1).build())
          .verify();
    }
  }

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class GetBestQuest {

    @ParameterizedTest
    @MethodSource("shouldReturnHighestPriorityQuest")
    void shouldReturnHighestPriorityQuest(Map<Quest, QuestPriority> quests, int expectedId) {
      Player player = new Player.Builder().withQuests(quests.keySet()).build();

      quests.forEach(player::setQuestPriority);

      Quest bestQuest = player.getBestQuest(quests.keySet());

      assertThat(bestQuest, notNullValue());
      assertThat(bestQuest.getId(), equalTo(expectedId));
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
