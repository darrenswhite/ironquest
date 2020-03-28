<template>
  <v-app v-cloak>
    <v-app-bar app>
      <v-toolbar-title>IronQuest</v-toolbar-title>
    </v-app-bar>
    <v-content>
      <v-container fluid>
        <v-row>
          <v-col
            offset-lg="2"
            lg="8"
            offset-xl="2"
            xl="8"
          >
            <settings />
          </v-col>
        </v-row>
        <v-row>
          <v-col
            offset-lg="2"
            lg="8"
            offset-xl="2"
            xl="8"
          >
            <v-btn
              :disabled="loading"
              block
              @click="run"
            >
              Run
            </v-btn>
          </v-col>
        </v-row>
        <v-row>
          <v-col
            offset-lg="2"
            lg="8"
            offset-xl="2"
            xl="8"
          >
            <actions />
          </v-col>
        </v-row>
      </v-container>
    </v-content>
  </v-app>
</template>

<script lang="ts">
import Vue from 'vue';
import Actions from '@/components/Actions.vue';
import Settings from '@/components/Settings.vue';
import {constants} from '@/store';
import {mapFields} from 'vuex-map-fields';

export default Vue.extend({
  components: {
    Actions,
    Settings,
  },
  computed: mapFields(['actions.loading']),
  methods: {
    run(): void {
      this.$store.dispatch(constants.FIND_PATH);
    },
  },
});
</script>
