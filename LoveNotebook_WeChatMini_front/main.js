import App from './App'
import Vue from 'vue'

import uView from "uview-ui";
import * as utils from './utils/utils.js' // 工具文件
import * as http from './utils/http.js' // http请求接口
import * as common from './utils/common.js' // 公共文件
import storage from 'utils/storage.js' // 缓存文件
import config from '@/config.js' // 配置

// 定义全局
Vue.prototype.$storage = storage
Vue.prototype.$http = http
Vue.prototype.$utils = utils
Vue.prototype.$common = common
Vue.prototype.$config = config

Vue.use(uView);

Vue.config.productionTip = false
App.mpType = 'app'
const app = new Vue({
	...App
})
app.$mount()