package com.darrenswhite.rs.ironquest.controller;

import com.darrenswhite.rs.ironquest.dto.PathDTO;
import com.darrenswhite.rs.ironquest.path.BestQuestNotFoundException;
import com.darrenswhite.rs.ironquest.path.PathFinder;
import com.darrenswhite.rs.ironquest.player.QuestPriority;
import com.darrenswhite.rs.ironquest.player.Skill;
import com.darrenswhite.rs.ironquest.quest.Quest;
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
 *
 * @author Darren S. White
 */
@RestController
@RequestMapping("/api/quests")
public class QuestController {

  private final PathFinder pathFinder;

  @Autowired
  public QuestController(PathFinder pathFinder) {
    this.pathFinder = pathFinder;
  }

  /**
   * Returns the optimal path.
   *
   * @param name player name to load data for
   * @param accessFilter filter quests by access
   * @param ironman <tt>true</tt> to enable ironman quest requirements; <tt>false</tt> otherwise.
   * @param recommended <tt>true</tt> to enable recommended quest requirements; <tt>false</tt>
   * otherwise.
   * @param lampSkills set of skills to use on lamps
   * @param questPriorities prioritise quests by id
   * @param typeFilter filter quests by type
   * @return the optimal path
   * @throws BestQuestNotFoundException if the "best" {@link Quest} can not be found
   */
  @GetMapping("/path")
  public PathDTO getPath(@RequestParam(name = "name", required = false) String name,
      @RequestParam(name = "accessFilter", required = false, defaultValue = "ALL") QuestAccessFilter accessFilter,
      @RequestParam(name = "ironman", required = false) boolean ironman,
      @RequestParam(name = "recommended", required = false) boolean recommended,
      @RequestParam(name = "lampSkills", required = false) Set<Skill> lampSkills,
      @RequestParam(name = "questPriorities", required = false) Map<Integer, QuestPriority> questPriorities,
      @RequestParam(name = "typeFilter", required = false, defaultValue = "ALL") QuestTypeFilter typeFilter)
      throws BestQuestNotFoundException {
    if (lampSkills == null) {
      lampSkills = Collections.emptySet();
    }
    if (questPriorities == null) {
      questPriorities = Collections.emptyMap();
    }
    return pathFinder
        .find(name, accessFilter, ironman, recommended, lampSkills, questPriorities, typeFilter).createDTO();
  }
}
