<template>
  <div class="profile-page">
    <!-- 复古未来主义背景 -->
    <div class="retro-bg">
      <div class="gradient-overlay"></div>
      <div class="geometric-shapes">
        <div class="geo-shape shape-1"></div>
        <div class="geo-shape shape-2"></div>
        <div class="geo-shape shape-3"></div>
        <div class="geo-shape shape-4"></div>
        <div class="geo-shape shape-5"></div>
        <div class="geo-shape shape-6"></div>
      </div>
      <div class="diagonal-lines">
        <div v-for="i in 8" :key="i" class="diag-line" :style="diagLineStyle(i)"></div>
      </div>
      <div class="floating-elements">
        <div class="float-element float-1"></div>
        <div class="float-element float-2"></div>
        <div class="float-element float-3"></div>
      </div>
    </div>

    <!-- 已登录状态 -->
    <div v-if="isLogin" class="profile-content">
      <!-- 用户信息卡片 - Brutalist风格 -->
      <div class="user-card brutalist-card">
        <div class="user-avatar-wrapper">
          <div class="avatar-ring brutalist-ring"></div>
          <div class="user-avatar brutalist-avatar">
            <img v-if="userInfo?.avatar" :src="userInfo.avatar" class="avatar-img" />
            <el-icon v-else :size="50"><User /></el-icon>
          </div>
          <div class="avatar-badge brutalist-badge">
            <el-icon><Medal /></el-icon>
          </div>
        </div>
        <h2 class="user-name">{{ userInfo?.nickName || userInfo?.nickname || userInfo?.username || '游泳爱好者' }}</h2>
        <p class="user-contact">{{ userInfo?.phonenumber || userInfo?.email || '暂无联系方式' }}</p>
        <div class="user-type">
          <span class="vip-tag brutalist-tag">VIP会员</span>
        </div>
      </div>

      <!-- 统计数据 - 粗边框风格 -->
      <div class="stats-card brutalist-stats">
        <div class="stat-item brutalist-stat">
          <div class="stat-icon brutalist-icon coral">
            <el-icon :size="24"><Calendar /></el-icon>
          </div>
          <div class="stat-content">
            <span class="stat-value">{{ userStats.reservationCount }}</span>
            <span class="stat-label">预约次数</span>
          </div>
        </div>
        <div class="stat-divider brutalist-divider"></div>
        <div class="stat-item brutalist-stat">
          <div class="stat-icon brutalist-icon teal">
            <el-icon :size="24"><Clock /></el-icon>
          </div>
          <div class="stat-content">
            <span class="stat-value">{{ userStats.totalHours }}h</span>
            <span class="stat-label">游泳时长</span>
          </div>
        </div>
        <div class="stat-divider brutalist-divider"></div>
        <div class="stat-item brutalist-stat">
          <div class="stat-icon brutalist-icon amber">
            <el-icon :size="24"><Coin /></el-icon>
          </div>
          <div class="stat-content">
            <span class="stat-value">¥{{ userStats.balance }}</span>
            <span class="stat-label">账户余额</span>
          </div>
        </div>
      </div>

      <!-- 功能菜单 - 粗边框分隔 -->
      <div class="menu-section">
        <div class="menu-card brutalist-menu">
          <div
            v-for="menu in mainMenus"
            :key="menu.key"
            class="menu-item brutalist-menu-item"
            @click="handleMenuClick(menu)"
          >
            <div class="menu-icon brutalist-menu-icon" :style="{ background: menu.bgColor }">
              <el-icon :size="20" :color="menu.iconColor">
                <component :is="menu.icon" />
              </el-icon>
            </div>
            <span class="menu-label">{{ menu.label }}</span>
            <div class="menu-right">
              <span v-if="menu.extra" class="menu-extra brutalist-extra">{{ menu.extra }}</span>
              <el-icon :size="16"><ArrowRight /></el-icon>
            </div>
          </div>
        </div>
      </div>

      <!-- 退出登录 - Brutalist风格按钮 -->
      <div class="logout-section">
        <button
          class="logout-btn brutalist-btn brutalist-btn-danger"
          @click="handleLogout"
        >
          <el-icon style="margin-right: 8px"><SwitchButton /></el-icon>
          退出登录
        </button>
      </div>
    </div>

    <!-- 未登录状态 - Brutalist风格 -->
    <div v-else class="guest-content">
      <div class="guest-card brutalist-card">
        <div class="guest-avatar brutalist-avatar-large">
          <el-icon :size="60"><User /></el-icon>
        </div>
        <h2 class="guest-title">欢迎来到智能游泳馆</h2>
        <p class="guest-subtitle">登录后享受更多服务</p>
        <div class="guest-actions">
          <button
            class="login-btn brutalist-btn brutalist-btn-primary"
            @click="goToLogin"
          >
            立即登录
          </button>
          <button
            class="register-btn brutalist-btn brutalist-btn-secondary"
            @click="goToRegister"
          >
            注册账号
          </button>
        </div>
      </div>

      <!-- 游客可访问的功能 -->
      <div class="guest-menu">
        <div class="menu-card brutalist-menu">
          <div
            v-for="menu in guestMenus"
            :key="menu.key"
            class="menu-item brutalist-menu-item"
            @click="handleMenuClick(menu)"
          >
            <div class="menu-icon brutalist-menu-icon" :style="{ background: menu.bgColor }">
              <el-icon :size="20" :color="menu.iconColor">
                <component :is="menu.icon" />
              </el-icon>
            </div>
            <span class="menu-label">{{ menu.label }}</span>
            <el-icon :size="16"><ArrowRight /></el-icon>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onActivated } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  User,
  Medal,
  Calendar,
  Clock,
  Coin,
  ArrowRight,
  Tickets,
  UserFilled,
  QuestionFilled,
  InfoFilled,
  SwitchButton,
  CreditCard,
  ChatDotSquare
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { getUserStatistics } from '@/api/user'
import type { UserInfo, UserStatistics } from '@/types/user'

