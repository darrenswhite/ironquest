package com.darrenswhite.rs.ironquest.dto;

/**
 * Data Transfer Object for {@link com.darrenswhite.rs.ironquest.path.PathStats}.
 *
 * @author Darren S. White
 */
public class PathStatsDTO {

  private final int percentComplete;

  private PathStatsDTO(int percentComplete) {
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
