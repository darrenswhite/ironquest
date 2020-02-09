package com.darrenswhite.rs.ironquest.quest;

import com.darrenswhite.rs.ironquest.player.QuestEntry;
import com.darrenswhite.rs.ironquest.player.QuestPriority;
import com.darrenswhite.rs.ironquest.player.QuestStatus;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A class representing a {@link Set} of {@link Quest}s.
 *
 * @author Darren S. White
 */
public class Quests {

  private final Set<Quest> questSet;

  public Quests(Set<Quest> questSet) {
    this.questSet = questSet;
  }

  public Set<Quest> getQuests() {
    return questSet;
  }

  /**
   * Create a {@link Set} of entries for the quests.
   *
   * @param questPriorities the priorities to use for each quest
   * @param accessFilter the access filter for the quests
   * @param typeFilter the type filter for the quests
   * @return set of quest entries
   */
  public Set<QuestEntry> createQuestEntries(Map<Integer, QuestPriority> questPriorities,
      QuestAccessFilter accessFilter, QuestTypeFilter typeFilter) {
    Predicate<Quest> accessAndTypeFilter = questMatchesAccessFilter(accessFilter)
        .and(questMatchesTypeFilter(typeFilter));
    Set<Integer> questRequirements = getQuestRequirements(accessAndTypeFilter);

    return questSet.stream().filter(accessAndTypeFilter.or(questIsRequirement(questRequirements)))
        .map(quest -> createQuestEntry(quest, questPriorities)).collect(Collectors.toSet());
  }

  /**
   * Returns a {@link Predicate} used to test if a {@link Quest} matches the specified {@link
   * QuestTypeFilter}.
   *
   * @return predicate to test if a quest matches the type filter
   * @throws IllegalArgumentException for invalid quest types
   */
  private Predicate<Quest> questMatchesTypeFilter(QuestTypeFilter typeFilter) {
    return quest -> {
      boolean valid;

      switch (quest.getType()) {
        case QUEST:
          valid = typeFilter.isQuests();
          break;
        case MINIQUEST:
          valid = typeFilter.isMiniquests();
          break;
        case SAGA:
          valid = typeFilter.isSagas();
          break;
        default:
          throw new IllegalArgumentException("Unknown quest type: " + quest.getType());
      }

      return valid;
    };
  }

  /**
   * Returns a {@link Predicate} used to test if a {@link Quest} matches the specified {@link
   * QuestAccessFilter}.
   *
   * @return predicate to test if a quest matches the type filter
   * @throws IllegalArgumentException for invalid quest access
   */
  private Predicate<Quest> questMatchesAccessFilter(QuestAccessFilter accessFilter) {
    return quest -> {
      boolean valid;

      switch (quest.getAccess()) {
        case FREE:
          valid = accessFilter.isFree();
          break;
        case MEMBERS:
          valid = accessFilter.isMembers();
          break;
        default:
          throw new IllegalArgumentException("Unknown quest access: " + quest.getAccess());
      }

      return valid;
    };
  }

  /**
   * Returns a {@link Predicate} used to test if a {@link Quest} is a required quest specified by
   * the {@link Set} of ids.
   *
   * @param questRequirements the ids of quests required
   * @return predicate to test if a quest is required for another quest
   */
  private Predicate<Quest> questIsRequirement(Set<Integer> questRequirements) {
    return quest -> questRequirements.contains(quest.getId());
  }

  /**
   * Returns a {@link Set} of {@link Quest} ids of all quest requirements, excluding filtered
   * quests.
   *
   * @param questFilter predicate to filter quests
   * @return set of quest ids
   */
  private Set<Integer> getQuestRequirements(Predicate<Quest> questFilter) {
    return questSet.stream().filter(questFilter)
        .flatMap(quest -> quest.getQuestRequirements(true).stream())
        .map(qr -> qr.getQuest().getId()).collect(Collectors.toSet());
  }

  /**
   * Creates a {@link QuestEntry} for the specified {@link Quest}.
   *
   * If the quest id is found in the <tt>questPriorities</tt> map, then that priority will be used.
   * Otherwise priority will be {@link QuestPriority#NORMAL}.
   *
   * @param quest the quest to create an entry for
   * @param questPriorities the quest priorities
   * @return the quest entry
   */
  private QuestEntry createQuestEntry(Quest quest, Map<Integer, QuestPriority> questPriorities) {
    QuestPriority priority = questPriorities.getOrDefault(quest.getId(), QuestPriority.NORMAL);

    return new QuestEntry(quest, QuestStatus.NOT_STARTED, priority);
  }
}
