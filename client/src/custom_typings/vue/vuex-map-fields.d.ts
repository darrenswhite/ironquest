declare module 'vuex-map-fields' {
  import {Computed, Mapper} from 'vuex';

  export interface UpdateFieldOptions {
    field: string;
    value: unknown;
  }

  export const mapFields: Mapper<Computed>;

  export function getField<S>(state: S): unknown;

  export function updateField<S>(state: S, options: UpdateFieldOptions): void;
}
