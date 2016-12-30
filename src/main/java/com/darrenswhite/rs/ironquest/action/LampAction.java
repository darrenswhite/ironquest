package com.darrenswhite.rs.ironquest.action;

import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.Skill;
import com.darrenswhite.rs.ironquest.quest.Lamp;
import com.darrenswhite.rs.ironquest.quest.Quest;

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
		this.quest = quest;
		this.lamp = lamp;
		this.skills = skills;
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
		return "Use " + Skill.formatXP(lamp.getValue()) + " xp lamp on " +
				skills;
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