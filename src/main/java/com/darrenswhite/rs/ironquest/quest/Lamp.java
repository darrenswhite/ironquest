package com.darrenswhite.rs.ironquest.quest;

import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.Skill;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author Darren White
 */
public class Lamp {

    /**
     * The logger
     */
    private static final Logger log =
            Logger.getLogger(Lamp.class.getName());

    /**
     * The exclusive key
     */
    private static final String KEY_EXCLUSIVE = "exclusive";

    /**
     * The requirements key
     */
    private static final String KEY_REQUIREMENTS = "requirements";

    /**
     * The reward values key
     */
    private static final String KEY_VALUES = "values";

    /**
     * The Lamp case to match any Skill level
     */
    private static final String LAMP_ANY_SKILL = "*";

    /**
     * The Lamp case to match all Skill levels
     */
    private static final String LAMP_ALL_SKILLS = "&";

    /**
     * The Lamp Skill requirement levels
     */
    private final Map<Set<Skill>, Integer> requirements;

    /**
     * The Lamp reward value
     */
    private final int value;
    /**
     * Used to determine if this lamp is exclusive
     */
    private final boolean exclusive;

    /**
     * Create a new Lamp instance
     *
     * @param requirements The skill requirements
     * @param value        The reward value
     * @param exclusive    if the skill requirements are exclusive
     *                     to each other
     */
    public Lamp(Map<Set<Skill>, Integer> requirements, int value,
                boolean exclusive) {
        this.requirements = Objects.requireNonNull(requirements);
        this.value = value;
        this.exclusive = exclusive;
    }

    /**
     * Create's a new Lamp object from the given JsonElement
     *
     * @param json The JsonElement containing Lamp data
     * @return A new Lamp object
     */
    public static Set<Lamp> fromJson(JsonElement json) {
        log.fine("Deserializing JSON: " + json);

        // Get the lamp object
        JsonObject lampObj = json.getAsJsonObject();

        // The requirements object
        JsonObject requirementsObject =
                lampObj.getAsJsonObject(KEY_REQUIREMENTS);
        // The values array
        JsonArray valuesArray = lampObj.getAsJsonArray(KEY_VALUES);

        // The exclusive boolean if exists
        boolean exclusive = lampObj.has(KEY_EXCLUSIVE) &&
                lampObj.get(KEY_EXCLUSIVE).getAsBoolean();
        Map<Set<Skill>, Integer> requirements = new HashMap<>();

        // Not all Lamp's have requirements
        if (requirementsObject != null) {
            // Parse requirements
            for (Map.Entry<String, JsonElement> e :
                    requirementsObject.entrySet()) {
                // Skill enum must be uppercase
                String key = e.getKey().toUpperCase();
                // Requirements are integers (Skill levels)
                int reqLvl = e.getValue().getAsInt();

                switch (key) {
                    // Chosen skill has to meet required level
                    case LAMP_ANY_SKILL:
                        for (Skill s : Skill.values()) {
                            requirements.put(new HashSet<Skill>() {{
                                add(s);
                            }}, reqLvl);
                        }
                        break;
                    // All skills must meet required level
                    case LAMP_ALL_SKILLS:
                        requirements.put(new HashSet<Skill>() {{
                            Collections.addAll(this, Skill.values());
                        }}, reqLvl);
                        break;
                    // Given skills must meet the required level
                    default:
                        String[] keys = key.split(",");
                        Set<Skill> skills = Arrays.stream(keys)
                                .map(Skill::valueOf)
                                .collect(Collectors.toSet());

                        requirements.put(skills, reqLvl);
                }
            }
        } else {
            // Add all Skill with level 1 requirement
            for (Skill s : Skill.values()) {
                requirements.put(new HashSet<Skill>() {{
                    add(s);
                }}, 1);
            }
        }

        Set<Lamp> lamps = new HashSet<>();

        // Create a new Lamp object for each value
        valuesArray.forEach(v ->
                lamps.add(new Lamp(requirements, v.getAsInt(), exclusive)));

        log.fine("Deserialized lamps: " + lamps);

        return lamps;
    }

    /**
     * Gets the Lamp skill requirement levels
     *
     * @return The Lamp requirements
     */
    public Map<Set<Skill>, Integer> getRequirements() {
        return Collections.unmodifiableMap(requirements);
    }

    /**
     * Gets the total of the Lamp reward value
     *
     * @return The reward value
     */
    public int getValue() {
        return value;
    }

    /**
     * Checks if the Player has requirements to use this Lamp
     *
     * @param p The Player to check
     * @return If the Player meets the requirements
     */
    public boolean hasRequirements(Player p) {
        // No requirements
        if (requirements.isEmpty()) {
            return true;
        }

        // Create a stream for the requirements
        // Ensure requirements are met for at least one set of skills
        return requirements.entrySet().stream()
                .anyMatch(e -> e.getKey().stream()
                        .noneMatch(s -> p.getLevel(s) < e.getValue()));
    }

    /**
     * If this Lamp has exclusive Skill rewards
     *
     * @return If this lamp is exclusive
     */
    public boolean isExclusive() {
        return exclusive;
    }

    @Override
    public String toString() {
        return "Lamp{" +
                "requirements=" + requirements +
                ", value=" + value +
                ", exclusive=" + exclusive +
                '}';
    }
}