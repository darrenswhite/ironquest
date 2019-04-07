package com.darrenswhite.rs.ironquest.player;

import java.util.Optional;

/**
 * An enum representing the priority of a {@link QuestEntry}.
 */
public enum QuestPriority {

  MAXIMUM,
  HIGH,
  NORMAL,
  LOW,
  MINIMUM;

  public static Optional<QuestPriority> tryGet(String name) {
    for (QuestPriority qp : values()) {
      if (name.equalsIgnoreCase(qp.name())) {
        return Optional.of(qp);
      }
    }

    return Optional.empty();
  }

  @Override
  public String toString() {
    String str = super.toString();
    return str.toUpperCase().charAt(0) + str.toLowerCase().substring(1);
  }
}
