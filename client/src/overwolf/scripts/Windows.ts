import {reduce} from 'lodash';

export interface WindowState {
  name: string;
  state: string;
}

export class Windows {
  static readonly CONTROLLER = 'controller';
  static readonly RESULTS = 'results';
  static readonly SETTINGS = 'settings';
  static readonly USERNAME = 'username';

  private static instance: Windows;

  private constructor() {}

  static getInstance(): Windows {
    if (!Windows.instance) {
      Windows.instance = new Windows();
    }

    return Windows.instance;
  }

  async obtainWindow(name: string): Promise<unknown> {
    return new Promise((resolve, reject) => {
      overwolf.windows.obtainDeclaredWindow(name, response => {
        if (response.status === 'success') {
          resolve();
        } else {
          reject(response);
        }
      });
    });
  }

  async getWindowState(name: string): Promise<string> {
    return new Promise<string>((resolve, reject) => {
      try {
        overwolf.windows.getWindowState(name, state => {
          if (state.status === 'success') {
            resolve(state.window_state_ex);
          } else {
            reject(state);
          }
        });
      } catch (e) {
        reject(e);
      }
    });
  }

  async getWindowStates(names: Array<string>): Promise<Array<WindowState>> {
    return reduce(
      names,
      async (arr: Promise<Array<WindowState>>, name: string) => {
        const act = await arr;
        act.push({
          name: name,
          state: await Windows.getInstance().getWindowState(name),
        });
        return Promise.resolve(act);
      },
      Promise.resolve([])
    );
  }

  async close(name: string): Promise<void> {
    await this.obtainWindow(name);
    overwolf.windows.close(name);
  }

  async minimize(name: string): Promise<void> {
    await this.obtainWindow(name);
    overwolf.windows.minimize(name);
  }

  async restore(name: string): Promise<void> {
    await this.obtainWindow(name);
    overwolf.windows.restore(name);
  }

  async toggle(name: string): Promise<void> {
    const state = await this.getWindowState(name);

    if (state === 'minimized' || state === 'closed') {
      await this.restore(name);
    } else {
      await this.minimize(name);
    }
  }

  getMainWindow(): Window {
    return overwolf.windows.getMainWindow();
  }
}
