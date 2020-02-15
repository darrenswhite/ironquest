<template>
  <v-container v-if="value.loading" fluid fill-height>
    <v-row justify="center" align="center">
      <v-progress-circular indeterminate color="primary"></v-progress-circular>
    </v-row>
  </v-container>
  <v-container v-else fluid>
    <v-row v-if="value.path">
      <v-col>
        <v-chip color="green">
          <v-avatar left>
            <v-icon>mdi-checkbox-marked-circle</v-icon>
          </v-avatar>
          Completed: {{ value.path.stats.percentComplete }}%
        </v-chip>
      </v-col>
      <v-col>
        <slot></slot>
      </v-col>
    </v-row>
    <v-row v-if="value.path && !value.path.actions">
      <p>None</p>
    </v-row>
    <v-row v-if="value.path && value.path.actions">
      <v-col cols="12" md="6" lg="6" xl="6">
        <v-list class="actions-list" dense>
          <v-list-item-group v-model="value.selectedAction" mandatory>
            <v-list-item
              v-for="(action, i) in value.path.actions"
              :key="i"
              :value="action"
            >
              <v-list-item-content>
                <v-list-item-title v-text="action.message"></v-list-item-title>
              </v-list-item-content>
            </v-list-item>
          </v-list-item-group>
        </v-list>
      </v-col>
      <v-col
        v-if="value.selectedAction"
        cols="12"
        md="6"
        lg="6"
        xl="6"
        class="actions-content"
      >
        <v-simple-table dense>
          <tbody>
            <tr v-for="row in SKILLS_TABLE">
              <td>{{ row[0] | capitalize }}:</td>
              <td>{{ get(value.selectedAction.player.levels, row[0]) }}</td>
              <td>{{ row[1] | capitalize }}:</td>
              <td>{{ get(value.selectedAction.player.levels, row[1]) }}</td>
              <td>{{ row[2] | capitalize }}:</td>
              <td>{{ get(value.selectedAction.player.levels, row[2]) }}</td>
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
              <td colspan="6" class="view-quest">
                <a
                  :href="
                    RUNESCAPE_WIKI_URL +
                      value.selectedAction.quest.displayName.replace(/ /g, '_')
                  "
                  target="_blank"
                  >View quest on wiki</a
                >
              </td>
            </tr>
          </tbody>
        </v-simple-table>
      </v-col>
    </v-row>
    <v-row justify="center" align="center">
      <v-alert
        :value="value.error"
        type="error"
        transition="fade-transition"
        dismissible
        prominent
      >
        <v-row align="center">
          <v-col class="grow">
            Something went wrong!
          </v-col>
          <v-col class="shrink">
            <v-btn :href="NEW_ISSUE_URL" target="_blank">Submit issue</v-btn>
          </v-col>
        </v-row>
        <v-row v-if="value.errorResponse" align="center">
          <v-col>
            <v-expansion-panels>
              <v-expansion-panel>
                <v-expansion-panel-header>
                  View details
                </v-expansion-panel-header>
                <v-expansion-panel-content>
                  Parameters:
                  <pre>{{ value.errorResponse.parameters }}</pre>
                  Response:
                  <pre>{{ value.errorResponse.response }}</pre>
                </v-expansion-panel-content>
              </v-expansion-panel>
            </v-expansion-panels>
          </v-col>
        </v-row>
      </v-alert>
    </v-row>
  </v-container>
</template>

<script lang="ts">
import Vue from 'vue';
import { Action, Path, PathFinderError, Skill } from 'ironquest';
import { get, head } from 'lodash';

const NEW_ISSUE_URL = 'https://github.com/darrenswhite/ironquest/issues/new';
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
  name: 'actions',
  props: {
    value: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      NEW_ISSUE_URL,
      RUNESCAPE_WIKI_URL,
      SKILLS_TABLE,
    };
  },
  methods: {
    displayActionsSuccess(path: Path): void {
      path.stats.percentComplete = Math.round(path.stats.percentComplete);

      this.value.loading = false;
      this.value.path = path;
      this.value.selectedAction = head(path.actions);
    },
    displayActionsFailure(response: PathFinderError): void {
      this.value.loading = false;
      this.value.error = true;
      this.value.errorResponse = response;
    },
    showLoader(): void {
      this.value.loading = true;
      this.value.path = null;
      this.value.selectedAction = null;
      this.value.error = false;
    },
    get,
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
.error {
  color: #ee0000;
}

.actions-list {
  max-height: 16rem;
  overflow-y: auto;
}

.actions-content {
  overflow-x: auto;
}

.view-quest {
  text-align: center;
}
</style>
