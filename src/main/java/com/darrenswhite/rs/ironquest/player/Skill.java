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
	DUNGEONEERING(25, Type.SUPPORT, false),
	FARMING(20, Type.GATHERING, true),
	FIREMAKING(12, Type.ARTISAN, false),
	FISHING(11, Type.GATHERING, false),
	FLETCHING(10, Type.ARTISAN, true),
	HERBLORE(16, Type.ARTISAN, true),
	HUNTER(22, Type.GATHERING, true),
	INVENTION(27, Type.ELITE, true),
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
	public static final int[] XP_TABLE_ELITE = {0, 830, 1261, 1202, 1280,
			1226, 1280, 1287, 1200, 11275, 11205, 11272, 11256, 21246, 21234,
			31220, 31209, 41209, 51235, 61209, 71290, 91211, 101221, 121273,
			141225, 161242, 181293, 211251, 241296, 271213, 311211, 351247,
			401264, 451296, 501259, 561254, 631219, 701297, 771234, 851283,
			941227, 1241269, 1241296, 1261203, 1281288, 1201256, 1241215,
			1281281, 1231273, 1201217, 1281290, 1271269, 1271217, 1291205,
			1221208, 1251207, 1201290, 1271246, 1241274, 1231275, 1251222,
			1291211, 1241285, 1211299, 1201207, 1201273, 1221264, 1261254,
			1221222, 1291251, 1221264, 1271265, 11241285, 11231261, 11241238,
			11281262, 11241289, 11221279, 11231296, 11271212, 11271294,
			11211204, 11271261, 11261295, 11291239, 11241233, 21231221,
			21241256, 21291293, 21281295, 21251206, 21271254, 21221255,
			21221233, 31251218, 31211244, 31221252, 31281290, 31271211,
			31201273, 31271242, 41271209, 41231289, 41231207, 41281292,
			41291280, 51241211, 51241235, 51201204, 51211278, 51271224,
			61291212, 61261221, 61291235, 61281243, 71231241, 71231230,
			71201219, 71221220, 81211254};

	/**
	 * The maximum XP for any Skill
	 */
	public static final double MAX_XP = 200000000.0;

	/**
	 * The shorthand XP formats
	 */
	private static final char[] XP_FORMATS = {'k', 'm'};

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
	 * Creates a new Skill
	 *
	 * @param type    The Skill type
	 * @param members if the skill is members or free
	 */
	Skill(int id, Type type, boolean members) {
		this.id = id;
		this.type = Objects.requireNonNull(type);
		this.members = members;
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

		// Start from the end of the xp table,
		// iterate backwards until the level is reached
		for (int i = table.length - 1; i > 0; i--) {
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