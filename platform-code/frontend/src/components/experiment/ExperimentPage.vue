<template>
    <v-container>
        <v-row>
                <v-col cols="2" xl=2 lg=2 md=3 sm="3" xs="3">
                <v-sheet rounded="lg">
                    <v-list color="transparent">
                        <v-list-item
                                v-for="exp in experiments"
                                :key="exp.id"
                                link
                                @click="gotoExp(exp.id)"
                        >
                            <v-list-item-content>
                                <v-list-item-title>
                                    {{expOrderToName(exp.order)}}
                                </v-list-item-title>
                            </v-list-item-content>
                        </v-list-item>

                        <v-divider class="my-2"></v-divider>

                        <v-list-item
                                link
                                color="grey lighten-4"
                                @click="goto('/exp/summary')"
                        >
                            <v-list-item-content>
                                <v-list-item-title>
                                    成绩汇总
                                </v-list-item-title>
                            </v-list-item-content>
                        </v-list-item>
                    </v-list>
                </v-sheet>
            </v-col>

                <v-col xl=9 lg="9">
                <v-sheet
                        min-height="70vh"
                        rounded="lg"
                >
                    <router-view :key="$route.fullPath"></router-view>

                </v-sheet>
            </v-col>
        </v-row>
    </v-container>
</template>
<script>
    import toChineseNumeral from "to-chinese-numeral"

    export default {
        name: 'ExperimentPage',
        components: {},
        props: {},
        computed: {},
        data() {
            return {
                experiments: [
                    {
                        id: 1,
                        name: "实验一",
                    },
                    {
                        id: 2,
                        name: "实验二",
                    },
                    {
                        id: 3,
                        name: "实验三",
                    },
                    {
                        id: 4,
                        name: "实验四",
                    },
                    {
                        id: 5,
                        name: "实验五",
                    },
                ],
            }
        },
        methods: {
            expOrderToName(order){
                if(order)
                    return "实验"+toChineseNumeral(order)
                else return "加载中..."
            },
            goto(link) {
                this.$router.push(link)
            },
            gotoExp(expId)
            {
                this.goto(`/exp/detail/${expId}`)
            }
        },
        mounted: async function () {
            this.experiments = (await this.$http.get("exp/all/brief", {})).data;
            if (this.experiments.length > 0 && !this.$route.params.id)
                this.gotoExp(this.experiments[0].id)

        }
    }
</script>
