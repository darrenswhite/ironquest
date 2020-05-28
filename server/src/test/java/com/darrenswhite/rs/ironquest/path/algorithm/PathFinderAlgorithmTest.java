package com.darrenswhite.rs.ironquest.path.algorithm;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

import com.darrenswhite.rs.ironquest.action.ActionType;
import com.darrenswhite.rs.ironquest.action.QuestAction;
import com.darrenswhite.rs.ironquest.path.Path;
import com.darrenswhite.rs.ironquest.path.PathFinder;
import com.darrenswhite.rs.ironquest.path.QuestNotFoundException;
import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.QuestPriority;
import com.darrenswhite.rs.ironquest.quest.Quest;
import com.darrenswhite.rs.ironquest.quest.QuestRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.core.io.InputStreamResource;

abstract class PathFinderAlgorithmTest {

  static final String QUESTS_FILE = "quests-algorithms.json";
  static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  static final String QUEST_49_MAGIC_REQUIREMENT_NO_REWARDS = "49 Magic Requirement, No Rewards";
  static final String QUEST_NO_REQUIREMENTS_45K_MAGIC_REWARD = "No Requirements, 45k Magic Reward";
  static final String QUEST_NO_REQUIREMENTS_50K_MAGIC_REWARD = "No Requirements, 50k Magic Reward";
  static final String QUEST_NO_REQUIREMENTS_200K_DEFENCE_REWARD = "No Requirements, 200k Defence Reward";

  static QuestRepository questRepository;

  @BeforeAll
  static void beforeAll() throws IOException {
    questRepository = new QuestRepository(new InputStreamResource(Objects.requireNonNull(
        PathFinderAlgorithmTest.class.getClassLoader().getResourceAsStream(QUESTS_FILE))),
        OBJECT_MAPPER);
  }

  static void assertQuestOrder(PathFinderAlgorithm algorithm, String... displayNameOrder)
      throws QuestNotFoundException {
    assertQuestOrder(algorithm, Collections.emptyMap(), displayNameOrder);
  }

  static void assertQuestOrder(PathFinderAlgorithm algorithm,
      Map<String, QuestPriority> questPriorities, String... displayNameOrder)
      throws QuestNotFoundException {
    Map<String, Quest> expectedQuestOrder = mapDisplayNamesToQuests(displayNameOrder);
    Player player = createPlayer(questPriorities);

    PathFinder pathFinder = new PathFinder(player, algorithm);

    Path path = pathFinder.find();
    List<Quest> questActions = path.getActions().stream()
        .filter(action -> action.getType() == ActionType.QUEST).map(QuestAction.class::cast)
        .map(QuestAction::getQuest).collect(Collectors.toList());

    assertThat(questActions, contains(
        expectedQuestOrder.values().stream().map(Matchers::equalTo).collect(Collectors.toList())));
  }

  static Player createPlayer(Map<String, QuestPriority> priorities) {
    Player player = new Player.Builder().withQuests(questRepository.getQuests()).build();

    priorities.forEach((displayName, priority) -> player
        .setQuestPriority(getQuestByDisplayName(displayName), priority));

    return player;
  }

  static LinkedHashMap<String, Quest> mapDisplayNamesToQuests(String[] displayNameOrder) {
    return Stream.of(displayNameOrder).collect(Collectors
        .toMap(Function.identity(), PathFinderAlgorithmTest::getQuestByDisplayName, (u, v) -> {
          throw new IllegalStateException(String.format("Duplicate quest %s", u));
        }, LinkedHashMap::new));
  }

  static Quest getQuestByDisplayName(String displayName) {
    return questRepository.getQuests().stream()
        .filter(quest -> quest.getDisplayName().equals(displayName)).findFirst().orElseThrow();
  }
}
