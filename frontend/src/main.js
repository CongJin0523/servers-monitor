import { createApp } from 'vue'
import App from './App.vue'
import router from "@/router"
import axios from 'axios'
import 'element-plus/theme-chalk/dark/css-vars.css'
import 'element-plus/theme-chalk/el-message.css'
import 'element-plus/dist/index.css'
import 'flag-icon-css/css/flag-icons.min.css'
axios.defaults.baseURL = 'http://127.0.0.1:8080'

createApp(App).use(router).mount('#app')

