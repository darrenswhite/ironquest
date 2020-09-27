package com.darrenswhite.rs.ironquest.player;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * An enum representing a player skill.
 *
 * @author Darren S. White
 */
public enum Skill {

  AGILITY(17, SkillType.SUPPORT, true),
  ARCHAEOLOGY(28, SkillType.GATHERING, true, 120),
  ATTACK(1, SkillType.COMBAT, false),
  CONSTITUTION(4, SkillType.COMBAT, false),
  CONSTRUCTION(23, SkillType.ARTISAN, true),
  COOKING(8, SkillType.ARTISAN, false),
  CRAFTING(13, SkillType.ARTISAN, false),
  DEFENCE(2, SkillType.COMBAT, false),
  DIVINATION(26, SkillType.GATHERING, true),
  DUNGEONEERING(25, SkillType.SUPPORT, false, 120),
  FARMING(20, SkillType.GATHERING, true),
  FIREMAKING(12, SkillType.ARTISAN, false),
  FISHING(11, SkillType.GATHERING, false),
  FLETCHING(10, SkillType.ARTISAN, true),
  HERBLORE(16, SkillType.ARTISAN, true),
  HUNTER(22, SkillType.GATHERING, true),
  INVENTION(27, SkillType.ELITE, true, 120),
  MAGIC(7, SkillType.COMBAT, false),
  MINING(15, SkillType.GATHERING, false),
  PRAYER(6, SkillType.COMBAT, false),
  RANGED(5, SkillType.GATHERING, false),
  RUNECRAFTING(21, SkillType.ARTISAN, false),
  SLAYER(19, SkillType.SUPPORT, true, 120),
  SMITHING(14, SkillType.ARTISAN, false),
  STRENGTH(3, SkillType.COMBAT, false),
  SUMMONING(24, SkillType.COMBAT, true),
  THIEVING(18, SkillType.SUPPORT, true),
  WOODCUTTING(9, SkillType.GATHERING, false);

  /**
   * The maximum experience for any given skill.
   */
  public static final double MAX_XP = 2147483648d;

  /**
   * The amount of xp for each skill for new accounts.
   */
  public static final Map<Skill, Double> INITIAL_XPS;

  /**
   * The xp-level table for standard skills.
   */
  protected static final double[] XP_TABLE = {0, 0, 83, 174, 276, 388, 512, 650, 801, 969, 1154,
      1358, 1584, 1833, 2107, 2411, 2746, 3115, 3523, 3973, 4470, 5018, 5624, 6291, 7028, 7842,
      8740, 9730, 10824, 12031, 13363, 14833, 16456, 18247, 20224, 22406, 24815, 27473, 30408,
      33648, 37224, 41171, 45529, 50339, 55649, 61512, 67983, 75127, 83014, 91721, 101333, 111945,
      123660, 136594, 150872, 166636, 184040, 203254, 224466, 247886, 273742, 302288, 333804,
      368599, 407015, 449428, 496254, 547953, 605032, 668051, 737627, 814445, 899257, 992895,
      1096278, 1210421, 1336443, 1475581, 1629200, 1798808, 1986068, 2192818, 2421087, 2673114,
      2951373, 3258594, 3597792, 3972294, 4385776, 4842295, 5346332, 5902831, 6517253, 7195629,
      7944614, 8771558, 9684577, 10692629, 11805606, 13034431, 14391160, 15889109, 17542976,
      19368992, 21385073, 23611006, 26068632, 28782069, 31777943, 35085654, 38737661, 42769801,
      47221641, 52136869, 57563718, 63555443, 70170840, 77474828, 85539082, 94442737, 104273167};

