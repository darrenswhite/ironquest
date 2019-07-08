package com.darrenswhite.rs.ironquest.action;

import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.QuestEntry;
import com.darrenswhite.rs.ironquest.player.Skill;
import com.darrenswhite.rs.ironquest.quest.reward.LampReward;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A class representing an {@link Action} to use a {@link LampReward} on a set of {@link Skill}'s
 * for a given {@link QuestEntry}. The lampReward maybe used in the future when requirements have
 * been met.
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

  public LampReward getLampReward() {
    return lampReward;
  }

  public QuestEntry getQuestEntry() {
    return questEntry;
  }

  public Set<Skill> getSkills() {
    return skills;
  }

  @Override
  public String getMessage() {
    StringBuilder message = new StringBuilder();
    String xp = Skill.formatXp(lampReward.getXp());

    message.append(questEntry.getQuest().getDisplayName()).append(": Use ").append(xp)
        .append(" xp lamp on");

    if (!skills.isEmpty()) {
      message.append(' ');
      message.append(skills.stream().map(Skill::toString).collect(Collectors.joining(", ")));
    }

    if (isFuture()) {
      message.append(" (when requirements are met)");
    }

    return message.toString();
  }

  @Override
  public boolean meetsRequirements(Player player) {
    return lampReward.meetsRequirements(player);
  }

  @Override
  public void process(Player player) {
    skills.forEach(s -> player.addSkillXP(s, lampReward.getXp()));
  }

  @Override
  public LampAction copyForPlayer(Player player) {
    return new LampAction(player, isFuture(), getQuestEntry(), getLampReward(), getSkills());
  }
}
