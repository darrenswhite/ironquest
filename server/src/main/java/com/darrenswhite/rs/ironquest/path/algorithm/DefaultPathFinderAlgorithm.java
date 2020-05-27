package com.darrenswhite.rs.ironquest.path.algorithm;

import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.quest.Quest;
import java.util.Comparator;
import org.springframework.stereotype.Component;

/**
 * The default algorithm implementation for {@link PathFinderAlgorithm}.
 *
 * @author Darren S. White
 */
@Component
public class DefaultPathFinderAlgorithm extends PathFinderAlgorithm {

  /**
   * {@inheritDoc}
   */
  @Override
  public AlgorithmId getId() {
    return AlgorithmId.DEFAULT;
  }

  /**
   * Return a comparator for comparing {@link Quest}s.
   *
   * The order in which quests are compared is: priority, skill requirements, rewards.
   *
   * @see Comparators#priority(Player)
   * @see Comparators#remainingSkillRequirements(Player)
   * @see Comparators#rewards(Player)
   */
  @Override
  protected Comparator<Quest> comparator(Player player) {
    return Comparators.priority(player)
        .thenComparing(Comparators.remainingSkillRequirements(player))
        .thenComparing(Comparators.rewards(player));
  }
}
