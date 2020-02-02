package com.darrenswhite.rs.ironquest.dto;

import java.util.List;

/**
 * Data Transfer Object for {@link com.darrenswhite.rs.ironquest.path.Path}.
 *
 * @author Darren S. White
 */
public class PathDTO {

  private final List<ActionDTO> actions;
  private final PathStatsDTO stats;

  PathDTO(List<ActionDTO> actions, PathStatsDTO stats) {
    this.actions = actions;
    this.stats = stats;
  }

  public List<ActionDTO> getActions() {
    return actions;
  }

  public PathStatsDTO getStats() {
    return stats;
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
      return new PathDTO(actions, stats);
    }
  }
}
