<template>
    <Judging :judging="judging"></Judging>
</template>

<script>
import {update} from "@/util";
import Judging from "@/components/experiment/Judging";

export default {
    name: "ExperimentDetail",
    components: {Judging},
    data() {
        return {

            timer: -1,
            judging: {
                lastSubmitTime: 0,
                experiment: {
                    title: "加载中...",
                    name: "",
                    intro: "加载中...",
                    documentLink: "",
                },
                testProcesses: [],
                currEvent: null,
                events: [
                    {
                        id: 0,
                        time: 0,
                        state: 'notStart',
                    },
                    {
                        id: 1,
                        time: 0,
                        state: 'notStart',
                    },
                    {
                        id: 2,
                        time: 0,
                        state: 'notStart',
                    },
                ],

            },

        }
    },
    methods: {
        goto(link) {
            this.$router.push(link)
        },
        clear(){
            if (this.timer !== -1)
                clearInterval(this.timer)
        }
    },
    mounted: function () {
        const expId = this.$route.params.id
        this.$http.get("judging/last", {
            params: {
                expId: expId
            }
        }).then(res => {
            this.judging = res.data
            this.timer = setInterval(() => {
                this.$http.get("judging/last/dynamic-part",
                    {
                        params: {
                            expId: expId
                        }
                    }
                )
                    .then((res)=>this.judging = update(res.data,this.judging))
                    .catch(this.clear)
            }, 1000)
        });
    },
    beforeDestroy() {
        console.log("d");
        this.clear();
    }
}
</script>

<style scoped>

</style>
