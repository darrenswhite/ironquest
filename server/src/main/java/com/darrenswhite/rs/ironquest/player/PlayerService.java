package com.darrenswhite.rs.ironquest.player;

import com.darrenswhite.rs.ironquest.quest.Quest;
import com.darrenswhite.rs.ironquest.quest.QuestAccessFilter;
import com.darrenswhite.rs.ironquest.quest.QuestRepository;
import com.darrenswhite.rs.ironquest.quest.QuestTypeFilter;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * {@link Service} for creating {@link Player}s.
 *
 * @author Darren S. White
 */
@Service
public class PlayerService {

  private static final Logger LOG = LogManager.getLogger(PlayerService.class);

  private final QuestRepository questRepository;
  private final HiscoreService hiscoreService;
  private final RuneMetricsService runeMetricsService;

  @Autowired
  public PlayerService(QuestRepository questRepository, HiscoreService hiscoreService,
      RuneMetricsService runeMetricsService) {
    this.questRepository = questRepository;
    this.hiscoreService = hiscoreService;
    this.runeMetricsService = runeMetricsService;
  }

  /**
   * Create a {@link Player} from the specified parameters.
   *
   * Quests will be filtered, prioritised and added to the player. Player data is loaded from the
   * hiscores and runemetrics.
   *
   * @param name player name to load data for; can be null
   * @param accessFilter filter quests by access
   * @param ironman <tt>true</tt> to enable ironman quest requirements; <tt>false</tt> otherwise.
   * @param recommended <tt>true</tt> to enable recommended quest requirements; <tt>false</tt>
   * otherwise.
   * @param lampSkills set of skills to use on lamps
   * @param questPriorities prioritise quests by id
   * @param typeFilter filter quests by type
   * @see Player#load(HiscoreService, RuneMetricsService)
   */
  public Player createPlayer(String name, QuestAccessFilter accessFilter,
      QuestTypeFilter typeFilter, boolean ironman, boolean recommended, Set<Skill> lampSkills,
      Map<Integer, QuestPriority> questPriorities) {
    LOG.debug("Creating player profile: {}", name);

    Set<Quest> filteredQuests = getFilteredQuests(accessFilter, typeFilter);
    Player player = new Player.Builder().withName(name).withIronman(ironman)
        .withRecommended(recommended).withLampSkills(lampSkills).withQuests(filteredQuests).build();

    for (Entry<Integer, QuestPriority> entry : questPriorities.entrySet()) {
      player.setQuestPriority(entry.getKey(), entry.getValue());
    }

    player.load(hiscoreService, runeMetricsService);

    return player;
  }


  /**
   * Create a {@link Set} of entries for the quests.
   *
   * @param accessFilter the access filter for the quests
   * @param typeFilter the type filter for the quests
   * @return set of quest entries
   */
  private Set<Quest> getFilteredQuests(QuestAccessFilter accessFilter, QuestTypeFilter typeFilter) {
    Predicate<Quest> accessAndTypeFilter = questMatchesAccessFilter(accessFilter)
        .and(questMatchesTypeFilter(typeFilter));
    Set<Integer> questRequirements = getQuestRequirements(accessAndTypeFilter);

    return questRepository.getQuests().stream()
        .filter(accessAndTypeFilter.or(questIsRequirement(questRequirements)))
        .collect(Collectors.toSet());
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
    return questRepository.getQuests().stream().filter(questFilter)
        .flatMap(quest -> quest.getQuestRequirements(true).stream())
        .map(qr -> qr.getQuest().getId()).collect(Collectors.toSet());
  }
}
