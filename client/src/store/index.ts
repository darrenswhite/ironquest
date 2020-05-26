import Vue from 'vue';
import Vuex, {
  ActionContext,
  ActionTree,
  GetterTree,
  MutationTree,
  Store,
} from 'vuex';
import {
  AjaxError,
  Path,
  PathFinderParameters,
  Quest,
  QuestAccessFilter,
  QuestPriority,
  QuestTypeFilter,
} from '@/common/types';
import {RootState} from './RootState';
import * as constants from './constants';
import axios from 'axios';
import qs from 'qs';
import {extend, filter, head, map} from 'lodash';
import {getField, updateField} from 'vuex-map-fields';
import {plugins} from './plugins';

const QUESTS_URL = __API__ + '/quests';
const PATH_FINDER_URL = QUESTS_URL + '/path';

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
  quests: {
    error: false,
    errorResponse: undefined,
    loading: false,
    quests: [],
  },
};

const getters: GetterTree<RootState, RootState> = {
  [constants.GETTERS.GET_FIELD]: getField,
  [constants.GETTERS.GET_QUESTS_PARAMETERS]: (state: RootState) => ({
    name: state.parameters.name,
    typeFilter: state.parameters.typeFilter,
    accessFilter: state.parameters.accessFilter,
  }),
};

const mutations: MutationTree<RootState> = {
  [constants.MUTATIONS.SET_ACTIONS_ERROR](
    state: RootState,
    error: AjaxError
  ): void {
    state.actions.loading = false;
    state.actions.error = true;
    state.actions.errorResponse = error;
  },
  [constants.MUTATIONS.SET_ACTIONS_LOADING](state: RootState): void {
    state.actions.loading = true;
    state.actions.path = undefined;
    state.actions.selectedAction = undefined;
    state.actions.error = false;
  },
  [constants.MUTATIONS.SET_ACTIONS_PATH](state: RootState, path: Path): void {
    path.stats.percentComplete = Math.round(path.stats.percentComplete);

    state.actions.loading = false;
    state.actions.path = path;
    state.actions.selectedAction = head(path.actions);
  },
  [constants.MUTATIONS.SET_PARAMETERS](
    state: RootState,
    parameters: PathFinderParameters
  ): void {
    state.parameters = parameters;
  },
  [constants.MUTATIONS.SET_QUESTS_ERROR](
    state: RootState,
    error: AjaxError
  ): void {
    state.quests.loading = false;
    state.quests.error = true;
    state.quests.errorResponse = error;
  },
  [constants.MUTATIONS.SET_QUESTS_LOADING](state: RootState): void {
    state.quests.loading = true;
    state.quests.quests = [];
    state.actions.error = false;
  },
  [constants.MUTATIONS.SET_QUESTS](state: RootState, quests: Quest[]): void {
    quests = filter(quests, quest => quest.id >= 0);
    quests = map(quests, quest => {
      quest.priority = QuestPriority.NORMAL;
      return quest;
    });

    state.quests.loading = false;
    state.quests.quests = quests;
  },
  [constants.MUTATIONS.UPDATE_FIELD]: updateField,
};

const actions: ActionTree<RootState, RootState> = {
  async [constants.ACTIONS.FIND_PATH](
    context: ActionContext<RootState, RootState>
  ): Promise<void> {
    context.commit(constants.MUTATIONS.SET_ACTIONS_LOADING);

    const params = context.state.parameters;

    return axios
      .get(PATH_FINDER_URL, {
        params,
        paramsSerializer(params) {
          return qs.stringify(params, {
            arrayFormat: 'repeat',
          });
        },
      })
      .then(response =>
        context.commit(constants.MUTATIONS.SET_ACTIONS_PATH, response.data)
      )
      .catch(error => {
        console.error('Failed to find path', error);
        context.commit(constants.MUTATIONS.SET_ACTIONS_ERROR, {
          error: error,
          parameters: extend({}, params),
        });
      });
  },
  async [constants.ACTIONS.LOAD_QUESTS](
    context: ActionContext<RootState, RootState>
  ): Promise<void> {
    context.commit(constants.MUTATIONS.SET_QUESTS_LOADING);

    const params = context.getters[constants.GETTERS.GET_QUESTS_PARAMETERS];

    return axios
      .get(QUESTS_URL, {
        params,
        paramsSerializer(params) {
          return qs.stringify(params, {
            arrayFormat: 'repeat',
          });
        },
      })
      .then(response => {
        context.commit(constants.MUTATIONS.SET_QUESTS, response.data);
      })
      .catch(error => {
        console.error('Failed to load quests', error);
        context.commit(constants.MUTATIONS.SET_QUESTS_ERROR, {
          error: error,
          parameters: extend({}, params),
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

export type ComputedMapper<S> = {
  [K in keyof S]: () => S[K];
};

export {RootState, constants, store};
