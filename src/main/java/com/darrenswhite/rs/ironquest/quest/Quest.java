package com.darrenswhite.rs.ironquest.quest;

import com.darrenswhite.rs.ironquest.dto.QuestDTO;
import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.quest.Quest.Builder;
import com.darrenswhite.rs.ironquest.quest.requirement.QuestRequirement;
import com.darrenswhite.rs.ironquest.quest.requirement.QuestRequirements;
import com.darrenswhite.rs.ironquest.quest.requirement.SkillRequirement;
import com.darrenswhite.rs.ironquest.quest.reward.QuestRewards;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Darren S. White
 */
@JsonDeserialize(builder = Builder.class)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Quest {

  private final int id;
  private final String title;
  private final String displayName;
  private final QuestAccess access;
  private final QuestType type;
  private final QuestRequirements requirements;
  private final QuestRewards rewards;

  private Quest(Builder builder) {
    this.id = builder.id;
    this.title = builder.title;
    this.displayName = builder.displayName;
    this.access = builder.access;
    this.type = builder.type;
    this.requirements = builder.requirements;
    this.rewards = builder.rewards;
  }

  public int getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getDisplayName() {
    return displayName != null ? displayName : title;
  }

  public QuestAccess getAccess() {
    return access;
  }

  public QuestType getType() {
    return type;
  }

  public QuestRequirements getRequirements() {
    return requirements;
  }

  public QuestRewards getRewards() {
    return rewards;
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
    Set<SkillRequirement> remainingSkillRequirements = new LinkedHashSet<>();

    remainingSkillRequirements = SkillRequirement.merge(remainingSkillRequirements,
        requirements.getSkills().stream().filter(sr -> !sr.test(player))
            .collect(Collectors.toCollection(LinkedHashSet::new)));

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

  public static class Builder {

    private int id;
    private String title;
    private String displayName;
    private QuestAccess access;
    private QuestType type;
    private QuestRequirements requirements = QuestRequirements.NONE;
    private QuestRewards rewards = QuestRewards.NONE;

    @JsonCreator
    public Builder() {
    }

    @JsonCreator
    public Builder(int id) {
      this.id = id;
    }

    public Builder withId(int id) {
      this.id = id;
      return this;
    }

    public Builder withTitle(String title) {
      this.title = title;
      return this;
    }

    public Builder withDisplayName(String displayName) {
      this.displayName = displayName;
      return this;
    }

    public Builder withAccess(QuestAccess access) {
      this.access = access;
      return this;
    }

    public Builder withType(QuestType type) {
      this.type = type;
      return this;
    }

    public Builder withRequirements(QuestRequirements requirements) {
      this.requirements = requirements;
      return this;
    }

    public Builder withRewards(QuestRewards rewards) {
      this.rewards = rewards;
      return this;
    }

    public Quest build() {
      return new Quest(this);
    }
  }
}
