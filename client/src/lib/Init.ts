import Vue from 'vue';
import _ from 'lodash';

Vue.filter('capitalize', (value: string) => {
  return _.capitalize(value);
});
