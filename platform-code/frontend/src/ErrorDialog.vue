<template>
    <v-dialog
            v-model="error"
            persistent
            max-width="320px"
    >
        <v-card>
            <v-card-title class="headline red lighten-2" primary-title>
                错误
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
                <v-btn color="primary" text @click="clearError">
                    OK
                </v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>
<script>
    export default {
        name: 'ErrorDialog',
        props: {
        },
        data() {
            return {
            }
        },
        computed: {
            error: {
                get:function(){
                    return this.$store.state.error;
                },
                set:function(isShow){
                    if(!isShow)
                        this.clearError();
                }
            },
            messages(){
                return ((this.error?.message)??"")
                    .split(/\n/g)
                    ;
            }
        },

        methods: {
            clearError(){
                this.$store.commit('clearError');

            }
        },
    }
</script>










