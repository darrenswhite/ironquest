package com.darrenswhite.rs.ironquest.dto;

import com.darrenswhite.rs.ironquest.path.PathStats;
import java.util.Objects;

/**
 * Data Transfer Object for {@link PathStats}.
 *
 * @author Darren S. White
 */
public class PathStatsDTO {

  private final int percentComplete;

  PathStatsDTO(Builder builder) {
    this.percentComplete = builder.percentComplete;
  }

  public double getPercentComplete() {
    return percentComplete;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof PathStatsDTO)) {
      return false;
    }
    PathStatsDTO that = (PathStatsDTO) o;
    return percentComplete == that.percentComplete;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final int hashCode() {
    return Objects.hash(percentComplete);
  }

  public static class Builder {

    private int percentComplete;

    public Builder withPercentComplete(int percentComplete) {
      this.percentComplete = percentComplete;
      return this;
    }

    public PathStatsDTO build() {
      return new PathStatsDTO(this);
    }
  }
}
