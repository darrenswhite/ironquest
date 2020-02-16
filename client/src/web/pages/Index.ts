import './index.styl';
import Actions from '../../components/Actions.vue';
import Settings from '../../components/Settings.vue';
import { mapFields, store, vuetify } from '@/lib';
import Vue from 'vue';

new Vue({
  store: store.original,
  vuetify,
  methods: {
    run(): void {
      this.$store.direct.dispatch.findPath();
    },
  },
  computed: mapFields(['actions.loading']),
  components: {
    Actions,
    Settings,
  },
}).$mount('#app');
