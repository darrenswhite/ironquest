package com.darrenswhite.rs.ironquest.quest;

import com.darrenswhite.rs.ironquest.player.QuestEntry;
import com.darrenswhite.rs.ironquest.player.QuestPriority;
import com.darrenswhite.rs.ironquest.player.QuestStatus;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Darren S. White
 */
public class Quests {

  private final Set<Quest> questSet;

  public Quests(Set<Quest> questSet) {
    this.questSet = questSet;
  }

  public Set<QuestEntry> createQuestEntries(Map<Integer, QuestPriority> questPriorities,
      QuestAccessFilter accessFilter, QuestTypeFilter typeFilter) {
    Predicate<Quest> accessAndTypeFilter = questMatchesAccessFilter(accessFilter)
        .and(questMatchesTypeFilter(typeFilter));
    Set<Integer> questRequirements = getQuestRequirements(accessAndTypeFilter);

    return questSet.stream().filter(accessAndTypeFilter.or(questIsRequirement(questRequirements)))
        .map(quest -> createQuestEntry(quest, questPriorities)).collect(Collectors.toSet());
  }

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

  private Predicate<Quest> questIsRequirement(Set<Integer> questRequirements) {
    return quest -> questRequirements.contains(quest.getId());
  }

  private Set<Integer> getQuestRequirements(Predicate<Quest> questFilter) {
    return questSet.stream().filter(questFilter)
        .flatMap(quest -> quest.getQuestRequirements(true).stream())
        .map(qr -> qr.getQuest().getId()).collect(Collectors.toSet());
  }

  private QuestEntry createQuestEntry(Quest quest, Map<Integer, QuestPriority> questPriorities) {
    QuestPriority priority = questPriorities.getOrDefault(quest.getId(), QuestPriority.NORMAL);

    return new QuestEntry(quest, QuestStatus.NOT_STARTED, priority);
  }
}
