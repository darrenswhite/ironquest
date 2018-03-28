package com.darrenswhite.rs.ironquest.player;

import java.util.Objects;
import java.util.Optional;

/**
 * @author Darren White
 */
public enum Skill {

    AGILITY(17, Type.SUPPORT, true),
    ATTACK(1, Type.COMBAT, false),
    CONSTITUTION(4, Type.COMBAT, false),
    CONSTRUCTION(23, Type.ARTISAN, true),
    COOKING(8, Type.ARTISAN, false),
    CRAFTING(13, Type.ARTISAN, false),
    DEFENCE(2, Type.COMBAT, false),
    DIVINATION(26, Type.GATHERING, true),
    DUNGEONEERING(25, Type.SUPPORT, false, 120),
    FARMING(20, Type.GATHERING, true),
    FIREMAKING(12, Type.ARTISAN, false),
    FISHING(11, Type.GATHERING, false),
    FLETCHING(10, Type.ARTISAN, true),
    HERBLORE(16, Type.ARTISAN, true),
    HUNTER(22, Type.GATHERING, true),
    INVENTION(27, Type.ELITE, true, 120),
    MAGIC(7, Type.COMBAT, false),
    MINING(15, Type.GATHERING, false),
    PRAYER(6, Type.COMBAT, false),
    RANGED(5, Type.GATHERING, false),
    RUNECRAFTING(21, Type.ARTISAN, false),
    SLAYER(19, Type.SUPPORT, true),
    SMITHING(14, Type.ARTISAN, false),
    STRENGTH(3, Type.COMBAT, false),
    SUMMONING(24, Type.COMBAT, true),
    THIEVING(18, Type.SUPPORT, true),
    WOODCUTTING(9, Type.GATHERING, false);

    /**
     * Normal skill xp table up to 120
     */
    public static final int[] XP_TABLE = {0, 0, 83, 174, 276, 388, 512, 650,
            801, 969, 1154, 1358, 1584, 1833, 2107, 2411, 2746, 3115, 3523,
            3973, 4470, 5018, 5624, 6291, 7028, 7842, 8740, 9730, 10824,
            12031, 13363, 14833, 16456, 18247, 20224, 22406, 24815, 27473,
            30408, 33648, 37224, 41171, 45529, 50339, 55649, 61512, 67983,
            75127, 83014, 91721, 101333, 111945, 123660, 136594, 150872,
            166636, 184040, 203254, 224466, 247886, 273742, 302288, 333804,
            368599, 407015, 449428, 496254, 547953, 605032, 668051, 737627,
            814445, 899257, 992895, 1096278, 1210421, 1336443, 1475581,
            1629200, 1798808, 1986068, 2192818, 2421087, 2673114, 2951373,
            3258594, 3597792, 3972294, 4385776, 4842295, 5346332, 5902831,
            6517253, 7195629, 7944614, 8771558, 9684577, 10692629, 11805606,
            13034431, 14391160, 15889109, 17542976, 19368992, 21385073,
            23611006, 26068632, 28782069, 31777943, 35085654, 38737661,
            42769801, 47221641, 52136869, 57563718, 63555443, 70170840,
            77474828, 85539082, 94442737, 104273167};

    /**
     * Elite skill xp table up to 120
     */
    public static final int[] XP_TABLE_ELITE = {0, 0, 830, 1861, 2902, 3980,
            5126, 6380, 7787, 9400, 11275, 13605, 16372, 19656, 23546, 28134,
            33520, 39809, 47109, 55535, 65209, 77190, 90811, 106221, 123573,
            143025, 164742, 188893, 215651, 245196, 277713, 316311, 358547,
            404364, 454796, 509259, 568254, 632019, 700797, 774834, 854383,
            946227, 1044569, 1149696, 1261903, 1381488, 1508756, 1644015,
            1787581, 1939773, 2100917, 2283490, 2476369, 2679917, 2894505,
            3120508, 3358307, 3608290, 3870846, 4146374, 4435275, 4758122,
            5096111, 5449685, 5819299, 6205407, 6608473, 7028964, 7467354,
            7924122, 8399751, 8925664, 9472665, 10041285, 10632061, 11245538,
            11882262, 12542789, 13227679, 13937496, 14672812, 15478994,
            16313404, 17176661, 18069395, 18992239, 19945833, 20930821,
            21947856, 22997593, 24080695, 25259906, 26475754, 27728955,
            29020233, 30350318, 31719944, 33129852, 34580790, 36073511,
            37608773, 39270442, 40978509, 42733789, 44537107, 46389292,
            48291180, 50243611, 52247435, 54303504, 56412678, 58575824,
            60793812, 63067521, 65397835, 67785643, 70231841, 72737330,
            75303019, 77929820, 80618654};

