<template>
    <v-card
        elevation="12"
        @contextmenu="openMenu"
    >
        <v-card-title>
            学生管理
            <v-spacer></v-spacer>
            <v-text-field
                v-model="search"
                append-icon="mdi-magnify"
                label="搜索姓名或学号"
                single-line
                hide-details
                style="transform: translateY(-10px);max-width: 200px"
                class="mr-4"
            ></v-text-field>
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
                        新建学生
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
                                            v-model="editedItem.name"
                                            :rules="[rules.required]"
                                            label="学生姓名"
                                        ></v-text-field>
                                    </v-col>
                                    <v-col>
                                        <v-text-field
                                            v-model="editedItem.clazz"
                                            :rules="[rules.required]"
                                            label="学生班级"
                                        ></v-text-field>
                                    </v-col>
                                </v-row>
                                <v-row>
                                    <v-col>
                                        <v-text-field
                                            v-model="editedItem.studentId"
                                            :rules="[rules.required]"
                                            label="学生学号"
                                        ></v-text-field>
                                    </v-col>
                                    <v-col>
                                        <!--                                    new student-->
                                        <v-text-field
                                            v-if="this.editedIndex===-1"
                                            v-model="editedItem.password"
                                            :rules="[rules.required]"
                                            label="学生密码"
                                        ></v-text-field>
                                        <v-text-field
                                            v-else
                                            v-model="editedItem.password"
                                            placeholder="留空则不进行修改"
                                            label="学生密码(留空则不变)"
                                        ></v-text-field>
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
            :server-items-length="totalStudent"
            :headers="headers"
            :items="students"
            :search="search"
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
                    <v-data-table
                        @click:row="gotoJudging"
                        :loading="!expandedItemExpSummary"
                        item-key="expOrder"
                        :headers="expSummaryHeader"
                        :items="expandedItemExpSummaryTableItem"
                    >

                    </v-data-table>
                </td>
            </template>
            <template v-slot:item.actions="{ item }">
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
        <v-menu
            v-model="showMenu"
            :position-x="x"
            :position-y="y"
            absolute
            offset-y
        >
            <v-list>
                <v-list-item
                    @click="exportTable"
                >
                    <v-list-item-title>
                        导出表格
                    </v-list-item-title>
                </v-list-item>
                <v-list-item
                    @click="importTable"
                >
                    <v-list-item-title>
                        导入表格
                    </v-list-item-title>
                </v-list-item>
            </v-list>
        </v-menu>

        <v-dialog v-model="dialogDelete" max-width="250px">
            <v-card>
                <v-card-title class="headline">
                    删除确认
                </v-card-title>
                <v-card-text>
                    你确定要删除 {{ editedItem.name }} 吗

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
import {saveAs} from "file-saver"

