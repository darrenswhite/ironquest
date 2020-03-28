<template>
  <v-app v-cloak>
    <v-content>
      <actions>
        <div id="settings">
          <v-btn @click="showSettings">
            Settings
          </v-btn>
        </div>
      </actions>
    </v-content>
  </v-app>
</template>

<script lang="ts">
import Vue from 'vue';
import Actions from '@/components/Actions.vue';
import {Windows} from '@/overwolf/scripts';
import {constants} from '@/store';

export default Vue.extend({
  components: {
    Actions,
  },
  mounted() {
    this.$store.dispatch(constants.FIND_PATH);
  },
  methods: {
    showSettings(): void {
      Windows.getInstance().minimize(Windows.RESULTS);
      Windows.getInstance().restore(Windows.SETTINGS);
    },
  },
});
</script>

<style lang="stylus">
#settings {
  text-align: right;
}
</style>
