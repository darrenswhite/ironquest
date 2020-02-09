package com.darrenswhite.rs.ironquest.quest.requirement;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.Skill;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Stream;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class CombatRequirementTest {

  private static final Map<Skill, Double> MIN_COMBAT_XP = new EnumMap<>(Skill.INITIAL_XPS);
  private static final Map<Skill, Double> MAX_COMBAT_XP = new EnumMap<>(Skill.class);

  static {
    MAX_COMBAT_XP.put(Skill.ATTACK, Skill.MAX_XP);
    MAX_COMBAT_XP.put(Skill.DEFENCE, Skill.MAX_XP);
    MAX_COMBAT_XP.put(Skill.STRENGTH, Skill.MAX_XP);
    MAX_COMBAT_XP.put(Skill.MAGIC, Skill.MAX_XP);
    MAX_COMBAT_XP.put(Skill.RANGED, Skill.MAX_XP);
    MAX_COMBAT_XP.put(Skill.PRAYER, Skill.MAX_XP);
    MAX_COMBAT_XP.put(Skill.CONSTITUTION, Skill.MAX_XP);
    MAX_COMBAT_XP.put(Skill.SUMMONING, Skill.MAX_XP);
  }

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class TestPlayer {

    @ParameterizedTest
    @MethodSource("shouldMeetRequirement")
    void shouldMeetRequirement(int combatLevel, Map<Skill, Double> skillXps) {
      Player player = new Player.Builder().withSkillXps(skillXps).build();

      CombatRequirement combatRequirement = new CombatRequirement.Builder(combatLevel).build();

      assertThat(combatRequirement.testPlayer(player), equalTo(true));
    }

    Stream<Arguments> shouldMeetRequirement() {
      return Stream.of(Arguments.of(3, MIN_COMBAT_XP), Arguments.of(3, MAX_COMBAT_XP),
          Arguments.of(138, MAX_COMBAT_XP));
    }

    @ParameterizedTest
    @MethodSource("shouldNotMeetRequirement")
    void shouldNotMeetRequirement(int combatLevel, Map<Skill, Double> skillXps) {
      Player player = new Player.Builder().withSkillXps(skillXps).build();

      CombatRequirement combatRequirement = new CombatRequirement.Builder(combatLevel).build();

      assertThat(combatRequirement.testPlayer(player), equalTo(false));
    }

    Stream<Arguments> shouldNotMeetRequirement() {
      return Stream.of(Arguments.of(4, MIN_COMBAT_XP), Arguments.of(138, MIN_COMBAT_XP),
          Arguments.of(139, MAX_COMBAT_XP));
    }
  }

  @Nested
  class Equals {

    @Test
    void shouldVerifyEqualsAndHashCode() {
      EqualsVerifier.forClass(CombatRequirement.class).verify();
    }
  }
}
