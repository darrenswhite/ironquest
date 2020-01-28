package com.darrenswhite.rs.ironquest.action;

import com.darrenswhite.rs.ironquest.dto.ActionDTO;
import com.darrenswhite.rs.ironquest.player.Player;

/**
 * A class representing an action to be completed.
 *
 * @author Darren S. White
 */
public abstract class Action {

  private final ActionType type;
  private final Player player;
  private final boolean future;

  Action(ActionType type, Player player, boolean future) {
    this.type = type;
    this.player = player.copy();
    this.future = future;
  }

  public abstract Action copyForPlayer(Player player);

  public abstract String getMessage();

  public abstract boolean meetsRequirements(Player player);

  public abstract void process(Player player);

  public abstract ActionDTO createDTO();

  public final ActionType getType() {
    return type;
  }

  public final Player getPlayer() {
    return player;
  }

  public final boolean isFuture() {
    return future;
  }

  @Override
  public final String toString() {
    return getMessage();
  }
}
