import {Action, Path, PathFinderError, PathFinderParameters} from 'ironquest';

export interface RootState {
  actions: {
    error: boolean;
    errorResponse?: PathFinderError;
    loading: boolean;
    path?: Path;
    selectedAction?: Action;
  };
  parameters: PathFinderParameters;
}
