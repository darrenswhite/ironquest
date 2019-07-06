package com.darrenswhite.rs.ironquest.path;

import com.darrenswhite.rs.ironquest.action.Action;
import com.darrenswhite.rs.ironquest.action.LampAction;
import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.QuestEntry;
import com.darrenswhite.rs.ironquest.player.QuestPriority;
import com.darrenswhite.rs.ironquest.player.QuestStatus;
import com.darrenswhite.rs.ironquest.player.Skill;
import com.darrenswhite.rs.ironquest.quest.Quest;
import com.darrenswhite.rs.ironquest.quest.QuestMemberFilter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Darren S. White
 */
@Service
public class PathFinder {

  private static final Logger LOG = LogManager.getLogger(PathFinder.class);

  /**
   * The URL to retrieve quest data from
   */
  private static final String QUESTS_URL = "https://us-central1-ironquest-e8f3e.cloudfunctions.net/getQuests";

  private final ObjectMapper objectMapper;
  private final Set<Quest> quests;

  @Autowired
  public PathFinder(ObjectMapper objectMapper) throws IOException {
    this.objectMapper = objectMapper;
    this.quests = loadQuests();
  }

  public Path find(String name, QuestMemberFilter memberFilter, boolean ironman,
      boolean recommended, Set<Skill> lampSkills, Map<Integer, QuestPriority> questPriorities) {
    LOG.debug("Using player profile: " + name);

    Player player = new Player();
    Set<QuestEntry> questEntries = createQuestEntries(quests, questPriorities);

    player.setName(name);
    player.setFree(memberFilter.isFree());
    player.setMembers(memberFilter.isMembers());
    player.setIronman(ironman);
    player.setRecommended(recommended);
    player.setLampSkills(lampSkills);
    player.setQuests(questEntries);
    player.load();

    return find(player);
  }

  private Path find(Player player) {
    Set<Action> actions = new LinkedHashSet<>();
    List<Action> futureActions = new ArrayList<>();

    completePlaceholderQuests(player);

    while (!player.getIncompleteQuests().isEmpty()) {
      Optional<QuestEntry> bestQuest = player.getBestQuest(player.getIncompleteQuests());

      if (bestQuest.isPresent()) {
        processQuest(player, actions, futureActions, bestQuest.get());
      } else {
        throw new IllegalStateException("Unable to find best quest");
      }

      processFutureActions(player, actions, futureActions);
    }

    for (Action futureAction : futureActions) {
      LOG.debug("Adding future action: {}", futureAction);

      actions.add(futureAction);
    }

    return new Path(actions);
  }

  private void processQuest(Player player, Set<Action> actions, List<Action> futureActions,
      QuestEntry bestQuest) {
    Set<Action> newActions = player.completeQuest(bestQuest);

    for (Action newAction : newActions) {
      if (newAction.isFuture()) {
        LOG.debug("Adding future action: {}", newAction);

        futureActions.add(newAction);
      } else {
        LOG.debug("Processing action: {}", newAction);

        newAction.process(player);
        actions.add(newAction);
      }
    }
  }

  private void processFutureActions(Player player, Set<Action> actions,
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

  private Set<QuestEntry> createQuestEntries(Set<Quest> quests,
      Map<Integer, QuestPriority> questPriorities) {
    return quests.stream().map(q -> {
      QuestPriority priority = questPriorities.getOrDefault(q.getId(), QuestPriority.NORMAL);
      return new QuestEntry(q, QuestStatus.NOT_STARTED, priority);
    }).collect(Collectors.toSet());
  }

  private Set<Quest> loadQuests() throws IOException {
    LOG.debug("Trying to retrieve quests from URL: {}", QUESTS_URL);

    return objectMapper.readValue(new URL(QUESTS_URL), new TypeReference<Set<Quest>>() {
    });
  }

  private void completePlaceholderQuests(Player player) {
    for (QuestEntry entry : player.getIncompleteQuests()) {
      Quest quest = entry.getQuest();

      if (quest.isPlaceholder()) {
        LOG.debug("Processing placeholder quest: {}", quest.getDisplayName());

        Set<Action> newActions = player.completeQuest(entry);

        for (Action newAction : newActions) {
          newAction.process(player);
        }
      }
    }
  }
}
