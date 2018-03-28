package com.darrenswhite.rs.ironquest.action;

import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.Skill;

import java.util.Objects;

/**
 * @author Darren White
 */
public class TrainAction extends Action {

    /**
     * The Skill to train
     */
    private final Skill skill;

    /**
     * The starting XP for the Skill
     */
    private final int startXP;

    /**
     * The ending XP for the Skill
     */
    private final int endXP;

    /**
     * Creates a new TrainAction
     *
     * @param player  The Player
     * @param skill   The Skill to train
     * @param startXP The starting XP of the Skill
     * @param endXP   The ending XP of the Skill
     */
    public TrainAction(Player player, Skill skill, int startXP, int endXP) {
        super(player);
        this.skill = Objects.requireNonNull(skill);
        this.startXP = startXP;
        this.endXP = endXP;
    }

    /**
     * Gets the ending XP of the Skill
     *
     * @return The end XP
     */
    public int getEndXP() {
        return endXP;
    }

    @Override
    public String getMessage() {
        int endLvl = skill.getLevelAt(endXP);
        String diffXp = Skill.formatXP(endXP - startXP);

        return "Train " + skill + " to level " + endLvl +
                ", requiring " + diffXp + " xp";
    }

    /**
     * Gets the Skill object
     *
     * @return A Skill
     */
    public Skill getSkill() {
        return skill;
    }

    /**
     * Gets the starting XP of the Skill
     *
     * @return The start XP
     */
    public int getStartXP() {
        return startXP;
    }
}