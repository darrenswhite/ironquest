<template>
  <v-autocomplete
    v-model="selectedQuests"
    :items="quests"
    :loading="loading"
    :error="error"
    :error-messages="errorResponse"
    item-text="displayName"
    item-value="id"
    label="Quest Priorities"
    :prepend-icon="mdiPriorityHigh"
    hint="Choose priorities for quests. Select a quest and click to change priority."
    persistent-hint
    clearable
    multiple
    chips
    deletable-chips
  >
    <template v-slot:selection="data">
      <v-menu right offset-x>
        <template v-slot:activator="{on}">
          <v-chip close v-on="on" @click:close="remove(data.item)">
            {{ `${data.item.displayName} (${capitalize(data.item.priority)})` }}
          </v-chip>
        </template>

        <v-list>
          <v-list-item
            v-for="(priority, index) in questPriorityItems"
            :key="index"
            @click="data.item.priority = priority.value"
          >
            <v-list-item-title>{{ priority.text }}</v-list-item-title>
          </v-list-item>
        </v-list>
      </v-menu>
    </template>
  </v-autocomplete>
</template>

<script lang="ts">
import Vue, {PropType} from 'vue';
import {Quest, QuestPriorities, QuestPriority} from '@/common/types';
import {capitalize, debounce, find, keys, map, reduce} from 'lodash';
import {mdiPriorityHigh} from '@mdi/js';
import {ComputedMapper, RootState, constants} from '@/store';
import {mapGetters, mapState} from 'vuex';

const QUEST_PRIORITIES = [
  QuestPriority.MAXIMUM,
  QuestPriority.HIGH,
  QuestPriority.NORMAL,
  QuestPriority.LOW,
  QuestPriority.MINIMUM,
] as QuestPriority[];

const QUEST_PRIORITY_DEBOUNCE = 2000;

export default Vue.extend({
  name: 'QuestPriorities',
  props: {
    value: {
      type: Object as PropType<QuestPriorities>,
      default: () => ({}),
    },
  },
  data() {
    return {
      dataValue: this.value,
      selectedQuests: map(keys(this.value), parseFloat),
    };
  },
  computed: {
    ...mapGetters({
      questsParameters: constants.GETTERS.GET_QUESTS_PARAMETERS,
    }),
    ...(mapState<RootState>({
      error: (state: RootState) => state.quests.error,
      errorResponse: (state: RootState) => state.quests.errorResponse,
      loading: (state: RootState) => state.quests.loading,
      quests: (state: RootState) => state.quests.quests,
    }) as ComputedMapper<RootState['quests']>),
    mdiPriorityHigh: () => mdiPriorityHigh,
    questPriorityItems() {
      return map(QUEST_PRIORITIES, priority => ({
        value: priority,
        text: capitalize(priority),
      }));
    },
    questPriorities(): QuestPriorities {
      return reduce(
        this.selectedQuests,
        (questPriorities: QuestPriorities, id: number) => {
          const quest = find(this.quests, ['id', id]);
          if (quest) {
            questPriorities[id] = quest.priority;
          }
          return questPriorities;
        },
        {}
      );
    },
    computedValue: {
      get(): QuestPriorities {
        return this.dataValue;
      },
      set(val: QuestPriorities) {
        this.dataValue = val;
        this.$emit('input', val);
      },
    },
  },
  watch: {
    selectedQuests() {
      this.computedValue = this.questPriorities;
    },
    quests: {
      deep: true,
      handler() {
        this.computedValue = this.questPriorities;
      },
    },
    questsParameters() {
      this.loadQuests();
    },
  },
  mounted() {
    this.loadQuests = debounce(this.loadQuests, QUEST_PRIORITY_DEBOUNCE);
    this.loadQuests();
  },
  methods: {
    capitalize,
    remove(quest: Quest) {
      const index = this.selectedQuests.indexOf(quest.id);

      if (index !== -1) {
        this.selectedQuests.splice(index, 1);
      }
    },
    loadQuests() {
      this.$store.dispatch(constants.ACTIONS.LOAD_QUESTS);
    },
  },
});
</script>
