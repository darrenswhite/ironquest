package com.darrenswhite.rs.ironquest.player;

import com.darrenswhite.rs.ironquest.IronQuest;
import com.darrenswhite.rs.ironquest.action.Action;
import com.darrenswhite.rs.ironquest.action.LampAction;
import com.darrenswhite.rs.ironquest.action.QuestAction;
import com.darrenswhite.rs.ironquest.quest.Lamp;
import com.darrenswhite.rs.ironquest.quest.Quest;
import com.darrenswhite.rs.ironquest.quest.RuneMetricsQuest;
import com.darrenswhite.rs.ironquest.quest.requirement.SkillRequirement;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author Darren White
 */
public class Player {

	/**
	 * URL for Hiscores
	 */
	private static final String URL_HISCORES_LITE =
			"http://services.runescape.com/m=hiscore/index_lite.ws?player=";

	/**
	 * URL for RuneMetrics quest data
	 */
	private static final String URL_RUNE_METRICS_QUESTS =
			"https://apps.runescape.com/runemetrics/quests?user=";

	/**
	 * The logger
	 */
	private static final Logger log =
			Logger.getLogger(Player.class.getName());

	/**
	 * The current stat xps
	 */
	private final Map<Skill, Integer> skillXPs = new LinkedHashMap<>();

	/**
	 * A set of completed Quest ids
	 */
	private final Set<Integer> quests = new HashSet<>();

	/**
	 * The name of this Player
	 */
	private final String name;

	/**
	 * Creates a new Player instance
	 *
	 * @param name The player name
	 */
	public Player(String name) {
		this.name = name;
	}

	/**
	 * Add XP to a Skill
	 *
	 * @param s  A skill to add xp to
	 * @param xp The amount of xp to add
	 */
	public void addSkillXP(Skill s, int xp) {
		int newXp = skillXPs.getOrDefault(s, 0) + xp;

		if (newXp >= 0) {
			// Add xp to a skill
			skillXPs.put(s, newXp);
		}
	}

	/**
	 * Mark a Quest as completed and process all rewards
	 *
	 * @param q          The Quest to be completed
	 * @param lampSkills The Skills to be used on Lamps
	 */
	public Set<Action> completeQuest(Quest q, Set<Skill> lampSkills) {
		// Don't complete quests that are already completed
		if (isQuestCompleted(q.getId())) {
			throw new IllegalArgumentException("Quest already completed!");
		}

		Set<Action> actions = new LinkedHashSet<>();

		log.fine("Completing quest: " + q.getId());

		// Add the quest id to the completed list
		quests.add(q.getId());

		// Add skill xp for all skill rewards
		q.getSkillRewards().forEach(this::addSkillXP);

		// Add a new QuestAction
		actions.add(new QuestAction(this, q));

		// Keep track of previous skill choices
		Set<Set<Skill>> previous = new HashSet<>();

		// Add xp for all lamps, if possible
		for (Lamp l : q.getLampRewards()) {
			log.fine("Processing lamp: " + l);

			// Get the best skills to use the lamp on based on the
			// lamps requirements
			Set<Skill> bestSkills = getBestLampSkills(l, previous, lampSkills);

			log.fine("Chosen lamp skills: " + bestSkills);

			// Keep track of previous choices
			previous.add(bestSkills);

			// Add the XP to each Skill
			bestSkills.forEach(s -> addSkillXP(s, l.getValue()));

			// Add a new LampAction
			actions.add(new LampAction(this, q, l, bestSkills));
		}

		return actions;
	}

	/**
	 * Copies this Player object and creates a new Player object
	 *
	 * @return A copy of this Player object
	 */
	public Player copy() {
		// Create a Player with the same name
		Player clone = new Player(name);

		// Copy the Skill XPs
		clone.skillXPs.putAll(skillXPs);

		// Copy the completed Quest ids
		clone.quests.addAll(quests);

		return clone;
	}

