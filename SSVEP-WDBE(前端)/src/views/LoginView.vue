<template>
    <div :class="['container', { 'sign-up-mode': isSignUpMode }]">
        <div class="forms-container">
            <div class="signin-signup">
                <!-- 登录表单 -->
                <form class="sign-in-form" @submit.prevent="onSignIn">
                    <h2 class="title">登录</h2>
                    <div class="input-field">
                        <img src="../../public/image/icon/login/accout.png" class="login_icon"/>
                        <input type="text" placeholder="账号" v-model="signInData.username" required />
                    </div>
                    <div class="input-field">
                        <img src="../../public/image/icon/login/password.png" class="login_icon"/>
                        <input type="password" placeholder="密码" v-model="signInData.password" required />
                    </div>
                    <button type="submit" class="btn solid">立即登录</button>
                </form>

                <!-- 注册表单 -->
                <form class="sign-up-form" @submit.prevent="onSignUp">
                    <h2 class="title">注册</h2>
                    <div class="input-field">
                        <img src="../../public/image/icon/login/accout.png" class="login_icon"/>
                        <input type="text" placeholder="账号" v-model="signUpData.username" required />
                    </div>
                    <div class="input-field">
                        <img src="../../public/image/icon/login/name.png" class="login_icon"/>
                        <input type="text" placeholder="用户名" v-model="signUpData.name" required />
                    </div>
                    <div class="input-field">
                        <img src="../../public/image/icon/login/password.png" class="login_icon"/>
                        <input type="password" placeholder="密码" v-model="signUpData.password" required />
                    </div>
                    <button type="submit" class="btn">立即注册</button>
                </form>
            </div>
        </div>

        <!-- 左侧面板 -->
        <div class="panels-container">
            <div class="panel left-panel">
                <div class="content">
                    <h3>加入我们</h3>
                    <p>加入我们，成为本站的一份子。</p>
                    <button class="btn transparent" @click="toggleSignUpMode">去注册</button>
                </div>
                <img src="/image/login/login.svg" class="image" alt="Login Image" />
            </div>

            <!-- 右侧面板 -->
            <div class="panel right-panel">
                <div class="content">
                    <h3>已有帐号？</h3>
                    <p>立即登录已有帐号，享受独家权益。</p>
                    <button class="btn transparent" @click="toggleSignUpMode">去登录</button>
                </div>
                <img src="/image/login/register.svg" class="image" alt="Register Image" />
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

const router = useRouter();
const isSignUpMode = ref(false);
const signInData = ref({ username: '', password: '' });
const signUpData = ref({ username: '', name: '', password: '' });

const toggleSignUpMode = () => {
    isSignUpMode.value = !isSignUpMode.value;
};

const onSignIn = async () => {
    // router.push('/main/HomeView');
    try {
        // 向后端 API 发送登录请求
        const response = await axios.post('http://localhost:8080/users', {
            action: 'login',
            username: signInData.value.username,
            password: signInData.value.password,
        });

        console.log(response);
        signInData.value = { username: '', password: ''};

        if (response.data.status === 'success') {
            const token = response.data.token;
            localStorage.setItem('authToken', token);

            router.push('/main/HomeView');
        } else {
            console.error('登录失败:', response.data.message);
        }
    } catch (error) {
        console.error('登录失败：', error.response ? error.response.data : error.message);
        signInData.value = { username: '', password: ''};
    }
};

const onSignUp = async () => {
    console.log("注册数据：", signUpData.value);
    try {
        // 向后端 API 发送注册请求
        const response = await axios.post('http://localhost:8080/users', {
            action: 'register',
            username: signUpData.value.username,
            password: signUpData.value.password,
            name: signUpData.value.name,
        });

        if (response.data.status === 'success') {
            console.log('注册成功:', response.data.message);
            toggleSignUpMode(); // 注册后返回到登录页面
            signUpData.value = { username: '', password: '', name: '' };
        } else {
            console.error('注册失败:', response.data.message);
        }
    } catch (error) {
        signUpData.value = { username: '', password: '', name: '' };
        if (error.response) {
            console.error('注册失败:', error.response.data);
        } else {
            console.error('请求失败:', error.message);
        }
    }
};
</script>

<style lang="less" scoped>

* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

body,
input {
    font-family: "Poppins", sans-serif;
}

.container {
    position: relative;
    width: 100%;
    background-color: #fff;
    min-height: 100vh;
    overflow: hidden;
}

.forms-container {
    position: absolute;
    width: 100%;
    height: 100%;
    top: 0;
    left: 0;
}

.signin-signup {
    position: absolute;
    top: 50%;
    transform: translate(-50%, -50%);
    left: 75%;
    width: 50%;
    transition: 1s 0.7s ease-in-out;
    display: grid;
    grid-template-columns: 1fr;
    z-index: 5;
}

form {
    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: column;
    padding: 0rem 5rem;
    transition: all 0.2s 0.7s;
    overflow: hidden;
    grid-column: 1 / 2;
    grid-row: 1 / 2;
}

form.sign-up-form {
    opacity: 0;
    z-index: 1;
}

form.sign-in-form {
    z-index: 2;
}

.title {
    font-size: 2.2rem;
    color: #444;
    margin-bottom: 10px;
}

.input-field {
    max-width: 380px;
    width: 100%;
    background-color: #f0f0f0;
    margin: 10px 0;
    height: 55px;
    border-radius: 55px;
    display: grid;
    grid-template-columns: 15% 85%;
    padding: 0 0.4rem;
    position: relative;
}

.input-field i {
    text-align: center;
    line-height: 55px;
    color: #acacac;
    transition: 0.5s;
    font-size: 1.1rem;
}

