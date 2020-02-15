import './username.styl';
import { Windows } from '@/overwolf/scripts';
import { PathFinder, vuetify } from '@/lib';
import Vue from 'vue';

new Vue({
  vuetify,
  data: {
    name: PathFinder.getInstance().parameters.name,
  },
  methods: {
    showResults(): void {
      PathFinder.getInstance().parameters.name = this.name;

      Windows.getInstance().close(Windows.USERNAME);
      Windows.getInstance().restore(Windows.RESULTS);
    },
  },
}).$mount('#app');
