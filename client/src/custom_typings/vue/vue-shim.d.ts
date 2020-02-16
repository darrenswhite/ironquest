declare module '*.vue' {
  import Vue from 'vue';
  export default Vue;
}

declare module 'vuex-shared-mutations' {
  // tslint:disable:no-any
  export default function createMutationsSharer(options: any): any;
}
