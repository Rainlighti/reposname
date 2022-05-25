<template>
    <v-card
        elevation="12"
    >
        <v-card-title>
            实验情况
            <v-spacer></v-spacer>
            <v-dialog
                v-model="dialog"
                max-width="500px"
            >
                <template v-slot:activator="{ on, attrs }">
                    <v-btn
                        elevation="4"
                        color="primary"
                        dark
                        class="mb-2"
                        v-bind="attrs"
                        v-on="on"
                    >
                        新建实验
                    </v-btn>
                </template>
                <v-card>
                    <v-card-title>
                        <span class="headline">{{ formTitle }}</span>
                    </v-card-title>

                    <v-card-text>
                        <v-form ref="form">
                            <v-container>
                                <v-row>
                                    <v-col>
                                        <v-text-field
                                            v-model="editedItem.order"
                                            type="number"
                                            :rules="[rules.required]"
                                            label="实验序号"
                                        ></v-text-field>
                                    </v-col>
                                    <v-col>
                                        <v-text-field
                                            v-model="editedItem.title"
                                            :rules="[rules.required]"
                                            label="实验标题"
                                        ></v-text-field>
                                    </v-col>
                                </v-row>
                                <v-row>
                                    <v-col>
                                        <v-text-field
                                            v-model="editedItem.documentLink"
                                            label="实验指导书链接"
                                        ></v-text-field>
                                    </v-col>
                                </v-row>
                                <v-row>
                                    <v-col>
                                        <v-textarea
                                            v-model="editedItem.intro"
                                            label="实验简介"
                                        ></v-textarea>

                                    </v-col>
                                </v-row>
                            </v-container>
                        </v-form>

                    </v-card-text>

                    <v-card-actions>
                        <v-spacer></v-spacer>
                        <v-btn
                            color="blue darken-1"
                            text
                            @click="close"
                        >
                            取消
                        </v-btn>
                        <v-btn
                            color="blue darken-1"
                            text
                            @click="save"
                        >
                            保存
                        </v-btn>
                    </v-card-actions>
                </v-card>
            </v-dialog>
        </v-card-title>
        <v-data-table
            :headers="headers"
            :items="experimentsDisplay"
            :loading="loading"
            :options.sync="options"
            :multi-sort="false"
            :expanded.sync="expanded"
            single-expand
            item-key="id"
            @item-expanded="updateExpandedItemExpInfo"
            show-expand
        >
            <template v-slot:expanded-item="{ headers }">
                <td :colspan="headers.length" class="px-0">
<!--                    :server-items-length="totalStudent"-->
                    <v-data-table
                        @click:row="gotoJudging"
                        :loading="!expandedItemExpDetail"
                        item-key="studentId"
                        :headers="expDetailHeader"
                        :items="expandedItemExpDetailTableItem"
                        :items-per-page="5"
                    >

                    </v-data-table>
                </td>
            </template>
            <template v-slot:item.actions="{ item }">
                <v-icon
                    small
                    class="mr-2"
                    @click="gotoExpTestPointManage(item)"
                >
                    mdi-test-tube
                </v-icon>
                <v-icon
                    small
                    class="mr-2"
                    @click="uploadJudgingCode(item)"
                >
                    mdi-file-code
                </v-icon>
                <v-icon
                    small
                    class="mr-2"
                    @click="editItem(item)"
                >
                    mdi-pencil
                </v-icon>
                <v-icon
                    small
                    @click="deleteItem(item)"
                >
                    mdi-delete
                </v-icon>
            </template>

        </v-data-table>


        <v-dialog v-model="dialogDelete" max-width="250px">
            <v-card>
                <v-card-title class="headline">
                    删除确认
                </v-card-title>
                <v-card-text>
                    你确定要删除 {{ editedItem.title }} 吗

                </v-card-text>
                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn color="blue darken-1" text @click="closeDelete">取消</v-btn>
                    <v-btn color="blue darken-1" text @click="deleteItemConfirm">删除</v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>

    </v-card>

</template>
<style scoped>
    .v-data-table .v-data-table{
        border-radius: 0 !important;
    }

