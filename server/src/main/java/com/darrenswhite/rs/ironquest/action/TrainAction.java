package com.darrenswhite.rs.ironquest.action;

import com.darrenswhite.rs.ironquest.dto.TrainActionDTO;
import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.Skill;
import java.text.MessageFormat;

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

  /**
   * Returns the {@link Skill} to be trained.
   *
   * @return the skill to train
   */
  public Skill getSkill() {
    return skill;
  }

  /**
   * Returns the starting XP that the {@link Player} has for the {@link Skill}.
   *
   * @return the starting xp
   */
  public double getStartXp() {
    return startXp;
  }

  /**
   * Returns the target XP that the {@link Player} much reach for the {@link Skill}.
   *
   * @return the target xp
   */
  public double getEndXp() {
    return endXp;
  }

  /**
   * Returns the amount of xp needed to reach the target xp.
   *
   * @return the amount of xp needed
   */
  public double getDiffXp() {
    return endXp - startXp;
  }

  /**
   * {@inheritDoc}
   *
   * Format is:
   *
   * Train `skill` to level `level`, requiring `xp` xp
   */
  @Override
  public String getMessage() {
    return MessageFormat
        .format("Train {0} to level {1}, requiring {2} xp", skill, skill.getLevelAt(endXp),
            Skill.formatXp(getDiffXp()));
  }

  /**
   * {@inheritDoc}
   *
   * @return true
   */
  @Override
  public boolean meetsRequirements(Player player) {
    return true;
  }

  /**
   * {@inheritDoc}
   *
   * Adds the xp needed to reach the end xp.
   *
   * @see TrainAction#getDiffXp()
   */
  @Override
  public void process(Player player) {
    player.addSkillXP(skill, getDiffXp());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TrainActionDTO createDTO() {
    return new TrainActionDTO.Builder().withPlayer(getPlayer().createDTO()).withFuture(isFuture())
        .withMessage(getMessage()).build();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TrainAction copyForPlayer(Player player) {
    return new TrainAction(player, getSkill(), getStartXp(), getEndXp());
  }
}
