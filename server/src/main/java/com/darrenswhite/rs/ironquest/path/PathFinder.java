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
import java.util.ArrayList;
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
 * {@link Service} for finding optimal {@link Path} for a given set of attributes.
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

  public Path find(String name, QuestAccessFilter accessFilter, boolean ironman,
      boolean recommended, Set<Skill> lampSkills, Map<Integer, QuestPriority> questPriorities,
      QuestTypeFilter typeFilter) throws BestQuestNotFoundException {
    LOG.debug("Using player profile: {}", name);

    Player player = createPlayer(name, accessFilter, ironman, recommended, lampSkills,
        questPriorities, typeFilter);

    return findForPlayer(player);
  }

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

  Path findForPlayer(Player player) throws BestQuestNotFoundException {
    List<Action> actions = new LinkedList<>();
    List<Action> futureActions = new ArrayList<>();
    PathStats stats = createStats(player);

    LOG.debug("Finding optimal quest path for player: {}", player.getName());

    completePlaceholderQuests(player);

    while (!player.getIncompleteQuests().isEmpty()) {
      Optional<QuestEntry> bestQuest = player.getBestQuest(player.getIncompleteQuests());

      if (!bestQuest.isPresent()) {
        throw new BestQuestNotFoundException(
            "Unable to find best quest for player: " + player.getName());
      }

      processQuest(player, actions, futureActions, bestQuest.get());
      processFutureActions(player, actions, futureActions);
    }

    for (Action futureAction : futureActions) {
      LOG.debug("Adding future action: {}", futureAction);

      actions.add(futureAction.copyForPlayer(player));
    }

    return new Path(actions, stats);
  }

  private PathStats createStats(Player player) {
    double completed =
        (double) player.getCompletedQuests().size() / (double) player.getQuests().size();

    if (Double.isNaN(completed)) {
      completed = 0;
    }

    int percentComplete = (int) Math.floor(completed * 100);

    return new PathStats(percentComplete);
  }

  private void processQuest(Player player, List<Action> actions, List<Action> futureActions,
      QuestEntry bestQuest) {
    List<Action> newActions = player.completeQuest(bestQuest);

    for (Action newAction : newActions) {
      if (newAction.isFuture()) {
        LOG.debug("Adding future action: {}", newAction);

        futureActions.add(newAction);
      } else {
        LOG.debug("Processing action: {}", newAction);

        newAction.process(player);
        actions.add(newAction.copyForPlayer(player));
      }
    }
  }

  private void processFutureActions(Player player, List<Action> actions,
      List<Action> futureActions) {
    for (Iterator<Action> iterator = futureActions.iterator(); iterator.hasNext(); ) {
      Action futureAction = iterator.next();

      if (futureAction.meetsRequirements(player)) {
        if (futureAction instanceof LampAction) {
          LampAction lampAction = (LampAction) futureAction;

          futureAction = player
              .createLampAction(lampAction.getQuestEntry(), lampAction.getLampReward());
        }

        LOG.debug("Processing future action: {}", futureAction);

        futureAction.process(player);
        actions.add(futureAction.copyForPlayer(player));
        iterator.remove();
      }
    }
  }

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
