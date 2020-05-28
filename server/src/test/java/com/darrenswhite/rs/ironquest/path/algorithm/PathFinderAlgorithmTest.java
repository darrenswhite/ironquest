package com.darrenswhite.rs.ironquest.path.algorithm;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.QuestPriority;
import com.darrenswhite.rs.ironquest.quest.Quest;
import com.darrenswhite.rs.ironquest.quest.QuestRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

  static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  static final String QUESTS_FILE = "quests-algorithms.json";

  static final String NO_REQUIREMENTS_NO_REWARDS_NAME = "No Requirements, No Rewards";
  static final String LOW_REQUIREMENTS_NO_REWARDS_NAME = "Low Requirements, No Rewards";
  static final String MEDIUM_REQUIREMENTS_NO_REWARDS_NAME = "Medium Requirements, No Rewards";
  static final String HIGH_REQUIREMENTS_NO_REWARDS_NAME = "High Requirements, No Rewards";
  static final String NO_REQUIREMENTS_LOW_REWARDS_NAME = "No Requirements, Low Rewards";
  static final String NO_REQUIREMENTS_MEDIUM_REWARDS_NAME = "No Requirements, Medium Rewards";
  static final String NO_REQUIREMENTS_HIGH_REWARDS_NAME = "No Requirements, High Rewards";
  static final String LOW_REQUIREMENTS_LOW_REWARDS_NAME = "Low Requirements, Low Rewards";
  static final String MEDIUM_REQUIREMENTS_MEDIUM_REWARDS_NAME = "Medium Requirements, Medium Rewards";
  static final String HIGH_REQUIREMENTS_HIGH_REWARDS_NAME = "High Requirements, High Rewards";

  static QuestRepository questRepository;

  @BeforeAll
  static void beforeAll() throws IOException {
    questRepository = new QuestRepository(new InputStreamResource(Objects.requireNonNull(
        PathFinderAlgorithmTest.class.getClassLoader().getResourceAsStream(QUESTS_FILE))),
        OBJECT_MAPPER);
  }

  static void assertQuestOrder(PathFinderAlgorithm algorithm, String... displayNameOrder) {
    assertQuestOrder(algorithm, Collections.emptyMap(), displayNameOrder);
  }

  static void assertQuestOrder(PathFinderAlgorithm algorithm,
      Map<String, QuestPriority> questPriorities, String... displayNameOrder) {
    Map<String, Quest> expectedQuestOrder = mapDisplayNamesToQuests(displayNameOrder);
    Player player = createPlayer(questPriorities);

    Comparator<Quest> comparator = algorithm.comparator(player);

    List<Quest> quests = new ArrayList<>(questRepository.getQuests());

    quests.sort(comparator);

    assertThat(quests, contains(
        expectedQuestOrder.values().stream().map(Matchers::equalTo).collect(Collectors.toList())));
  }

  private static Player createPlayer(Map<String, QuestPriority> questPriorities) {
    Player player = new Player.Builder().withQuests(questRepository.getQuests()).build();

    questPriorities.forEach((displayName, priority) -> player
        .setQuestPriority(getQuestByDisplayName(displayName), priority));
    return player;
  }

  private static LinkedHashMap<String, Quest> mapDisplayNamesToQuests(String[] displayNameOrder) {
    return Stream.of(displayNameOrder).collect(Collectors
        .toMap(Function.identity(), PathFinderAlgorithmTest::getQuestByDisplayName, (u, v) -> {
          throw new IllegalStateException(String.format("Duplicate quest %s", u));
        }, LinkedHashMap::new));
  }

  private static Quest getQuestByDisplayName(String displayName) {
    return questRepository.getQuests().stream()
        .filter(quest -> quest.getDisplayName().equals(displayName)).findFirst().orElseThrow();
  }
}
