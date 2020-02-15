import { Hotkeys, Windows } from '@/overwolf/scripts';
import { Cache, PathFinder } from '@/lib';

export class Controller {
  private constructor() {}

  static async init(): Promise<void> {
    Controller.registerHotkeys();
    Cache.getInstance().loadParameters();
    Controller.toggleStartWindow();
  }

  static registerHotkeys(): void {
    Hotkeys.getInstance().setHotkey(
      Hotkeys.TOGGLE,
      Controller.toggleStartWindow
    );
    Hotkeys.getInstance().setHotkey(Hotkeys.QUIT, Controller.relaunch);
  }

  static async toggleStartWindow(): Promise<void> {
    const params = PathFinder.getInstance().parameters;

    if (params.name) {
      Windows.getInstance().toggle(Windows.RESULTS);
    } else {
      Windows.getInstance().toggle(Windows.USERNAME);
    }
  }

  static async relaunch(): Promise<void> {
    overwolf.extensions.relaunch();
  }
}

(() => {
  Controller.init();
})();
