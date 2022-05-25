<template>
    <v-app id="inspire">
        <v-app-bar
                app
                color="primary"
                dark
        >
            <v-toolbar-title class=" mr-5" style="margin-left: 11vh">
                NCUT操作系统实验平台</v-toolbar-title>
            <template v-if="isLogin">
                <v-btn text @click="goto('/exp')">
                    <span class="mr-2">实验详情</span>
                </v-btn>
                <v-btn text @click="isUserInfoDialogOpen = true">
                    <span class="mr-2">用户信息</span>
                </v-btn>
                <!--            增加用户类型判断-->
                <v-btn text @click="goto('/manage')"
                       v-if="userType==='admin'"
                >
                    <span class="mr-2">后台管理</span>
                </v-btn>
                <!--            <v-btn text @click="goto('/queue')">-->
                <!--                <span class="mr-2">等待队列</span>-->
                <!--            </v-btn>-->
                <v-spacer></v-spacer>

                <v-responsive max-width="170">
                    <!--                学生姓名-->
                </v-responsive>
            </template>
        </v-app-bar>




        <v-main class="grey lighten-3">
            <router-view></router-view>
        </v-main>
        <UserInfo v-model="isUserInfoDialogOpen"
                  v-if="isUserInfoDialogOpen"
                  :user="user"
        />
        <ErrorDialog></ErrorDialog>
        <WarningDialog></WarningDialog>
        <MessageToast></MessageToast>
    </v-app>
</template>


<script>


    import UserInfo from "./UserInfo";
    import ErrorDialog from "./ErrorDialog";
    import MessageToast from "./MessageToast";
    import WarningDialog from "@/WarningDialog";

    export default {
        name: 'App',

        components: {WarningDialog, MessageToast, ErrorDialog, UserInfo},
        data: () => ({
            isUserInfoDialogOpen:false,
            user:null,

        }),
        methods: {
            goto(link) {
                this.$router.push(link)
            }
        },
        watch:{
            isLogin:{
                handler(val){
                    if(val)
                    {
                        this.$http.get("/me/user")
                            .then((res)=>this.user = res.data)
                    }
                }
            }
        },
        computed: {
            userType(){
              return this.user?.type;
            },
            isLogin(){
                return !!this.$store.state.token;
            }
        },
    }
</script>
