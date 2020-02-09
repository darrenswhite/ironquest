package com.darrenswhite.rs.ironquest.quest.requirement;

import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.quest.Quest;

/**
 * A class representing a requirement for a {@link Quest}.
 *
 * @author Darren S. White
 */
public abstract class Requirement {

  protected final boolean ironman;
  protected final boolean recommended;

  protected Requirement(boolean ironman, boolean recommended) {
    this.ironman = ironman;
    this.recommended = recommended;
  }

  public final boolean isIronman() {
    return ironman;
  }

  public final boolean isRecommended() {
    return recommended;
  }

  /**
   * Test if the specified {@link Player} meets this requirement.
   *
   * @param player the player
   * @return <tt>true</tt> if the player meets this requirement; <tt>false</tt> otherwise
   */
  public boolean test(Player player) {
    boolean testIronman = isIronman() && !player.isIronman();
    boolean testRecommended = isRecommended() && !player.isRecommended();

    return testIronman || testRecommended || testPlayer(player);
  }

  /**
   * Test if the specified {@link Player} meets this requirement.
   *
   * This is not constrained to ironman and recommended requirements.
   *
   * @param player the player
   * @return <tt>true</tt> if the player meets this requirements; <tt>false</tt> otherwise
   */
  protected abstract boolean testPlayer(Player player);
}
