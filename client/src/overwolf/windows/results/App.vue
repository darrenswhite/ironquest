<template>
  <v-app v-cloak>
    <v-content>
      <actions>
        <div class="settings">
          <v-btn @click="run">
            Refresh
          </v-btn>
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
    this.run();
  },
  methods: {
    run(): void {
      this.$store.dispatch(constants.FIND_PATH);
    },
    showSettings(): void {
      Windows.getInstance().minimize(Windows.RESULTS);
      Windows.getInstance().restore(Windows.SETTINGS);
    },
  },
});
</script>

<style scoped lang="scss">
.settings {
  text-align: right;
}
</style>
