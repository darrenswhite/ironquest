<template>
  <div class="container">
    <label for="name" id="name-label">
      Username
      <span title="Enter your RuneScape name to retrieve quest and skill information.">&#9432;</span>
    </label>

    <input v-model="value.name" id="name" placeholder="Username" type="text" />

    <div id="type-filter-label">
      Type Filter
      <span title="Filter by quests/miniquests/sagas.">&#9432;</span>
    </div>

    <div id="type-filter">
      <label>
        <input v-model="value.typeFilter" type="radio" value="ALL" name="typeFilter" />
        All
      </label>

      <label>
        <input v-model="value.typeFilter" type="radio" value="QUESTS" name="typeFilter" />
        Quests
      </label>

      <label>
        <input v-model="value.typeFilter" type="radio" value="MINIQUESTS" name="typeFilter" />
        Miniquests
      </label>

      <label>
        <input v-model="value.typeFilter" type="radio" value="SAGAS" name="typeFilter" />
        Sagas
      </label>
    </div>

    <div id="member-filter-label">
      Member Filter
      <span title="Filter by free/member quests.">&#9432;</span>
    </div>

    <div id="member-filter">
      <label>
        <input v-model="value.accessFilter" type="radio" value="ALL" name="accessFilter" />
        All
      </label>

      <label>
        <input v-model="value.accessFilter" type="radio" value="FREE" name="accessFilter" />
        Free
      </label>

      <label>
        <input v-model="value.accessFilter" type="radio" value="MEMBERS" name="accessFilter" />
        Members
      </label>
    </div>

    <div id="requirement-filter-label">
      Requirement Filter
      <span title="Toggle ironman/recommended requirements for quests.">&#9432;</span>
    </div>

    <div id="requirement-filter">
      <label>
        <input v-model="value.ironman" type="checkbox" />
        Ironman
      </label>

      <label>
        <input v-model="value.recommended" type="checkbox" />
        Recommended
      </label>
    </div>

    <div id="lamp-skills-label">
      Lamp Skills
      <span title="Choose which skills xp lamps should be used on.">&#9432;</span>
    </div>

    <select
      v-model="selectedLampSkill"
      v-on:click="addLampSkill"
      v-on:keyup.space="addLampSkill"
      id="lamp-skills"
      size="5"
    >
      <option
        v-for="skill in lampSkillsInitial"
        v-bind:key="skill"
        v-bind:value="skill"
      >
        {{ _.capitalize(skill) }}
      </option>
    </select>

    <select
      v-model="selectedLampSkillSelection"
      v-on:click="removeLampSkill"
      v-on:keyup.space="removeLampSkill"
      id="lamp-skills-selection"
      size="5"
    >
      <option
        v-for="skill in value.lampSkills"
        v-bind:key="skill"
        v-bind:value="skill"
      >
        {{ _.capitalize(skill) }}
      </option>
    </select>
  </div>
</template>

<script lang="ts">
import Vue from 'vue';
import { Skill } from 'ironquest';
import _ from 'lodash';

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
  props: {
    value: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      selectedLampSkill: null as Skill | null,
      selectedLampSkillSelection: null as Skill | null,
    };
  },
  methods: {
    addLampSkill(): void {
      if (this.selectedLampSkill) {
        const index = this.value.lampSkills.indexOf(this.selectedLampSkill);

        if (index === -1) {
          this.value.lampSkills.push(this.selectedLampSkill);
        }
      }
    },
    removeLampSkill(): void {
      if(this.selectedLampSkillSelection) {
        const index = this.value.lampSkills.indexOf(this.selectedLampSkillSelection);

        if (index !== -1) {
          this.value.lampSkills.splice(index, 1);
        }
      }
    },
  },
  computed: {
    lampSkillsInitial(): Array<Skill> {
      return _.filter(
        SKILLS,
        skill => this.value.lampSkills.indexOf(skill) === -1
      );
    },
    _() {
      return _;
    },
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
  grid-template-columns(50%, auto);
}

#name-label {
  cursor: default;
  font-weight: 700;
  grid-area(1, 1, 2, 3);
}

#name {
  grid-area(2, 1, 3, 3);
}

#type-filter-label {
  cursor: default;
  font-weight: 700;
  grid-area(3, 1, 4, 3);
}

#type-filter {
  grid-area(4, 1, 5, 3);
}

#member-filter-label {
  cursor: default;
  font-weight: 700;
  grid-area(5, 1, 6, 3);
}

#member-filter {
  grid-area(6, 1, 7, 3);
}

#requirement-filter-label {
  cursor: default;
  font-weight: 700;
  grid-area(7, 1, 8, 3);
}

#requirement-filter {
  grid-area(8, 1, 9, 3);
}

#lamp-skills-label {
  cursor: default;
  font-weight: 700;
  grid-area(9, 1, 10, 3);
}

#lamp-skills {
  grid-area(10, 1, 11, 2);
}

#lamp-skills-selection {
  grid-area(10, 2, 11, 3);
}
</style>
