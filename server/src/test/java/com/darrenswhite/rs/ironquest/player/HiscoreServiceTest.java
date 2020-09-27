package com.darrenswhite.rs.ironquest.player;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.Map;
import java.util.Objects;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class HiscoreServiceTest {

  static final String HISCORES_FILE = "hiscores.csv";

  static HiscoreService hiscoreService;

  @BeforeAll
  static void beforeAll() {
    String url = Objects
        .requireNonNull(HiscoreServiceTest.class.getClassLoader().getResource(HISCORES_FILE))
        .toString();

    hiscoreService = new HiscoreService(url);
  }

  @Nested
  class Load {

    @Test
    void shouldParseXPForEachSkill() {
      String name = "user";

      Map<Skill, Double> loadedSkills = hiscoreService.load(name);

      assertThat(loadedSkills, notNullValue());
      assertThat(loadedSkills, aMapWithSize(28));
      assertThat(loadedSkills.get(Skill.ATTACK), is(737627D));
      assertThat(loadedSkills.get(Skill.DEFENCE), is(28782069D));
      assertThat(loadedSkills.get(Skill.STRENGTH), is(165576D));
      assertThat(loadedSkills.get(Skill.CONSTITUTION), is(1154D));
      assertThat(loadedSkills.get(Skill.RANGED), is(10692629D));
      assertThat(loadedSkills.get(Skill.PRAYER), is(3597792D));
      assertThat(loadedSkills.get(Skill.MAGIC), is(19368992D));
      assertThat(loadedSkills.get(Skill.COOKING), is(8740D));
      assertThat(loadedSkills.get(Skill.WOODCUTTING), is(203254D));
      assertThat(loadedSkills.get(Skill.FLETCHING), is(12031D));
      assertThat(loadedSkills.get(Skill.FISHING), is(63555443D));
      assertThat(loadedSkills.get(Skill.FIREMAKING), is(2421087D));
      assertThat(loadedSkills.get(Skill.CRAFTING), is(22406D));
      assertThat(loadedSkills.get(Skill.SMITHING), is(6291D));
      assertThat(loadedSkills.get(Skill.MINING), is(3973D));
      assertThat(loadedSkills.get(Skill.HERBLORE), is(3523D));
      assertThat(loadedSkills.get(Skill.AGILITY), is(0D));
      assertThat(loadedSkills.get(Skill.THIEVING), is(0D));
      assertThat(loadedSkills.get(Skill.SLAYER), is(1D));
      assertThat(loadedSkills.get(Skill.FARMING), is(83014D));
      assertThat(loadedSkills.get(Skill.RUNECRAFTING), is(104273167D));
      assertThat(loadedSkills.get(Skill.HUNTER), is(111945D));
      assertThat(loadedSkills.get(Skill.CONSTRUCTION), is(20224D));
      assertThat(loadedSkills.get(Skill.SUMMONING), is(28782069D));
      assertThat(loadedSkills.get(Skill.DUNGEONEERING), is(55649D));
      assertThat(loadedSkills.get(Skill.DIVINATION), is(1833D));
      assertThat(loadedSkills.get(Skill.INVENTION), is(80618654D));
      assertThat(loadedSkills.get(Skill.ARCHAEOLOGY), is(58376D));
    }
  }
}
