package com.darrenswhite.rs.ironquest.path;

import com.darrenswhite.rs.ironquest.action.Action;
import java.util.Set;

public class Path {

  private final Set<Action> actions;

  public Path(Set<Action> actions) {
    this.actions = actions;
  }

  public Set<Action> getActions() {
    return actions;
  }
}