const router = useRouter()
const userStore = useUserStore()

const isLogin = computed(() => userStore.isLogin)
const userInfo = computed(() => userStore.userInfo)

// 用户统计数据
const userStats = ref<UserStatistics>({
  reservationCount: 0,
  totalHours: 0,
  balance: 0,
  totalPoints: 0
})

// 主要菜单
const mainMenus = [
  {
    key: 'orders',
    label: '我的预约',
    icon: 'Tickets',
    bgColor: 'linear-gradient(135deg, rgba(255, 107, 107, 0.15) 0%, rgba(255, 107, 107, 0.08) 100%)',
    iconColor: '#FF6B6B',
    path: '/order'
  },
  {
    key: 'membership',
    label: '会员卡',
    icon: 'CreditCard',
    bgColor: 'linear-gradient(135deg, rgba(255, 169, 77, 0.15) 0%, rgba(255, 169, 77, 0.08) 100%)',
    iconColor: '#FFA94D',
    path: '/membership'
  },
  {
    key: 'feedback',
    label: '建议反馈',
    icon: 'ChatDotSquare',
    bgColor: 'linear-gradient(135deg, rgba(32, 201, 151, 0.15) 0%, rgba(32, 201, 151, 0.08) 100%)',
    iconColor: '#20C997',
    path: '/feedback'
  },
  {
    key: 'profile',
    label: '个人信息',
    icon: 'UserFilled',
    bgColor: 'linear-gradient(135deg, rgba(11, 37, 69, 0.15) 0%, rgba(11, 37, 69, 0.08) 100%)',
    iconColor: '#0B2545',
    action: 'editProfile'
  }
]

