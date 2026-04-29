<template>
  <div class="login-page">
    <!-- Retro-Futuristic Background -->
    <div class="login-bg">
      <!-- Animated geometric patterns -->
      <div class="geometric-layer layer-1">
        <div v-for="i in 12" :key="`geo-${i}`" class="geo-shape" :style="geoStyle(i)"></div>
      </div>

      <!-- Diagonal stripe overlay -->
      <div class="stripe-overlay"></div>

      <!-- Gradient background -->
      <div class="gradient-bg"></div>
    </div>

    <!-- Top Navigation -->
    <div class="top-nav">
      <div class="nav-back" @click="handleBack">
        <div class="back-brutal">
          <el-icon :size="22"><ArrowLeft /></el-icon>
        </div>
      </div>
      <div class="nav-title">登录</div>
      <div class="nav-spacer"></div>
    </div>

    <!-- Main Content -->
    <div class="login-content">

      <!-- Logo/Welcome Section -->
      <div class="welcome-section">
        <div class="logo-container">
          <!-- Animated logo ring -->
          <div class="logo-ring ring-1"></div>
          <div class="logo-ring ring-2"></div>

          <!-- Retro logo icon -->
          <div class="logo-icon">
            <svg viewBox="0 0 80 80" fill="none" xmlns="http://www.w3.org/2000/svg">
              <!-- Outer hexagon -->
              <path d="M40 5 L70 22.5 L70 57.5 L40 75 L10 57.5 L10 22.5 Z"
                stroke="rgba(255,255,255,0.3)" stroke-width="2" fill="none"/>
              <!-- Inner hexagon -->
              <path d="M40 18 L58 28.5 L58 51.5 L40 62 L22 51.5 L22 28.5 Z"
                stroke="rgba(255,255,255,0.5)" stroke-width="2" fill="none"/>
              <!-- Water drop -->
              <path d="M40 25 C40 25, 52 38, 52 48 C52 56.28, 46.63 62, 40 62 C33.37 62, 28 56.28, 28 48 C28 38, 40 25, 40 25 Z"
                fill="rgba(255,255,255,0.8)"/>
            </svg>
          </div>
        </div>

        <h1 class="welcome-title">欢迎回来</h1>
        <p class="welcome-sub">登录智能游泳馆，开启畅游之旅</p>
      </div>

      <!-- Login Form Card -->
      <div class="form-card">
        <el-form
          ref="loginRef"
          :model="loginForm"
          :rules="loginRules"
          @submit.prevent="handleLogin"
        >
          <!-- Username Input -->
          <el-form-item prop="username">
            <div class="brutal-input">
              <div class="input-label">用户名</div>
              <div class="input-wrapper">
                <div class="input-icon-box">
                  <el-icon :size="22"><User /></el-icon>
                </div>
                <el-input
                  v-model="loginForm.username"
                  placeholder="请输入用户名"
                  size="large"
                  auto-complete="off"
                  clearable
                  class="custom-input"
                />
              </div>
            </div>
          </el-form-item>

          <!-- Password Input -->
          <el-form-item prop="password">
            <div class="brutal-input">
              <div class="input-label">密码</div>
              <div class="input-wrapper">
                <div class="input-icon-box">
                  <el-icon :size="22"><Lock /></el-icon>
                </div>
                <el-input
                  v-model="loginForm.password"
                  type="password"
                  placeholder="请输入密码"
                  size="large"
                  auto-complete="off"
                  show-password
                  class="custom-input"
                  @keyup.enter="handleLogin"
                />
              </div>
            </div>
          </el-form-item>

          <!-- Captcha Input -->
          <el-form-item v-if="captchaEnabled" prop="code">
            <div class="brutal-input">
              <div class="input-label">验证码</div>
              <div class="captcha-wrapper">
                <div class="input-wrapper captcha-input">
                  <div class="input-icon-box">
                    <el-icon :size="22"><Key /></el-icon>
                  </div>
                  <el-input
                    v-model="loginForm.code"
                    placeholder="请输入验证码"
                    size="large"
                    auto-complete="off"
                    clearable
                    class="custom-input"
                    @keyup.enter="handleLogin"
                  />
                </div>
                <div class="captcha-brutal" @click="getCode">
                  <img v-if="codeUrl" :src="codeUrl" class="captcha-img" alt="验证码" />
                  <span v-else>获取</span>
                </div>
              </div>
            </div>
          </el-form-item>

          <!-- Form Actions -->
          <div class="form-actions">
            <div class="remember-check">
              <el-checkbox v-model="loginForm.rememberMe" class="brutal-checkbox">
                <span class="check-label">记住我</span>
              </el-checkbox>
            </div>
            <div class="forgot-link">
              <span>忘记密码?</span>
            </div>
          </div>

          <!-- Login Button -->
          <el-button
            type="primary"
            size="large"
            class="login-brutal-btn"
            :loading="loading"
            @click.prevent="handleLogin"
          >
            <span v-if="!loading" class="btn-content">
              <span class="btn-text">立即登录</span>
              <div class="btn-deco"></div>
            </span>
            <span v-else>登录中...</span>
          </el-button>
        </el-form>

        <!-- Divider -->
        <div class="divider-line">
          <span>还没有账号?</span>
        </div>

        <!-- Register Link -->
        <router-link to="/register" class="register-link">
          <div class="register-brutal">
            <span class="register-text">立即注册新账号</span>
            <div class="register-arrow">
              <el-icon :size="20"><ArrowRight /></el-icon>
            </div>
          </div>
        </router-link>
      </div>

    </div>

    <!-- Bottom Decorative Elements -->
    <div class="bottom-deco">
      <div class="deco-circle circle-1"></div>
      <div class="deco-triangle triangle-1"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { User, Lock, ArrowLeft, ArrowRight, Key } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { getCodeImg } from '@/api/auth'
