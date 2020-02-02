package com.darrenswhite.rs.ironquest.dto;

import com.darrenswhite.rs.ironquest.action.ActionType;

/**
 * Data Transfer Object for {@link com.darrenswhite.rs.ironquest.action.TrainAction}.
 *
 * @author Darren S. White
 */
public class TrainActionDTO extends ActionDTO {

  TrainActionDTO(PlayerDTO player, boolean future, String message) {
    super(ActionType.TRAIN, player, future, message);
  }

  public static class Builder {

    private PlayerDTO player;
    private boolean future;
    private String message;

    public Builder withPlayer(PlayerDTO player) {
      this.player = player;
      return this;
    }

    public Builder withFuture(boolean future) {
      this.future = future;
      return this;
    }

    public Builder withMessage(String message) {
      this.message = message;
      return this;
    }

    public TrainActionDTO build() {
      return new TrainActionDTO(player, future, message);
    }
  }
}
