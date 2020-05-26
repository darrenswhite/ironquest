package com.darrenswhite.rs.ironquest.path.algorithm;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * {@link Component} factory for {@link PathFinderAlgorithm}.
 */
@Component
public class AlgorithmFactory {

  private final Map<AlgorithmId, PathFinderAlgorithm> algorithms;

  @Autowired
  public AlgorithmFactory(Set<PathFinderAlgorithm> algorithms) {
    this.algorithms = algorithms.stream()
        .collect(Collectors.toMap(PathFinderAlgorithm::getId, Function.identity()));
  }

  /**
   * Returns the {@link PathFinderAlgorithm} with the given {@link AlgorithmId}.
   *
   * @param id the algorithm id
   * @return the algorithm; or null
   */
  public PathFinderAlgorithm getAlgorithm(AlgorithmId id) {
    return algorithms.get(id);
  }
}
