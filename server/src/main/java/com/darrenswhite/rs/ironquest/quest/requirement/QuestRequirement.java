package com.darrenswhite.rs.ironquest.quest.requirement;

import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.quest.Quest;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Objects;

/**
 * A class representing a quest requirement for a {@link Quest}.
 *
 * @author Darren S. White
 */
@JsonDeserialize(builder = QuestRequirement.Builder.class)
public class QuestRequirement extends Requirement {

  private final Quest quest;

  QuestRequirement(Builder builder) {
    super(builder.ironman, builder.recommended);
    this.quest = builder.quest;
  }

  public Quest getQuest() {
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
    if (!(o instanceof QuestRequirement)) {
      return false;
    }
    QuestRequirement that = (QuestRequirement) o;
    return ironman == that.ironman && recommended == that.recommended && Objects
        .equals(quest, that.quest);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final int hashCode() {
    return Objects.hash(ironman, recommended, quest);
  }

  /**
   * {@inheritDoc}
   *
   * @return <tt>true</tt> if the player has completed the quest; <tt>false</tt> otherwise.
   */
  @Override
  protected boolean testPlayer(Player player) {
    return player.isQuestCompleted(quest);
  }

  public static class Builder {

    private Quest quest;
    private boolean ironman;
    private boolean recommended;

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
      return new QuestRequirement(this);
    }
  }
}
