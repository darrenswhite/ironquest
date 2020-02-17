import { RootState } from './RootState';
import VuexPersistence from 'vuex-persist';
import createMutationsSharer, {
  BroadcastChannelStrategy,
  LocalStorageStratery,
  Strategy,
} from 'vuex-shared-mutations';

function getStrategy(): Strategy {
  let strategy;

  if (BroadcastChannelStrategy.available()) {
    strategy = new BroadcastChannelStrategy({
      key: 'ironquest',
    });
  }

  if (LocalStorageStratery.available()) {
    strategy = new LocalStorageStratery({
      key: 'ironquest',
    });
  }

  if (!strategy) {
    throw new Error('No strategies available');
  }

  return strategy;
}

const mutationsSharer = createMutationsSharer<RootState>({
  predicate: () => true,
  strategy: getStrategy(),
});

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

export { plugins };
