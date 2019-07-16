package com.darrenswhite.rs.ironquest.quest.reward;

import com.darrenswhite.rs.ironquest.dto.LampRewardDTO;
import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.Skill;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Darren S. White
 */
public class LampReward implements Reward {

  /**
   * The case to match ANY {@link Skill} level
   */
  private static final String ANY_SKILL = "*";

  /**
   * The case to match all {@link Skill} levels
   */
  private static final String ALL_SKILLS = "&";

  /**
   * Default {@link Skill} requirements if none are present.
   *
   * Default is any {@link Skill} at level 1.
   */
  private static final Map<Set<Skill>, Integer> DEFAULT_REQUIREMENTS = new HashMap<>();

  static {
    Set<Skill> skills;

    for (Skill s : Skill.values()) {
      skills = new HashSet<>();
      skills.add(s);
      DEFAULT_REQUIREMENTS.put(skills, 1);
    }
  }

  @JsonDeserialize(using = LampRequirementsDeserializer.class)
  private Map<Set<Skill>, Integer> requirements = DEFAULT_REQUIREMENTS;
  private double xp;
  private boolean exclusive;

  public Map<Set<Skill>, Integer> getRequirements() {
    return requirements;
  }

  public void setRequirements(Map<Set<Skill>, Integer> requirements) {
    this.requirements = requirements;
  }

  public double getXp() {
    return xp;
  }

  public void setXp(double xp) {
    this.xp = xp;
  }

  public boolean isExclusive() {
    return exclusive;
  }

  public void setExclusive(boolean exclusive) {
    this.exclusive = exclusive;
  }

  public boolean meetsRequirements(Player player) {
    if (requirements.isEmpty()) {
      return true;
    }

    return requirements.entrySet().stream().anyMatch(e -> e.getKey().stream().anyMatch(s -> {
      if (s == Skill.INVENTION && (player.getLevel(Skill.CRAFTING) < 80
          || player.getLevel(Skill.DIVINATION) < 80 || player.getLevel(Skill.SMITHING) < 80)) {
        return false;
      }
      return player.getLevel(s) >= e.getValue();
    }));
  }

  public LampRewardDTO createDTO() {
    return new LampRewardDTO.Builder().withXp(getXp()).build();
  }

  public static class LampRequirementsDeserializer extends
      JsonDeserializer<Map<Set<Skill>, Integer>> {

    @Override
    public Map<Set<Skill>, Integer> deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException {
      JsonNode node = p.getCodec().readTree(p);
      Map<Set<Skill>, Integer> requirements = new HashMap<>();
      Set<Skill> skills;

      for (Iterator<Entry<String, JsonNode>> it = node.fields(); it.hasNext(); ) {
        Entry<String, JsonNode> e = it.next();
        String key = e.getKey().toUpperCase();
        int reqLvl = e.getValue().asInt();

        switch (key) {
          case ANY_SKILL:
            for (Skill s : Skill.values()) {
              skills = new HashSet<>();
              skills.add(s);
              requirements.put(skills, reqLvl);
            }
            break;
          case ALL_SKILLS:
            skills = new HashSet<>();
            Collections.addAll(skills, Skill.values());
            requirements.put(skills, reqLvl);
            break;
          default:
            String[] keys = key.split(",");
            skills = Arrays.stream(keys).map(Skill::valueOf).collect(Collectors.toSet());
            requirements.put(skills, reqLvl);
        }
      }

      return requirements;
    }
  }
}
