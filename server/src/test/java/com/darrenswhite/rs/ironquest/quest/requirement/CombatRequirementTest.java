package com.darrenswhite.rs.ironquest.quest.requirement;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.Skill;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class CombatRequirementTest {

  private static final Map<Skill, Double> MAX_COMBAT_XP = new HashMap<>();

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

  @Test
  public void testPlayer() {
    Player playerWith3Combat = new Player.Builder().build();
    Player playerWith138Combat = new Player.Builder().withSkillXps(MAX_COMBAT_XP).build();

    assertThat(new CombatRequirement.Builder(3).build().testPlayer(playerWith3Combat),
        equalTo(true));
    assertThat(new CombatRequirement.Builder(4).build().testPlayer(playerWith3Combat),
        equalTo(false));
    assertThat(new CombatRequirement.Builder(138).build().testPlayer(playerWith3Combat),
        equalTo(false));
    assertThat(new CombatRequirement.Builder(3).build().testPlayer(playerWith138Combat),
        equalTo(true));
    assertThat(new CombatRequirement.Builder(138).build().testPlayer(playerWith138Combat),
        equalTo(true));
    assertThat(new CombatRequirement.Builder(139).build().testPlayer(playerWith138Combat),
        equalTo(false));
  }
}
