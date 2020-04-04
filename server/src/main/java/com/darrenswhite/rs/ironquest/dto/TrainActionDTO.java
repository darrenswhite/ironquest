package com.darrenswhite.rs.ironquest.dto;

import com.darrenswhite.rs.ironquest.action.ActionType;
import com.darrenswhite.rs.ironquest.action.TrainAction;
import java.util.Objects;

/**
 * Data Transfer Object for {@link TrainAction}.
 *
 * @author Darren S. White
 */
public class TrainActionDTO extends ActionDTO {

  TrainActionDTO(Builder builder) {
    super(ActionType.TRAIN, builder.player, builder.future, builder.message);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof TrainActionDTO)) {
      return false;
    }
    TrainActionDTO that = (TrainActionDTO) o;
    return future == that.future && type == that.type && Objects.equals(player, that.player)
        && Objects.equals(message, that.message);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final int hashCode() {
    return Objects.hash(future, type, player, message);
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
      return new TrainActionDTO(this);
    }
  }
}
