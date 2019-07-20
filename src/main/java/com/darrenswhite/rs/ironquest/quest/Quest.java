package com.darrenswhite.rs.ironquest.quest;

import com.darrenswhite.rs.ironquest.dto.QuestDTO;
import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.quest.requirement.QuestRequirement;
import com.darrenswhite.rs.ironquest.quest.requirement.QuestRequirements;
import com.darrenswhite.rs.ironquest.quest.requirement.SkillRequirement;
import com.darrenswhite.rs.ironquest.quest.reward.QuestRewards;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.util.HashSet;
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
  private QuestType type;
  private QuestRequirements requirements = new QuestRequirements.Builder().build();
  private QuestRewards rewards = new QuestRewards.Builder().build();

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

  public QuestType getType() {
    return type;
  }

  public void setType(QuestType type) {
    this.type = type;
  }

  public QuestRequirements getRequirements() {
    return requirements;
  }

  public void setRequirements(QuestRequirements requirements) {
    this.requirements = requirements;
  }

  public QuestRewards getRewards() {
    return rewards;
  }

  public void setRewards(QuestRewards rewards) {
    this.rewards = rewards;
  }

  public boolean meetsCombatRequirement(Player player) {
    return requirements.getCombat() == null || requirements.getCombat().test(player);
  }

  public boolean meetsQuestPointRequirement(Player player) {
    return requirements.getQuestPoints() == null || requirements.getQuestPoints().test(player);
  }

  public boolean meetsQuestRequirements(Player player) {
    return requirements.getQuests().stream().allMatch(r -> r.test(player));
  }

  public boolean meetsSkillRequirements(Player player) {
    return requirements.getSkills().stream().allMatch(r -> r.test(player));
  }

  public boolean meetsAllRequirements(Player player) {
    return meetsCombatRequirement(player) && meetsQuestPointRequirement(player)
        && meetsQuestRequirements(player) && meetsSkillRequirements(player);
  }

  public Set<Quest> getRemainingQuestRequirements(Player player, boolean recursive) {
    Set<Quest> remainingQuestRequirements = requirements.getQuests().stream()
        .filter(q -> !q.test(player)).map(QuestRequirement::getQuest).collect(Collectors.toSet());

    if (recursive) {
      for (Quest quest : remainingQuestRequirements) {
        remainingQuestRequirements.addAll(quest.getRemainingQuestRequirements(player, true));
      }
    }

    return remainingQuestRequirements;
  }

  public Set<SkillRequirement> getRemainingSkillRequirements(Player player, boolean recursive) {
    Set<SkillRequirement> remainingSkillRequirements = new HashSet<>();

    remainingSkillRequirements = SkillRequirement.merge(remainingSkillRequirements,
        requirements.getSkills().stream().filter(sr -> !sr.test(player))
            .collect(Collectors.toSet()));

    if (recursive) {
      Set<Quest> remainingQuestRequirements = getRemainingQuestRequirements(player, true);

      for (Quest quest : remainingQuestRequirements) {
        remainingSkillRequirements = SkillRequirement
            .merge(remainingSkillRequirements, quest.getRemainingSkillRequirements(player, true));
      }
    }

    return remainingSkillRequirements;
  }

  public int getTotalRemainingSkillRequirements(Player player, boolean recursive) {
    return getRemainingSkillRequirements(player, recursive).stream()
        .mapToInt(SkillRequirement::getLevel).sum();
  }

  public boolean isPlaceholder() {
    return id < 0;
  }

  public Set<QuestRequirement> getQuestRequirements(boolean recursive) {
    Set<QuestRequirement> questRequirements = new HashSet<>(requirements.getQuests());

    if (recursive) {
      questRequirements.addAll(requirements.getQuests().stream()
          .flatMap(qr -> qr.getQuest().getQuestRequirements(true).stream())
          .collect(Collectors.toSet()));
    }

    return questRequirements;
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
