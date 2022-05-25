import Vue from 'vue'
import App from './App.vue'
import vuetify from './plugins/vuetify';
import VueAxios from "vue-axios";
import axios from "axios"
import VueRouter from "vue-router";
import Vuex from 'vuex'
import md5 from "js-md5"
import Login from "@/components/Login";
import Upload from "@/components/Upload";
const {ipcRenderer} = window.require('electron')
const config = ipcRenderer.sendSync("get-config")
Vue.config.productionTip = false
Vue.use(Vuex)
const store = new Vuex.Store({
    state: {
        token: null,
        error: null,
        toastMessage: null,
        config:null,
    },
    mutations: {
        error(state, message) {
            if (message.constructor === String)
                state.error = {
                    message: message,
                    onConfirm:null,
                };
            else state.error = message
        },
        toast(state, message) {
            state.toastMessage = message;
        },
        clearError(state) {
            state.error?.onConfirm && state.error?.onConfirm();
            state.error = null;
        },
        clearToast(state) {
            state.toastMessage = null;
        },
        login(state, token) {
            state.token = token
        },
        logout(state) {
            state.token = null
            ipcRenderer.send("set-token",undefined)
            vm.$router.push("/login");
        },
        updateConfig(state)
        {
            state.config = ipcRenderer
                .sendSync("get-config")
            axiosInstance.defaults.baseURL = state.config.server
            state.token = state.config.token;
        }
    }
})

let axiosInstance = axios.create({
    withCredentials: false,
    headers: {
        "Access-Control-Allow-Origin": "*",
        "Access-Control-Allow-Headers": "*",
        "Access-Control-Allow-Methods": "*"
    }
});
Vue.use(VueAxios, axiosInstance);
Vue.use(VueRouter);
Vue.prototype.$md5 = md5;
const routes = [
    {
        path: "/",
        redirect: '/login'
    },
    {
        path: "/upload"  ,
        component: Upload,
    },
    {
        path: "/login",
        component:Login,
    },
]

const router = new VueRouter({
    routes
})


Vue.config.productionTip = false

let vm = new Vue({
    vuetify,
    router,
    store,
    render: h => h(App)
})
axiosInstance.interceptors.request.use(function (config) {
    let token = null;
    let ls = ipcRenderer.sendSync("get-token")
    if (vm.$store.state.token)
        token = vm.$store.state.token;
    else if (ls) {
        token = ls
        vm.$store.commit("login", token);
    }
    config.headers.token = token;
    return config;
});
axiosInstance.interceptors.response.use(function (res) {
    if (res.headers.token) {
        vm.$store.commit("login", res.headers.token);
        // localStorage.setItem("token", res.headers.token);
        ipcRenderer.sendSync("set-token",res.headers.token)
    }
    return res
}, function (error) {
    if (error?.response?.status === 403) {
        vm.$store.commit("logout");
        // localStorage.removeItem("token");
        ipcRenderer.sendSync("set-token",undefined)
        vm.$store.commit("error", {
            message:"您未登录或权限不足，请重新登录",
            onConfirm:()=>{
                vm.$router.push("/login");
            }
        })
    }
    return Promise.reject(error)
});
vm.$mount('#app')



