<template>
  <v-app v-cloak>
    <v-main>
      <v-container fluid>
        <settings />
        <v-row>
          <v-col col="6">
            <v-btn
              block
              @click="close"
            >
              Cancel
            </v-btn>
          </v-col>
          <v-col col="6">
            <v-btn
              block
              @click="showResults"
            >
              Save
            </v-btn>
          </v-col>
        </v-row>
      </v-container>
    </v-main>
  </v-app>
</template>

<script lang="ts">
import Vue from 'vue';
import Settings from '@/components/Settings.vue';
import {Windows} from '@/overwolf/scripts';
import {constants} from '@/store';

export default Vue.extend({
  components: {
    Settings,
  },
  methods: {
    close(): void {
      Windows.getInstance().close(Windows.SETTINGS);
      Windows.getInstance().restore(Windows.RESULTS);
    },
    showResults(): void {
      this.$store
        .dispatch(constants.ACTIONS.FIND_PATH)
        .finally(() => Windows.getInstance().close(Windows.SETTINGS));

      Windows.getInstance().minimize(Windows.SETTINGS);
      Windows.getInstance().restore(Windows.RESULTS);
    },
  },
});
</script>
