package com.darrenswhite.rs.ironquest.controller;

import com.darrenswhite.rs.ironquest.dto.PathDTO;
import com.darrenswhite.rs.ironquest.dto.PathFinderParametersDTO;
import com.darrenswhite.rs.ironquest.path.BestQuestNotFoundException;
import com.darrenswhite.rs.ironquest.path.PathFinder;
import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.PlayerService;
import com.darrenswhite.rs.ironquest.quest.Quest;
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
@RequestMapping("/quests")
public class QuestController {

  private final PathFinder pathFinder;
  private final PlayerService playerService;

  @Autowired
  public QuestController(PathFinder pathFinder, PlayerService playerService) {
    this.pathFinder = pathFinder;
    this.playerService = playerService;
  }

  /**
   * Returns a {@link Set} of {@link Quest}s which are not completed.
   *
   * @param pathFinderParametersDTO the parameters
   * @return set of incomplete quests
   */
  @GetMapping
  public Set<Quest> getRemainingQuests(PathFinderParametersDTO pathFinderParametersDTO) {
    return createPlayer(pathFinderParametersDTO).getIncompleteQuests();
  }

  /**
   * Finds the optimal path.
   *
   * @param pathFinderParametersDTO the parameters
   * @return the optimal path
   * @throws BestQuestNotFoundException if the "best" {@link Quest} can not be found
   */
  @GetMapping("/path")
  public PathDTO getPath(PathFinderParametersDTO pathFinderParametersDTO)
      throws BestQuestNotFoundException {
    Player player = createPlayer(pathFinderParametersDTO);

    return pathFinder.find(player).createDTO();
  }

  /**
   * Create a {@link Player} from the given parameters.
   *
   * @param pathFinderParametersDTO the parameters
   * @return the player
   */
  private Player createPlayer(PathFinderParametersDTO pathFinderParametersDTO) {
    return playerService
        .createPlayer(pathFinderParametersDTO.getName(), pathFinderParametersDTO.getAccessFilter(),
            pathFinderParametersDTO.getTypeFilter(), pathFinderParametersDTO.isIronman(),
            pathFinderParametersDTO.isRecommended(), pathFinderParametersDTO.getLampSkills(),
            pathFinderParametersDTO.getQuestPriorities());
  }
}
