<script setup>
import {logout} from "@/net/index.js";
import router from "@/router/index.js";
import DarkModeToggle from "@/components/DarkModeToggle.vue";
import {Back} from "@element-plus/icons-vue";
import TabItem from "@/components/TabItem.vue";
import {useRoute} from "vue-router";
import {ref} from "vue";

const route = useRoute()

const tabs = [
    {id: 1, name: "Manage", route: "manage"},
    {id: 2, name: "Security", route: "security"},
]

const defaultIndex = () => {
    for (let tab of tabs) {
        if(route.name === tab.route)
            return tab.id
    }
    return 1
}

const tab = ref(defaultIndex())
function changePage(item) {
    tab.value = item.id
    router.push({name: item.route})
}

function userLogout() {
    logout(() => router.push('/'));
}
</script>

<template>
    <el-container class="main-container">
        <el-header class="main-header">
            <el-image style="height: 30px" src="/logo.svg"/>
            <div class="tabs">
                <TabItem v-for="item in tabs" :name="item.name"
                          :active="item.id === tab" @click="changePage(item)"/>
                <DarkModeToggle style="margin: 0 15px" />
<!--                <div style="text-align: right;line-height: 16px;margin-right: 10px">-->
<!--                    <div>-->
<!--                        <el-tag type="success"  size="small">管理员</el-tag>-->
<!--                        <el-tag v-else size="small">子账户</el-tag>-->
<!--                    </div>-->
<!--                    <div style="font-size: 13px;color: grey"></div>-->
<!--                </div>-->
                <el-dropdown>
                    <el-avatar :size="30" class="avatar"
                               src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"/>
                    <template #dropdown>
                        <el-dropdown-menu>
                            <el-dropdown-item @click="userLogout">
                                <el-icon><Back/></el-icon>
                                Logout
                            </el-dropdown-item>
                        </el-dropdown-menu>
                    </template>
                </el-dropdown>
            </div>

        </el-header>
        <el-main class="main-content">
            <router-view v-slot="{ Component }">
                <transition name="el-fade-in-linear" mode="out-in">
                    <keep-alive exclude="Security">
                        <component :is="Component"/>
                    </keep-alive>
                </transition>
            </router-view>
        </el-main>
    </el-container>
</template>

<style scoped>
.main-container {
    height: 100vh;
    width: 100vw;

    .main-header {
        height: 55px;
        background-color: var(--el-bg-color);
        border-bottom: solid 1px var(--el-border-color);
        display: flex;
        align-items: center;

        .tabs {
            height: 55px;
            gap: 10px;
            flex: 1px;
            display: flex;
            align-items: center;
            justify-content: right;
        }
    }

    .main-content {
        height: 100%;
        background-color: #f5f5f5;
    }
}

.dark .main-container .main-content {
    background-color: #232323;
}
</style>