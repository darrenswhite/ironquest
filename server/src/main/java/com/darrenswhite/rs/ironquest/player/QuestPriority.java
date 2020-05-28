package com.darrenswhite.rs.ironquest.player;

/**
 * An enum representing the priority of a {@link QuestEntry}.
 *
 * @author Darren S. White
 */
public enum QuestPriority {

  MINIMUM(1),
  LOW(2),
  NORMAL(3),
  HIGH(4),
  MAXIMUM(5);

  private final int weight;

  QuestPriority(int weight) {
    this.weight = weight;
  }

  public int getWeight() {
    return weight;
  }

  /**
   * Test if this priority is considered less than {@link QuestPriority#NORMAL}.
   *
   * @return <tt>true</tt> if less than normal; <tt>false</tt> otherwise
   */
  public boolean lessThanNormal() {
    return weight < NORMAL.weight;
  }

  /**
   * Test if this priority is considered greater than {@link QuestPriority#NORMAL}.
   *
   * @return <tt>true</tt> if greater than normal; <tt>false</tt> otherwise
   */
  public boolean greaterThanNormal() {
    return weight > NORMAL.weight;
  }
}
