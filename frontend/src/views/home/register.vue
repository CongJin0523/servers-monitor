<script setup>
import {reactive, ref, computed} from "vue";
import {EditPen, Lock, Message, User} from "@element-plus/icons-vue";
import router from "@/router/index.js";
import {get, post} from "@/net"
import {ElMessage} from "element-plus";

const coldTimer = ref(0);
const formRef = ref();
const form = reactive({
    username: '',
    password: '',
    password_repeat: '',
    email: '',
    code: '',
})
const validateUsername = (rule, value, callback) => {
    if (value === ''){
        callback(new Error('please enter username'));
    } else if (!/^[a-zA-Z0-9]+$/.test(value)) {
        callback(new Error('Username is not allowed including special characters'));
    } else {
        callback();
    }
}
const validatePassword = (rule, value, callback) => {
    if (value === '') {
        callback(new Error('please enter password again'));
    } else if (value !== form.password) {
        callback(new Error("Password twice does not match"));
    } else {
        callback();
    }
}

const validateCode = (rule, value, callback) => {
    if (!/^[0-9]{6}$/.test(value)) {
        callback(new Error('please enter valid code'));
    } else {
        callback();
    }
}


const rule = {
    username: [{validator: validateUsername, trigger: ['blur', 'change']}],
    password: [{required : true, messages : "please enter password", trigger: 'blur'},
        {min: 6, max: 20, message: "The length of password must be between 6 and 20 characters", trigger: ['blur', 'change']},],
    password_repeat: [{validator: validatePassword, trigger: ['blur', 'change']}],
    email: [{required: true, type: 'email', trigger: ['blur', 'change'], messages: "Please enter valid email"}],
    code: [{validator: validateCode, trigger: ['blur', 'change']},],
}
let timer = null;
function askValidCode() {
    if (isValidEmail) {
        get(`/api/auth/verify-code?email=${form.email}&type=register`,
            () => {
                coldTimer.value = 60;
                timer = setInterval(() =>
                    {
                        coldTimer.value--;
                        if (coldTimer.value <= 0) {
                            clearInterval(timer);
                            timer = null;
                        }
                    }, 1000);
                ElMessage.success('Verification Code has been sent successfully!');
            }, () => {
                clearInterval(timer);
                timer = null;
                coldTimer.value = 0;
                ElMessage.error('Verification Code sent failed! Please try again!');
            })
    } else {
        ElMessage.error('Please enter a valid email');
    }
}


const isValidEmail = computed(() => /^[\w\\.-]+@[\w\\.-]+\.\w+$/.test(form.email));

function submitRegisterForm() {
    formRef.value.validate((valid) => {
        if (valid) {
            post('/api/auth/register',
                {
                    username: form.username,
                    password: form.password,
                    email: form.email,
                    code: form.code,
                },
                () => {
                ElMessage.success('Register successfully!');
                router.push('/');
                }
            )
        } else {
            ElMessage.error('Please check the form for errors.')
        }
    })
}
</script>

<template>
    <div style="text-align: center; margin: 0 40px">
        <div style="margin-top: 150px">
            <div style="font-size: 25px;font-weight: bold;">Create a AW Account</div>
        </div>
        <div style="margin-top: 50px; width: 100%">
            <el-form :model="form" label-position="top" :rules="rule" ref='formRef'>
                <el-form-item prop="username">
                    <el-input v-model="form.username" placeholder="Username" maxlength="20" type="text">
                        <template #prefix>
                            <el-icon>
                                <User/>
                            </el-icon>
                        </template>
                    </el-input>
                </el-form-item>
                <el-form-item prop="password">
                    <el-input v-model="form.password" placeholder="Password" maxlength="20" type="password">
                        <template #prefix>
                            <el-icon>
                                <Lock/>
                            </el-icon>
                        </template>
                    </el-input>
                </el-form-item>
                <el-form-item prop="password_repeat">
                    <el-input v-model="form.password_repeat" placeholder="Repeat password" maxlength="20"
                              type="password">
                        <template #prefix>
                            <el-icon>
                                <Lock/>
                            </el-icon>
                        </template>
                    </el-input>
                </el-form-item>
                <el-form-item prop="email">
                    <el-input v-model="form.email" placeholder="email" type="text">
                        <template #prefix>
                            <el-icon>
                                <Message/>
                            </el-icon>
                        </template>
                    </el-input>
                </el-form-item>
                <el-form-item prop="code">
                    <el-row :gutter="10" style="width: 100%;">
                        <el-col :span="17">
                            <el-input v-model="form.code" placeholder="Enter Code" minlength="6" maxlength="6" type="text">
                                <template #prefix>
                                    <el-icon>
                                        <EditPen/>
                                    </el-icon>
                                </template>
                            </el-input>
                        </el-col>
                        <el-col :span="5">
                            <el-button @click="askValidCode" :disabled="!isValidEmail || coldTimer" type="success">{{coldTimer > 0 ? `Try again after ${coldTimer}S` : `Get Code`}}</el-button>
                        </el-col>
                    </el-row>
                </el-form-item>
            </el-form>
        </div>
        <div style="margin-top: 50px">
            <el-button  @click="submitRegisterForm" type="primary" style="width: 80%">Submit</el-button>
        </div>
        <div style="margin-top: 20px">
            <span style="font-size: 14px;line-height: 15px;">Already have an account? </span>
            <el-link style="translate: 0 -1px" @click="router.push('/')">Log in</el-link>
        </div>
    </div>
</template>

<style scoped>
.el-form-item {
    text-align: left;
    font-weight: bold;
}
</style>