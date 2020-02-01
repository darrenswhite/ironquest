export {};
import { PathFinder } from 'ironquest';

declare global {
  interface Window {
    pathFinder: PathFinder;
  }
}
