package com.darrenswhite.rs.ironquest.player;

import com.darrenswhite.rs.ironquest.quest.RuneMetricsQuest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * {@link Service} for retrieving quest data from RuneMetrics.
 */
@Service
public class RuneMetricsService {

  private static final Logger LOG = LogManager.getLogger(RuneMetricsService.class);

  private final String url;
  private final ObjectMapper objectMapper;

  @Autowired
  public RuneMetricsService(@Value("${runemetrics.url}") String url, ObjectMapper objectMapper) {
    this.url = url;
    this.objectMapper = objectMapper;
  }

  public Set<RuneMetricsQuest> load(String name) {
    Set<RuneMetricsQuest> quests = new LinkedHashSet<>();

    LOG.debug("Loading quests for player: {}...", name);

    try {
      String runeMetricsUrl = String
          .format(url, URLEncoder.encode(name, StandardCharsets.UTF_8.toString()));

      try (InputStreamReader in = new InputStreamReader(new URL(runeMetricsUrl).openStream())) {
        JsonNode rmQuestsJson = objectMapper.readTree(in).get("quests");

        quests.addAll(objectMapper.readValue(objectMapper.treeAsTokens(rmQuestsJson),
            new TypeReference<Set<RuneMetricsQuest>>() {
            }));
      }
    } catch (IOException e) {
      LOG.warn("Failed to load quests for player: {}", name, e);
    }

    return quests;
  }
}
