import { createApp } from 'vue'
import App from './App.vue'
import router from "@/router"
import axios from 'axios'
import 'element-plus/theme-chalk/dark/css-vars.css'
import 'element-plus/theme-chalk/el-message.css'
import 'element-plus/dist/index.css'
import '@/assets/css/element.less'
import 'flag-icon-css/css/flag-icons.min.css'
import {createPinia} from "pinia"
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
axios.defaults.baseURL = 'http://127.0.0.1:8080'

const app = createApp(App)
const pinia = createPinia()
app.use(pinia)
pinia.use(piniaPluginPersistedstate)
app.use(router)

app.mount('#app')

