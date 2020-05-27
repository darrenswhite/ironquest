package com.darrenswhite.rs.ironquest.path;

import com.darrenswhite.rs.ironquest.action.Action;
import com.darrenswhite.rs.ironquest.action.LampAction;
import com.darrenswhite.rs.ironquest.path.algorithm.DefaultAlgorithm;
import com.darrenswhite.rs.ironquest.path.algorithm.PathFinderAlgorithm;
import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.quest.Quest;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Find the optimal {@link Path} for a given set of parameters.
 *
 * @author Darren S. White
 */
public class PathFinder {

  private static final Logger LOG = LogManager.getLogger(PathFinder.class);

  private final Player player;
  private final PathFinderAlgorithm algorithm;

  public PathFinder(Player player) {
    this(player, new DefaultAlgorithm());
  }

  public PathFinder(Player player, PathFinderAlgorithm algorithm) {
    this.player = player;
    this.algorithm = algorithm;
  }

  /**
   * Find the optimal {@link Path} for the specified {@link Player} and the given {@link
   * PathFinderAlgorithm}.
   *
   * Initial stats for the player are calculated and incomplete placeholder quests are completed.
   *
   * The optimal quest is found and marked as completed for the {@link Player} until all quests are
   * completed or a {@link QuestNotFoundException} is thrown.
   *
   * If there are any actions which can not be completed after all quests are completed, these are
   * added as future actions.
   *
   * The default algorithm is used which is implemented by {@link DefaultAlgorithm}.
   *
   * @return the optimal path
   * @throws QuestNotFoundException if the optimal quest can not be found
   */
  public Path find() throws QuestNotFoundException {
    LOG.debug("Finding optimal quest path for player: {}", player.getName());

    List<Action> actions = new LinkedList<>();
    PathStats stats = createStats(player);
    QuestIterator iterator = new QuestIterator(player, algorithm);

    completePlaceholderQuests(player);

    while (iterator.hasNext()) {
      Quest next = iterator.next();

      actions.addAll(completeQuest(player, next));
      processFutureActions(player, actions);
    }

    if (!player.getIncompleteQuests().isEmpty()) {
      throw new QuestNotFoundException("Unable to find next quest for player: " + player.getName());
    }

    processFutureActions(player, actions);

    return new Path(actions, stats);
  }

  /**
   * Create {@link PathStats} for the specified {@link Player}.
   *
   * @param player the player
   * @return the path stats
   */
  private PathStats createStats(Player player) {
    double completed =
        (double) player.getCompletedQuests().size() / (double) player.getQuests().size();

    if (Double.isNaN(completed)) {
      completed = 0;
    }

    int percentComplete = (int) Math.floor(completed * 100);

    return new PathStats(percentComplete);
  }

  /**
   * Complete the quest and process all non-future actions.
   *
   * @return the processed quest actions
   */
  private List<Action> completeQuest(Player player, Quest quest) {
    List<Action> processedActions = new LinkedList<>();
    List<Action> questActions = player.completeQuest(quest);

    for (Action newAction : questActions) {
      if (newAction.isFuture()) {
        LOG.debug("Adding future action: {}", newAction);

        processedActions.add(newAction);
      } else {
        LOG.debug("Processing action: {}", newAction);

        newAction.process(player);
        processedActions.add(newAction.copyForPlayer(player));
      }
    }

    return processedActions;
  }

  /**
   * Process all future actions if the requirements are met. Any actions which are processed are
   * removed from the <tt>actions</tt> list, copied for the player and added to the end of the
   * list.
   *
   * @param player the player
   * @param actions the actions
   */
  private void processFutureActions(Player player, List<Action> actions) {
    List<Action> processedActions = new LinkedList<>();

    for (Iterator<Action> iterator = actions.iterator(); iterator.hasNext(); ) {
      Action action = iterator.next();

      if (action.isFuture() && action.meetsRequirements(player)) {
        if (action instanceof LampAction) {
          LampAction lampAction = (LampAction) action;

          action = player.createLampAction(lampAction.getQuest(), lampAction.getLampReward());
        }

        LOG.debug("Processing future action: {}", action);

        action.process(player);
        processedActions.add(action.copyForPlayer(player));
        iterator.remove();
      }
    }

    actions.addAll(processedActions);
  }

  /**
   * Complete all incomplete placeholder quests for the {@link Player} and process all new {@link
   * Action}s.
   *
   * @param player the player
   * @see Quest#isPlaceholder()
   */
  private void completePlaceholderQuests(Player player) {
    for (Quest quest : player.getIncompleteQuests()) {
      if (quest.isPlaceholder()) {
        LOG.debug("Processing placeholder quest: {}", quest.getDisplayName());

        List<Action> newActions = player.completeQuest(quest);

        for (Action newAction : newActions) {
          newAction.process(player);
        }
      }
    }
  }
}
