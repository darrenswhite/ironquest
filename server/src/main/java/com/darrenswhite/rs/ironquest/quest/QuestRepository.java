package com.darrenswhite.rs.ironquest.quest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;

/**
 * {@link Repository} for retrieving {@link Quest}s from a {@link Resource}.
 *
 * @author Darren S. White
 */
@Repository
public class QuestRepository {

  private static final Logger LOG = LogManager.getLogger(QuestRepository.class);

  private final Set<Quest> quests;

  /**
   * Create a new {@link QuestRepository}.
   *
   * @param questsResource the resource to retrieve quest data from
   * @param objectMapper an {@link ObjectMapper}
   */
  public QuestRepository(@Value("${quests.resource}") Resource questsResource,
      ObjectMapper objectMapper) throws IOException {
    this.quests = load(questsResource, objectMapper);
  }

  public Set<Quest> getQuests() {
    return quests;
  }

  /**
   * Retrieve quest data from the specified {@link Resource}.
   *
   * @param questsResource the resource to retrieve quest data from
   * @return the loaded quests
   */
  private LinkedHashSet<Quest> load(Resource questsResource, ObjectMapper objectMapper)
      throws IOException {
    LOG.debug("Trying to retrieve quests from resource: {}", questsResource);

    try (InputStream in = questsResource.getInputStream()) {
      return objectMapper.readValue(in, new TypeReference<>() {
      });
    }
  }
}
