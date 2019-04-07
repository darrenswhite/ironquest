package com.darrenswhite.rs.ironquest.quest.requirement;

import com.darrenswhite.rs.ironquest.player.Player;

/**
 * @author Darren S. White
 */
public abstract class Requirement {

  private boolean ironman;
  private boolean recommended;

  public boolean isIronman() {
    return ironman;
  }

  public void setIronman(boolean ironman) {
    this.ironman = ironman;
  }

  public boolean isRecommended() {
    return recommended;
  }

  public void setRecommended(boolean recommended) {
    this.recommended = recommended;
  }

  public boolean test(Player p) {
    boolean testIronman = isIronman() && !p.isIronman();
    boolean testRecommended = isRecommended() && !p.isRecommended();
    return testIronman || testRecommended || testPlayer(p);
  }

  protected abstract boolean testPlayer(Player p);
}