	/**
	 * Gets the best set of Skills to use a lamp on.
	 * <p>
	 * If the lamp is exclusive it will ignore any choices that have
	 * previously been chosen (if the previous set contains them).
	 * <p>
	 * The best skill set is chosen by the set with the highest amount
	 * of XP required to complete all remaining quests in the open list.
	 *
	 * @param lamp     The lamp to get the best Skills for
	 * @param previous The previous Skill choices for the Lamp
	 * @param force    The Skills to force use if possible
	 * @return A set of Skills to use a lamp on
	 */
	private Set<Skill> getBestLampSkills(Lamp lamp, Set<Set<Skill>> previous,
	                                     Set<Skill> force) {
		// Ensure player has requirements
		if (!lamp.hasRequirements(this)) {
			throw new IllegalStateException(
					"Unable to use lamp: requirements not met");
		}

		log.fine("Previous choices: " + previous);

		// Create a stream for the lamp requirements
		// Filter the stream based on requirements met
		// Get the skills that meet requirements
		Set<Set<Skill>> choices = lamp.getRequirements().entrySet().stream()
				.filter(e -> e.getKey().stream().filter(s ->
						getLevel(s) < e.getValue()).count() == 0)
				.filter(e -> !lamp.isExclusive() ||
						!previous.contains(e.getKey()))
				.map(Map.Entry::getKey)
				.collect(Collectors.toSet());
		// Forced Skill choices
		Set<Set<Skill>> forceChoices = new LinkedHashSet<>();

		// Iterate all forced skill options
		for (Skill s : force) {
			// Get all choices for this forced skill
			Set<Set<Skill>> validChoice = choices.stream()
					.filter(c -> c.contains(s))
					.collect(Collectors.toSet());

			// Add all the forced choices
			forceChoices.addAll(validChoice);
		}

		log.fine("Skill choices: " + choices);
		log.fine("Force choices: " + forceChoices);

		// Force Skill choice if possible
		if (forceChoices.size() > 0) {
			choices = forceChoices;
		}

		// Get remaining xp requirements
		Map<Skill, Integer> xpReqs = getRemainingXPRequirements();

		// Map the stream to the skills
		// Get the total XP requirements for each set of skill choices
		Map<Set<Skill>, Integer> xpChoicesReqs = choices.stream()
				.collect(Collectors.toMap(s -> s, s -> s.stream()
						.mapToInt(sk -> xpReqs.getOrDefault(sk, 0))
						.sum()));

		// Get the set of skills with the maximum xp requirement
		Optional<Map.Entry<Set<Skill>, Integer>> choice =
				xpChoicesReqs.entrySet().stream()
						.max(Comparator.comparingInt(Map.Entry::getValue));

		// This shouldn't happen
		if (!choice.isPresent()) {
			throw new IllegalStateException(
					"Unable to use lamp: no suitable lamp found");
		}

		return choice.get().getKey();
	}

	/**
	 * Calculate the current combat level. The formula for determining the
	 * combat level is found here:
	 * <p>
	 * http://runescape.wikia.com/wiki/Combat_level
	 *
	 * @return The combat level
	 */
	public double getCombatLevel() {
		// Get combat levels
		int attack = getLevel(Skill.ATTACK);
		int constitution = getLevel(Skill.CONSTITUTION);
		int defence = getLevel(Skill.DEFENCE);
		int magic = getLevel(Skill.MAGIC);
		int prayer = getLevel(Skill.PRAYER);
		int range = getLevel(Skill.RANGED);
		int strength = getLevel(Skill.STRENGTH);
		int summoning = getLevel(Skill.SUMMONING);

		// Combat equation found here:
		// http://runescape.wikia.com/wiki/Combat_level
		double max = Math.max(attack + strength,
				Math.max(2.0 * magic, 2.0 * range));

		max *= 13.0 / 10.0;

		return (max + defence + constitution +
				0.5 * prayer + 0.5 * summoning) / 4.0;
	}

	/**
	 * Gets the level for a Skill
	 *
	 * @param s The Skill to get the level for
	 * @return The Skill level
	 */
	public int getLevel(Skill s) {
		// Get skill level based on skill xp
		return s.getLevelAt(getXP(s));
	}

