<template>
    <v-dialog
            v-model="isOpen"
            persistent
            max-width="320px"
    >
        <v-card>
            <v-card-title>
                    <span class="headline">
                        用户信息
                    </span>
            </v-card-title>
            <v-card-text>
                学生姓名：{{user.name}}
                <br>
                学生班级：{{user.clazz}}
                <br>
                学生学号：{{user.studentId}}
                <br>



            </v-card-text>
            <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn
                    color="red darken-1"
                    text
                    @click="logout()"
                >
                    退出登录
                </v-btn>
                <v-btn
                        color="blue darken-1"
                        text
                        @click="$emit('change', false)"
                >
                    关闭
                </v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>
<script>
    export default {
        name: 'UserInfo',
        model: {
            prop: 'isOpen',
            event: 'change'
        },
        props: {
            isOpen: {
                type:Boolean,
                default: false,
            },
            user:Object,
        },
        data() {
            return {

            }
        },
        methods: {
            change() {
            },
            logout(){
                this.$store.commit('logout');
                this.$emit('change', false)
                this.$router.push("/login");

            }
        },
    }
</script>











