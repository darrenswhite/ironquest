package com.darrenswhite.rs.ironquest.player;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * {@link Service} for retrieving skill data from the Hiscores.
 */
@Service
public class HiscoreService {

  private static final Logger LOG = LogManager.getLogger(Player.class);

  private final String url;

  public HiscoreService(@Value("${hiscores.url}") String url) {
    this.url = url;
  }

  public Map<Skill, Double> load(String name) {
    Map<Skill, Double> skillXps = new EnumMap<>(Skill.class);

    LOG.debug("Loading hiscores for player: {}...", name);

    try {
      CSVFormat format = CSVFormat.DEFAULT.withDelimiter(',');
      String hiscoresUrl = String
          .format(url, URLEncoder.encode(name, StandardCharsets.UTF_8.toString()));

      try (InputStreamReader in = new InputStreamReader(new URL(hiscoresUrl).openStream())) {
        CSVParser parser = format.parse(in);
        List<CSVRecord> records = parser.getRecords();

        for (int i = 1; i < Skill.values().length + 1; i++) {
          Skill skill = Skill.tryGet(i);
          CSVRecord r = records.get(i);

          if (skill != null) {
            skillXps
                .put(skill, Math.max(Skill.INITIAL_XPS.get(skill), Double.parseDouble(r.get(2))));
          } else {
            LOG.warn("Unknown skill with id: {}", i);
          }
        }
      }
    } catch (IOException e) {
      LOG.warn("Failed to load hiscores for player: {}", name, e);
    }

    return skillXps;
  }
}
