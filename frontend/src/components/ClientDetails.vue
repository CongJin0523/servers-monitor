<script setup>
import {watch, reactive, computed} from "vue";
import {get} from "@/net"
import {copyIp, cpuNameToImage, fitByUnit, osNameToIcon, percentageToStatus, rename} from '@/tools'
import {Money} from "@element-plus/icons-vue";
import RuntimeHistory from "@/components/RuntimeHistory.vue";

const props = defineProps({
    id: Number,
    update: Function,
})

const details = reactive({
    base: {},
    runtime: {
        list: []
    }
})

function updateDetails() {
    props.update()
    init(props.id)
}
let timer = setInterval(() => {
    if(props.id !== -1 && details.runtime) {
        get(`/api/monitor/runtime-now?clientId=${props.id}`, data => {
            if(details.runtime.list.length >= 360)
                details.runtime.list.splice(0, 1)
            details.runtime.list.push(data)
        })
    } else {
        clearInterval(timer)
    }
}, 10000)


const now = computed(() => details.runtime.list[details.runtime.list.length - 1])
const init = id => {
    if(id !== -1) {
        details.base = {}
        details.runtime = { list: [] }
        get(`/api/monitor/details?clientId=${id}`, data => Object.assign(details.base, data))
        get(`/api/monitor/runtime-history?clientId=${id}`, data => Object.assign(details.runtime, data))
        console.info(details.runtime)
    }
}
watch(() => props.id, init, { immediate: true })



</script>


