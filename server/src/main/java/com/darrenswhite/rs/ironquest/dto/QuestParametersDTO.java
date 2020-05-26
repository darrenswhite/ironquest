package com.darrenswhite.rs.ironquest.dto;

import com.darrenswhite.rs.ironquest.quest.QuestAccessFilter;
import com.darrenswhite.rs.ironquest.quest.QuestTypeFilter;

public class QuestParametersDTO {

  /**
   * Player name to load data for.
   */
  private String name;

  /**
   * Filter quests by access.
   */
  private QuestAccessFilter accessFilter = QuestAccessFilter.ALL;

  /**
   * Filter quests by type.
   */
  private QuestTypeFilter typeFilter = QuestTypeFilter.ALL;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public QuestAccessFilter getAccessFilter() {
    return accessFilter;
  }

  public void setAccessFilter(QuestAccessFilter accessFilter) {
    this.accessFilter = accessFilter;
  }

  public QuestTypeFilter getTypeFilter() {
    return typeFilter;
  }

  public void setTypeFilter(QuestTypeFilter typeFilter) {
    this.typeFilter = typeFilter;
  }
}
