<script setup>
import PreviewCard from "@/components/PreviewCard.vue";
import {useRoute} from "vue-router";
import {ref, reactive} from "vue";
import {get} from "@/net"
import ClientDetails from "@/components/ClientDetails.vue";


const list = ref([])
const route = useRoute()
const updateList = () => {
    if (route.name === 'manage') {
        get('/api/monitor/list', data => {
            list.value = data
            console.log(data)
        })
    }
}
setInterval(updateList, 10000)
updateList()

const detail = reactive({
    show: false,
    id: -1,
})
const displayClientDetails = (id) => {
    detail.show = true
    detail.id = id
}
</script>

<template>
    <div class="manage-main">
        <div class="title"><i class="fa-solid fa-server"></i>
            Servers
        </div>
        <div class="desc">
            Manage all registered servers instances here, monitor their real-time status, and quickly perform management
            and operations.
        </div>
        <el-divider style="margin: 10px 0"/>
        <div class="card-list">
            <PreviewCard v-for="item in list" :data="item" :update="updateList" @click="displayClientDetails(item.id)"/>
        </div>
        <el-divider style="margin: 10px 0"/>
        <el-drawer size="520" :show-close="false" v-model="detail.show" :with-header="false" v-if="list.length" @close="detail.id = -1" >
            <ClientDetails :id="detail.id" :update="updateList"></ClientDetails>
        </el-drawer>
    </div>
</template>

<style scoped>
:deep(.el-drawer__header) {
    margin-bottom: 10px;
}

:deep(.el-checkbox-group .el-checkbox) {
    margin-right: 10px;
}

:deep(.el-drawer) {
    margin: 10px;
    height: calc(100% - 20px);
    border-radius: 10px;
}

:deep(.el-drawer__body) {
    padding: 0;
}

.manage-main {
    margin: 0 50px;

    .title {
        font-size: 22px;
        font-weight: bold;
    }

    .desc {
        font-size: 15px;
        color: grey;
    }
}

.card-list {
    display: flex;
    gap: 20px;
    flex-wrap: wrap;
}
</style>