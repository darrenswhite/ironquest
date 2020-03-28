import {Hotkeys, Windows} from '@/overwolf/scripts';
import {store} from '@/store';

export class Controller {
  private constructor() {}

  static init(): void {
    Controller.registerHotkeys();
    Controller.registerLaunchTrigger();
    Controller.toggleStartWindow();
  }

  static registerLaunchTrigger(): void {
    overwolf.extensions.onAppLaunchTriggered.addListener(this.toggleStartWindow);
  }

  static registerHotkeys(): void {
    Hotkeys.getInstance().setHotkey(
      Hotkeys.TOGGLE,
      Controller.toggleStartWindow
    );
    Hotkeys.getInstance().setHotkey(Hotkeys.QUIT, Controller.relaunch);
  }

  static toggleStartWindow(): void {
    const parameters = store.state.parameters;

    if (parameters.name) {
      Windows.getInstance().toggle(Windows.RESULTS);
    } else {
      Windows.getInstance().toggle(Windows.USERNAME);
    }
  }

  static relaunch(): void {
    overwolf.extensions.relaunch();
  }
}
