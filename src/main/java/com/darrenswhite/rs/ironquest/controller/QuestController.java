package com.darrenswhite.rs.ironquest.controller;

import com.darrenswhite.rs.ironquest.path.Path;
import com.darrenswhite.rs.ironquest.path.PathFinder;
import com.darrenswhite.rs.ironquest.player.QuestPriority;
import com.darrenswhite.rs.ironquest.player.Skill;
import com.darrenswhite.rs.ironquest.quest.QuestMemberFilter;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/quests")
public class QuestController {

  private final PathFinder pathFinder;

  @Autowired
  public QuestController(PathFinder pathFinder) {
    this.pathFinder = pathFinder;
  }

  @GetMapping("/path")
  public Path getPath(@RequestParam(name = "name", required = false) String name,
      @RequestParam(name = "memberFilter", required = false, defaultValue = "BOTH") QuestMemberFilter memberFilter,
      @RequestParam(name = "ironman", required = false) boolean ironman,
      @RequestParam(name = "recommended", required = false) boolean recommended,
      @RequestParam(name = "lampSkills", required = false) Set<Skill> lampSkills,
      @RequestParam(name = "questPriorities", required = false) Map<Integer, QuestPriority> questPriorities) {
    if (lampSkills == null) {
      lampSkills = Collections.emptySet();
    }
    if (questPriorities == null) {
      questPriorities = Collections.emptyMap();
    }
    return pathFinder.find(name, memberFilter, ironman, recommended, lampSkills, questPriorities);
  }
}
