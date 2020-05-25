package com.darrenswhite.rs.ironquest.path;

import com.darrenswhite.rs.ironquest.quest.Quest;

/**
 * Exception thrown when {@link PathFinderAlgorithm} fails to find the next optimal {@link Quest} to
 * be completed.
 *
 * @author Darren S. White
 */
public class QuestNotFoundException extends Exception {

  public QuestNotFoundException(String message) {
    super(message);
  }
}
