package com.darrenswhite.rs.ironquest.path;

import com.darrenswhite.rs.ironquest.dto.PathStatsDTO;
import java.util.Objects;

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

  /**
   * {@inheritDoc}
   */
  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof PathStats)) {
      return false;
    }
    PathStats pathStats = (PathStats) o;
    return percentComplete == pathStats.percentComplete;
  }

  @Override
  public final int hashCode() {
    return Objects.hash(percentComplete);
  }
}
