import {
  Action,
  AjaxError,
  Path,
  PathFinderParameters,
  Quest,
} from '../common/types';

export interface RootState {
  actions: {
    error: boolean;
    errorResponse?: AjaxError;
    loading: boolean;
    path?: Path;
    selectedAction?: Action;
  };
  parameters: PathFinderParameters;
  quests: {
    error: boolean;
    errorResponse?: AjaxError;
    loading: boolean;
    quests: Quest[];
  };
}
