package com.darrenswhite.rs.ironquest.quest;

import static com.darrenswhite.rs.ironquest.Matchers.hasPropertyAtPath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

import com.darrenswhite.rs.ironquest.player.QuestEntry;
import com.darrenswhite.rs.ironquest.player.QuestPriority;
import com.darrenswhite.rs.ironquest.player.Skill;
import com.darrenswhite.rs.ironquest.quest.requirement.CombatRequirement;
import com.darrenswhite.rs.ironquest.quest.requirement.QuestPointsRequirement;
import com.darrenswhite.rs.ironquest.quest.requirement.QuestRequirement;
import com.darrenswhite.rs.ironquest.quest.requirement.QuestRequirements;
import com.darrenswhite.rs.ironquest.quest.requirement.SkillRequirement;
import com.darrenswhite.rs.ironquest.quest.reward.LampReward;
import com.darrenswhite.rs.ironquest.quest.reward.LampType;
import com.darrenswhite.rs.ironquest.quest.reward.QuestRewards;
import com.darrenswhite.rs.ironquest.util.MapBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.InputStreamResource;

class QuestServiceTest {

  static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  static final String QUESTS_FILE = "quests-minimal.json";

  static QuestService questService;

  @BeforeAll
  static void beforeAll() throws IOException {
    InputStream in = QuestServiceTest.class.getClassLoader().getResourceAsStream(QUESTS_FILE);

    questService = new QuestService(new InputStreamResource(in), OBJECT_MAPPER);
  }

  static class QuestMatcher extends TypeSafeMatcher<Quest> {

    private final Quest quest;

    QuestMatcher(Quest quest) {
      this.quest = quest;
    }

    @Override
    public void describeTo(Description description) {
      description.appendText("should match quest ").appendValue(quest);
    }

    @Override
    protected boolean matchesSafely(Quest item) {
      return quest.getId() == item.getId() && Objects.equals(quest.getTitle(), item.getTitle())
          && Objects.equals(quest.getDisplayName(), item.getDisplayName())
          && quest.getAccess() == item.getAccess() && quest.getType() == item.getType() && Objects
          .equals(quest.getRequirements(), item.getRequirements()) && Objects
          .equals(quest.getRewards(), item.getRewards());
    }
  }

  @Nested
  class GetQuests {

