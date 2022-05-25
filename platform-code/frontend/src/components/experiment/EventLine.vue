<template>
    <v-card
        class="mt-7 mb-auto"
        style="height: 224px"

    >

        <v-card-text
            @click="test()"
        >
            <div class="font-weight-bold ml-4 mb-2">
                当前进度：
                {{getCurrEventStateInfo()}}
            </div>

            <v-timeline
                    dense
            >
                <v-timeline-item
                        v-for="event in events"
                        :key="event.id"
                        :color="getEventColor(event.state)"
                        small
                >
                    <div
                    >
                        <div class="font-weight-normal">
                            <strong
                            > {{event.name}}</strong>
                            <template >
                                @{{ formatTime(event.time) }}
                            </template>
                        </div>
                    </div>
                </v-timeline-item>
            </v-timeline>
        </v-card-text>
    </v-card>
</template>
<script>
    export default {
        name: 'EventLine',
        props: {
            events: Array,
            currEvent:Object,
        },
        methods: {
            test(){
                console.log(1);
            },

            getEventColor(state) {
                const stateColorTable = {
                    'notStart': 'grey',
                    'waiting': 'blue',
                    'running': 'teal',
                    'done': 'green',
                    'error': 'red',
                };
                return stateColorTable[state];
            },
            getCurrEventStateInfo(){
                const e = this.currEvent;
                if(!e)
                    return "加载中..."
                const name = e.name;
                if(e.state==="waiting")
                {
                    return `等待${name}中...`
                }
                if(e.state==='running')
                {
                    return `正在${name}中...`
                }
                if(e.state==='done')
                {
                    return `已完成${name}`
                }
                if(e.state==='notStart')
                {
                    return `尚未开始`
                }
                if(e.state==='error')
                {
                    return `${name}时出现错误`
                }

            },
            formatTime(timestamp) {
                if(timestamp && timestamp!==0)
                {
                    const d = new Date(timestamp);
                    const padLeft2 = (num)=> {
                        num = num+"";
                        return `${num.length !== 2 ? "0" : ""}${num}`;
                    };
                    return `${d.getMonth()+1}月${d.getDate()}日
                    ${padLeft2(d.getHours())}:${padLeft2(d.getMinutes())}:${padLeft2(d.getSeconds())}`
                }
                else return "尚未开始"

            },
        },
        computed: {
        },
    }
</script>
