<template>
  <v-container fluid>
    <v-row>
      <v-text-field
        v-model="name"
        label="Username"
        hint="Enter your RuneScape name to retrieve quest and skill information."
        clearable
      />
    </v-row>

    <v-row>
      <v-radio-group
        v-model="typeFilter"
        label="Type Filter"
        hint="Filter by quests/miniquests/sagas."
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
      <v-checkbox
        v-model="ironman"
        label="Ironman"
        hint="Toggle ironman requirements for quests."
        class="mr-4"
      />
      <v-checkbox
        v-model="recommended"
        label="Recommended"
        hint="Toggle recommended requirements for quests."
      />
    </v-row>

    <v-row>
      <v-select
        v-model="lampSkills"
        :items="skillOptions"
        item-text="text"
        item-value="value"
        label="Lamp Skills"
        multiple
        chips
        hint="Choose which skills xp lamps should be used on."
      />
    </v-row>
  </v-container>
</template>

<script lang="ts">
import Vue from 'vue';
import {Skill} from 'ironquest';
import {capitalize, map} from 'lodash';
import {mapFields} from 'vuex-map-fields';

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
  computed: {
    skillOptions() {
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
    ]),
  },
});
</script>
