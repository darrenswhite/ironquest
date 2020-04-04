package com.darrenswhite.rs.ironquest.controller;

import com.darrenswhite.rs.ironquest.dto.PathDTO;
import com.darrenswhite.rs.ironquest.dto.PathFinderParametersDTO;
import com.darrenswhite.rs.ironquest.path.BestQuestNotFoundException;
import com.darrenswhite.rs.ironquest.path.PathFinder;
import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.PlayerService;
import com.darrenswhite.rs.ironquest.quest.Quest;
import com.darrenswhite.rs.ironquest.quest.QuestRepository;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
  private final PlayerService playerService;
  private final QuestRepository questRepository;

  @Autowired
  public QuestController(PathFinder pathFinder, PlayerService playerService,
      QuestRepository questRepository) {
    this.pathFinder = pathFinder;
    this.playerService = playerService;
    this.questRepository = questRepository;
  }

  /**
   * Returns the loaded {@link Set} of {@link Quest}s.
   *
   * @return set of quests
   */
  @GetMapping
  public Set<Quest> getQuests() {
    return questRepository.getQuests();
  }

  /**
   * Finds the optimal path.
   *
   * @return the optimal path
   * @throws BestQuestNotFoundException if the "best" {@link Quest} can not be found
   */
  @GetMapping("/path")
  public PathDTO getPath(PathFinderParametersDTO pathFinderParametersDTO)
      throws BestQuestNotFoundException {
    Player player = playerService
        .createPlayer(pathFinderParametersDTO.getName(), pathFinderParametersDTO.getAccessFilter(),
            pathFinderParametersDTO.getTypeFilter(), pathFinderParametersDTO.isIronman(),
            pathFinderParametersDTO.isRecommended(), pathFinderParametersDTO.getLampSkills(),
            pathFinderParametersDTO.getQuestPriorities());

    return pathFinder.find(player).createDTO();
  }
}
