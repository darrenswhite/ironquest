import './settings.styl';
import Settings from '@/components/Settings.vue';
import { Windows } from '@/overwolf/scripts';
import { vuetify } from '@/lib';
import { constants, store } from '@/store';
import Vue from 'vue';

new Vue({
  store,
  vuetify,
  methods: {
    close(): void {
      Windows.getInstance().close(Windows.SETTINGS);
      Windows.getInstance().restore(Windows.RESULTS);
    },
    showResults(): void {
      this.$store
        .dispatch(constants.FIND_PATH)
        .finally(() => Windows.getInstance().close(Windows.SETTINGS));

      Windows.getInstance().minimize(Windows.SETTINGS);
      Windows.getInstance().restore(Windows.RESULTS);
    },
  },
  components: {
    Settings,
  },
}).$mount('#app');
