package com.darrenswhite.rs.ironquest.quest.requirement;

import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.Skill;
import com.darrenswhite.rs.ironquest.quest.Quest;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A class representing a {@link Skill} requirement for a {@link Quest}.
 *
 * @author Darren S. White
 */
@JsonDeserialize(builder = SkillRequirement.Builder.class)
public class SkillRequirement extends Requirement {

  private final Skill skill;
  private final int level;

  SkillRequirement(boolean ironman, boolean recommended, Skill skill, int level) {
    super(ironman, recommended);
    this.skill = skill;
    this.level = level;
  }

  /**
   * Merge both {@link SkillRequirement}s together.
   *
   * @param requirements the first collection
   * @param merge the second collection
   * @return {@link LinkedHashSet} of merged {@link SkillRequirement}.
   */
  public static Set<SkillRequirement> merge(Collection<SkillRequirement> requirements,
      Collection<SkillRequirement> merge) {
    Set<SkillRequirement> merged = new LinkedHashSet<>(requirements);

    merge.forEach(mergeRequirement -> {
      SkillRequirement skillRequirement = requirements.stream()
          .filter(sr -> sr.skill == mergeRequirement.skill).findFirst().orElse(null);

      if (skillRequirement != null) {
        if (mergeRequirement.level > skillRequirement.level) {
          merged.remove(skillRequirement);
          merged.add(mergeRequirement);
        }
      } else {
        merged.add(mergeRequirement);
      }
    });

    return merged;
  }

  public int getLevel() {
    return level;
  }

  public Skill getSkill() {
    return skill;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SkillRequirement that = (SkillRequirement) o;
    return ironman == that.ironman && recommended == that.recommended && level == that.level
        && skill == that.skill;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects.hash(ironman, recommended, skill, level);
  }

  /**
   * {@inheritDoc}
   *
   * @return <tt>true</tt> if the player has the required skill level; <tt>false</tt> otherwise.
   */
  @Override
  protected boolean testPlayer(Player player) {
    return player.getLevel(skill) >= level;
  }

  public static class Builder {

    private Skill skill;
    private int level;
    private boolean ironman = false;
    private boolean recommended = false;

    @JsonCreator
    public Builder() {
    }

    public Builder(Skill skill, int level) {
      this.skill = skill;
      this.level = level;
    }

    public Builder withSkill(Skill skill) {
      this.skill = skill;
      return this;
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

    public SkillRequirement build() {
      return new SkillRequirement(ironman, recommended, skill, level);
    }
  }
}
