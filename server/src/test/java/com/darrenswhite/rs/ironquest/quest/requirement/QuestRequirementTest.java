package com.darrenswhite.rs.ironquest.quest.requirement;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.QuestStatus;
import com.darrenswhite.rs.ironquest.quest.Quest;
import java.util.Set;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class QuestRequirementTest {

  @Nested
  class TestPlayer {

    @Test
    void shouldMeetRequirement() {
      QuestRequirement questRequirement = new QuestRequirement.Builder(
          new Quest.Builder().withId(0).build()).build();
      Quest requiredQuest = questRequirement.getQuest();
      Quest questWithRequirement = new Quest.Builder().withId(1).withRequirements(
          new QuestRequirements.Builder().withQuests(Set.of(questRequirement)).build()).build();

      Player playerWithCompletedQuestRequirement = new Player.Builder()
          .withQuests(Set.of(requiredQuest, questWithRequirement)).build();

      playerWithCompletedQuestRequirement.setQuestStatus(requiredQuest, QuestStatus.COMPLETED);

      assertThat(questRequirement.testPlayer(playerWithCompletedQuestRequirement), equalTo(true));
    }

    @Test
    void shouldNotMeetRequirement() {
      Quest requiredQuest = new Quest.Builder().withId(0).build();
      QuestRequirement questRequirement = new QuestRequirement.Builder(requiredQuest).build();
      Quest questWithRequirement = new Quest.Builder().withId(1).withRequirements(
          new QuestRequirements.Builder().withQuests(Set.of(questRequirement)).build()).build();

      Player playerWithIncompleteQuestRequirement = new Player.Builder()
          .withQuests(Set.of(requiredQuest, questWithRequirement)).build();

      assertThat(questRequirement.testPlayer(playerWithIncompleteQuestRequirement), equalTo(false));
    }
  }

  @Nested
  class Equals {

    @Test
    void shouldVerifyEqualsAndHashCode() {
      EqualsVerifier.forClass(QuestRequirement.class)
          .withPrefabValues(Quest.class, new Quest.Builder(0).build(), new Quest.Builder(1).build())
          .verify();
    }
  }
}
