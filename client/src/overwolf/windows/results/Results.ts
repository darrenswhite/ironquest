import './results';
import Actions from '../../../components/Actions.vue';
import { Windows } from '../../scripts';
import { PathFinder } from '../../../lib';
import { Action, Path } from 'ironquest';
import Vue from 'vue';

$(() => {
  const vm = new Vue({
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
        vm.actions.bus.$emit('showLoader');
      },
      displayActionsSuccess(path: Path): void {
        vm.actions.bus.$emit('displayActionsSuccess', path);
      },
      displayActionsFailure(response: unknown): void {
        vm.actions.bus.$emit('displayActionsFailure', response);
      },
    },
    components: {
      actions: Actions,
    },
  }).$mount('#app');

  PathFinder.getInstance().listeners.push({
    start: vm.showLoader,
    success: vm.displayActionsSuccess,
    failure: vm.displayActionsFailure,
  });

  PathFinder.getInstance().find();
});
