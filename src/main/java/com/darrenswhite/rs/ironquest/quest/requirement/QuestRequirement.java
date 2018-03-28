package com.darrenswhite.rs.ironquest.quest.requirement;

import com.darrenswhite.rs.ironquest.player.Player;

/**
 * @author Darren White
 */
public class QuestRequirement extends Requirement {

	private final int id;

	public QuestRequirement(int id) {
		this(false, false, id);
	}

	public QuestRequirement(boolean ironman, boolean recommended, int id) {
		super(ironman, recommended);
		this.id = id;
	}

	public int getId() {
		return id;
	}

	@Override
	public boolean isOther() {
		return false;
	}

	@Override
	protected boolean test(Player p) {
		return p.isQuestCompleted(id);
	}
}