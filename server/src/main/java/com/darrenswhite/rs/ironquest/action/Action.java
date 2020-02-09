package com.darrenswhite.rs.ironquest.action;

import com.darrenswhite.rs.ironquest.dto.ActionDTO;
import com.darrenswhite.rs.ironquest.player.Player;

/**
 * A class representing an action to be completed for a {@link Player}.
 *
 * @author Darren S. White
 */
public abstract class Action {

  protected final ActionType type;
  protected final Player player;
  protected final boolean future;

  Action(ActionType type, Player player, boolean future) {
    this.type = type;
    this.player = player.copy();
    this.future = future;
  }

  /**
   * Returns a copy of this action for the specified {@link Player}.
   *
   * @param player the player
   * @return the copied action
   */
  public abstract Action copyForPlayer(Player player);

  /**
   * Returns the human-readable message for this {@link Action}.
   *
   * @return human-readable action message
   */
  public abstract String getMessage();

  /**
   * Test if the given {@link Player} satisfies all requirements for this {@link Action}.
   *
   * @param player the player
   * @return <tt>true</tt> if the player has all requirements; <tt>false</tt> otherwise.
   */
  public abstract boolean meetsRequirements(Player player);

  /**
   * Apply this {@link Action} to the given {@link Player}.
   *
   * @param player the player
   */
  public abstract void process(Player player);

  /**
   * Returns a DTO for this {@link Action}.
   *
   * @return the DTO
   */
  public abstract ActionDTO createDTO();

  /**
   * Returns the {@link ActionType}.
   *
   * @return the type of action
   */
  public final ActionType getType() {
    return type;
  }

  /**
   * Returns the {@link Player}.
   *
   * @return the player
   */
  public final Player getPlayer() {
    return player;
  }

  /**
   * Returns if this {@link Action} is to be processed in the future.
   *
   * @return <tt>true</tt> if the action can only be processed in the future; <tt>false</tt>
   * otherwise.
   */
  public final boolean isFuture() {
    return future;
  }

  /**
   * Returns {@link Action#getMessage()}
   *
   * @return the action message
   * @see Action#getMessage()
   */
  @Override
  public final String toString() {
    return getMessage();
  }
}
