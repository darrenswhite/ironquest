import {store} from '@/store';
import Vue, {VueConstructor} from 'vue';
import Vuetify from 'vuetify/lib';
import colors from 'vuetify/lib/util/colors';

import '@/styles/base.scss';

Vue.config.productionTip = false;

Vue.use(Vuetify);

if ('serviceWorker' in navigator) {
  navigator.serviceWorker.register('/service-worker.js');
}

const vuetify = new Vuetify({
  icons: {
    iconfont: 'mdiSvg',
  },
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
