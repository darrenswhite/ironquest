import {Hotkeys, Windows} from '@/overwolf/scripts';
import {store} from '@/store';
import {each, every, filter, find, isFunction, map, noop} from 'lodash';

export class Controller {
  static readonly MENU = [
    {
      label: 'Open',
      id: 'open',
      click: Controller.toggleStartWindow,
    },
  ];

  private constructor() {}

  static init(): void {
    Controller.registerHotkeys();
    Controller.registerLaunchTrigger();
    Controller.registerTray();
    Controller.toggleStartWindow();
  }

  static registerHotkeys(): void {
    Hotkeys.getInstance().setHotkey(
      Hotkeys.TOGGLE,
      Controller.toggleStartWindow
    );
    Hotkeys.getInstance().setHotkey(Hotkeys.QUIT, Controller.relaunch);
  }

  static registerLaunchTrigger(): void {
    overwolf.extensions.onAppLaunchTriggered.addListener(
      this.toggleStartWindow
    );
  }

  static registerTray(): void {
    overwolf.os.tray.setMenu(
      {
        menu_items: map(Controller.MENU, item => ({
          label: item.label,
          id: item.id,
        })) as overwolf.os.tray.menu_item,
      } as overwolf.os.tray.ExtensionTrayMenu,
      noop
    );

    overwolf.os.tray.onTrayIconClicked.addListener(
      Controller.toggleStartWindow
    );

    overwolf.os.tray.onTrayIconDoubleClicked.addListener(
      Controller.toggleStartWindow
    );

    overwolf.os.tray.onMenuItemClicked.addListener(e => {
      const item = find(Controller.MENU, ['id', e.item]);

      if (item && isFunction(item.click)) {
        item.click();
      }
    });
  }

  static relaunch(): void {
    overwolf.extensions.relaunch();
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
}
