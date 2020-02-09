package com.darrenswhite.rs.ironquest.path;

import com.darrenswhite.rs.ironquest.dto.PathStatsDTO;

/**
 * Additional stats for a {@link Path}.
 *
 * @author Darren S. White
 */
public class PathStats {

  private final int percentComplete;

  public PathStats(int percentComplete) {
    this.percentComplete = percentComplete;
  }

  /**
   * The initial percentage of quests completed at the start of the path, before completing any
   * actions.
   *
   * @return percentage of initial quests complete
   */
  public double getPercentComplete() {
    return percentComplete;
  }

  /**
   * Returns a DTO for this {@link PathStats}.
   *
   * @return the DTO
   */
  public PathStatsDTO createDTO() {
    return new PathStatsDTO.Builder().withPercentComplete(percentComplete).build();
  }
}
