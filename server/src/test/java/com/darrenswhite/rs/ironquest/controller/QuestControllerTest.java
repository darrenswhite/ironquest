package com.darrenswhite.rs.ironquest.controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.darrenswhite.rs.ironquest.dto.PathDTO;
import com.darrenswhite.rs.ironquest.path.BestQuestNotFoundException;
import com.darrenswhite.rs.ironquest.path.Path;
import com.darrenswhite.rs.ironquest.path.PathFinder;
import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.PlayerService;
import com.darrenswhite.rs.ironquest.player.QuestPriority;
import com.darrenswhite.rs.ironquest.player.Skill;
import com.darrenswhite.rs.ironquest.quest.Quest;
import com.darrenswhite.rs.ironquest.quest.QuestAccessFilter;
import com.darrenswhite.rs.ironquest.quest.QuestRepository;
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

  static PathFinder pathFinder;
  static PlayerService playerService;
  static QuestRepository questRepository;
  static QuestController controller;

  @BeforeAll
  static void beforeAll() {
    pathFinder = mock(PathFinder.class);
    playerService = mock(PlayerService.class);
    questRepository = mock(QuestRepository.class);
    controller = new QuestController(pathFinder, playerService, questRepository);
  }

  @AfterEach
  void tearDown() {
    reset(pathFinder);
    reset(playerService);
  }

  @Nested
  class GetQuests {

    @Test
    void shouldReturnAllQuests() {
      Set<Quest> quests = Sets
          .newLinkedHashSet(new Quest.Builder(1).build(), new Quest.Builder(2).build(),
              new Quest.Builder(3).build());

      when(questRepository.getQuests()).thenReturn(quests);

      Set<Quest> result = controller.getQuests();

      assertThat(result, equalTo(quests));
    }
  }

  @Nested
  class GetPath {

    @Test
    void shouldFindPathAndCreateDTO() throws BestQuestNotFoundException {
      String name = "username";
      QuestAccessFilter accessFilter = QuestAccessFilter.ALL;
      Set<Skill> lampSkills = new LinkedHashSet<>();
      Map<Integer, QuestPriority> questPriorities = new LinkedHashMap<>();
      QuestTypeFilter typeFilter = QuestTypeFilter.ALL;
      Player player = mock(Player.class);
      Path path = mock(Path.class);
      PathDTO pathDTO = mock(PathDTO.class);

      when(playerService
          .createPlayer(name, accessFilter, typeFilter, true, true, lampSkills, questPriorities))
          .thenReturn(player);
      when(pathFinder.find(player)).thenReturn(path);
      when(path.createDTO()).thenReturn(pathDTO);

      PathDTO result = controller
          .getPath(name, accessFilter, true, true, lampSkills, questPriorities, typeFilter);

      verify(playerService)
          .createPlayer(name, accessFilter, typeFilter, true, true, lampSkills, questPriorities);
      verify(pathFinder).find(player);
      verify(path).createDTO();
      assertThat(result, equalTo(pathDTO));
    }

    @Test
    void shouldUseEmptyCollectionsWhenNullValuesAreUsed() throws BestQuestNotFoundException {
      String name = "username";
      QuestAccessFilter accessFilter = QuestAccessFilter.ALL;
      QuestTypeFilter typeFilter = QuestTypeFilter.ALL;
      Player player = mock(Player.class);
      Path path = mock(Path.class);
      PathDTO pathDTO = mock(PathDTO.class);

      when(playerService
          .createPlayer(eq(name), eq(accessFilter), eq(typeFilter), eq(false), eq(false),
              eq(new LinkedHashSet<>()), eq(new LinkedHashMap<>()))).thenReturn(player);
      when(pathFinder.find(player)).thenReturn(path);
      when(path.createDTO()).thenReturn(pathDTO);

      PathDTO result = controller.getPath(name, accessFilter, false, false, null, null, typeFilter);

      verify(playerService)
          .createPlayer(eq(name), eq(accessFilter), eq(typeFilter), eq(false), eq(false),
              eq(new LinkedHashSet<>()), eq(new LinkedHashMap<>()));
      verify(pathFinder).find(player);
      verify(path).createDTO();
      assertThat(result, equalTo(pathDTO));
    }
  }
}
