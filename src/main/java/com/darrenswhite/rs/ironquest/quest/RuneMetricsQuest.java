package com.darrenswhite.rs.ironquest.quest;

/**
 * @author Darren S. White
 */
public class RuneMetricsQuest {

  private String title;
  private Status status;
  private int difficulty;
  private boolean members;
  private int questPoints;
  private boolean userEligible;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public int getDifficulty() {
    return difficulty;
  }

  public void setDifficulty(int difficulty) {
    this.difficulty = difficulty;
  }

  public boolean isMembers() {
    return members;
  }

  public void setMembers(boolean members) {
    this.members = members;
  }

  public int getQuestPoints() {
    return questPoints;
  }

  public void setQuestPoints(int questPoints) {
    this.questPoints = questPoints;
  }

  public boolean isUserEligible() {
    return userEligible;
  }

  public void setUserEligible(boolean userEligible) {
    this.userEligible = userEligible;
  }

  public enum Status {
    COMPLETED,
    NOT_STARTED,
    STARTED
  }
}
