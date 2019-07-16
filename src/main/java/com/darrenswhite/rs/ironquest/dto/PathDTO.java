package com.darrenswhite.rs.ironquest.dto;

import java.util.Set;

/**
 * Data Transfer Object for {@link com.darrenswhite.rs.ironquest.path.Path}.
 *
 * @author Darren S. White
 */
public class PathDTO {

  private final Set<ActionDTO> actions;

  private PathDTO(Set<ActionDTO> actions) {
    this.actions = actions;
  }

  public Set<ActionDTO> getActions() {
    return actions;
  }

  public static class Builder {

    private Set<ActionDTO> actions;

    public Builder withActions(Set<ActionDTO> actions) {
      this.actions = actions;
      return this;
    }

    public PathDTO build() {
      return new PathDTO(actions);
    }
  }
}
