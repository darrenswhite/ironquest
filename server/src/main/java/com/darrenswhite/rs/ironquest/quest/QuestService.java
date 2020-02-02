package com.darrenswhite.rs.ironquest.quest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

/**
 * {@link Service} for loading {@link Quests} from a {@link Resource}.
 *
 * @author Darren S. White
 */
@Service
public class QuestService {

  private static final Logger LOG = LogManager.getLogger(QuestService.class);

  private final Quests quests;

  /**
   * Create a new {@link QuestService}.
   *
   * @param questsResource the resource to retrieve quest data from
   * @param objectMapper an {@link ObjectMapper}
   */
  public QuestService(@Value("${quests.resource}") Resource questsResource,
      ObjectMapper objectMapper) throws IOException {
    this.quests = load(questsResource, objectMapper);
  }

  public Quests getQuests() {
    return quests;
  }

  private Quests load(Resource questsResource, ObjectMapper objectMapper) throws IOException {
    LOG.debug("Trying to retrieve quests from resource: {}", questsResource);

    try (InputStream in = questsResource.getInputStream()) {
      return new Quests(objectMapper.readValue(in, new TypeReference<Set<Quest>>() {
      }));
    }
  }
}
