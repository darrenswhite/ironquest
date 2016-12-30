package com.darrenswhite.rs.ironquest.action;

import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.quest.Quest;

/**
 * @author Darren White
 */
public class QuestAction extends Action {

	/**
	 * The Quest object
	 */
	private final Quest quest;

	/**
	 * Creates a new QuestAction
	 *
	 * @param player The Player
	 * @param quest  The Quest
	 */
	public QuestAction(Player player, Quest quest) {
		super(player);
		this.quest = quest;
	}

	/**
	 * Gets the Quest object
	 *
	 * @return A Quest
	 */
	public Quest getQuest() {
		return quest;
	}

	@Override
	public String getMessage() {
		return quest.getDisplayName();
	}
}