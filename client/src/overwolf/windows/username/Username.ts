import './username.styl';
import { Windows } from '@/overwolf/scripts';
import { mapFields, store, vuetify } from '@/lib';
import Vue from 'vue';

new Vue({
  store: store.original,
  vuetify,
  computed: mapFields(['parameters.name']),
  methods: {
    showResults(): void {
      Windows.getInstance().close(Windows.USERNAME);
      Windows.getInstance().restore(Windows.RESULTS);
    },
  },
}).$mount('#app');
