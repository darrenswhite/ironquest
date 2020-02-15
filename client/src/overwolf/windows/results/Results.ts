import './results.styl';
import Actions from '@/components/Actions.vue';
import { Windows } from '@/overwolf/scripts';
import { PathFinder, vuetify } from '@/lib';
import { Action, Path } from 'ironquest';
import Vue from 'vue';

new Vue({
  vuetify,
  data: {
    actions: {
      bus: new Vue(),
      error: false,
      loading: false,
      path: null as Path | null,
      selectedAction: null as Action | null | undefined,
    },
  },
  methods: {
    showSettings(): void {
      Windows.getInstance().minimize(Windows.RESULTS);
      Windows.getInstance().restore(Windows.SETTINGS);
    },
    showLoader(): void {
      this.actions.bus.$emit('showLoader');
    },
    displayActionsSuccess(path: Path): void {
      this.actions.bus.$emit('displayActionsSuccess', path);
    },
    displayActionsFailure(response: unknown): void {
      this.actions.bus.$emit('displayActionsFailure', response);
    },
  },
  components: {
    Actions,
  },
  mounted() {
    PathFinder.getInstance().listeners.push({
      start: this.showLoader,
      success: this.displayActionsSuccess,
      failure: this.displayActionsFailure,
    });

    PathFinder.getInstance().find();
  },
}).$mount('#app');
