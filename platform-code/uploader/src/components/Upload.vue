<template>
    <v-container class="" fluid>
        <v-card :loading="productSize<0 || isLoading">
            <v-card-title>
                上传文件
            </v-card-title>
            <template slot="progress">
                <v-progress-linear
                    v-if="productSize<0"
                    :active="true"
                    :indeterminate="true"
                ></v-progress-linear>
                <v-progress-linear
                    v-else
                    v-model="progress"
                    :active="true"
                    :indeterminate="false"
                ></v-progress-linear>
            </template>
            <v-card-text class="white">
                <v-list>
                    <v-list-item two-line>
                        <v-list-item-content>
                            <v-list-item-title>实验名</v-list-item-title>
                            <v-list-item-subtitle>
                                {{
                                    config
                                        .expName
                                }}
                            </v-list-item-subtitle>
                        </v-list-item-content>
                    </v-list-item>
                    <v-list-item two-line>
                        <v-list-item-content>
                            <v-list-item-title>上传目标</v-list-item-title>
                            <v-list-item-subtitle>
                                {{
                                    config
                                        .server
                                        .replace("http://", '')
                                }}
                            </v-list-item-subtitle>
                        </v-list-item-content>
                    </v-list-item>
                    <v-list-item two-line>
                        <v-list-item-content>
                            <v-list-item-title>文件总大小</v-list-item-title>
                            <v-list-item-subtitle v-if="productSize<0">
                                打包中
                            </v-list-item-subtitle>
                            <v-list-item-subtitle v-else>
                                {{ convertByteToKB(productSize) + "KB" }}
                            </v-list-item-subtitle>
                        </v-list-item-content>
                    </v-list-item>
                    <v-list-group>
                        <template v-slot:activator>
                            <v-list-item-title>文件忽略规则列表</v-list-item-title>
                        </template>
                        <v-list-item>
                            <v-data-table
                                :loading="productSize<0"
                                loading-text="打包中 请稍等..."
                                :headers="[{text:'规则',value:'pattern'}]"
                                disable-sort
                                :items-per-page="5"
                                :mobile-breakpoint="300"
                                :footer-props="{
                                                    itemsPerPageText:''
                                                }"
                                :items="excludePatterns"
                            >

                            </v-data-table>
                        </v-list-item>
                    </v-list-group>
                    <v-list-group>
                        <template v-slot:activator>
                            <v-list-item-title>将上传的文件列表</v-list-item-title>
                        </template>
                        <v-list-item>
                            <v-data-table
                                :headers=
                                    "[
                                        {
                                            text:'文件名称',
                                            value:'filename',
                                        },
                                        {
                                            text:'大小(KB)',
                                            value: 'size',
                                        }
                                        ]"
                                :mobile-breakpoint="300"
                                :items-per-page="5"
                                sort-by="size"
                                :sort-desc="true"
                                :footer-props="{
                                                    itemsPerPageText:''
                                                }"
                                :items="packedFilesInKbSize"
                            >

                            </v-data-table>
                        </v-list-item>
                    </v-list-group>

                </v-list>

            </v-card-text>
            <v-card-actions class="white">
                <v-btn
                    block
                    class="mx-0"
                    color="primary"
                    @click="upload()"
                    :disabled="isLoading||productSize<0"
                >
                    上传
                    <v-icon
                        dark
                        right
                    >
                        mdi-rocket-launch
                    </v-icon>
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
}


</style>
<script>
const {ipcRenderer} = window.require('electron')
export default {

    name: "Upload",
    data: function () {
        return {
            config: {
                expName: "加载中...",
                server: "加载中...",
            },
            progress: 0,
            studentId: '',
            password: '',
            isLoading: false,
            rules: {
                required: value => !!value || '必填',
            },
            productSize: -1,
            packedFiles: []
        }
    },
    mounted() {
        this.config = this.$store.state.config
        ipcRenderer.once('pre-pack-done', (event, packInfo) => {
            this.productSize = packInfo.productSize;
            this.packedFiles = packInfo.packedFiles;
        })
        ipcRenderer
            .send("pre-pack")
        ipcRenderer.on('pack-done',this.onPackDone )
        //TODO:unreg
        ipcRenderer.on("pack-error",
            (event, args) =>
                this.$store.commit('error', "打包错误，原因：" + args))
        ipcRenderer
            .once("pre-pack-error",
                (event, args) =>
                    this.$store.commit('error', "获取文件信息出错，原因：" + args))
    },
    computed: {
        excludePatterns: function () {
            return this.config?.exclude?.map(it => ({'pattern': it}));
        },
        packedFilesInKbSize: function () {
            return this.packedFiles.map(it => {
                let file = {...it}
                file.size = this.convertByteToKB(file.size)
                return file;
            })
        }
    },
    beforeDestroy() {
        ipcRenderer.removeListener('pack-done',this.onPackDone )
    },
    methods: {
        convertByteToKB(size) {
            return (size / 1024).toFixed(2);
        },
        upload: function () {
            this.productSize = -1;
            ipcRenderer
                .send("pack")
            // await ipcRenderer.send("upload")

        },
        onPackDone(event, packInfo){
            this.productSize = packInfo.productSize;
            this.packedFiles = packInfo.packedFiles;
            const blob = new Blob([packInfo.file])
            const server = this.config.server;
            const expId = this.config.expId;
            let form = new FormData();
            form.append("code", blob, "upload.zip")
            this.isLoading = true;
            this.$http
                .put( "/user-code/" + expId,
                    form,
                    {
                        onUploadProgress:
                            (e) =>
                                this.progress = (e.loaded * 100 / e.total)
                    }
                )
                .then(() => this.$store.commit('toast', "上传成功"))
                .catch((error) => this.$store.commit('error', "上传失败,原因：" + error))
                .finally(() => {
                    this.progress = 0;
                    this.isLoading = false;
                })
        },
        retrievePassword: function () {
            this.$router.push("/retrieve");
        },
        login: function () {
            if (this.$refs.form.validate()) {
                this.isLoading = true;
                this.$http.post('/auth/login/by_password', null, {
                    params: {
                        studentId: this.studentId,
                        password: this.$md5(this.password)
                    }
                })
                    .then((res) => {
                        this.isLoading = false;
                        this.$store.commit('toast', "登录成功")
                        this.$router.push('/upload')
                    })
                    .catch((err) => {
                        this.isLoading = false;
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
