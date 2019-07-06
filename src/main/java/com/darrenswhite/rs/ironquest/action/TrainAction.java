package com.darrenswhite.rs.ironquest.action;

import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.Skill;

/**
 * A class representing an {@link Action} to train a {@link Skill} with a start and end xp goal.
 *
 * @author Darren S. White
 */
public class TrainAction extends Action {

  private final Skill skill;
  private final double startXp;
  private final double endXp;

  public TrainAction(Player player, Skill skill, double startXp, double endXp) {
    super(ActionType.TRAIN, player, false);
    this.skill = skill;
    this.startXp = startXp;
    this.endXp = endXp;
  }

  public double getEndXp() {
    return endXp;
  }

  public Skill getSkill() {
    return skill;
  }

  public double getStartXp() {
    return startXp;
  }

  public double getDiffXp() {
    return endXp - startXp;
  }

  @Override
  public String getMessage() {
    return String.format("Train %s to level %d, requiring %s xp", skill, skill.getLevelAt(endXp),
        Skill.formatXp(getDiffXp()));
  }

  @Override
  public boolean meetsRequirements(Player player) {
    return true;
  }

  @Override
  public void process(Player player) {
    player.addSkillXP(skill, getDiffXp());
  }

  @Override
  public TrainAction copyForPlayer(Player player) {
    return new TrainAction(player, getSkill(), getStartXp(), getEndXp());
  }
}
