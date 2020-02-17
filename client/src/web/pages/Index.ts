import './index.styl';
import Actions from '../../components/Actions.vue';
import Settings from '../../components/Settings.vue';
import { vuetify } from '@/lib';
import { constants, store } from '@/store';
import { mapFields } from 'vuex-map-fields';
import Vue from 'vue';

new Vue({
  store,
  vuetify,
  methods: {
    run(): void {
      this.$store.dispatch(constants.FIND_PATH);
    },
  },
  computed: mapFields(['actions.loading']),
  components: {
    Actions,
    Settings,
  },
}).$mount('#app');
