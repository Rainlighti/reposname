<template>
    <div class="text-center ">
        <v-snackbar
                v-model="toastMessage"
                top
        >
            {{ toastMessage}}

            <template v-slot:action="{ attrs }">
                <v-btn
                        color="pink"
                        text
                        v-bind="attrs"
                        @click="clear"
                >
                    关闭
                </v-btn>
            </template>
        </v-snackbar>
    </div>
</template>
<script>
    export default {
        name: 'MessageToast',
        props: {
        },
        data() {
            return {
            }
        },
        computed: {
            toastMessage: {
                get:function(){
                    return this.$store.state.toastMessage;
                },
                set:function(isShow){
                    if(!isShow)
                        this.clear();
                }
            }
        },

        methods: {
            clear(){
                this.$store.commit('clearToast');
            }
        },
    }
</script>










