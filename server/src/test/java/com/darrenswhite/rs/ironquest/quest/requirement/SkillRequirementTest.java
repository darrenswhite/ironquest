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
import org.junit.jupiter.api.Test;

class SkillRequirementTest {

  @Test
  void testPlayer() {
    SkillRequirement skillRequirement = new SkillRequirement.Builder(Skill.RANGED, 50).build();
    Player playerWith49Ranged = createPlayerWithRangedLevel(49);
    Player playerWith50Ranged = createPlayerWithRangedLevel(50);
    Player playerWith51Ranged = createPlayerWithRangedLevel(51);

    assertThat(skillRequirement.testPlayer(playerWith49Ranged), equalTo(false));
    assertThat(skillRequirement.testPlayer(playerWith50Ranged), equalTo(true));
    assertThat(skillRequirement.testPlayer(playerWith51Ranged), equalTo(true));
  }

  @Test
  void merge() {
    List<SkillRequirement> first = Arrays
        .asList(new SkillRequirement.Builder(Skill.SUMMONING, 10).build(),
            new SkillRequirement.Builder(Skill.HERBLORE, 20).build(),
            new SkillRequirement.Builder(Skill.STRENGTH, 30).build());
    List<SkillRequirement> second = Arrays
        .asList(new SkillRequirement.Builder(Skill.SUMMONING, 20).build(),
            new SkillRequirement.Builder(Skill.HERBLORE, 10).build(),
            new SkillRequirement.Builder(Skill.DIVINATION, 30).build());

    Set<SkillRequirement> merged = SkillRequirement.merge(first, second);

    assertThat(merged, containsInAnyOrder(new SkillRequirement.Builder(Skill.SUMMONING, 20).build(),
        new SkillRequirement.Builder(Skill.HERBLORE, 20).build(),
        new SkillRequirement.Builder(Skill.STRENGTH, 30).build(),
        new SkillRequirement.Builder(Skill.DIVINATION, 30).build()));
  }

  private Player createPlayerWithRangedLevel(int level) {
    return new Player.Builder().withSkillXps(
        new MapBuilder<Skill, Double>().put(Skill.RANGED, Skill.RANGED.getXpAtLevel(level)).build())
        .build();
  }
}
