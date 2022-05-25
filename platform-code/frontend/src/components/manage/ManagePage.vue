<template>
    <v-container>
        <v-row class="mt-2">
            <v-col cols="2" xl=2 lg=2 md=3 sm="3" xs="3">
                <v-card
                    elevation="12"
                    width="256"
                >
                    <v-navigation-drawer
                        floating
                        permanent
                    >
                        <v-list
                            dense
                            rounded
                        >
                            <v-list-item
                                v-for="item in items"
                                :key="item.title"
                                @click="goto(item.link)"
                                link
                            >
                                <v-list-item-icon>
                                    <v-icon>{{ item.icon }}</v-icon>
                                </v-list-item-icon>

                                <v-list-item-content>
                                    <v-list-item-title>{{ item.title }}</v-list-item-title>
                                </v-list-item-content>
                            </v-list-item>
                        </v-list>
                    </v-navigation-drawer>
                </v-card>
            </v-col>
            <v-col xl=9 lg="9">
                <router-view>

                </router-view>
            </v-col>
        </v-row>
    </v-container>

</template>
<script>
import toChineseNumeral from "to-chinese-numeral"

export default {
    name: 'ManagePage',
    components: {},
    props: {},
    computed: {},
    data() {
        return {
            items: [
                {
                    title: '评测队列',
                    icon: 'mdi-view-dashboard',
                    link:"/manage/queue"
                },
                {
                    title: '实验情况',
                    icon: 'mdi-beaker-outline',
                    link:"/manage/exp"
                },
                {
                    title: '学生信息',
                    icon: 'mdi-school-outline',
                    link:"/manage/student"
                },
                {
                    title: '代码查重',
                    icon: 'mdi-test-tube',
                    link: "/manage/review"
                },
            ],
        }
    },
    methods: {
        expOrderToName(order) {
            if (order)
                return "实验" + toChineseNumeral(order)
            else return "加载中..."
        },
        goto(link) {
            this.$router.push(link)
        },
        gotoExp(expId) {
            this.goto(`/exp/detail/${expId}`)
        }
    },
    mounted: async function () {
        // this.experiments = (await this.$http.get("exp", {})).data;
        // if (this.experiments.length > 0 && !this.$route.params.id)
        //     this.gotoExp(this.experiments[0].id)

    }
}
</script>