	/**
	 * Gets all Skill levels
	 *
	 * @return A Map of Skills with levels
	 */
	public Map<Skill, Integer> getLevels() {
		// Map Skill XP to levels adding them to a new LinkedHashMap
		return skillXPs.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey,
						e -> e.getKey().getLevelAt(e.getValue()),
						(u, v) -> {
							throw new IllegalStateException(
									String.format("Duplicate key %s", u));
						}, LinkedHashMap::new));
	}

	/**
	 * Gets the Player name
	 *
	 * @return The name of the Player
	 */
	public Optional<String> getName() {
		return Optional.ofNullable(name);
	}

	/**
	 * Calculates the total number of Quest points
	 *
	 * @return The number of Quest points
	 */
	public int getQuestPoints() {
		// Count quests points for all quests
		return quests.stream()
				.mapToInt(id -> IronQuest.getInstance()
						.getQuest(id)
						.getQuestPoints())
				.sum();
	}

	/**
	 * Get the remaining skill XP requirements to complete all quests
	 *
	 * @return A map of skill xp values to complete remaining quests
	 */
	private Map<Skill, Integer> getRemainingXPRequirements() {
		IronQuest quest = IronQuest.getInstance();
		// Store remaining xp requirements in a Map
		Map<Skill, Integer> remaining = new HashMap<>();
		// Get the maximum requirements for remaining quests
		Set<SkillRequirement> maxRequirements = quest.getMaxRequirements(
				quest.getOpen());

		// Get the remaining xp requirements for each Skill
		maxRequirements.forEach(r -> {
			Skill s = r.getSkill();
			int lvl = r.getLevel();

			remaining.put(s, s.getXPAt(lvl) - getXP(s));
		});

		return remaining;
	}

	/**
	 * Gets the total skill level
	 *
	 * @return The total skill level
	 */
	public int getTotalLevel() {
		return getLevels().values().stream()
				.mapToInt(Integer::intValue)
				.sum();
	}

	/**
	 * Gets the XP for a Skill
	 *
	 * @param s The Skill to get the XP for
	 * @return The Skill XP
	 */
	public int getXP(Skill s) {
		// Get current xp for skill
		return skillXPs.get(s);
	}

	/**
	 * Gets the Skill XPs
	 *
	 * @return The Skill XPs
	 */
	public Map<Skill, Integer> getXPs() {
		return Collections.unmodifiableMap(skillXPs);
	}

	/**
	 * Checks if a Quest has been completed using the Quest id
	 *
	 * @param id The id for a Quest
	 * @return If a Quest has been completed or not
	 */
	public boolean isQuestCompleted(int id) {
		// Check if the id is in the completed set
		return quests.contains(id);
	}

	/**
	 * Initializes this Player, retrieving XP from hiscores and Quest's
	 * from RuneMetrics
	 */
	public void load() {
		// All skills start at zero xp
		for (Skill s : Skill.values()) {
			skillXPs.put(s, 0);
		}

		// Constitution starts at level 10
		addSkillXP(Skill.CONSTITUTION, Skill.XP_TABLE[10]);

		if (name != null && !name.trim().isEmpty()) {
			// Load skill xps from hiscores
			loadHiscores();
			// Load completed quests from RuneMetrics
			loadQuests();
		}
	}

	/**
	 * Loads the Player's skill XP from the RuneScape Hiscores
	 */
	private void loadHiscores() {
		// CSV format
		CSVFormat format = CSVFormat.DEFAULT.withDelimiter(',');

		String url;

		try {
			// Encode the name into the URL
			url = URL_HISCORES_LITE +
					URLEncoder.encode(name, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.log(Level.SEVERE, "Unsupported encoding: ", e);
			return;
		}

		// Open a new InputStream for the URL
		try (InputStream in = new URL(url).openStream()) {
			// Parse the CSV data
			CSVParser parser = format.parse(new InputStreamReader(in));
			List<CSVRecord> records = parser.getRecords();

			// Parse the Skill XP values
			for (int i = 1; i < Skill.values().length + 1; i++) {
				Optional<Skill> skill = Skill.tryGet(i);
				CSVRecord r = records.get(i);

				skill.ifPresent(s -> skillXPs.put(s,
						(int) Math.max(0, Double.parseDouble(r.get(2)))));
			}
		} catch (IOException e) {
			log.log(Level.SEVERE,
					"Unable to parse Hiscores CSV: ", e);
		}
	}

	/**
	 * Loads the Player's Quest data from RuneMetrics
	 */
	private void loadQuests() {
		// Quest data is in JSON
		Gson gson = new Gson();
		String url;

		try {
			// Encode name into the URL
			url = URL_RUNE_METRICS_QUESTS +
					URLEncoder.encode(name, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.log(Level.SEVERE, "Unsupported encoding: ", e);
			return;
		}

		// Open a new InputStream for the URL
		try (InputStream in = new URL(url).openStream()) {
			// Parse the JSON data
			JsonParser parser = new JsonParser();
			JsonObject jsonObject =
					parser.parse(new InputStreamReader(in)).getAsJsonObject();
			JsonArray questsArray =
					jsonObject.getAsJsonArray("quests");

			// Parse the Quests
			LinkedHashSet<RuneMetricsQuest> rmQuests =
					gson.fromJson(questsArray,
							new TypeToken<LinkedHashSet<RuneMetricsQuest>>() {
							}.getType());

			IronQuest instance = IronQuest.getInstance();

			// Add all completed quests to the completed quest set
			for (RuneMetricsQuest rmq : rmQuests) {
				try {
					if (rmq.getStatus() == RuneMetricsQuest.Status.COMPLETED) {
						quests.add(instance.getQuest(rmq.getTitle()).getId());
					}
				} catch (IllegalArgumentException e) {
					log.warning("Unable to find quest: " + rmq);
				}
			}
		} catch (IOException e) {
			log.log(Level.SEVERE,
					"Unable to parse RuneMetrics JSON: ", e);
		}
	}

	/**
	 * Resets this Player
	 */
	public void reset() {
		// Clear completed quests
		quests.clear();
		// Load data
		load();
	}

	@Override
	public String toString() {
		return "Player{" +
				"name='" + name + '\'' +
				'}';
	}
}