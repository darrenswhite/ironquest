package com.darrenswhite.rs.ironquest.dto;

import com.darrenswhite.rs.ironquest.action.ActionType;
import com.darrenswhite.rs.ironquest.action.LampAction;
import java.util.Objects;

/**
 * Data Transfer Object for {@link LampAction}.
 *
 * @author Darren S. White
 */
public class LampActionDTO extends ActionDTO {

  private final QuestDTO quest;

  LampActionDTO(Builder builder) {
    super(ActionType.LAMP, builder.player, builder.future, builder.message);
    this.quest = builder.quest;
  }

  public QuestDTO getQuest() {
    return quest;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof LampActionDTO)) {
      return false;
    }
    LampActionDTO that = (LampActionDTO) o;
    return future == that.future && type == that.type && Objects.equals(player, that.player)
        && Objects.equals(quest, that.quest) && Objects.equals(message, that.message);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final int hashCode() {
    return Objects.hash(future, type, player, quest, message);
  }

  public static class Builder {

    private PlayerDTO player;
    private boolean future;
    private String message;
    private QuestDTO quest;

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

    public Builder withQuest(QuestDTO quest) {
      this.quest = quest;
      return this;
    }

    public LampActionDTO build() {
      return new LampActionDTO(this);
    }
  }
}
