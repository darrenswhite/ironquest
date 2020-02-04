package com.darrenswhite.rs.ironquest.player;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import java.util.Map;
import org.junit.jupiter.api.Test;

class HiscoreServiceTest {

  @Test
  void load() {
    String url = HiscoreServiceTest.class.getClassLoader().getResource("hiscores.csv").toString();
    String name = "user";
    HiscoreService hiscoreService = new HiscoreService(url);

    Map<Skill, Double> loadedSkills = hiscoreService.load(name);

    assertThat(loadedSkills, notNullValue());
    assertThat(loadedSkills, aMapWithSize(27));
    assertThat(loadedSkills.get(Skill.ATTACK), equalTo(737627D));
    assertThat(loadedSkills.get(Skill.DEFENCE), equalTo(28782069D));
    assertThat(loadedSkills.get(Skill.STRENGTH), equalTo(165576D));
    assertThat(loadedSkills.get(Skill.CONSTITUTION), equalTo(1154D));
    assertThat(loadedSkills.get(Skill.RANGED), equalTo(10692629D));
    assertThat(loadedSkills.get(Skill.PRAYER), equalTo(3597792D));
    assertThat(loadedSkills.get(Skill.MAGIC), equalTo(19368992D));
    assertThat(loadedSkills.get(Skill.COOKING), equalTo(8740D));
    assertThat(loadedSkills.get(Skill.WOODCUTTING), equalTo(203254D));
    assertThat(loadedSkills.get(Skill.FLETCHING), equalTo(12031D));
    assertThat(loadedSkills.get(Skill.FISHING), equalTo(63555443D));
    assertThat(loadedSkills.get(Skill.FIREMAKING), equalTo(2421087D));
    assertThat(loadedSkills.get(Skill.CRAFTING), equalTo(22406D));
    assertThat(loadedSkills.get(Skill.SMITHING), equalTo(6291D));
    assertThat(loadedSkills.get(Skill.MINING), equalTo(3973D));
    assertThat(loadedSkills.get(Skill.HERBLORE), equalTo(3523D));
    assertThat(loadedSkills.get(Skill.AGILITY), equalTo(0D));
    assertThat(loadedSkills.get(Skill.THIEVING), equalTo(0D));
    assertThat(loadedSkills.get(Skill.SLAYER), equalTo(1D));
    assertThat(loadedSkills.get(Skill.FARMING), equalTo(83014D));
    assertThat(loadedSkills.get(Skill.RUNECRAFTING), equalTo(104273167D));
    assertThat(loadedSkills.get(Skill.HUNTER), equalTo(111945D));
    assertThat(loadedSkills.get(Skill.CONSTRUCTION), equalTo(20224D));
    assertThat(loadedSkills.get(Skill.SUMMONING), equalTo(28782069D));
    assertThat(loadedSkills.get(Skill.DUNGEONEERING), equalTo(55649D));
    assertThat(loadedSkills.get(Skill.DIVINATION), equalTo(1833D));
    assertThat(loadedSkills.get(Skill.INVENTION), equalTo(80618654D));
  }
}
