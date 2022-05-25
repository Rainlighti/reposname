<template>
    <v-container fluid>
        <v-data-table
            :headers="headers"
            :items="scores"
            :items-per-page="5"
            item-key="expOrder"
            class="elevation-1"
        ></v-data-table>
        <v-card class="mt-2">
            <v-card-text>
                <v-row>
                    <v-col>
                        总分：{{scoreSum}}
                    </v-col>
                    <v-col>
                        满分：{{fullScoreSum}}
                    </v-col>
                    <v-col>
                        平均分：{{avgScore}}
                    </v-col>
                    <v-col>
                        未得分：{{remainScore}}
                    </v-col>
                </v-row>
            </v-card-text>

        </v-card>
    </v-container>
</template>

<script>
import ExperimentIntroduce from "./ExperimentIntroduction";
import EventLine from "./EventLine";
import JudgingPanel from "./TestProcessPanel";
import toChineseNumeral from "to-chinese-numeral";

export default {
    name: "ExperimentSummary",
    components: {JudgingPanel, EventLine, ExperimentIntroduce},
    data() {
        return {
            scores: [],
            headers: [
                {
                    text: '实验序号',
                    value: 'expOrder',
                },
                {
                    text: '实验标题',
                    value: 'expTitle',
                },
                {
                    text: '获得分数',
                    value: 'score',
                },
                {
                    text: '全部分数',
                    value: 'fullScore',
                },
            ],
        }
    },
    computed: {
        fullScoreSum() {
            return this.scores
                .map(it => it.fullScore)
                .reduce((a, b) => a + b)
                ;
        },
        scoreSum(){
            return this.scores
                .map(it => it.score)
                .reduce((a, b) => a + b)
                ;
        },
        avgScore(){
            return (this.scoreSum/this.scores.length).toFixed(2);
        },
        remainScore(){
            return this.fullScoreSum-this.scoreSum;
        }
    },
    mounted() {
        this.$http.get("judging/summary")
            .then(res =>
                this.scores = res.data
                    .map(it => {
                            it.expOrder = this.expOrderToName(it.expOrder);
                            return it;
                        }
                    )
            )
    },
    methods: {
        goto(link) {
            this.$router.push(link)
        },
        expOrderToName(order) {
            if (order)
                return "实验" + toChineseNumeral(order)
            else return "加载中..."
        },
    },
}
</script>

<style scoped>

</style>