.input-field input {
    background: none;
    outline: none;
    border: none;
    line-height: 1;
    font-weight: 600;
    font-size: 1.1rem;
    color: #333;
}

.login_icon{
    height: 28px;
    width: 28px;
    margin-left: 15px;
    margin-top: 12px;
}

.input-field input::placeholder {
    color: #aaa;
    font-weight: 500;
}



.btn {
    width: 150px;
    background-color: #6762ff;
    border: none;
    outline: none;
    height: 49px;
    border-radius: 49px;
    color: #fff;
    text-transform: uppercase;
    font-weight: 600;
    margin: 10px 0;
    cursor: pointer;
    transition: 0.5s;
}

.btn:hover {
    background-color: #6762ff;
}

.panels-container {
    position: absolute;
    height: 100%;
    width: 100%;
    top: 0;
    left: 0;
    display: grid;
    grid-template-columns: repeat(2, 1fr);
}

.container:before {
    content: "";
    position: absolute;
    height: 2000px;
    width: 2000px;
    top: -10%;
    right: 48%;
    transform: translateY(-50%);
    background-image: linear-gradient(-45deg, #6762ff 0%, #8b87ff 100%);
    transition: 1.8s ease-in-out;
    border-radius: 50%;
    z-index: 6;
}

.image {
    width: 100%;
    transition: transform 1.1s ease-in-out;
    transition-delay: 0.4s;
}

.panel {
    display: flex;
    flex-direction: column;
    align-items: flex-end;
    justify-content: space-around;
    text-align: center;
    z-index: 6;
}

.left-panel {
    pointer-events: all;
    padding: 3rem 17% 2rem 12%;
}

.right-panel {
    pointer-events: none;
    padding: 3rem 12% 2rem 17%;
}

.panel .content {
    color: #fff;
    transition: transform 0.9s ease-in-out;
    transition-delay: 0.6s;
}

.panel h3 {
    font-weight: 600;
    line-height: 1;
    font-size: 1.5rem;
}

.panel p {
    font-size: 0.95rem;
    padding: 0.7rem 0;
}

.btn.transparent {
    margin: 0;
    background: none;
    border: 2px solid #fff;
    width: 130px;
    height: 41px;
    font-weight: 600;
    font-size: 0.8rem;
}

.right-panel .image,
.right-panel .content {
    transform: translateX(800px);
}

/* ANIMATION */

.container.sign-up-mode:before {
    transform: translate(100%, -50%);
    right: 52%;
}

.container.sign-up-mode .left-panel .image,
.container.sign-up-mode .left-panel .content {
    transform: translateX(-800px);
}

.container.sign-up-mode .signin-signup {
    left: 25%;
}

.container.sign-up-mode form.sign-up-form {
    opacity: 1;
    z-index: 2;
}

.container.sign-up-mode form.sign-in-form {
    opacity: 0;
    z-index: 1;
}

.container.sign-up-mode .right-panel .image,
.container.sign-up-mode .right-panel .content {
    transform: translateX(0%);
}

.container.sign-up-mode .left-panel {
    pointer-events: none;
}

.container.sign-up-mode .right-panel {
    pointer-events: all;
}

@media (max-width: 870px) {
    .container {
        min-height: 800px;
        height: 100vh;
    }

    .signin-signup {
        width: 100%;
        top: 95%;
        transform: translate(-50%, -100%);
        transition: 1s 0.8s ease-in-out;
    }

    .signin-signup,
    .container.sign-up-mode .signin-signup {
        left: 50%;
    }

    .panels-container {
        grid-template-columns: 1fr;
        grid-template-rows: 1fr 2fr 1fr;
    }

    .panel {
        flex-direction: row;
        justify-content: space-around;
        align-items: center;
        padding: 2.5rem 8%;
        grid-column: 1 / 2;
    }

    .right-panel {
        grid-row: 3 / 4;
    }

    .left-panel {
        grid-row: 1 / 2;
    }

    .image {
        width: 200px;
        transition: transform 0.9s ease-in-out;
        transition-delay: 0.6s;
    }

    .panel .content {
        padding-right: 15%;
        transition: transform 0.9s ease-in-out;
        transition-delay: 0.8s;
    }

    .panel h3 {
        font-size: 1.2rem;
    }

    .panel p {
        font-size: 0.7rem;
        padding: 0.5rem 0;
    }

    .btn.transparent {
        width: 110px;
        height: 35px;
        font-size: 0.7rem;
    }

    .container:before {
        width: 1500px;
        height: 1500px;
        transform: translateX(-50%);
        left: 30%;
        bottom: 68%;
        right: initial;
        top: initial;
        transition: 2s ease-in-out;
    }

    .container.sign-up-mode:before {
        transform: translate(-50%, 100%);
        bottom: 32%;
        right: initial;
    }

    .container.sign-up-mode .left-panel .image,
    .container.sign-up-mode .left-panel .content {
        transform: translateY(-300px);
    }

    .container.sign-up-mode .right-panel .image,
    .container.sign-up-mode .right-panel .content {
        transform: translateY(0px);
    }

    .right-panel .image,
    .right-panel .content {
        transform: translateY(300px);
    }

    .container.sign-up-mode .signin-signup {
        top: 5%;
        transform: translate(-50%, 0);
    }
}

@media (max-width: 570px) {
    form {
        padding: 0 1.5rem;
    }

    .image {
        display: none;
    }

    .panel .content {
        padding: 0.5rem 1rem;
    }

    .container {
        padding: 1.5rem;
    }

    .container:before {
        bottom: 72%;
        left: 50%;
    }

    .container.sign-up-mode:before {
        bottom: 28%;
        left: 50%;
    }
}
</style>