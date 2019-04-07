package com.darrenswhite.rs.ironquest;

import com.darrenswhite.rs.ironquest.action.Action;
import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.QuestEntry;
import com.darrenswhite.rs.ironquest.player.QuestPriority;
import com.darrenswhite.rs.ironquest.player.QuestStatus;
import com.darrenswhite.rs.ironquest.player.Skill;
import com.darrenswhite.rs.ironquest.quest.Quest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Darren S. White
 */
public class IronQuest implements Runnable {

  private static final Logger LOG = LogManager.getLogger(IronQuest.class);

  /**
   * The URL to retrieve quest data from
   */
  private static final String QUESTS_URL = "https://us-central1-ironquest-e8f3e.cloudfunctions.net/getQuests";

  private static final IronQuest instance = new IronQuest();

  private final ObservableList<Action> actions = FXCollections.observableList(new LinkedList<>());
  private final Player player = new Player();

  private IronQuest() {
    // default constructor
  }

  public static IronQuest getInstance() {
    return instance;
  }

  public ObservableList<Action> getActions() {
    return actions;
  }

  public Player getPlayer() {
    return player;
  }

  @Override
  public void run() {
    LOG.info("Using player profile: " + player.getName());

    player.reset();
    player.load();

    if (Platform.isFxApplicationThread()) {
      actions.clear();
    } else {
      Platform.runLater(actions::clear);
    }

    PathFinder pathFinder = new PathFinder(player);

    pathFinder.find();

    if (Platform.isFxApplicationThread()) {
      actions.addAll(pathFinder.getActions());
    } else {
      Platform.runLater(() -> actions.addAll(pathFinder.getActions()));
    }
  }

  void load() throws IOException {
    Set<Quest> quests = loadQuests();
    Properties properties = loadProperties();

    String name = properties.getProperty("name", null);
    Set<Skill> lampSkills = parseLampSkills(properties.getProperty("lampSkills", ""));
    boolean ironman = Boolean.parseBoolean(properties.getProperty("ironman", "false"));
    boolean recommended = Boolean.parseBoolean(properties.getProperty("recommended", "false"));
    boolean free = Boolean.parseBoolean(properties.getProperty("free", "true"));
    boolean members = Boolean.parseBoolean(properties.getProperty("members", "true"));

    Map<Integer, QuestPriority> questPriorities = parseQuestPriorities(
        properties.getProperty("priorities"));
    Set<QuestEntry> questEntries = createQuestEntries(quests, questPriorities);

    player.setName(name);
    player.setQuests(questEntries);
    player.setLampSkills(lampSkills);
    player.setIronman(ironman);
    player.setRecommended(recommended);
    player.setFree(free);
    player.setMembers(members);
  }

  void save() throws IOException {
    Properties prop = new Properties();

    LOG.info("Saving settings...");

    prop.setProperty("name", player.getName());
    prop.setProperty("ironman", Boolean.toString(player.isIronman()));
    prop.setProperty("recommended", Boolean.toString(player.isRecommended()));
    prop.setProperty("free", Boolean.toString(player.isFree()));
    prop.setProperty("members", Boolean.toString(player.isMembers()));

    String lampSkillsStr = player.getLampSkills().stream().map(Skill::toString)
        .collect(Collectors.joining(","));

    if (!lampSkillsStr.isEmpty()) {
      prop.setProperty("lampSkills", lampSkillsStr);
    }

    String priorities = player.getQuests().stream()
        .filter(q -> q.getPriority() != QuestPriority.NORMAL)
        .map(q -> q.getQuest().getId() + "->" + q.getPriority().toString())
        .collect(Collectors.joining(","));

    if (!priorities.isEmpty()) {
      prop.setProperty("priorities", priorities);
    }

    try (OutputStream out = new FileOutputStream(getPropertiesPath())) {
      prop.store(out, "");
    }
  }

  private File getPropertiesPath() {
    String home = System.getProperty("user.home");
    return new File(home, ".ironquest");
  }

  private Set<QuestEntry> createQuestEntries(Set<Quest> quests,
      Map<Integer, QuestPriority> questPriorities) {
    return quests.stream().map(q -> {
      QuestPriority priority = questPriorities.getOrDefault(q.getId(), QuestPriority.NORMAL);
      return new QuestEntry(q, QuestStatus.NOT_STARTED, priority);
    }).collect(Collectors.toSet());
  }

  private Map<Integer, QuestPriority> parseQuestPriorities(String prioritiesString) {
    Map<Integer, QuestPriority> priorities = new HashMap<>();

    if (prioritiesString != null && !prioritiesString.trim().isEmpty()) {
      String[] prioritiesArray = prioritiesString.split(",");

      for (String questPriority : prioritiesArray) {
        String[] setting = questPriority.split("->");
        int id = Integer.parseInt(setting[0]);
        String priority = setting[1];

        QuestPriority.tryGet(priority).ifPresent(qp -> priorities.put(id, qp));
      }
    }

    return priorities;
  }

  private Set<Skill> parseLampSkills(String lampSkillsString) {
    Set<Skill> lampSkills = new LinkedHashSet<>();

    if (lampSkillsString != null && !lampSkillsString.trim().isEmpty()) {
      String[] lampSkillsArray = lampSkillsString.split(",");

      for (String s : lampSkillsArray) {
        Skill.tryGet(s).ifPresent(lampSkills::add);
      }
    }

    return lampSkills;
  }

  private Properties loadProperties() {
    Properties properties = new Properties();

    LOG.info("Loading settings...");

    File file = getPropertiesPath();

    if (file.exists()) {
      try (InputStream in = new FileInputStream(file)) {
        properties.load(in);
      } catch (IOException e) {
        LOG.warn("Unable to load properties file", e);
      }
    }

    return properties;
  }

  private Set<Quest> loadQuests() throws IOException {
    LOG.info("Trying to retrieve quests from URL: {}", QUESTS_URL);

    URL url = new URL(QUESTS_URL);
    return new ObjectMapper().readValue(url, new TypeReference<Set<Quest>>() {
    });
  }
}
