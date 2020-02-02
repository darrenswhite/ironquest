package com.darrenswhite.rs.ironquest.quest;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Objects;

/**
 * @author Darren S. White
 */
@JsonDeserialize(builder = RuneMetricsQuest.Builder.class)
public class RuneMetricsQuest {

  private final String title;
  private final Status status;
  private final int difficulty;
  private final boolean members;
  private final int questPoints;
  private final boolean userEligible;

  RuneMetricsQuest(Builder builder) {
    this.title = builder.title;
    this.status = builder.status;
    this.difficulty = builder.difficulty;
    this.members = builder.members;
    this.questPoints = builder.questPoints;
    this.userEligible = builder.userEligible;
  }

  public String getTitle() {
    return title;
  }

  public Status getStatus() {
    return status;
  }

  public int getDifficulty() {
    return difficulty;
  }

  public boolean isMembers() {
    return members;
  }

  public int getQuestPoints() {
    return questPoints;
  }

  public boolean isUserEligible() {
    return userEligible;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RuneMetricsQuest that = (RuneMetricsQuest) o;
    return difficulty == that.difficulty && members == that.members
        && questPoints == that.questPoints && userEligible == that.userEligible && Objects
        .equals(title, that.title) && status == that.status;
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, status, difficulty, members, questPoints, userEligible);
  }

  public enum Status {
    COMPLETED,
    NOT_STARTED,
    STARTED
  }

  public static class Builder {

    private String title;
    private Status status;
    private int difficulty;
    private boolean members;
    private int questPoints;
    private boolean userEligible;

    public Builder withTitle(String title) {
      this.title = title;
      return this;
    }

    public Builder withStatus(Status status) {
      this.status = status;
      return this;
    }

    public Builder withDifficulty(int difficulty) {
      this.difficulty = difficulty;
      return this;
    }

    public Builder withMembers(boolean members) {
      this.members = members;
      return this;
    }

    public Builder withQuestPoints(int questPoints) {
      this.questPoints = questPoints;
      return this;
    }

    public Builder withUserEligible(boolean userEligible) {
      this.userEligible = userEligible;
      return this;
    }

    public RuneMetricsQuest build() {
      return new RuneMetricsQuest(this);
    }
  }
}
