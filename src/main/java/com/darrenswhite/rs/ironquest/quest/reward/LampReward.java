package com.darrenswhite.rs.ironquest.quest.reward;

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

  private static final double[] SMALL_XP_LAMP_VALUES = {62, 69, 77, 85, 93, 104, 123, 127, 144, 153,
      170, 188, 205, 229, 252, 261, 274, 285, 298, 310, 324, 337, 352, 367, 384, 399, 405, 414, 453,
      473, 493, 514, 536, 559, 583, 608, 635, 662, 691, 720, 752, 784, 818, 853, 889, 929, 970,
      1012, 1055, 1101, 1148, 1200, 1249, 1304, 1362, 1422, 1485, 1546, 1616, 1684, 1757, 1835,
      1911, 2004, 2108, 2171, 2269, 2379, 2470, 2592, 2693, 2809, 2946, 3082, 3213, 3339, 3495,
      3646, 3792, 3980, 4166, 4347, 4521, 4762, 4918, 5033, 5375, 5592, 5922, 6121, 6451, 6614,
      6928, 7236, 7532, 8064, 8347, 8602};

  private static final double[] MEDIUM_XP_LAMP_VALUES = {125, 138, 154, 170, 186, 208, 246, 254,
      288, 307, 340, 376, 411, 458, 504, 523, 548, 570, 596, 620, 649, 674, 704, 735, 768, 798, 810,
      828, 906, 946, 986, 1028, 1072, 1118, 1166, 1217, 1270, 1324, 1383, 1441, 1504, 1569, 1636,
      1707, 1779, 1858, 1941, 2025, 2110, 2202, 2296, 2400, 2499, 2609, 2724, 2844, 2970, 3092,
      3233, 3368, 3515, 3671, 3822, 4009, 4216, 4343, 4538, 4758, 4940, 5185, 5386, 5618, 5893,
      6164, 6427, 6679, 6990, 7293, 7584, 7960, 8332, 8695, 9043, 9524, 9837, 10066, 10751, 11185,
      11845, 12243, 12903, 13229, 13857, 14472, 15065, 16129, 16695, 17204};

  private static final double[] LARGE_XP_LAMP_VALUES = {250, 276, 308, 340, 373, 416, 492, 508, 577,
      614, 680, 752, 822, 916, 1008, 1046, 1096, 1140, 1192, 1240, 1298, 1348, 1408, 1470, 1536,
      1596, 1621, 1656, 1812, 1892, 1973, 2056, 2144, 2237, 2332, 2434, 2540, 2648, 2766, 2882,
      3008, 3138, 3272, 3414, 3558, 3716, 3882, 4050, 4220, 4404, 4593, 4800, 4998, 5218, 5448,
      5688, 5940, 6184, 6466, 6737, 7030, 7342, 7645, 8018, 8432, 8686, 9076, 9516, 9880, 10371,
      10772, 11237, 11786, 12328, 12855, 13358, 13980, 14587, 15169, 15920, 16664, 17390, 18087,
      19048, 19674, 20132, 21502, 22370, 23690, 24486, 25806, 26458, 27714, 28944, 30130, 32258,
      33390, 34408};

  private static final double[] HUGE_XP_LAMP_VALUES = {500, 552, 616, 680, 746, 832, 984, 1016,
      1154, 1228, 1360, 1504, 1644, 1832, 2016, 2092, 2192, 2280, 2384, 2480, 2596, 2696, 2816,
      2940, 3072, 3192, 3242, 3312, 3624, 3784, 3946, 4112, 4288, 4474, 4664, 4868, 5080, 5296,
      5532, 5764, 6016, 6276, 6544, 6828, 7116, 7432, 7764, 8100, 8440, 8808, 9186, 9600, 9996,
      10436, 10896, 11376, 11880, 12368, 12932, 13474, 14060, 14684, 15290, 16036, 16864, 17372,
      18152, 19032, 19760, 20742, 21544, 22474, 23572, 24656, 25710, 26716, 27960, 29174, 30338,
      31840, 33328, 34780, 36174, 38096, 39348, 40264, 43004, 44740, 47380, 48972, 51612, 52916,
      55428, 57888, 60260, 64516, 66780, 68816};

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
  private static final Map<Set<Skill>, Integer> DEFAULT_REQUIREMENTS;

  static {
    Map<Set<Skill>, Integer> defaultRequirements = new HashMap<>();
    Set<Skill> skills;

    for (Skill s : Skill.values()) {
      skills = new HashSet<>();
      skills.add(s);
      defaultRequirements.put(skills, 1);
    }

    DEFAULT_REQUIREMENTS = Collections.unmodifiableMap(defaultRequirements);
  }

  @JsonDeserialize(using = LampRequirementsDeserializer.class)
  private Map<Set<Skill>, Integer> requirements = new HashMap<>(DEFAULT_REQUIREMENTS);
  private double xp;
  private boolean exclusive;
  private LampType type;
  private boolean singleChoice;
  private double multiplier = 1;

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

  public LampType getType() {
    return type;
  }

  public void setType(LampType type) {
    this.type = type;
  }

  public boolean isSingleChoice() {
    return singleChoice;
  }

  public void setSingleChoice(boolean singleChoice) {
    this.singleChoice = singleChoice;
  }

  public double getMultiplier() {
    return multiplier;
  }

  public void setMultiplier(double multiplier) {
    this.multiplier = multiplier;
  }

  public Set<Set<Skill>> getChoices(Player player, Set<Set<Skill>> previous) {
    Map<Set<Skill>, Integer> choices;

    if (singleChoice) {
      choices = new HashMap<>();
      for (Entry<Set<Skill>, Integer> entry : requirements.entrySet()) {
        for (Skill skill : entry.getKey()) {
          Set<Skill> split = new HashSet<>();
          split.add(skill);
          choices.put(split, entry.getValue());
        }
      }
    } else {
      choices = new HashMap<>(requirements);
    }

    return choices.entrySet().stream().filter(
        e -> e.getKey().stream().noneMatch(s -> player.getLevel(s) < e.getValue()) && (!exclusive
            || !previous.contains(e.getKey()))).map(Map.Entry::getKey).collect(Collectors.toSet());
  }

  public double getXpForSkills(Player player, Set<Skill> skills) {
    int skillsLen = skills.size();
    double actualXp;

    if (type == LampType.XP) {
      actualXp = xp;
    } else if (skillsLen != 1) {
      throw new IllegalArgumentException("Dynamic lamps can only be used on one skill");
    } else {
      int level = Math.min(98, player.getLevel(skills.iterator().next())) - 1;

      if (type == LampType.SMALL_XP) {
        actualXp = SMALL_XP_LAMP_VALUES[level];
      } else if (type == LampType.MEDIUM_XP) {
        actualXp = MEDIUM_XP_LAMP_VALUES[level];
      } else if (type == LampType.LARGE_XP) {
        actualXp = LARGE_XP_LAMP_VALUES[level];
      } else if (type == LampType.HUGE_XP) {
        actualXp = HUGE_XP_LAMP_VALUES[level];
      } else if (type == LampType.DRAGONKIN) {
        actualXp = Math.floor((Math.pow(level, 3) - 2 * Math.pow(level, 2) + 100 * level) / 20);
      } else {
        throw new IllegalArgumentException("Unknown lamp type: " + type);
      }
    }

    actualXp *= multiplier;

    return actualXp;
  }

  public boolean meetsRequirements(Player player) {
    if (requirements.isEmpty()) {
      return true;
    }

    return requirements.entrySet().stream().anyMatch(e -> e.getKey().stream().allMatch(s -> {
      if (s == Skill.INVENTION && (player.getLevel(Skill.CRAFTING) < 80
          || player.getLevel(Skill.DIVINATION) < 80 || player.getLevel(Skill.SMITHING) < 80)) {
        return false;
      }
      return player.getLevel(s) >= e.getValue();
    }));
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
