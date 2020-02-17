import { isArray, map, mapValues, zipObject } from 'lodash';

export interface UpdateFieldOptions {
  field: string;
  value: unknown;
}

export function mapFields(fields: string[] | { [key: string]: string }) {
  const fieldsObject = isArray(fields) ? arrayToObject(fields) : fields;

  return mapValues(fieldsObject, field => {
    return {
      get(): unknown {
        const store = ((this as unknown) as Vue).$store;

        return store.getters.getField(field);
      },
      set(value: unknown) {
        const store = ((this as unknown) as Vue).$store;

        return store.direct.commit.updateField({
          field,
          value,
        });
      },
    };
  });
}

function arrayToObject(fields: string[]) {
  return zipObject(
    map(fields, field => {
      const parts = field.split(/[.[\]]+/);
      return parts[parts.length - 1];
    }),
    fields
  );
}

// tslint:disable:no-any
export function getField(state: any) {
  return (field: string) =>
    field.split(/[.[\]]+/).reduce((prev, key) => prev[key], state);
}

// tslint:disable:no-any
export function updateField(state: any, options: UpdateFieldOptions) {
  options.field.split(/[.[\]]+/).reduce((prev, key, index, array) => {
    if (array.length === index + 1) {
      prev[key] = options.value;
    }

    return prev[key];
  }, state);
}
