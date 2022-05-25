<template>
    <v-container fluid>
        <v-row justify="start">
            <v-col style="flex-grow: 0.5">
                <TestProcessPanel :judging="judging"
                                  :show-last-submit-code-download="showLastSubmitCodeDownload"
                />

            </v-col>
            <v-col justify="start">
                <ExperimentIntroduction :experiment="judging.experiment"/>
                <EventLine :events="judging.events" :curr-event="judging.currEvent"
                />
            </v-col>
        </v-row>
    </v-container>
</template>

<script>
import TestProcessPanel from "@/components/experiment/TestProcessPanel";
import EventLine from "@/components/experiment/EventLine";
import ExperimentIntroduction from "@/components/experiment/ExperimentIntroduction";

export default {
    name: "Judging",
    components: {TestProcessPanel, EventLine, ExperimentIntroduction},
    props:{
        judging:{},
        showLastSubmitCodeDownload:{
            type:Boolean,
            default:false
        }
    }
}
</script>

<style scoped>

</style>
