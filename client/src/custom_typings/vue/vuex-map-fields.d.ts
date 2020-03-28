declare module 'vuex-map-fields' {
  export interface UpdateFieldOptions {
    field: string;
    value: unknown;
  }

  export interface MappedFields {
    [key: string]: {
      get(): unknown;
      set(value: unknown): void;
    };
  }

  export function mapFields(
    fields: string[] | {[key: string]: string}
  ): MappedFields;

  export function getField(state: unknown): unknown;

  export function updateField(
    state: unknown,
    options: UpdateFieldOptions
  ): void;
}
