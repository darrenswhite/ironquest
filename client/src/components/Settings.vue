<template>
  <v-container fluid>
    <v-row>
      <v-text-field
        v-model="value.name"
        label="Username"
        hint="Enter your RuneScape name to retrieve quest and skill information."
        clearable
      ></v-text-field>
    </v-row>

    <v-row>
      <v-radio-group
        v-model="value.typeFilter"
        label="Type Filter"
        hint="Filter by quests/miniquests/sagas."
        row
      >
        <v-radio label="All" value="ALL"></v-radio>
        <v-radio label="Quests" value="QUESTS"></v-radio>
        <v-radio label="Miniquests" value="MINIQUESTS"></v-radio>
        <v-radio label="Sagas" value="SAGAS"></v-radio>
      </v-radio-group>
    </v-row>

    <v-row>
      <v-radio-group
        v-model="value.accessFilter"
        label="Member Filter"
        hint="Filter by free/member quests."
        row
      >
        <v-radio label="All" value="ALL"></v-radio>
        <v-radio label="Free" value="FREE"></v-radio>
        <v-radio label="Members" value="MEMBERS"></v-radio>
      </v-radio-group>
    </v-row>

    <v-row>
      <v-checkbox
        v-model="value.ironman"
        label="Ironman"
        hint="Toggle ironman requirements for quests."
        class="mr-4"
      ></v-checkbox>
      <v-checkbox
        v-model="value.recommended"
        label="Recommended"
        hint="Toggle recommended requirements for quests."
      ></v-checkbox>
    </v-row>

    <v-row>
      <v-select
        v-model="value.lampSkills"
        :items="skillOptions"
        item-text="text"
        item-value="value"
        label="Lamp Skills"
        multiple
        chips
        hint="Choose which skills xp lamps should be used on."
      ></v-select>
    </v-row>
  </v-container>
</template>

<script lang="ts">
import Vue from 'vue';
import { Skill } from 'ironquest';
import { capitalize, map } from 'lodash';

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
  name: 'settings',
  props: {
    value: {
      type: Object,
      required: true,
    },
  },
  computed: {
    skillOptions() {
      return map(SKILLS, skill => {
        return {
          value: skill,
          text: capitalize(skill),
        };
      });
    },
  },
  watch: {
    value() {
      this.$emit('input', this.value);
    },
  },
});
</script>
