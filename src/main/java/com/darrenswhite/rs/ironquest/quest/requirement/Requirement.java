package com.darrenswhite.rs.ironquest.quest.requirement;

import com.darrenswhite.rs.ironquest.player.Player;

import java.util.function.Predicate;

/**
 * @author Darren White
 */
public abstract class Requirement {

	private boolean ironman;
	private boolean recommended;

	protected Requirement(boolean ironman, boolean recommended) {
		this.ironman = ironman;
		this.recommended = recommended;
	}

	public static Requirement from(Predicate<Player> predicate) {
		return from(false, false, predicate);
	}

	public static Requirement from(boolean ironman, boolean recommended,
	                               Predicate<Player> predicate) {
		return new Requirement(ironman, recommended) {

			@Override
			public boolean test(Player p) {
				return predicate.test(p);
			}
		};
	}

	public boolean isIronman() {
		return ironman;
	}

	public boolean isRecommended() {
		return recommended;
	}

	public void setIronman(boolean ironman) {
		this.ironman = ironman;
	}

	public void setRecommended(boolean recommended) {
		this.recommended = recommended;
	}

	public boolean test(Player p, boolean ironman, boolean recommended) {
		return (isIronman() && !ironman)
				|| (isRecommended() && !recommended)
				|| test(p);
	}

	public abstract boolean test(Player p);
}