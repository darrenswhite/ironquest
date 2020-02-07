package com.darrenswhite.rs.ironquest.player;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;

import com.darrenswhite.rs.ironquest.quest.Quest;
import com.darrenswhite.rs.ironquest.util.MapBuilder;
import java.util.Collections;
import org.junit.Test;
import org.junit.jupiter.api.Nested;

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
}
