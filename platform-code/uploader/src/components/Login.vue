<template>
    <v-container class="center " fluid>
        <v-card class="full-width">
            <v-card-title>
                        登录
            </v-card-title>
            <v-card-text>
                <v-form ref="form">
                    <v-container>
                        <v-row>
                            <v-text-field
                                    label="学号"
                                    v-model="studentId"
                                    required
                                    :rules="[rules.required]"
                            ></v-text-field>
                        </v-row>
                        <v-row>
                            <v-text-field
                                    label="密码"
                                    v-model="password"
                                    type="password"
                                    :rules="[rules.required]"
                                    required
                            ></v-text-field>
                        </v-row>
                    </v-container>

                </v-form>

            </v-card-text>
            <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn
                        color="blue darken-1"
                        text
                        @click="login()"
                        :loading="inLoading"
                >
                    登录
                </v-btn>
            </v-card-actions>
        </v-card>


    </v-container>
</template>
<style scoped>

    .center {
        display: flex;
        justify-content: center;
        justify-items: center;
        align-content: center;
        align-items: center;
        height: 100%;
        width: 100%;
    }
    .full-width{
        width: 100%;
    }


</style>
<script>
    export default {

        name: "Login",
        data: function () {
            return {
                studentId: '',
                password: '',
                inLoading: false,
                rules: {
                    required: value => !!value || '必填',
                }

            }
        },
        mounted: function () {
            this.$nextTick(()=>{
                if(this.$store.state.token)
                    this.$router.push("/upload")
            })
        },
        methods: {
            retrievePassword: function () {
                this.$router.push("/retrieve");
            },
            login: function () {
                if (this.$refs.form.validate()) {
                    this.inLoading = true;
                    this.$http.post('/auth/login/by_password', null, {
                        params: {
                            studentId: this.studentId,
                            password: this.$md5(this.password)
                        }
                    })
                        .then((res) => {
                            this.inLoading = false;
                            this.$router.push('/upload')
                        })
                        .catch((err) => {
                            this.inLoading = false;
                            if (err.response) {
                                switch (err.response.status) {
                                    case 401:
                                        this.$store.commit('error', "该用户不存在或密码错误");
                                        break;
                                    default:
                                        this.$store.commit('error', "网络出错,请稍后再试");
                                }
                            } else
                                this.$store.commit('error', "网络出错,请稍后再试");

                        })
                }

            }
        }

    }
</script>

<style scoped>

</style>
