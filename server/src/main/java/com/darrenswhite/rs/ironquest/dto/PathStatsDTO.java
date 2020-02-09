package com.darrenswhite.rs.ironquest.dto;

import com.darrenswhite.rs.ironquest.path.PathStats;

/**
 * Data Transfer Object for {@link PathStats}.
 *
 * @author Darren S. White
 */
public class PathStatsDTO {

  private final int percentComplete;

  PathStatsDTO(int percentComplete) {
    this.percentComplete = percentComplete;
  }

  public double getPercentComplete() {
    return percentComplete;
  }

  public static class Builder {

    private int percentComplete;

    public Builder withPercentComplete(int percentComplete) {
      this.percentComplete = percentComplete;
      return this;
    }

    public PathStatsDTO build() {
      return new PathStatsDTO(percentComplete);
    }
  }
}
