package com.darrenswhite.rs.ironquest.quest;

/**
 * Enum representing quest type filters.
 *
 * @author Darren S. White
 */
public enum QuestTypeFilter {

  ALL,
  QUESTS,
  SAGAS,
  MINIQUESTS;

  public boolean isQuests() {
    return this == ALL || this == QUESTS;
  }

  public boolean isSagas() {
    return this == ALL || this == SAGAS;
  }

  public boolean isMiniquests() {
    return this == ALL || this == MINIQUESTS;
  }
}
