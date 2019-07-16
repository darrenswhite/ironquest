package com.darrenswhite.rs.ironquest.dto;

import com.darrenswhite.rs.ironquest.action.ActionType;
import com.darrenswhite.rs.ironquest.player.Skill;
import java.util.Set;

/**
 * Data Transfer Object for {@link com.darrenswhite.rs.ironquest.action.LampAction}.
 *
 * @author Darren S. White
 */
public class LampActionDTO extends ActionDTO {

  private final QuestDTO quest;
  private final LampRewardDTO lampReward;
  private final Set<Skill> skills;

  private LampActionDTO(ActionType type, PlayerDTO player, boolean future, String message,
      QuestDTO quest, LampRewardDTO lampReward, Set<Skill> skills) {
    super(type, player, future, message);
    this.quest = quest;
    this.lampReward = lampReward;
    this.skills = skills;
  }

  public QuestDTO getQuest() {
    return quest;
  }

  public LampRewardDTO getLampReward() {
    return lampReward;
  }

  public Set<Skill> getSkills() {
    return skills;
  }

  public static class Builder {

    private ActionType type;
    private PlayerDTO player;
    private boolean future;
    private String message;
    private QuestDTO quest;
    private LampRewardDTO lampReward;
    private Set<Skill> skills;

    public Builder withType(ActionType type) {
      this.type = type;
      return this;
    }

    public Builder withPlayer(PlayerDTO player) {
      this.player = player;
      return this;
    }

    public Builder withFuture(boolean future) {
      this.future = future;
      return this;
    }

    public Builder withMessage(String message) {
      this.message = message;
      return this;
    }

    public Builder withQuest(QuestDTO quest) {
      this.quest = quest;
      return this;
    }

    public Builder withLampReward(LampRewardDTO lampReward) {
      this.lampReward = lampReward;
      return this;
    }

    public Builder withSkills(Set<Skill> skills) {
      this.skills = skills;
      return this;
    }

    public LampActionDTO build() {
      return new LampActionDTO(type, player, future, message, quest, lampReward, skills);
    }
  }
}
