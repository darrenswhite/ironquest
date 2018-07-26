package com.darrenswhite.rs.ironquest;

import com.darrenswhite.rs.ironquest.action.Action;
import com.darrenswhite.rs.ironquest.action.LampAction;
import com.darrenswhite.rs.ironquest.action.TrainAction;
import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.Skill;
import com.darrenswhite.rs.ironquest.quest.Lamp;
import com.darrenswhite.rs.ironquest.quest.Quest;
import com.darrenswhite.rs.ironquest.quest.QuestDeserializer;
import com.darrenswhite.rs.ironquest.quest.requirement.QuestRequirement;
import com.darrenswhite.rs.ironquest.quest.requirement.SkillRequirement;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
    private static final String FILE_QUESTS_JSON = "/json/quests.json";

    /**
     * The IronQuest instance
     */
    private static IronQuest instance;

    /**
     * The list of Quest's to be completed
     */
    private final List<Quest> open = new ArrayList<>();

    /**
     * The list of lamps that should be used when requirements are met
     */
    private final Map<Lamp, Quest> futureLamps = new HashMap<>();

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
     * Ironman mode
     */
    private boolean ironman;

    /**
     * Recommended mode
     */
    private boolean recommended;

    /**
     * Show members quests
     */
    private boolean members;

    /**
     * Show free quests
     */
    private boolean free;

    /**
     * Creates a new IronQuest instance with the given set of Quest's
     *
     * @param quests The set of Quest's to use
     */
    private IronQuest(Set<Quest> quests) {
        this.quests = Objects.requireNonNull(quests);
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
                log.log(Level.SEVERE, "Unable to deserialize JSON: ", e);
            }
        }

        return instance;
    }

    public static void amalgamateRequirements(Set<SkillRequirement> remaining,
                                              Set<SkillRequirement> qRemaining) {
        // Add them to the remaining map
        // if they are larger or not present
        qRemaining.forEach(qr -> {
            Optional<SkillRequirement> req = remaining.stream()
                    .filter(r -> r.getSkill() == qr.getSkill())
                    .findAny();

            if (req.isPresent()) {
                if (qr.getLevel() > req.get().getLevel()) {
                    remaining.remove(req.get());
                    remaining.add(qr);
                }
            } else {
                remaining.add(qr);
            }
        });
    }

    /**
     * Adds a new TrainAction for a skill to a required level
     *
     * @param req The SkillRequirement
     */
    private void addTrainAction(SkillRequirement req) {
        Skill s = req.getSkill();
        int reqXP = s.getXPAt(req.getLevel());
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
     * @see Quest#getPriority(Player, boolean, boolean)
     */
    private Quest getBestQuest() {
        // Create a new stream from the open list
        // Filter the stream to contain Quest's which the player has all
        // requirements for and all Lamp requirements
        // Get the maximum Quest by comparing priority
        Optional<Quest> best = open.stream()
                .filter(q -> q.hasRequirements(player, ironman, recommended))
                .max(Comparator.comparingInt(q -> q.getPriority(player, ironman, recommended)));

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
                .filter(q -> q.hasOtherRequirements(player, ironman, recommended) &&
                        q.hasQuestRequirements(player, ironman, recommended))
                .min(Comparator.comparingInt(q ->
                        q.getRemainingSkillRequirements(player, ironman, recommended)
                                .stream()
                                .mapToInt(SkillRequirement::getLevel)
                                .sum()));

        // Quest list must be empty or requirements are invalid
        if (!closest.isPresent()) {
            throw new IllegalStateException("Unable to find best quest: " + open);
        }

        // Get the closest quest
        Quest closestQuest = closest.get();

        // Notify user which skills have to be trained
        closestQuest.getRemainingSkillRequirements(player, ironman, recommended)
                .forEach(this::addTrainAction);

        return closestQuest;
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
     * Gets the maximum Skill requirements for the given Quests
     *
     * @param quests A collection of Quests
     * @return The Skill level requirements
     */
    public Set<SkillRequirement> getMaxRequirements(Collection<Quest> quests) {
        Set<SkillRequirement> requirements = new HashSet<>();

        for (Quest q : quests) {
            // Add the Quests' SkillRequirements
            amalgamateRequirements(requirements, q.getSkillRequirements());
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
     * Creates a new Player with the given name
     *
     * @param name The Player name
     */
    public void setPlayer(String name) {
        player = new Player(name);
    }

    /**
     * Gets the Path to store the properties at
     *
     * @return A Path to store the properties in
     */
    private Path getPropertiesPath() {
        String home = System.getProperty("user.home");
        return Paths.get(home, ".ironquest");
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
                .filter(q -> q.getTitle().equalsIgnoreCase(title) ||
                        q.getDisplayName().equalsIgnoreCase(title))
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
     * Test if currently in ironman mode
     *
     * @return true if ironman mode is enabled
     */
    public boolean isIronman() {
        return ironman;
    }

    /**
     * Set ironman mode
     *
     * @param ironman true to enable ironman mode
     */
    public void setIronman(boolean ironman) {
        this.ironman = ironman;
    }

    /**
     * Test if currently in recommended mode
     *
     * @return true if recommended mode is enabled
     */
    public boolean isRecommended() {
        return recommended;
    }

    /**
     * Set recommended mode
     *
     * @param recommended true to enable recommended mode
     */
    public void setRecommended(boolean recommended) {
        this.recommended = recommended;
    }

    /**
     * Test if members quests are being used
     *
     * @return true if members quests are being used
     */
    public boolean isMembers() {
        return members;
    }

    /**
     * Set to use members quests
     *
     * @param members true to use members quests
     */
    public void setMembers(boolean members) {
        this.members = members;
    }

    /**
     * Test if free quests are being used
     *
     * @return true if free quests are being used
     */
    public boolean isFree() {
        return free;
    }

    /**
     * Set to use free quests
     *
     * @param free true to use free quests
     */
    public void setFree(boolean free) {
        this.free = free;
    }

    /**
     * Loads the previously saved state from
     * a properties file
     */
    void load() {
        Properties prop = new Properties();

        log.info("Loading settings...");

        Path path = getPropertiesPath();

        if (!Files.exists(path)) {
            return;
        }

        // Load the properties file
        try (InputStream in = Files.newInputStream(path)) {
            prop.load(in);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Unable to load properties: ", e);
            return;
        }

        // Load the property name
        setPlayer(prop.getProperty("name"));

        // Load ironman mode
        setIronman(Boolean.parseBoolean(prop.getProperty("ironman",
                "false")));

        // Load recommended mode
        setRecommended(Boolean.parseBoolean(prop.getProperty("recommended",
                "false")));

        // Load members mode
        setMembers(Boolean.parseBoolean(prop.getProperty("members",
                "true")));

        // Load free mode
        setFree(Boolean.parseBoolean(prop.getProperty("free",
                "true")));

        // Get the lamp skills comma delimited string
        String lampSkillsStr = prop.getProperty("lampSkills", "");
        // Split the string by commas
        String[] skillsArr = lampSkillsStr.split(",");
        Set<Skill> lampSkills = new LinkedHashSet<>();

        // Parse the skill strings array
        for (String s : skillsArr) {
            Optional<Skill> skill = Skill.tryGet(s);

            skill.ifPresent(lampSkills::add);
        }

        // Set the lamp skills set
        setLampSkills(lampSkills);
    }

    /**
     * Process the JSON Quest data and create Actions
     */
    @Override
    public void run() {
        // Check player instance
        if (player != null && player.getName().isPresent()) {
            log.info("Using player profile: " + player.getName().get());

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

        // Clear previous lamp skills
        quests.forEach(q -> q.setPreviousLampSkills(new HashSet<>()));

        // Clear previous actions
        Platform.runLater(actions::clear);
        // Clear any previous items from the list
        open.clear();
        // Add all quests to the open list
        open.addAll(quests);

        // Remove all quests the player has already completed
        open.removeIf(q -> player.isQuestCompleted(q.getId()));

        // Filter member/free
        if (!members) {
            open.removeIf(Quest::isMembers);
        }
        if (!free) {
            open.removeIf(q -> {
                if (q.isMembers()) {
                    return false;
                }
                for (Quest quest : open) {
                    if (quest.isMembers()) {
                        for (QuestRequirement qr : quest.getQuestRequirements()) {
                            if (qr.getId() == q.getId()) {
                                return false;
                            }
                        }
                    }
                }
                return true;
            });
        }

        // Process placeholder quest with ids less than 0
        // For example, Unstable Foundations
        for (Iterator<Quest> it = open.iterator(); it.hasNext(); ) {
            Quest q = it.next();
            if (q.getId() < 0) {
                log.info("Processing placeholder quest: " + q);
                player.completeQuest(q, lampSkills);
                it.remove();
            }
        }

        log.info("Force lamp skills: " + lampSkills);

        // Loop until all quests completed
        while (!open.isEmpty()) {
            // Get the best quest
            Quest best = getBestQuest();

            log.info("Best: " + best);

            // Complete the quest
            Set<Action> newActions = player.completeQuest(best, lampSkills);

            // Keep track of future lamps
            for (Iterator<Action> it = newActions.iterator(); it.hasNext(); ) {
                Action a = it.next();
                if (a instanceof LampAction) {
                    LampAction la = (LampAction) a;
                    if (la.isFuture()) {
                        futureLamps.put(la.getLamp(), la.getQuest());
                        it.remove();
                    }
                }
            }

            // Check future lamps
            for (Iterator<Map.Entry<Lamp, Quest>> it = futureLamps.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<Lamp, Quest> entry = it.next();
                Lamp lamp = entry.getKey();
                Quest quest = entry.getValue();
                if (lamp.hasRequirements(player)) {
                    newActions.add(player.useQuestLamp(quest, lamp, lampSkills));
                    it.remove();
                }
            }

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

        // Show all future lamps
        for (Map.Entry<Lamp, Quest> entry : futureLamps.entrySet()) {
            LampAction action = player.useQuestLamp(entry.getValue(), entry.getKey(), lampSkills);
            log.info("Adding action: " + action.getMessage());
            if (Platform.isFxApplicationThread()) {
                actions.add(action);
            } else {
                Platform.runLater(() -> actions.add(action));
            }
        }

        // Clear any future lamps
        futureLamps.clear();
    }

    /**
     * Saves the state (player name and lamp skill
     * choices) to a properties file
     */
    void save() {
        Properties prop = new Properties();

        log.info("Saving settings...");

        // Store player name
        if (player != null && player.getName().isPresent()) {
            prop.setProperty("name", player.getName().get());
        }

        // Join lamp skills by commas
        String lampSkillsStr = lampSkills.stream()
                .map(Skill::toString)
                .collect(Collectors.joining(","));

        // Store lamp skills set
        if (!lampSkillsStr.isEmpty()) {
            prop.setProperty("lampSkills", lampSkillsStr);
        }

        // Store ironman mode
        prop.setProperty("ironman", Boolean.toString(ironman));

        // Store recommended mode
        prop.setProperty("recommended", Boolean.toString(recommended));

        // Store members mode
        prop.setProperty("members", Boolean.toString(members));

        // Store free mode
        prop.setProperty("free", Boolean.toString(free));

        // Store the properties to file
        try (OutputStream out = Files.newOutputStream(getPropertiesPath())) {
            prop.store(out, "");
        } catch (IOException e) {
            log.log(Level.SEVERE, "Unable to save properties: ", e);
        }
    }
}