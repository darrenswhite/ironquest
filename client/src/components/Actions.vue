<template>
  <div class="container">
    <div v-if="value.loading" id="actions_loading">
      <p>Loading...</p>
    </div>
    <div v-if="value.error" id="actions_error">
      <p>Something went wrong!</p>
    </div>
    <div v-if="value.path" id="path_stats">
      <span id="path_stats_completed">
        Completed:
        <span id="path_stats_completed_inner">
          {{ value.path.stats.percentComplete }}%
        </span>
      </span>
    </div>
    <p v-if="value.path && !value.path.actions" id="actions_none">None</p>
    <select
      v-if="value.path && value.path.actions"
      v-model="value.selectedAction"
      id="actions"
      size="5"
    >
      <option v-for="action in value.path.actions" v-bind:value="action">
        {{ action.message }}
      </option>
    </select>
    <div v-if="value.selectedAction" id="actions_content">
      <table>
        <tbody>
          <tr v-for="row in SKILLS_TABLE">
            <td>{{ _.capitalize(row[0]) }}:</td>
            <td>{{ _.get(value.selectedAction.player.levels, row[0]) }}</td>
            <td>{{ _.capitalize(row[1]) }}:</td>
            <td>{{ _.get(value.selectedAction.player.levels, row[1]) }}</td>
            <td>{{ _.capitalize(row[2]) }}:</td>
            <td>{{ _.get(value.selectedAction.player.levels, row[2]) }}</td>
          </tr>
          <tr>
            <td>Total level:</td>
            <td>{{ value.selectedAction.player.totalLevel }}</td>
            <td>Combat level:</td>
            <td>{{ value.selectedAction.player.combatLevel }}</td>
            <td>Quest points:</td>
            <td>{{ value.selectedAction.player.questPoints }}</td>
          </tr>
          <tr
            v-if="
              value.selectedAction.type === 'QUEST' ||
                value.selectedAction.type === 'LAMP'
            "
          >
            <td colspan="6" id="view_quest">
              <a :href="RUNESCAPE_WIKI_URL + value.selectedAction.quest.displayName.replace(/ /g, '_')" target="_blank"
                >View quest on wiki</a
              >
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script lang="ts">
import Vue from 'vue';
import { Action, Path, Skill } from 'ironquest';
import _ from 'lodash';

const RUNESCAPE_WIKI_URL = 'https://runescape.wiki/';
const SKILLS_TABLE = [
  [Skill.ATTACK, Skill.CONSTITUTION, Skill.MINING],
  [Skill.STRENGTH, Skill.AGILITY, Skill.SMITHING],
  [Skill.DEFENCE, Skill.HERBLORE, Skill.FISHING],
  [Skill.RANGED, Skill.THIEVING, Skill.COOKING],
  [Skill.PRAYER, Skill.CRAFTING, Skill.FIREMAKING],
  [Skill.MAGIC, Skill.FLETCHING, Skill.WOODCUTTING],
  [Skill.RUNECRAFTING, Skill.SLAYER, Skill.FARMING],
  [Skill.CONSTRUCTION, Skill.HUNTER, Skill.SUMMONING],
  [Skill.DUNGEONEERING, Skill.DIVINATION, Skill.INVENTION],
];

export default Vue.extend({
  props: {
    value: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      RUNESCAPE_WIKI_URL,
      SKILLS_TABLE,
    };
  },
  methods: {
    displayActionsSuccess(path: Path): void {
      path.stats.percentComplete = Math.round(path.stats.percentComplete);

      this.value.loading = false;
      this.value.path = path;
      this.value.selectedAction = _.head(path.actions);
    },
    displayActionsFailure(response: unknown): void {
      console.error(`Failed to find path: `, response);

      this.value.loading = false;
      this.value.error = true;
    },
    showLoader(): void {
      this.value.loading = true;
      this.value.path = null;
      this.value.selectedAction = null;
      this.value.error = false;
    },
  },
  computed: {
    _() {
      return _;
    },
  },
  mounted() {
    this.value.bus.$on('displayActionsSuccess', this.displayActionsSuccess);
    this.value.bus.$on('displayActionsFailure', this.displayActionsFailure);
    this.value.bus.$on('showLoader', this.showLoader);
  },
  watch: {
    value() {
      this.$emit('input', this.value);
    },
  },
});
</script>

<style lang="stylus" scoped>
@require '../styles/grid.mixin';

.container {
  display-grid();
  grid-gap(1rem);

  @media screen and (min-width: 0) {
    grid-template-columns(100%);
  }

  @media screen and (min-width: 1024px) {
    grid-template-columns(50%, auto);
  }
}

#actions_error > p {
  color: #ee0000;
}

#actions {
  grid-area(2, 1, 3, 2);
}

#actions_content {
  overflow: auto;

  @media screen and (min-width: 0) {
    grid-area(3, 1, 4, 2);
  }

  @media screen and (min-width: 1024px) {
    grid-area(2, 2, 3, 3);
  }
}

#view_quest {
  text-align: center;
}

#actions_error,
#actions_loading,
#actions_none {
  grid-area(1, 1, 3, 3);
  margin-bottom: auto;
  margin-top: auto;
  text-align: center;
}

#actions_loading > p,
#actions_none > p {
  color: #cccccc;
  font-style: italic;
}

#path_stats {
  grid-area(1, 1, 2, 3);
}

#path_stats_completed {
  background-color: #222222;
  border: 1px solid #222222;
  border-radius: 4px;
  display: inline-block;
  font-weight: 700;
  padding: 0.5rem;
  text-align: center;
}

#path_stats_completed_inner {
  font-weight: 400;
}
</style>
