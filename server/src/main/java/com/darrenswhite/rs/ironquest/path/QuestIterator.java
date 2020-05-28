package com.darrenswhite.rs.ironquest.path;

import com.darrenswhite.rs.ironquest.path.algorithm.PathFinderAlgorithm;
import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.Skill;
import com.darrenswhite.rs.ironquest.quest.Quest;
import com.darrenswhite.rs.ironquest.quest.requirement.CombatRequirement;
import com.darrenswhite.rs.ironquest.quest.requirement.QuestPointsRequirement;
import com.darrenswhite.rs.ironquest.quest.requirement.QuestRequirement;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Interface used to iterate a {@link Collection} of {@link Quest}s in optimal order.
 *
 * @author Darren S. White
 */
public class QuestIterator implements Iterator<Quest> {

  private final Player player;
  private final PathFinderAlgorithm algorithm;

  private Quest next;

  public QuestIterator(Player player, PathFinderAlgorithm algorithm) {
    this.player = player;
    this.algorithm = algorithm;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final boolean hasNext() {
    return (next = find()) != null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final Quest next() {
    if (next == null) {
      throw new NoSuchElementException();
    }

    return next;
  }

  /**
   * Find the next optimal {@link Quest} that can be completed or can be reached via training one or
   * more {@link Skill}s.
   *
   * A {@link Quest} can be completed when all {@link CombatRequirement}s, {@link
   * QuestPointsRequirement}s, and {@link QuestRequirement}s are satisfied.
   *
   * {@link Quest}s are compared using the given algorithm.
   */
  private Quest find() {
    return player.getIncompleteQuests().stream().filter(
        quest -> quest.meetsCombatRequirement(player) && quest.meetsQuestPointRequirement(player)
            && quest.meetsQuestRequirements(player)).max(algorithm.getQuestComparator(player))
        .orElse(null);
  }
}