import type { LoginData } from '@/types/user'

const router = useRouter()
const userStore = useUserStore()

const loginRef = ref<FormInstance>()
const loading = ref(false)
const captchaEnabled = ref(true)
const codeUrl = ref('')
const redirect = ref('/')

const loginForm = ref<LoginData>({
  username: '',
  password: '',
  rememberMe: false,
  code: '',
  uuid: '',
  grantType: 'password'
})

const loginRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' }
  ]
}

watch(
  () => router.currentRoute.value,
  (newRoute: any) => {
    redirect.value = newRoute.query && newRoute.query.redirect && decodeURIComponent(newRoute.query.redirect as string)
  },
  { immediate: true }
)

onMounted(() => {
  getCode()
  getLoginData()
})

const getCode = async () => {
  try {
    const res = await getCodeImg()
    if (res.code === 200 && res.data) {
      captchaEnabled.value = res.data.captchaEnabled === undefined ? true : res.data.captchaEnabled
      if (captchaEnabled.value) {
        codeUrl.value = 'data:image/gif;base64,' + res.data.img
        loginForm.value.uuid = res.data.uuid
      }
    }
  } catch (error) {
    console.error('获取验证码失败:', error)
  }
}

const getLoginData = () => {
  const username = localStorage.getItem('username')
  const password = localStorage.getItem('password')
  const rememberMe = localStorage.getItem('rememberMe')
  if (username && password) {
    loginForm.value.username = username
    loginForm.value.password = password as string
    loginForm.value.rememberMe = Boolean(rememberMe)
  }
}

const handleBack = () => {
  router.back()
}

const handleLogin = async () => {
  if (!loginRef.value) return

  await loginRef.value.validate(async (valid: boolean) => {
    if (valid) {
      loading.value = true

      if (loginForm.value.rememberMe) {
        localStorage.setItem('username', String(loginForm.value.username))
        localStorage.setItem('password', String(loginForm.value.password))
        localStorage.setItem('rememberMe', String(loginForm.value.rememberMe))
      } else {
        localStorage.removeItem('username')
        localStorage.removeItem('password')
        localStorage.removeItem('rememberMe')
      }

      try {
        await userStore.login(loginForm.value)
        ElMessage.success('登录成功')
        const redirectUrl = redirect.value || '/'
        await router.push(redirectUrl)
        loading.value = false
      } catch (err: any) {
        loading.value = false
        if (captchaEnabled.value) {
          await getCode()
        }
      }
    }
  })
}

const geoStyle = (index: number) => {
  const shapes = ['circle', 'square', 'triangle']
  const shape = shapes[index % 3]
  const size = Math.random() * 50 + 30
  const left = Math.random() * 100
  const top = Math.random() * 100
  const delay = Math.random() * 5
  const duration = Math.random() * 10 + 15

  return {
    width: `${size}px`,
    height: `${size}px`,
    left: `${left}%`,
    top: `${top}%`,
    animationDelay: `${delay}s`,
    animationDuration: `${duration}s`,
    '--shape-type': shape
  }
}
</script>

<style scoped lang="scss">
.login-page {
  min-height: 100vh;
  position: relative;
  overflow: hidden;
}

// ===================================
// BACKGROUND
// ===================================
.login-bg {
  position: fixed;
  inset: 0;
  z-index: 0;
  overflow: hidden;
}

