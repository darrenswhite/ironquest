import Vue from 'vue';
import Multiselect from 'vue-multiselect';
import _ from 'lodash';

Vue.component('multiselect', Multiselect);

Vue.filter('capitalize', (value: string) => {
  return _.capitalize(value);
});
