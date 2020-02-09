package com.darrenswhite.rs.ironquest.quest;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class RuneMetricsQuestTest {

  @Nested
  class Equals {

    @Test
    void shouldVerifyEqualsAndHashCode() {
      EqualsVerifier.forClass(RuneMetricsQuest.class).verify();
    }
  }
}
