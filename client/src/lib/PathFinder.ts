import { Path, PathFinderListener, PathFinderParameters } from 'ironquest';
import { Cache } from './';
import querystring from 'querystring';

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

    const url = new URL(PATH_FINDER_URL);

    url.search = querystring.stringify(
      this.parameters as querystring.ParsedUrlQueryInput
    );

    fetch(url.toString())
      .then(response => {
        if (response.ok) {
          return response.json();
        } else {
          throw response;
        }
      })
      .then((path: Path) =>
        this.listeners.forEach(listener => listener.success(path))
      )
      .catch(async response => {
        const json = await response.json();

        this.listeners.forEach(listener => listener.failure({
          response: json,
          parameters: this.parameters
        }));
      });
  }
}
