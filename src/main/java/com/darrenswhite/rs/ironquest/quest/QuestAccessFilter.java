package com.darrenswhite.rs.ironquest.quest;

/**
 * @author Darren S. White
 */
public enum QuestAccessFilter {

  ALL,
  FREE,
  MEMBERS;

  public boolean isFree() {
    return this == ALL || this == FREE;
  }

  public boolean isMembers() {
    return this == ALL || this == MEMBERS;
  }
}
