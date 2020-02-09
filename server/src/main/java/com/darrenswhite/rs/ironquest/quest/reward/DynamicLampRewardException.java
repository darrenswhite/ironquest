package com.darrenswhite.rs.ironquest.quest.reward;

import com.darrenswhite.rs.ironquest.player.Skill;

/**
 * Exception thrown when trying to use a dynamic {@link LampReward} on more than one {@link Skill}.
 *
 * @author Darren S. White
 */
public class DynamicLampRewardException extends RuntimeException {

  public DynamicLampRewardException(String message) {
    super(message);
  }
}
