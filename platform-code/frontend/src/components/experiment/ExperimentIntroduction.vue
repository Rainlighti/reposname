<template>
    <v-card

    >
        <v-list-item two-line>
            <v-list-item-content>
                <v-list-item-title class="headline">
                    实验介绍
                </v-list-item-title>
                <v-list-item-subtitle>{{experiment.title}}</v-list-item-subtitle>
            </v-list-item-content>
        </v-list-item>
        <v-card-text>
            {{experiment.intro}}


        </v-card-text>
        <v-card-actions>
            <v-btn text :href="experiment.documentLink">
                实验指导书下载
            </v-btn>
        </v-card-actions>
    </v-card>
</template>
<script>
    export default {
        name: 'ExperimentIntroduction',
        props:{
            experiment:Object,
        },

    }
</script>
