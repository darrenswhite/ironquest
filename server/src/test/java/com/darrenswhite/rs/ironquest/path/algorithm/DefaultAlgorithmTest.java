package com.darrenswhite.rs.ironquest.path.algorithm;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.QuestPriority;
import com.darrenswhite.rs.ironquest.player.Skill;
import com.darrenswhite.rs.ironquest.quest.Quest;
import com.darrenswhite.rs.ironquest.quest.requirement.QuestRequirements;
import com.darrenswhite.rs.ironquest.quest.requirement.SkillRequirement;
import com.darrenswhite.rs.ironquest.quest.reward.LampReward;
import com.darrenswhite.rs.ironquest.quest.reward.LampType;
import com.darrenswhite.rs.ironquest.quest.reward.QuestRewards;
import java.util.Collections;
import java.util.Map;
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
  class GetQuestComparator {

    @ParameterizedTest
    @MethodSource({"shouldReturnHighestPriorityQuest", "shouldReturnLowestSkillRequirementsQuest",
        "shouldReturnHighestRewardsQuest", "shouldCompareInCorrectOrder"})
    void shouldReturnMaximumQuest(Quest first, QuestPriority firstPriority, Quest second,
        QuestPriority secondPriority, Quest expected) {
      Set<Quest> quests = Set.of(first, second);
      Player player = new Player.Builder().withQuests(quests).build();

      player.setQuestPriority(first, firstPriority);
      player.setQuestPriority(second, secondPriority);

      Quest result = Collections.min(quests, algorithm.getQuestComparator(player));

      assertThat(result, equalTo(expected));
    }

    Stream<Arguments> shouldReturnHighestPriorityQuest() {
      Quest first = new Quest.Builder(0).withDisplayName("first").build();
      Quest second = new Quest.Builder(1).withDisplayName("second").build();

      return Stream
          .of(Arguments.of(first, QuestPriority.MINIMUM, second, QuestPriority.MAXIMUM, second),
              Arguments.of(first, QuestPriority.LOW, second, QuestPriority.MAXIMUM, second),
              Arguments.of(first, QuestPriority.NORMAL, second, QuestPriority.MAXIMUM, second),
              Arguments.of(first, QuestPriority.HIGH, second, QuestPriority.MAXIMUM, second),

              Arguments.of(first, QuestPriority.MAXIMUM, second, QuestPriority.MINIMUM, first),
              Arguments.of(first, QuestPriority.MAXIMUM, second, QuestPriority.LOW, first),
              Arguments.of(first, QuestPriority.MAXIMUM, second, QuestPriority.NORMAL, first),
              Arguments.of(first, QuestPriority.MAXIMUM, second, QuestPriority.HIGH, first),

              Arguments.of(first, QuestPriority.MINIMUM, second, QuestPriority.MINIMUM, first),
              Arguments.of(first, QuestPriority.LOW, second, QuestPriority.LOW, first),
              Arguments.of(first, QuestPriority.NORMAL, second, QuestPriority.NORMAL, first),
              Arguments.of(first, QuestPriority.HIGH, second, QuestPriority.HIGH, first),
              Arguments.of(first, QuestPriority.MAXIMUM, second, QuestPriority.MAXIMUM, first));
    }

    Stream<Arguments> shouldReturnLowestSkillRequirementsQuest() {
      Quest questWithNoRequirements = new Quest.Builder(0)
          .withDisplayName("questWithNoRequirements").build();
      Quest questWithLowSkillRequirements = new Quest.Builder(1)
          .withDisplayName("questWithLowRequirements").withRequirements(
              new QuestRequirements.Builder()
                  .withSkills(Set.of(new SkillRequirement.Builder(Skill.DEFENCE, 2).build()))
                  .build()).build();
      Quest questWithMediumSkillRequirements = new Quest.Builder(2)
          .withDisplayName("questWithMediumRequirements").withRequirements(
              new QuestRequirements.Builder()
                  .withSkills(Set.of(new SkillRequirement.Builder(Skill.RANGED, 92).build()))
                  .build()).build();
      Quest questWithHighSkillRequirements = new Quest.Builder(3)
          .withDisplayName("questWithHighRequirements").withRequirements(
              new QuestRequirements.Builder()
                  .withSkills(Set.of(new SkillRequirement.Builder(Skill.HERBLORE, 120).build()))
                  .build()).build();

      return Stream.of(Arguments
              .of(questWithNoRequirements, QuestPriority.NORMAL, questWithLowSkillRequirements,
                  QuestPriority.NORMAL, questWithNoRequirements), Arguments
              .of(questWithNoRequirements, QuestPriority.NORMAL, questWithMediumSkillRequirements,
                  QuestPriority.NORMAL, questWithNoRequirements), Arguments
              .of(questWithNoRequirements, QuestPriority.NORMAL, questWithHighSkillRequirements,
                  QuestPriority.NORMAL, questWithNoRequirements),

          Arguments.of(questWithLowSkillRequirements, QuestPriority.NORMAL,
              questWithMediumSkillRequirements, QuestPriority.NORMAL,
              questWithLowSkillRequirements), Arguments
              .of(questWithLowSkillRequirements, QuestPriority.NORMAL,
                  questWithHighSkillRequirements, QuestPriority.NORMAL,
                  questWithLowSkillRequirements),

          Arguments.of(questWithMediumSkillRequirements, QuestPriority.NORMAL,
              questWithHighSkillRequirements, QuestPriority.NORMAL,
              questWithMediumSkillRequirements));
    }

    Stream<Arguments> shouldReturnHighestRewardsQuest() {
      Quest questWithNoRewards = new Quest.Builder(0).withDisplayName("questWithNoRewards").build();
      Quest questWithLowRewards = new Quest.Builder(1).withDisplayName("questWithLowRewards")
          .withRewards(new QuestRewards.Builder().withXp(Map.of(Skill.MAGIC, 100d)).build())
          .build();
      Quest questWithMediumRewards = new Quest.Builder(2).withDisplayName("questWithMediumRewards")
          .withRewards(new QuestRewards.Builder().withLamps(
              Set.of(new LampReward.Builder().withType(LampType.XP).withXp(25000).build())).build())
          .build();
      Quest questWithHighRewards = new Quest.Builder(3).withDisplayName("questWithHighRewards")
          .withRewards(new QuestRewards.Builder().withLamps(
              Set.of(new LampReward.Builder().withType(LampType.XP).withXp(20000d).build()))
              .withXp(Map.of(Skill.STRENGTH, 20000d)).build()).build();

      return Stream.of(Arguments
              .of(questWithNoRewards, QuestPriority.NORMAL, questWithLowRewards, QuestPriority.NORMAL,
                  questWithLowRewards), Arguments
              .of(questWithNoRewards, QuestPriority.NORMAL, questWithMediumRewards,
                  QuestPriority.NORMAL, questWithMediumRewards), Arguments
              .of(questWithNoRewards, QuestPriority.NORMAL, questWithHighRewards, QuestPriority.NORMAL,
                  questWithHighRewards),

          Arguments.of(questWithLowRewards, QuestPriority.NORMAL, questWithMediumRewards,
              QuestPriority.NORMAL, questWithMediumRewards), Arguments
              .of(questWithLowRewards, QuestPriority.NORMAL, questWithHighRewards,
                  QuestPriority.NORMAL, questWithHighRewards),

          Arguments.of(questWithMediumRewards, QuestPriority.NORMAL, questWithHighRewards,
              QuestPriority.NORMAL, questWithHighRewards));
    }

    Stream<Arguments> shouldCompareInCorrectOrder() {
      Quest questWithNoRequirements = new Quest.Builder(0)
          .withDisplayName("questWithNoRequirementsNoRewards").build();
      Quest questWithNoRequirementsLowRewards = new Quest.Builder(1)
          .withDisplayName("questWithNoRequirementsLowRewards").withRewards(
              new QuestRewards.Builder().withLamps(
                  Set.of(new LampReward.Builder().withType(LampType.XP).withXp(20000d).build()))
                  .withXp(Map.of(Skill.STRENGTH, 20000d)).build()).build();
      Quest questWithHighRequirementsHighRewards = new Quest.Builder(2)
          .withDisplayName("questWithHighRequirementsHighRewards").withRequirements(
              new QuestRequirements.Builder()
                  .withSkills(Set.of(new SkillRequirement.Builder(Skill.HERBLORE, 120).build()))
                  .build()).withRewards(new QuestRewards.Builder().withLamps(
              Set.of(new LampReward.Builder().withType(LampType.XP).withXp(20000d).build()))
              .withXp(Map.of(Skill.STRENGTH, 20000d)).build()).build();

      return Stream.of(Arguments
              .of(questWithNoRequirements, QuestPriority.NORMAL, questWithNoRequirementsLowRewards,
                  QuestPriority.NORMAL, questWithNoRequirementsLowRewards), Arguments
              .of(questWithNoRequirements, QuestPriority.HIGH, questWithNoRequirementsLowRewards,
                  QuestPriority.NORMAL, questWithNoRequirements), Arguments
              .of(questWithNoRequirements, QuestPriority.NORMAL, questWithNoRequirementsLowRewards,
                  QuestPriority.LOW, questWithNoRequirements),

          Arguments.of(questWithHighRequirementsHighRewards, QuestPriority.NORMAL,
              questWithNoRequirementsLowRewards, QuestPriority.NORMAL,
              questWithNoRequirementsLowRewards), Arguments
              .of(questWithHighRequirementsHighRewards, QuestPriority.HIGH,
                  questWithNoRequirementsLowRewards, QuestPriority.NORMAL,
                  questWithHighRequirementsHighRewards), Arguments
              .of(questWithHighRequirementsHighRewards, QuestPriority.NORMAL,
                  questWithNoRequirementsLowRewards, QuestPriority.LOW,
                  questWithHighRequirementsHighRewards));
    }
  }
}
