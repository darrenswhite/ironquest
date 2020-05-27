package com.darrenswhite.rs.ironquest.player;

/**
 * An enum representing the priority of a {@link QuestEntry}.
 *
 * @author Darren S. White
 */
public enum QuestPriority {

  MAXIMUM(5),
  HIGH(4),
  NORMAL(3),
  LOW(2),
  MINIMUM(1);

  private final int weight;

  QuestPriority(int weight) {
    this.weight = weight;
  }

  public int getWeight() {
    return weight;
  }
}
