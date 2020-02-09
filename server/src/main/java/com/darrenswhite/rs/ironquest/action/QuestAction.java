package com.darrenswhite.rs.ironquest.action;

import com.darrenswhite.rs.ironquest.dto.QuestActionDTO;
import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.QuestEntry;
import com.darrenswhite.rs.ironquest.player.QuestStatus;
import com.darrenswhite.rs.ironquest.quest.Quest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A class representing an {@link Action} to complete a {@link Quest} for a {@link Player}.
 *
 * @author Darren S. White
 */
public class QuestAction extends Action {

  private static final Logger LOG = LogManager.getLogger(QuestAction.class);

  private final QuestEntry entry;

  public QuestAction(Player player, QuestEntry entry) {
    super(ActionType.QUEST, player, false);
    this.entry = entry;
  }

  /**
   * Returns the {@link QuestEntry} for this action.
   *
   * @return the quest entry
   */
  public QuestEntry getQuestEntry() {
    return entry;
  }

  /**
   * {@inheritDoc}
   *
   * @see Quest#getDisplayName()
   */
  @Override
  public String getMessage() {
    return entry.getQuest().getDisplayName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean meetsRequirements(Player player) {
    return entry.getQuest().meetsAllRequirements(player);
  }

  /**
   * {@inheritDoc}
   *
   * Sets the {@link QuestEntry} status as completed and adds quest xp rewards to the {@link}
   * Player.
   */
  @Override
  public void process(Player player) {
    LOG.debug("Completing quest: {}", entry.getQuest().getDisplayName());

    entry.setStatus(QuestStatus.COMPLETED);
    entry.getQuest().getRewards().getXp().forEach(player::addSkillXP);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public QuestActionDTO createDTO() {
    return new QuestActionDTO.Builder().withPlayer(getPlayer().createDTO()).withFuture(isFuture())
        .withMessage(getMessage()).withQuest(getQuestEntry().getQuest().createDTO()).build();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public QuestAction copyForPlayer(Player player) {
    return new QuestAction(player, getQuestEntry());
  }
}
