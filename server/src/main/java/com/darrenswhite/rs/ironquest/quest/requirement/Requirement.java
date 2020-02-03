package com.darrenswhite.rs.ironquest.quest.requirement;

import com.darrenswhite.rs.ironquest.player.Player;

/**
 * @author Darren S. White
 */
public abstract class Requirement {

  private final boolean ironman;
  private final boolean recommended;

  protected Requirement(boolean ironman, boolean recommended) {
    this.ironman = ironman;
    this.recommended = recommended;
  }

  public boolean isIronman() {
    return ironman;
  }

  public boolean isRecommended() {
    return recommended;
  }

  public boolean test(Player p) {
    boolean testIronman = isIronman() && !p.isIronman();
    boolean testRecommended = isRecommended() && !p.isRecommended();
    return testIronman || testRecommended || testPlayer(p);
  }

  protected abstract boolean testPlayer(Player p);
}
