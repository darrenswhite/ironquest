package com.darrenswhite.rs.ironquest.player;

import com.darrenswhite.rs.ironquest.action.Action;
import com.darrenswhite.rs.ironquest.action.LampAction;
import com.darrenswhite.rs.ironquest.action.QuestAction;
import com.darrenswhite.rs.ironquest.action.TrainAction;
import com.darrenswhite.rs.ironquest.dto.PlayerDTO;
import com.darrenswhite.rs.ironquest.quest.Quest;
import com.darrenswhite.rs.ironquest.quest.RuneMetricsQuest;
import com.darrenswhite.rs.ironquest.quest.requirement.Requirement;
import com.darrenswhite.rs.ironquest.quest.requirement.SkillRequirement;
import com.darrenswhite.rs.ironquest.quest.reward.LampReward;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A class representing a player.
 *
 * @author Darren S. White
 */
public class Player {

  private static final Logger LOG = LogManager.getLogger(Player.class);

  private final String name;
  private final Map<Skill, Double> skillXps;
  private final Set<QuestEntry> quests;
  private final Set<Skill> lampSkills;
  private final boolean ironman;
  private final boolean recommended;

  Player(Builder builder) {
    this.name = builder.name;
    this.skillXps = builder.skillXps;
    this.quests = builder.quests;
    this.lampSkills = builder.lampSkills;
    this.ironman = builder.ironman;
    this.recommended = builder.recommended;
  }

  public String getName() {
    return name;
  }

  public Map<Skill, Double> getSkillXps() {
    return skillXps;
  }

  public Set<QuestEntry> getQuests() {
    return quests;
  }

  public Set<Skill> getLampSkills() {
    return lampSkills;
  }

  public boolean isIronman() {
    return ironman;
  }

