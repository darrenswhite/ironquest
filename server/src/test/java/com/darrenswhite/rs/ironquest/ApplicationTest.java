package com.darrenswhite.rs.ironquest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
class ApplicationTest {

  @Nested
  class CorsConfigurer {

    @Test
    void shouldAcceptAllOrigins() {
      CorsRegistry registry = mock(CorsRegistry.class);

      new Application().corsConfigurer().addCorsMappings(registry);

      verify(registry).addMapping("/**");
    }
  }
}
