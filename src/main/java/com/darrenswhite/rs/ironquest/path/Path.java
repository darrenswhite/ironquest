package com.darrenswhite.rs.ironquest.path;

import com.darrenswhite.rs.ironquest.action.Action;
import com.darrenswhite.rs.ironquest.dto.ActionDTO;
import com.darrenswhite.rs.ironquest.dto.PathDTO;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Path containing {@link Action}'s and {@link PathStats}
 *
 * @author Darren S. White
 */
public class Path {

  private final List<Action> actions;
  private final PathStats stats;

  public Path(List<Action> actions, PathStats stats) {
    this.actions = actions;
    this.stats = stats;
  }

  public List<Action> getActions() {
    return actions;
  }

  public PathStats getStats() {
    return stats;
  }

  public PathDTO createDTO() {
    List<ActionDTO> actionDTOs = getActions().stream().map(Action::createDTO)
        .collect(Collectors.toCollection(LinkedList::new));

    return new PathDTO.Builder().withActions(actionDTOs).withStats(getStats().createDTO()).build();
  }
}
