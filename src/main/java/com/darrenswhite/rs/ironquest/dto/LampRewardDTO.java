package com.darrenswhite.rs.ironquest.dto;

/**
 * Data Transfer Object for {@link com.darrenswhite.rs.ironquest.quest.reward.LampReward}.
 *
 * @author Darren S. White
 */
public class LampRewardDTO {

  private final double xp;

  private LampRewardDTO(double xp) {
    this.xp = xp;
  }

  public double getXp() {
    return xp;
  }

  public static class Builder {

    private double xp;

    public Builder withXp(double xp) {
      this.xp = xp;
      return this;
    }

    public LampRewardDTO build() {
      return new LampRewardDTO(xp);
    }
  }
}
