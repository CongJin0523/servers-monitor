<script setup>
import {ref, reactive} from "vue";
import {Lock, User} from "@element-plus/icons-vue";
import {login} from "@/net";
import router from "@/router/index.js";

const loginFormRef = ref();

const loginForm = reactive({
    username: '',
    password: '',
    remember: false,
})

const loginFormRule = {
    username: [
        {required: true, message: 'Please input username'},
    ],
    password: [
        {required: true, message: 'Please input password'},
    ]
}

function userLogin() {
    loginFormRef.value.validate(valid => {
        if (valid) {
            login(loginForm.username, loginForm.password, loginForm.remember, () => {router.push("/index")});
        }
    })
}
</script>

<template>
    <div style="text-align: center; margin: 0 40px">
        <div style="margin-top: 150px">
            <div style="font-size: 25px;font-weight: bold;">Login</div>
        </div>
        <div style="margin-top: 50px">
            <el-form :model="loginForm" label-position="top" :rules="loginFormRule" ref="loginFormRef" >
                <el-form-item label="Username" prop="username">
                    <el-input v-model="loginForm.username" placeholder="Username/Email"  maxlength="20" type="text">
                        <template #prefix>
                            <el-icon><User/></el-icon>
                        </template>
                    </el-input>
                </el-form-item>
                <el-form-item label="Password" prop="password">
                    <el-input v-model="loginForm.password" placeholder="Password"  maxlength="20" type="password">
                        <template #prefix>
                            <el-icon><Lock/></el-icon>
                        </template>
                    </el-input>
                </el-form-item>
                <el-row>
                    <el-col :span="12" style="text-align: left;">
                        <el-form-item prop="remember">
                            <el-checkbox v-model="loginForm.remember">Rememberme</el-checkbox>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12" style="text-align: right;">
                        <el-link @click="router.push('/reset')">Forget Password</el-link>
                    </el-col>
                </el-row>
            </el-form>
        </div>
        <div style="margin-top: 20px">
            <el-button @click="userLogin" type="primary" style="width: 80%">Login</el-button>
        </div>
        <el-divider>
            <span style="font-size: 13px; color: gray">Create User</span>
        </el-divider>
        <div style="margin-top: 20px">
            <el-button @click="router.push('/register')" type="warning"  style="width: 80%" plain>Sign up</el-button>
        </div>

    </div>
</template>

<style scoped>
.el-form-item {
    text-align: left;
    font-weight: bold;
}
</style>