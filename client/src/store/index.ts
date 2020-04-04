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
import axios from 'axios';
import qs from 'qs';
import {extend, head} from 'lodash';
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
    questPriorities: {},
  },
};

const getters = {
  [constants.GET_FIELD]: getField,
};

const mutations: MutationTree<RootState> = {
  [constants.UPDATE_FIELD]: updateField,
  [constants.SET_ERROR](state: RootState, error: PathFinderError): void {
    state.actions.loading = false;
    state.actions.error = true;
    state.actions.errorResponse = error;
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

    return axios
      .get(PATH_FINDER_URL, {
        params: context.state.parameters,
        paramsSerializer(params) {
          return qs.stringify(params, {
            arrayFormat: 'repeat',
          });
        },
      })
      .then(response => context.commit(constants.SET_PATH, response.data))
      .catch(error => {
        console.error('Failed to find path', error);
        context.commit(constants.SET_ERROR, {
          error: error,
          parameters: extend({}, context.state.parameters),
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
