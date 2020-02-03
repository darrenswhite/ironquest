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

public class SkillRequirementTest {

  @Test
  public void testPlayer() {
    SkillRequirement skillRequirement = new SkillRequirement(Skill.RANGED, 50);
    Player playerWith49Ranged = createPlayerWithRangedLevel(49);
    Player playerWith50Ranged = createPlayerWithRangedLevel(50);
    Player playerWith51Ranged = createPlayerWithRangedLevel(51);

    assertThat(skillRequirement.testPlayer(playerWith49Ranged), equalTo(false));
    assertThat(skillRequirement.testPlayer(playerWith50Ranged), equalTo(true));
    assertThat(skillRequirement.testPlayer(playerWith51Ranged), equalTo(true));
  }

  @Test
  public void merge() {
    List<SkillRequirement> first = Arrays
        .asList(new SkillRequirement(Skill.SUMMONING, 10), new SkillRequirement(Skill.HERBLORE, 20),
            new SkillRequirement(Skill.STRENGTH, 30));
    List<SkillRequirement> second = Arrays
        .asList(new SkillRequirement(Skill.SUMMONING, 20), new SkillRequirement(Skill.HERBLORE, 10),
            new SkillRequirement(Skill.DIVINATION, 30));

    Set<SkillRequirement> merged = SkillRequirement.merge(first, second);

    assertThat(merged, containsInAnyOrder(new SkillRequirement(Skill.SUMMONING, 20),
        new SkillRequirement(Skill.HERBLORE, 20), new SkillRequirement(Skill.STRENGTH, 30),
        new SkillRequirement(Skill.DIVINATION, 30)));
  }

  private Player createPlayerWithRangedLevel(int level) {
    return new Player.Builder().withSkillXps(
        new MapBuilder<Skill, Double>().put(Skill.RANGED, Skill.RANGED.getXpAtLevel(level)).build())
        .build();
  }
}
