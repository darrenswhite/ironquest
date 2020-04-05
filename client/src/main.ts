import {store} from '@/store';
import Vue, {VueConstructor} from 'vue';
import Vuetify from 'vuetify/lib';
import colors from 'vuetify/lib/util/colors';

import '@mdi/font/css/materialdesignicons.css';
import '@/styles/base.scss';

Vue.config.productionTip = false;

Vue.use(Vuetify);

const vuetify = new Vuetify({
  theme: {
    dark: true,
    themes: {
      dark: {
        primary: colors.amber.darken3,
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
