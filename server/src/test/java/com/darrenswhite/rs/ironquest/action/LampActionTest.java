package com.darrenswhite.rs.ironquest.action;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.darrenswhite.rs.ironquest.dto.LampActionDTO;
import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.Skill;
import com.darrenswhite.rs.ironquest.quest.Quest;
import com.darrenswhite.rs.ironquest.quest.reward.LampReward;
import com.darrenswhite.rs.ironquest.quest.reward.LampType;
import com.darrenswhite.rs.ironquest.util.MapBuilder;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
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

class LampActionTest {

  @Nested
  class GetType {

    @Test
    void shouldReturnCorrectType() {
      Quest quest = new Quest.Builder().build();
      Player player = new Player.Builder().build();
      LampReward lampReward = new LampReward.Builder().build();
      Set<Skill> skills = Collections.emptySet();

      LampAction lampAction = new LampAction(player, false, quest, lampReward, skills);

      assertThat(lampAction.getType(), equalTo(ActionType.LAMP));
    }
  }

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class GetMessage {

    @ParameterizedTest
    @MethodSource("shouldFormatMessage")
    void shouldFormatMessage(String title, LampType lampType, int xp, Set<Skill> skills,
        boolean future, double multiplier, String message) {
      Quest quest = new Quest.Builder().withDisplayName(title).build();
      Map<Skill, Double> skillXps = new MapBuilder<Skill, Double>().put(Skill.DEFENCE, 50000d)
          .put(Skill.MAGIC, 750000d).put(Skill.RANGED, 1000000d).put(Skill.THIEVING, 5000000d)
          .put(Skill.HERBLORE, 9000000d).build();
      Player player = new Player.Builder().withSkillXps(skillXps).build();
      LampReward lampReward = new LampReward.Builder().withType(lampType).withXp(xp)
          .withMultiplier(multiplier).build();

      LampAction lampAction = new LampAction(player, future, quest, lampReward, skills);

      assertThat(lampAction.getMessage(), equalTo(message));
      assertThat(lampAction.toString(), equalTo(lampAction.getMessage()));
    }

    Stream<Arguments> shouldFormatMessage() {
      return Stream.of(Arguments
          .of("playerA", LampType.XP, 500, Collections.singleton(Skill.ATTACK), false, 1.0,
              "playerA: Use XP Lamp on Attack to gain 500 xp"), Arguments
          .of("playerB", LampType.XP, 1000, Collections.emptySet(), true, 1.0,
              "playerB: Use XP Lamp to gain 1k xp (when requirements are met)"), Arguments
          .of("playerC", LampType.SMALL_XP, 0, Collections.singleton(Skill.DEFENCE), false, 1.0,
              "playerC: Use Small XP Lamp on Defence to gain 784 xp"), Arguments
          .of("playerD", LampType.MEDIUM_XP, 0, Collections.singleton(Skill.MAGIC), false, 1.0,
              "playerD: Use Medium XP Lamp on Magic to gain 5.185k xp"), Arguments
          .of("playerE", LampType.LARGE_XP, 0, Collections.singleton(Skill.RANGED), false, 1.0,
              "playerE: Use Large XP Lamp on Ranged to gain 11.786k xp"), Arguments
          .of("playerF", LampType.HUGE_XP, 0, Collections.singleton(Skill.THIEVING), false, 1.0,
              "playerF: Use Huge XP Lamp on Thieving to gain 47.38k xp"), Arguments
          .of("playerG", LampType.DRAGONKIN, 0, Collections.singleton(Skill.HERBLORE), false, 1.0,
              "playerG: Use Dragonkin Lamp on Herblore to gain 42.441k xp"), Arguments
          .of("playerH", LampType.XP, 150,
              new LinkedHashSet<>(Arrays.asList(Skill.SUMMONING, Skill.DIVINATION)), false, 1.0,
              "playerH: Use XP Lamp on Summoning, Divination to gain 150 xp"), Arguments
          .of("playerI", LampType.XP, 125, Collections.singleton(Skill.CONSTRUCTION), false, 1.5,
              "playerI: Use XP Lamp on Construction to gain 187.5 xp"));
    }
  }

  @Nested
  class MeetsRequirements {

    @Test
    void shouldCallMeetsRequirementsOnLampReward() {
      Quest quest = new Quest.Builder().build();
      Player player = new Player.Builder().build();
      LampReward lampReward = mock(LampReward.class);
      Set<Skill> skills = Collections.emptySet();

      LampAction lampAction = new LampAction(player, false, quest, lampReward, skills);

      lampAction.meetsRequirements(player);

      verify(lampReward).meetsRequirements(player);
    }
  }

  @Nested
  class Process {

    @Test
    void shouldAddXPForEachSkillToPlayer() {
      Quest quest = new Quest.Builder().build();
      Player player = new Player.Builder().build();
      LampReward lampReward = new LampReward.Builder().withType(LampType.XP).withXp(1000).build();
      Set<Skill> skills = new HashSet<>(Arrays.asList(Skill.DEFENCE, Skill.STRENGTH));

      LampAction lampAction = new LampAction(player, false, quest, lampReward, skills);

      lampAction.process(player);

      assertThat(player.getXp(Skill.DEFENCE), equalTo(1000D));
      assertThat(player.getXp(Skill.STRENGTH), equalTo(1000D));
    }
  }

  @Nested
  class CreateDTO {

    @Test
    void shouldCreateWithCorrectFields() {
      String displayName = "displayName";
      Quest quest = new Quest.Builder().withDisplayName(displayName).build();
      Player player = new Player.Builder().build();
      LampReward lampReward = new LampReward.Builder().withType(LampType.XP).withXp(500).build();
      Set<Skill> skills = Collections.singleton(Skill.PRAYER);

      LampAction lampAction = new LampAction(player, false, quest, lampReward, skills);

      LampActionDTO dto = lampAction.createDTO();

      assertThat(dto.getMessage(), equalTo(lampAction.getMessage()));
      assertThat(dto.getQuest(), equalTo(quest.createDTO()));
      assertThat(dto.getPlayer(), equalTo(player.createDTO()));
      assertThat(dto.getType(), equalTo(ActionType.LAMP));
      assertThat(dto.isFuture(), equalTo(false));
    }
  }

  @Nested
  class CopyForPlayer {

    @Test
    void shouldCopyAllValues() {
      Quest quest = new Quest.Builder().build();
      Player player = new Player.Builder().withName("original").build();
      Player playerToCopy = new Player.Builder().withName("copy").build();

      LampReward lampReward = new LampReward.Builder().build();
      Set<Skill> skills = Collections.singleton(Skill.ATTACK);

      LampAction lampAction = new LampAction(player, false, quest, lampReward, skills);

      LampAction copied = lampAction.copyForPlayer(playerToCopy);

      assertThat(copied.getQuest(), equalTo(quest));
      assertThat(copied.getLampReward(), equalTo(lampReward));
      assertThat(copied.getSkills(), equalTo(skills));
      assertThat(copied.getPlayer(), equalTo(playerToCopy));
      assertThat(copied, not(sameInstance(lampAction)));
    }
  }

  @Nested
  class Equals {

    @Test
    void shouldVerifyEqualsAndHashCode() {
      EqualsVerifier.forClass(LampAction.class)
          .withPrefabValues(Quest.class, new Quest.Builder(0).build(), new Quest.Builder(1).build())
          .verify();
    }
  }
}
