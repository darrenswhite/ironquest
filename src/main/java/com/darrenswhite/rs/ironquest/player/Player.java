package com.darrenswhite.rs.ironquest.player;

import com.darrenswhite.rs.ironquest.action.Action;
import com.darrenswhite.rs.ironquest.action.LampAction;
import com.darrenswhite.rs.ironquest.action.QuestAction;
import com.darrenswhite.rs.ironquest.action.TrainAction;
import com.darrenswhite.rs.ironquest.quest.Quest;
import com.darrenswhite.rs.ironquest.quest.RuneMetricsQuest;
import com.darrenswhite.rs.ironquest.quest.requirement.Requirement;
import com.darrenswhite.rs.ironquest.quest.requirement.SkillRequirement;
import com.darrenswhite.rs.ironquest.quest.reward.LampReward;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A class representing a player.
 *
 * @author Darren S. White
 */
public class Player {

  /**
   * URL for Hiscores
   */
  private static final String URL_HISCORES_LITE = "https://services.runescape.com/m=hiscore/index_lite.ws?player=";

  /**
   * URL for RuneMetrics quest data
   */
  private static final String URL_RUNE_METRICS_QUESTS = "https://apps.runescape.com/runemetrics/quests?user=";

  private static final Logger LOG = LogManager.getLogger(Player.class);

  private String name;
  private Map<Skill, Double> skillXps = new EnumMap<>(Skill.INITIAL_XPS);
  private Set<QuestEntry> quests = new HashSet<>();
  private Set<Skill> lampSkills = new LinkedHashSet<>();
  private boolean ironman = false;
  private boolean recommended = false;
  private boolean free = true;
  private boolean members = true;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Map<Skill, Double> getSkillXps() {
    return skillXps;
  }

  public void setSkillXps(Map<Skill, Double> skillXps) {
    this.skillXps = skillXps;
  }

  public Set<QuestEntry> getQuests() {
    return quests;
  }

  public void setQuests(Set<QuestEntry> quests) {
    this.quests = quests;
  }

  public Set<Skill> getLampSkills() {
    return lampSkills;
  }

  public void setLampSkills(Set<Skill> lampSkills) {
    this.lampSkills = lampSkills;
  }

  public boolean isIronman() {
    return ironman;
  }

  public void setIronman(boolean ironman) {
    this.ironman = ironman;
  }

  public boolean isRecommended() {
    return recommended;
  }

  public void setRecommended(boolean recommended) {
    this.recommended = recommended;
  }

  public boolean isFree() {
    return free;
  }

  public void setFree(boolean free) {
    this.free = free;
  }

  public boolean isMembers() {
    return members;
  }

  public void setMembers(boolean members) {
    this.members = members;
  }

  public Player copy() {
    Player player = new Player();

    player.setName(name);
    player.setSkillXps(new EnumMap<>(skillXps));
    player.setQuests(
        quests.stream().map(e -> new QuestEntry(e.getQuest(), e.getStatus(), e.getPriority()))
            .collect(Collectors.toSet()));
    player.setLampSkills(new LinkedHashSet<>(lampSkills));
    player.setIronman(ironman);
    player.setRecommended(recommended);
    player.setFree(free);
    player.setMembers(members);

    return player;
  }

