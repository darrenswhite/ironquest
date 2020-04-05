<template>
  <v-app v-cloak>
    <v-app-bar app>
      <v-app-bar-nav-icon @click="drawer = !drawer" />
      <v-toolbar-title>IronQuest</v-toolbar-title>
    </v-app-bar>
    <v-navigation-drawer
      v-model="drawer"
      absolute
      temporary
    >
      <v-list dense>
        <v-list-item
          v-for="(item, index) in items"
          :key="index"
          :href="item.link"
          target="_blank"
          link
        >
          <v-list-item-icon>
            <v-icon>{{ item.icon }}</v-icon>
          </v-list-item-icon>

          <v-list-item-content>
            <v-list-item-title>{{ item.title }}</v-list-item-title>
          </v-list-item-content>
        </v-list-item>
      </v-list>
    </v-navigation-drawer>
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

const GITHUB_URL = 'https://github.com/darrenswhite/ironquest';
const PAYPAL_URL =
  'https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=ZJZ94RDWF6GU4&item_name=IronQuest&currency_code=GBP&source=url';

export default Vue.extend({
  components: {
    Actions,
    Settings,
  },
  data() {
    return {
      drawer: false,
      items: [
        {title: 'GitHub', icon: 'mdi-github', link: GITHUB_URL},
        {title: 'Donate', icon: 'mdi-currency-usd-circle', link: PAYPAL_URL},
      ],
    };
  },
  computed: mapFields(['actions.loading']),
  methods: {
    run(): void {
      this.$store.dispatch(constants.FIND_PATH);
    },
  },
});
</script>
