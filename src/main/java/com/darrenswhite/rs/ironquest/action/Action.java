package com.darrenswhite.rs.ironquest.action;

import com.darrenswhite.rs.ironquest.player.Player;

/**
 * A class representing an action to be completed.
 *
 * @author Darren S. White
 */
public abstract class Action {

  private final Player player;
  private final boolean future;
  private boolean processed = false;

  protected Action(Player player, boolean future) {
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

  public final boolean isFuture() {
    return future;
  }

  public final Player getPlayer() {
    return player;
  }

  @Override
  public final String toString() {
    return getMessage();
  }

  protected abstract void processPlayer(Player player);
}
