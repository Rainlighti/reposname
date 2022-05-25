<template>
    <v-card
        elevation="12"
    >
        <v-card-title>
            评测集群
            <v-icon v-ripple class="ml-1" @click="fetchWorkers">
                mdi-refresh
            </v-icon>
        </v-card-title>
        <v-card-text>
            <v-container fluid>
                <v-row>
                    <v-col
                        class="pt-1"
                        v-for="worker in onlineWorkersTable"
                        :key="worker.name"
                    >
                        <v-card
                            width="250px"
                            :loading="!!(getWorkerRunningJudgingList(worker.name).length)"

                        >
                            <template slot="progress">
                                <v-progress-linear
                                    color="cyan"
                                    indeterminate
                                ></v-progress-linear>
                            </template>

                            <v-card-title>
                                <div style="font-size: 0.8em">
                                    {{ worker.name }}
                                </div>
                            </v-card-title>
                            <v-card-text>
                                <v-slide-y-transition group>
                                    <v-card
                                        style="margin-bottom: 10px"
                                        dark
                                        :color="getJudgingColor(judging)"
                                        v-for="judging in getWorkerRunningJudgingList(worker.name)"
                                        :key="judging.id"
                                        ripple
                                        @click="gotoJudging(judging)"
                                    >
                                        <v-card-title
                                            style="font-size: 1.2em;padding-bottom: 8px"
                                            class="padding-less"

                                        >
                                            {{ judging.user.name }}
                                        </v-card-title>
                                        <v-card-subtitle
                                            class="padding-less"
                                            style="padding-top:7px"
                                        >
                                            {{ getCurrEventStateInfo(judging) }}
                                        </v-card-subtitle>
                                    </v-card>

                                </v-slide-y-transition>
                            </v-card-text>
                        </v-card>
                    </v-col>

                </v-row>

            </v-container>
        </v-card-text>

        <v-card elevation="0">
            <v-card-title>
                等待队列
            </v-card-title>


            <v-card-text>
                <div class="font-weight-bold ml-8 mb-2 mb-3">
                    最上方的任务将最先被执行
                </div>

                <v-timeline
                    align-top
                    :dense="$vuetify.breakpoint.smAndDown"

                >
                    <v-slide-y-transition group>
                        <v-timeline-item
                            v-for="judging in waitingQueue"
                            :key="judging.id"
                            :color="getJudgingColor(judging)"
                            :icon="getJudgingIcon(judging)"
                            fill-dot
                        >
                            <v-card
                                :color="getJudgingColor(judging)"
                                dark
                                ripple
                                @click="gotoJudging(judging)"
                            >
                                <v-card-title class="title">
                                    {{ judging.user.name }}
                                    <!--                                    {{ isWeightPriority ? ("   优先级:" + judging.weight) : "" }}-->
                                </v-card-title>
                                <v-card-subtitle>
                                    {{
                                        getCurrEventStateInfo(judging)
                                    }}<br>{{ getCurrEventLastedTimeInfo(judging) }}

                                </v-card-subtitle>
                            </v-card>
                        </v-timeline-item>
                    </v-slide-y-transition>
                </v-timeline>
            </v-card-text>
        </v-card>


    </v-card>

</template>
<style scoped>
.padding-less {
    padding: 10px;
    padding-left: 16px;

}
</style>

<script>
import moment from "moment"

const stateColorTable = {
    'waiting': 'teal',
    'done': 'teal',
    'running': 'green',
    'error': 'red',
    'notStart': 'black',
    undefined: 'red'
};
const stateIconTable = {
    'waiting': 'mdi-pause',
    'done': 'mdi-check',
    'running': 'mdi-play',
    'error': 'mdi-alert-circle',
    'notStart': 'mdi-stop',
    undefined: 'mdi-alert'
}

function isEqual(a, b) {
    return JSON.stringify(a) === JSON.stringify(b);
}

function isWaiting(judging) {
    return judging.currEvent.state === 'waiting' && judging.currEvent.type === 'CompileEvent'
}


export default {
    name: "QueueDetail",
    data() {
        return {
            allJudgingInQueue: [],
            onlineWorkersTable: {},
            timer: -1
        }
    },
    mounted() {
        this.fetchWorkers();
        this.fetchJudgings();
        // this.timer = setInterval(this.fetchJudgings, 1000)
        this.timer = setInterval(this.fetchJudgings,500)
    },
    beforeDestroy() {
        if (this.timer !== -1)
            clearInterval(this.timer)
    },

    computed: {
        waitingQueue() {
            return this.allJudgingInQueue
                .filter(isWaiting)
                .sort(((a, b) => a.currEvent.time - b.currEvent.time))
                ;
        },
    },
    methods: {
        gotoJudging(judging){
            if(judging.id!==-1)
                this.$router.push("/manage/judging/"+judging.id)
            else
                this.$store.commit('warning',
                    "无提交记录。")


        },
        fetchJudgings() {
            this.$http.get("judging/in-queue")
                .then((res) => {
                    const judgings = res.data;
                    judgings.forEach(
                        (it) => this.addOnlineWorker({
                            name: it.workerName
                        })
                    )
                    return this.allJudgingInQueue = judgings;
                })
                .catch((error) => this.$store.commit('toast', "拉取等待队列时出现错误:" + error.response.data))
        },
        fetchWorkers() {
            this.$http.get("worker/online")
                .then((res) => this.mergeOnlineWorkerTable(res.data))
                .catch((error) => this.$store.commit('error', "拉取评测机列表时出现错误:" + error.response.data))
        },
        getWorkerRunningJudgingList(workerName) {
            return this.allJudgingInQueue
                .filter(it => it.workerName === workerName)
                .filter((it) => !isWaiting(it))
                .sort(((a, b) => a.currEvent.time - b.currEvent.time))

        },
        addOnlineWorker(newWorker) {
            if (!newWorker||!newWorker.name)
                return
            this.onlineWorkersTable[newWorker.name] = newWorker;
        },
        mergeOnlineWorkerTable(newWorkers) {
            newWorkers.forEach(this.addOnlineWorker)
        },
        getJudgingColor(judging) {
            return stateColorTable[judging?.currEvent?.state]
        },
        getJudgingIcon(judging) {
            return stateIconTable[judging?.currEvent?.state]
        },
        getCurrEventStateInfo(judging) {
            const e = judging?.currEvent;
            if (!e)
                return "加载中..."
            const name = e.name;
            if (e.state === "waiting") {
                return `等待${name}中...`
            }
            if (e.state === 'running') {
                const tp = judging?.testProcesses?.find(it => it.state === 'pending')
                if (tp)
                    return "正在评测 " + tp.test.name + " 中..."
                return `正在${name}中...`
            }
            if (e.state === 'done') {
                return `已完成${name}`
            }
            if (e.state === 'notStart') {
                return `尚未开始`
            }
            if (e.state === 'error') {
                return `${name}时出现错误`
            }

        },
        getCurrEventLastedTimeInfo(judging) {
            const e = judging?.currEvent;
            if (!e)
                return "加载中..."
            if (!e.time || e.time <= 0)
                return "无时间信息"

            const lastedTime = moment(e.time).fromNow(true)
            if (e.state === 'waiting')
                return "已经等待了 " + lastedTime
            else if (e.state === 'running')
                return "已经运行了 " + lastedTime
            else
                return "已经持续了 " + lastedTime

        }
    },

}
</script>

<style scoped>

</style>
