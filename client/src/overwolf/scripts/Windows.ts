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
    return new Promise(async (resolve, reject) => {
      overwolf.windows.obtainDeclaredWindow(name, response => {
        if (response.status === 'success') {
          resolve();
        } else {
          reject(response);
        }
      });
    });
  }

  async getWindowState(name: string): Promise<unknown> {
    return new Promise<unknown>(async (resolve, reject) => {
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

  async close(name: string): Promise<unknown> {
    return new Promise<unknown>(async (resolve, reject) => {
      try {
        await this.obtainWindow(name);
        overwolf.windows.close(name, resolve);
      } catch (e) {
        reject(e);
      }
    });
  }

  async minimize(name: string): Promise<unknown> {
    return new Promise<unknown>(async (resolve, reject) => {
      try {
        await this.obtainWindow(name);
        overwolf.windows.minimize(name, result => {
          if (result.status === 'success') {
            resolve();
          } else {
            reject(result);
          }
        });
      } catch (e) {
        reject(e);
      }
    });
  }

  async restore(name: string): Promise<unknown> {
    return new Promise<unknown>(async (resolve, reject) => {
      try {
        await this.obtainWindow(name);
        overwolf.windows.restore(name, result => {
          if (result.status === 'success') {
            resolve();
          } else {
            reject(result);
          }
        });
      } catch (e) {
        reject(e);
      }
    });
  }

  async toggle(name: string): Promise<unknown> {
    return new Promise<unknown>(async (resolve, reject) => {
      try {
        const state = await this.getWindowState(name);

        if (state === 'minimized' || state === 'closed') {
          await this.restore(name);
        } else {
          await this.minimize(name);
        }

        resolve();
      } catch (e) {
        reject(e);
      }
    });
  }

  getMainWindow(): Window {
    return overwolf.windows.getMainWindow();
  }
}
