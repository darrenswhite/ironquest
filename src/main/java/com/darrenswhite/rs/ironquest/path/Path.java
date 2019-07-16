package com.darrenswhite.rs.ironquest.path;

import com.darrenswhite.rs.ironquest.action.Action;
import com.darrenswhite.rs.ironquest.dto.PathDTO;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Path {

  private final Set<Action> actions;

  public Path(Set<Action> actions) {
    this.actions = actions;
  }

  public Set<Action> getActions() {
    return actions;
  }

  public PathDTO createDTO() {
    return new PathDTO.Builder().withActions(actions.stream().map(Action::createDTO)
        .collect(Collectors.toCollection(LinkedHashSet::new))).build();
  }
}
