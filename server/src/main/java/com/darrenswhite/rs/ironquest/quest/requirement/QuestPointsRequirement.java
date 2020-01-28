package com.darrenswhite.rs.ironquest.quest.requirement;

import com.darrenswhite.rs.ironquest.player.Player;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Darren S. White
 */
public class QuestPointsRequirement extends Requirement {

  private final int amount;

  public QuestPointsRequirement(@JsonProperty("amount") int amount) {
    this.amount = amount;
  }

  public int getAmount() {
    return amount;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(amount);
    sb.append(" Quest Points");
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
    return p.getQuestPoints() >= amount;
  }
}
