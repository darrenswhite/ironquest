package com.darrenswhite.rs.ironquest.action;

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
  private boolean processed = false;

  Action(ActionType type, Player player, boolean future) {
    this.type = type;
    this.player = player.copy();
    this.future = future;
  }

  public abstract String getMessage();

  public abstract boolean meetsRequirements(Player player);

  public final void process(Player player) {
    if (!processed && meetsRequirements(player)) {
      processPlayer(player);
      processed = true;
    }
  }

  public final Player getPlayer() {
    return player;
  }

  public final ActionType getType() {
    return type;
  }

  public final boolean isFuture() {
    return future;
  }

  public final boolean isProcessed() {
    return processed;
  }

  @Override
  public final String toString() {
    return getMessage();
  }

  protected abstract void processPlayer(Player player);
}
