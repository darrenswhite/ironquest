package com.darrenswhite.rs.ironquest.path;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import com.darrenswhite.rs.ironquest.action.Action;
import com.darrenswhite.rs.ironquest.action.LampAction;
import com.darrenswhite.rs.ironquest.action.QuestAction;
import com.darrenswhite.rs.ironquest.action.TrainAction;
import com.darrenswhite.rs.ironquest.dto.PathDTO;
import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.QuestEntry;
import com.darrenswhite.rs.ironquest.player.QuestPriority;
import com.darrenswhite.rs.ironquest.player.QuestStatus;
import com.darrenswhite.rs.ironquest.player.Skill;
import com.darrenswhite.rs.ironquest.quest.Quest;
import com.darrenswhite.rs.ironquest.quest.reward.LampReward;
import com.darrenswhite.rs.ironquest.quest.reward.LampType;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class PathTest {

  @Nested
  class CreateDTO {

    @Test
    void shouldCreateWithCorrectFields() {
      Quest quest = new Quest.Builder().build();
      QuestEntry entry = new QuestEntry(quest, QuestStatus.NOT_STARTED, QuestPriority.NORMAL);
      Player player = new Player.Builder().build();
      LampReward lampReward = new LampReward.Builder().withType(LampType.XP).withXp(500).build();
      Set<Skill> skills = Collections.singleton(Skill.HERBLORE);
      LampAction lampAction = new LampAction(player, false, entry, lampReward, skills);
      QuestAction questAction = new QuestAction(player, entry);
      TrainAction trainAction = new TrainAction(player, Skill.ATTACK, 0, 100);
      List<Action> actions = new LinkedList<>(Arrays.asList(lampAction, questAction, trainAction));
      PathStats stats = new PathStats(55);
      Path path = new Path(actions, stats);

      PathDTO dto = path.createDTO();

      assertThat(dto.getActions().size(), equalTo(actions.size()));
      assertThat(dto.getStats().getPercentComplete(), equalTo(stats.getPercentComplete()));
    }
  }
}
