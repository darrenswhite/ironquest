package com.darrenswhite.rs.ironquest.quest;

import com.darrenswhite.rs.ironquest.IronQuest;
import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.Skill;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Darren White
 */
public class Quest {

	/**
	 * The Quest unique id
	 */
	private final int id;

	/**
	 * The Quest title
	 */
	private final String title;

	/**
	 * The name to display for the Quest
	 */
	private final String displayName;

	/**
	 * The skill requirement levels
	 */
	private final Map<Skill, Integer> skillRequirements;

	/**
	 * Quest ids requirements
	 */
	private final Set<Integer> questRequirements;

	/**
	 * Other requirement the Player must meet
	 */
	private final Set<Predicate<Player>> otherRequirements;

	/**
	 * Skill XP rewards
	 */
	private final Map<Skill, Integer> skillRewards;

	/**
	 * Lamp rewards
	 */
	private final Set<Lamp> lampRewards;

	/**
	 * Quest points reward
	 */
	private final int questPoints;

	/**
	 * Creates a new Quest instance
	 *
	 * @param id                The Quest unique id
	 * @param title             The Quest title
	 * @param displayName       The Quest display name
	 * @param skillRequirements The skill requirement levels
	 * @param questRequirements Quest ids requirements
	 * @param otherRequirements Other requirement the Player must meet
	 * @param questPoints       Skill XP rewards
	 * @param skillRewards      Lamp rewards
	 * @param lampRewards       Quest points reward
	 */
	public Quest(int id, String title, String displayName,
	             Map<Skill, Integer> skillRequirements,
	             Set<Integer> questRequirements,
	             Set<Predicate<Player>> otherRequirements,
	             int questPoints, Map<Skill, Integer> skillRewards,
	             Set<Lamp> lampRewards) {
		this.id = id;
		this.title = title;
		this.displayName = displayName;
		this.skillRequirements = skillRequirements;
		this.questRequirements = questRequirements;
		this.otherRequirements = otherRequirements;
		this.questPoints = questPoints;
		this.skillRewards = skillRewards;
		this.lampRewards = lampRewards;
	}

	/**
	 * Gets this Quest display name
	 *
	 * @return The Quest display name
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Gets this Quest unique id
	 *
	 * @return The Quest id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Gets this Quest lamp rewards
	 *
	 * @return A set of Lamp rewards
	 */
	public Set<Lamp> getLampRewards() {
		return Collections.unmodifiableSet(lampRewards);
	}

	/**
	 * Calculates the priority for this Quest based on skill requirements
	 * and rewards
	 *
	 * @param p The Player instance
	 * @return The priority of this Quest
	 */
	public int getPriority(Player p) {
		// Get the total remaining skill requirements
		int reqs = getRemainingSkillRequirements(p).values().stream()
				.mapToInt(Integer::intValue).sum();
		// Get the total rewards and scale down by a factor of 100
		int rwds = (getTotalLampRewards() + getTotalSkillRewards()) / 100;

		// Priority is low requirements and high rewards
		return rwds - reqs;
	}

	/**
	 * Gets the amount of quest points for this Quest
	 *
	 * @return The number of Quest points
	 */
	public int getQuestPoints() {
		return questPoints;
	}

	/**
	 * Gets the remaining skill level requirements for this Quest
	 *
	 * @param p The Player instance
	 * @return The remaining skill level requirements
	 */
	public Map<Skill, Integer> getRemainingSkillRequirements(Player p) {
		// Create a new Stream for the Skill requirements
		Stream<Map.Entry<Skill, Integer>> skillStream =
				skillRequirements.entrySet().stream();

		// Filter by removing requirements already met
		skillStream = skillStream.filter(e ->
				p.getSkillLevel(e.getKey()) < e.getValue());

		// Collect the results in a Map
		Map<Skill, Integer> remaining = skillStream.collect(
				Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		// Create a new Stream for the Quest requirements
		Stream<Integer> questIdsStream = questRequirements.stream();

		// Filter by removing Quest's already completed
		questIdsStream = questIdsStream.filter(id -> !p.isQuestCompleted(id));

		// Map Quest ids to Quest objects
		Stream<Quest> questStream = questIdsStream.map(id ->
				IronQuest.getInstance().getQuest(id));

		// Add all Quest skill requirements
		questStream.forEach(q -> {
			// Get remaining skill requirements
			Map<Skill, Integer> qRemaining =
					q.getRemainingSkillRequirements(p);

			// Add them to the remaining map
			// if they are larger or not present
			qRemaining.forEach((s, lvl) -> {
				if (lvl > remaining.getOrDefault(s, 0)) {
					remaining.put(s, lvl);
				}
			});
		});

		return remaining;
	}

	/**
	 * Gets the Skill level requirements
	 *
	 * @return The Skill level requirements
	 */
	public Map<Skill, Integer> getSkillRequirements() {
		return Collections.unmodifiableMap(skillRequirements);
	}

	/**
	 * Gets the Skill XP rewards
	 *
	 * @return The Skill XP rewards
	 */
	public Map<Skill, Integer> getSkillRewards() {
		return Collections.unmodifiableMap(skillRewards);
	}

	/**
	 * Gets this Quest title
	 *
	 * @return The Quest title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Gets the total Lamp rewards
	 *
	 * @return The total Lamp reward values
	 */
	private int getTotalLampRewards() {
		return lampRewards.stream().mapToInt(Lamp::getValue).sum();
	}

	/**
	 * Gets the total Skill XP rewards
	 *
	 * @return The total Skill XP rewards
	 */
	private int getTotalSkillRewards() {
		return skillRewards.values().stream()
				.mapToInt(Integer::intValue).sum();
	}

	/**
	 * Checks if the Player meets all 'other' requirements
	 *
	 * @param p The Player instance
	 * @return If the Player meets all other requirements
	 */
	public boolean hasOtherRequirements(Player p) {
		return otherRequirements.stream().filter(pr ->
				!pr.test(p)).count() == 0;
	}

	/**
	 * Checks if the Player meets all Quest requirements
	 *
	 * @param p The Player instance
	 * @return If the Player meets all Quest requirements
	 */
	public boolean hasQuestRequirements(Player p) {
		return questRequirements.stream().filter(q ->
				!p.isQuestCompleted(q)).count() == 0;
	}

	/**
	 * Checks if the Player meets all requirements
	 *
	 * @param p The Player instance
	 * @return If the Player meets all requirements
	 */
	public boolean hasRequirements(Player p) {
		return hasSkillRequirements(p) && hasQuestRequirements(p) &&
				hasOtherRequirements(p);
	}

	/**
	 * Checks if the Player meets all Skill requirements
	 *
	 * @param p The Player instance
	 * @return If the Player meets all Skill requirements
	 */
	public boolean hasSkillRequirements(Player p) {
		return skillRequirements.entrySet().stream().filter(e ->
				p.getSkillLevel(e.getKey()) < e.getValue()).count() == 0;
	}

	@Override
	public String toString() {
		return "Quest{" +
				"id=" + id +
				", title='" + title + '\'' +
				", displayName='" + displayName + '\'' +
				", skillRequirements=" + skillRequirements +
				", questRequirements=" + questRequirements +
				", otherRequirements=" + otherRequirements +
				", skillRewards=" + skillRewards +
				", lampRewards=" + lampRewards +
				'}';
	}
}