<script setup>
import axios from "axios";
defineProps({
    token: String
})
</script>

<template>
    <div class="register-card">
        <div class="title"><i class="fa-regular fa-square-plus"></i> Add New Host</div>
        <div class="desc">Please follow the steps below to complete the addition of the new host. After adding, you can manage the server in real-time and monitor its running status.</div>
        <el-divider style="margin: 10px 0"/>
        <div class="sub-title">1. Deploy the Client</div>
        <div class="desc">Run the monitoring client program on the server that needs to be monitored. The client program relies on the Java 17 runtime environment, so please ensure it is installed in advance. After starting the client, you can proceed to the next step.</div>
        <div class="sub-title" style="margin-top: 10px">2. Enter the Monitoring Server Address</div>
        <div class="desc">This address is used for the client to report runtime status data in real-time. Please make sure it is entered correctly.</div>
        <el-input v-model="axios.defaults.baseURL" readonly/>
        <div class="sub-title" style="margin-top: 10px">3. Enter the Authorization Code</div>
        <div class="desc">All requests from the client must carry the authorization code in order to be properly recognized by the server.</div>
        <el-input :model-value="token" readonly/>
    </div>
</template>

<style scoped>
.register-card {
    margin: 15px 20px;

    .title {
        font-size: 18px;
        font-weight: bold;
    }

    .sub-title {
        font-size: 16px;
        font-weight: bold;
        color: dodgerblue;
    }

    .desc {
        font-size: 13px;
        color: grey;
        line-height: 16px;
    }
}
</style>