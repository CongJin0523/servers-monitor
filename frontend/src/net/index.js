import axios from "axios";
import {ElMessage} from "element-plus";

const authItemName = "access_token";
const defaultFailure = (url, code, message) => {
    console.warn(`Failed to retrieve url ${url}, code ${code}, message ${message}`);
    ElMessage(
        {
            message: message,
            type: "warning",
        }
    );
}

const defaultError = (error) => {
    console.error(`Error ${error}`);
    ElMessage(
        {
            message: 'An error occurred',
            type: "warning",
        }
    );

}

//token
function storeAccessToken(token, remember, expire) {
    const authObject = {token: token, expire: expire};
    const str = JSON.stringify(authObject);
    if (remember) {
        localStorage.setItem(authItemName, str);
    } else {
        sessionStorage.setItem(authItemName, str);
    }
}

function getAccessToken() {
    const str = localStorage.getItem(authItemName) || sessionStorage.getItem(authItemName);
    if (!str) {
        return null;
    }
    const authObject = JSON.parse(str);
    if (authObject.expire <= new Date()) {
        deleteAccessToken();
        ElMessage(
            {
                message: 'Login expired',
                type: "warning",
            }
        );
        return null;
    }
    return authObject.token;
}
function getAuthenticationHeader() {
    return getAccessToken() ? {Authorization: `Bearer ${getAccessToken()}`,} : null;
}

function deleteAccessToken() {
    localStorage.removeItem(authItemName);
    sessionStorage.removeItem(authItemName);
}

function authorized() {
    return !!getAccessToken();
}

function internalPost(url, data, header, success, failure = defaultFailure, error = defaultError) {
    axios.post(url, data, {headers : header})
    .then( ({data}) => {
        if (data && data.code === 200) {
            success(data.data);
        } else {
            failure(data.url, data.code, data.message);
        }
    }).catch( (err) => error(err));
}
function internalGet(url, header, success, failure = defaultFailure, error = defaultError) {
    axios.get(url, {headers : header})
        .then( ({data}) => {
            if (data && data.code === 200) {
                success(data.data);
            } else {
                failure(data.url, data.code, data.message);
            }
        }).catch( (err) => error(err));
}

function get(url, success, failure = defaultFailure) {
    internalGet(url, getAuthenticationHeader(), success, failure);
}
function post(url, data, success, failure) {
    internalPost(url, data, getAuthenticationHeader(), success, failure);
}

function login(username, password, remember, success, failure = defaultFailure, error = defaultError) {
    internalPost('api/auth/login', {
        username: username,
        password: password,
    }, {
        'Content-type': 'application/x-www-form-urlencoded;charset=utf-8',
    }, (data) => {
        storeAccessToken(data.token, remember, data.expire);
        ElMessage({
            message: `login success, welcome to ${data.username}!`,
            type: "success",
        })
        success(data);
    })
}

function logout(success, failure = defaultFailure) {
    get('api/auth/logout',
        (data) => {
            deleteAccessToken();
            ElMessage({
                message: `logout success!`,
                type: "success",
            })
            success(data);
        });
}



export {login, logout, post, get, authorized};