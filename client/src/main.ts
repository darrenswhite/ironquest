import { store } from '@/store';
import Vue, { VueConstructor } from 'vue';
import Vuetify from 'vuetify';
import { capitalize } from 'lodash';

import 'vuetify/dist/vuetify.min.css';
import '@/styles/base.styl';

Vue.config.productionTip = false;

Vue.filter('capitalize', capitalize);

Vue.use(Vuetify);

const vuetify = new Vuetify({
  theme: {
    dark: true,
    themes: {
      dark: {
        primary: '#ff8c00',
      },
    },
  },
});

export function createVue(app: VueConstructor<Vue>): Vue {
  return new Vue({
    store,
    vuetify,
    render: h => h(app),
  }).$mount('#app');
}
