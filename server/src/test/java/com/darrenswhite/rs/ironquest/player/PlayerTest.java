package com.darrenswhite.rs.ironquest.player;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.darrenswhite.rs.ironquest.quest.Quest;
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
import java.util.Set;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class PlayerTest {

  @Nested
  class Copy {

    @Test
    public void shouldReturnCopyWhichIsEqualButNotSameInstance() {
      Player original = new Player.Builder().withIronman(true).withRecommended(true)
          .withName("username").withLampSkills(Collections.singleton(Skill.HERBLORE)).withQuests(
              Collections.singleton(
                  new QuestEntry(new Quest.Builder(0).build(), QuestStatus.NOT_STARTED,
                      QuestPriority.NORMAL)))
          .withSkillXps(new MapBuilder<Skill, Double>().put(Skill.PRAYER, 500d).build()).build();

      Player copy = original.copy();

      assertThat(original, equalTo(copy));
      assertThat(original, not(sameInstance(copy)));
    }
  }

  @Nested
  class CompleteQuest {

    @Test
    void shouldThrowExceptionWhenQuestAlreadyCompleted() {
      QuestEntry completedQuest = new QuestEntry(new Quest.Builder(0).build(),
          QuestStatus.COMPLETED, QuestPriority.NORMAL);
      Player player = new Player.Builder().withQuests(Collections.singleton(completedQuest))
          .build();

      assertThrows(QuestAlreadyCompletedException.class,
          () -> player.completeQuest(completedQuest));
    }

    @Test
    void shouldThrowExceptionWhenMissingCombatRequirement() {
      QuestEntry questWithCombatRequirement = new QuestEntry(new Quest.Builder(0).withRequirements(
          new QuestRequirements.Builder().withCombat(new CombatRequirement.Builder(138).build())
              .build()).build(), QuestStatus.NOT_STARTED, QuestPriority.NORMAL);
      Player player = new Player.Builder()
          .withQuests(Collections.singleton(questWithCombatRequirement)).build();

      assertThrows(MissingQuestRequirementsException.class,
          () -> player.completeQuest(questWithCombatRequirement));
    }

    @Test
    void shouldThrowExceptionWhenMissingQuestPointRequirement() {
      QuestEntry questWithQuestPointRequirement = new QuestEntry(new Quest.Builder(0)
          .withRequirements(new QuestRequirements.Builder()
              .withQuestPoints(new QuestPointsRequirement.Builder(300).build()).build()).build(),
          QuestStatus.NOT_STARTED, QuestPriority.NORMAL);
      Player player = new Player.Builder()
          .withQuests(Collections.singleton(questWithQuestPointRequirement)).build();

      assertThrows(MissingQuestRequirementsException.class,
          () -> player.completeQuest(questWithQuestPointRequirement));
    }

    @Test
    void shouldThrowExceptionWhenMissingQuestRequirement() {
      QuestEntry requiredQuest = new QuestEntry(new Quest.Builder(0).build(),
          QuestStatus.NOT_STARTED, QuestPriority.NORMAL);
      QuestEntry questWithQuestRequirement = new QuestEntry(new Quest.Builder(1).withRequirements(
          new QuestRequirements.Builder().withQuests(
              Collections.singleton(new QuestRequirement.Builder(requiredQuest.getQuest()).build()))
              .build()).build(), QuestStatus.NOT_STARTED, QuestPriority.NORMAL);
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
}
