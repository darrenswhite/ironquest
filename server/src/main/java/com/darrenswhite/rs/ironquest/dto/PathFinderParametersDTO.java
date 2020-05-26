package com.darrenswhite.rs.ironquest.dto;

import com.darrenswhite.rs.ironquest.path.PathFinder;
import com.darrenswhite.rs.ironquest.path.algorithm.AlgorithmId;
import com.darrenswhite.rs.ironquest.player.QuestPriority;
import com.darrenswhite.rs.ironquest.player.Skill;
import com.darrenswhite.rs.ironquest.quest.QuestAccessFilter;
import com.darrenswhite.rs.ironquest.quest.QuestTypeFilter;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Data Transfer Object for {@link PathFinder} parameters.
 *
 * @author Darren S. White
 */
public class PathFinderParametersDTO {

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

  /**
   * Toggle ironman requirements. Set to <tt>true</tt> to enable ironman quest requirements;
   * <tt>false</tt> otherwise.
   */
  private boolean ironman;

  /**
   * Toggle recommended requirements. Set to <tt>true</tt> to enable recommended quest
   * requirements;
   * <tt>false</tt> otherwise.
   */
  private boolean recommended;

  /**
   * Set of skills to use on lamps.
   */
  private Set<Skill> lampSkills = new LinkedHashSet<>();

  /**
   * Prioritise quests by id.
   */
  private Map<Integer, QuestPriority> questPriorities = new LinkedHashMap<>();

  /**
   * Specify the algorithm to use.
   */
  private AlgorithmId algorithm;

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

  public boolean isIronman() {
    return ironman;
  }

  public void setIronman(boolean ironman) {
    this.ironman = ironman;
  }

  public boolean isRecommended() {
    return recommended;
  }

  public void setRecommended(boolean recommended) {
    this.recommended = recommended;
  }

  public Set<Skill> getLampSkills() {
    return lampSkills;
  }

  public void setLampSkills(Set<Skill> lampSkills) {
    this.lampSkills = lampSkills;
  }

  public Map<Integer, QuestPriority> getQuestPriorities() {
    return questPriorities;
  }

  public void setQuestPriorities(Map<Integer, QuestPriority> questPriorities) {
    this.questPriorities = questPriorities;
  }

  public AlgorithmId getAlgorithm() {
    return algorithm;
  }

  public void setAlgorithm(AlgorithmId algorithm) {
    this.algorithm = algorithm;
  }
}
