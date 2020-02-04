package com.darrenswhite.rs.ironquest.quest.requirement;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.QuestEntry;
import com.darrenswhite.rs.ironquest.player.QuestPriority;
import com.darrenswhite.rs.ironquest.player.QuestStatus;
import com.darrenswhite.rs.ironquest.quest.Quest;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class QuestRequirementTest {

  @Nested
  class TestPlayer {

    @Test
    void shouldMeetRequirement() {
      QuestRequirement questRequirement = new QuestRequirement.Builder(new Quest.Builder().build())
          .build();
      QuestEntry requiredQuestEntry = new QuestEntry(questRequirement.getQuest(),
          QuestStatus.COMPLETED, QuestPriority.NORMAL);
      QuestEntry questWithRequirement = new QuestEntry(new Quest.Builder().withRequirements(
          new QuestRequirements.Builder().withQuests(Collections.singleton(questRequirement))
              .build()).build(), QuestStatus.NOT_STARTED, QuestPriority.NORMAL);

      Player playerWithCompletedQuestRequirement = new Player.Builder()
          .withQuests(new HashSet<>(Arrays.asList(requiredQuestEntry, questWithRequirement)))
          .build();

      assertThat(questRequirement.testPlayer(playerWithCompletedQuestRequirement), equalTo(true));
    }

    @Test
    void shouldNotMeetRequirement() {
      QuestRequirement questRequirement = new QuestRequirement.Builder(new Quest.Builder().build())
          .build();
      QuestEntry requiredQuestEntry = new QuestEntry(questRequirement.getQuest(),
          QuestStatus.NOT_STARTED, QuestPriority.NORMAL);
      QuestEntry questWithRequirement = new QuestEntry(new Quest.Builder().withRequirements(
          new QuestRequirements.Builder().withQuests(Collections.singleton(questRequirement))
              .build()).build(), QuestStatus.NOT_STARTED, QuestPriority.NORMAL);

      Player playerWithIncompleteQuestRequirement = new Player.Builder()
          .withQuests(new HashSet<>(Arrays.asList(requiredQuestEntry, questWithRequirement)))
          .build();

      assertThat(questRequirement.testPlayer(playerWithIncompleteQuestRequirement), equalTo(false));
    }
  }

}
