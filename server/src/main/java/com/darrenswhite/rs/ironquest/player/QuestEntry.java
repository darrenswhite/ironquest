package com.darrenswhite.rs.ironquest.player;

import com.darrenswhite.rs.ironquest.quest.Quest;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A quest entry for a {@link Player}.
 *
 * @author Darren S. White
 */
public class QuestEntry {

  private final Set<Set<Skill>> previousLampSkills = new HashSet<>();
  private final Quest quest;
  private QuestStatus status;
  private QuestPriority priority;

  public QuestEntry(Quest quest, QuestStatus status, QuestPriority priority) {
    this.quest = quest;
    this.status = status;
    this.priority = priority;
  }

  public Quest getQuest() {
    return quest;
  }

  public QuestStatus getStatus() {
    return status;
  }

  public void setStatus(QuestStatus status) {
    this.status = status;
  }

  public Set<Set<Skill>> getPreviousLampSkills() {
    return previousLampSkills;
  }

  public QuestPriority getPriority() {
    return priority;
  }

  public void setPriority(QuestPriority priority) {
    this.priority = priority;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    QuestEntry that = (QuestEntry) o;
    return quest.equals(that.quest);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects.hash(quest);
  }
}
