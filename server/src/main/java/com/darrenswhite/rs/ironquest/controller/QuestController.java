package com.darrenswhite.rs.ironquest.controller;

import com.darrenswhite.rs.ironquest.dto.PathDTO;
import com.darrenswhite.rs.ironquest.dto.PathFinderParametersDTO;
import com.darrenswhite.rs.ironquest.path.Path;
import com.darrenswhite.rs.ironquest.path.PathFinderService;
import com.darrenswhite.rs.ironquest.path.QuestNotFoundException;
import com.darrenswhite.rs.ironquest.path.algorithm.AlgorithmId;
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

  private final PlayerService playerService;
  private final PathFinderService pathFinderService;

  @Autowired
  public QuestController(PlayerService playerService, PathFinderService pathFinderService) {
    this.playerService = playerService;
    this.pathFinderService = pathFinderService;
  }

  /**
   * Returns a {@link Set} of {@link Quest}s which are not completed for the given parameters.
   *
   * @param parameters the parameters
   * @return set of incomplete quests
   */
  @GetMapping
  public Set<Quest> getRemainingQuests(PathFinderParametersDTO parameters) {
    return createPlayer(parameters).getIncompleteQuests();
  }

  /**
   * Finds the optimal {@link Path} for the given parameters.
   *
   * @param parameters the parameters
   * @return the optimal path
   * @throws QuestNotFoundException if the optimal {@link Quest} can not be found
   */
  @GetMapping("/path")
  public PathDTO getPath(PathFinderParametersDTO parameters) throws QuestNotFoundException {
    Player player = createPlayer(parameters);
    AlgorithmId algorithm = parameters.getAlgorithm();

    return pathFinderService.find(player, algorithm).createDTO();
  }

  /**
   * Create a {@link Player} from the given parameters.
   *
   * @param parameters the parameters
   * @return the player
   */
  private Player createPlayer(PathFinderParametersDTO parameters) {
    return playerService.createPlayer(parameters.getName(), parameters.getAccessFilter(),
        parameters.getTypeFilter(), parameters.isIronman(), parameters.isRecommended(),
        parameters.getLampSkills(), parameters.getQuestPriorities());
  }
}
