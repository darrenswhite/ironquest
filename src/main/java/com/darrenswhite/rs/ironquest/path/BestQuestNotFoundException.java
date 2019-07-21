package com.darrenswhite.rs.ironquest.path;

import com.darrenswhite.rs.ironquest.quest.Quest;

/**
 * Exception thrown when {@link PathFinder} fails to find the "best" {@link Quest}.
 *
 * @author Darren S. White
 */
public class BestQuestNotFoundException extends Exception {

  public BestQuestNotFoundException(String message) {
    super(message);
  }
}