  public boolean isRecommended() {
    return recommended;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Player player = (Player) o;
    return ironman == player.ironman && recommended == player.recommended && Objects
        .equals(name, player.name) && Objects.equals(skillXps, player.skillXps) && Objects
        .equals(quests, player.quests) && Objects.equals(lampSkills, player.lampSkills);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, skillXps, quests, lampSkills, ironman, recommended);
  }

  public PlayerDTO createDTO() {
    return new PlayerDTO.Builder().setName(name).withLevels(getLevels())
        .withQuestPoints(getQuestPoints()).withTotalLevel(getTotalLevel())
        .withCombatLevel((int) Math.floor(getCombatLevel())).build();
  }

  public Player copy() {
    Set<QuestEntry> copiedQuests = quests.stream()
        .map(e -> new QuestEntry(e.getQuest(), e.getStatus(), e.getPriority()))
        .collect(Collectors.toSet());

    return new Player.Builder().withName(name).withSkillXps(new EnumMap<>(skillXps))
        .withQuests(copiedQuests).withLampSkills(new LinkedHashSet<>(lampSkills))
        .withIronman(ironman).withRecommended(recommended).build();
  }

  public Map<Skill, Integer> getLevels() {
    return skillXps.entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getKey().getLevelAt(e.getValue())));
  }

  public int getQuestPoints() {
    return getCompletedQuests().stream().mapToInt(q -> q.getQuest().getRewards().getQuestPoints())
        .sum();
  }


  public Set<QuestEntry> getCompletedQuests() {
    return quests.stream().filter(e -> e.getStatus() == QuestStatus.COMPLETED)
        .collect(Collectors.toSet());
  }

  /**
   * Get all quest entries which are not completed.
   *
   * @return set of incomplete quests
   */
  public Set<QuestEntry> getIncompleteQuests() {
    return quests.stream().filter(e -> e.getStatus() != QuestStatus.COMPLETED)
        .collect(Collectors.toSet());
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

  public List<Action> completeQuest(QuestEntry entry) {
    List<Action> actions = new LinkedList<>();
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

    for (LampReward lampReward : quest.getRewards().getLamps()) {
      LampAction lampAction = createLampAction(entry, lampReward);

      actions.add(lampAction);
    }

    return actions;
  }

  public void load(HiscoreService hiscoreService, RuneMetricsService runeMetricsService) {
    if (name != null && !name.trim().isEmpty()) {
      loadHiscores(hiscoreService);
      loadQuests(runeMetricsService);
    }
  }

  /**
   * Gets the 'best' {@link QuestEntry} from the given {@link Collection} of {@link QuestEntry}'s to
   * be completed next if any. If no 'best' {@link QuestEntry} if available then the 'nearest'
   * {@link QuestEntry} will be returned.
   * <p>
   * The 'best' {@link QuestEntry} is determined by this {@link Player} meeting all {@link
   * Requirement}'s for a {@link Quest} and then getting the maximum {@link QuestEntry} compared by
   * {@link QuestPriority}.
   * <p>
   * The 'nearest' {@link QuestEntry} is determined by this {@link Player} meeting all {@link
   * Requirement}'s (including all {@link LampReward} {@link Requirement}'s but excluding {@link
   * SkillRequirement}'s) and then getting the minimum {@link QuestEntry} compared by the total
   * remaining {@link SkillRequirement}'s.
   *
   * @param quests the collection of quests to search
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
    double rewards = getTotalQuestRewards(quest) / 100;
    return rewards - requirements;
  }

  private double getTotalQuestRewards(Quest quest) {
    double xpRewards = quest.getRewards().getXp().values().stream().mapToDouble(Double::doubleValue)
        .sum();
    Set<Set<Skill>> previousLampSkills = new HashSet<>();
    double lampXpRewards = quest.getRewards().getLamps().stream()
        .filter(l -> l.meetsRequirements(this)).mapToDouble(lampReward -> {
          Set<Skill> skills = getBestLampSkills(lampReward, previousLampSkills);
          previousLampSkills.add(skills);
          return lampReward.getXpForSkills(this, skills);
        }).sum();

    return xpRewards + lampXpRewards;
  }

  private Set<Skill> getBestLampSkills(LampReward lampReward, Set<Set<Skill>> previous) {
    Set<Skill> bestLampSkills = null;
    Set<Set<Skill>> lampSkillChoices = lampReward.getChoices(this, previous);

    for (Skill lampSkill : lampSkills) {
      bestLampSkills = lampSkillChoices.stream().filter(skills -> skills.contains(lampSkill))
          .findFirst().orElse(null);

      if (bestLampSkills != null) {
        break;
      }
    }

    if (bestLampSkills == null) {
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

      bestLampSkills = choice.get().getKey();
    }

    return bestLampSkills;
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

  private void loadHiscores(HiscoreService hiscoreService) {
    skillXps.putAll(hiscoreService.load(name));
  }

  private void loadQuests(RuneMetricsService runeMetricsService) {
    Set<RuneMetricsQuest> rmQuests = runeMetricsService.load(name);

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

  public static class Builder {

    private String name;
    private Map<Skill, Double> skillXps = new EnumMap<>(Skill.INITIAL_XPS);
    private Set<QuestEntry> quests = new HashSet<>();
    private Set<Skill> lampSkills = new LinkedHashSet<>();
    private boolean ironman = false;
    private boolean recommended = false;

    public Builder withName(String name) {
      this.name = name;
      return this;
    }

    public Builder withSkillXps(Map<Skill, Double> skillXps) {
      this.skillXps = skillXps;
      return this;
    }

    public Builder withQuests(Set<QuestEntry> quests) {
      this.quests = quests;
      return this;
    }

    public Builder withLampSkills(Set<Skill> lampSkills) {
      this.lampSkills = lampSkills;
      return this;
    }

    public Builder withIronman(boolean ironman) {
      this.ironman = ironman;
      return this;
    }

    public Builder withRecommended(boolean recommended) {
      this.recommended = recommended;
      return this;
    }

    public Player build() {
      return new Player(this);
    }
  }
}
