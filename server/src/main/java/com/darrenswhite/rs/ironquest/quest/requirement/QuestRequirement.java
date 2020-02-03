package com.darrenswhite.rs.ironquest.quest.requirement;

import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.quest.Quest;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Objects;

/**
 * @author Darren S. White
 */
@JsonDeserialize(builder = QuestRequirement.Builder.class)
public class QuestRequirement extends Requirement {

  private final Quest quest;

  QuestRequirement(boolean ironman, boolean recommended, Quest quest) {
    super(ironman, recommended);
    this.quest = quest;
  }

  public Quest getQuest() {
    return quest;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    QuestRequirement that = (QuestRequirement) o;
    return Objects.equals(quest, that.quest);
  }

  @Override
  public int hashCode() {
    return Objects.hash(quest);
  }

  @Override
  protected boolean testPlayer(Player p) {
    return p.isQuestCompleted(quest);
  }

  public static class Builder {

    private Quest quest;
    private boolean ironman;
    private boolean recommended;

    @JsonCreator
    public Builder() {
    }

    public Builder(Quest quest) {
      this.quest = quest;
    }

    public Builder withQuest(Quest quest) {
      this.quest = quest;
      return this;
    }

    public Builder withIronman(boolean ironman) {
      this.ironman = ironman;
      return this;
    }

    public Builder withRecommended(boolean recommended) {
      this.recommended = recommended;
      return this;
    }

    public QuestRequirement build() {
      return new QuestRequirement(ironman, recommended, quest);
    }
  }
}
