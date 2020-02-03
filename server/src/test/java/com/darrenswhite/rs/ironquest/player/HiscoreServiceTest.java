package com.darrenswhite.rs.ironquest.player;

import static org.hamcrest.MatcherAssert.assertThat;
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
    assertThat(737627D, equalTo(loadedSkills.get(Skill.ATTACK)));
    assertThat(28782069D, equalTo(loadedSkills.get(Skill.DEFENCE)));
    assertThat(165576D, equalTo(loadedSkills.get(Skill.STRENGTH)));
    assertThat(1154D, equalTo(loadedSkills.get(Skill.CONSTITUTION)));
    assertThat(10692629D, equalTo(loadedSkills.get(Skill.RANGED)));
    assertThat(3597792D, equalTo(loadedSkills.get(Skill.PRAYER)));
    assertThat(19368992D, equalTo(loadedSkills.get(Skill.MAGIC)));
    assertThat(8740D, equalTo(loadedSkills.get(Skill.COOKING)));
    assertThat(203254D, equalTo(loadedSkills.get(Skill.WOODCUTTING)));
    assertThat(12031D, equalTo(loadedSkills.get(Skill.FLETCHING)));
    assertThat(63555443D, equalTo(loadedSkills.get(Skill.FISHING)));
    assertThat(2421087D, equalTo(loadedSkills.get(Skill.FIREMAKING)));
    assertThat(22406D, equalTo(loadedSkills.get(Skill.CRAFTING)));
    assertThat(6291D, equalTo(loadedSkills.get(Skill.SMITHING)));
    assertThat(3973D, equalTo(loadedSkills.get(Skill.MINING)));
    assertThat(3523D, equalTo(loadedSkills.get(Skill.HERBLORE)));
    assertThat(0D, equalTo(loadedSkills.get(Skill.AGILITY)));
    assertThat(0D, equalTo(loadedSkills.get(Skill.THIEVING)));
    assertThat(1D, equalTo(loadedSkills.get(Skill.SLAYER)));
    assertThat(83014D, equalTo(loadedSkills.get(Skill.FARMING)));
    assertThat(104273167D, equalTo(loadedSkills.get(Skill.RUNECRAFTING)));
    assertThat(111945D, equalTo(loadedSkills.get(Skill.HUNTER)));
    assertThat(20224D, equalTo(loadedSkills.get(Skill.CONSTRUCTION)));
    assertThat(28782069D, equalTo(loadedSkills.get(Skill.SUMMONING)));
    assertThat(55649D, equalTo(loadedSkills.get(Skill.DUNGEONEERING)));
    assertThat(1833D, equalTo(loadedSkills.get(Skill.DIVINATION)));
    assertThat(80618654D, equalTo(loadedSkills.get(Skill.INVENTION)));
  }
}
