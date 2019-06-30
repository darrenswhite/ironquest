package com.darrenswhite.rs.ironquest.quest;

/**
 * @author Darren S. White
 */
public enum QuestMemberFilter {

  BOTH,
  FREE,
  MEMBERS;

  public boolean isFree() {
    return this == BOTH || this == FREE;
  }

  public boolean isMembers() {
    return this == BOTH || this == MEMBERS;
  }
}
