package com.darrenswhite.rs.ironquest.path;

import com.darrenswhite.rs.ironquest.action.Action;
import com.darrenswhite.rs.ironquest.dto.ActionDTO;
import com.darrenswhite.rs.ironquest.dto.PathDTO;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Path containing {@link Action}'s and {@link PathStats}
 *
 * @author Darren S. White
 */
public class Path {

  private final Set<Action> actions;
  private final PathStats stats;

  public Path(Set<Action> actions, PathStats stats) {
    this.actions = actions;
    this.stats = stats;
  }

  public Set<Action> getActions() {
    return actions;
  }

  public PathStats getStats() {
    return stats;
  }

  public PathDTO createDTO() {
    Set<ActionDTO> actionDTOs = getActions().stream().map(Action::createDTO)
        .collect(Collectors.toCollection(LinkedHashSet::new));

    return new PathDTO.Builder().withActions(actionDTOs).withStats(getStats().createDTO()).build();
  }
}
