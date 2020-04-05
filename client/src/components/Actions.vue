<template>
  <v-container
    v-if="loading"
    fluid
    fill-height
  >
    <v-row
      justify="center"
      align="center"
    >
      <v-progress-circular
        indeterminate
        color="primary"
      />
    </v-row>
  </v-container>
  <v-container
    v-else
    fluid
  >
    <v-row v-if="path">
      <v-col>
        <v-chip color="green">
          <v-avatar left>
            <v-icon>{{ mdiCheckboxMarkedCircle }}</v-icon>
          </v-avatar>
          Completed: {{ path.stats.percentComplete }}%
        </v-chip>
      </v-col>
      <v-col>
        <slot />
      </v-col>
    </v-row>
    <v-row v-if="path && !path.actions">
      <p>None</p>
    </v-row>
    <v-row v-if="path && path.actions">
      <v-col
        cols="12"
        md="6"
        lg="6"
        xl="6"
      >
        <v-list
          class="actions-list"
          dense
        >
          <v-list-item-group
            v-model="selectedAction"
            mandatory
          >
            <v-list-item
              v-for="(action, i) in path.actions"
              :key="i"
              :value="action"
            >
              <v-list-item-content>
                <v-list-item-title v-text="action.message" />
              </v-list-item-content>
            </v-list-item>
          </v-list-item-group>
        </v-list>
      </v-col>
      <v-col
        v-if="selectedAction"
        cols="12"
        md="6"
        lg="6"
        xl="6"
        class="actions-content"
      >
        <v-simple-table dense>
          <tbody>
            <tr
              v-for="(row, index) in SKILLS_TABLE"
              :key="index"
            >
              <td>{{ capitalize(row[0]) }}:</td>
              <td>{{ get(selectedAction.player.levels, row[0]) }}</td>
              <td>{{ capitalize(row[1]) }}:</td>
              <td>{{ get(selectedAction.player.levels, row[1]) }}</td>
              <td>{{ capitalize(row[2]) }}:</td>
              <td>{{ get(selectedAction.player.levels, row[2]) }}</td>
            </tr>
            <tr>
              <td>Total level:</td>
              <td>{{ selectedAction.player.totalLevel }}</td>
              <td>Combat level:</td>
              <td>{{ selectedAction.player.combatLevel }}</td>
              <td>Quest points:</td>
              <td>{{ selectedAction.player.questPoints }}</td>
            </tr>
            <tr
              v-if="
                selectedAction.type === 'QUEST' ||
                  selectedAction.type === 'LAMP'
              "
            >
              <td
                colspan="6"
                class="view-quest"
              >
                <a
                  :href="
                    RUNESCAPE_WIKI_URL +
                      selectedAction.quest.displayName.replace(/ /g, '_')
                  "
                  target="_blank"
                  rel="noopener"
                >
                  View quest on wiki
                </a>
              </td>
            </tr>
          </tbody>
        </v-simple-table>
      </v-col>
    </v-row>
    <v-row v-if="error">
      <v-col>
        <slot />
      </v-col>
    </v-row>
    <v-row
      justify="center"
      align="center"
    >
      <v-alert
        :value="error"
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
            <v-btn
              :href="NEW_ISSUE_URL"
              target="_blank"
              rel="noopener"
            >
              Submit issue
            </v-btn>
          </v-col>
        </v-row>
        <v-row
          v-if="errorResponse"
          align="center"
        >
          <v-col>
            <v-expansion-panels>
              <v-expansion-panel>
                <v-expansion-panel-header>
                  View details
                </v-expansion-panel-header>
                <v-expansion-panel-content>
                  Parameters:
                  <pre>{{ errorResponse.parameters }}</pre>
                  Response:
                  <pre>{{ errorResponse.response }}</pre>
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
import {Skill} from '@/common/types';
import {capitalize, get} from 'lodash';
import {mapFields} from 'vuex-map-fields';
import {mdiCheckboxMarkedCircle} from '@mdi/js';

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
  name: 'Actions',
  data() {
    return {
      NEW_ISSUE_URL,
      RUNESCAPE_WIKI_URL,
      SKILLS_TABLE,
    };
  },
  computed: {
    mdiCheckboxMarkedCircle: () => mdiCheckboxMarkedCircle,
    ...mapFields([
      'actions.error',
      'actions.errorResponse',
      'actions.loading',
      'actions.path',
      'actions.selectedAction',
    ]),
  },
  methods: {
    capitalize,
    get,
  },
});
</script>

<style lang="scss" scoped>
pre {
  white-space: pre-wrap;
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
