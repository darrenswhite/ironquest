import {createVue} from '@/main';
import App from './App.vue';

if ('serviceWorker' in navigator) {
  navigator.serviceWorker.register('service-worker.js');
}

createVue(App);
