package com.darrenswhite.rs.ironquest.quest.requirement;

import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.quest.Quest;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

/**
 * @author Darren S. White
 */
@JsonIdentityInfo(scope = Quest.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "quest")
public class QuestRequirement extends Requirement {

  private Quest quest;

  public Quest getQuest() {
    return quest;
  }

  public void setQuest(Quest quest) {
    this.quest = quest;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(quest.getDisplayName());
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
    return p.isQuestCompleted(quest);
  }
}
