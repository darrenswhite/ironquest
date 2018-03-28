package com.darrenswhite.rs.ironquest.quest;

import com.darrenswhite.rs.ironquest.player.Skill;
import com.darrenswhite.rs.ironquest.quest.requirement.*;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.*;
import java.util.logging.Logger;

/**
 * @author Darren White
 */
public class QuestDeserializer implements JsonDeserializer<Quest> {

	/**
	 * The logger
	 */
	private static final Logger log =
			Logger.getLogger(QuestDeserializer.class.getName());

	/**
	 * The id key
	 */
	private static final String KEY_ID = "id";

	/**
	 * The title key
	 */
	private static final String KEY_TITLE = "title";

	/**
	 * The display name key
	 */
	private static final String KEY_DISPLAY_NAME = "name";

	/**
	 * The requirements key
	 */
	private static final String KEY_REQUIREMENTS = "requirements";

	/**
	 * The ironman requirements key
	 */
	private static final String KEY_REQUIREMENTS_IRONMAN = "ironman";

	/**
	 * The recommended requirements key
	 */
	private static final String KEY_REQUIREMENTS_RECOMMENDED = "recommended";

	/**
	 * The combat level requirements key
	 */
	private static final String KEY_REQUIREMENTS_COMBAT = "COMBAT";

	/**
	 * The quest points requirements key
	 */
	private static final String KEY_REQUIREMENTS_QP = "qp";

	/**
	 * The quest requirements key
	 */
	private static final String KEY_REQUIREMENTS_QUESTS = "quests";

	/**
	 * The rewards key
	 */
	private static final String KEY_REWARDS = "rewards";

	/**
	 * The reward lamps key
	 */
	private static final String KEY_REWARDS_LAMPS = "lamps";

	/**
	 * The quest points key
	 */
	private static final String KEY_REWARDS_QP = "qp";

	@Override
	public Quest deserialize(JsonElement json, Type typeOfT,
	                         JsonDeserializationContext context)
			throws JsonParseException {
		log.fine("Deserializing JSON: " + json);

		// Quest are objects
		JsonObject quest = json.getAsJsonObject();

		// Get the id
		int id = quest.get(KEY_ID).getAsInt();
		// Get the title
		String title = quest.get(KEY_TITLE).getAsString();
		// Get the requirements object
		JsonObject requirementsObject =
				quest.getAsJsonObject(KEY_REQUIREMENTS);
		// Get the requirements object
		JsonObject ironmanRequirementsObject =
				quest.getAsJsonObject(KEY_REQUIREMENTS_IRONMAN);
		// Get the requirements object
		JsonObject recommendedRequirementsObject =
				quest.getAsJsonObject(KEY_REQUIREMENTS_RECOMMENDED);
		// Get the rewards object
		JsonObject rewardsObject = quest.getAsJsonObject(KEY_REWARDS);

		// Display name is the title by default
		String displayName = title;

		// Get the display name
		if (quest.has(KEY_DISPLAY_NAME)) {
			displayName = quest.get(KEY_DISPLAY_NAME).getAsString();
		}

		Set<Requirement> requirements = new HashSet<>();
		int questPoints = 0;
		Map<Skill, Integer> skillRewards = new HashMap<>();
		Set<Lamp> lampRewards = new HashSet<>();

		// Requirements may not be present
		if (requirementsObject != null) {
			requirements.addAll(parseRequirements(requirementsObject));
		}

		if (ironmanRequirementsObject != null) {
			Set<Requirement> ironman =
					parseRequirements(ironmanRequirementsObject);

			ironman.forEach(r -> r.setIronman(true));

			requirements.addAll(ironman);
		}

		if (recommendedRequirementsObject != null) {
			Set<Requirement> recommended =
					parseRequirements(recommendedRequirementsObject);

			recommended.forEach(r -> r.setRecommended(true));

			requirements.addAll(recommended);
		}

		// Parse all rewards
		for (Map.Entry<String, JsonElement> e : rewardsObject.entrySet()) {
			String key = e.getKey();
			Object value = e.getValue();

			// Get the Skill if any
			Optional<Skill> skill = Skill.tryGet(key);

			if (skill.isPresent()) {
				// Add the Skill XP reward
				skillRewards.put(skill.get(), e.getValue()
						.getAsInt());
			} else if (key.equalsIgnoreCase(KEY_REWARDS_LAMPS)) {
				// Lamps are arrays
				JsonArray lamps = (JsonArray) value;

				// Add all Lamps
				lamps.forEach(l -> lampRewards.addAll(Lamp.fromJson(l)));
			} else if (key.equals(KEY_REWARDS_QP)) {
				// Set quest point reward
				questPoints = e.getValue().getAsInt();
			}
		}

		// Create the new Quest
		Quest q = new Quest(id, title, displayName, requirements, questPoints,
				skillRewards, lampRewards);

		log.fine("Deserialized quest: " + q.toString());

		return q;
	}

	private Set<Requirement> parseRequirements(JsonObject requirementsObject) {
		Set<Requirement> requirements = new HashSet<>();

		// Parse all requirements
		for (Map.Entry<String, JsonElement> e :
				requirementsObject.entrySet()) {
			String key = e.getKey();
			JsonElement value = e.getValue();

			// Get the Skill if any
			Optional<Skill> skill = Skill.tryGet(key);

			if (skill.isPresent()) {
				// Add the Skill level requirement
				requirements.add(new SkillRequirement(skill.get(),
						value.getAsInt()));
			} else if (key.equalsIgnoreCase(KEY_REQUIREMENTS_QUESTS)) {
				// Quest requirements are array of ids
				JsonArray questIds = (JsonArray) value;

				// Add the quest ids
				questIds.forEach(q ->
						requirements.add(
								new QuestRequirement(q.getAsInt())));
			} else {
				// Other requirements
				switch (key) {
					// Quest points requirement
					case KEY_REQUIREMENTS_QP:
						requirements.add(new QuestPointsRequirement(value.getAsInt()));
						break;
					// Combat level requirement
					case KEY_REQUIREMENTS_COMBAT:
						requirements.add(new CombatRequirement(value.getAsInt()));
						break;
					// Invalid requirement
					default:
						throw new IllegalArgumentException(
								"Unknown requirement: " + key);
				}
			}
		}

		return requirements;
	}
}