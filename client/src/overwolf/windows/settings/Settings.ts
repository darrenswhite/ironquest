import './settings.styl';
import { QuestAccessFilter, QuestTypeFilter } from 'ironquest';
import Settings from '../../../components/Settings.vue';
import { Windows } from '../../scripts';
import { PathFinder } from '../../../lib';
import _ from 'lodash';
import Vue from 'vue';

$(() => {
  new Vue({
    data: {
      parameters: {
        name: PathFinder.getInstance().parameters.name,
        typeFilter:
          PathFinder.getInstance().parameters.typeFilter || QuestTypeFilter.ALL,
        accessFilter:
          PathFinder.getInstance().parameters.accessFilter ||
          QuestAccessFilter.ALL,
        ironman: PathFinder.getInstance().parameters.ironman,
        recommended: PathFinder.getInstance().parameters.recommended,
        lampSkills: PathFinder.getInstance().parameters.lampSkills || [],
      },
    },
    methods: {
      close(): void {
        Windows.getInstance().close(Windows.SETTINGS);
        Windows.getInstance().restore(Windows.RESULTS);
      },
      showResults(): void {
        PathFinder.getInstance().parameters = {
          name: this.parameters.name,
          typeFilter: this.parameters.typeFilter,
          accessFilter: this.parameters.accessFilter,
          ironman: this.parameters.ironman,
          recommended: this.parameters.recommended,
          lampSkills: this.parameters.lampSkills,
        };
        PathFinder.getInstance().find();

        Windows.getInstance().close(Windows.SETTINGS);
        Windows.getInstance().restore(Windows.RESULTS);
      },
    },
    components: {
      Settings,
    },
  }).$mount('#app');
});
