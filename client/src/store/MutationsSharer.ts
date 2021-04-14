import createMutationsSharer, {
  BroadcastChannelStrategy,
  LocalStorageStratery,
  Strategy,
} from 'vuex-shared-mutations';

import {RootState} from './RootState';

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

export {mutationsSharer};
