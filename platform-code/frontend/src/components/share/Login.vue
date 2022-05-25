<template>
    <v-container class="background center" fluid>
        <v-card min-width="360px">
            <v-card-title>
                    <span class="headline">
                        登录
                    </span>
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
    .background {
        background: url("../../assets/login-background.jpg") left 60%/cover no-repeat !important;
        width: 100%;
        height: 100%;
    }

    .center {
        display: flex;
        justify-content: center;
        justify-items: center;
        align-content: center;
        align-items: center;
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
        created: function () {
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
                            this.$store.commit('toast', "登录成功")
                            this.$router.push('/exp')
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
