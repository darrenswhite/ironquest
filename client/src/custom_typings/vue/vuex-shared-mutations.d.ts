declare module 'vuex-shared-mutations' {
  import {Plugin} from 'vuex';

  export type Strategy = unknown;

  export interface Options {
    predicate: Predicate;
    strategy?: Strategy;
  }

  export default function createMutationsSharer<T>(options: Options): Plugin<T>;

  export class BroadcastChannelStrategy implements Strategy {
    static available(): boolean;
    constructor(options: {key: string});
  }

  export class LocalStorageStratery implements Strategy {
    static available(): boolean;
    constructor(options: {key: string});
  }
}
