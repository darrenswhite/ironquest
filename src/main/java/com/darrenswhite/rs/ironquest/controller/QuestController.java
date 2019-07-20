package com.darrenswhite.rs.ironquest.controller;

import com.darrenswhite.rs.ironquest.dto.PathDTO;
import com.darrenswhite.rs.ironquest.path.PathFinder;
import com.darrenswhite.rs.ironquest.player.QuestPriority;
import com.darrenswhite.rs.ironquest.player.Skill;
import com.darrenswhite.rs.ironquest.quest.QuestAccessFilter;
import com.darrenswhite.rs.ironquest.quest.QuestTypeFilter;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * {@link RestController} for quests path finder API.
 */
@RestController
@RequestMapping("/api/quests")
public class QuestController {

  private final PathFinder pathFinder;

  @Autowired
  public QuestController(PathFinder pathFinder) {
    this.pathFinder = pathFinder;
  }

  @GetMapping("/path")
  public PathDTO getPath(@RequestParam(name = "name", required = false) String name,
      @RequestParam(name = "accessFilter", required = false, defaultValue = "ALL") QuestAccessFilter accessFilter,
      @RequestParam(name = "ironman", required = false) boolean ironman,
      @RequestParam(name = "recommended", required = false) boolean recommended,
      @RequestParam(name = "lampSkills", required = false) Set<Skill> lampSkills,
      @RequestParam(name = "questPriorities", required = false) Map<Integer, QuestPriority> questPriorities,
      @RequestParam(name = "typeFilter", required = false, defaultValue = "ALL") QuestTypeFilter typeFilter) {
    if (lampSkills == null) {
      lampSkills = Collections.emptySet();
    }
    if (questPriorities == null) {
      questPriorities = Collections.emptyMap();
    }
    return pathFinder
        .find(name, accessFilter, ironman, recommended, lampSkills, questPriorities, typeFilter)
        .createDTO();
  }
}
