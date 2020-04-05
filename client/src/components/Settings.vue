<template>
  <v-container fluid>
    <v-row>
      <v-text-field
        v-model="name"
        label="Username"
        hint="Enter your RuneScape name to retrieve quest and skill information."
        :prepend-icon="mdiAccount"
        persistent-hint
        clearable
      />
    </v-row>

    <v-row>
      <v-radio-group
        v-model="typeFilter"
        label="Type Filter"
        hint="Filter by quests/miniquests/sagas."
        :prepend-icon="mdiCompassRose"
        persistent-hint
        row
      >
        <v-radio
          label="All"
          value="ALL"
        />
        <v-radio
          label="Quests"
          value="QUESTS"
        />
        <v-radio
          label="Miniquests"
          value="MINIQUESTS"
        />
        <v-radio
          label="Sagas"
          value="SAGAS"
        />
      </v-radio-group>
    </v-row>

    <v-row>
      <v-radio-group
        v-model="accessFilter"
        label="Member Filter"
        hint="Filter by free/member quests."
        :prepend-icon="mdiStar"
        persistent-hint
        row
      >
        <v-radio
          label="All"
          value="ALL"
        />
        <v-radio
          label="Free"
          value="FREE"
        />
        <v-radio
          label="Members"
          value="MEMBERS"
        />
      </v-radio-group>
    </v-row>

    <v-row>
      <v-switch
        v-model="ironman"
        label="Ironman"
        hint="Toggle ironman requirements for quests."
        :prepend-icon="mdiDumbbell"
        persistent-hint
      />
    </v-row>
    <v-row>
      <v-switch
        v-model="recommended"
        label="Recommended"
        hint="Toggle recommended requirements for quests."
        :prepend-icon="mdiShieldPlus"
        persistent-hint
      />
    </v-row>

    <v-row>
      <v-autocomplete
        v-model="lampSkills"
        :items="skillItems"
        item-text="text"
        item-value="value"
        label="Lamp Skills"
        class="mt-4"
        :prepend-icon="mdiChartBar"
        hint="Choose which skills xp lamps should be used on."
        persistent-hint
        clearable
        multiple
        chips
        deletable-chips
      />
    </v-row>

    <v-row>
      <quest-priorities
        v-model="questPriorities"
        class="mt-4"
      />
    </v-row>
  </v-container>
</template>

<script lang="ts">
import Vue from 'vue';
import {Skill} from '@/common/types';
import {capitalize, map} from 'lodash';
import {mapFields} from 'vuex-map-fields';
import {
  mdiAccount,
  mdiChartBar,
  mdiCompassRose,
  mdiDumbbell,
  mdiShieldPlus,
  mdiStar,
} from '@mdi/js';
import QuestPriorities from './QuestPriorities.vue';

const SKILLS = [
  Skill.AGILITY,
  Skill.ATTACK,
  Skill.CONSTITUTION,
  Skill.CONSTRUCTION,
  Skill.COOKING,
  Skill.CRAFTING,
  Skill.DEFENCE,
  Skill.DIVINATION,
  Skill.DUNGEONEERING,
  Skill.FARMING,
  Skill.FIREMAKING,
  Skill.FISHING,
  Skill.FLETCHING,
  Skill.HERBLORE,
  Skill.HUNTER,
  Skill.INVENTION,
  Skill.MAGIC,
  Skill.MINING,
  Skill.PRAYER,
  Skill.RANGED,
  Skill.RUNECRAFTING,
  Skill.SLAYER,
  Skill.SMITHING,
  Skill.STRENGTH,
  Skill.SUMMONING,
  Skill.THIEVING,
  Skill.WOODCUTTING,
] as Skill[];

export default Vue.extend({
  name: 'Settings',
  components: {
    QuestPriorities,
  },
  computed: {
    mdiAccount: () => mdiAccount,
    mdiChartBar: () => mdiChartBar,
    mdiCompassRose: () => mdiCompassRose,
    mdiDumbbell: () => mdiDumbbell,
    mdiShieldPlus: () => mdiShieldPlus,
    mdiStar: () => mdiStar,
    skillItems() {
      return map(SKILLS, skill => ({
        value: skill,
        text: capitalize(skill),
      }));
    },
    ...mapFields([
      'parameters.name',
      'parameters.typeFilter',
      'parameters.accessFilter',
      'parameters.ironman',
      'parameters.recommended',
      'parameters.lampSkills',
      'parameters.questPriorities',
    ]),
  },
});
</script>
