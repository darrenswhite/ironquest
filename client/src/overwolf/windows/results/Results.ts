import './results.styl';
import Actions from '@/components/Actions.vue';
import { Windows } from '@/overwolf/scripts';
import { vuetify } from '@/lib';
import { constants, store } from '@/store';
import Vue from 'vue';

new Vue({
  store,
  vuetify,
  methods: {
    showSettings(): void {
      Windows.getInstance().minimize(Windows.RESULTS);
      Windows.getInstance().restore(Windows.SETTINGS);
    },
  },
  components: {
    Actions,
  },
  mounted() {
    this.$store.dispatch(constants.FIND_PATH);
  },
}).$mount('#app');