    @Test
    void shouldParseAllQuests() {
      Quest questA = new Quest.Builder(-1).withAccess(QuestAccess.FREE)
          .withRewards(new QuestRewards.Builder().withQuestPoints(1).build()).withTitle("a")
          .withType(QuestType.QUEST).build();
      Quest questB = new Quest.Builder(0).withAccess(QuestAccess.FREE).withRequirements(
          new QuestRequirements.Builder()
              .withCombat(new CombatRequirement.Builder().withLevel(40).withIronman(true).build())
              .withSkills(new HashSet<>(Arrays.asList(
                  new SkillRequirement.Builder().withLevel(50).withSkill(Skill.AGILITY).build(),
                  new SkillRequirement.Builder().withLevel(60).withSkill(Skill.AGILITY)
                      .withRecommended(true).build()))).build())
          .withRewards(new QuestRewards.Builder().withQuestPoints(2).build()).withTitle("b")
          .withType(QuestType.SAGA).build();
      Quest questC = new Quest.Builder(1).withAccess(QuestAccess.MEMBERS).withRewards(
          new QuestRewards.Builder().withLamps(new HashSet<>(Arrays.asList(
              new LampReward.Builder().withExclusive(true).withRequirements(
                  new MapBuilder<Set<Skill>, Integer>()
                      .put(new HashSet<>(Arrays.asList(Skill.ATTACK, Skill.DEFENCE)), 1)
                      .put(new HashSet<>(Arrays.asList(Skill.CONSTITUTION, Skill.STRENGTH)), 1)
                      .build()).withType(LampType.XP).withXp(35000).build(),
              new LampReward.Builder().withExclusive(true).withRequirements(
                  new MapBuilder<Set<Skill>, Integer>()
                      .put(new HashSet<>(Arrays.asList(Skill.ATTACK, Skill.DEFENCE)), 1)
                      .put(new HashSet<>(Arrays.asList(Skill.CONSTITUTION, Skill.STRENGTH)), 1)
                      .build()).withType(LampType.XP).withXp(20000).build()))).withQuestPoints(5)
              .build()).withTitle("c").withType(QuestType.SAGA).build();
      Quest questD = new Quest.Builder(2).withAccess(QuestAccess.MEMBERS).withRequirements(
          new QuestRequirements.Builder()
              .withCombat(new CombatRequirement.Builder().withLevel(40).build())
              .withQuestPoints(new QuestPointsRequirement.Builder().withAmount(2).build())
              .withQuests(new HashSet<>(Arrays.asList(new QuestRequirement.Builder(questB).build(),
                  new QuestRequirement.Builder(questC).build()))).withSkills(new HashSet<>(Arrays
              .asList(
                  new SkillRequirement.Builder().withLevel(30).withSkill(Skill.HERBLORE).build(),
                  new SkillRequirement.Builder().withLevel(50).withSkill(Skill.RANGED).build())))
              .build()).withRewards(new QuestRewards.Builder().withLamps(new HashSet<>(Arrays
          .asList(new LampReward.Builder().withType(LampType.SMALL_XP).build(),
              new LampReward.Builder().withRequirements(new MapBuilder<Set<Skill>, Integer>()
                  .put(new HashSet<>(Arrays.asList(Skill.MINING, Skill.SMITHING)), 10).build())
                  .withSingleChoice(true).withType(LampType.XP).withXp(1000).build(),
              new LampReward.Builder().withMultiplier(1.5).withRequirements(
                  new MapBuilder<Set<Skill>, Integer>().put(Collections.singleton(Skill.AGILITY), 1)
                      .build()).withType(LampType.MEDIUM_XP).build(),
              new LampReward.Builder().withType(LampType.DRAGONKIN).build(),
              new LampReward.Builder().withRequirements(new MapBuilder<Set<Skill>, Integer>()
                  .put(Collections.singleton(Skill.AGILITY), 20)
                  .put(Collections.singleton(Skill.ATTACK), 20)
                  .put(Collections.singleton(Skill.CONSTITUTION), 20)
                  .put(Collections.singleton(Skill.CONSTRUCTION), 20)
                  .put(Collections.singleton(Skill.COOKING), 20)
                  .put(Collections.singleton(Skill.CRAFTING), 20)
                  .put(Collections.singleton(Skill.DEFENCE), 20)
                  .put(Collections.singleton(Skill.DIVINATION), 20)
                  .put(Collections.singleton(Skill.DUNGEONEERING), 20)
                  .put(Collections.singleton(Skill.FARMING), 20)
                  .put(Collections.singleton(Skill.FIREMAKING), 20)
                  .put(Collections.singleton(Skill.FISHING), 20)
                  .put(Collections.singleton(Skill.FLETCHING), 20)
                  .put(Collections.singleton(Skill.HERBLORE), 20)
                  .put(Collections.singleton(Skill.HUNTER), 20)
                  .put(Collections.singleton(Skill.INVENTION), 20)
                  .put(Collections.singleton(Skill.MAGIC), 20)
                  .put(Collections.singleton(Skill.MINING), 20)
                  .put(Collections.singleton(Skill.PRAYER), 20)
                  .put(Collections.singleton(Skill.RANGED), 20)
                  .put(Collections.singleton(Skill.RUNECRAFTING), 20)
                  .put(Collections.singleton(Skill.SLAYER), 20)
                  .put(Collections.singleton(Skill.SMITHING), 20)
                  .put(Collections.singleton(Skill.STRENGTH), 20)
                  .put(Collections.singleton(Skill.SUMMONING), 20)
                  .put(Collections.singleton(Skill.THIEVING), 20)
                  .put(Collections.singleton(Skill.WOODCUTTING), 20).build()).withType(LampType.XP)
                  .withXp(1000).build(), new LampReward.Builder().withRequirements(
                  new MapBuilder<Set<Skill>, Integer>().put(new HashSet<>(Arrays
                      .asList(Skill.AGILITY, Skill.ATTACK, Skill.CONSTITUTION, Skill.CONSTRUCTION,
                          Skill.COOKING, Skill.CRAFTING, Skill.DEFENCE, Skill.DIVINATION,
                          Skill.DUNGEONEERING, Skill.FARMING, Skill.FIREMAKING, Skill.FISHING,
                          Skill.FLETCHING, Skill.HERBLORE, Skill.HUNTER, Skill.INVENTION,
                          Skill.MAGIC, Skill.MINING, Skill.PRAYER, Skill.RANGED, Skill.RUNECRAFTING,
                          Skill.SLAYER, Skill.SMITHING, Skill.STRENGTH, Skill.SUMMONING,
                          Skill.THIEVING, Skill.WOODCUTTING)), 10).build()).withType(LampType.XP)
                  .withXp(100).build()))).withQuestPoints(3).build()).withTitle("d")
          .withType(QuestType.MINIQUEST).build();

      Set<Quest> loadedQuests = questService.getQuests().getQuests();

      assertThat(loadedQuests, notNullValue());
      assertThat(loadedQuests, hasSize(4));
      assertThat(loadedQuests,
          containsInAnyOrder(new QuestMatcher(questA), new QuestMatcher(questB),
              new QuestMatcher(questC), new QuestMatcher(questD)));
    }

