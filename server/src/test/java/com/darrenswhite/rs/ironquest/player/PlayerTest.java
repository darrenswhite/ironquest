package com.darrenswhite.rs.ironquest.player;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
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
import java.util.Collections;
import java.util.Map;
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
          .withName("username").withLampSkills(Set.of(Skill.HERBLORE))
          .withQuests(Set.of(new Quest.Builder(0).build())).withSkillXps(Map.of(Skill.PRAYER, 500d))
          .build();

      Player copy = original.copy();

      assertThat(original, equalTo(copy));
      assertThat(original, not(sameInstance(copy)));
    }

    @Test
    public void shouldCopyQuestEntryPriority() {
      Quest quest = new Builder(0).build();
      Player original = new Player.Builder().withQuests(Set.of(quest)).build();

      original.setQuestPriority(quest, QuestPriority.MAXIMUM);

      Player copy = original.copy();

      assertThat(original, equalTo(copy));
      assertThat(original, not(sameInstance(copy)));
      assertThat(copy.getQuestPriority(quest), equalTo(QuestPriority.MAXIMUM));
    }

    @Test
    public void shouldCopyQuestEntryStatus() {
      Quest quest = new Builder(0).build();
      Player original = new Player.Builder().withQuests(Set.of(quest)).build();

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
      Player player = new Player.Builder().withQuests(Set.of(completedQuest)).build();

      player.setQuestStatus(completedQuest, QuestStatus.COMPLETED);

      assertThrows(QuestAlreadyCompletedException.class,
          () -> player.completeQuest(completedQuest));
    }

    @Test
    void shouldThrowExceptionWhenMissingCombatRequirement() {
      Quest questWithCombatRequirement = new Quest.Builder(0).withRequirements(
          new QuestRequirements.Builder().withCombat(new CombatRequirement.Builder(138).build())
              .build()).build();
      Player player = new Player.Builder().withQuests(Set.of(questWithCombatRequirement)).build();

      assertThrows(MissingQuestRequirementsException.class,
          () -> player.completeQuest(questWithCombatRequirement));
    }

    @Test
    void shouldThrowExceptionWhenMissingQuestPointRequirement() {
      Quest questWithQuestPointRequirement = new Quest.Builder(0).withRequirements(
          new QuestRequirements.Builder()
              .withQuestPoints(new QuestPointsRequirement.Builder(300).build()).build()).build();
      Player player = new Player.Builder().withQuests(Set.of(questWithQuestPointRequirement))
          .build();

      assertThrows(MissingQuestRequirementsException.class,
          () -> player.completeQuest(questWithQuestPointRequirement));
    }

    @Test
    void shouldThrowExceptionWhenMissingQuestRequirement() {
      Quest requiredQuest = new Quest.Builder(0).build();
      Quest questWithQuestRequirement = new Quest.Builder(1).withRequirements(
          new QuestRequirements.Builder()
              .withQuests(Set.of(new QuestRequirement.Builder(requiredQuest).build())).build())
          .build();
      Player player = new Player.Builder()
          .withQuests(Set.of(requiredQuest, questWithQuestRequirement)).build();

      assertThrows(MissingQuestRequirementsException.class,
          () -> player.completeQuest(questWithQuestRequirement));
    }
  }

  @Nested
  class GetOptimalLampSkills {

    @Test
    void shouldThrowExceptionWhenRequirementsNotMet() {
      LampReward lampReward = new LampReward.Builder()
          .withRequirements(Map.of(Set.of(Skill.CRAFTING), 99)).withXp(100).withType(LampType.XP)
          .build();
      Player player = new Player.Builder().build();

      assertThrows(LampSkillsNotFoundException.class,
          () -> player.getOptimalLampSkills(lampReward, Collections.emptySet()));
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
