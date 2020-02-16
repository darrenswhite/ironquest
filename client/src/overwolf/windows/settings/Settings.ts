import './settings.styl';
import Settings from '@/components/Settings.vue';
import { Windows } from '@/overwolf/scripts';
import { store, vuetify } from '@/lib';
import Vue from 'vue';

new Vue({
  store: store.original,
  vuetify,
  methods: {
    close(): void {
      Windows.getInstance().close(Windows.SETTINGS);
      Windows.getInstance().restore(Windows.RESULTS);
    },
    showResults(): void {
      this.$store.direct.dispatch
        .findPath()
        .finally(() => Windows.getInstance().close(Windows.SETTINGS));

      Windows.getInstance().minimize(Windows.SETTINGS);
      Windows.getInstance().restore(Windows.RESULTS);
    },
  },
  components: {
    Settings,
  },
}).$mount('#app');