    @Test
    void shouldParseNestedQuestRequirements() {
      Quest questB = new Quest.Builder(0).withAccess(QuestAccess.FREE).withRequirements(
          new QuestRequirements.Builder()
              .withCombat(new CombatRequirement.Builder().withLevel(40).withIronman(true).build())
              .withSkills(new HashSet<>(Arrays.asList(
                  new SkillRequirement.Builder().withLevel(50).withSkill(Skill.AGILITY).build(),
                  new SkillRequirement.Builder().withLevel(60).withSkill(Skill.AGILITY)
                      .withRecommended(true).build()))).build())
          .withRewards(new QuestRewards.Builder().withQuestPoints(2).build()).withTitle("b")
          .withType(QuestType.SAGA).build();
      Quest questC = new Quest.Builder(1).withAccess(QuestAccess.MEMBERS).withRewards(
          new QuestRewards.Builder().withLamps(new HashSet<>(Arrays.asList(
              new LampReward.Builder().withExclusive(true).withRequirements(
                  new MapBuilder<Set<Skill>, Integer>()
                      .put(new HashSet<>(Arrays.asList(Skill.ATTACK, Skill.DEFENCE)), 1)
                      .put(new HashSet<>(Arrays.asList(Skill.CONSTITUTION, Skill.STRENGTH)), 1)
                      .build()).withType(LampType.XP).withXp(35000).build(),
              new LampReward.Builder().withExclusive(true).withRequirements(
                  new MapBuilder<Set<Skill>, Integer>()
                      .put(new HashSet<>(Arrays.asList(Skill.ATTACK, Skill.DEFENCE)), 1)
                      .put(new HashSet<>(Arrays.asList(Skill.CONSTITUTION, Skill.STRENGTH)), 1)
                      .build()).withType(LampType.XP).withXp(20000).build()))).withQuestPoints(5)
              .build()).withTitle("c").withType(QuestType.SAGA).build();

      Set<Quest> loadedQuests = questService.getQuests().getQuests();

      Quest questWithQuestRequirements = loadedQuests.stream().filter(quest -> quest.getId() == 2)
          .findFirst().orElse(null);

      assertThat(questWithQuestRequirements, notNullValue());

      Set<QuestRequirement> questRequirements = questWithQuestRequirements.getRequirements()
          .getQuests();

      assertThat(questRequirements, notNullValue());
      assertThat(questRequirements, hasSize(2));

      List<Quest> quests = questRequirements.stream().map(QuestRequirement::getQuest)
          .collect(Collectors.toList());

      assertThat(quests, containsInAnyOrder(new QuestMatcher(questB), new QuestMatcher(questC)));
    }
  }

  @Nested
  class CreateQuestEntries {

    @Test
    void shouldCreateEntryForAllQuestsWithNormalPriority() {
      Set<QuestEntry> questEntries = questService.getQuests()
          .createQuestEntries(Collections.emptyMap(), QuestAccessFilter.ALL, QuestTypeFilter.ALL);

      assertThat(questEntries, notNullValue());
      assertThat(questEntries, hasSize(4));
      assertThat(questEntries, everyItem(hasProperty("priority", equalTo(QuestPriority.NORMAL))));
    }

