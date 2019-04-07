package com.darrenswhite.rs.ironquest;

import com.darrenswhite.rs.ironquest.action.Action;
import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.QuestEntry;
import com.darrenswhite.rs.ironquest.quest.Quest;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Darren S. White
 */
public class PathFinder {

  private static final Logger LOG = LogManager.getLogger(PathFinder.class);

  private final Set<Action> actions = new LinkedHashSet<>();
  private final Player player;

  public PathFinder(Player player) {
    this.player = player;
  }

  public Set<Action> getActions() {
    return actions;
  }

  public void find() {
    actions.clear();

    completePlaceholderQuests();

    while (!player.getIncompleteQuests().isEmpty()) {
      Optional<QuestEntry> bestQuest = player.getBestQuest(player.getIncompleteQuests());

      if (bestQuest.isPresent()) {
        Set<Action> newActions = player.completeQuest(bestQuest.get());

        for (Action newAction : newActions) {
          LOG.info("Adding action: {}", newAction);

          newAction.process(player);
          actions.add(newAction);
        }
      } else {
        throw new IllegalStateException("Unable to find best quest");
      }
    }
  }

  private void completePlaceholderQuests() {
    for (QuestEntry entry : player.getIncompleteQuests()) {
      Quest quest = entry.getQuest();

      if (quest.isPlaceholder()) {
        LOG.info("Processing placeholder quest: {}", quest.getDisplayName());

        Set<Action> newActions = player.completeQuest(entry);

        for (Action newAction : newActions) {
          newAction.process(player);
        }
      }
    }
  }
}
