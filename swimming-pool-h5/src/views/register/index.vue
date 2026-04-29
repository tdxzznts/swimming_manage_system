<template>
  <div class="register-page">
    <!-- Retro-Futuristic Background -->
    <div class="register-bg">
      <!-- Animated geometric patterns -->
      <div class="geometric-layer layer-1">
        <div v-for="i in 10" :key="`geo-${i}`" class="geo-shape" :style="geoStyle(i)"></div>
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
      <div class="nav-title">注册</div>
      <div class="nav-spacer"></div>
    </div>

    <!-- Main Content -->
    <div class="register-content">

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
              <!-- Star shape -->
              <path d="M40 25 L43.5 35 L54 35 L45.5 42 L48.5 52 L40 46 L31.5 52 L34.5 42 L26 35 L36.5 35 Z"
                fill="rgba(255,255,255,0.8)"/>
            </svg>
          </div>
        </div>

        <h1 class="welcome-title">创建账号</h1>
        <p class="welcome-sub">加入智能游泳馆，开始健康新生活</p>
      </div>

      <!-- Register Form Card -->
      <div class="form-card">
        <el-form
          ref="registerFormRef"
          :model="registerForm"
          :rules="registerRules"
          @submit.prevent="handleRegister"
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
                  v-model="registerForm.username"
                  placeholder="请输入用户名"
                  size="large"
                  clearable
                  class="custom-input"
                />
              </div>
            </div>
          </el-form-item>

          <!-- Email Input -->
          <el-form-item prop="email">
            <div class="brutal-input">
              <div class="input-label">邮箱</div>
              <div class="input-wrapper">
                <div class="input-icon-box">
                  <el-icon :size="22"><Message /></el-icon>
                </div>
                <el-input
                  v-model="registerForm.email"
                  placeholder="请输入邮箱"
                  size="large"
                  clearable
                  class="custom-input"
                />
              </div>
            </div>
          </el-form-item>

          <!-- Verification Code Input -->
          <el-form-item prop="verifyCode">
            <div class="brutal-input">
              <div class="input-label">验证码</div>
              <div class="code-wrapper">
                <div class="input-wrapper code-input">
                  <div class="input-icon-box">
                    <el-icon :size="22"><Key /></el-icon>
                  </div>
                  <el-input
                    v-model="registerForm.verifyCode"
                    placeholder="请输入验证码"
                    size="large"
                    clearable
                    class="custom-input"
                    maxlength="6"
                  />
                </div>
                <el-button
                  size="large"
                  class="code-brutal-btn"
                  :disabled="codeCountdown > 0"
                  :loading="codeLoading"
                  @click="handleSendCode"
                >
                  {{ codeCountdown > 0 ? `${codeCountdown}s` : '获取验证码' }}
                </el-button>
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
                  v-model="registerForm.password"
                  type="password"
                  placeholder="请输入密码"
                  size="large"
                  show-password
                  class="custom-input"
                />
              </div>
              <!-- Password Strength Indicator -->
              <div class="password-strength" v-if="registerForm.password">
                <div class="strength-bar-brutal">
                  <div
                    class="strength-fill-brutal"
                    :class="`strength-${passwordStrength}`"
                    :style="{ width: strengthWidth }"
                  ></div>
                </div>
                <span class="strength-text-brutal">{{ strengthText }}</span>
              </div>
            </div>
          </el-form-item>

          <!-- Confirm Password Input -->
          <el-form-item prop="confirmPassword">
            <div class="brutal-input">
              <div class="input-label">确认密码</div>
              <div class="input-wrapper">
                <div class="input-icon-box">
                  <el-icon :size="22"><Lock /></el-icon>
                </div>
                <el-input
                  v-model="registerForm.confirmPassword"
                  type="password"
                  placeholder="请确认密码"
                  size="large"
                  show-password
                  class="custom-input"
                />
              </div>
            </div>
          </el-form-item>

          <!-- Terms Agreement -->
          <div class="terms-brutal">
            <el-checkbox v-model="agreeTerms" class="brutal-checkbox">
              <span class="check-label">
                我已阅读并同意
                <a href="#" class="terms-link" @click.prevent>《用户协议》</a>
                和
                <a href="#" class="terms-link" @click.prevent>《隐私政策》</a>
              </span>
            </el-checkbox>
          </div>

          <!-- Register Button -->
          <el-button
            type="primary"
            size="large"
            class="register-brutal-btn"
            :loading="loading"
            :disabled="!agreeTerms"
            @click.prevent="handleRegister"
          >
            <span v-if="!loading" class="btn-content">
              <span class="btn-text">立即注册</span>
              <div class="btn-deco"></div>
            </span>
            <span v-else>注册中...</span>
          </el-button>
        </el-form>

        <!-- Divider -->
        <div class="divider-line">
          <span>已有账号?</span>
        </div>

        <!-- Login Link -->
        <router-link to="/login" class="login-link">
          <div class="login-brutal">
            <span class="login-text">返回登录</span>
            <div class="login-arrow">
              <el-icon :size="20"><ArrowRight /></el-icon>
            </div>
          </div>
        </router-link>
      </div>

    </div>

    <!-- Bottom Decorative Elements -->
    <div class="bottom-deco">
      <div class="deco-circle circle-1"></div>
      <div class="deco-square square-1"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { User, Message, Lock, Key, ArrowLeft, ArrowRight } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { sendEmailCode } from '@/api/auth'

