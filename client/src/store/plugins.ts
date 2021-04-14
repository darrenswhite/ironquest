import VuexPersistence from 'vuex-persist';

import {mutationsSharer} from './MutationsSharer';
import {RootState} from './RootState';

const vuexLocal = new VuexPersistence<RootState>({
  storage: window.localStorage,
  reducer: state => ({
    parameters: state.parameters,
  }),
});

const plugins = [vuexLocal.plugin];

if (typeof overwolf !== 'undefined') {
  plugins.push(mutationsSharer);
}

export {plugins};
