package com.darrenswhite.rs.ironquest.quest.requirement;

import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.quest.Quest;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Objects;

/**
 * A class representing a quest point requirement for a {@link Quest}.
 *
 * @author Darren S. White
 */
@JsonDeserialize(builder = QuestPointsRequirement.Builder.class)
public class QuestPointsRequirement extends Requirement {

  private final int amount;

  QuestPointsRequirement(Builder builder) {
    super(builder.ironman, builder.recommended);
    this.amount = builder.amount;
  }

  public int getAmount() {
    return amount;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof QuestPointsRequirement)) {
      return false;
    }
    QuestPointsRequirement that = (QuestPointsRequirement) o;
    return ironman == that.ironman && recommended == that.recommended && amount == that.amount;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final int hashCode() {
    return Objects.hash(ironman, recommended, amount);
  }

  /**
   * {@inheritDoc}
   *
   * @return <tt>true</tt> if the player has the required quest points; <tt>false</tt> otherwise.
   */
  @Override
  protected boolean testPlayer(Player player) {
    return player.getQuestPoints() >= amount;
  }

  public static class Builder {

    private int amount;
    private boolean ironman;
    private boolean recommended;

    @JsonCreator
    public Builder() {
    }

    public Builder(int amount) {
      this.amount = amount;
    }

    public Builder withAmount(int amount) {
      this.amount = amount;
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

    public QuestPointsRequirement build() {
      return new QuestPointsRequirement(this);
    }
  }
}
