package com.darrenswhite.rs.ironquest.dto;

/**
 * Data Transfer Object for {@link com.darrenswhite.rs.ironquest.quest.Quest}.
 *
 * @author Darren S. White
 */
public class QuestDTO {

  private final String displayName;

  private QuestDTO(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }

  public static class Builder {

    private String displayName;

    public Builder withDisplayName(String displayName) {
      this.displayName = displayName;
      return this;
    }

    public QuestDTO build() {
      return new QuestDTO(displayName);
    }
  }
}
