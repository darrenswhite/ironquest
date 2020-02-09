package com.darrenswhite.rs.ironquest.dto;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class LampActionDTOTest {

  @Nested
  class Equals {

    @Test
    void shouldVerifyEqualsAndHashCode() {
      EqualsVerifier.forClass(LampActionDTO.class).verify();
    }
  }
}
