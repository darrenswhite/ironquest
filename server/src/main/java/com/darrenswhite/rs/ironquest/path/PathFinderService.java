package com.darrenswhite.rs.ironquest.path;

import com.darrenswhite.rs.ironquest.path.algorithm.AlgorithmFactory;
import com.darrenswhite.rs.ironquest.path.algorithm.AlgorithmId;
import com.darrenswhite.rs.ironquest.path.algorithm.PathFinderAlgorithm;
import com.darrenswhite.rs.ironquest.player.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PathFinderService {

  private final AlgorithmFactory algorithmFactory;

  @Autowired
  public PathFinderService(AlgorithmFactory algorithmFactory) {
    this.algorithmFactory = algorithmFactory;
  }

  /**
   * Find the optimal {@link Path} for the specified {@link Player} using the given algorithm.
   *
   * @param player the player
   * @param id the id of the algorithm to use
   * @return the optimal path
   * @throws QuestNotFoundException if the optimal quest can not be found
   * @see PathFinder#find()
   * @see AlgorithmFactory#getAlgorithm(AlgorithmId)
   */
  public Path find(Player player, AlgorithmId id) throws QuestNotFoundException {
    PathFinderAlgorithm algorithm = createAlgorithm(id);

    return new PathFinder(player, algorithm).find();
  }

  /**
   * Get the {@link PathFinderAlgorithm} for the given id.
   *
   * @param id the algorithm id
   * @return the algorithm
   */
  private PathFinderAlgorithm createAlgorithm(AlgorithmId id) {
    return algorithmFactory.getAlgorithm(id);
  }
}
