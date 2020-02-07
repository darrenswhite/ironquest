package com.darrenswhite.rs.ironquest.quest.reward;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;

import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.Skill;
import com.darrenswhite.rs.ironquest.util.MapBuilder;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class LampRewardTest {

  @Nested
  class GetChoices {

    @Test
    void shouldReturnEachSkillRequirement() {
      Player player = new Player.Builder().build();

      LampReward lampReward = new LampReward.Builder().withRequirements(
          new MapBuilder<Set<Skill>, Integer>()
              .put(new HashSet<>(Arrays.asList(Skill.ATTACK, Skill.DEFENCE)), 1).build()).build();

      Set<Set<Skill>> choices = lampReward.getChoices(player, Collections.emptySet());

      assertThat(choices, equalTo(
          Collections.singleton(new HashSet<>(Arrays.asList(Skill.ATTACK, Skill.DEFENCE)))));
    }

    @Test
    void shouldReturnEachSkillRequirementAsSingleton() {
      Player player = new Player.Builder().build();

      LampReward lampReward = new LampReward.Builder().withSingleChoice(true).withRequirements(
          new MapBuilder<Set<Skill>, Integer>()
              .put(new HashSet<>(Arrays.asList(Skill.ATTACK, Skill.DEFENCE)), 1).build()).build();

      Set<Set<Skill>> choices = lampReward.getChoices(player, Collections.emptySet());

      assertThat(choices, containsInAnyOrder(Collections.singleton(Skill.ATTACK),
          Collections.singleton(Skill.DEFENCE)));
    }
  }
}
