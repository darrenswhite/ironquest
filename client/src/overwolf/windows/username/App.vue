<template>
  <v-app v-cloak>
    <v-content>
      <v-container fluid>
        <v-row no-gutters>
          <v-text-field
            v-model="name"
            v-on:keyup.enter="showResults"
            label="Username"
            hint="Enter your RuneScape name to retrieve quest and skill information."
            clearable
          ></v-text-field>
        </v-row>
        <v-row no-gutters>
          <v-btn v-on:click="showResults" block>
            Continue
          </v-btn>
        </v-row>
      </v-container>
    </v-content>
  </v-app>
</template>

<script lang="ts">
import Vue from 'vue';
import { Windows } from '@/overwolf/scripts';
import { mapFields } from 'vuex-map-fields';

export default Vue.extend({
  computed: mapFields(['parameters.name']),
  methods: {
    showResults(): void {
      Windows.getInstance().close(Windows.USERNAME);
      Windows.getInstance().restore(Windows.RESULTS);
    },
  },
});
</script>

<style lang="stylus">
#settings {
  text-align: right;
}
</style>
