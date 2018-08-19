package com.darrenswhite.rs.ironquest.action;

import com.darrenswhite.rs.ironquest.player.Player;
import java.util.Objects;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Darren White
 */
public abstract class Action {

  /**
   * The logger
   */
  private static final Logger LOG = LogManager.getLogger(Action.class);

  /**
   * The instance of the Player
   */
  private final Player player;

  /**
   * Creates a new Action with this Player. The Player object is copied to preserve its data.
   *
   * @param player The Player instance
   */
  public Action(Player player) {
    this.player = Objects.requireNonNull(player).copy();
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

  /**
   * The action to perform when the Action is clicked
   *
   * @param scene The scene the MouseEvent originated from
   * @param e The MouseEvent which occurred
   */
  public void onClick(Scene scene, MouseEvent e) {
    LOG.debug("Action click: {}", e);
  }

  @Override
  public String toString() {
    return getMessage();
  }
}
