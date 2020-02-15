import './index.styl';
import Actions from '../../components/Actions.vue';
import Settings from '../../components/Settings.vue';
import { Action, Path } from 'ironquest';
import { Cache, PathFinder, vuetify } from '@/lib';
import Vue from 'vue';

Cache.getInstance().loadParameters();

const vm = new Vue({
  vuetify,
  data: {
    actions: {
      bus: new Vue(),
      error: false,
      loading: false,
      path: null as Path | null,
      selectedAction: null as Action | null | undefined,
    },
    parameters: {
      name: PathFinder.getInstance().parameters.name,
      typeFilter:
        PathFinder.getInstance().parameters.typeFilter,
      accessFilter:
        PathFinder.getInstance().parameters.accessFilter,
      ironman: PathFinder.getInstance().parameters.ironman,
      recommended: PathFinder.getInstance().parameters.recommended,
      lampSkills: PathFinder.getInstance().parameters.lampSkills,
    },
  },
  methods: {
    run(): void {
      this.updateParameters();
      PathFinder.getInstance().find();
    },
    updateParameters(): void {
      PathFinder.getInstance().parameters = {
        name: this.parameters.name,
        typeFilter: this.parameters.typeFilter,
        accessFilter: this.parameters.accessFilter,
        ironman: this.parameters.ironman,
        recommended: this.parameters.recommended,
        lampSkills: this.parameters.lampSkills,
      };
    },
    showLoader(): void {
      vm.actions.bus.$emit('showLoader');
    },
    displayActionsSuccess(path: Path): void {
      vm.actions.bus.$emit('displayActionsSuccess', path);
    },
    displayActionsFailure(response: unknown): void {
      vm.actions.bus.$emit('displayActionsFailure', response);
    },
    saveParameters(): void {
      this.updateParameters();
      Cache.getInstance().saveParameters();
    },
  },
  components: {
    Actions,
    Settings,
  },
  created() {
    window.addEventListener('beforeunload', this.saveParameters);

    PathFinder.getInstance().listeners.push({
      start: this.showLoader,
      success: this.displayActionsSuccess,
      failure: this.displayActionsFailure,
    });
  },
}).$mount('#app');
