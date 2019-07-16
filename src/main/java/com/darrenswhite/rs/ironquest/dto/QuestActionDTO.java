package com.darrenswhite.rs.ironquest.dto;

import com.darrenswhite.rs.ironquest.action.ActionType;

/**
 * Data Transfer Object for {@link com.darrenswhite.rs.ironquest.action.QuestAction}.
 *
 * @author Darren S. White
 */
public class QuestActionDTO extends ActionDTO {

  private final QuestDTO quest;

  private QuestActionDTO(ActionType type, PlayerDTO player, boolean future, String message,
      QuestDTO quest) {
    super(type, player, future, message);
    this.quest = quest;
  }

  public QuestDTO getQuest() {
    return quest;
  }

  public static class Builder {

    private ActionType type;
    private PlayerDTO player;
    private boolean future;
    private String message;
    private QuestDTO quest;

    public Builder withType(ActionType type) {
      this.type = type;
      return this;
    }

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

    public QuestActionDTO build() {
      return new QuestActionDTO(type, player, future, message, quest);
    }
  }
}
