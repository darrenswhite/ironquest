import { PathFinder } from './';
import { QuestAccessFilter, QuestTypeFilter } from 'ironquest';

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

    PathFinder.getInstance().parameters = {
      name: PathFinder.getInstance().parameters.name || '',
      typeFilter:
        PathFinder.getInstance().parameters.typeFilter || QuestTypeFilter.ALL,
      accessFilter:
        PathFinder.getInstance().parameters.accessFilter ||
        QuestAccessFilter.ALL,
      ironman: PathFinder.getInstance().parameters.ironman || false,
      recommended: PathFinder.getInstance().parameters.recommended || false,
      lampSkills: PathFinder.getInstance().parameters.lampSkills || [],
    };
  }

  saveParameters(): void {
    localStorage.pathFinderParameters = JSON.stringify(
      PathFinder.getInstance().parameters
    );
  }
}
