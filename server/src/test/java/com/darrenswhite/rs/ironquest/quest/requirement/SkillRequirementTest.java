package com.darrenswhite.rs.ironquest.quest.requirement;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.Skill;
import com.darrenswhite.rs.ironquest.util.MapBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class SkillRequirementTest {

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class TestPlayer {

    @ParameterizedTest
    @MethodSource("shouldMeetRequirement")
    void shouldMeetRequirement(int levelRequired, int playerLevel) {
      Player player = new Player.Builder().withSkillXps(
          new MapBuilder<Skill, Double>().put(Skill.RANGED, Skill.RANGED.getXpAtLevel(playerLevel))
              .build()).build();

      SkillRequirement skillRequirement = new SkillRequirement.Builder(Skill.RANGED, levelRequired)
          .build();

      assertThat(skillRequirement.testPlayer(player), equalTo(true));
    }

    Stream<Arguments> shouldMeetRequirement() {
      return Stream.of(Arguments.of(1, 1), Arguments.of(1, 99), Arguments.of(99, 99));
    }

    @ParameterizedTest
    @MethodSource("shouldNotMeetRequirement")
    void shouldNotMeetRequirement(int levelRequired, int playerLevel) {
      Player player = new Player.Builder().withSkillXps(
          new MapBuilder<Skill, Double>().put(Skill.RANGED, Skill.RANGED.getXpAtLevel(playerLevel))
              .build()).build();

      SkillRequirement skillRequirement = new SkillRequirement.Builder(Skill.RANGED, levelRequired)
          .build();

      assertThat(skillRequirement.testPlayer(player), equalTo(false));
    }

    Stream<Arguments> shouldNotMeetRequirement() {
      return Stream.of(Arguments.of(2, 1), Arguments.of(99, 98), Arguments.of(99, 1));
    }
  }

  @Nested
  class Merge {

    @Test
    void shouldCreateMapWithHighestRequirementFromBothCollections() {
      List<SkillRequirement> first = Arrays
          .asList(new SkillRequirement.Builder(Skill.SUMMONING, 10).build(),
              new SkillRequirement.Builder(Skill.HERBLORE, 20).build(),
              new SkillRequirement.Builder(Skill.STRENGTH, 30).build());
      List<SkillRequirement> second = Arrays
          .asList(new SkillRequirement.Builder(Skill.SUMMONING, 20).build(),
              new SkillRequirement.Builder(Skill.HERBLORE, 10).build(),
              new SkillRequirement.Builder(Skill.DIVINATION, 30).build());

      Set<SkillRequirement> merged = SkillRequirement.merge(first, second);

      assertThat(merged,
          containsInAnyOrder(new SkillRequirement.Builder(Skill.SUMMONING, 20).build(),
              new SkillRequirement.Builder(Skill.HERBLORE, 20).build(),
              new SkillRequirement.Builder(Skill.STRENGTH, 30).build(),
              new SkillRequirement.Builder(Skill.DIVINATION, 30).build()));
    }
  }

}
