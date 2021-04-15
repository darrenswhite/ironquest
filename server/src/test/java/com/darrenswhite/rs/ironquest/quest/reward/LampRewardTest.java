package com.darrenswhite.rs.ironquest.quest.reward;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.Skill;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class LampRewardTest {

  @Nested
  class GetChoices {

    @Test
    void shouldReturnEachSkillRequirement() {
      Player player = new Player.Builder().build();

      LampReward lampReward = new LampReward.Builder(0).withRequirements(
          Map.of(Set.of(Skill.ATTACK, Skill.DEFENCE), 1, Set.of(Skill.SUMMONING, Skill.PRAYER), 1))
          .build();

      Set<Set<Skill>> choices = lampReward.getChoices(player, Collections.emptySet());

      assertThat(choices, containsInAnyOrder(Set.of(Skill.ATTACK, Skill.DEFENCE),
          Set.of(Skill.SUMMONING, Skill.PRAYER)));
    }

    @Test
    void shouldExcludeChoicesWithMissingLevelRequirements() {
      Player player = new Player.Builder().build();

      LampReward lampReward = new LampReward.Builder(0)
          .withRequirements(Map.of(Set.of(Skill.COOKING), 2, Set.of(Skill.MAGIC), 1)).build();

      Set<Set<Skill>> choices = lampReward.getChoices(player, Collections.emptySet());

      assertThat(choices, is(Set.of(Set.of(Skill.MAGIC))));
    }

    @Test
    void shouldReturnEachSkillRequirementAsSingletonWhenSingleChoice() {
      Player player = new Player.Builder().build();

      LampReward lampReward = new LampReward.Builder(0).withSingleChoice(true)
          .withRequirements(Map.of(Set.of(Skill.ATTACK, Skill.DEFENCE), 1)).build();

      Set<Set<Skill>> choices = lampReward.getChoices(player, Collections.emptySet());

      assertThat(choices, containsInAnyOrder(Set.of(Skill.ATTACK), Set.of(Skill.DEFENCE)));
    }

    @Test
    void shouldReturnEachSkillRequirementWhenExclusive() {
      Player player = new Player.Builder().build();

      LampReward lampReward = new LampReward.Builder(0).withExclusive(true)
          .withRequirements(Map.of(Set.of(Skill.ATTACK, Skill.DEFENCE), 1)).build();

      Set<Set<Skill>> choices = lampReward.getChoices(player, Collections.emptySet());

      assertThat(choices, is(Set.of(Set.of(Skill.ATTACK, Skill.DEFENCE))));
    }

    @Test
    void shouldExcludePreviousChoicesWhenExclusive() {
      Player player = new Player.Builder().build();

      LampReward lampReward = new LampReward.Builder(0).withExclusive(true).withRequirements(
          Map.of(Set.of(Skill.ATTACK, Skill.DEFENCE), 1, Set.of(Skill.STRENGTH, Skill.CONSTITUTION),
              1)).build();

      Set<Set<Skill>> choices = lampReward
          .getChoices(player, Set.of(Set.of(Skill.ATTACK, Skill.DEFENCE)));

      assertThat(choices, is(Set.of(Set.of(Skill.STRENGTH, Skill.CONSTITUTION))));
    }
  }

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class GetXpForSkills {

    @Test
    void shouldThrowExceptionForDynamicLampWithMultipleSkills() {
      Player player = new Player.Builder().build();

      LampReward lampReward = new LampReward.Builder(0).withType(LampType.SMALL_XP)
          .withRequirements(Map.of(Set.of(Skill.ATTACK, Skill.DEFENCE), 1)).build();

      assertThrows(DynamicLampRewardException.class,
          () -> lampReward.getXpForSkills(player, Set.of(Skill.DUNGEONEERING, Skill.CONSTITUTION)));
    }

    @ParameterizedTest
    @MethodSource("shouldReturnXpForLamp")
    void shouldReturnXpForLamp(LampType lampType, double xp, double multiplier,
        Map<Set<Skill>, Integer> requirements, Set<Skill> skills, double expectedXp) {
      Player player = new Player.Builder()
          .withSkillXps(Map.of(Skill.DIVINATION, Skill.MAX_XP, Skill.RANGED, 0d)).build();

      LampReward lampReward = new LampReward.Builder(0).withType(lampType).withXp(xp)
          .withMultiplier(multiplier).withRequirements(requirements).build();

      double actualXp = lampReward.getXpForSkills(player, skills);

      assertThat(actualXp, is(expectedXp));
    }

    Stream<Arguments> shouldReturnXpForLamp() {
      return Stream.of(Arguments
              .of(LampType.XP, 100, 1, Map.of(Set.of(Skill.RANGED), 1), Collections.emptySet(), 100),
          Arguments
              .of(LampType.XP, 100, 1.5, Map.of(Set.of(Skill.RANGED), 1), Collections.emptySet(),
                  150), Arguments
              .of(LampType.SMALL_XP, 0, 1, Map.of(Set.of(Skill.RANGED), 1), Set.of(Skill.RANGED),
                  62), Arguments
              .of(LampType.MEDIUM_XP, 0, 1, Map.of(Set.of(Skill.RANGED), 1), Set.of(Skill.RANGED),
                  125), Arguments
              .of(LampType.LARGE_XP, 0, 1, Map.of(Set.of(Skill.RANGED), 1), Set.of(Skill.RANGED),
                  250), Arguments
              .of(LampType.HUGE_XP, 0, 1, Map.of(Set.of(Skill.RANGED), 1), Set.of(Skill.RANGED),
                  500), Arguments
              .of(LampType.DRAGONKIN, 0, 1, Map.of(Set.of(Skill.RANGED), 1), Set.of(Skill.RANGED),
                  4), Arguments
              .of(LampType.XP, 100, 1, Map.of(Set.of(Skill.DIVINATION), 1), Collections.emptySet(),
                  100), Arguments.of(LampType.XP, 100, 1.5, Map.of(Set.of(Skill.DIVINATION), 1),
              Collections.emptySet(), 150), Arguments
              .of(LampType.SMALL_XP, 0, 1, Map.of(Set.of(Skill.DIVINATION), 1),
                  Set.of(Skill.DIVINATION), 8602), Arguments
              .of(LampType.MEDIUM_XP, 0, 1, Map.of(Set.of(Skill.DIVINATION), 1),
                  Set.of(Skill.DIVINATION), 17204), Arguments
              .of(LampType.LARGE_XP, 0, 1, Map.of(Set.of(Skill.DIVINATION), 1),
                  Set.of(Skill.DIVINATION), 34408), Arguments
              .of(LampType.HUGE_XP, 0, 1, Map.of(Set.of(Skill.DIVINATION), 1),
                  Set.of(Skill.DIVINATION), 68816), Arguments
              .of(LampType.DRAGONKIN, 0, 1, Map.of(Set.of(Skill.DIVINATION), 1),
                  Set.of(Skill.DIVINATION), 48029));
    }
  }

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class MeetsRequirements {

    @Test
    void shouldReturnTrueWhenNoRequirements() {
      Player player = new Player.Builder().build();

      LampReward lampReward = new LampReward.Builder(0).withType(LampType.XP).withXp(100)
          .withRequirements(Collections.emptyMap()).build();

      assertThat(lampReward.meetsRequirements(player), is(true));
    }

    @ParameterizedTest
    @MethodSource("shouldCheckEliteSkillHasMissingRequirements")
    void shouldCheckEliteSkillHasMissingRequirements(int craftingLevel, int divinationLevel,
        int smithingLevel, boolean meetsRequirements) {
      Player player = new Player.Builder().withSkillXps(
          Map.of(Skill.CRAFTING, Skill.CRAFTING.getXpAtLevel(craftingLevel), Skill.DIVINATION,
              Skill.DIVINATION.getXpAtLevel(divinationLevel), Skill.SMITHING,
              Skill.SMITHING.getXpAtLevel(smithingLevel), Skill.INVENTION, 0d)).build();

      LampReward lampReward = new LampReward.Builder(0).withType(LampType.XP).withXp(100)
          .withRequirements(Map.of(Set.of(Skill.INVENTION), 1)).build();

      assertThat(lampReward.meetsRequirements(player), is(meetsRequirements));
    }

    Stream<Arguments> shouldCheckEliteSkillHasMissingRequirements() {
      return Stream.of(Arguments.of(1, 1, 1, false), Arguments.of(79, 79, 79, false),
          Arguments.of(80, 79, 79, false), Arguments.of(79, 80, 79, false),
          Arguments.of(79, 79, 80, false), Arguments.of(80, 80, 79, false),
          Arguments.of(79, 80, 80, false), Arguments.of(80, 79, 80, false),
          Arguments.of(80, 80, 80, true));
    }

    @Test
    void shouldReturnFalseWhenLevelTooLow() {
      Player player = new Player.Builder()
          .withSkillXps(Map.of(Skill.HERBLORE, Skill.HERBLORE.getXpAtLevel(79))).build();

      LampReward lampReward = new LampReward.Builder(0).withType(LampType.XP).withXp(100)
          .withRequirements(Map.of(Set.of(Skill.HERBLORE), 80)).build();

      assertThat(lampReward.meetsRequirements(player), is(false));
    }
  }

  @Nested
  class Equals {

    @Test
    void shouldVerifyEqualsAndHashCode() {
      EqualsVerifier.forClass(LampReward.class).verify();
    }
  }
}
