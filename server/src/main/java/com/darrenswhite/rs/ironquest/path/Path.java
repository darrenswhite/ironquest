package com.darrenswhite.rs.ironquest.path;

import com.darrenswhite.rs.ironquest.action.Action;
import com.darrenswhite.rs.ironquest.dto.ActionDTO;
import com.darrenswhite.rs.ironquest.dto.PathDTO;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Class encapsulating a {@link List} of {@link Action}s and {@link PathStats}.
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

  /**
   * Returns the {@link List} of {@link Action}s.
   *
   * @return the actions
   */
  public List<Action> getActions() {
    return actions;
  }

  /**
   * Returns the {@link PathStats}.
   *
   * @return the path stats
   */
  public PathStats getStats() {
    return stats;
  }

  /**
   * Returns a DTO for this {@link Path}.
   *
   * @return the DTO
   */
  public PathDTO createDTO() {
    List<ActionDTO> actionDTOs = getActions().stream().map(Action::createDTO)
        .collect(Collectors.toCollection(LinkedList::new));

    return new PathDTO.Builder().withActions(actionDTOs).withStats(getStats().createDTO()).build();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Path)) {
      return false;
    }
    Path path = (Path) o;
    return Objects.equals(actions, path.actions) && Objects.equals(stats, path.stats);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final int hashCode() {
    return Objects.hash(actions, stats);
  }
}
