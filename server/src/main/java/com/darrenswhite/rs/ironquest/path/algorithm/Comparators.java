package com.darrenswhite.rs.ironquest.path.algorithm;

import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.QuestPriority;
import com.darrenswhite.rs.ironquest.quest.Quest;
import com.darrenswhite.rs.ironquest.quest.requirement.SkillRequirement;
import java.util.Comparator;

/**
 * Convenient class for common {@link Comparator<Quest>} variants.
 */
public class Comparators {

  private Comparators() {
  }

  /**
   * Returns a {@link Comparator<Quest>} that compares by {@link QuestPriority}.
   *
   * @return a comparator that compares by priority
   * @see Player#getQuestPriority(Quest)
   */
  static Comparator<Quest> priority(Player player) {
    return Comparator.comparing(player::getQuestPriority);
  }

  /**
   * Returns a {@link Comparator<Quest>} that compares by remaining {@link SkillRequirement}s.
   *
   * @param player the player
   * @return the a comparator that compares by remaining skill requirements
   * @see Player#getTotalRemainingSkillRequirements(Quest, boolean)
   */
  static Comparator<Quest> remainingSkillRequirements(Player player) {
    return Comparator.comparing(quest -> player.getTotalRemainingSkillRequirements(quest, true));
  }

  /**
   * Compare two {@link Quest}s by remaining {@link SkillRequirement}s.
   *
   * @param player the player
   * @return the a comparator that compares by remaining skill requirements
   * @see Player#getTotalQuestRewards(Quest)
   */
  static Comparator<Quest> rewards(Player player) {
    return Comparator.comparing(player::getTotalQuestRewards).reversed();
  }
}
