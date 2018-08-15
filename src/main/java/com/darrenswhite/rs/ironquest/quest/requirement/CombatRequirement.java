package com.darrenswhite.rs.ironquest.quest.requirement;

import com.darrenswhite.rs.ironquest.player.Player;

/**
 * @author Darren White
 */
public class CombatRequirement extends Requirement {

  private final int level;

  public CombatRequirement(int level) {
    this(false, false, level);
  }

  public CombatRequirement(boolean ironman, boolean recommended, int level) {
    super(ironman, recommended);
    this.level = level;
  }

  @Override
  public boolean isOther() {
    return true;
  }

  @Override
  protected boolean test(Player p) {
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
