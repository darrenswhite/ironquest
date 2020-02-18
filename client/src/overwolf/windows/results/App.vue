<template>
  <v-app v-cloak>
    <v-content>
      <actions>
        <div id="settings">
          <v-btn v-on:click="showSettings">
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
import { Windows } from '@/overwolf/scripts';
import { constants, store } from '@/store';

export default Vue.extend({
  methods: {
    showSettings(): void {
      Windows.getInstance().minimize(Windows.RESULTS);
      Windows.getInstance().restore(Windows.SETTINGS);
    },
  },
  components: {
    Actions,
  },
  mounted() {
    this.$store.dispatch(constants.FIND_PATH);
  },
});
</script>

<style lang="stylus">
#settings {
  text-align: right;
}
</style>
