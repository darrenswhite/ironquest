package com.darrenswhite.rs.ironquest;

import com.darrenswhite.rs.ironquest.action.Action;
import com.darrenswhite.rs.ironquest.action.TrainAction;
import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.Skill;
import com.darrenswhite.rs.ironquest.quest.Quest;
import com.darrenswhite.rs.ironquest.quest.QuestDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.logging.Logger;

/**
 * @author Darren White
 */
public class IronQuest implements Runnable {

	/**
	 * The logger
	 */
	private static final Logger log =
			Logger.getLogger(IronQuest.class.getName());

	/**
	 * The JSON file containing quest data
	 */
	private static final String FILE_QUESTS_JSON =
			"resources/json/quests.json";

	/**
	 * The IronQuest instance
	 */
	private static IronQuest instance;

	/**
	 * The list of Quest's to be completed
	 */
	private final List<Quest> open = new ArrayList<>();

	/**
	 * The set of all Quest's
	 */
	private final Set<Quest> quests;

	/**
	 * The current Steps produced by this instance
	 * Use FX collection
	 */
	private final ObservableList<Action> actions =
			FXCollections.observableList(new LinkedList<>());

	/**
	 * The current player instance
	 */
	private Player player;

	/**
	 * A Set of Skills to use lamps on. This is empty by default
	 * and Lamp Skills will be chosen by an algorithm. Use this
	 * to force a Set of Skills to use for every Lamp.
	 */
	private Set<Skill> lampSkills = new LinkedHashSet<>();

	/**
	 * Creates a new IronQuest instance with the given set of Quest's
	 *
	 * @param quests The set of Quest's to use
	 */
	private IronQuest(Set<Quest> quests) {
		this.quests = Objects.requireNonNull(quests);
	}

	/**
	 * Adds a new TrainAction for a skill to a required level
	 *
	 * @param s      The Skill to train
	 * @param reqLvl The required level
	 */
	private void addTrainAction(Skill s, Integer reqLvl) {
		int reqXP = s.getXPAt(reqLvl);
		int currXp = player.getXP(s);
		int diffXp = reqXP - currXp;

		// Add Skill XP
		player.addSkillXP(s, diffXp);

		// Create a new TrainAction
		Action action = new TrainAction(player, s, currXp, reqXP);

		log.info("Adding action: " + action.getMessage());

		// Add the new action
		// Run on FX thread
		if (Platform.isFxApplicationThread()) {
			actions.addAll(action);
		} else {
			Platform.runLater(() -> actions.add(action));
		}
	}

	/**
	 * Gets the Actions list
	 *
	 * @return A list of Actions
	 */
	public ObservableList<Action> getActions() {
		return FXCollections.unmodifiableObservableList(actions);
	}

	/**
	 * Gets the 'best' Quest to be completed next if any. If no 'best' Quest
	 * if available then the closest Quest will be returned.
	 * <p>
	 * The best Quest is determined by the Player having all requirements
	 * for a Quest (and all Lamp requirements) and then getting the maximum
	 * Quest compared by Quest priority.
	 * <p>
	 * The closest quest is determined by the Player having all requirements
	 * (including all Lamp requirements but excluding skill requirements) and
	 * then getting the minimum Quest compared by total remaining skill
	 * requirements.
	 *
	 * @return The best Quest to be completed
	 * @see Quest#getPriority(Player)
	 */
	private Quest getBestQuest() {
		// Create a new stream from the open list
		// Filter the stream to contain Quest's which the player has all
		// requirements for and all Lamp requirements
		// Get the maximum Quest by comparing priority
		Optional<Quest> best = open.stream()
				.filter(q -> q.hasRequirements(player) &&
						q.getLampRewards().stream()
								.filter(l -> !l.hasRequirements(player))
								.count() == 0)
				.max(Comparator.comparingInt(q -> q.getPriority(player)));

		// Return the best quest if there is one
		if (best.isPresent()) {
			return best.get();
		}

		// Create a new stream from the open list
		// Filter the stream to contain Quest's which the player has all
		// requirements (including Lamp requirements but excluding skills)
		// Get the minimum Quest by comparing total remaining
		// skill requirements
		Optional<Quest> closest = open.stream()
				.filter(q -> q.hasOtherRequirements(player) &&
						q.hasQuestRequirements(player) &&
						q.getLampRewards().stream()
								.filter(l -> !l.hasRequirements(player))
								.count() == 0)
				.min(Comparator.comparingInt(q ->
						q.getRemainingSkillRequirements(player)
								.values().stream()
								.mapToInt(Integer::intValue)
								.sum()));

		// Quest list must be empty or requirements are invalid
		if (!closest.isPresent()) {
			throw new IllegalStateException("Unable to find best quest");
		}

		// Get the closest quest
		Quest closestQuest = closest.get();

		// Notify user which skills have to be trained
		closestQuest.getRemainingSkillRequirements(player)
				.forEach(this::addTrainAction);

		return closestQuest;
	}

