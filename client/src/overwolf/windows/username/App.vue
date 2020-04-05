<template>
  <v-app v-cloak>
    <v-content>
      <v-container fluid>
        <v-row no-gutters>
          <v-text-field
            v-model="name"
            label="Username"
            hint="Enter your RuneScape name to retrieve quest and skill information."
            :prepend-icon="mdiAccount"
            persistent-hint
            clearable
            @keyup.enter="showResults"
          />
        </v-row>
        <v-row no-gutters>
          <v-btn
            block
            class="mt-4"
            @click="showResults"
          >
            Continue
          </v-btn>
        </v-row>
      </v-container>
    </v-content>
  </v-app>
</template>

<script lang="ts">
import Vue from 'vue';
import {Windows} from '@/overwolf/scripts';
import {mapFields} from 'vuex-map-fields';
import {mdiAccount} from '@mdi/js';

export default Vue.extend({
  computed: {
    mdiAccount: () => mdiAccount,
    ...mapFields(['parameters.name']),
  },
  methods: {
    showResults(): void {
      Windows.getInstance().close(Windows.USERNAME);
      Windows.getInstance().restore(Windows.RESULTS);
    },
  },
});
</script>
