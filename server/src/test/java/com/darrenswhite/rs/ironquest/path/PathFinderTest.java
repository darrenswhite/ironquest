package com.darrenswhite.rs.ironquest.path;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.QuestStatus;
import com.darrenswhite.rs.ironquest.player.Skill;
import com.darrenswhite.rs.ironquest.quest.Quest;
import com.darrenswhite.rs.ironquest.quest.requirement.QuestPointsRequirement;
import com.darrenswhite.rs.ironquest.quest.requirement.QuestRequirement;
import com.darrenswhite.rs.ironquest.quest.requirement.QuestRequirements;
import com.darrenswhite.rs.ironquest.quest.reward.LampReward;
import com.darrenswhite.rs.ironquest.quest.reward.LampType;
import com.darrenswhite.rs.ironquest.quest.reward.QuestRewards;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class PathFinderTest {

  @Nested
  class Find {

    @Test
    void findForPlayer() throws QuestNotFoundException {
      Quest questNotStarted = new Quest.Builder().withId(0).withDisplayName("questNotStarted")
          .build();
      Quest questCompleted = new Quest.Builder().withId(1).withDisplayName("questCompleted")
          .build();
      Quest questInProgress = new Quest.Builder().withId(2).withDisplayName("questInProgress")
          .build();
      Player player = new Player.Builder()
          .withQuests(Set.of(questNotStarted, questCompleted, questInProgress)).build();

      player.setQuestStatus(questCompleted, QuestStatus.COMPLETED);
      player.setQuestStatus(questInProgress, QuestStatus.IN_PROGRESS);

      Path path = new PathFinder(player).find();

      assertThat(path.getActions(), hasSize(2));
      assertThat(path.getActions().get(0).getMessage(), equalTo("questInProgress"));
      assertThat(path.getActions().get(1).getMessage(), equalTo("questNotStarted"));
      assertThat(path.getStats().getPercentComplete(), equalTo(33D));
    }

    @Test
    void shouldProcessFutureActions() throws QuestNotFoundException {
      Quest questWithXpLampReward = new Quest.Builder().withId(0)
          .withDisplayName("questWithXpLampReward").withRewards(new QuestRewards.Builder()
              .withLamps(Set.of(new LampReward.Builder().withType(LampType.XP).withXp(1000)
                  .withRequirements(Map.of(Set.of(Skill.ATTACK), 2)).build())).build()).build();
      Quest questCompleted = new Quest.Builder().withId(1).withDisplayName("questCompleted")
          .build();
      Quest questWithQuestRequirementAndXpReward = new Quest.Builder().withId(2)
          .withDisplayName("questWithXpReward").withRequirements(new QuestRequirements.Builder()
              .withQuests(Collections
                  .singleton(new QuestRequirement.Builder(questWithXpLampReward).build())).build())
          .withRewards(new QuestRewards.Builder().withXp(Map.of(Skill.ATTACK, 500d)).build())
          .build();
      Player player = new Player.Builder().withQuests(
          Set.of(questWithXpLampReward, questCompleted, questWithQuestRequirementAndXpReward))
          .build();

      player.setQuestStatus(questCompleted, QuestStatus.COMPLETED);

      Path path = new PathFinder(player).find();

      assertThat(path.getActions(), hasSize(3));
      assertThat(path.getActions().get(0).getMessage(), equalTo("questWithXpLampReward"));
      assertThat(path.getActions().get(1).getMessage(), equalTo("questWithXpReward"));
      assertThat(path.getActions().get(2).getMessage(),
          equalTo("questWithXpLampReward: Use XP Lamp on Attack to gain 1k xp"));
      assertThat(path.getStats().getPercentComplete(), equalTo(33D));
    }

    @Test
    void shouldAddFutureActions() throws QuestNotFoundException {
      Quest questWithXpLampReward = new Quest.Builder().withId(0)
          .withDisplayName("questWithXpLampReward").withRewards(new QuestRewards.Builder()
              .withLamps(Set.of(new LampReward.Builder().withType(LampType.XP).withXp(1000)
                  .withRequirements(Map.of(Set.of(Skill.ATTACK), 2)).build())).build()).build();
      Quest questCompleted = new Quest.Builder().withId(1).withDisplayName("questCompleted")
          .build();
      Quest questNotStarted = new Quest.Builder().withId(2).withDisplayName("questNotStarted")
          .build();
      Player player = new Player.Builder()
          .withQuests(Set.of(questWithXpLampReward, questCompleted, questNotStarted)).build();

      player.setQuestStatus(questCompleted, QuestStatus.COMPLETED);

      Path path = new PathFinder(player).find();

      assertThat(path.getActions(), hasSize(3));
      assertThat(path.getActions().get(0).getMessage(), equalTo("questNotStarted"));
      assertThat(path.getActions().get(1).getMessage(), equalTo("questWithXpLampReward"));
      assertThat(path.getActions().get(2).getMessage(),
          equalTo("questWithXpLampReward: Use XP Lamp to gain 1k xp (when requirements are met)"));
      assertThat(path.getActions().get(2).isFuture(), equalTo(true));
      assertThat(path.getStats().getPercentComplete(), equalTo(33D));
    }

    @Test
    void shouldCompletePlaceholderQuests() throws QuestNotFoundException {
      Quest placeholderQuestWithQuestPointReward = new Quest.Builder().withId(-1)
          .withDisplayName("placeholderQuestWithQuestPointReward")
          .withRewards(new QuestRewards.Builder().withQuestPoints(1).build()).build();
      Player player = new Player.Builder().withQuests(Set.of(placeholderQuestWithQuestPointReward))
          .build();

      Path path = new PathFinder(player).find();

      assertThat(path.getActions(), empty());
      assertThat(path.getStats().getPercentComplete(), equalTo(0D));
      assertThat(player.getQuestPoints(), equalTo(1));
    }

    @Test
    void shouldThrowExceptionWhenQuestNotFound() {
      Quest questWithQuestPointRequirement = new Quest.Builder().withId(0)
          .withDisplayName("questWithQuestPointRequirement").withRequirements(
              new QuestRequirements.Builder()
                  .withQuestPoints(new QuestPointsRequirement.Builder(4).build()).build()).build();
      Player player = new Player.Builder().withQuests(Set.of(questWithQuestPointRequirement))
          .build();

      assertThrows(QuestNotFoundException.class, () -> new PathFinder(player).find());
    }
  }
}
