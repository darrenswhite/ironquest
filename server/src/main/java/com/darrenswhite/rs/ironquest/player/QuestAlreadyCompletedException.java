package com.darrenswhite.rs.ironquest.player;

import com.darrenswhite.rs.ironquest.quest.Quest;

/**
 * Exception thrown when trying to complete an already completed {@link Quest}.
 *
 * @author Darren S. White
 */
public class QuestAlreadyCompletedException extends RuntimeException {

  public QuestAlreadyCompletedException(String message) {
    super(message);
  }
}
