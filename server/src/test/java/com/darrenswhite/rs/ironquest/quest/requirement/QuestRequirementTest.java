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
import org.junit.jupiter.api.Test;

class QuestRequirementTest {

  @Test
  void testPlayer() {
    QuestRequirement questRequirement = new QuestRequirement.Builder(new Quest.Builder().build())
        .build();
    Player playerWithIncompleteQuestRequirement = createPlayerWithQuestRequirement(questRequirement,
        QuestStatus.NOT_STARTED);
    Player playerWithCompletedQuestRequirement = createPlayerWithQuestRequirement(questRequirement,
        QuestStatus.COMPLETED);

    assertThat(questRequirement.testPlayer(playerWithIncompleteQuestRequirement), equalTo(false));
    assertThat(questRequirement.testPlayer(playerWithCompletedQuestRequirement), equalTo(true));
  }

  private Player createPlayerWithQuestRequirement(QuestRequirement questRequirement,
      QuestStatus requiredQuestStatus) {
    QuestEntry requiredQuestEntry = new QuestEntry(questRequirement.getQuest(), requiredQuestStatus,
        QuestPriority.NORMAL);
    QuestEntry questWithRequirement = new QuestEntry(new Quest.Builder().withRequirements(
        new QuestRequirements.Builder().withQuests(Collections.singleton(questRequirement)).build())
        .build(), QuestStatus.NOT_STARTED, QuestPriority.NORMAL);

    return new Player.Builder()
        .withQuests(new HashSet<>(Arrays.asList(requiredQuestEntry, questWithRequirement))).build();
  }
}
