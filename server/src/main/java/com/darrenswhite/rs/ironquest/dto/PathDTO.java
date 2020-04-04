package com.darrenswhite.rs.ironquest.dto;

import com.darrenswhite.rs.ironquest.path.Path;
import java.util.List;
import java.util.Objects;

/**
 * Data Transfer Object for {@link Path}.
 *
 * @author Darren S. White
 */
public class PathDTO {

  private final List<ActionDTO> actions;
  private final PathStatsDTO stats;

  PathDTO(Builder builder) {
    this.actions = builder.actions;
    this.stats = builder.stats;
  }

  public List<ActionDTO> getActions() {
    return actions;
  }

  public PathStatsDTO getStats() {
    return stats;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof PathDTO)) {
      return false;
    }
    PathDTO pathDTO = (PathDTO) o;
    return Objects.equals(actions, pathDTO.actions) && Objects.equals(stats, pathDTO.stats);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final int hashCode() {
    return Objects.hash(actions, stats);
  }

  public static class Builder {

    private List<ActionDTO> actions;
    private PathStatsDTO stats;

    public Builder withActions(List<ActionDTO> actions) {
      this.actions = actions;
      return this;
    }

    public Builder withStats(PathStatsDTO stats) {
      this.stats = stats;
      return this;
    }

    public PathDTO build() {
      return new PathDTO(this);
    }
  }
}
