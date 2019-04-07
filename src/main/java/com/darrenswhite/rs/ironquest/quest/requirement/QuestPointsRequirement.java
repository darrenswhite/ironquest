package com.darrenswhite.rs.ironquest.quest.requirement;

import com.darrenswhite.rs.ironquest.player.Player;

/**
 * @author Darren S. White
 */
public class QuestPointsRequirement extends Requirement {

  private int amount;

  public int getAmount() {
    return amount;
  }

  public void setAmount(int amount) {
    this.amount = amount;
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
