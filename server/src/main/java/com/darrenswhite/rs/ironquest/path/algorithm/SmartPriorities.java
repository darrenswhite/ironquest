package com.darrenswhite.rs.ironquest.path.algorithm;

import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.QuestPriority;
import com.darrenswhite.rs.ironquest.player.Skill;
import com.darrenswhite.rs.ironquest.quest.Quest;
import com.darrenswhite.rs.ironquest.quest.requirement.QuestRequirement;
import com.darrenswhite.rs.ironquest.quest.requirement.SkillRequirement;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * The forced priorities algorithm implementation for {@link PathFinderAlgorithm}.
 *
 * @author Darren S. White
 */
@Component
public class SmartPriorities extends PathFinderAlgorithm {

  /**
   * {@inheritDoc}
   */
  @Override
  public AlgorithmId getId() {
    return AlgorithmId.SMART_PRIORITIES;
  }

  /**
   * Return a comparator for comparing {@link Quest}s.
   *
   * The order in which quests are compared is: priority, skill requirements, score, rewards.
   *
   * @see Comparators#noSkillRequirementsAndGreaterThanNormalPriority(Player)
   * @see Comparators#scoring(Player, BiFunction)
   * @see SmartPriorities#getQuestScore(Player, Quest)
   */
  @Override
  protected Comparator<Quest> comparator(Player player) {
    return Comparators.noSkillRequirementsAndGreaterThanNormalPriority(player)
        .thenComparing(Comparators.remainingSkillRequirements(player))
        .thenComparing(Comparators.scoring(player, this::getQuestScore))
        .thenComparing(Comparators.rewards(player));
  }

  /**
   * Calculates the score for the given {@link Quest}.
   *
   * The score for a quest is calculated as "xp gained towards goal * priority weight".
   *
   * The score is summed for each goal.
   *
   * A goal is defined as a prioritised quest and its quest requirements (recursively).
   *
   * If there are no prioritised quests then the score will be 0.
   *
   * @param player the player
   * @param quest the quest to calculate the score for
   * @return the quest score
   * @see Player#getPrioritisedQuests()
   * @see SmartPriorities#getXpGainedTowardsGoal(Player, Quest, Quest)
   * @see QuestPriority#getWeight()
   */
  private double getQuestScore(Player player, Quest quest) {
    if (player.getPrioritisedQuests().isEmpty()) {
      return 0;
    }

    Set<Quest> prioritisedQuests = player.getPrioritisedQuests();
    Set<Quest> priorityRequirements = prioritisedQuests.stream().map(
        q -> q.getQuestRequirements(true).stream().map(QuestRequirement::getQuest)
            .collect(Collectors.toSet())).flatMap(Collection::parallelStream)
        .collect(Collectors.toSet());
    Set<Quest> goal = Set.of(prioritisedQuests, priorityRequirements).stream()
        .flatMap(Collection::parallelStream).collect(Collectors.toSet());

    return goal.stream().mapToDouble(
        priorityQuest -> getXpGainedTowardsGoal(player, quest, priorityQuest) * player
            .getQuestPriority(priorityQuest).getWeight()).sum();
  }

  /**
   * Calculates the total xp gained from the given {@link Quest} which is also a {@link
   * SkillRequirement} for the specified goal.
   *
   * @param player the player
   * @param quest the quest to calculate the amount of xp gained for
   * @param goal the goal to reach
   * @return the amount of xp gained towards the goal
   */
  private double getXpGainedTowardsGoal(Player player, Quest quest, Quest goal) {
    Set<Skill> skillRequirements = player.getRemainingSkillRequirements(goal, true).stream()
        .map(SkillRequirement::getSkill).collect(Collectors.toSet());
    Map<Skill, Double> questRewards = player.getQuestRewards(quest);

    return skillRequirements.stream().mapToDouble(skill -> questRewards.getOrDefault(skill, 0d))
        .sum();
  }
}
