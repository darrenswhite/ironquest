package com.darrenswhite.rs.ironquest.quest.reward;

import com.darrenswhite.rs.ironquest.player.Skill;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Darren S. White
 */
@JsonDeserialize(builder = QuestRewards.Builder.class)
public class QuestRewards {

  private final Map<Skill, Double> xp;
  private final Set<LampReward> lamps;
  private final int questPoints;

  private QuestRewards(Builder builder) {
    this.xp = builder.xp;
    this.lamps = builder.lamps;
    this.questPoints = builder.questPoints;
  }

  public Map<Skill, Double> getXp() {
    return xp;
  }

  public Set<LampReward> getLamps() {
    return lamps;
  }

  public int getQuestPoints() {
    return questPoints;
  }

  public static class Builder {

    private Map<Skill, Double> xp = Collections.emptyMap();
    private Set<LampReward> lamps = Collections.emptySet();
    private int questPoints = 0;

    @JsonDeserialize(as = LinkedHashMap.class)
    public Builder withXp(Map<Skill, Double> xp) {
      this.xp = xp;
      return this;
    }

    @JsonDeserialize(as = LinkedHashSet.class)
    public Builder withLamps(Set<LampReward> lamps) {
      this.lamps = lamps;
      return this;
    }

    public Builder withQuestPoints(int questPoints) {
      this.questPoints = questPoints;
      return this;
    }

    public QuestRewards build() {
      return new QuestRewards(this);
    }
  }
}
