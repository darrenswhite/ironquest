package com.darrenswhite.rs.ironquest.action;

import com.darrenswhite.rs.ironquest.dto.QuestActionDTO;
import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.QuestEntry;
import com.darrenswhite.rs.ironquest.player.QuestStatus;
import com.darrenswhite.rs.ironquest.quest.Quest;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A class representing an {@link Action} to complete a {@link Quest} for a {@link Player}.
 *
 * @author Darren S. White
 */
public class QuestAction extends Action {

  private static final Logger LOG = LogManager.getLogger(QuestAction.class);

  private final Quest quest;

  public QuestAction(Player player, Quest quest) {
    super(ActionType.QUEST, player, false);
    this.quest = quest;
  }

  /**
   * Returns the {@link Quest} for this action.
   *
   * @return the quest entry
   */
  public Quest getQuest() {
    return quest;
  }

  /**
   * {@inheritDoc}
   *
   * @see Quest#getDisplayName()
   */
  @Override
  public String getMessage() {
    return quest.getDisplayName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean meetsRequirements(Player player) {
    return quest.meetsAllRequirements(player);
  }

  /**
   * {@inheritDoc}
   *
   * Sets the {@link QuestEntry} status as completed and adds quest xp rewards to the {@link
   * Player}.
   */
  @Override
  public void process(Player player) {
    LOG.debug("Completing quest: {}", quest.getDisplayName());

    player.getQuestEntry(quest).setStatus(QuestStatus.COMPLETED);
    quest.getRewards().getXp().forEach(player::addSkillXP);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public QuestActionDTO createDTO() {
    return new QuestActionDTO.Builder().withPlayer(getPlayer().createDTO()).withFuture(isFuture())
        .withMessage(getMessage()).withQuest(getQuest().createDTO()).build();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public QuestAction copyForPlayer(Player player) {
    return new QuestAction(player, getQuest());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof QuestAction)) {
      return false;
    }
    QuestAction that = (QuestAction) o;
    return future == that.future && type == that.type && Objects.equals(player, that.player)
        && Objects.equals(quest, that.quest);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final int hashCode() {
    return Objects.hash(future, type, player, quest);
  }
}
