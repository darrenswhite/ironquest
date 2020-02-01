import { Path, PathFinderListener, PathFinderParameters } from 'ironquest';
import { Cache } from './';

const PATH_FINDER_URL = 'https://iron-quest.herokuapp.com/api/quests/path';

export class PathFinder {
  listeners = new Array<PathFinderListener>();
  parameters: PathFinderParameters = {};

  private constructor() {}

  static getInstance(): PathFinder {
    const mainWindow =
      typeof overwolf !== 'undefined'
        ? overwolf.windows.getMainWindow()
        : window;

    if (!mainWindow.pathFinder) {
      mainWindow.pathFinder = new PathFinder();
    }

    return mainWindow.pathFinder;
  }

  find(): void {
    Cache.getInstance().saveParameters();

    this.listeners.forEach(listener => listener.start());

    $.get({
      url: PATH_FINDER_URL,
      data: this.parameters,
    }).then(
      (path: Path) =>
        this.listeners.forEach(listener => listener.success(path)),
      (response: unknown) =>
        this.listeners.forEach(listener => listener.failure(response))
    );
  }
}
