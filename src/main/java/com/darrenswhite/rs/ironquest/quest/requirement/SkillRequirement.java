package com.darrenswhite.rs.ironquest.quest.requirement;

import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.Skill;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Darren S. White
 */
public class SkillRequirement extends Requirement {

  private Skill skill;
  private int level;

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

  public void setLevel(int level) {
    this.level = level;
  }

  public Skill getSkill() {
    return skill;
  }

  public void setSkill(Skill skill) {
    this.skill = skill;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(level);
    sb.append(' ');
    sb.append(skill);
    if (isIronman()) {
      sb.append(" (Ironman)");
    }
    if (isRecommended()) {
      sb.append(" (Recommended)");
    }
    return sb.toString();
  }

  @Override
  protected boolean testPlayer(Player p) {
    return p.getLevel(skill) >= level;
  }
}
