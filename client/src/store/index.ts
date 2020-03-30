import Vue from 'vue';
import Vuex, {ActionContext, ActionTree, MutationTree, Store} from 'vuex';
import {
  Path,
  PathFinderError,
  PathFinderParameters,
  QuestAccessFilter,
  QuestTypeFilter,
} from '@/common/types';
import {RootState} from './RootState';
import * as constants from './constants';
import {head} from 'lodash';
import querystring from 'querystring';
import {getField, updateField} from 'vuex-map-fields';
import {plugins} from './plugins';

const PATH_FINDER_URL = __API__ + '/api/quests/path';

Vue.use(Vuex);

const state: RootState = {
  actions: {
    error: false,
    errorResponse: undefined,
    loading: false,
    path: undefined,
    selectedAction: undefined,
  },
  parameters: {
    name: '',
    typeFilter: QuestTypeFilter.ALL,
    accessFilter: QuestAccessFilter.ALL,
    ironman: false,
    recommended: false,
    lampSkills: [],
  },
};

const getters = {
  getField,
};

const mutations: MutationTree<RootState> = {
  updateField,
  [constants.SET_ERROR](state: RootState, response: PathFinderError): void {
    state.actions.loading = false;
    state.actions.error = true;
    state.actions.errorResponse = response;
  },
  [constants.SET_PARAMETERS](
    state: RootState,
    parameters: PathFinderParameters
  ): void {
    state.parameters = parameters;
  },
  [constants.SET_PATH](state: RootState, path: Path): void {
    path.stats.percentComplete = Math.round(path.stats.percentComplete);

    state.actions.loading = false;
    state.actions.path = path;
    state.actions.selectedAction = head(path.actions);
  },
  [constants.SHOW_LOADER](state: RootState): void {
    state.actions.loading = true;
    state.actions.path = undefined;
    state.actions.selectedAction = undefined;
    state.actions.error = false;
  },
};

const actions: ActionTree<RootState, RootState> = {
  async [constants.FIND_PATH](
    context: ActionContext<RootState, RootState>
  ): Promise<void> {
    context.commit(constants.SHOW_LOADER);

    const url = new URL(PATH_FINDER_URL);

    url.search = querystring.stringify(
      context.state.parameters as querystring.ParsedUrlQueryInput
    );

    return fetch(url.toString())
      .then(async response => {
        const json = await response.json();

        if (response.ok) {
          return json;
        } else {
          throw json;
        }
      })
      .then(response => context.commit(constants.SET_PATH, response))
      .catch(response => {
        context.commit(constants.SET_ERROR, {
          response: response,
          parameters: Object.assign({}, context.state.parameters),
        });
      });
  },
};

const store = new Store<RootState>({
  plugins,
  state,
  getters,
  mutations,
  actions,
});

export {RootState, constants, store};
