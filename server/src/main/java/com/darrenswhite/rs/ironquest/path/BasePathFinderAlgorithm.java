package com.darrenswhite.rs.ironquest.path;

import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.Skill;
import com.darrenswhite.rs.ironquest.quest.Quest;
import com.darrenswhite.rs.ironquest.quest.requirement.CombatRequirement;
import com.darrenswhite.rs.ironquest.quest.requirement.QuestPointsRequirement;
import com.darrenswhite.rs.ironquest.quest.requirement.QuestRequirement;
import java.util.Comparator;
import java.util.NoSuchElementException;

/**
 * Base class for {@link PathFinderAlgorithm}.
 *
 * @author Darren S. White
 */
public abstract class BasePathFinderAlgorithm implements PathFinderAlgorithm {

  protected final Player player;

  private Quest next;

  public BasePathFinderAlgorithm(Player player) {
    this.player = player;
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
   * Returns a {@link Comparator<Quest>} that returns the most optimal {@link Quest} to complete.
   *
   * @return a comparator that will compare quests and return the most optimal quest
   */
  protected abstract Comparator<Quest> getQuestComparator();

  /**
   * Find the next optimal {@link Quest} that can be completed or can be reached via training one or
   * more {@link Skill}s.
   *
   * A {@link Quest} can be completed when all {@link CombatRequirement}s, {@link
   * QuestPointsRequirement}s, and {@link QuestRequirement}s are satisfied.
   *
   * {@link Quest}s are compared by {@link BasePathFinderAlgorithm#getQuestComparator()} and then
   * compared by {@link Quest#getId()} as a fallback to ensure consistent results.
   */
  private Quest find() {
    return player.getIncompleteQuests().stream().filter(
        quest -> quest.meetsCombatRequirement(player) && quest.meetsQuestPointRequirement(player)
            && quest.meetsQuestRequirements(player))
        .min(this.getQuestComparator().thenComparingInt(Quest::getId)).orElse(null);
  }
}
