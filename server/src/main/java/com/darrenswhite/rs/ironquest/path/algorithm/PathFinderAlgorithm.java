package com.darrenswhite.rs.ironquest.path.algorithm;

import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.quest.Quest;
import java.util.Comparator;

/**
 * Abstract class used to compare {@link Quest}s.
 *
 * @author Darren S. White
 */
public abstract class PathFinderAlgorithm {

  /**
   * Returns the {@link AlgorithmId} for this algorithm.
   *
   * @return the unique id
   */
  public abstract AlgorithmId getId();

  /**
   * Returns a {@link Comparator<Quest>} that returns the most optimal {@link Quest} to complete for
   * the given {@link Player}.
   *
   * @param player the player
   * @return a comparator that will compare quests and return the most optimal quest
   * @see PathFinderAlgorithm#comparator(Player)
   */
  public Comparator<Quest> getQuestComparator(Player player) {
    return this.comparator(player).thenComparing(Quest::getId);
  }

  /**
   * Returns a {@link Comparator<Quest>} that returns the most optimal {@link Quest} to complete for
   * the given {@link Player}.
   *
   * @param player the player
   * @return a comparator that will compare quests and return the most optimal quest
   */
  protected abstract Comparator<Quest> comparator(Player player);
}
