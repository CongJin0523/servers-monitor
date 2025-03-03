<script setup>
import {computed, reactive, ref} from "vue";
import router from "@/router/index.js";
import {EditPen, Lock, Message, User} from "@element-plus/icons-vue";
import {get, post} from "@/net/index.js";
import {ElMessage} from "element-plus";

const active = ref(0);
const coldTimer = ref(0);
const formRef = ref();
let timer = null;
const form = reactive({
    email: '',
    code: '',
    password: '',
    password_repeat: '',
})
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

const isValidEmail = computed(() => /^[\w\\.-]+@[\w\\.-]+\.\w+$/.test(form.email));

function askValidCode() {
    if (isValidEmail) {
        get(`/api/auth/verify-code?email=${form.email}&type=reset`,
            () => {
                coldTimer.value = 60;
                timer = setInterval(() => {
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

const rule = {
    password: [{required: true, messages: "please enter password", trigger: 'blur'},
        {
            min: 6,
            max: 20,
            message: "The length of password must be between 6 and 20 characters",
            trigger: ['blur', 'change']
        },],
    password_repeat: [{validator: validatePassword, trigger: ['blur', 'change']}],
    email: [{required: true, type: 'email', trigger: ['blur', 'change'], messages: "Please enter valid email"}],
    code: [{validator: validateCode, trigger: ['blur', 'change']},],
}

function confirmCode() {
    post(`/api/auth/confirm-verify-code`,
        {
            email: form.email,
            code: form.code,
        }, () => {
        active.value++;
        }, () => {
        ElMessage.warning('Verification code is incorrect! Please try again!');
        })
}
function submitResetForm() {
    router.push("/");
    formRef.value.validate((valid) => {
        if (valid) {

            post('/api/auth/reset-password',
                {
                    password: form.password,
                    email: form.email,
                    code: form.code,
                },
                () => {
                    ElMessage.success('Reset successfully!');
                    active.value = 0;
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
    <div>
        <div style="margin-top: 150px">
            <el-steps align-center :active="active" finish-status="success">
                <el-step title="Validate Email"></el-step>
                <el-step title="Reset Password"></el-step>
            </el-steps>
        </div>
        <div style="text-align: center; margin: 0 40px" v-if="active === 0">
            <div style="margin-top: 50px">
                <div style="font-size: 25px;font-weight: bold;">Reset Password</div>
                <div style="font-size: 14px; color: gray">Please enter the email used when you registered</div>
            </div>
            <div style="margin-top: 50px; width: 100%">
                <el-form :model="form" label-position="top" :rules="rule">
                    <el-form-item prop="email">
                        <el-input v-model="form.email" placeholder="Please enter account email" type="text">
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
                                <el-input v-model="form.code" placeholder="Enter Code" minlength="6" maxlength="6"
                                          type="text">
                                    <template #prefix>
                                        <el-icon>
                                            <EditPen/>
                                        </el-icon>
                                    </template>
                                </el-input>
                            </el-col>
                            <el-col :span="5">
                                <el-button @click="askValidCode" :disabled="!isValidEmail || coldTimer" type="success">
                                    {{ coldTimer > 0 ? `Try again after ${coldTimer}S` : `Get Code` }}
                                </el-button>
                            </el-col>
                        </el-row>
                    </el-form-item>
                </el-form>
            </div>
            <div style="margin-top: 50px">
                <el-button @click="confirmCode" type="primary" style="width: 80%">Start Reset</el-button>
            </div>
        </div>
        <div style="text-align: center; margin: 0 40px" v-if="active === 1">
            <div style="margin-top: 50px">
                <div style="font-size: 25px;font-weight: bold;">Reset Password</div>
                <div style="font-size: 14px; color: gray">Please enter your new password</div>
            </div>
            <div style="margin-top: 50px">
                <el-form :model="form" label-position="top" :rules="rule" ref="formRef">
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
                </el-form>
            </div>
            <div style="margin-top: 50px">
                <el-button @click="submitResetForm" type="primary" style="width: 80%">Reset</el-button>
            </div>
        </div>
        <div style="margin-top: 20px; text-align: center">
            <span style="font-size: 14px;line-height: 15px;">Already have an account? </span>
            <el-link style="translate: 0 -1px" @click="router.push('/')">Log in</el-link>
        </div>
    </div>
</template>


<style scoped>

</style>