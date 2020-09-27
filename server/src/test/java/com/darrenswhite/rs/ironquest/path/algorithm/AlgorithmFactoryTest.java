package com.darrenswhite.rs.ironquest.path.algorithm;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Set;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class AlgorithmFactoryTest {

  @Nested
  class GetAlgorithm {

    @Test
    void shouldReturnCorrectAlgorithmGivenId() {
      DefaultAlgorithm defaultAlgorithm = new DefaultAlgorithm();
      SmartPriorities smartPriorities = new SmartPriorities();
      AlgorithmFactory algorithmFactory = new AlgorithmFactory(
          Set.of(defaultAlgorithm, smartPriorities));

      assertThat(algorithmFactory.getAlgorithm(AlgorithmId.DEFAULT), is(defaultAlgorithm));
      assertThat(algorithmFactory.getAlgorithm(AlgorithmId.SMART_PRIORITIES), is(smartPriorities));
    }
  }
}