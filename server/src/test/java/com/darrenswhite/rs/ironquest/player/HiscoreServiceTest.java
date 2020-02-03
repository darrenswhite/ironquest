package com.darrenswhite.rs.ironquest.player;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import java.util.Map;
import org.junit.Test;

public class HiscoreServiceTest {

  @Test
  public void load() {
    String url = HiscoreServiceTest.class.getClassLoader().getResource("hiscores.csv").toString();
    String name = "user";
    HiscoreService hiscoreService = new HiscoreService(url);

    Map<Skill, Double> loadedSkills = hiscoreService.load(name);

    assertThat(loadedSkills, notNullValue());
    assertThat(27, equalTo(loadedSkills.size()));
    assertThat(737627D, closeTo(loadedSkills.get(Skill.ATTACK), 0));
    assertThat(28782069D, closeTo(loadedSkills.get(Skill.DEFENCE), 0));
    assertThat(165576D, closeTo(loadedSkills.get(Skill.STRENGTH), 0));
    assertThat(1154D, closeTo(loadedSkills.get(Skill.CONSTITUTION), 0));
    assertThat(10692629D, closeTo(loadedSkills.get(Skill.RANGED), 0));
    assertThat(3597792D, closeTo(loadedSkills.get(Skill.PRAYER), 0));
    assertThat(19368992D, closeTo(loadedSkills.get(Skill.MAGIC), 0));
    assertThat(8740D, closeTo(loadedSkills.get(Skill.COOKING), 0));
    assertThat(203254D, closeTo(loadedSkills.get(Skill.WOODCUTTING), 0));
    assertThat(12031D, closeTo(loadedSkills.get(Skill.FLETCHING), 0));
    assertThat(63555443D, closeTo(loadedSkills.get(Skill.FISHING), 0));
    assertThat(2421087D, closeTo(loadedSkills.get(Skill.FIREMAKING), 0));
    assertThat(22406D, closeTo(loadedSkills.get(Skill.CRAFTING), 0));
    assertThat(6291D, closeTo(loadedSkills.get(Skill.SMITHING), 0));
    assertThat(3973D, closeTo(loadedSkills.get(Skill.MINING), 0));
    assertThat(3523D, closeTo(loadedSkills.get(Skill.HERBLORE), 0));
    assertThat(0D, closeTo(loadedSkills.get(Skill.AGILITY), 0));
    assertThat(0D, closeTo(loadedSkills.get(Skill.THIEVING), 0));
    assertThat(1D, closeTo(loadedSkills.get(Skill.SLAYER), 0));
    assertThat(83014D, closeTo(loadedSkills.get(Skill.FARMING), 0));
    assertThat(104273167D, closeTo(loadedSkills.get(Skill.RUNECRAFTING), 0));
    assertThat(111945D, closeTo(loadedSkills.get(Skill.HUNTER), 0));
    assertThat(20224D, closeTo(loadedSkills.get(Skill.CONSTRUCTION), 0));
    assertThat(28782069D, closeTo(loadedSkills.get(Skill.SUMMONING), 0));
    assertThat(55649D, closeTo(loadedSkills.get(Skill.DUNGEONEERING), 0));
    assertThat(1833D, closeTo(loadedSkills.get(Skill.DIVINATION), 0));
    assertThat(80618654D, closeTo(loadedSkills.get(Skill.INVENTION), 0));
  }
}
