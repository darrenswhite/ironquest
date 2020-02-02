package com.darrenswhite.rs.ironquest.player;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import com.darrenswhite.rs.ironquest.quest.RuneMetricsQuest;
import com.darrenswhite.rs.ironquest.quest.RuneMetricsQuest.Status;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import org.junit.Test;

public class RuneMetricsServiceTest {

  @Test
  public void load() {
    ObjectMapper objectMapper = new ObjectMapper();
    String url = RuneMetricsServiceTest.class.getClassLoader().getResource("runemetrics.json")
        .toString();
    String name = "user";
    RuneMetricsService rms = new RuneMetricsService(url, objectMapper);

    Set<RuneMetricsQuest> loadedQuests = rms.load(name);

    assertThat(loadedQuests, notNullValue());
    assertThat(3, equalTo(loadedQuests.size()));

    RuneMetricsQuest questA = new RuneMetricsQuest.Builder().withTitle("a")
        .withStatus(Status.NOT_STARTED).withDifficulty(1).withMembers(false).withQuestPoints(10)
        .withUserEligible(false).build();
    RuneMetricsQuest questB = new RuneMetricsQuest.Builder().withTitle("b")
        .withStatus(Status.STARTED).withDifficulty(2).withMembers(true).withQuestPoints(20)
        .withUserEligible(false).build();
    RuneMetricsQuest questC = new RuneMetricsQuest.Builder().withTitle("c")
        .withStatus(Status.COMPLETED).withDifficulty(3).withMembers(false).withQuestPoints(30)
        .withUserEligible(true).build();

    assertThat(loadedQuests, containsInAnyOrder(questA, questB, questC));
  }
}