	/**
	 * Get the current instance, creating a new one if there is no instance
	 *
	 * @return The current instance
	 */
	public static IronQuest getInstance() {
		// Get the instance, creating a new one if it doesn't exist yet
		if (instance == null) {
			// Create a gson builder to read quest data
			GsonBuilder gsonBuilder = new GsonBuilder();

			// Used to deserialize quests
			gsonBuilder.registerTypeAdapter(Quest.class,
					new QuestDeserializer());

			// Create the gson object
			Gson gson = gsonBuilder.create();

			log.info("Trying to read quests from file: " +
					FILE_QUESTS_JSON);

			// Read the quests from file
			try (InputStreamReader in = new InputStreamReader(
					MainFX.getResource(FILE_QUESTS_JSON).openStream())) {
				// Store the json in a set of Quests
				log.info("Deserializing JSON...");
				Set<Quest> quests = gson.fromJson(in,
						new TypeToken<LinkedHashSet<Quest>>() {
						}.getType());

				// Create a new instance
				instance = new IronQuest(quests);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return instance;
	}

	/**
	 * Gets the Skills to be used on Lamps
	 *
	 * @return A Set of Skills
	 */
	public Set<Skill> getLampSkills() {
		return lampSkills;
	}

	/**
	 * Gets the maximum Skill requirements for the given Quests
	 *
	 * @param quests A collection of Quests
	 * @return The Skill level requirements
	 */
	public Map<Skill, Integer> getMaxRequirements(Collection<Quest> quests) {
		Map<Skill, Integer> requirements = new HashMap<>();

		for (Quest q : quests) {
			// Create a stream for the skill requirements
			// Filter by maximum requirements
			// Add the higher requirements
			q.getSkillRequirements().entrySet().stream()
					.filter(e -> e.getValue() >
							requirements.getOrDefault(e.getKey(), 0))
					.forEach(e -> requirements.put(e.getKey(), e.getValue()));
		}

		return requirements;
	}

	/**
	 * The current list of Quest's to be completed
	 *
	 * @return A list of Quest's
	 */
	public List<Quest> getOpen() {
		return Collections.unmodifiableList(open);
	}

	/**
	 * Gets the current player instance
	 *
	 * @return The player instance
	 */
	public Optional<Player> getPlayer() {
		return Optional.ofNullable(player);
	}

	/**
	 * Gets a Quest using its id
	 *
	 * @param id The Quest id
	 * @return The Quest with the given id
	 */
	public Quest getQuest(int id) {
		// Filter quests by the id
		Optional<Quest> quest = quests.stream()
				.filter(q -> q.getId() == id)
				.findAny();

		// Quest doesn't exist
		if (!quest.isPresent()) {
			throw new IllegalArgumentException(
					"Unable to find quest with id: " + id);
		}

		return quest.get();
	}

	/**
	 * Gets a Quest using its title
	 *
	 * @param title The Quest title
	 * @return The Quest with the given title
	 */
	public Quest getQuest(String title) {
		// Filter quests by the title
		Optional<Quest> quest = quests.stream()
				.filter(q ->
						q.getTitle()
								.equalsIgnoreCase(title))
				.findAny();

		// Quest doesn't exist
		if (!quest.isPresent()) {
			throw new IllegalArgumentException(
					"Unable to find quest with name: " + title);
		}

		return quest.get();
	}

	/**
	 * Gets the set of all Quest's
	 *
	 * @return A set of Quest's
	 */
	public Set<Quest> getQuests() {
		return Collections.unmodifiableSet(quests);
	}

	/**
	 * Process the JSON Quest data and create Actions
	 */
	@Override
	public void run() {
		// Check player instance
		if (player != null) {
			log.info("Using player profile: " + player);

			// Reset the player from previous runs
			player.reset();
		} else {
			log.warning("No player profile set, using default");

			// Use a player with no name
			setPlayer(null);

			// Load the default player
			player.load();
		}

		log.info("Generating actions...");

		// Clear previous actions
		Platform.runLater(actions::clear);
		// Clear any previous items from the list
		open.clear();
		// Add all quests to the open list
		open.addAll(quests);

		// Remove all quests the player has already completed
		open.removeIf(q -> player.isQuestCompleted(q.getId()));

		log.info("Force lamp skills: " + lampSkills);

		// Loop until all quests completed
		while (!open.isEmpty()) {
			// Get the best quest
			Quest best = getBestQuest();

			log.info("Best: " + best);

			// Complete the quest
			Set<Action> newActions = player.completeQuest(best, lampSkills);

			newActions.forEach(a -> log.info("Adding action: " +
					a.getMessage()));

			// Add all the actions
			// Run on FX thread
			if (Platform.isFxApplicationThread()) {
				actions.addAll(newActions);
			} else {
				Platform.runLater(() -> actions.addAll(newActions));
			}

			// Remove it from the list
			open.remove(best);
		}
	}

	/**
	 * A Set of Skills to use lamps on. This is empty by default
	 * and Lamp Skills will be chosen by an algorithm. Use this
	 * to force a Set of Skills to use for every Lamp.
	 *
	 * @param lampSkills A Set of Skills to use Lamp's on
	 */
	public void setLampSkills(Set<Skill> lampSkills) {
		this.lampSkills = lampSkills;
	}

	/**
	 * Creates a new Player with the given name
	 *
	 * @param name The Player name
	 */
	public void setPlayer(String name) {
		player = new Player(name);
	}
}