export {};
import { AppStore } from '@/lib';

declare global {
  interface Window {
    store: AppStore;
  }
}
