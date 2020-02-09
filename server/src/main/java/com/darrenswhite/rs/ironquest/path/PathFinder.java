package com.darrenswhite.rs.ironquest.path;

import com.darrenswhite.rs.ironquest.action.Action;
import com.darrenswhite.rs.ironquest.action.LampAction;
import com.darrenswhite.rs.ironquest.player.HiscoreService;
import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.QuestEntry;
import com.darrenswhite.rs.ironquest.player.QuestPriority;
import com.darrenswhite.rs.ironquest.player.RuneMetricsService;
import com.darrenswhite.rs.ironquest.player.Skill;
import com.darrenswhite.rs.ironquest.quest.Quest;
import com.darrenswhite.rs.ironquest.quest.QuestAccessFilter;
import com.darrenswhite.rs.ironquest.quest.QuestService;
import com.darrenswhite.rs.ironquest.quest.QuestTypeFilter;
import com.darrenswhite.rs.ironquest.quest.Quests;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * {@link Service} for finding the optimal {@link Path} for a given set of parameters.
 *
 * @author Darren S. White
 */
@Service
public class PathFinder {

  private static final Logger LOG = LogManager.getLogger(PathFinder.class);

  private final QuestService questService;
  private final HiscoreService hiscoreService;
  private final RuneMetricsService runeMetricsService;

  @Autowired
  public PathFinder(QuestService questService, HiscoreService hiscoreService,
      RuneMetricsService runeMetricsService) {
    this.questService = questService;
    this.hiscoreService = hiscoreService;
    this.runeMetricsService = runeMetricsService;
  }

  /**
   * Create a new {@link Player} from the specified parameters and find the optimal {@link Path}.
   *
   * @param name player name to load data for; can be null
   * @param accessFilter filter quests by access
   * @param ironman <tt>true</tt> to enable ironman quest requirements; <tt>false</tt> otherwise.
   * @param recommended <tt>true</tt> to enable recommended quest requirements; <tt>false</tt>
   * otherwise.
   * @param lampSkills set of skills to use on lamps
   * @param questPriorities prioritise quests by id
   * @param typeFilter filter quests by type
   * @return the optimal path
   * @throws BestQuestNotFoundException if the "best" {@link Quest} can not be found
   * @see PathFinder#createPlayer(String, QuestAccessFilter, boolean, boolean, Set, Map,
   * QuestTypeFilter)
   * @see PathFinder#findForPlayer(Player)
   */
  public Path find(String name, QuestAccessFilter accessFilter, boolean ironman,
      boolean recommended, Set<Skill> lampSkills, Map<Integer, QuestPriority> questPriorities,
      QuestTypeFilter typeFilter) throws BestQuestNotFoundException {
    LOG.debug("Using player profile: {}", name);

    Player player = createPlayer(name, accessFilter, ironman, recommended, lampSkills,
        questPriorities, typeFilter);

    return findForPlayer(player);
  }

  /**
   * Create a {@link Player} from the specified parameters.
   *
   * Quests will be filtered, prioritised and added to the player. Player data is loaded from the
   * hiscores and runemetrics.
   *
   * @param name player name to load data for; can be null
   * @param accessFilter filter quests by access
   * @param ironman <tt>true</tt> to enable ironman quest requirements; <tt>false</tt> otherwise.
   * @param recommended <tt>true</tt> to enable recommended quest requirements; <tt>false</tt>
   * otherwise.
   * @param lampSkills set of skills to use on lamps
   * @param questPriorities prioritise quests by id
   * @param typeFilter filter quests by type
   * @see Quests#createQuestEntries(Map, QuestAccessFilter, QuestTypeFilter)
   * @see Player#load(HiscoreService, RuneMetricsService)
   */
  Player createPlayer(String name, QuestAccessFilter accessFilter, boolean ironman,
      boolean recommended, Set<Skill> lampSkills, Map<Integer, QuestPriority> questPriorities,
      QuestTypeFilter typeFilter) {
    Set<QuestEntry> questEntries = questService.getQuests()
        .createQuestEntries(questPriorities, accessFilter, typeFilter);
    Player player = new Player.Builder().withName(name).withIronman(ironman)
        .withRecommended(recommended).withLampSkills(lampSkills).withQuests(questEntries).build();

    player.load(hiscoreService, runeMetricsService);

    return player;
  }

  /**
   * Find the optimal {@link Path} for the specified {@link Player}.
   *
   * Initial stats for the player are calculated and incomplete placeholder quests are completed.
   *
   * The best quest is found and marked as completed for the {@link Player} until all quests are
   * completed or a {@link BestQuestNotFoundException} is thrown.
   *
   * If there are any actions which can not be completed after all quests are completed, these are
   * added as future actions.
   *
   * @param player the player to find the path for
   * @return the optimal path
   * @throws BestQuestNotFoundException if the best quest can not be found
   */
  Path findForPlayer(Player player) throws BestQuestNotFoundException {
    List<Action> actions = new LinkedList<>();
    PathStats stats = createStats(player);

    LOG.debug("Finding optimal quest path for player: {}", player.getName());

    completePlaceholderQuests(player);

    while (!player.getIncompleteQuests().isEmpty()) {
      Optional<QuestEntry> bestQuest = player.getBestQuest(player.getIncompleteQuests());

      if (!bestQuest.isPresent()) {
        throw new BestQuestNotFoundException(
            "Unable to find best quest for player: " + player.getName());
      }

      actions.addAll(completeQuest(player, bestQuest.get()));
      processFutureActions(player, actions);
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
  private List<Action> completeQuest(Player player, QuestEntry bestQuest) {
    List<Action> processedActions = new LinkedList<>();
    List<Action> questActions = player.completeQuest(bestQuest);

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

          action = player.createLampAction(lampAction.getQuestEntry(), lampAction.getLampReward());
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
    for (QuestEntry entry : player.getIncompleteQuests()) {
      Quest quest = entry.getQuest();

      if (quest.isPlaceholder()) {
        LOG.debug("Processing placeholder quest: {}", quest.getDisplayName());

        List<Action> newActions = player.completeQuest(entry);

        for (Action newAction : newActions) {
          newAction.process(player);
        }
      }
    }
  }
}
