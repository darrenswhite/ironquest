package com.darrenswhite.rs.ironquest.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.darrenswhite.rs.ironquest.dto.PathDTO;
import com.darrenswhite.rs.ironquest.path.Path;
import com.darrenswhite.rs.ironquest.path.PathFinder;
import com.darrenswhite.rs.ironquest.player.QuestPriority;
import com.darrenswhite.rs.ironquest.player.Skill;
import com.darrenswhite.rs.ironquest.quest.QuestAccessFilter;
import com.darrenswhite.rs.ironquest.quest.QuestTypeFilter;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.junit.Test;

public class QuestControllerTest {

  @Test
  public void getPath() {
    PathFinder pathFinder = mock(PathFinder.class);
    QuestController controller = new QuestController(pathFinder);

    String name = "username";
    QuestAccessFilter accessFilter = QuestAccessFilter.ALL;
    Set<Skill> lampSkills = new LinkedHashSet<>();
    Map<Integer, QuestPriority> questPriorities = new LinkedHashMap<>();
    QuestTypeFilter typeFilter = QuestTypeFilter.ALL;
    Path path = mock(Path.class);
    PathDTO pathDTO = mock(PathDTO.class);

    when(pathFinder.find(name, accessFilter, true, true, lampSkills, questPriorities, typeFilter))
        .thenReturn(path);
    when(path.createDTO()).thenReturn(pathDTO);

    PathDTO result = controller
        .getPath(name, accessFilter, true, true, lampSkills, questPriorities, typeFilter);

    verify(pathFinder)
        .find(name, accessFilter, true, true, lampSkills, questPriorities, typeFilter);
    verify(path).createDTO();
    assertEquals(pathDTO, result);
  }

  @Test
  public void getPath_NullValues() {
    PathFinder pathFinder = mock(PathFinder.class);
    QuestController controller = new QuestController(pathFinder);

    String name = "username";
    QuestAccessFilter accessFilter = QuestAccessFilter.ALL;
    QuestTypeFilter typeFilter = QuestTypeFilter.ALL;
    Path path = mock(Path.class);
    PathDTO pathDTO = mock(PathDTO.class);

    when(pathFinder
        .find(eq(name), eq(accessFilter), eq(false), eq(false), eq(new LinkedHashSet<>()),
            eq(new LinkedHashMap<>()), eq(typeFilter))).thenReturn(path);
    when(path.createDTO()).thenReturn(pathDTO);

    PathDTO result = controller.getPath(name, accessFilter, false, false, null, null, typeFilter);

    verify(pathFinder)
        .find(eq(name), eq(accessFilter), eq(false), eq(false), eq(new LinkedHashSet<>()),
            eq(new LinkedHashMap<>()), eq(typeFilter));
    verify(path).createDTO();
    assertEquals(pathDTO, result);
  }
}
