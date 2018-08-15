package com.darrenswhite.rs.ironquest.quest;

import java.util.Objects;

/**
 * @author Darren White
 */
public class RuneMetricsQuest {

  private final String title;
  private final Status status;
  private final int difficulty;
  private final boolean members;
  private final int questPoints;
  private final boolean userEligible;

  public RuneMetricsQuest(String title, Status status, int difficulty, boolean members,
      int questPoints,
      boolean userEligible) {
    this.title = Objects.requireNonNull(title);
    this.status = Objects.requireNonNull(status);
    this.difficulty = difficulty;
    this.members = members;
    this.questPoints = questPoints;
    this.userEligible = userEligible;
  }

  public int getDifficulty() {
    return difficulty;
  }

  public int getQuestPoints() {
    return questPoints;
  }

  public Status getStatus() {
    return status;
  }

  public String getTitle() {
    return title;
  }

  public boolean isMembers() {
    return members;
  }

  public boolean isUserEligible() {
    return userEligible;
  }

  @Override
  public String toString() {
    return "RuneMetricsQuest{" +
        "title='" + title + '\'' +
        ", status=" + status +
        ", difficulty=" + difficulty +
        ", members=" + members +
        ", questPoints=" + questPoints +
        ", userEligible=" + userEligible +
        '}';
  }

  public enum Status {
    COMPLETED,
    NOT_STARTED,
    STARTED
  }
}
