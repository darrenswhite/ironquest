package com.darrenswhite.rs.ironquest.action;

import com.darrenswhite.rs.ironquest.player.Player;

/**
 * @author Darren White
 */
public abstract class Action {

	/**
	 * The instance of the Player
	 */
	private final Player player;

	/**
	 * Creates a new Action with this Player. The Player object is
	 * copied to preserve its data.
	 *
	 * @param player The Player instance
	 */
	public Action(Player player) {
		this.player = player.copy();
	}

	/**
	 * The message for this Action
	 *
	 * @return A message
	 */
	public abstract String getMessage();

	/**
	 * Gets the Player object
	 *
	 * @return A Player
	 */
	public Player getPlayer() {
		return player;
	}

	@Override
	public String toString() {
		return getMessage();
	}
}