<template>
  <v-app v-cloak>
    <v-content>
      <v-container fluid>
        <settings></settings>
        <v-row>
          <v-col col="6">
            <v-btn v-on:click="close" block>
              Cancel
            </v-btn>
          </v-col>
          <v-col col="6">
            <v-btn v-on:click="showResults" block>
              Save
            </v-btn>
          </v-col>
        </v-row>
      </v-container>
    </v-content>
  </v-app>
</template>

<script lang="ts">
import Vue from 'vue';
import Settings from '@/components/Settings.vue';
import { Windows } from '@/overwolf/scripts';
import { constants, store } from '@/store';

export default Vue.extend({
  methods: {
    close(): void {
      Windows.getInstance().close(Windows.SETTINGS);
      Windows.getInstance().restore(Windows.RESULTS);
    },
    showResults(): void {
      this.$store
        .dispatch(constants.FIND_PATH)
        .finally(() => Windows.getInstance().close(Windows.SETTINGS));

      Windows.getInstance().minimize(Windows.SETTINGS);
      Windows.getInstance().restore(Windows.RESULTS);
    },
  },
  components: {
    Settings,
  },
});
</script>
