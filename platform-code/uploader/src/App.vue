<template>
    <v-app >
        <v-app-bar
            app
            color="teal"
            dark
        >
            <v-toolbar-title class="">
                OS实验代码上传工具
            </v-toolbar-title>

            <v-spacer></v-spacer>
            <v-btn text @click="logout()" v-if="isLogin&&user.name">
                <span class="">{{user.name}}</span>
            </v-btn>
            <v-btn text @click="logout()" v-else-if="isLogin">
                <span class="">退出登录</span>
            </v-btn>

        </v-app-bar>

        <v-main>
            <router-view></router-view>
        </v-main>
            <ErrorDialog></ErrorDialog>
            <MessageToast></MessageToast>
    </v-app>
</template>
<style>
.center {
    display: flex;
    justify-content: center;
    justify-items: center;
    align-content: center;
    align-items: center;
}
</style>

<script>
import ErrorDialog from "@/ErrorDialog";
import MessageToast from "@/MessageToast";

const {ipcRenderer} = window.require('electron')

export default {
    name: 'App',

    components: {
        ErrorDialog,MessageToast

    },
    created() {
        this.$store.commit('updateConfig')
    },
    methods: {
        logout(){
            this.$store.commit('logout');
        },
        updateUserInfo() {
            this.$http.get('/me/user')
            .then((res)=>this.user=res.data)
        }
    },
    computed:{
      isLogin(){
          let token = this.$store.state.token
          if(token)
              this.updateUserInfo();
          return token;
      },
    },


    data: () => ({
        user:{}
    }),
};
</script>
