package com.darrenswhite.rs.ironquest.path.algorithm;

import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.quest.Quest;
import java.util.Comparator;

/**
 * The default algorithm implementation for {@link PathFinderAlgorithm}.
 *
 * @author Darren S. White
 */
public class DefaultPathFinderAlgorithm extends PathFinderAlgorithm {

  /**
   * Return a comparator for comparing {@link Quest}s.
   *
   * The order in which quests are compared is: priority, calculated priority, skill requirements.
   *
   * @see Player#questPriorityComparator()
   * @see DefaultPathFinderAlgorithm#calculatedQuestPriorityComparator(Player)
   * @see Player#questSkillRequirementsComparator()
   */
  @Override
  public Comparator<Quest> comparator(Player player) {
    return player.questPriorityComparator().thenComparing(calculatedQuestPriorityComparator(player))
        .thenComparing(player.questSkillRequirementsComparator());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public AlgorithmId getId() {
    return AlgorithmId.DEFAULT;
  }

  /**
   * Returns a {@link Comparator<Quest>} that compares by a calculated priority.
   *
   * Priority is derived from low skill requirements and high rewards.
   *
   * @return a comparator that compares by calculated priority
   */
  private Comparator<Quest> calculatedQuestPriorityComparator(Player player) {
    Comparator<Quest> comparing = Comparator.comparing(quest -> {
      int requirements = player.getTotalRemainingSkillRequirements(quest, true);
      double rewards = player.getTotalQuestRewards(quest) / 100;

      return rewards - requirements;
    });
    return comparing.reversed();
  }
}
