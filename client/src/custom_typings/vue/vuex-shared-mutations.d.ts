declare module 'vuex-shared-mutations' {
  import { Plugin } from 'vuex';

  export type Predicate =
    | string[]
    | ((mutation: { type: string; payload: unknown }, state: unknown) => void);

  export interface Strategy {
    addEventListener(fn: Function): unknown;
    share(message: unknown): unknown;
  }

  export interface Options {
    predicate: Predicate;
    strategy?: Strategy;
  }

  export default function createMutationsSharer<T>(
    options: Options
  ): Plugin<T>;

  export class BroadcastChannelStrategy implements Strategy {
    static available(): boolean;
    constructor(options: { key: string });
    addEventListener(fn: Function): unknown;
    share(message: unknown): unknown;
  }

  export class LocalStorageStratery implements Strategy {
    static available(): boolean;
    constructor(options: { key: string });
    addEventListener(fn: Function): unknown;
    share(message: unknown): unknown;
  }
}
