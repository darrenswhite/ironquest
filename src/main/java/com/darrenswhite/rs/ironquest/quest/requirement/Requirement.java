package com.darrenswhite.rs.ironquest.quest.requirement;

import com.darrenswhite.rs.ironquest.player.Player;

/**
 * @author Darren White
 */
public abstract class Requirement {

  private boolean ironman;
  private boolean recommended;

  protected Requirement(boolean ironman, boolean recommended) {
    this.ironman = ironman;
    this.recommended = recommended;
  }

  public boolean isIronman() {
    return ironman;
  }

  public void setIronman(boolean ironman) {
    this.ironman = ironman;
  }

  public abstract boolean isOther();

  public boolean isRecommended() {
    return recommended;
  }

  public void setRecommended(boolean recommended) {
    this.recommended = recommended;
  }

  public boolean test(Player p, boolean ironman, boolean recommended) {
    boolean testIronman = isIronman() && !ironman;
    boolean testRecommended = isRecommended() && !recommended;
    return testIronman || testRecommended || test(p);
  }

  protected abstract boolean test(Player p);
}