    /**
     * The maximum XP for any Skill
     */
    public static final int MAX_XP = Integer.MAX_VALUE;

    /**
     * The shorthand XP formats
     */
    private static final char[] XP_FORMATS = {'k', 'm', 'b'};

    /**
     * The Skill id
     */
    private final int id;

    /**
     * The Skill type
     */
    private final Type type;

    /**
     * If the Skill is members-only
     */
    private final boolean members;

    /**
     * The maximum level for this Skill
     */
    private final int maxLevel;

    /**
     * Creates a new Skill
     *
     * @param id      The Skill id
     * @param type    The Skill type
     * @param members If the skill is members or free
     */
    Skill(int id, Type type, boolean members) {
        this(id, type, members, 99);
    }

    /**
     * Creates a new Skill
     *
     * @param id       The Skill id
     * @param type     The Skill type
     * @param members  If the skill is members or free
     * @param maxLevel The maximum level for the Skill
     */
    Skill(int id, Type type, boolean members, int maxLevel) {
        this.id = id;
        this.type = Objects.requireNonNull(type);
        this.members = members;
        this.maxLevel = maxLevel;
    }

    /**
     * Checks the XP is in valid range
     *
     * @param xp The XP to check
     */
    private static void checkXPRange(double xp) {
        if (xp < 0 || xp > MAX_XP) {
            throw new IllegalArgumentException("XP out of range: " + xp);
        }
    }

    /**
     * Formats the XP as a String
     *
     * @param xp The XP to format
     * @return The XP as a formatted String
     */
    public static String formatXP(double xp) {
        // Ensure valid XP range
        checkXPRange(xp);

        int fmt = -1;

        // Get the XP shorthand format
        while (xp >= 1000.0) {
            xp /= 1000.0;
            fmt++;
        }

        // Convert XP to a String
        String xpStr = Double.toString(xp);

        // Number is < 1000
        if (fmt == -1) {
            return xpStr;
        }

        // Add the shorthand format to the XP String
        return xpStr + XP_FORMATS[fmt];
    }

    /**
     * Gets a Skill with the given name
     *
     * @param name The Skill name
     * @return The Skill with the name
     */
    public static Optional<Skill> tryGet(String name) {
        // Use optionals to get the Skill
        for (Skill s : values()) {
            if (name.equalsIgnoreCase(s.name())) {
                return Optional.of(s);
            }
        }

        return Optional.empty();
    }

    /**
     * Gets a Skill with the given id
     *
     * @param id The Skill id
     * @return The Skill with the id
     */
    public static Optional<Skill> tryGet(int id) {
        // Use optionals to get the Skill
        for (Skill s : values()) {
            if (id == s.id) {
                return Optional.of(s);
            }
        }

        return Optional.empty();
    }

    /**
     * Gets a Skill level at the given XP
     *
     * @param xp The XP to get the level at
     * @return The Skill level
     */
    public int getLevelAt(double xp) {
        // Ensure valid XP range
        checkXPRange(xp);

        // Use normal xp table
        int[] table = XP_TABLE;

        // Use elite xp table
        if (getType() == Type.ELITE) {
            table = XP_TABLE_ELITE;
        }

        // Force max skill level
        int max = Math.min(maxLevel, table.length - 1);

        // Start from the end of the xp table,
        // iterate backwards until the level is reached
        for (int i = max; i > 0; i--) {
            if (xp >= table[i]) {
                return i;
            }
        }

        // Lowest level is 1
        return 1;
    }

    /**
     * Get the Skill type
     *
     * @return The Skill type
     */
    public Type getType() {
        return type;
    }

    /**
     * Get the XP at a given level
     *
     * @param lvl The level to get the XP for
     * @return The XP at a given level
     */
    public int getXPAt(int lvl) {
        // Use normal xp table
        int[] table = XP_TABLE;

        // Use elite xp table
        if (getType() == Type.ELITE) {
            table = XP_TABLE_ELITE;
        }

        // Get the xp for this level
        return table[lvl];
    }

    @Override
    public String toString() {
        // Get the first char as uppercase
        char first = name().toUpperCase().charAt(0);
        // Get the rest of the name as lowercase with spaces
        String remaining = name().substring(1).toLowerCase()
                .replace('_', ' ');

        return first + remaining;
    }

    /**
     * A Skill type
     */
    public enum Type {

        ARTISAN,
        COMBAT,
        ELITE,
        GATHERING,
        SUPPORT;

        @Override
        public String toString() {
            // Get the first char as uppercase
            char first = name().toUpperCase().charAt(0);
            // Get the rest of the name as lowercase with spaces
            String remaining = name().substring(1)
                    .toLowerCase()
                    .replace('_', ' ');

            return first + remaining;
        }
    }
}