  public Map<Skill, Integer> getLevels() {
    return skillXps.entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getKey().getLevelAt(e.getValue())));
  }

  public int getQuestPoints() {
    return getCompletedQuests().stream().mapToInt(q -> q.getQuest().getQuestPointsReward()).sum();
  }

  public Set<QuestEntry> getCompletedQuests() {
    return quests.stream().filter(e -> e.getStatus() == QuestStatus.COMPLETED)
        .collect(Collectors.toSet());
  }

  /**
   * Gets all quests which are not completed. This also filters members/free quests.
   *
   * @return set of incomplete quests
   */
  public Set<QuestEntry> getIncompleteQuests() {
    return quests.stream().filter(e -> {
      boolean membersQuest = e.getQuest().isMembers();

      if (!members && membersQuest || !free && !membersQuest) {
        return false;
      }

      return e.getStatus() != QuestStatus.COMPLETED;
    }).collect(Collectors.toSet());
  }

  public int getTotalLevel() {
    return getLevels().values().stream().mapToInt(Integer::intValue).sum();
  }

  public double getCombatLevel() {
    // Combat equation found here:
    // http://runescape.wikia.com/wiki/Combat_level

    double attack = getLevel(Skill.ATTACK);
    double constitution = getLevel(Skill.CONSTITUTION);
    double defence = getLevel(Skill.DEFENCE);
    double magic = getLevel(Skill.MAGIC);
    double prayer = getLevel(Skill.PRAYER);
    double range = getLevel(Skill.RANGED);
    double strength = getLevel(Skill.STRENGTH);
    double summoning = getLevel(Skill.SUMMONING);

    double max = Math.max(attack + strength, Math.max(2 * magic, 2 * range));

    max *= 13d / 10d;

    return (max + defence + constitution + Math.floor(prayer / 2) + Math.floor(summoning / 2)) / 4;
  }

  public int getLevel(Skill s) {
    return s.getLevelAt(getXp(s));
  }

  public double getXp(Skill s) {
    return skillXps.get(s);
  }

  public void addSkillXP(Skill s, double xp) {
    double newXp = skillXps.getOrDefault(s, 0d) + xp;

    if (newXp >= 0) {
      skillXps.put(s, newXp);
    }
  }

  public boolean isQuestCompleted(Quest quest) {
    return quests.stream()
        .anyMatch(e -> e.getQuest().equals(quest) && e.getStatus() == QuestStatus.COMPLETED);
  }

  public boolean isQuestEntryCompleted(QuestEntry entry) {
    return quests.stream().anyMatch(e -> e.equals(entry) && e.getStatus() == QuestStatus.COMPLETED);
  }

  public Set<Action> completeQuest(QuestEntry entry) {
    Set<Action> actions = new LinkedHashSet<>();
    Quest quest = entry.getQuest();

    if (isQuestCompleted(quest)) {
      throw new IllegalArgumentException("Quest already completed: " + quest.getId());
    } else if (!quest.meetsCombatRequirement(this) || !quest.meetsQuestPointRequirement(this)
        || !quest.meetsQuestRequirements(this)) {
      throw new IllegalArgumentException("Unmet requirements for quest: " + quest.getId());
    } else if (!quest.meetsSkillRequirements(this)) {
      for (SkillRequirement sr : quest.getRemainingSkillRequirements(this, false)) {
        actions.add(createTrainAction(sr));
      }
    }

    actions.add(new QuestAction(this, entry));

    for (LampReward lampReward : quest.getLampRewards()) {
      LampAction lampAction = createLampAction(entry, lampReward);

      actions.add(lampAction);
    }

    return actions;
  }

  public void load() {
    if (name != null && !name.trim().isEmpty()) {
      try {
        loadHiscores();
      } catch (IOException e) {
        LOG.warn("Failed to load hiscores for player: {}", name, e);
      }

      try {
        loadQuests();
      } catch (IOException e) {
        LOG.warn("Failed to load quests for player: {}", name, e);
      }
    }
  }

  public void reset() {
    for (Skill s : Skill.values()) {
      skillXps.put(s, Skill.INITIAL_XPS.get(s));
    }

    quests.forEach(e -> {
      e.setStatus(QuestStatus.NOT_STARTED);
      e.getPreviousLampSkills().clear();
    });
  }

  /**
   * Gets the 'best' {@link QuestEntry} to be completed next if any. If no 'best' {@link QuestEntry}
   * if available then the 'nearest' {@link QuestEntry} will be returned.
   * <p>
   * The 'best' {@link QuestEntry} is determined by this {@link Player} meeting all {@link
   * Requirement}'s for a {@link Quest} and then getting the maximum {@link QuestEntry} compared by
   * {@link QuestPriority}.
   * <p>
   * The 'nearest {@link QuestEntry} is determined by this {@link Player} meeting all {@link
   * Requirement}'s (including all {@link LampReward} {@link Requirement}'s but excluding {@link
   * SkillRequirement}'s) and then getting the minimum {@link QuestEntry} compared by the total
   * remaining {@link SkillRequirement}'s.
   *
   * @return The best {@link QuestEntry} to be completed
   */
  public Optional<QuestEntry> getBestQuest(Collection<QuestEntry> quests) {
    return quests.stream().filter(e -> e.getQuest().meetsCombatRequirement(this) && e.getQuest()
        .meetsQuestPointRequirement(this) && e.getQuest().meetsQuestRequirements(this))
        .reduce((first, second) -> {
          boolean firstSkillRequirements = first.getQuest().meetsSkillRequirements(this);
          boolean secondSkillRequirements = second.getQuest().meetsSkillRequirements(this);

          if (firstSkillRequirements && secondSkillRequirements) {
            return compareQuestByPriority(first, second);
          } else if (firstSkillRequirements) {
            return first;
          } else if (secondSkillRequirements) {
            return second;
          } else {
            return compareQuestBySkillRequirements(first, second);
          }
        });
  }

  public LampAction createLampAction(QuestEntry questEntry, LampReward lampReward) {
    Set<Skill> bestSkills = new HashSet<>();
    boolean future = true;

    if (lampReward.meetsRequirements(this)) {
      Set<Set<Skill>> previous = questEntry.getPreviousLampSkills();

      bestSkills = getBestLampSkills(lampReward, previous);
      future = false;

      previous.add(bestSkills);
    }

    return new LampAction(this, future, questEntry, lampReward, bestSkills);
  }

  private TrainAction createTrainAction(SkillRequirement skillRequirement) {
    Skill skill = skillRequirement.getSkill();
    double currentXp = getXp(skill);
    double requirementXp = skill.getXpAtLevel(skillRequirement.getLevel());

    return new TrainAction(this, skill, currentXp, requirementXp);
  }

  private QuestEntry compareQuestByPriority(QuestEntry first, QuestEntry second) {
    int priorityComparison = first.getPriority().compareTo(second.getPriority());

    if (priorityComparison != 0) {
      if (priorityComparison > 0) {
        return first;
      } else {
        return second;
      }
    } else {
      return getQuestPriority(first.getQuest()) > getQuestPriority(second.getQuest()) ? first
          : second;
    }
  }

  private QuestEntry compareQuestBySkillRequirements(QuestEntry first, QuestEntry second) {
    return first.getQuest().getTotalRemainingSkillRequirements(this, true) > second.getQuest()
        .getTotalRemainingSkillRequirements(this, true) ? second : first;
  }

  private double getQuestPriority(Quest quest) {
    int requirements = quest.getTotalRemainingSkillRequirements(this, true);
    double rewards = (quest.getTotalLampRewardsXp(this) + quest.getTotalXpRewards()) / 100;
    return rewards - requirements;
  }

  private Set<Skill> getBestLampSkills(LampReward lampReward, Set<Set<Skill>> previous) {
    Set<Set<Skill>> lampSkillChoices = lampReward.getRequirements().entrySet().stream().filter(
        e -> e.getKey().stream().noneMatch(s -> getLevel(s) < e.getValue()) && (
            !lampReward.isExclusive() || !previous.contains(e.getKey()))).map(Map.Entry::getKey)
        .collect(Collectors.toSet());
    Set<Set<Skill>> skillChoices = new LinkedHashSet<>();

    for (Skill s : lampSkills) {
      skillChoices
          .addAll(lampSkillChoices.stream().filter(c -> c.contains(s)).collect(Collectors.toSet()));
    }

    if (!skillChoices.isEmpty()) {
      lampSkillChoices = skillChoices;
    }

    Map<Skill, Double> xpRequirements = calculateRemainingXpRequirements();

    Map<Set<Skill>, Double> xpChoicesRequirements = lampSkillChoices.stream().collect(Collectors
        .toMap(s -> s,
            s -> s.stream().mapToDouble(sk -> xpRequirements.getOrDefault(sk, 0d)).sum()));

    Optional<Map.Entry<Set<Skill>, Double>> choice = xpChoicesRequirements.entrySet().stream()
        .max(Comparator.comparingDouble(Map.Entry::getValue));

    if (!choice.isPresent()) {
      throw new IllegalStateException(
          "Unable to use lampReward: no suitable skill found: lampReward=" + lampReward
              + ",previous=" + previous + ",lampSkills=" + lampSkills);
    }

    return choice.get().getKey();
  }

  private Map<Skill, Double> calculateRemainingXpRequirements() {
    Map<Skill, Double> remaining = new EnumMap<>(Skill.class);
    Set<SkillRequirement> maxRequirements = calculateMaxRequirements();

    maxRequirements.forEach(r -> {
      Skill s = r.getSkill();
      int lvl = r.getLevel();

      remaining.put(s, s.getXpAtLevel(lvl) - getXp(s));
    });

    return remaining;
  }

  private Set<SkillRequirement> calculateMaxRequirements() {
    Set<SkillRequirement> requirements = new HashSet<>();

    for (QuestEntry entry : quests) {
      requirements = SkillRequirement
          .merge(requirements, entry.getQuest().getRemainingSkillRequirements(this, false));
    }

    return requirements;
  }

  private void loadHiscores() throws IOException {
    CSVFormat format = CSVFormat.DEFAULT.withDelimiter(',');

    try (InputStreamReader in = new InputStreamReader(
        new URL(URL_HISCORES_LITE + URLEncoder.encode(name, "UTF-8")).openStream())) {
      CSVParser parser = format.parse(in);
      List<CSVRecord> records = parser.getRecords();

      for (int i = 1; i < Skill.values().length + 1; i++) {
        Optional<Skill> skill = Skill.tryGet(i);
        CSVRecord r = records.get(i);

        skill.ifPresent(s -> skillXps.put(s, Math.max(0, Double.parseDouble(r.get(2)))));
      }
    }
  }

  private void loadQuests() throws IOException {
    URL url = new URL(URL_RUNE_METRICS_QUESTS + URLEncoder.encode(name, "UTF-8"));
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode rmQuestsJson = objectMapper.readTree(url).get("quests");
    Set<RuneMetricsQuest> rmQuests = objectMapper.readValue(objectMapper.treeAsTokens(rmQuestsJson),
        objectMapper.getTypeFactory().constructType(new TypeReference<Set<RuneMetricsQuest>>() {
        }));

    for (RuneMetricsQuest rmq : rmQuests) {
      String title = rmq.getTitle();
      Optional<QuestEntry> entry = quests.stream().filter(
          e -> e.getQuest().getTitle().equalsIgnoreCase(title) || e.getQuest().getDisplayName()
              .equalsIgnoreCase(title)).findAny();

      if (entry.isPresent()) {
        QuestStatus status;
        switch (rmq.getStatus()) {
          case COMPLETED:
            status = QuestStatus.COMPLETED;
            break;
          case STARTED:
            status = QuestStatus.IN_PROGRESS;
            break;
          case NOT_STARTED:
          default:
            status = QuestStatus.NOT_STARTED;
            break;
        }

        entry.get().setStatus(status);
      } else {
        LOG.warn("Failed to find RuneMetricsQuest: {}", title);
      }
    }
  }
}
