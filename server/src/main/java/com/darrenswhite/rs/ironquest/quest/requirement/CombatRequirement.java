package com.darrenswhite.rs.ironquest.quest.requirement;

import com.darrenswhite.rs.ironquest.player.Player;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Objects;

/**
 * @author Darren S. White
 */
@JsonDeserialize(builder = CombatRequirement.Builder.class)
public class CombatRequirement extends Requirement {

  private final int level;

  CombatRequirement(boolean ironman, boolean recommended, int level) {
    super(ironman, recommended);
    this.level = level;
  }

  public int getLevel() {
    return level;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CombatRequirement that = (CombatRequirement) o;
    return ironman == that.ironman && recommended == that.recommended && level == that.level;
  }

  @Override
  public int hashCode() {
    return Objects.hash(ironman, recommended, level);
  }

  @Override
  protected boolean testPlayer(Player p) {
    return p.getCombatLevel() >= level;
  }

  public static class Builder {

    private int level;
    private boolean ironman;
    private boolean recommended;

    @JsonCreator
    public Builder() {
    }

    public Builder(int level) {
      this.level = level;
    }

    public Builder withLevel(int level) {
      this.level = level;
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

    public CombatRequirement build() {
      return new CombatRequirement(ironman, recommended, level);
    }
  }
}
