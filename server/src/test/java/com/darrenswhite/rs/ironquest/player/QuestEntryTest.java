package com.darrenswhite.rs.ironquest.player;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.darrenswhite.rs.ironquest.quest.Quest;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class QuestEntryTest {

  @Nested
  class Constructor {

    @Test
    void shouldSetDefaultPriorityAndStatus() {
      QuestEntry questEntry = new QuestEntry(null);

      assertThat(questEntry.getPriority(), equalTo(QuestPriority.NORMAL));
      assertThat(questEntry.getStatus(), equalTo(QuestStatus.NOT_STARTED));
    }
  }

  @Nested
  class Equals {

    @Test
    void shouldVerifyEqualsAndHashCode() {
      EqualsVerifier.forClass(QuestEntry.class)
          .withPrefabValues(Quest.class, new Quest.Builder(0).build(), new Quest.Builder(1).build())
          .withOnlyTheseFields("quest").verify();
    }
  }
}
