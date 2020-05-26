package com.darrenswhite.rs.ironquest.controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.darrenswhite.rs.ironquest.dto.PathDTO;
import com.darrenswhite.rs.ironquest.dto.PathFinderParametersDTO;
import com.darrenswhite.rs.ironquest.path.Path;
import com.darrenswhite.rs.ironquest.path.PathFinderService;
import com.darrenswhite.rs.ironquest.path.QuestNotFoundException;
import com.darrenswhite.rs.ironquest.path.algorithm.AlgorithmId;
import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.PlayerService;
import com.darrenswhite.rs.ironquest.player.QuestPriority;
import com.darrenswhite.rs.ironquest.player.Skill;
import com.darrenswhite.rs.ironquest.quest.Quest;
import com.darrenswhite.rs.ironquest.quest.QuestAccessFilter;
import com.darrenswhite.rs.ironquest.quest.QuestTypeFilter;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class QuestControllerTest {

  static PlayerService playerService;
  static PathFinderService pathFinderService;
  static QuestController controller;

  @BeforeAll
  static void beforeAll() {
    playerService = mock(PlayerService.class);
    pathFinderService = mock(PathFinderService.class);
    controller = new QuestController(playerService, pathFinderService);
  }

  @AfterEach
  void tearDown() {
    reset(playerService);
    reset(pathFinderService);
  }

  @Nested
  class GetQuests {

    @Test
    void shouldReturnIncompleteQuestsForPlayer() {
      Set<Quest> quests = Sets
          .newLinkedHashSet(new Quest.Builder(1).build(), new Quest.Builder(2).build(),
              new Quest.Builder(3).build());
      String name = "username";
      QuestAccessFilter accessFilter = QuestAccessFilter.ALL;
      QuestTypeFilter typeFilter = QuestTypeFilter.ALL;
      Player player = mock(Player.class);
      PathFinderParametersDTO parameters = new PathFinderParametersDTO();

      parameters.setName(name);
      parameters.setAccessFilter(accessFilter);
      parameters.setTypeFilter(typeFilter);

      when(playerService.createPlayer(name, accessFilter, typeFilter)).thenReturn(player);
      when(player.getIncompleteQuests()).thenReturn(quests);

      Set<Quest> result = controller.getQuests(parameters);

      assertThat(result, equalTo(quests));
    }
  }

  @Nested
  class GetPath {

    @Test
    void shouldFindPathAndCreateDTO() throws QuestNotFoundException {
      String name = "username";
      QuestAccessFilter accessFilter = QuestAccessFilter.ALL;
      QuestTypeFilter typeFilter = QuestTypeFilter.ALL;
      Set<Skill> lampSkills = new LinkedHashSet<>();
      Map<Integer, QuestPriority> questPriorities = new LinkedHashMap<>();
      Player player = mock(Player.class);
      Path path = mock(Path.class);
      PathDTO pathDTO = mock(PathDTO.class);
      PathFinderParametersDTO parameters = new PathFinderParametersDTO();
      AlgorithmId algorithm = AlgorithmId.DEFAULT;

      parameters.setName(name);
      parameters.setAccessFilter(accessFilter);
      parameters.setTypeFilter(typeFilter);
      parameters.setIronman(true);
      parameters.setRecommended(true);
      parameters.setLampSkills(lampSkills);
      parameters.setQuestPriorities(questPriorities);
      parameters.setAlgorithm(algorithm);

      when(playerService
          .createPlayer(name, accessFilter, typeFilter, true, true, lampSkills, questPriorities))
          .thenReturn(player);
      when(pathFinderService.find(player, algorithm)).thenReturn(path);
      when(path.createDTO()).thenReturn(pathDTO);

      PathDTO result = controller.getPath(parameters);

      verify(playerService)
          .createPlayer(name, accessFilter, typeFilter, true, true, lampSkills, questPriorities);
      verify(pathFinderService).find(player, algorithm);
      verify(path).createDTO();
      assertThat(result, equalTo(pathDTO));
    }
  }
}