// 其他菜单
const otherMenus = [
  {
    key: 'help',
    label: '帮助中心',
    icon: 'QuestionFilled',
    bgColor: 'linear-gradient(135deg, rgba(32, 201, 151, 0.15) 0%, rgba(32, 201, 151, 0.08) 100%)',
    iconColor: '#20C997',
    action: 'help'
  },
  {
    key: 'about',
    label: '关于我们',
    icon: 'InfoFilled',
    bgColor: 'linear-gradient(135deg, rgba(11, 37, 69, 0.15) 0%, rgba(11, 37, 69, 0.08) 100%)',
    iconColor: '#0B2545',
    action: 'about'
  }
]

// 游客可访问的菜单
const guestMenus = [
  ...otherMenus
]

onMounted(() => {
  if (isLogin.value) {
    loadUserStats()
    refreshUserInfo()
  }
})

// 页面激活时也刷新用户信息（支持从其他页面返回时看到最新数据）
onActivated(() => {
  if (isLogin.value) {
    refreshUserInfo()
  }
})

// 刷新用户信息
async function refreshUserInfo() {
  try {
    await userStore.getInfo()
  } catch (error) {
    console.error('刷新用户信息失败:', error)
  }
}

async function loadUserStats() {
  try {
    const response = await getUserStatistics()
    userStats.value = response.data || {
      reservationCount: 0,
      totalHours: 0,
      balance: 0,
      totalPoints: 0
    }
  } catch (error) {
    console.error('加载用户统计失败:', error)
  }
}

function handleMenuClick(menu: any) {
  if (menu.path) {
    router.push(menu.path)
  } else if (menu.action) {
    handleAction(menu.action)
  }
}

function handleAction(action: string) {
  switch (action) {
    case 'editProfile':
      router.push('/profile-edit?tab=profile')
      break
    case 'help':
      ElMessage.info('帮助中心开发中...')
      break
    case 'about':
      ElMessage.info('关于我们：智能游泳馆H5 v1.0.0')
      break
    default:
      ElMessage.info('功能开发中...')
  }
}

