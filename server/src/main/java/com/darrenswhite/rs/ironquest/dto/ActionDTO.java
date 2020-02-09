package com.darrenswhite.rs.ironquest.dto;

import com.darrenswhite.rs.ironquest.action.Action;
import com.darrenswhite.rs.ironquest.action.ActionType;

/**
 * Data Transfer Object for {@link Action}.
 *
 * @author Darren S. White
 */
public abstract class ActionDTO {

  protected final ActionType type;
  protected final PlayerDTO player;
  protected final boolean future;
  protected final String message;

  protected ActionDTO(ActionType type, PlayerDTO player, boolean future, String message) {
    this.type = type;
    this.player = player;
    this.future = future;
    this.message = message;
  }

  public final ActionType getType() {
    return type;
  }

  public final PlayerDTO getPlayer() {
    return player;
  }

  public final boolean isFuture() {
    return future;
  }

  public final String getMessage() {
    return message;
  }
}
