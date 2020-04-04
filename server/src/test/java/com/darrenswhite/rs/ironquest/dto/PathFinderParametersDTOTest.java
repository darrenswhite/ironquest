package com.darrenswhite.rs.ironquest.dto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

import com.darrenswhite.rs.ironquest.quest.QuestAccessFilter;
import com.darrenswhite.rs.ironquest.quest.QuestTypeFilter;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class PathFinderParametersDTOTest {

  @Nested
  class Constructor {

    @Test
    void shouldSetDefaultValues() {
      PathFinderParametersDTO pathFinderParametersDTO = new PathFinderParametersDTO();

      assertThat(pathFinderParametersDTO.getName(), nullValue());
      assertThat(pathFinderParametersDTO.getAccessFilter(), equalTo(QuestAccessFilter.ALL));
      assertThat(pathFinderParametersDTO.getTypeFilter(), equalTo(QuestTypeFilter.ALL));
      assertThat(pathFinderParametersDTO.isIronman(), equalTo(false));
      assertThat(pathFinderParametersDTO.isRecommended(), equalTo(false));
      assertThat(pathFinderParametersDTO.getLampSkills(), equalTo(new LinkedHashSet<>()));
      assertThat(pathFinderParametersDTO.getQuestPriorities(), equalTo(new LinkedHashMap<>()));
    }
  }
}