async function handleLogout() {
  try {
    await ElMessageBox.confirm(
      '确定要退出登录吗？',
      '退出登录',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await userStore.logout()
    ElMessage.success('已退出登录')
  } catch (error) {
    // 用户取消
  }
}

function goToLogin() {
  router.push('/login')
}

function goToRegister() {
  router.push('/register')
}

function diagLineStyle(index: number) {
  return {
    top: `${(index - 1) * 12.5}%`,
    animationDelay: `${index * 0.2}s`
  }
}
</script>

<style scoped lang="scss">
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@700;900&family=Space+Grotesk:wght@400;500;600;700&display=swap');

.profile-page {
  min-height: 100vh;
  position: relative;
  overflow: hidden;
  font-family: 'Space Grotesk', sans-serif;
}

/* 复古未来主义背景 */
.retro-bg {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(180deg, #0B2545 0%, #134074 50%, #1A4D6F 100%);
  z-index: 0;
  overflow: hidden;
}

.gradient-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background:
    radial-gradient(circle at 20% 30%, rgba(255, 107, 107, 0.15) 0%, transparent 50%),
    radial-gradient(circle at 80% 70%, rgba(32, 201, 151, 0.15) 0%, transparent 50%),
    radial-gradient(circle at 50% 50%, rgba(255, 169, 77, 0.1) 0%, transparent 60%);
  animation: gradient-shift 8s ease-in-out infinite alternate;
}

@keyframes gradient-shift {
  0% { opacity: 0.8; }
  100% { opacity: 1; }
}

/* 几何浮动图形 */
.geometric-shapes {
  position: absolute;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.geo-shape {
  position: absolute;
  border: 3px solid rgba(255, 255, 255, 0.1);
  animation: float-rotate 20s ease-in-out infinite;

  &.shape-1 {
    width: 80px;
    height: 80px;
    top: 10%;
    left: 10%;
    transform: rotate(45deg);
    border-color: rgba(255, 107, 107, 0.2);
    animation-delay: 0s;
  }

  &.shape-2 {
    width: 60px;
    height: 60px;
    top: 60%;
    right: 15%;
    border-radius: 50%;
    border-color: rgba(32, 201, 151, 0.2);
    animation-delay: -5s;
  }

  &.shape-3 {
    width: 100px;
    height: 100px;
    bottom: 20%;
    left: 20%;
    clip-path: polygon(50% 0%, 0% 100%, 100% 100%);
    background: rgba(255, 169, 77, 0.1);
    border: none;
    animation-delay: -10s;
  }

  &.shape-4 {
    width: 50px;
    height: 50px;
    top: 30%;
    right: 30%;
    border-radius: 50%;
    border: 3px dashed rgba(255, 107, 107, 0.3);
    animation-delay: -3s;
  }

  &.shape-5 {
    width: 70px;
    height: 70px;
    bottom: 40%;
    right: 8%;
    transform: rotate(30deg);
    border-color: rgba(32, 201, 151, 0.15);
    animation-delay: -7s;
  }

  &.shape-6 {
    width: 40px;
    height: 40px;
    top: 15%;
    left: 60%;
    clip-path: polygon(50% 0%, 100% 50%, 50% 100%, 0% 50%);
    background: rgba(255, 255, 255, 0.05);
    border: none;
    animation-delay: -12s;
  }
}

@keyframes float-rotate {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  25% {
    transform: translateY(-20px) rotate(5deg);
  }
  50% {
    transform: translateY(-10px) rotate(-5deg);
  }
  75% {
    transform: translateY(-30px) rotate(3deg);
  }
}

/* 斜纹装饰 */
.diagonal-lines {
  position: absolute;
  width: 100%;
  height: 100%;
  overflow: hidden;
  opacity: 0.3;
}

.diag-line {
  position: absolute;
  left: -50%;
  width: 200%;
  height: 2px;
  background: linear-gradient(90deg, transparent 0%, rgba(255, 255, 255, 0.3) 50%, transparent 100%);
  transform: rotate(-45deg);
  animation: diag-slide 3s linear infinite;
}

@keyframes diag-slide {
  0% {
    transform: translateX(-100%) rotate(-45deg);
    opacity: 0;
  }
  50% {
    opacity: 1;
  }
  100% {
    transform: translateX(100%) rotate(-45deg);
    opacity: 0;
  }
}

/* 浮动元素 */
.floating-elements {
  position: absolute;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.float-element {
  position: absolute;
  border-radius: 50%;
  opacity: 0.1;
  animation: gentle-float 15s ease-in-out infinite;

  &.float-1 {
    width: 150px;
    height: 150px;
    top: 20%;
    left: 5%;
    background: radial-gradient(circle, rgba(255, 107, 107, 0.3) 0%, transparent 70%);
    animation-delay: 0s;
  }

  &.float-2 {
    width: 100px;
    height: 100px;
    bottom: 30%;
    right: 10%;
    background: radial-gradient(circle, rgba(32, 201, 151, 0.3) 0%, transparent 70%);
    animation-delay: -5s;
  }

  &.float-3 {
    width: 80px;
    height: 80px;
    top: 50%;
    left: 50%;
    background: radial-gradient(circle, rgba(255, 169, 77, 0.3) 0%, transparent 70%);
    animation-delay: -10s;
  }
}

@keyframes gentle-float {
  0%, 100% {
    transform: translate(0, 0);
  }
  33% {
    transform: translate(30px, -30px);
  }
  66% {
    transform: translate(-20px, 20px);
  }
}

/* Brutalist 卡片基础样式 */
.brutalist-card {
  background: #FFFFFF;
  border: 3px solid #0B2545;
  border-radius: 0;
  box-shadow: 6px 6px 0px #0B2545;
  position: relative;
  transition: all 0.2s ease;

  &:active {
    transform: translate(3px, 3px);
    box-shadow: 3px 3px 0px #0B2545;
  }
}

/* 已登录内容 */
.profile-content {
  position: relative;
  z-index: 10;
  padding: 70px 16px 20px 16px;
}

/* 用户信息卡片 */
.user-card {
  padding: 28px 24px;
  text-align: center;
  margin-bottom: 20px;
}

.user-avatar-wrapper {
  position: relative;
  display: inline-block;
  margin-bottom: 16px;
}

.brutalist-ring {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 120px;
  height: 120px;
  border-radius: 50%;
  border: 4px solid #0B2545;
  background: linear-gradient(135deg, rgba(255, 107, 107, 0.2) 0%, rgba(32, 201, 151, 0.2) 100%);
  animation: brutalist-pulse 2s infinite;
}

@keyframes brutalist-pulse {
  0%, 100% {
    transform: translate(-50%, -50%) scale(1);
  }
  50% {
    transform: translate(-50%, -50%) scale(1.05);
  }
}

.brutalist-avatar {
  position: relative;
  z-index: 1;
  width: 90px;
  height: 90px;
  border-radius: 50%;
  background: linear-gradient(135deg, #FF6B6B 0%, #0B2545 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  margin: 0 auto;
  border: 4px solid #FFFFFF;
  box-shadow: 4px 4px 0px #0B2545;
  overflow: hidden;
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.brutalist-badge {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #FFA94D 0%, #FF6B6B 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  border: 3px solid #0B2545;
  box-shadow: 3px 3px 0px #0B2545;
}

.user-name {
  font-family: 'Playfair Display', serif;
  font-size: 24px;
  font-weight: 900;
  color: #0B2545;
  margin: 0 0 8px;
  letter-spacing: 0.5px;
  text-transform: uppercase;
}

.user-contact {
  font-size: 14px;
  color: #6B7280;
  margin: 0 0 16px;
  font-weight: 500;
}

.user-type {
  display: flex;
  justify-content: center;
  gap: 8px;
}

.vip-tag {
  padding: 6px 16px;
  font-family: 'Space Grotesk', sans-serif;
  font-size: 12px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.brutalist-tag {
  background: linear-gradient(135deg, #FFA94D 0%, #FF6B6B 100%);
  color: #FFFFFF;
  border: 3px solid #0B2545;
  box-shadow: 4px 4px 0px #0B2545;
  border-radius: 0;
}

/* 统计数据卡片 */
.stats-card {
  display: flex;
  justify-content: space-around;
  align-items: center;
  padding: 20px;
  margin-bottom: 20px;
}

.brutalist-stats {
  background: #FFFFFF;
  border: 3px solid #0B2545;
  border-radius: 0;
  box-shadow: 6px 6px 0px #0B2545;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  flex: 1;
}

.brutalist-icon {
  width: 52px;
  height: 52px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 3px solid #0B2545;
  box-shadow: 4px 4px 0px #0B2545;
  border-radius: 0;

  &.coral {
    background: #FF6B6B;
    color: #FFFFFF;
  }

  &.teal {
    background: #20C997;
    color: #FFFFFF;
  }

  &.amber {
    background: #FFA94D;
    color: #FFFFFF;
  }
}

.stat-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.stat-value {
  font-family: 'Playfair Display', serif;
  font-size: 24px;
  font-weight: 900;
  color: #0B2545;
  letter-spacing: 1px;
}

.stat-label {
  font-size: 12px;
  font-weight: 600;
  color: #6B7280;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.brutalist-divider {
  width: 3px;
  height: 50px;
  background: #0B2545;
}

/* 功能菜单 */
.menu-section {
  margin-bottom: 20px;
}

.brutalist-menu {
  background: #FFFFFF;
  border: 3px solid #0B2545;
  border-radius: 0;
  box-shadow: 6px 6px 0px #0B2545;
  overflow: hidden;
}

.brutalist-menu-item {
  display: flex;
  align-items: center;
  padding: 18px 20px;
  cursor: pointer;
  transition: all 0.2s ease;
  border-bottom: 3px solid #0B2545;

  &:last-child {
    border-bottom: none;
  }

  &:hover {
    background: rgba(11, 37, 69, 0.05);
  }

  &:active {
    background: rgba(11, 37, 69, 0.1);
  }
}

.brutalist-menu-icon {
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  flex-shrink: 0;
  border: 3px solid #0B2545;
  box-shadow: 3px 3px 0px #0B2545;
  border-radius: 0;
}

.menu-label {
  flex: 1;
  font-size: 16px;
  font-weight: 600;
  color: #0B2545;
  letter-spacing: 0.5px;
}

.menu-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.brutalist-extra {
  font-size: 12px;
  font-weight: 700;
  color: #FFFFFF;
  background: #FF6B6B;
  padding: 4px 12px;
  border: 3px solid #0B2545;
  box-shadow: 3px 3px 0px #0B2545;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  border-radius: 0;
}

/* Brutalist 按钮 */
.brutalist-btn {
  font-family: 'Space Grotesk', sans-serif;
  font-size: 16px;
  font-weight: 700;
  padding: 16px 32px;
  border: 3px solid #0B2545;
  border-radius: 0;
  cursor: pointer;
  transition: all 0.2s ease;
  text-transform: uppercase;
  letter-spacing: 1px;
  box-shadow: 6px 6px 0px #0B2545;

  &:active {
    transform: translate(3px, 3px);
    box-shadow: 3px 3px 0px #0B2545;
  }
}

.brutalist-btn-danger {
  background: linear-gradient(135deg, #FF6B6B 0%, #FF8E8E 100%);
  color: #FFFFFF;
}

.brutalist-btn-primary {
  background: linear-gradient(135deg, #0B2545 0%, #134074 100%);
  color: #FFFFFF;
}

.brutalist-btn-secondary {
  background: #FFFFFF;
  color: #0B2545;
}

/* 退出登录 */
.logout-section {
  display: flex;
  justify-content: center;
}

.logout-btn {
  width: 100%;
  max-width: 320px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 未登录内容 */
.guest-content {
  position: relative;
  z-index: 10;
  padding: 40px 16px 20px;
}

.guest-card {
  padding: 48px 32px;
  text-align: center;
  margin-bottom: 24px;
}

.brutalist-avatar-large {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: linear-gradient(135deg, #FF6B6B 0%, #0B2545 100%);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: #FFFFFF;
  margin-bottom: 24px;
  border: 4px solid #FFFFFF;
  box-shadow: 6px 6px 0px #0B2545;
}

.guest-title {
  font-family: 'Playfair Display', serif;
  font-size: 28px;
  font-weight: 900;
  color: #0B2545;
  margin: 0 0 12px;
  letter-spacing: 1px;
  text-transform: uppercase;
}

.guest-subtitle {
  font-size: 16px;
  color: #6B7280;
  margin: 0 0 32px;
  font-weight: 500;
}

.guest-actions {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.login-btn,
.register-btn {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.guest-menu {
  margin-top: 20px;
}

/* 响应式调整 */
@media (max-width: 375px) {
  .user-name {
    font-size: 20px;
  }

  .guest-title {
    font-size: 24px;
  }

  .stat-value {
    font-size: 20px;
  }

  .brutalist-icon {
    width: 48px;
    height: 48px;
  }
}

/* 添加复古装饰角标 */
.brutalist-card::before,
.brutalist-card::after {
  content: '';
  position: absolute;
  width: 20px;
  height: 20px;
  border: 3px solid #0B2545;
  transition: all 0.3s ease;
}

.brutalist-card::before {
  top: -3px;
  left: -3px;
  border-right: none;
  border-bottom: none;
}

.brutalist-card::after {
  bottom: -3px;
  right: -3px;
  border-left: none;
  border-top: none;
}

/* 菜单项悬停效果 */
.brutalist-menu-item:hover .brutalist-menu-icon {
  transform: scale(1.05);
  box-shadow: 4px 4px 0px #0B2545;
}
</style>
