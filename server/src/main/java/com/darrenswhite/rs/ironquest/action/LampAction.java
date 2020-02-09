package com.darrenswhite.rs.ironquest.action;

import com.darrenswhite.rs.ironquest.dto.LampActionDTO;
import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.QuestEntry;
import com.darrenswhite.rs.ironquest.player.Skill;
import com.darrenswhite.rs.ironquest.quest.reward.LampReward;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A class representing an {@link Action} to use a {@link LampReward} on a set of {@link Skill}s for
 * a given {@link QuestEntry}. The {@link LampReward} maybe used in the future when requirements
 * have been met.
 *
 * @author Darren S. White
 */
public class LampAction extends Action {

  private final QuestEntry questEntry;
  private final LampReward lampReward;
  private final Set<Skill> skills;

  public LampAction(Player player, boolean future, QuestEntry questEntry, LampReward lampReward,
      Set<Skill> skills) {
    super(ActionType.LAMP, player, future);
    this.questEntry = questEntry;
    this.lampReward = lampReward;
    this.skills = skills;
  }

  /**
   * Returns the {@link LampReward} for this action.
   *
   * @return the xp lamp
   */
  public LampReward getLampReward() {
    return lampReward;
  }

  /**
   * Returns the {@link QuestEntry} for this action.
   *
   * @return the quest entry
   */
  public QuestEntry getQuestEntry() {
    return questEntry;
  }

  /**
   * Returns the {@link Set<Skill>} to be used on the lamp for this action.
   *
   * @return the set of skills
   */
  public Set<Skill> getSkills() {
    return skills;
  }

  /**
   * {@inheritDoc}
   *
   * Format is:
   *
   * For non-future actions:
   *
   * `Quest display name`: Use `lamp type description` on `skills` to gain `xp` xp.
   *
   * For future actions:
   *
   * `Quest display name`: Use `lamp type description` on `skills` to gain `xp` xp (when
   * requirements are met).
   */
  @Override
  public String getMessage() {
    StringBuilder message = new StringBuilder();
    String xp = Skill.formatXp(lampReward.getXpForSkills(getPlayer(), skills));

    message.append(questEntry.getQuest().getDisplayName());
    message.append(": Use ");
    message.append(lampReward.getType().getDescription());

    if (!skills.isEmpty()) {
      message.append(" on ");
      message.append(skills.stream().map(Skill::toString).collect(Collectors.joining(", ")));
    }

    message.append(" to gain ");
    message.append(xp);
    message.append(" xp");

    if (isFuture()) {
      message.append(" (when requirements are met)");
    }

    return message.toString();
  }

  /**
   * {@inheritDoc}
   *
   * @see LampReward#meetsRequirements(Player)
   */
  @Override
  public boolean meetsRequirements(Player player) {
    return lampReward.meetsRequirements(player);
  }

  /**
   * {@inheritDoc}
   *
   * Add XP to the {@link Player} for each {@link Skill}.
   *
   * @see LampReward#getXpForSkills(Player, Set<Skill>)
   */
  @Override
  public void process(Player player) {
    double xp = lampReward.getXpForSkills(player, skills);

    skills.forEach(s -> player.addSkillXP(s, xp));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public LampActionDTO createDTO() {
    return new LampActionDTO.Builder().withPlayer(getPlayer().createDTO()).withFuture(isFuture())
        .withMessage(getMessage()).withQuest(getQuestEntry().getQuest().createDTO()).build();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public LampAction copyForPlayer(Player player) {
    return new LampAction(player, isFuture(), getQuestEntry(), getLampReward(), getSkills());
  }
}
