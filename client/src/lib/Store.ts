import Vue from 'vue';
import Vuex from 'vuex';
import { createDirectStore } from 'direct-vuex';
import {
  Action,
  Path,
  PathFinderError,
  PathFinderParameters,
  QuestAccessFilter,
  QuestTypeFilter,
} from 'ironquest';
import { head } from 'lodash';
import querystring from 'querystring';
import { getField, updateField } from './MapFields';
import createMutationsSharer from 'vuex-shared-mutations';

const PATH_FINDER_URL = __API__ + '/api/quests/path';

Vue.use(Vuex);

const {
  store,
  rootActionContext,
  moduleActionContext,
  rootGetterContext,
  moduleGetterContext,
} = createDirectStore({
  plugins: [
    createMutationsSharer({
      predicate: [
        'updateField',
        'loadParameters',
        'setError',
        'setParameters',
        'setPath',
        'showLoader',
      ],
    }),
  ],
  state: {
    actions: {
      error: false,
      errorResponse: null as PathFinderError | null,
      loading: false,
      path: null as Path | null,
      selectedAction: null as Action | null | undefined,
    },
    parameters: {
      name: '',
      typeFilter: QuestTypeFilter.ALL,
      accessFilter: QuestAccessFilter.ALL,
      ironman: false,
      recommended: false,
      lampSkills: [],
    } as PathFinderParameters,
  },
  getters: {
    getField,
  },
  mutations: {
    updateField,
    loadParameters(state) {
      if (!localStorage.pathFinderParameters) {
        localStorage.pathFinderParameters = '{}';
      }

      const parameters = JSON.parse(localStorage.pathFinderParameters);

      state.parameters = {
        name: parameters.name || '',
        typeFilter: parameters.typeFilter || QuestTypeFilter.ALL,
        accessFilter: parameters.accessFilter || QuestAccessFilter.ALL,
        ironman: parameters.ironman || false,
        recommended: parameters.recommended || false,
        lampSkills: parameters.lampSkills || [],
      };
    },
    setError(state, response: PathFinderError) {
      state.actions.loading = false;
      state.actions.error = true;
      state.actions.errorResponse = response;
    },
    setParameters(state, parameters: PathFinderParameters) {
      state.parameters = parameters;
    },
    setPath(state, path: Path) {
      path.stats.percentComplete = Math.round(path.stats.percentComplete);

      state.actions.loading = false;
      state.actions.path = path;
      state.actions.selectedAction = head(path.actions);
    },
    showLoader(state) {
      state.actions.loading = true;
      state.actions.path = null;
      state.actions.selectedAction = null;
      state.actions.error = false;
    },
  },
  actions: {
    async findPath(context): Promise<void> {
      const { commit, state } = rootActionContext(context);

      commit.showLoader();

      const url = new URL(PATH_FINDER_URL);

      url.search = querystring.stringify(
        state.parameters as querystring.ParsedUrlQueryInput
      );

      return fetch(url.toString())
        .then(response => {
          if (response.ok) {
            return response.json();
          } else {
            throw response;
          }
        })
        .then(commit.setPath)
        .catch(async response => {
          const json = await response.json();

          commit.setError({
            response: json,
            parameters: Object.assign({}, state.parameters),
          });
        });
    },
  },
});

store.commit.loadParameters();

store.original.subscribe((_, state) => {
  localStorage.pathFinderParameters = JSON.stringify(state.parameters);
});

export {
  store,
  rootActionContext,
  moduleActionContext,
  rootGetterContext,
  moduleGetterContext,
};

export type AppStore = typeof store;

declare module 'vuex' {
  interface Store<S> {
    direct: AppStore;
  }
}
