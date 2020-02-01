import { PathFinder } from './';

export class Cache {
  private static instance: Cache;

  private constructor() {}

  static getInstance(): Cache {
    if (!Cache.instance) {
      Cache.instance = new Cache();
    }

    return Cache.instance;
  }

  loadParameters(): void {
    if (!localStorage.pathFinderParameters) {
      localStorage.pathFinderParameters = '{}';
    }

    PathFinder.getInstance().parameters = JSON.parse(
      localStorage.pathFinderParameters
    );
  }

  saveParameters(): void {
    localStorage.pathFinderParameters = JSON.stringify(
      PathFinder.getInstance().parameters
    );
  }
}
