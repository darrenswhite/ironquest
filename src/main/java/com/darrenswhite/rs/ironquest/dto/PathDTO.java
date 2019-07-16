package com.darrenswhite.rs.ironquest.dto;

import java.util.Set;

/**
 * Data Transfer Object for {@link com.darrenswhite.rs.ironquest.path.Path}.
 *
 * @author Darren S. White
 */
public class PathDTO {

  private final Set<ActionDTO> actions;
  private final PathStatsDTO stats;

  private PathDTO(Set<ActionDTO> actions, PathStatsDTO stats) {
    this.actions = actions;
    this.stats = stats;
  }

  public Set<ActionDTO> getActions() {
    return actions;
  }

  public PathStatsDTO getStats() {
    return stats;
  }

  public static class Builder {

    private Set<ActionDTO> actions;
    private PathStatsDTO stats;

    public Builder withActions(Set<ActionDTO> actions) {
      this.actions = actions;
      return this;
    }

    public Builder withStats(PathStatsDTO stats) {
      this.stats = stats;
      return this;
    }

    public PathDTO build() {
      return new PathDTO(actions, stats);
    }
  }
}
