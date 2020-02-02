package com.darrenswhite.rs.ironquest.dto;

import com.darrenswhite.rs.ironquest.player.Skill;
import java.util.Map;
import java.util.Objects;

/**
 * Data Transfer Object for {@link com.darrenswhite.rs.ironquest.player.Player}.
 *
 * @author Darren S. White
 */
public class PlayerDTO {

  private final String name;
  private final Map<Skill, Integer> levels;
  private final int questPoints;
  private final int totalLevel;
  private final int combatLevel;

  PlayerDTO(String name, Map<Skill, Integer> levels, int questPoints, int totalLevel,
      int combatLevel) {
    this.name = name;
    this.levels = levels;
    this.questPoints = questPoints;
    this.totalLevel = totalLevel;
    this.combatLevel = combatLevel;
  }

  public String getName() {
    return name;
  }

  public Map<Skill, Integer> getLevels() {
    return levels;
  }

  public int getQuestPoints() {
    return questPoints;
  }

  public int getTotalLevel() {
    return totalLevel;
  }

  public int getCombatLevel() {
    return combatLevel;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PlayerDTO playerDTO = (PlayerDTO) o;
    return questPoints == playerDTO.questPoints && totalLevel == playerDTO.totalLevel
        && combatLevel == playerDTO.combatLevel && Objects.equals(name, playerDTO.name) && Objects
        .equals(levels, playerDTO.levels);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, levels, questPoints, totalLevel, combatLevel);
  }

  public static class Builder {

    private String name;
    private Map<Skill, Integer> levels;
    private int questPoints;
    private int totalLevel;
    private int combatLevel;

    public Builder setName(String name) {
      this.name = name;
      return this;
    }

    public Builder withLevels(Map<Skill, Integer> levels) {
      this.levels = levels;
      return this;
    }

    public Builder withQuestPoints(int questPoints) {
      this.questPoints = questPoints;
      return this;
    }

    public Builder withTotalLevel(int totalLevel) {
      this.totalLevel = totalLevel;
      return this;
    }

    public Builder withCombatLevel(int combatLevel) {
      this.combatLevel = combatLevel;
      return this;
    }

    public PlayerDTO build() {
      return new PlayerDTO(name, levels, questPoints, totalLevel, combatLevel);
    }
  }
}
