package com.darrenswhite.rs.ironquest.quest.requirement;

import com.darrenswhite.rs.ironquest.player.Player;

/**
 * @author Darren S. White
 */
public class CombatRequirement extends Requirement {

  private int level;

  public void setLevel(int level) {
    this.level = level;
  }

  public int getLevel() {
    return level;
  }

  @Override
  protected boolean testPlayer(Player p) {
    return p.getCombatLevel() >= level;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(level);
    sb.append(" Combat");
    if (isIronman()) {
      sb.append(" (Ironman)");
    }
    if (isRecommended()) {
      sb.append(" (Recommended)");
    }
    return sb.toString();
  }
}