</style>

<script>
import toChineseNumeral from "to-chinese-numeral";

const deepCopy = (obj) => JSON.parse(JSON.stringify(obj));
export default {
    name: "ExperimentManage",
    data() {
        return {
            rules: {
                required: value => !!value || '必填',
            },
            loading: false,
            options: {},
            totalStudent: 0,
            dialog: false,
            expanded: [],
            dialogDelete: false,
            expandedItemExpDetail: null,
            expDetailHeader: [
                {
                    text: '学生姓名',
                    align: 'start',
                    value: 'studentName',
                },
                {
                    text: '班级',
                    value: 'studentClass',
                },
                {text: '学号', value: 'studentId'},
                {text: '成绩', value: 'score'},
                {text: '满分', value: 'fullScore'},
                {
                    text: '上次提交时间',
                    value: 'lastSubmitTime',
                    sortable: false,
                },
            ],
            editedIndex: -1,
            editedItem: {
                order: 0,
                intro: "",
                title: "",
                documentLink: "",
            },
            defaultItem: {
                order: 0,
                intro: "",
                title: "",
                documentLink: ""
            },
            headers: [
                {
                    text: '实验序号',
                    align: 'start',
                    value: 'order',
                },
                {
                    text: '实验标题',
                    value: 'title',
                },
                {text: '实验简介', value: 'intro'},
                {text: '满分', value: 'fullScore'},
                {
                    text: '测试点数量',
                    value: 'testPointAmount',
                },
                {
                    text: '评测程序',
                    value: 'hasJudgingProgram',
                },
                {text: '操作', value: 'actions', sortable: false},
                {text: '', value: 'data-table-expand'},
            ],
            experiments: [

            ],
        }
    },
    computed: {
        formTitle() {
            if (this.editedIndex === -1)
                this.$nextTick(() => this.$refs?.form?.reset())
            return this.editedIndex === -1 ? '新建实验' : '编辑实验信息'
        },
        expandedItemExpDetailTableItem() {
            return this.expandedItemExpDetail ?? []
        },
        experimentsDisplay() {
            const exps = deepCopy(this.experiments)
            exps.forEach((it) => {
                it.hasJudgingProgram = it.hasJudgingProgram ? "有" : "无";
                it.intro = this.omitIfTooLong(it.intro, 13)
                it.order = this.expOrderToName(it.order)
            })
            return exps;
        }
    },
    watch: {
        options: {
            handler() {
                this.fetchExperimentDetail()
            },
            deep: true,
        },
        dialog(val) {
            val || this.close()
        },
        dialogDelete(val) {
            val || this.closeDelete()
        },
    },
    mounted() {
        this.fetchExperimentDetail()
    },
    methods: {
        uploadJudgingCode(exp) {
            let fileSelector = document.createElement('input');
            fileSelector.setAttribute('type', 'file');
            fileSelector.style.display = 'none'
            fileSelector.onchange = ((event) => {
                let formData = new FormData();
                const file = event.target.files[0]
                formData.append("code", file)
                this.$http.put("judging-code/" + exp.id,
                    formData, {
                        headers: {
                            'Content-Type': 'multipart/form-data'
                        }
                    }
                )
                    .then((res) => this.$store.commit('toast', "上传成功"))
                    .catch((error) => this.$store.commit('error', "上传评测代码时出现错误:" + error.response.data))


            })
            fileSelector.click()


        },
        omitIfTooLong(str, max) {
            if (str.length > max)
                return str.substring(0, max - 3) + "..."
            else return str;
        },
        gotoExpTestPointManage(item){
            if (item.id && item.id !== -1)
                this.$router.push("exp/" + item.id+"/test-point")
            else
                this.$store.commit('warning',
                    "找不到实验")

        },
        gotoJudging(expJudging) {
            if (expJudging.judgingId !== -1)
                this.$router.push("/manage/judging/" + expJudging.judgingId)
            else
                this.$store.commit('warning',
                    "无提交记录。\n此实验可能还未访问过这个实验。")


        },
        expOrderToName(order) {
            if (order)
                return "实验" + toChineseNumeral(order)
            else return "加载中..."
        },
        updateExpandedItemExpInfo(event) {
            const {item, value} = event
            this.expandedItemExpDetail = null
            if (!value)
                return
            const expId = item.id
            this.$http.get("exp/" + expId + "/student")
                .then(res => {
                    this.expandedItemExpDetail = res.data.content
                    this.totalStudent = res.data.totalElements
                    this.expandedItemExpDetail
                        .forEach(status => {
                                status.fullScore = item.fullScore;
                                status.lastSubmitTime = this.formatTime(status.lastSubmitTime)
                            }
                        )
                })
            .catch((error)=>this.$store.commit('error',"拉取实验学生情况时出现错误:"+error.response.data))


        },

        formatTime(timestamp) {
            if (timestamp && timestamp !== 0) {
                const d = new Date(timestamp);
                const padLeft2 = (num) => {
                    num = num + "";
                    return `${num.length !== 2 ? "0" : ""}${num}`;
                };
                return `${d.getMonth() + 1}月${d.getDate()}日
                ${padLeft2(d.getHours())}:${padLeft2(d.getMinutes())}`
            } else return "暂无"

        },
        fetchExperimentDetail() {
            this.loading = true;
            const {sortBy, sortDesc, page, itemsPerPage} = this.options
            let params = {
                page: page - 1,
                size: itemsPerPage,
                sort: (sortBy.length !== 0)
                    ? (
                        (sortBy[0])
                        +
                        (
                            sortDesc[0]
                                ? ",DESC"
                                : ",ASC"
                        )
                    )
                    : ("")
            }
            if (this.search && isNaN(+this.search))
                params.order = this.search;
            else
                params.intro = this.search;
            this.$http.get("exp", {})
                .then(res => {
                    this.experiments = res.data;
                    this.experiments.sort((a,b)=>a.order-b.order)

                })
                .finally(() => this.loading = false)
        },
        editItem(item) {
            item = this.experiments.filter(it => it.id === item.id)[0]
            this.editedItem = Object.assign({}, item)
            this.editedIndex = this.experiments.indexOf(item)
            this.dialog = true
            // this.$nextTick(()=>console.(this.$refs.form))
        },

        deleteItem(item) {
            item = this.experiments.filter(it => it.id === item.id)[0]
            this.editedItem = Object.assign({}, item)
            this.editedIndex = this.experiments.indexOf(item)
            this.dialogDelete = true
        },

        deleteItemConfirm() {
            this.$http.delete("exp/" + this.editedItem.id)
                .then(() => this.$store.commit('toast', "删除成功"))
                .catch((error) => this.$store.commit('error', "删除失败," + error.response.data))
                .finally(() => this.fetchExperimentDetail())
            this.closeDelete()
        },

        close() {
            this.dialog = false
            this.$nextTick(() => {
                this.editedItem = Object.assign({}, this.defaultItem)
                this.editedIndex = -1
            })
        },

        closeDelete() {
            this.dialogDelete = false
            this.$nextTick(() => {
                this.editedItem = Object.assign({}, this.defaultItem)
                this.editedIndex = -1
            })
        },

        save() {
            if (this.$refs.form.validate()) {
                if (this.editedIndex > -1) {
                    this.editExperiment()
                } else {
                    this.addExperiment()
                }
                this.close()
            }
        },
        editExperiment() {
            const id = this.editedItem.id
            this.$http.patch("exp/" + id, this.editedItem)
                .then(() => this.$store.commit('toast', "编辑成功"))
                // .catch(console.dir)
                .catch((error) => this.$store.commit('error', "编辑失败," + error.response.data))
                .finally(() => this.fetchExperimentDetail())

        },
        addExperiment() {
            //add
            this.$http.put("exp", this.editedItem)
                .then(() => this.$store.commit('toast', "添加成功"))
                .catch((error) => this.$store.commit('error', "添加失败," + error.response))
                .finally(() => this.fetchExperimentDetail())

        },
    },

}
</script>

<style scoped>

</style>
