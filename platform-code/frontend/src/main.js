import Vue from 'vue'
import '@mdi/font/css/materialdesignicons.css'
import App from './App.vue'
import vuetify from './plugins/vuetify';
import VueAxios from "vue-axios";
import axios from "axios"
import VueRouter from "vue-router";
import ExperimentPage from "./components/experiment/ExperimentPage";
import ExperimentDetail from "./components/experiment/ExperimentDetail";
import ExperimentSummary from "./components/experiment/ExperimentSummary";
import Vuex from 'vuex'
import Login from "./components/share/Login";
import md5 from "js-md5"
import moment from "moment";
import ManagePage from "@/components/manage/ManagePage";
import QueueDetail from "@/components/manage/queue/QueueDetail";
import ExperimentManage from "@/components/manage/queue/ExperimentManage";
import StudentManage from "@/components/manage/queue/StudentManage";
import CodeReview from "@/components/manage/queue/CodeReview";
import Judging from "@/components/experiment/Judging";
import PeekJudging from "@/components/manage/queue/PeekJudging";
import TestPointManage from "@/components/manage/queue/TestPointManage";

import * as fundebug from "fundebug-javascript";
import fundebugVue from "fundebug-vue";
fundebug.apikey = "f5545b3c916b8adcac13b06c9b421276bf7d046037ac77a2374c35b3caaf70da"
fundebugVue(fundebug, Vue);

moment.locale('zh-cn')
Vue.use(Vuex)
const store = new Vuex.Store({
    state: {
        token: null,
        error: null,
        warning: null,
        toastMessage: null,
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
        warning(state, message) {
            if (message.constructor === String)
                state.warning = {
                    message: message,
                    onConfirm:null,
                };
            else state.warning = message
        },
        toast(state, message) {
            state.toastMessage = message;
        },
        clearError(state) {
            state.error?.onConfirm && state.error?.onConfirm();
            state.error = null;
        },
        clearWarning(state) {
            state.warning?.onConfirm && state.warning?.onConfirm();
            state.warning = null;
        },
        clearToast(state) {
            state.toastMessage = null;
        },
        login(state, token) {
            state.token = token
        },
        logout(state) {
            state.token = null
            localStorage.removeItem("token");
        },
    }
})

let axiosInstance = axios.create({
    baseURL: process.env.VUE_APP_BASE,
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
    {path: "/", redirect: '/exp'},
    {path: "/login", component: Login},
    {
        path: '/exp',
        component: ExperimentPage,
        children: [
            {
                path: 'detail/:id',
                component: ExperimentDetail,
            },
            {
                path: 'summary',
                component: ExperimentSummary,
            },
        ]
    },
    {
        path: '/manage',
        component:ManagePage,
        children: [
            {
                path: 'queue',
                component:QueueDetail,
            },
            {
                path: 'exp',
                component:ExperimentManage,
            },
            {
                path: 'student',
                component:StudentManage,
            },
            {
                path: 'review',
                component:CodeReview,
            },
            {
                path: "exp/:id/test-point",
                component: TestPointManage
            },
            {
                path: 'judging/:id',
                component:PeekJudging,
            },
        ]
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
    if (vm.$store.state.token)
        token = vm.$store.state.token;
    else if (localStorage.getItem("token")) {
        token = localStorage.getItem("token")
        vm.$store.commit("login", token);
    }
    config.headers.token = token;
    return config;
});
axiosInstance.interceptors.response.use(function (res) {
    if (res.headers.token) {
        vm.$store.commit("login", res.headers.token);
        localStorage.setItem("token", res.headers.token);
    }
    return res
}, function (error) {
    if (error.response.status === 403) {
        vm.$store.commit("logout");
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










