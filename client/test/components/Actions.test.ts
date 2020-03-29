import Vue from 'vue';
import {createLocalVue, shallowMount} from '@vue/test-utils';
import Vuex, {Store} from 'vuex';
import Vuetify from 'vuetify';
import {getField, updateField} from 'vuex-map-fields';
import Actions from '@/components/Actions.vue';
import {RootState} from '@/store';
import {QuestAccessFilter, QuestTypeFilter} from '@/common/types';

Vue.use(Vuetify);

const localVue = createLocalVue();

localVue.use(Vuex);

describe('Actions.vue', () => {
  let store: Store<RootState>;

  beforeEach(() => {
    store = new Vuex.Store({
      state: {
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
      },
      getters: {
        getField,
      },
      mutations: {
        updateField,
      },
    });
  });

  it('should render component and match snapshot', () => {
    const wrapper = shallowMount(Actions, {store, localVue});

    expect(wrapper.html()).toMatchSnapshot();
  });

  it('should render loading indicator and match snapshot', () => {
    store.state.actions.loading = true;

    const wrapper = shallowMount(Actions, {store, localVue});

    expect(wrapper.html()).toMatchSnapshot();
  });
});
