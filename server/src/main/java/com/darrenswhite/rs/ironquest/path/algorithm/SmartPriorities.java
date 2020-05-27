package com.darrenswhite.rs.ironquest.path.algorithm;

import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.QuestPriority;
import com.darrenswhite.rs.ironquest.player.Skill;
import com.darrenswhite.rs.ironquest.quest.Quest;
import com.darrenswhite.rs.ironquest.quest.requirement.SkillRequirement;
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
   * The order in which quests are compared is: priority, skill requirements, distance to
   * prioritised quests, rewards.
   *
   * @see Comparators#priority(Player)
   * @see Comparators#remainingSkillRequirements(Player)
   * @see Comparators#scoring(Player, BiFunction)
   * @see Comparators#rewards(Player)
   */
  @Override
  protected Comparator<Quest> comparator(Player player) {
    return Comparators.priority(player)
        .thenComparing(Comparators.remainingSkillRequirements(player))
        .thenComparing(Comparators.scoring(player, this::distanceToPrioritisedQuests).reversed())
        .thenComparing(Comparators.rewards(player));
  }

  /**
   * Calculates the minimum distance to a prioritised {@link Quest}.
   *
   * The distance for a quest is calculated as "xp gained towards goal * priority weight".
   *
   * A goal is defined as a prioritised quest.
   *
   * The minimum distance is then chosen.
   *
   * @param player the player
   * @param quest the quest to calculate the distance of
   * @return the minimum distance to a prioritised quest
   * @see Player#getPrioritisedQuests()
   * @see SmartPriorities#getXpGainedTowardsGoal(Player, Quest, Quest)
   * @see QuestPriority#getWeight()
   */
  private double distanceToPrioritisedQuests(Player player, Quest quest) {
    Set<Quest> prioritisedQuests = player.getPrioritisedQuests();

    return prioritisedQuests.stream().mapToDouble(
        priorityQuest -> getXpGainedTowardsGoal(player, quest, priorityQuest) * player
            .getQuestPriority(priorityQuest).getWeight()).min().orElse(0);
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