  /**
   * The xp-level table for elite skills.
   */
  protected static final double[] XP_TABLE_ELITE = {0, 0, 830, 1861, 2902, 3980, 5126, 6380, 7787,
      9400, 11275, 13605, 16372, 19656, 23546, 28134, 33520, 39809, 47109, 55535, 65209, 77190,
      90811, 106221, 123573, 143025, 164742, 188893, 215651, 245196, 277713, 316311, 358547, 404364,
      454796, 509259, 568254, 632019, 700797, 774834, 854383, 946227, 1044569, 1149696, 1261903,
      1381488, 1508756, 1644015, 1787581, 1939773, 2100917, 2283490, 2476369, 2679917, 2894505,
      3120508, 3358307, 3608290, 3870846, 4146374, 4435275, 4758122, 5096111, 5449685, 5819299,
      6205407, 6608473, 7028964, 7467354, 7924122, 8399751, 8925664, 9472665, 10041285, 10632061,
      11245538, 11882262, 12542789, 13227679, 13937496, 14672812, 15478994, 16313404, 17176661,
      18069395, 18992239, 19945833, 20930821, 21947856, 22997593, 24080695, 25259906, 26475754,
      27728955, 29020233, 30350318, 31719944, 33129852, 34580790, 36073511, 37608773, 39270442,
      40978509, 42733789, 44537107, 46389292, 48291180, 50243611, 52247435, 54303504, 56412678,
      58575824, 60793812, 63067521, 65397835, 67785643, 70231841, 72737330, 75303019, 77929820,
      80618654};

  /**
   * The number format used for xp.
   */
  private static final DecimalFormat XP_FORMAT = new DecimalFormat("#.####");

  /**
   * The letters used for donating a base 1000 amount of xp.
   */
  private static final char[] XP_FORMATS = {'k', 'm', 'b'};

  static {
    Map<Skill, Double> initialXps = new EnumMap<>(Skill.class);
    for (Skill skill : values()) {
      initialXps.put(skill, skill == CONSTITUTION ? XP_TABLE[10] : 0);
    }
    INITIAL_XPS = Collections.unmodifiableMap(initialXps);
  }

  private final int id;
  private final SkillType type;
  private final boolean members;
  private final int maxLevel;

  Skill(int id, SkillType type, boolean members) {
    this(id, type, members, 99);
  }

  Skill(int id, SkillType type, boolean members, int maxLevel) {
    this.id = id;
    this.type = type;
    this.members = members;
    this.maxLevel = maxLevel;
  }

  /**
   * Returns the specified xp in the string format of "#.###" with an optional trailing character
   * denoting the base of 1000.
   *
   * For example:
   *
   * 1000 -> 1k 10500000 -> 10.5m
   *
   * @param xp the xp to format
   * @return the xp formatted as a string
   * @see Skill#XP_FORMAT
   * @see Skill#XP_FORMATS
   */
  public static String formatXp(double xp) {
    checkXPRange(xp);

    int fmt = -1;

    while (xp >= 1000.0) {
      xp /= 1000.0;
      fmt++;
    }

    String xpStr = XP_FORMAT.format(xp);

    if (fmt != -1) {
      xpStr += XP_FORMATS[fmt];
    }

    return xpStr;

  }

  /**
   * Returns the {@link Skill} with the specified id if found.
   *
   * @param id the id of the skill
   * @return a skill with the given id; or null if not found
   */
  public static Skill getById(int id) {
    return Stream.of(values()).filter(skill -> skill.id == id).findFirst().orElse(null);
  }

  /**
   * Tests if the xp is positive and does not exceed the maximum xp.
   *
   * @param xp the xp to check
   * @throws IllegalArgumentException if the xp is invalid
   * @see Skill#MAX_XP
   */
  private static void checkXPRange(double xp) {
    if (xp < 0 || xp > MAX_XP) {
      throw new IllegalArgumentException("XP out of range: " + xp);
    }
  }

  public int getId() {
    return id;
  }

  public boolean isMembers() {
    return members;
  }

  public int getMaxLevel() {
    return maxLevel;
  }

  public SkillType getType() {
    return type;
  }

  /**
   * Returns the level for this skill at the specified xp.
   *
   * @param xp the xp
   * @return the level
   */
  public int getLevelAt(double xp) {
    checkXPRange(xp);

    double[] table = getXpTable();
    int max = Math.min(maxLevel, table.length - 1);
    int level = 1;

    for (int i = max; i > 0; i--) {
      if (xp >= table[i]) {
        level = i;
        break;
      }
    }

    return level;
  }

  /**
   * Returns the xp for this skill at the specified level.
   *
   * @param level the level
   * @return the xp
   */
  public double getXpAtLevel(int level) {
    return getXpTable()[level];
  }

  /**
   * Returns the xp-level table used for this skill.
   *
   * @return the xp table
   */
  public double[] getXpTable() {
    return type == SkillType.ELITE ? XP_TABLE_ELITE : XP_TABLE;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    String name = name();
    char first = name.toUpperCase().charAt(0);
    String remaining = name.substring(1).toLowerCase().replace('_', ' ');

    return first + remaining;
  }
}
