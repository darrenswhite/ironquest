package com.darrenswhite.rs.ironquest.player;

import com.darrenswhite.rs.ironquest.quest.Quest;
import com.darrenswhite.rs.ironquest.quest.requirement.QuestRequirements;

/**
 * Exception thrown when trying to complete a {@link Quest} with unmet {@link QuestRequirements}.
 *
 * @author Darren S. White
 */
public class MissingQuestRequirementsException extends RuntimeException {

  public MissingQuestRequirementsException(String message) {
    super(message);
  }
}
