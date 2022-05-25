<template>
    <v-card
        max-width="300"

    >
        <v-list-item two-line>
            <v-list-item-content>
                <v-list-item-title class="headline">
                    {{ judging.experiment.title }}
                </v-list-item-title>
                <v-list-item-subtitle>
                    {{ expOrderToName(judging.experiment.order)}}
                </v-list-item-subtitle>
            </v-list-item-content>
        </v-list-item>

        <v-card-text>
            <v-row align="center">
                <v-col
                    class="display-3"
                    cols="12"
                >
                    {{ score }}/{{ fullScore }}分
                </v-col>
            </v-row>
        </v-card-text>

        <v-list-item>
            <v-list-item-title>
                上次提交
            </v-list-item-title>
            <v-list-item-subtitle class="text-right">{{ lastSubmitTime }}</v-list-item-subtitle>
        </v-list-item>
        <v-progress-linear
            style="max-width: calc(100% - 32px)"
            class="mx-4"
            indeterminate
            v-if="showProgressBar"
        >


        </v-progress-linear>

        <v-list class="transparent">
            <v-list-item
                v-for="item in judging.testProcesses"
                :key="item.name"
                @click="showRejectReasonDialog(item)"
            >
                <v-list-item-title>{{ item.test.name }}</v-list-item-title>

                <v-list-item-icon class="ml-5">
                    <v-icon>{{ getTestIcon(item.state) }}</v-icon>
                </v-list-item-icon>

                <v-list-item-subtitle class="text-right">
                    {{ item.test.score }}分
                </v-list-item-subtitle>
            </v-list-item>
        </v-list>

        <v-divider></v-divider>

        <v-card-actions>
            <v-btn text @click="isErrorMessageDialogOpen=true">
                <template v-if="judging.errorMessage">
                    错误详情
                </template>
                <template v-else>
                    提交详情
                </template>
            </v-btn>
            <v-btn
                v-if="showLastSubmitCodeDownload"
                @click="downloadLastSubmitCode"
                text
            >
                查看代码
            </v-btn>
        </v-card-actions>
        <v-dialog
            v-model="isErrorMessageDialogOpen"
            persistent
            max-width="450px"
        >
            <v-card>
                <v-card-title class="headline primary lighten-2" primary-title>
                    <template v-if="judging.errorMessage">
                        错误详情
                    </template>
                    <template v-else>
                        提交详情
                    </template>
                </v-card-title>

                <v-card-text>
                    <template
                        v-if="judging.workerName||
                        judging.compileWarning ||
                         judging.errorMessage">
                        <div style="margin-top:30px" v-if="judging.workerName">
                            评测机名称：<br>
                            {{ judging.workerName }}
                            <br>
                        </div>
                        <div style="margin-top:30px" v-if="judging.compileWarning">
                            编译警告：<br>
                            <div
                                class="block"
                                v-for="(line,index) in splitByLineBreak(judging.compileWarning)"
                                :key="index"
                            >
                                {{line}}
                            </div>
                        </div>
                        <div style="margin-top:30px" v-if="judging.errorMessage">
                            评测时错误：<br>
                            <div
                                class="block"
                                v-for="(line,index) in splitByLineBreak(judging.errorMessage)"
                                :key="index"
                            >
                                {{line}}
                            </div>
                            <br>
                        </div>
                    </template>
                    <template v-else>
                        <div style="margin-top:30px">
                            无信息
                        </div>

                    </template>
                </v-card-text>

                <v-divider></v-divider>

                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn color="primary" text @click="isErrorMessageDialogOpen=false">
                        OK
                    </v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
        <v-dialog
            v-model="isRejectReasonDialogOpen"
            persistent
            max-width="320px"
        >
            <v-card>
                <v-card-title class="headline primary lighten-2" primary-title>
                    评测点未通过原因
                </v-card-title>

                <v-card-text>
                    <div style="margin-top:30px" v-if="selectedTestProcess">
                        <template v-if="selectedTestProcess.rejectReason">
                            {{ selectedTestProcess.rejectReason }}
                        </template>
                        <template v-else>
                            无
                        </template>

                    </div>
                </v-card-text>

                <v-divider></v-divider>

                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn color="primary" text @click="closeRejectReasonDialog">
                        OK
                    </v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
    </v-card>
</template>
<style scoped>
.block {
    display: block;
}
</style>
<script>
import toChineseNumeral from "to-chinese-numeral";
import {saveAs} from "file-saver";

export default {
    name: 'TestProcessPanel',
    props: {
        judging: Object,
        showLastSubmitCodeDownload:{
            type:Boolean,
            default:false
        }
    },
    methods: {
        downloadLastSubmitCode(){
            this.$http.get(
                "user-code/peek/"+this.judging.id,
                {
                    responseType: 'blob'
                }
            )
            .then((res)=>saveAs(res.data,"code.zip"))

        },
        expOrderToName(order){
            if(order)
                return "实验"+toChineseNumeral(order)
            else return "加载中..."
        },
        getTestIcon(state) {
            const stateIconTable = {
                'pending': 'mdi-chevron-triple-right',
                'pass': "mdi-check",
                'reject': "mdi-close",
                'notStart': "mdi-pause"
            };
            return stateIconTable[state]
        },
        showRejectReasonDialog(testProcess) {
            if (testProcess.state === 'reject') {
                this.isRejectReasonDialogOpen = true;
                this.selectedTestProcess = testProcess;
            }
        },
        splitByLineBreak(src) {
            return src.split(/\n/)
        },
        closeRejectReasonDialog() {
            this.isRejectReasonDialogOpen = false;
            this.selectedTestProcess = null;
        }
    },
    data() {
        return {
            isErrorMessageDialogOpen: false,
            isRejectReasonDialogOpen: false,
            selectedTestProcess: null,
        }
    },
    computed: {
        lastSubmitTime() {
            if (this.judging?.lastSubmitTime !== 0 && this.judging?.lastSubmitTime) {
                const d = new Date(this.judging.lastSubmitTime);
                const padLeft2 = (num) => {
                    num = num + "";
                    return `${num.length !== 2 ? "0" : ""}${num}`;
                };
                return `${d.getMonth() + 1}月${d.getDate()}日
                ${padLeft2(d.getHours())}:${padLeft2(d.getMinutes())}`
            } else return "无提交"
        },
        showProgressBar() {
            return this.judging.currEvent?.state === 'waiting'
                || this.judging.currEvent?.state === 'running'
        },
        fullScore() {
            let sum = 0;
            this.judging.testProcesses.forEach(it => sum += it.test.score);
            return sum;
        },
        score() {
            let sum = 0;
            this.judging.testProcesses
                .filter(it => it.state === 'pass')
                .forEach(it => sum += it.test.score);
            return sum;

        }
    },
}
</script>