const router = useRouter()
const userStore = useUserStore()

const registerFormRef = ref<FormInstance>()
const loading = ref(false)
const codeLoading = ref(false)
const codeCountdown = ref(0)
const agreeTerms = ref(false)
let countdownTimer: number | null = null

const registerForm = reactive({
  username: '',
  email: '',
  verifyCode: '',
  password: '',
  confirmPassword: ''
})

// Password strength calculation
const passwordStrength = computed(() => {
  const password = registerForm.password
  if (!password) return 0

  let strength = 0
  if (password.length >= 6) strength++
  if (password.length >= 10) strength++
  if (/[a-z]/.test(password) && /[A-Z]/.test(password)) strength++
  if (/\d/.test(password)) strength++
  if (/[^a-zA-Z0-9]/.test(password)) strength++

  return Math.min(strength, 3)
})

const strengthWidth = computed(() => {
  const widths = ['0%', '33%', '66%', '100%']
  return widths[passwordStrength.value]
})

const strengthText = computed(() => {
  const texts = ['非常弱', '弱', '中等', '强']
  return texts[passwordStrength.value]
})

const validatePassword = (rule: any, value: any, callback: any) => {
  if (value === '') {
    callback(new Error('请输入密码'))
  } else if (value.length < 6) {
    callback(new Error('密码长度不能少于6位'))
  } else if (!/(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/.test(value)) {
    callback(new Error('密码必须包含大小写字母和数字'))
  } else {
    if (registerForm.confirmPassword !== '') {
      registerFormRef.value?.validateField('confirmPassword')
    }
    callback()
  }
}

const validateConfirmPassword = (rule: any, value: any, callback: any) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const registerRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 4, max: 20, message: '用户名长度为4-20位', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9]+$/, message: '用户名只能包含字母和数字', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  verifyCode: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 6, message: '验证码为6位数字', trigger: 'blur' }
  ],
  password: [
    { required: true, validator: validatePassword, trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const handleBack = () => {
  router.back()
}

const handleSendCode = async () => {
  if (!registerForm.email) {
    ElMessage.warning('请先输入邮箱')
    return
  }
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(registerForm.email)) {
    ElMessage.warning('请输入正确的邮箱格式')
    return
  }

  codeLoading.value = true
  try {
    const res = await sendEmailCode(registerForm.email)
    if (res.code === 200) {
      ElMessage.success('验证码已发送到您的邮箱')
      codeCountdown.value = 60
      countdownTimer = window.setInterval(() => {
        codeCountdown.value--
        if (codeCountdown.value <= 0) {
          if (countdownTimer) {
            clearInterval(countdownTimer)
            countdownTimer = null
          }
        }
      }, 1000)
    }
  } catch (error) {
    console.error('发送验证码错误:', error)
  } finally {
    codeLoading.value = false
  }
}

const handleRegister = async () => {
  if (!registerFormRef.value) return

  if (!agreeTerms.value) {
    ElMessage.warning('请阅读并同意用户协议和隐私政策')
    return
  }

  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const success = await userStore.register(registerForm)
        if (success) {
          ElMessage.success('注册成功，请登录')
          router.replace('/login')
        }
      } catch (error) {
        console.error('注册错误:', error)
      } finally {
        loading.value = false
      }
    }
  })
}

const geoStyle = (index: number) => {
  const shapes = ['circle', 'square', 'diamond']
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
.register-page {
  min-height: 100vh;
  position: relative;
  overflow: hidden;
}

// ===================================
// BACKGROUND
// ===================================
.register-bg {
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
    rgba(32, 201, 151, 0.03) 40px,
    rgba(32, 201, 151, 0.03) 80px
  );
}

.geometric-layer {
  position: absolute;
  inset: 0;
}

