package com.darrenswhite.rs.ironquest.dto;

import com.darrenswhite.rs.ironquest.action.ActionType;
import com.darrenswhite.rs.ironquest.action.QuestAction;

/**
 * Data Transfer Object for {@link QuestAction}.
 *
 * @author Darren S. White
 */
public class QuestActionDTO extends ActionDTO {

  private final QuestDTO quest;

  QuestActionDTO(PlayerDTO player, boolean future, String message, QuestDTO quest) {
    super(ActionType.QUEST, player, future, message);
    this.quest = quest;
  }

  public QuestDTO getQuest() {
    return quest;
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

    public QuestActionDTO build() {
      return new QuestActionDTO(player, future, message, quest);
    }
  }
}
