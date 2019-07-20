package com.darrenswhite.rs.ironquest.quest.requirement;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Darren S. White
 */
@JsonDeserialize(builder = QuestRequirements.Builder.class)
public class QuestRequirements {

  private final CombatRequirement combat;
  private final QuestPointsRequirement questPoints;
  private final Set<QuestRequirement> quests;
  private final Set<SkillRequirement> skills;

  private QuestRequirements(Builder builder) {
    this.combat = builder.combat;
    this.questPoints = builder.questPoints;
    this.quests = builder.quests;
    this.skills = builder.skills;
  }

  public CombatRequirement getCombat() {
    return combat;
  }

  public QuestPointsRequirement getQuestPoints() {
    return questPoints;
  }

  public Set<QuestRequirement> getQuests() {
    return quests;
  }

  public Set<SkillRequirement> getSkills() {
    return skills;
  }

  public static class Builder {

    private CombatRequirement combat;
    private QuestPointsRequirement questPoints;
    private Set<QuestRequirement> quests = Collections.emptySet();
    private Set<SkillRequirement> skills = Collections.emptySet();

    public Builder withCombat(CombatRequirement combat) {
      this.combat = combat;
      return this;
    }

    public Builder withQuestPoints(QuestPointsRequirement questPoints) {
      this.questPoints = questPoints;
      return this;
    }

    @JsonDeserialize(as = LinkedHashSet.class)
    public Builder withQuests(Set<QuestRequirement> quests) {
      this.quests = quests;
      return this;
    }

    @JsonDeserialize(as = LinkedHashSet.class)
    public Builder withSkills(Set<SkillRequirement> skills) {
      this.skills = skills;
      return this;
    }

    public QuestRequirements build() {
      return new QuestRequirements(this);
    }
  }
}
