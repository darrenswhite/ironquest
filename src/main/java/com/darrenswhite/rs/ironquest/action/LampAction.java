package com.darrenswhite.rs.ironquest.action;

import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.Skill;
import com.darrenswhite.rs.ironquest.quest.Lamp;
import com.darrenswhite.rs.ironquest.quest.Quest;

import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

/**
 * @author Darren White
 */
public class LampAction extends Action {

    /**
     * The Quest that the Lamp belongs to
     */
    private final Quest quest;

    /**
     * The Lamp object
     */
    private final Lamp lamp;

    /**
     * The Skills chosen for the Lamp
     */
    private final Set<Skill> skills;

    /**
     * Creates a new LampAction
     *
     * @param player The Player
     * @param quest  The Quest which the Lamp belongs to
     * @param lamp   The Lamp
     * @param skills The chosen set of Skills
     */
    public LampAction(Player player, Quest quest, Lamp lamp,
                      Set<Skill> skills) {
        super(player);
        this.quest = Objects.requireNonNull(quest);
        this.lamp = Objects.requireNonNull(lamp);
        this.skills = Objects.requireNonNull(skills);
    }

    /**
     * Gets the Lamp
     *
     * @return A Lamp
     */
    public Lamp getLamp() {
        return lamp;
    }

    @Override
    public String getMessage() {
        StringBuilder message = new StringBuilder();
        String xp = Skill.formatXP(lamp.getValue());

        message.append("Use ").append(xp).append(" xp lamp on ");

        Iterator<Skill> it = skills.iterator();

        // Add the first skill
        if (it.hasNext()) {
            message.append(it.next());
        }

        // Append remaining skills separated by
        // a comma (or `and` for the last Skill)
        while (it.hasNext()) {
            Skill s = it.next();

            if (it.hasNext()) {
                message.append(", ");
            } else {
                message.append(" and ");
            }

            message.append(s);
        }

        return message.toString();
    }

    /**
     * Gets the Quest which the Lamp belongs to
     *
     * @return A Quest
     */
    public Quest getQuest() {
        return quest;
    }

    /**
     * Gets the Skills chosen for the Lamp
     *
     * @return A Set of Skills
     */
    public Set<Skill> getSkills() {
        return skills;
    }
}