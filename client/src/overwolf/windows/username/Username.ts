import './username.styl';
import { Windows } from '@/overwolf/scripts';
import { vuetify } from '@/lib';
import { store } from '@/store';
import { mapFields } from 'vuex-map-fields';
import Vue from 'vue';

new Vue({
  store,
  vuetify,
  computed: mapFields(['parameters.name']),
  methods: {
    showResults(): void {
      Windows.getInstance().close(Windows.USERNAME);
      Windows.getInstance().restore(Windows.RESULTS);
    },
  },
}).$mount('#app');
