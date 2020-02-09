package com.darrenswhite.rs.ironquest.quest.reward;

/**
 * An enum represeting a type of {@link LampReward}.
 *
 * @author Darren S. White
 */
public enum LampType {

  XP("XP Lamp"),
  SMALL_XP("Small XP Lamp"),
  MEDIUM_XP("Medium XP Lamp"),
  LARGE_XP("Large XP Lamp"),
  HUGE_XP("Huge XP Lamp"),
  DRAGONKIN("Dragonkin Lamp");

  private final String description;

  LampType(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }
}
