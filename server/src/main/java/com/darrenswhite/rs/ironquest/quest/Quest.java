package com.darrenswhite.rs.ironquest.quest;

import com.darrenswhite.rs.ironquest.dto.QuestDTO;
import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.quest.requirement.CombatRequirement;
import com.darrenswhite.rs.ironquest.quest.requirement.QuestPointsRequirement;
import com.darrenswhite.rs.ironquest.quest.requirement.QuestRequirement;
import com.darrenswhite.rs.ironquest.quest.requirement.QuestRequirements;
import com.darrenswhite.rs.ironquest.quest.requirement.Requirement;
import com.darrenswhite.rs.ironquest.quest.requirement.SkillRequirement;
import com.darrenswhite.rs.ironquest.quest.reward.QuestRewards;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A class representing a quest.
 *
 * @author Darren S. White
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Quest {

  private final int id;
  private final String title;
  private final String displayName;
  private final QuestAccess access;
  private final QuestType type;
  private final QuestRequirements requirements;
  private final QuestRewards rewards;

  public Quest(@JsonProperty("id") int id, @JsonProperty("title") String title,
      @JsonProperty("displayName") String displayName, @JsonProperty("access") QuestAccess access,
      @JsonProperty("type") QuestType type,
      @JsonProperty("requirements") QuestRequirements requirements,
      @JsonProperty("rewards") QuestRewards rewards) {
    this.id = id;
    this.title = title;
    this.displayName = displayName;
    this.access = access;
    this.type = type;
    this.requirements = requirements != null ? requirements : QuestRequirements.NONE;
    this.rewards = rewards != null ? rewards : QuestRewards.NONE;
  }

  Quest(Builder builder) {
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

  /**
   * Test if the player meets the {@link CombatRequirement}s, if any.
   *
   * @param player the player
   * @return <tt>true</tt> if the player meets the combat requirement; <tt>false</tt> otherwise
   */
  public boolean meetsCombatRequirement(Player player) {
    return requirements.getCombat() == null || requirements.getCombat().test(player);
  }

  /**
   * Test if the player meets the {@link QuestPointsRequirement}s, if any.
   *
   * @param player the player
   * @return <tt>true</tt> if the player meets the quest point requirement; <tt>false</tt> otherwise
   */
  public boolean meetsQuestPointRequirement(Player player) {
    return requirements.getQuestPoints() == null || requirements.getQuestPoints().test(player);
  }

  /**
   * Test if the player meets all {@link QuestRequirement}s.
   *
   * @param player the player
   * @return <tt>true</tt> if the player meets all quest requirements; <tt>false</tt> otherwise
   */
  public boolean meetsQuestRequirements(Player player) {
    return requirements.getQuests().stream().allMatch(r -> r.test(player));
  }

  /**
   * Test if the player meets all {@link SkillRequirement}s.
   *
   * @param player the player
   * @return <tt>true</tt> if the player meets all skill requirements; <tt>false</tt> otherwise
   */
  public boolean meetsSkillRequirements(Player player) {
    return requirements.getSkills().stream().allMatch(r -> r.test(player));
  }


  /**
   * Test if the player meets all {@link Requirement}s.
   *
   * @param player the player
   * @return <tt>true</tt> if the player meets all requirements; <tt>false</tt> otherwise
   */
  public boolean meetsAllRequirements(Player player) {
    return meetsCombatRequirement(player) && meetsQuestPointRequirement(player)
        && meetsQuestRequirements(player) && meetsSkillRequirements(player);
  }

  /**
   * Get remaining {@link Quest}s required to complete this {@link Quest}.
   *
   * @param player the player
   * @param recursive <tt>true</tt> to get requirements recursively; <tt>false</tt> otherwise
   * @return remaining quest requirements
   */
  public Set<Quest> getRemainingQuestRequirements(Player player, boolean recursive) {
    return getQuestRequirements(recursive).stream().filter(q -> !q.test(player))
        .map(QuestRequirement::getQuest).collect(Collectors.toSet());
  }

  /**
   * Get remaining {@link SkillRequirement}s to complete this {@link Quest}.
   *
   * @param player the player
   * @param recursive <tt>true</tt> to get requirements recursively; <tt>false</tt> otherwise
   * @return remaining skill requirements
   */
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

  /**
   * Get total levels required to complete this {@link Quest}.
   *
   * @param player the player
   * @param recursive <tt>true</tt> to get requirements recursively; <tt>false</tt> otherwise
   * @return total skill level requirements remaining
   */
  public int getTotalRemainingSkillRequirements(Player player, boolean recursive) {
    return getRemainingSkillRequirements(player, recursive).stream()
        .mapToInt(SkillRequirement::getLevel).sum();
  }

  /**
   * Test if this {@link Quest} is a placeholder.
   *
   * @return <tt>true</tt> if this quest is a placeholder; <tt>false</tt> otherwise
   */
  public boolean isPlaceholder() {
    return id < 0;
  }

  /**
   * Get all {@link QuestRequirements}s to complete this {@link Quest}.
   *
   * @param recursive <tt>true</tt> to get requirements recursively; <tt>false</tt> otherwise
   * @return quest requirements
   */
  public Set<QuestRequirement> getQuestRequirements(boolean recursive) {
    Set<QuestRequirement> questRequirements = new HashSet<>(requirements.getQuests());

    if (recursive) {
      questRequirements.addAll(requirements.getQuests().stream()
          .flatMap(qr -> qr.getQuest().getQuestRequirements(true).stream())
          .collect(Collectors.toSet()));
    }

    return questRequirements;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Quest)) {
      return false;
    }
    Quest quest = (Quest) o;
    return id == quest.id;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final int hashCode() {
    return Objects.hash(id);
  }

  /**
   * Returns a DTO for this {@link Quest}.
   *
   * @return the DTO
   */
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

    public Builder() {
    }

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