export default {
    name: "StudentManage",
    data() {
        return {
            rules: {
                required: value => !!value || '必填',
            },
            loading: false,
            search: '',
            options: {},
            totalStudent: 0,
            dialog: false,
            expanded: [],
            dialogDelete: false,
            showMenu: false,
            x: 0,
            y: 0,
            expandedItemExpSummary: null,
            expSummaryHeader: [
                {
                    text: "实验序号",
                    value: "expOrder"
                },
                {
                    text: "实验名称",
                    value: "expTitle"
                },
                {
                    text: "已获分数",
                    value: "score"
                },
                {
                    text: "满分",
                    value: "fullScore"
                },
            ],
            editedIndex: -1,
            editedItem: {
                name: '',
                studentId: "",
                clazz: "",
                password: "",
            },
            defaultItem: {
                name: '',
                studentId: "",
                clazz: "",
                password: "",
            },
            headers: [
                {
                    text: '学生姓名',
                    align: 'start',
                    value: 'name',
                },
                {
                    text: '班级',
                    value: 'clazz',
                },
                {text: '学号', value: 'studentId'},
                {text: '总成绩', value: 'scoreSum'},
                {
                    text: '试做实验个数',
                    value: 'triedExpCount',
                    sortable: false,
                },
                {
                    text: '上次提交时间',
                    value: 'lastSubmitTime',
                    sortable: false,
                },
                {text: '操作', value: 'actions', sortable: false},
                {text: '', value: 'data-table-expand'},
            ],
            students: [
            ],
        }
    },
    computed: {
        formTitle() {
            if(this.editedIndex===-1)
                this.$nextTick(()=>this.$refs?.form?.reset())
            return this.editedIndex === -1 ? '新建学生' : '编辑学生信息'
        },
        expandedItemExpSummaryTableItem() {
            return this.expandedItemExpSummary ?? []
        }
    },
    watch: {
        options: {
            handler() {
                this.fetchStudents()
            },
            deep: true,
        },
        search: {
            handler() {
                this.fetchStudents()
            },
        },
        dialog(val) {
            val || this.close()
        },
        dialogDelete(val) {
            val || this.closeDelete()
        },
    },
    mounted() {
        this.fetchStudents()
    },
    methods: {
        importTable(){
            let fileSelector = document.createElement('input');
            fileSelector.setAttribute('type', 'file');
            fileSelector.style.display = 'none'
            fileSelector.onchange = ((event) => {
                let formData = new FormData();
                const file = event.target.files[0]
                formData.append("excel", file)
                this.$http.put("student/import/" ,
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
        exportTable(){
            this.$http.get("student/export/info",
                {
                    responseType: 'blob'
                }
            )
            .then((res)=>saveAs(res.data,"studentInfo.csv"))
            .catch((error)=>
                this.$store.commit("error",
                    "导出失败\n"+error.response))

        },
        openMenu(e){
            console.log(1);
            e.preventDefault()
            this.showMenu = false
            this.x = e.clientX
            this.y = e.clientY
            this.$nextTick(() => {
                this.showMenu = true
            })

        },
        gotoJudging(expJudging){
            if(expJudging.judgingId!==-1)
                this.$router.push("/manage/judging/"+expJudging.judgingId)
            else
                this.$store.commit('warning',
                    "无提交记录。\n此学生可能还未访问过这个实验。")



        },
        expOrderToName(order) {
            if (order)
                return "实验" + toChineseNumeral(order)
            else return "加载中..."
        },
        updateExpandedItemExpInfo(event) {
            const {item, value} = event
            this.expandedItemExpSummary = null
            if (!value)
                return
            const studentId = item.id
            this.$http.get("judging/student/" + studentId + "/summary")
                .then(res => {
                    this.expandedItemExpSummary = res.data
                    this.expandedItemExpSummary
                        .forEach(item =>
                            item.expOrder = this.expOrderToName(item.expOrder))
                    this.expandedItemExpSummary.sort(
                        (a, b) => a.expOrder - b.expOrder
                    )

                })
            .catch((error)=>this.$store.commit('error',"时出现错误:"+error.response.data))


        },

        formatTime(timestamp) {
            if (timestamp && timestamp !== 0) {
                const d = new Date(timestamp);
                const padLeft2 = (num) => {
                    num = num + "";
                    return `${num.length !== 2 ? "0" : ""}${num}`;
                };
                return `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日
                ${padLeft2(d.getHours())}:${padLeft2(d.getMinutes())}`
            } else return "暂无"

        },
        fetchStudents() {
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
                params.name = this.search;
            else
                params.studentId = this.search;
            this.$http.get("student", {
                params: params
            })
                .then(res => {
                    this.students = res.data.content;
                    this.students
                        .forEach(it =>
                            it.lastSubmitTime = this.formatTime(it.lastSubmitTime))
                    this.totalStudent = res.data.totalElements;
                })
                .finally(() => this.loading = false)
        },
        editItem(item) {

            this.editedIndex = this.students.indexOf(item)
            this.editedItem = Object.assign({}, item)
            this.dialog = true
            // this.$nextTick(()=>console.(this.$refs.form))
        },

        deleteItem(item) {
            this.editedIndex = this.students.indexOf(item)
            this.editedItem = Object.assign({}, item)
            this.dialogDelete = true
        },

        deleteItemConfirm() {
            this.$http.delete("student/" + this.editedItem.id)
                .then(() => this.$store.commit('toast', "删除成功"))
                .catch((error) => this.$store.commit('error', "删除失败," + error.response.data))
                .finally(() => this.fetchStudents())
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
                    this.editStudent()
                } else {
                    this.addStudent()
                }
                this.close()
            }
        },
        editStudent() {
            const id = this.editedItem.id
            if (this.editedItem.password)
                this.editedItem.password = this.$md5(this.editedItem.password)
            this.$http.patch("student/" + id, this.editedItem)
                .then(() => this.$store.commit('toast', "编辑成功"))
                // .catch(console.dir)
                .catch((error) => this.$store.commit('error', "编辑失败," + error.response.data))
                .finally(() => this.fetchStudents())

        },
        addStudent() {
            //add
            this.editedItem.password = this.$md5(this.editedItem.password)
            this.$http.put("student", this.editedItem)
                .then(() => this.$store.commit('toast', "添加成功"))
                .catch((error) => this.$store.commit('error', "添加失败," + error.response.data))
                .finally(() => this.fetchStudents())

        },
    },

}
</script>

<style scoped>

</style>
