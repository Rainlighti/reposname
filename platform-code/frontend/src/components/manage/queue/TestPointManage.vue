<template>
    <v-card
        elevation="12"
    >
        <v-card-title>
            测试点列表
            <template v-if="expName">
                （{{ expName }}）
            </template>
            <v-spacer></v-spacer>
            <v-dialog
                v-model="dialog"
                max-width="400px"
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
                        新建测试点
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
                                            label="测试点名称"
                                        ></v-text-field>
                                    </v-col>
                                </v-row>
                                <v-row>
                                    <v-col>
                                        <v-text-field
                                            v-model="editedItem.score"
                                            type="number"
                                            :rules="[rules.required]"
                                            label="测试点分数"
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
            :headers="headers"
            :items="testPoints"
            :loading="loading"
            :multi-sort="false"
            item-key="id"
        >
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

const deepCopy = (obj) => JSON.parse(JSON.stringify(obj));
export default {
    name: "TestPointManage",
    data() {
        return {
            rules: {
                required: value => !!value || '必填',
            },
            expName:null,
            expId:-1,
            loading: false,
            dialog: false,
            dialogDelete: false,
            editedIndex: -1,
            editedItem: {
                name:"",
                score:0,
            },
            defaultItem: {
                name:"",
                score:0,
            },
            headers: [
                {
                    text: '测试点ID',
                    align: 'start',
                    value: 'id',
                },
                {
                    text: '测试点名称',
                    value: 'name',
                },
                {text: '分数', value: 'score'},
                {text: '操作', value: 'actions', sortable: false},
            ],
            testPoints: [

            ],
        }
    },
    computed: {
        formTitle() {
            if (this.editedIndex === -1)
                this.$nextTick(() => this.$refs?.form?.reset())
            return this.editedIndex === -1 ? '新建测试点' : '编辑测试点信息'
        },
    },
    watch: {
        dialog(val) {
            val || this.close()
        },
        dialogDelete(val) {
            val || this.closeDelete()
        },
    },
    mounted() {
        this.expId = this.$route.params.id;
        this.fetchTestPointsDetail()
        this.$http.get("exp/"+this.expId+"/title")
        .then((res)=>this.expName = res.data)
    },
    methods: {
        expOrderToName(order) {
            if (order)
                return "测试点" + toChineseNumeral(order)
            else return null
        },
        fetchTestPointsDetail() {
            this.loading = true;
            this.$http.get("exp/"+this.expId+"/test-point", {})
                .then(res => {
                    this.testPoints = res.data;
                })
                .finally(() => this.loading = false)
        },
        editItem(item) {
            item = this.testPoints.filter(it => it.id === item.id)[0]
            this.editedItem = Object.assign({}, item)
            this.editedIndex = this.testPoints.indexOf(item)
            this.dialog = true
            // this.$nextTick(()=>console.(this.$refs.form))
        },

        deleteItem(item) {
            item = this.testPoints.filter(it => it.id === item.id)[0]
            this.editedItem = Object.assign({}, item)
            this.editedIndex = this.testPoints.indexOf(item)
            this.dialogDelete = true
        },

        deleteItemConfirm() {
            this.$http.delete("test-point/" + this.editedItem.id)
                .then(() => this.$store.commit('toast', "删除成功"))
                .catch((error) => this.$store.commit('error', "删除失败," + error.response.data))
                .finally(() => this.fetchTestPointsDetail())
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
                    this.editTestPoints()
                } else {
                    this.addTestPoints()
                }
                this.close()
            }
        },
        editTestPoints() {
            const id = this.editedItem.id
            this.$http.patch("test-point/" + id, this.editedItem)
                .then(() => this.$store.commit('toast', "编辑成功"))
                // .catch(console.dir)
                .catch((error) => this.$store.commit('error', "编辑失败," + error.response.data))
                .finally(() => this.fetchTestPointsDetail())

        },
        addTestPoints() {
            //add
            this.$http.put("test-point?expId="+this.expId, this.editedItem)
                .then(() => this.$store.commit('toast', "添加成功"))
                .catch((error) => this.$store.commit('error', "添加失败," + error.response))
                .finally(() => this.fetchTestPointsDetail())

        },
    },

}
</script>

<style scoped>

</style>
