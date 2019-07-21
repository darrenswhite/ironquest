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

  public double getPercentComplete() {
    return percentComplete;
  }

  public PathStatsDTO createDTO() {
    return new PathStatsDTO.Builder().withPercentComplete(percentComplete).build();
  }
}