    @Test
    void shouldCreateEntryForAllQuestsWithGivenPriority() {
      QuestPriority priorityA = QuestPriority.MAXIMUM;
      QuestPriority priorityB = QuestPriority.NORMAL;
      QuestPriority priorityC = QuestPriority.LOW;
      QuestPriority priorityD = QuestPriority.HIGH;

      Set<QuestEntry> questEntries = questService.getQuests().createQuestEntries(
          new MapBuilder<Integer, QuestPriority>().put(-1, priorityA).put(0, priorityB)
              .put(1, priorityC).put(2, priorityD).build(), QuestAccessFilter.ALL,
          QuestTypeFilter.ALL);

      assertThat(questEntries, notNullValue());
      assertThat(questEntries, hasSize(4));
      assertThat(questEntries, containsInAnyOrder(allOf(hasPropertyAtPath("quest.id", equalTo(-1)),
          hasProperty("priority", equalTo(QuestPriority.MAXIMUM))),
          allOf(hasPropertyAtPath("quest.id", equalTo(0)),
              hasProperty("priority", equalTo(QuestPriority.NORMAL))),
          allOf(hasPropertyAtPath("quest.id", equalTo(1)),
              hasProperty("priority", equalTo(QuestPriority.LOW))),
          allOf(hasPropertyAtPath("quest.id", equalTo(2)),
              hasProperty("priority", equalTo(QuestPriority.HIGH)))));
    }

    @Test
    void shouldCreateEntryForAllFreeQuests() {
      Set<QuestEntry> questEntries = questService.getQuests()
          .createQuestEntries(Collections.emptyMap(), QuestAccessFilter.FREE, QuestTypeFilter.ALL);

      assertThat(questEntries, notNullValue());
      assertThat(questEntries, hasSize(2));
      assertThat(questEntries,
          everyItem(hasPropertyAtPath("quest.access", equalTo(QuestAccess.FREE))));
    }

    @Test
    void shouldCreateEntryForAllMembersQuests() {
      Set<QuestEntry> questEntries = questService.getQuests()
          .createQuestEntries(Collections.emptyMap(), QuestAccessFilter.MEMBERS,
              QuestTypeFilter.ALL);

      assertThat(questEntries, notNullValue());
      // quest requirements are also included
      assertThat(questEntries, hasSize(3));
      assertThat(questEntries, containsInAnyOrder(allOf(hasPropertyAtPath("quest.id", equalTo(0)),
          hasPropertyAtPath("quest.access", equalTo(QuestAccess.FREE))),
          allOf(hasPropertyAtPath("quest.id", equalTo(1)),
              hasPropertyAtPath("quest.access", equalTo(QuestAccess.MEMBERS))),
          allOf(hasPropertyAtPath("quest.id", equalTo(2)),
              hasPropertyAtPath("quest.access", equalTo(QuestAccess.MEMBERS)))));
    }

    @Test
    void shouldCreateEntryForAllMiniquests() {
      Set<QuestEntry> questEntries = questService.getQuests()
          .createQuestEntries(Collections.emptyMap(), QuestAccessFilter.ALL,
              QuestTypeFilter.MINIQUESTS);

      assertThat(questEntries, notNullValue());
      // quest requirements are also included
      assertThat(questEntries, hasSize(3));
      assertThat(questEntries, containsInAnyOrder(allOf(hasPropertyAtPath("quest.id", equalTo(0)),
          hasPropertyAtPath("quest.type", equalTo(QuestType.SAGA))),
          allOf(hasPropertyAtPath("quest.id", equalTo(1)),
              hasPropertyAtPath("quest.type", equalTo(QuestType.SAGA))),
          allOf(hasPropertyAtPath("quest.id", equalTo(2)),
              hasPropertyAtPath("quest.type", equalTo(QuestType.MINIQUEST)))));
    }

    @Test
    void shouldCreateEntryForAllSagas() {
      Set<QuestEntry> questEntries = questService.getQuests()
          .createQuestEntries(Collections.emptyMap(), QuestAccessFilter.ALL, QuestTypeFilter.SAGAS);

      assertThat(questEntries, notNullValue());
      assertThat(questEntries, hasSize(2));
      assertThat(questEntries, containsInAnyOrder(allOf(hasPropertyAtPath("quest.id", equalTo(0)),
          hasPropertyAtPath("quest.type", equalTo(QuestType.SAGA))),
          allOf(hasPropertyAtPath("quest.id", equalTo(1)),
              hasPropertyAtPath("quest.type", equalTo(QuestType.SAGA)))));
    }

    @Test
    void shouldCreateEntryForAllQuests() {
      Set<QuestEntry> questEntries = questService.getQuests()
          .createQuestEntries(Collections.emptyMap(), QuestAccessFilter.ALL,
              QuestTypeFilter.QUESTS);

      assertThat(questEntries, notNullValue());
      assertThat(questEntries, hasSize(1));
      assertThat(questEntries, containsInAnyOrder(allOf(hasPropertyAtPath("quest.id", equalTo(-1)),
          hasPropertyAtPath("quest.type", equalTo(QuestType.QUEST)))));
    }
  }
}
