package com.darrenswhite.rs.ironquest.quest;

import com.darrenswhite.rs.ironquest.dto.QuestDTO;
import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.Skill;
import com.darrenswhite.rs.ironquest.quest.requirement.CombatRequirement;
import com.darrenswhite.rs.ironquest.quest.requirement.QuestPointsRequirement;
import com.darrenswhite.rs.ironquest.quest.requirement.QuestRequirement;
import com.darrenswhite.rs.ironquest.quest.requirement.SkillRequirement;
import com.darrenswhite.rs.ironquest.quest.reward.LampReward;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Darren S. White
 */
@JsonIdentityInfo(scope = QuestRequirement.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Quest {

  private int id;
  private String title;
  private String displayName;
  private QuestAccess access;
  private CombatRequirement combatRequirement;
  private QuestPointsRequirement questPointsRequirement;
  private Set<QuestRequirement> questRequirements = new HashSet<>();
  private Set<SkillRequirement> skillRequirements = new HashSet<>();
  private Map<Skill, Double> xpRewards = new EnumMap<>(Skill.class);
  private Set<LampReward> lampRewards = new HashSet<>();
  private int questPointsReward;
  private QuestType type;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDisplayName() {
    return displayName != null ? displayName : title;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public QuestAccess getAccess() {
    return access;
  }

  public void setAccess(QuestAccess access) {
    this.access = access;
  }

  public CombatRequirement getCombatRequirement() {
    return combatRequirement;
  }

  public void setCombatRequirement(CombatRequirement combatRequirement) {
    this.combatRequirement = combatRequirement;
  }

  public QuestPointsRequirement getQuestPointsRequirement() {
    return questPointsRequirement;
  }

  public void setQuestPointsRequirement(QuestPointsRequirement questPointsRequirement) {
    this.questPointsRequirement = questPointsRequirement;
  }

  public Set<QuestRequirement> getQuestRequirements() {
    return questRequirements;
  }

  public void setQuestRequirements(Set<QuestRequirement> questRequirements) {
    this.questRequirements = questRequirements;
  }

  public Set<SkillRequirement> getSkillRequirements() {
    return skillRequirements;
  }

  public void setSkillRequirements(Set<SkillRequirement> skillRequirements) {
    this.skillRequirements = skillRequirements;
  }

  public int getQuestPointsReward() {
    return questPointsReward;
  }

  public void setQuestPointsReward(int questPointsReward) {
    this.questPointsReward = questPointsReward;
  }

  public Map<Skill, Double> getXpRewards() {
    return xpRewards;
  }

  public void setXpRewards(Map<Skill, Double> xpRewards) {
    this.xpRewards = xpRewards;
  }

  public Set<LampReward> getLampRewards() {
    return lampRewards;
  }

  public void setLampRewards(Set<LampReward> lampRewards) {
    this.lampRewards = lampRewards;
  }

  public QuestType getType() {
    return type;
  }

  public void setType(QuestType type) {
    this.type = type;
  }

  public boolean meetsCombatRequirement(Player player) {
    return combatRequirement == null || combatRequirement.test(player);
  }

  public boolean meetsQuestPointRequirement(Player player) {
    return questPointsRequirement == null || questPointsRequirement.test(player);
  }

  public boolean meetsQuestRequirements(Player player) {
    return questRequirements.stream().allMatch(r -> r.test(player));
  }

  public boolean meetsSkillRequirements(Player player) {
    return skillRequirements.stream().allMatch(r -> r.test(player));
  }

  public boolean meetsAllRequirements(Player player) {
    return meetsCombatRequirement(player) && meetsQuestPointRequirement(player)
        && meetsQuestRequirements(player) && meetsSkillRequirements(player);
  }

  public Set<Quest> getRemainingQuestRequirements(Player player, boolean recursive) {
    Set<Quest> requirements = questRequirements.stream().filter(q -> !q.test(player))
        .map(QuestRequirement::getQuest).collect(Collectors.toSet());

    if (recursive) {
      for (Quest quest : requirements) {
        requirements.addAll(quest.getRemainingQuestRequirements(player, true));
      }
    }

    return requirements;
  }

  public Set<SkillRequirement> getRemainingSkillRequirements(Player player, boolean recursive) {
    Set<SkillRequirement> requirements = new HashSet<>();

    requirements = SkillRequirement.merge(requirements,
        skillRequirements.stream().filter(sr -> !sr.test(player)).collect(Collectors.toSet()));

    if (recursive) {
      Set<Quest> remainingQuestRequirements = getRemainingQuestRequirements(player, true);

      for (Quest quest : remainingQuestRequirements) {
        requirements = SkillRequirement
            .merge(requirements, quest.getRemainingSkillRequirements(player, true));
      }
    }

    return requirements;
  }

  public int getTotalRemainingSkillRequirements(Player player, boolean recursive) {
    return getRemainingSkillRequirements(player, recursive).stream()
        .mapToInt(SkillRequirement::getLevel).sum();
  }

  public boolean isPlaceholder() {
    return id < 0;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Quest quest = (Quest) o;
    return id == quest.id;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  public QuestDTO createDTO() {
    return new QuestDTO.Builder().withDisplayName(getDisplayName()).build();
  }
}
