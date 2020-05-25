export class Hotkeys {
  static readonly TOGGLE = 'toggle_IronQuest';
  static readonly QUIT = 'quit_IronQuest';

  private static instance: Hotkeys;

  private constructor() {}

  static getInstance(): Hotkeys {
    if (!Hotkeys.instance) {
      Hotkeys.instance = new Hotkeys();
    }

    return Hotkeys.instance;
  }

  setHotkey(actionId: string, callback: () => void): void {
    overwolf.settings.registerHotKey(actionId, result => {
      if (result.success) {
        callback();
      } else {
        console.error(`IronQuest: Failed to register hotkey ${actionId}`);
      }
    });
  }
}
