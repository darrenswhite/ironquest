package com.darrenswhite.rs.ironquest.player;

import com.darrenswhite.rs.ironquest.action.Action;
import com.darrenswhite.rs.ironquest.action.LampAction;
import com.darrenswhite.rs.ironquest.action.QuestAction;
import com.darrenswhite.rs.ironquest.action.TrainAction;
import com.darrenswhite.rs.ironquest.dto.PlayerDTO;
import com.darrenswhite.rs.ironquest.quest.Quest;
import com.darrenswhite.rs.ironquest.quest.RuneMetricsQuest;
import com.darrenswhite.rs.ironquest.quest.requirement.QuestRequirement;
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
import java.util.function.Function;
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
  private final Map<Integer, QuestEntry> quests;
  private final Set<Skill> lampSkills;
  private final boolean ironman;
  private final boolean recommended;

  Player(Builder builder) {
    this.name = builder.name;
    this.skillXps = builder.skillXps;
    this.quests = createQuestEntries(builder.quests);
    this.lampSkills = builder.lampSkills;
    this.ironman = builder.ironman;
    this.recommended = builder.recommended;
  }

  /**
   * Returns the player username.
   *
   * @return the username
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the player xp for each {@link Skill}.
   *
   * @return map of skill xp
   */
  public Map<Skill, Double> getSkillXps() {
    return skillXps;
  }

  /**
   * Returns a {@link Set} of {@link Quest}s.
   *
   * @return set of quests
   */
  public Set<Quest> getQuests() {
    return quests.values().stream().map(QuestEntry::getQuest).collect(Collectors.toSet());
  }

  /**
   * Returns the preferred {@link Skill}s to use on lamps.
   *
   * @return set of skills
   */
  public Set<Skill> getLampSkills() {
    return lampSkills;
  }

  /**
   * Returns if the player should use ironman requirements.
   *
   * @return <tt>true</tt> if ironman requirements are to be used; <tt>false</tt> otherwise.
   */
  public boolean isIronman() {
    return ironman;
  }

  /**
   * Returns if the player should use recommended requirements.
   *
   * @return <tt>true</tt> if recommended requirements are to be used; <tt>false</tt> otherwise.
   */
  public boolean isRecommended() {
    return recommended;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Player)) {
      return false;
    }
    Player player = (Player) o;
    return ironman == player.ironman && recommended == player.recommended && Objects
        .equals(name, player.name) && Objects.equals(skillXps, player.skillXps) && Objects
        .equals(quests, player.quests) && Objects.equals(lampSkills, player.lampSkills);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final int hashCode() {
    return Objects.hash(name, skillXps, quests, lampSkills, ironman, recommended);
  }

  /**
   * Returns a DTO for this {@link Player}.
   *
   * @return the DTO
   */
  public PlayerDTO createDTO() {
    return new PlayerDTO.Builder().setName(name).withLevels(getLevels())
        .withQuestPoints(getQuestPoints()).withTotalLevel(getTotalLevel())
        .withCombatLevel((int) Math.floor(getCombatLevel())).build();
  }

  /**
   * Returns a copy of this {@link Player}.
   *
   * @return the new player instance
   */
  public Player copy() {
    Set<Quest> copiedQuests = quests.values().stream().map(QuestEntry::copy)
        .map(QuestEntry::getQuest).collect(Collectors.toSet());

    Player copy = new Builder().withName(name).withSkillXps(new EnumMap<>(skillXps))
        .withQuests(copiedQuests).withLampSkills(new LinkedHashSet<>(lampSkills))
        .withIronman(ironman).withRecommended(recommended).build();

    for (Map.Entry<Integer, QuestEntry> entry : quests.entrySet()) {
      copy.quests.get(entry.getKey()).setPriority(entry.getValue().getPriority());
      copy.quests.get(entry.getKey()).setStatus(entry.getValue().getStatus());
    }

    return copy;
  }

  /**
   * Returns the levels for each {@link Skill}.
   *
   * @return map of skill levels
   */
  public Map<Skill, Integer> getLevels() {
    return skillXps.entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getKey().getLevelAt(e.getValue())));
  }

  /**
   * Returns the total number of quest points for all completed {@link Quest}s.
   *
   * @return number of quest points
   */
  public int getQuestPoints() {
    return getCompletedQuests().stream().mapToInt(q -> q.getRewards().getQuestPoints()).sum();
  }

  /**
   * Get all quests which are completed.
   *
   * @return set of complete quests
   */
  public Set<Quest> getCompletedQuests() {
    return quests.values().stream().filter(e -> e.getStatus() == QuestStatus.COMPLETED)
        .map(QuestEntry::getQuest).collect(Collectors.toSet());
  }

  /**
   * Get all quests which are not completed.
   *
   * @return set of incomplete quests
   */
  public Set<Quest> getIncompleteQuests() {
    return quests.values().stream().filter(e -> e.getStatus() != QuestStatus.COMPLETED)
        .map(QuestEntry::getQuest).collect(Collectors.toSet());
  }

  /**
   * Returns the total level for all {@link Skill}s.
   *
   * @return total level
   */
  public int getTotalLevel() {
    return getLevels().values().stream().mapToInt(Integer::intValue).sum();
  }

  /**
   * Calculates and returns the combat level.
   *
   * @return combat level
   * @see <a href="https://runescape.fandom.com/wiki/Combat_level">Combat level formula</a>
   */
  public double getCombatLevel() {
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

  /**
   * Returns the level for the specified {@link Skill}.
   *
   * @param s the skill
   * @return the skill level
   */
  public int getLevel(Skill s) {
    return s.getLevelAt(getXp(s));
  }

  /**
   * Returns the xp for the specified {@link Skill}.
   *
   * @param s the skill
   * @return the skill xp
   */
  public double getXp(Skill s) {
    return skillXps.get(s);
  }

  /**
   * Add xp to the specified {@link Skill}.
   *
   * @param s the skill
   * @param xp the amount of xp
   */
  public void addSkillXP(Skill s, double xp) {
    double newXp = skillXps.getOrDefault(s, 0d) + xp;

    if (newXp >= 0) {
      skillXps.put(s, newXp);
    }
  }

  /**
   * Test if a {@link Quest} has been completed.
   *
   * @param quest the quest
   * @return <tt>true</tt> if the quest is marked as completed; <tt>false</tt> otherwise.
   */
  public boolean isQuestCompleted(Quest quest) {
    return quests.get(quest.getId()).getStatus() == QuestStatus.COMPLETED;
  }

  /**
   * Complete the specified quest and create a list of {@link Action}s to be processed.
   *
   * @param quest the quest entry to mark as completed
   * @return actions to be processed upon quest completion
   * @throws QuestAlreadyCompletedException when the quest is already completed
   * @throws MissingQuestRequirementsException when there are unmet requirements for the quest
   */
  public List<Action> completeQuest(Quest quest) {
    List<Action> actions = new LinkedList<>();

    if (isQuestCompleted(quest)) {
      throw new QuestAlreadyCompletedException(
          "Quest already completed: " + quest.getDisplayName() + " (" + quest.getId() + ")");
    } else if (!quest.meetsCombatRequirement(this) || !quest.meetsQuestPointRequirement(this)
        || !quest.meetsQuestRequirements(this)) {
      throw new MissingQuestRequirementsException(
          "Missing requirements for quest: " + quest.getId());
    } else if (!quest.meetsSkillRequirements(this)) {
      for (SkillRequirement sr : getRemainingSkillRequirements(quest, false)) {
        actions.add(createTrainAction(sr));
      }
    }

    actions.add(new QuestAction(this, quest));

    for (LampReward lampReward : quest.getRewards().getLamps()) {
      LampAction lampAction = createLampAction(quest, lampReward);

      actions.add(lampAction);
    }

    return actions;
  }

  /**
   * Load skill data from hiscores and quest data from runemetrics.
   *
   * A non-empty username is required.
   *
   * @param hiscoreService the service used to retrieve hiscore data from
   * @param runeMetricsService the service used to retrieve runemetrics data from
   */
  public void load(HiscoreService hiscoreService, RuneMetricsService runeMetricsService) {
    if (name != null && !name.trim().isEmpty()) {
      loadHiscores(hiscoreService);
      loadQuests(runeMetricsService);
    }
  }

  /**
   * Creates a {@link LampAction} to be processed for the specified {@link Quest} and {@link
   * LampReward}.
   *
   * If this {@link Player} does meet the lamp requirements, then a {@link Set} of optimal {@link
   * Skill}s to used for the new action. This set of optimal skills is added to {@link
   * QuestEntry#getPreviousLampSkills()}.
   *
   * If this {@link Player} does not meet the lamp requirements, then the lamp can be processed in
   * the future when requirements have been met.
   *
   * @return the lamp action
   * @see Player#getOptimalLampSkills(LampReward, Set)
   * @see QuestEntry#getPreviousLampSkills()
   */
  public LampAction createLampAction(Quest quest, LampReward lampReward) {
    Set<Skill> optimalSkills = new HashSet<>();
    boolean future = true;

    if (lampReward.meetsRequirements(this)) {
      Set<Set<Skill>> previous = getQuestEntry(quest).getPreviousLampSkills();

      optimalSkills = getOptimalLampSkills(lampReward, previous);
      future = false;

      previous.add(optimalSkills);
    }

    return new LampAction(this, future, quest, lampReward, optimalSkills);
  }

  /**
   * Set the {@link QuestPriority} for the given {@link Quest}.
   *
   * @param quest the quest
   * @param priority the priority
   * @see Player#setQuestPriority(int, QuestPriority)
   */
  public void setQuestPriority(Quest quest, QuestPriority priority) {
    setQuestPriority(quest.getId(), priority);
  }

  /**
   * Set the {@link QuestPriority} for the given {@link Quest} id.
   *
   * @param questId the id of quest
   * @param priority the priority
   * @see QuestEntry#setPriority(QuestPriority)
   */
  public void setQuestPriority(int questId, QuestPriority priority) {
    quests.get(questId).setPriority(priority);
  }

  /**
   * Get the {@link QuestPriority} for the given {@link Quest}.
   *
   * @param quest the quest
   * @return the priority
   * @see Player#getQuestPriority(int)
   */
  public QuestPriority getQuestPriority(Quest quest) {
    return getQuestPriority(quest.getId());
  }

  /**
   * Get the {@link QuestPriority} for the given {@link Quest}.
   *
   * @param questId the id of quest
   * @return the priority
   * @see QuestEntry#getPriority()
   */
  public QuestPriority getQuestPriority(int questId) {
    return quests.get(questId).getPriority();
  }

  /**
   * Set the {@link QuestStatus} for the given {@link Quest}.
   *
   * @param quest the quest
   * @param status the status
   * @see Player#setQuestStatus(int, QuestStatus)
   */
  public void setQuestStatus(Quest quest, QuestStatus status) {
    setQuestStatus(quest.getId(), status);
  }

  /**
   * Set the {@link QuestStatus} for the given {@link Quest} id.
   *
   * @param questId the id of quest
   * @param status the status
   * @see QuestEntry#setStatus(QuestStatus)
   */
  public void setQuestStatus(int questId, QuestStatus status) {
    quests.get(questId).setStatus(status);
  }

  /**
   * Get the {@link QuestStatus} for the given {@link Quest}.
   *
   * @param quest the quest
   * @return the status
   * @see Player#getQuestStatus(int)
   */
  public QuestStatus getQuestStatus(Quest quest) {
    return getQuestStatus(quest.getId());
  }

  /**
   * Get the {@link QuestStatus} for the given {@link Quest}.
   *
   * @param questId the id of quest
   * @return the status
   * @see QuestEntry#getStatus()
   */
  public QuestStatus getQuestStatus(int questId) {
    return quests.get(questId).getStatus();
  }

  /**
   * Get the optimal skill choices to be used on a {@link LampReward} from a {@link Quest}.
   *
   * If any of the lamp reward choices contains any of the player <tt>lampSkills</tt>, then that
   * will be the set of skills returned.
   *
   * The next choice for the optimal set of skills to use is determined by the highest skill
   * requirement needed to complete all remaining quests.
   *
   * If there are no remaining skill requirements then there is no preference to which set of skills
   * will be returned.
   *
   * @param lampReward the lamp reward from the quest
   * @param previous set of previous skill choices used for the quest
   * @return the optimal choice of skills to use for the lamp reward
   * @throws LampSkillsNotFoundException if there are no skill choices found for the lamp reward
   * @see LampReward#getChoices(Player, Set)
   */
  public Set<Skill> getOptimalLampSkills(LampReward lampReward, Set<Set<Skill>> previous) {
    Set<Skill> optimalLampSkills = null;
    Set<Set<Skill>> lampSkillChoices = lampReward.getChoices(this, previous);

    for (Skill lampSkill : lampSkills) {
      optimalLampSkills = lampSkillChoices.stream().filter(skills -> skills.contains(lampSkill))
          .findFirst().orElse(null);

      if (optimalLampSkills != null) {
        break;
      }
    }

    if (optimalLampSkills == null) {
      Map<Skill, Double> xpRequirements = getRemainingXpRequirements();

      Map<Set<Skill>, Double> xpChoicesRequirements = lampSkillChoices.stream().collect(Collectors
          .toMap(s -> s,
              s -> s.stream().mapToDouble(sk -> xpRequirements.getOrDefault(sk, 0d)).sum()));

      optimalLampSkills = xpChoicesRequirements.entrySet().stream()
          .max(Comparator.comparingDouble(Map.Entry::getValue)).map(Map.Entry::getKey).orElse(null);
    }

    if (optimalLampSkills == null) {
      throw new LampSkillsNotFoundException(
          "Unable to use lampReward: no suitable skills found: lampReward=" + lampReward
              + ",previous=" + previous + ",lampSkills=" + lampSkills);
    }

    return optimalLampSkills;
  }

  /**
   * Get total levels required to complete this {@link Quest}.
   *
   * @param recursive <tt>true</tt> to get requirements recursively; <tt>false</tt> otherwise
   * @return total skill level requirements remaining
   */
  public int getTotalRemainingSkillRequirements(Quest quest, boolean recursive) {
    return getRemainingSkillRequirements(quest, recursive).stream()
        .mapToInt(SkillRequirement::getLevel).sum();
  }

  /**
   * Get remaining {@link Quest}s required to complete this {@link Quest}.
   *
   * @param recursive <tt>true</tt> to get requirements recursively; <tt>false</tt> otherwise
   * @return remaining quest requirements
   */
  public Set<Quest> getRemainingQuestRequirements(Quest quest, boolean recursive) {
    return quest.getQuestRequirements(recursive).stream().filter(q -> !q.test(this))
        .map(QuestRequirement::getQuest).collect(Collectors.toSet());
  }

  /**
   * Get remaining {@link SkillRequirement}s to complete this {@link Quest}.
   *
   * @param recursive <tt>true</tt> to get requirements recursively; <tt>false</tt> otherwise
   * @return remaining skill requirements
   */
  public Set<SkillRequirement> getRemainingSkillRequirements(Quest quest, boolean recursive) {
    Set<SkillRequirement> remainingSkillRequirements = new LinkedHashSet<>();

    remainingSkillRequirements = SkillRequirement.merge(remainingSkillRequirements,
        quest.getRequirements().getSkills().stream().filter(sr -> !sr.test(this))
            .collect(Collectors.toCollection(LinkedHashSet::new)));

    if (recursive) {
      Set<Quest> remainingQuestRequirements = getRemainingQuestRequirements(quest, true);

      for (Quest questRequirement : remainingQuestRequirements) {
        remainingSkillRequirements = SkillRequirement.merge(remainingSkillRequirements,
            getRemainingSkillRequirements(questRequirement, true));
      }
    }

    return remainingSkillRequirements;
  }

  /**
   * Returns the total xp and lamp rewards from the specified {@link Quest}.
   *
   * @param quest the quest
   * @return the total rewards
   */
  public double getTotalQuestRewards(Quest quest) {
    return getQuestRewards(quest).values().stream().mapToDouble(Double::doubleValue).sum();
  }

  /**
   * Creates a {@link TrainAction} to be processed for the specified {@link SkillRequirement}.
   *
   * @param skillRequirement the skill requirement
   * @return the train action
   */
  private TrainAction createTrainAction(SkillRequirement skillRequirement) {
    Skill skill = skillRequirement.getSkill();
    double currentXp = getXp(skill);
    double requirementXp = skill.getXpAtLevel(skillRequirement.getLevel());

    return new TrainAction(this, skill, currentXp, requirementXp);
  }

  /**
   * Returns the skill xp and lamp rewards from the specified {@link Quest}.
   *
   * @param quest the quest
   * @return the total rewards
   */
  private Map<Skill, Double> getQuestRewards(Quest quest) {
    Map<Skill, Double> rewards = new EnumMap<>(Skill.class);
    Set<Set<Skill>> previousLampSkills = new HashSet<>();

    rewards.putAll(quest.getRewards().getXp());

    quest.getRewards().getLamps().stream().filter(l -> l.meetsRequirements(this))
        .forEach(lampReward -> {
          Set<Skill> skills = getOptimalLampSkills(lampReward, previousLampSkills);
          double xp = lampReward.getXpForSkills(this, skills);

          previousLampSkills.add(skills);
          skills.forEach(skill -> rewards.put(skill, xp));
        });

    return rewards;
  }

  /**
   * Returns the xp required for each {@link Skill} needed to complete all quests.
   *
   * If the skill has no requirements left then it will not be present in the returned map.
   *
   * @return xp required for each skill
   */
  private Map<Skill, Double> getRemainingXpRequirements() {
    Map<Skill, Double> remaining = new EnumMap<>(Skill.class);
    Set<SkillRequirement> maxRequirements = getRemainingSkillRequirements();

    maxRequirements.forEach(r -> {
      Skill s = r.getSkill();
      int lvl = r.getLevel();

      remaining.put(s, s.getXpAtLevel(lvl) - getXp(s));
    });

    return remaining;
  }

  /**
   * Returns the highest {@link SkillRequirement}s required to complete all remaining quests.
   *
   * @return the maximum skill requirements for all quests
   * @see SkillRequirement#merge(Collection, Collection)
   */
  private Set<SkillRequirement> getRemainingSkillRequirements() {
    Set<SkillRequirement> requirements = new HashSet<>();

    for (Quest entry : getIncompleteQuests()) {
      requirements = SkillRequirement
          .merge(requirements, getRemainingSkillRequirements(entry, false));
    }

    return requirements;
  }

  /**
   * Load skill xp data from the hiscores.
   *
   * @param hiscoreService the hiscore service to retrieve data from
   */
  private void loadHiscores(HiscoreService hiscoreService) {
    skillXps.putAll(hiscoreService.load(name));
  }

  /**
   * Load quest status data from the runemetrics.
   *
   * @param runeMetricsService the runemetrics service to retrieve data from
   */
  private void loadQuests(RuneMetricsService runeMetricsService) {
    Set<RuneMetricsQuest> rmQuests = runeMetricsService.load(name);

    for (RuneMetricsQuest rmq : rmQuests) {
      String title = rmq.getTitle();
      Optional<QuestEntry> entry = quests.values().stream().filter(
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

  /**
   * Create a {@link Map} of {@link QuestEntry}s for the given {@link Quest}s.
   *
   * The key is the {@link Quest} id and the value is the {@link QuestEntry}.
   *
   * @param quests the quests to create entries for
   * @return map of quest entries
   */
  private Map<Integer, QuestEntry> createQuestEntries(Set<Quest> quests) {
    return quests.stream().map(QuestEntry::new)
        .collect(Collectors.toMap(entry -> entry.getQuest().getId(), Function.identity()));
  }

  /**
   * Returns the {@link QuestEntry} for the specified {@link Quest}.
   *
   * @param quest the quest to find the entry for
   * @return the quest entry or <tt>null</tt> if not found
   */
  private QuestEntry getQuestEntry(Quest quest) {
    return quests.get(quest.getId());
  }

  public static class Builder {

    private String name;
    private Map<Skill, Double> skillXps = new EnumMap<>(Skill.INITIAL_XPS);
    private Set<Quest> quests = new HashSet<>();
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

    public Builder withQuests(Set<Quest> quests) {
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