.geo-shape {
  position: absolute;
  opacity: 0.08;
  background: linear-gradient(135deg, #20C997 0%, #FFA94D 100%);
  animation: float-geo 20s ease-in-out infinite;

  &[style*="--shape-type: circle"] {
    border-radius: 50%;
  }

  &[style*="--shape-type: square"] {
    border-radius: 4px;
  }

  &[style*="--shape-type: diamond"] {
    width: 0 !important;
    height: 0 !important;
    background: none;
    border-left: var(--width) solid transparent;
    border-right: var(--width) solid transparent;
    border-bottom: calc(var(--height) * 1.7) solid rgba(32, 201, 151, 0.08);
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
.register-content {
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
  color: var(--color-secondary);
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
// VERIFICATION CODE
// ===================================
.code-wrapper {
  display: flex;
  gap: 10px;
}

.code-input {
  flex: 1;
}

.code-brutal-btn {
  width: 90px;
  height: 50px;
  background: var(--color-secondary);
  border: 2px solid var(--color-obsidian);
  border-radius: 10px;
  font-size: 11px;
  font-weight: 700;
  color: #fff;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  box-shadow: 3px 3px 0 var(--color-obsidian);
  transition: all 0.15s ease-out;
  flex-shrink: 0;

  &:active:not(:disabled):not(.is-loading) {
    box-shadow: none;
    transform: translate(2px, 2px);
  }

  &:disabled {
    opacity: 0.5;
    background: var(--color-taupe);
  }
}

// ===================================
// PASSWORD STRENGTH
// ===================================
.password-strength {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 8px;
  padding: 0 4px;
}

.strength-bar-brutal {
  flex: 1;
  height: 6px;
  background: var(--color-cream);
  border: 2px solid var(--color-obsidian);
  border-radius: 3px;
  overflow: hidden;
}

.strength-fill-brutal {
  height: 100%;
  transition: all 0.3s;

  &.strength-0 {
    background: var(--color-error);
    width: 0%;
  }
  &.strength-1 {
    background: var(--color-warning);
  }
  &.strength-2 {
    background: var(--color-secondary);
  }
  &.strength-3 {
    background: var(--color-primary);
  }
}

.strength-text-brutal {
  font-family: var(--font-body);
  font-size: 10px;
  font-weight: 700;
  color: var(--color-obsidian);
  text-transform: uppercase;
  letter-spacing: 0.05em;
  min-width: 36px;
}

// ===================================
// TERMS
// ===================================
.terms-brutal {
  margin-bottom: 20px;
  padding: 0 4px;
}

.brutal-checkbox {
  :deep(.el-checkbox__label) {
    color: var(--color-charcoal);
    font-size: 12px;
    line-height: 1.5;
  }

  :deep(.el-checkbox__inner) {
    border-radius: 4px;
    border-color: var(--color-taupe);
    border-width: 2px;

    &:hover {
      border-color: var(--color-secondary);
    }
  }

  :deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
    background-color: var(--color-secondary);
    border-color: var(--color-secondary);
  }
}

.check-label {
  font-weight: 500;
}

.terms-link {
  color: var(--color-secondary);
  text-decoration: none;
  font-weight: 600;

  &:active {
    opacity: 0.7;
  }
}

// ===================================
// REGISTER BUTTON
// ===================================
.register-brutal-btn {
  width: 100%;
  height: 54px;
  background: linear-gradient(135deg, #20C997 0%, #12B886 100%);
  border: 3px solid var(--color-obsidian);
  border-radius: 12px;
  box-shadow: 6px 6px 0 var(--color-obsidian);
  font-size: 16px;
  font-weight: 700;
  color: #fff;
  transition: all 0.15s ease-out;
  margin-top: 8px;

  &:active:not(:disabled):not(.is-loading) {
    box-shadow: none;
    transform: translate(4px, 4px);
  }

  &:disabled:not(.is-loading) {
    opacity: 0.5;
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
// LOGIN LINK
// ===================================
.login-link {
  display: block;
  text-decoration: none;
}

.login-brutal {
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

.login-text {
  font-family: var(--font-body);
  font-size: 14px;
  font-weight: 600;
  color: var(--color-secondary);
}

.login-arrow {
  width: 32px;
  height: 32px;
  background: var(--color-secondary);
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
  background: linear-gradient(135deg, rgba(32, 201, 151, 0.1) 0%, rgba(255, 169, 77, 0.1) 100%);
  filter: blur(30px);

  &.circle-1 {
    width: 200px;
    height: 200px;
    bottom: -50px;
    right: -50px;
  }
}

.deco-square {
  position: absolute;
  background: linear-gradient(135deg, rgba(255, 107, 107, 0.08) 0%, rgba(255, 169, 77, 0.08) 100%);
  filter: blur(25px);
  border-radius: 8px;

  &.square-1 {
    width: 100px;
    height: 100px;
    bottom: 30px;
    left: 5%;
    transform: rotate(15deg);
  }
}
</style>
