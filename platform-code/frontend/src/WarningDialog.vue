<template>
    <v-dialog
            v-model="warning"
            persistent
            max-width="320px"
    >
        <v-card>
            <v-card-title class="headline warning lighten-2" primary-title>
                警告
            </v-card-title>

            <v-card-text>
                <div style="margin-top:30px;">
                    <div
                        style="margin:0;display: block"
                        v-for="(line,index) of messages"
                        :key="index"
                    >
                        {{line}}
                    </div>
                </div>
            </v-card-text>

            <v-divider></v-divider>

            <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="primary" text @click="clearWarning">
                    OK
                </v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>
<script>
    export default {
        name: 'WarningDialog',
        props: {
        },
        data() {
            return {
            }
        },
        computed: {
            warning: {
                get:function(){
                    return this.$store.state.warning;
                },
                set:function(isShow){
                    if(!isShow)
                        this.clearWarning();
                }
            },
            messages(){
                return ((this.warning?.message)??"")
                .split(/\n/g)
                    ;
            }
        },

        methods: {
            clearWarning(){
                this.$store.commit('clearWarning');

            }
        },
    }
</script>










