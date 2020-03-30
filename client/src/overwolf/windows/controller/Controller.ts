import {Hotkeys, Windows} from '@/overwolf/scripts';
import {store} from '@/store';
import {each, every, filter} from 'lodash';

export class Controller {
  private constructor() {}

  static init(): void {
    Controller.registerHotkeys();
    Controller.registerLaunchTrigger();
    Controller.toggleStartWindow();
  }

  static registerLaunchTrigger(): void {
    overwolf.extensions.onAppLaunchTriggered.addListener(
      this.toggleStartWindow
    );
  }

  static registerHotkeys(): void {
    Hotkeys.getInstance().setHotkey(
      Hotkeys.TOGGLE,
      Controller.toggleStartWindow
    );
    Hotkeys.getInstance().setHotkey(Hotkeys.QUIT, Controller.relaunch);
  }

  static async toggleStartWindow(): Promise<void> {
    const windows = [Windows.USERNAME, Windows.RESULTS, Windows.SETTINGS];
    const windowStates = await Windows.getInstance().getWindowStates(windows);

    if (every(windowStates, ['state', 'closed'])) {
      const parameters = store.state.parameters;

      if (parameters.name) {
        Windows.getInstance().toggle(Windows.RESULTS);
      } else {
        Windows.getInstance().toggle(Windows.USERNAME);
      }
    } else {
      const minimized = filter(
        windowStates,
        state => state.state === 'minimized'
      );

      each(minimized, windowState => {
        Windows.getInstance().restore(windowState.name);
      });
    }
  }

  static relaunch(): void {
    overwolf.extensions.relaunch();
  }
}