<template>
    <el-scrollbar>
        <div class="client-details" v-loading="Object.keys(details.base).length === 0">
            <div v-if="Object.keys(details.base).length">
                <div style="display: flex;justify-content: space-between">
                    <div class="title">
                        <i class="fa-solid fa-server"></i>
                        Server Info
                    </div>
                </div>
                <el-divider style="margin: 10px 0"/>
                <div class="details-list">
                    <div>
                        <span>Server ID</span>
                        <span>{{ details.base.id }}</span>
                    </div>
                    <div>
                        <span>Server Name</span>
                        <span>{{ details.base.name }}</span>&nbsp;
                        <i @click.stop="rename(details.base.id, details.base.name, updateDetails)"
                           class="fa-solid fa-pen-to-square interact-item"/>
                    </div>
                    <div>
                        <span>Status</span>
                        <span>
                    <i style="color: #18cb18" class="fa-solid fa-circle-play" v-if="details.base.online"></i>
                    <i style="color: #18cb18" class="fa-solid fa-circle-stop" v-else></i>
                    {{ details.base.online ? 'Online' : 'Offline' }}
                </span>
                    </div>
                    <div v-if="!details.editNode">
                        <span>Server Node</span>
                        <span :class="`flag-icon flag-icon-${details.base.location}`"></span>&nbsp;
                        <span>{{details.base.node}}</span>&nbsp;
                        <i @click.stop="enableNodeEdit"
                           class="fa-solid fa-pen-to-square interact-item"/>
                    </div>
                    <div v-else>
                        <span>Server Node</span>
                        <div style="display: inline-block;height: 15px">
                            <!--                        <div style="display: flex">-->
                            <!--                            <el-select v-model="nodeEdit.location" style="width: 80px" size="small">-->
                            <!--                                <el-option v-for="item in locations" :value="item.name">-->
                            <!--                                    <span :class="`flag-icon flag-icon-${item.name}`"></span>&nbsp;-->
                            <!--                                    {{item.desc}}-->
                            <!--                                </el-option>-->
                            <!--                            </el-select>-->
                            <!--                            <el-input v-model="nodeEdit.name" style="margin-left: 10px"-->
                            <!--                                      size="small" placeholder="Please enter node name..."/>-->
                            <!--                            <div style="margin-left: 10px">-->
                            <!--                                <i @click.stop="submitNodeEdit" class="fa-solid fa-check interact-item"/>-->
                            <!--                            </div>-->
                            <!--                        </div>-->
                        </div>
                    </div>
                    <div>
                        <span>IP address</span>
                        <span>
                    {{ details.base.ip }}
                    <i class="fa-solid fa-copy interact-item" style="color: dodgerblue"
                       @click.stop="copyIp(details.base.ip)"></i>
                </span>
                    </div>
                    <div style="display: flex">
                        <span>Processor</span>
                        <span>{{ details.base.cpuName }}</span>
                        <el-image style="height: 20px;margin-left: 10px"
                                  :src="`/cpu-icons/${cpuNameToImage(details.base.cpuName)}`"/>
                    </div>
                    <div>
                        <span>Hardware Info</span>
                        <span>
            <i class="fa-solid fa-microchip"></i>
            <span style="margin-right: 10px">{{ ` ${details.base.cpuCore} CPU Cores /` }}</span>
            <i class="fa-solid fa-memory"></i>
            <span>{{ ` ${details.base.memory?.toFixed(1) ?? '0.0'} GB Memory` }}</span>
          </span>
                    </div>
                    <div>
                        <span>Operating System</span>
                        <i :style="{color: osNameToIcon(details.base.osName).color}"
                           :class="`fa-brands ${osNameToIcon(details.base.osName).icon}`"></i>
                        <span style="margin-left: 10px">{{ `${details.base.osName} ${details.base.osVersion}` }}</span>
                    </div>
                </div>
                <div class="title" style="margin-top: 20px">
                    <i class="fa-solid fa-gauge-high"></i>
                    Activity Monitor
                </div>
                <el-divider style="margin: 10px 0"/>
                <div v-if="details.base.online" v-loading="!details.runtime.list.length"
                     style="min-height: 200px">
                    <div style="display: flex" v-if="details.runtime.list.length">
                        <el-progress type="dashboard" :width="100" :percentage="now.cpuUsage * 100"
                                     :status="percentageToStatus(now.cpuUsage * 100)">
                            <div style="font-size: 17px;font-weight: bold;color: initial">CPU</div>
                            <div style="font-size: 13px;color: grey;margin-top: 5px">{{ (now.cpuUsage * 100).toFixed(1) }}%</div>
                        </el-progress>
                        <el-progress style="margin-left: 20px" type="dashboard" :width="100"
                                     :percentage="now.memoryUsage / details.runtime.memory * 100"
                                     :status="percentageToStatus(now.memoryUsage / details.runtime.memory * 100)">
                            <div style="font-size: 16px;font-weight: bold;color: initial">Memory</div>
                            <div style="font-size: 13px;color: grey;margin-top: 5px">{{ (now.memoryUsage).toFixed(1) }} GB</div>
                        </el-progress>
                        <div style="flex: 1;margin-left: 30px;display: flex;flex-direction: column;height: 80px">
                            <div style="flex: 1;font-size: 14px">
                                <div>Network</div>
                                <div>
                                    <i style="color: orange" class="fa-solid fa-arrow-up"></i>
                                    <span>{{` ${fitByUnit(now.networkUpload, 'KB')}/s`}}</span>
                                    <el-divider direction="vertical"/>
                                    <i style="color: dodgerblue" class="fa-solid fa-arrow-down"></i>
                                    <span>{{` ${fitByUnit(now.networkDownload, 'KB')}/s`}}</span>
                                </div>
                            </div>
                            <div>
                                <div style="font-size: 13px;display: flex;justify-content: space-between">
                                    <div>
                                        <i class="fa-solid fa-hard-drive"></i>
                                        <span>Storage Capacity</span>
                                    </div>
                                    <div>{{now.diskUsage.toFixed(1)}} GB / {{details.runtime.disk.toFixed(1)}} GB</div>
                                </div>
                                <el-progress type="line" :show-text="false"
                                             :status="percentageToStatus(now.diskUsage / details.runtime.disk * 100)"
                                             :percentage="now.diskUsage / details.runtime.disk * 100" />
                            </div>
                        </div>
                    </div>
                    <RuntimeHistory style="margin-top: 20px" :data="details.runtime.list"/>

                </div>
                <el-empty description="The Server is offline, please check if it is running properly" v-else/>
            </div>

        </div>
    </el-scrollbar>
</template>

<style scoped>
interact-item {
    transition: .3s;

    &:hover {
        cursor: pointer;
        scale: 1.1;
        opacity: 0.8;
    }
}

.client-details {
    height: 100%;
    padding: 20px;

    .title {
        color: dodgerblue;
        font-size: 18px;
        font-weight: bold;
    }

    .details-list {
        font-size: 14px;

        & div {
            margin-bottom: 10px;

            & span:first-child {
                color: gray;
                font-size: 13px;
                font-weight: normal;
                width: 120px;
                display: inline-block;
            }

            & span {
                font-weight: bold;
            }
        }
    }
}
</style>