.gradient-bg {
  position: absolute;
  inset: 0;
  background: linear-gradient(160deg, #0B2545 0%, #1a3a5c 40%, #2d4a6c 100%);
}

.stripe-overlay {
  position: absolute;
  inset: 0;
  background: repeating-linear-gradient(
    65deg,
    transparent,
    transparent 40px,
    rgba(255, 107, 107, 0.03) 40px,
    rgba(255, 107, 107, 0.03) 80px
  );
}

.geometric-layer {
  position: absolute;
  inset: 0;
}

.geo-shape {
  position: absolute;
  opacity: 0.08;
  background: linear-gradient(135deg, #FF6B6B 0%, #FFA94D 100%);
  animation: float-geo 20s ease-in-out infinite;

  &[style*="--shape-type: circle"] {
    border-radius: 50%;
  }

  &[style*="--shape-type: square"] {
    border-radius: 4px;
  }

  &[style*="--shape-type: triangle"] {
    width: 0 !important;
    height: 0 !important;
    background: none;
    border-left: var(--width) solid transparent;
    border-right: var(--width) solid transparent;
    border-bottom: calc(var(--height) * 1.7) solid rgba(255, 107, 107, 0.08);
  }
}

@keyframes float-geo {
  0%, 100% {
    transform: translateY(0) rotate(0deg) scale(1);
  }
  33% {
    transform: translateY(-30px) rotate(10deg) scale(1.05);
  }
  66% {
    transform: translateY(15px) rotate(-5deg) scale(0.95);
  }
}

// ===================================
// TOP NAVIGATION
// ===================================
.top-nav {
  position: relative;
  z-index: 10;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
}

.nav-back {
  cursor: pointer;
}

.back-brutal {
  width: 44px;
  height: 44px;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(10px);
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  transition: all 0.2s;

  &:active {
    transform: scale(0.95);
    background: rgba(255, 255, 255, 0.25);
  }
}

.nav-title {
  font-family: var(--font-display);
  font-size: 22px;
  font-weight: 700;
  color: #fff;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
}

.nav-spacer {
  width: 44px;
}

// ===================================
// MAIN CONTENT
// ===================================
.login-content {
  position: relative;
  z-index: 10;
  padding: 0 20px 40px;
}

// ===================================
// WELCOME SECTION
// ===================================
.welcome-section {
  text-align: center;
  padding: 20px 0 32px;
}

.logo-container {
  position: relative;
  display: inline-block;
  margin-bottom: 20px;
}

.logo-ring {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  border-radius: 50%;
  border: 2px solid rgba(255, 255, 255, 0.15);

  &.ring-1 {
    width: 100px;
    height: 100px;
    animation: pulse-ring-1 3s ease-in-out infinite;
  }

  &.ring-2 {
    width: 130px;
    height: 130px;
    animation: pulse-ring-2 4s ease-in-out infinite reverse;
  }
}

@keyframes pulse-ring-1 {
  0%, 100% { transform: translate(-50%, -50%) scale(1); opacity: 0.3; }
  50% { transform: translate(-50%, -50%) scale(1.2); opacity: 0.1; }
}

@keyframes pulse-ring-2 {
  0%, 100% { transform: translate(-50%, -50%) scale(1); opacity: 0.15; }
  50% { transform: translate(-50%, -50%) scale(1.1); opacity: 0.05; }
}

.logo-icon {
  position: relative;
  z-index: 1;
  width: 70px;
  height: 70px;

  svg {
    width: 100%;
    height: 100%;
    filter: drop-shadow(0 4px 20px rgba(0, 0, 0, 0.3));
  }
}

.welcome-title {
  font-family: var(--font-display);
  font-size: 28px;
  font-weight: 900;
  color: #fff;
  margin: 0 0 8px;
  text-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
  letter-spacing: -0.5px;
}

.welcome-sub {
  font-family: var(--font-body);
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
  margin: 0;
}

// ===================================
// FORM CARD
// ===================================
.form-card {
  background: #fff;
  border: 3px solid var(--color-obsidian);
  border-radius: 20px;
  box-shadow: 8px 8px 0 var(--color-obsidian);
  padding: 24px 20px;
}

:deep(.el-form-item) {
  margin-bottom: 16px;
}

.brutal-input {
  .input-label {
    font-family: var(--font-body);
    font-size: 11px;
    font-weight: 700;
    color: var(--color-obsidian);
    margin-bottom: 6px;
    text-transform: uppercase;
    letter-spacing: 0.1em;
  }
}

.input-wrapper {
  display: flex;
  align-items: center;
  background: var(--color-cream);
  border: 2px solid var(--color-obsidian);
  border-radius: 10px;
  padding: 0 12px;
  height: 50px;
  transition: all 0.2s;

  &:focus-within {
    background: #fff;
    box-shadow: 4px 4px 0 var(--color-obsidian);
    transform: translate(-2px, -2px);
  }
}

.input-icon-box {
  color: var(--color-primary);
  margin-right: 10px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.custom-input {
  flex: 1;

  :deep(.el-input__wrapper) {
    background: transparent;
    box-shadow: none;
    padding: 0;
    min-height: 50px;
  }

  :deep(.el-input__inner) {
    color: var(--color-obsidian);
    font-size: 15px;
    font-weight: 500;
    padding: 12px 0;

    &::placeholder {
      color: var(--color-taupe);
      font-weight: 400;
    }
  }
}

// ===================================
// CAPTCHA
// ===================================
.captcha-wrapper {
  display: flex;
  gap: 10px;
}

.captcha-input {
  flex: 1;
}

.captcha-brutal {
  width: 110px;
  height: 50px;
  background: var(--color-cream);
  border: 2px solid var(--color-obsidian);
  border-radius: 10px;
  overflow: hidden;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  flex-shrink: 0;

  &:active {
    box-shadow: 3px 3px 0 var(--color-obsidian);
    transform: translate(-1px, -1px);
  }

  .captcha-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  span {
    font-size: 11px;
    font-weight: 600;
    color: var(--color-taupe);
  }
}

// ===================================
// FORM ACTIONS
// ===================================
.form-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.brutal-checkbox {
  :deep(.el-checkbox__label) {
    color: var(--color-charcoal);
    font-size: 12px;
  }

  :deep(.el-checkbox__inner) {
    border-radius: 4px;
    border-color: var(--color-taupe);
    border-width: 2px;

    &:hover {
      border-color: var(--color-primary);
    }
  }

  :deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
    background-color: var(--color-primary);
    border-color: var(--color-primary);
  }
}

.check-label {
  font-weight: 500;
}

.forgot-link {
  font-size: 12px;
  font-weight: 600;
  color: var(--color-primary);
  cursor: pointer;

  &:active {
    opacity: 0.7;
  }
}

// ===================================
// LOGIN BUTTON
// ===================================
.login-brutal-btn {
  width: 100%;
  height: 54px;
  background: linear-gradient(135deg, #FF6B6B 0%, #FFA94D 100%);
  border: 3px solid var(--color-obsidian);
  border-radius: 12px;
  box-shadow: 6px 6px 0 var(--color-obsidian);
  font-size: 16px;
  font-weight: 700;
  color: #fff;
  transition: all 0.15s ease-out;
  margin-top: 8px;

  &:active:not(:disabled) {
    box-shadow: none;
    transform: translate(4px, 4px);
  }

  &.is-loading {
    opacity: 0.8;
  }
}

.btn-content {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.btn-text {
  position: relative;
  z-index: 1;
}

.btn-deco {
  position: absolute;
  right: 16px;
  width: 20px;
  height: 20px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
}

// ===================================
// DIVIDER
// ===================================
.divider-line {
  position: relative;
  text-align: center;
  margin: 24px 0 20px;

  &::before {
    content: '';
    position: absolute;
    top: 50%;
    left: 0;
    right: 0;
    height: 2px;
    background: repeating-linear-gradient(
      90deg,
      var(--color-taupe) 0px,
      var(--color-taupe) 4px,
      transparent 4px,
      transparent 8px
    );
    opacity: 0.3;
  }

  span {
    position: relative;
    display: inline-block;
    padding: 0 12px;
    background: #fff;
    font-size: 12px;
    font-weight: 600;
    color: var(--color-taupe);
  }
}

// ===================================
// REGISTER LINK
// ===================================
.register-link {
  display: block;
  text-decoration: none;
}

.register-brutal {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 16px;
  background: var(--color-cream);
  border: 2px dashed var(--color-obsidian);
  border-radius: 12px;
  transition: all 0.2s;

  &:active {
    background: var(--color-sand);
  }
}

.register-text {
  font-family: var(--font-body);
  font-size: 14px;
  font-weight: 600;
  color: var(--color-primary);
}

.register-arrow {
  width: 32px;
  height: 32px;
  background: var(--color-primary);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

// ===================================
// BOTTOM DECORATION
// ===================================
.bottom-deco {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 150px;
  pointer-events: none;
  z-index: 1;
}

.deco-circle {
  position: absolute;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(255, 107, 107, 0.1) 0%, rgba(255, 169, 77, 0.1) 100%);
  filter: blur(30px);

  &.circle-1 {
    width: 200px;
    height: 200px;
    bottom: -50px;
    right: -50px;
  }
}

.deco-triangle {
  position: absolute;
  width: 0;
  height: 0;
  border-left: 40px solid transparent;
  border-right: 40px solid transparent;
  border-bottom: 70px solid rgba(32, 201, 151, 0.08);
  transform: rotate(-15deg);
  filter: blur(20px);

  &.triangle-1 {
    bottom: 20px;
    left: 10%;
  }
}
</style>
