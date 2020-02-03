package com.darrenswhite.rs.ironquest.quest.requirement;

import com.darrenswhite.rs.ironquest.player.Player;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Objects;

/**
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    QuestPointsRequirement that = (QuestPointsRequirement) o;
    return amount == that.amount;
  }

  @Override
  public int hashCode() {
    return Objects.hash(amount);
  }

  @Override
  protected boolean testPlayer(Player p) {
    return p.getQuestPoints() >= amount;
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
