package com.darrenswhite.rs.ironquest.quest.requirement;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.QuestEntry;
import com.darrenswhite.rs.ironquest.player.QuestPriority;
import com.darrenswhite.rs.ironquest.player.QuestStatus;
import com.darrenswhite.rs.ironquest.quest.Quest;
import com.darrenswhite.rs.ironquest.quest.reward.QuestRewards;
import java.util.Collections;
import org.junit.jupiter.api.Test;

public class QuestPointsRequirementTest {

  @Test
  public void testPlayer() {
    QuestPointsRequirement questPointsRequirement = new QuestPointsRequirement.Builder(3).build();

    Player playerWith0QuestPoints = createPlayerWithQuestPoints(0);
    Player playerWith1QuestPoints = createPlayerWithQuestPoints(1);
    Player playerWith2QuestPoints = createPlayerWithQuestPoints(2);
    Player playerWith3QuestPoints = createPlayerWithQuestPoints(3);
    Player playerWith4QuestPoints = createPlayerWithQuestPoints(4);

    assertThat(questPointsRequirement.testPlayer(playerWith0QuestPoints), equalTo(false));
    assertThat(questPointsRequirement.testPlayer(playerWith1QuestPoints), equalTo(false));
    assertThat(questPointsRequirement.testPlayer(playerWith2QuestPoints), equalTo(false));
    assertThat(questPointsRequirement.testPlayer(playerWith3QuestPoints), equalTo(true));
    assertThat(questPointsRequirement.testPlayer(playerWith4QuestPoints), equalTo(true));
  }

  private Player createPlayerWithQuestPoints(int questPoints) {
    QuestEntry questWith3QuestPoints = new QuestEntry(new Quest.Builder()
        .withRewards(new QuestRewards.Builder().withQuestPoints(questPoints).build()).build(),
        QuestStatus.COMPLETED, QuestPriority.NORMAL);
    return new Player.Builder().withQuests(Collections.singleton(questWith3QuestPoints)).build();
  }
}
