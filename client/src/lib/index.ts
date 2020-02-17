import Vue from 'vue';
import { capitalize } from 'lodash';

Vue.config.productionTip = false;

Vue.filter('capitalize', capitalize);

export { vuetify } from './Vuetify';
