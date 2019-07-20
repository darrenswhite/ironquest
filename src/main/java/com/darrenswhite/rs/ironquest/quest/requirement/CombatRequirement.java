package com.darrenswhite.rs.ironquest.quest.requirement;

import com.darrenswhite.rs.ironquest.player.Player;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Darren S. White
 */
public class CombatRequirement extends Requirement {

  private final int level;

  public CombatRequirement(@JsonProperty("level") int level) {
    this.level = level;
  }

  public int getLevel() {
    return level;
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

  @Override
  protected boolean testPlayer(Player p) {
    return p.getCombatLevel() >= level;
  }
}
