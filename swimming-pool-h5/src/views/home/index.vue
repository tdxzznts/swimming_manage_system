<template>
  <div class="home-page">
    <!-- Retro-Futuristic Header Section -->
    <div class="hero-section">
      <!-- Animated geometric background -->
      <div class="hero-bg">
        <div class="geometric-layer layer-1">
          <div v-for="i in 8" :key="`circle-${i}`" class="geo-circle" :style="circleStyle(i)"></div>
        </div>
        <div class="geometric-layer layer-2">
          <div v-for="i in 6" :key="`square-${i}`" class="geo-square" :style="squareStyle(i)"></div>
        </div>
        <div class="diagonal-stripe"></div>
      </div>

      <!-- Header content -->
      <div class="hero-content">
        <div class="greeting-block">
          <p class="greeting-sub">WELCOME BACK</p>
          <h1 class="greeting-title">智能游泳馆</h1>
        </div>
        <div class="status-indicator">
          <div class="status-dot"></div>
          <span>营业中</span>
        </div>
      </div>

      <!-- Decorative pool tile pattern -->
      <div class="tile-pattern"></div>
    </div>

    <!-- Main content area -->
    <div class="main-content">

      <!-- Carousel with brutalist design -->
      <div class="carousel-section" v-if="carouselItems.length > 0">
        <el-carousel
          height="160"
          :autoplay="true"
          :interval="4000"
          :loop="true"
          indicator-position="outside"
          arrow="never"
        >
          <el-carousel-item v-for="(item, index) in carouselItems" :key="item.id || index">
            <div
              class="carousel-card"
              :class="{ 'has-image': item.imageUrl }"
              :style="item.imageUrl ? { backgroundImage: `url(${item.imageUrl})` } : {}"
            >
              <div class="carousel-inner" v-if="!item.imageUrl">
                <div class="carousel-pattern"></div>
                <h2 class="carousel-title">{{ item.title || '畅游一夏' }}</h2>
                <div class="carousel-deco"></div>
              </div>
              <div class="carousel-overlay" v-if="item.imageUrl && item.title">
                <h3 class="overlay-title">{{ item.title }}</h3>
              </div>
            </div>
          </el-carousel-item>
        </el-carousel>
      </div>

      <!-- Quick Actions - Brutalist Grid -->
      <div class="actions-section">
        <!-- Primary action card -->
        <div class="action-primary" @click="handleReservation">
          <div class="action-brutal-border">
            <div class="action-content">
              <div class="action-icon">
                <div class="icon-shape">
                  <el-icon :size="32"><Calendar /></el-icon>
                </div>
              </div>
              <div class="action-text">
                <h3>快速预约</h3>
                <p>选择时段，即刻畅游</p>
              </div>
            </div>
            <div class="action-arrow">
              <el-icon :size="20"><ArrowRight /></el-icon>
            </div>
          </div>
        </div>

        <!-- Secondary actions grid -->
        <div class="actions-grid">
          <div class="action-secondary" @click="handleOrder">
            <div class="action-icon-small">
              <el-icon :size="26"><Ticket /></el-icon>
            </div>
            <span>我的订单</span>
            <div class="action-deco dot-1"></div>
            <div class="action-deco dot-2"></div>
          </div>
          <div class="action-secondary" @click="handleMember">
            <div class="action-icon-small">
              <el-icon :size="26"><CreditCard /></el-icon>
            </div>
            <span>会员卡</span>
            <div class="action-deco dot-1"></div>
            <div class="action-deco dot-2"></div>
          </div>
        </div>
      </div>

      <!-- Stats Cards - Geometric Design -->
      <div class="stats-section">
        <div class="stat-card">
          <div class="stat-icon-wrapper">
            <div class="stat-pattern">
              <svg viewBox="0 0 40 40" xmlns="http://www.w3.org/2000/svg">
                <circle cx="20" cy="20" r="18" fill="none" stroke="currentColor" stroke-width="2" stroke-dasharray="4 2"/>
              </svg>
            </div>
            <el-icon :size="22" class="stat-icon"><Clock /></el-icon>
          </div>
          <div class="stat-content">
            <p class="stat-label">今日剩余时段</p>
            <p class="stat-value">{{ homeStatistics.remainingSlots }} <span class="stat-unit">个</span></p>
          </div>
          <div class="stat-deco"></div>
        </div>

        <div class="stat-card">
          <div class="stat-icon-wrapper">
            <div class="stat-pattern">
              <svg viewBox="0 0 40 40" xmlns="http://www.w3.org/2000/svg">
                <rect x="8" y="8" width="24" height="24" fill="none" stroke="currentColor" stroke-width="2" transform="rotate(45 20 20)"/>
              </svg>
            </div>
            <el-icon :size="22" class="stat-icon"><User /></el-icon>
          </div>
          <div class="stat-content">
            <p class="stat-label">当前人数</p>
            <p class="stat-value">{{ homeStatistics.currentPeopleCount }} <span class="stat-unit">人</span></p>
          </div>
          <div class="stat-deco"></div>
        </div>
      </div>

      <!-- Announcements Section -->
      <div class="announcements-section" v-if="announcements.length > 0">
        <div class="section-header">
          <div class="header-left">
            <div class="header-icon">
              <el-icon :size="20"><Bell /></el-icon>
            </div>
            <h3>最新公告</h3>
          </div>
          <div class="header-link" @click="handleViewAllAnnouncements">
            <span>全部</span>
            <el-icon :size="16"><ArrowRight /></el-icon>
          </div>
        </div>

        <div class="announcements-list">
          <div
            v-for="(item, index) in announcements"
            :key="item.id"
            class="announcement-item"
            :class="{ 'item-first': index === 0 }"
            @click="handleAnnouncementClick(item)"
          >
            <div class="announcement-badge">
              <span class="badge-text">{{ item.announcementTypeName }}</span>
            </div>
            <div class="announcement-info">
              <p class="announcement-title">{{ item.title }}</p>
              <div class="announcement-meta">
                <span class="meta-time">{{ formatTime(item.publishTime) }}</span>
                <div class="meta-dot"></div>
              </div>
            </div>
            <div class="announcement-arrow">
              <el-icon :size="16"><ArrowRight /></el-icon>
            </div>
          </div>
        </div>
      </div>

    </div>

    <!-- Bottom spacing for tab bar -->
    <div class="bottom-spacer"></div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Calendar, ArrowRight, Ticket, CreditCard, Clock, User, Bell } from '@element-plus/icons-vue'
import { getHomeStatistics } from '@/api/reservation'
import { listLatestAnnouncements } from '@/api/announcement'
import { getActiveCarouselList } from '@/api/carousel'
import type { Carousel } from '@/types/carousel'
import type { HomeStatistics } from '@/types/reservation'
import type { Announcement } from '@/types/announcement'
import { ElMessage } from 'element-plus'

const router = useRouter()

// Home statistics
const homeStatistics = ref<HomeStatistics>({
  remainingSlots: 0,
  currentPeopleCount: 0
})

// Announcements list
const announcements = ref<Announcement[]>([])

// Carousel items
const carouselItems = ref<any[]>([])

// Format time helper
const formatTime = (time?: string) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)

  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`

  return date.toLocaleDateString('zh-CN')
}

// Load home statistics
const loadHomeStatistics = async () => {
  try {
    const response = await getHomeStatistics()
    homeStatistics.value = response.data || { remainingSlots: 0, currentPeopleCount: 0 }
  } catch (error) {
    console.error('加载首页统计失败:', error)
  }
}

// Load announcements
const loadAnnouncements = async () => {
  try {
    const response = await listLatestAnnouncements(3)
    announcements.value = response.data || []
  } catch (error) {
    console.error('加载公告失败:', error)
  }
}

// Load carousel items
const loadCarousels = async () => {
  try {
    const response = await getActiveCarouselList()
    if (response.code === 200) {
      const carousels = response.data || []
      carouselItems.value = carousels.map((item: Carousel) => ({
        ...item,
        gradient: 'linear-gradient(135deg, #FF6B6B 0%, #FFA94D 100%)'
      }))
    }
  } catch (error) {
    console.error('加载轮播图失败:', error)
    carouselItems.value = [{
      title: '畅游一夏',
      gradient: 'linear-gradient(135deg, #FF6B6B 0%, #FFA94D 100%)',
      imageUrl: ''
    }]
  }
}

// Event handlers
const handleReservation = () => router.push('/reservation')
const handleOrder = () => router.push('/order')
const handleMember = () => router.push('/membership')
const handleViewAllAnnouncements = () => router.push('/announcements')
const handleAnnouncementClick = (announcement: Announcement) => {
  router.push({
    name: 'AnnouncementDetail',
    params: { id: announcement.id }
  })
}

// Geometric styles
const circleStyle = (index: number) => {
  const size = Math.random() * 60 + 40
  const left = Math.random() * 100
  const top = Math.random() * 100
  const delay = Math.random() * 4
  return {
    width: `${size}px`,
    height: `${size}px`,
    left: `${left}%`,
    top: `${top}%`,
    animationDelay: `${delay}s`
  }
}

const squareStyle = (index: number) => {
  const size = Math.random() * 30 + 20
  const left = Math.random() * 100
  const top = Math.random() * 100
  const delay = Math.random() * 3
  const rotation = Math.random() * 45
  return {
    width: `${size}px`,
    height: `${size}px`,
    left: `${left}%`,
    top: `${top}%`,
    animationDelay: `${delay}s`,
    transform: `rotate(${rotation}deg)`
  }
}

// Lifecycle
onMounted(() => {
  loadHomeStatistics()
  loadAnnouncements()
  loadCarousels()
})
</script>

<style scoped lang="scss">
.home-page {
  min-height: 100vh;
  background: var(--color-cream);
  position: relative;
  overflow: hidden;
}

// ===================================
// HERO SECTION
// ===================================
.hero-section {
  position: relative;
  padding: 80px 20px 40px;
  background: linear-gradient(180deg, #0B2545 0%, #1a3a5c 50%, var(--color-cream) 100%);
  overflow: hidden;
}

.hero-bg {
  position: absolute;
  inset: 0;
  overflow: hidden;
}

.geometric-layer {
  position: absolute;
  inset: 0;
}

.geo-circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 107, 107, 0.06);
  animation: float-geometric 12s ease-in-out infinite;

  &::after {
    content: '';
    position: absolute;
    inset: 4px;
    border-radius: 50%;
    border: 1px solid rgba(255, 255, 255, 0.1);
  }
}

.geo-square {
  position: absolute;
  background: rgba(32, 201, 151, 0.05);
  animation: float-geometric 10s ease-in-out infinite reverse;

  &::after {
    content: '';
    position: absolute;
    inset: 4px;
    border: 1px solid rgba(255, 255, 255, 0.08);
  }
}

.diagonal-stripe {
  position: absolute;
  inset: 0;
  background: repeating-linear-gradient(
    60deg,
    transparent,
    transparent 30px,
    rgba(255, 255, 255, 0.02) 30px,
    rgba(255, 255, 255, 0.02) 60px
  );
}

@keyframes float-geometric {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  33% { transform: translateY(-15px) rotate(5deg); }
  66% { transform: translateY(8px) rotate(-3deg); }
}

.hero-content {
  position: relative;
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  z-index: 1;
}

.greeting-block {
  .greeting-sub {
    font-family: var(--font-body);
    font-size: 11px;
    font-weight: 600;
    color: rgba(255, 255, 255, 0.7);
    letter-spacing: 0.15em;
    margin: 0 0 8px;
  }

  .greeting-title {
    font-family: var(--font-display);
    font-size: 36px;
    font-weight: 900;
    color: #fff;
    margin: 0;
    letter-spacing: -1px;
    line-height: 1;
    text-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
  }
}

.status-indicator {
  display: flex;
  align-items: center;
  gap: 8px;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(10px);
  padding: 8px 14px;
  border-radius: 24px;
  border: 1px solid rgba(255, 255, 255, 0.2);

  span {
    font-family: var(--font-body);
    font-size: 12px;
    font-weight: 500;
    color: #fff;
  }
}

.status-dot {
  width: 8px;
  height: 8px;
  background: #20C997;
  border-radius: 50%;
  animation: pulse-status 2s ease-in-out infinite;
  box-shadow: 0 0 12px rgba(32, 201, 151, 0.6);
}

@keyframes pulse-status {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.6; transform: scale(1.1); }
}

.tile-pattern {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 60px;
  background-image:
    linear-gradient(45deg, rgba(255, 255, 255, 0.03) 25%, transparent 25%),
    linear-gradient(-45deg, rgba(255, 255, 255, 0.03) 25%, transparent 25%),
    linear-gradient(45deg, transparent 75%, rgba(255, 255, 255, 0.03) 75%),
    linear-gradient(-45deg, transparent 75%, rgba(255, 255, 255, 0.03) 75%);
  background-size: 24px 24px;
  background-position: 0 0, 0 12px, 12px -12px, -12px 0px;
}

// ===================================
// MAIN CONTENT
// ===================================
.main-content {
  position: relative;
  padding: 0 16px;
  z-index: 1;
}

// ===================================
// CAROUSEL SECTION
// ===================================
.carousel-section {
  margin-bottom: 20px;

  :deep(.el-carousel__indicator) {
    background: rgba(11, 37, 69, 0.2);

    &.is-active {
      background: var(--color-primary);
    }
  }
}

.carousel-card {
  height: 100%;
  border-radius: 16px;
  overflow: hidden;
  position: relative;
  background: linear-gradient(135deg, #FF6B6B 0%, #FFA94D 100%);
  box-shadow: 6px 6px 0 var(--color-obsidian);
  border: 3px solid var(--color-obsidian);

  &.has-image {
    background-size: cover;
    background-position: center;
  }
}

.carousel-inner {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  padding: 24px;
}

.carousel-pattern {
  position: absolute;
  inset: 0;
  opacity: 0.1;
  background-image:
    radial-gradient(circle at 20% 50%, #fff 2px, transparent 2px),
    radial-gradient(circle at 80% 50%, #fff 2px, transparent 2px);
  background-size: 30px 30px;
}

.carousel-title {
  font-family: var(--font-display);
  font-size: 28px;
  font-weight: 900;
  color: #fff;
  margin: 0;
  text-align: center;
  text-shadow: 2px 2px 0 rgba(0, 0, 0, 0.2);
  letter-spacing: -0.5px;
}

.carousel-deco {
  position: absolute;
  bottom: 12px;
  right: 12px;
  width: 40px;
  height: 40px;
  border: 3px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
}

.carousel-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(to top, rgba(11, 37, 69, 0.9), transparent);
  padding: 16px;
}

.overlay-title {
  font-family: var(--font-display);
  font-size: 18px;
  font-weight: 700;
  color: #fff;
  margin: 0;
}

// ===================================
// ACTIONS SECTION
// ===================================
.actions-section {
  margin-bottom: 20px;
}

.action-primary {
  margin-bottom: 12px;
  cursor: pointer;

  &:active .action-brutal-border {
    transform: translate(3px, 3px);
    box-shadow: none;
  }
}

.action-brutal-border {
  background: #fff;
  border: 3px solid var(--color-obsidian);
  border-radius: 12px;
  box-shadow: 5px 5px 0 var(--color-obsidian);
  padding: 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  transition: all 0.15s ease-out;
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 6px;
    height: 100%;
    background: linear-gradient(180deg, #FF6B6B 0%, #20C997 100%);
  }
}

.action-content {
  display: flex;
  align-items: center;
  gap: 16px;
  flex: 1;
}

.action-icon {
  position: relative;
}

.icon-shape {
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, #FF6B6B 0%, #FFA94D 100%);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.3);

  &::after {
    content: '';
    position: absolute;
    inset: -3px;
    border-radius: 16px;
    border: 2px dashed rgba(11, 37, 69, 0.15);
  }
}

.action-text {
  h3 {
    font-family: var(--font-display);
    font-size: 20px;
    font-weight: 700;
    color: var(--color-obsidian);
    margin: 0 0 4px;
    letter-spacing: -0.3px;
  }

  p {
    font-family: var(--font-body);
    font-size: 12px;
    color: var(--color-taupe);
    margin: 0;
  }
}

.action-arrow {
  width: 36px;
  height: 36px;
  background: var(--color-cream);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-obsidian);
  flex-shrink: 0;
}

.actions-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.action-secondary {
  position: relative;
  background: #fff;
  border: 2px solid var(--color-obsidian);
  border-radius: 12px;
  padding: 18px 16px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  box-shadow: 3px 3px 0 var(--color-obsidian);
  transition: all 0.15s ease-out;

  &:active {
    transform: translate(2px, 2px);
    box-shadow: none;
  }

  span {
    font-family: var(--font-body);
    font-size: 13px;
    font-weight: 600;
    color: var(--color-obsidian);
  }

  .action-deco {
    position: absolute;
    width: 6px;
    height: 6px;
    background: var(--color-secondary);
    border-radius: 50%;
    opacity: 0.4;

    &.dot-1 {
      top: 8px;
      left: 8px;
    }

    &.dot-2 {
      bottom: 8px;
      right: 8px;
    }
  }
}

.action-icon-small {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #20C997 0%, #12B886 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  box-shadow: 0 4px 10px rgba(32, 201, 151, 0.25);

  &::after {
    content: '';
    position: absolute;
    inset: -2px;
    border-radius: 14px;
    border: 1px solid rgba(32, 201, 151, 0.2);
  }
}

// ===================================
// STATS SECTION
// ===================================
.stats-section {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  margin-bottom: 20px;
}

.stat-card {
  position: relative;
  background: #fff;
  border: 2px solid var(--color-obsidian);
  border-radius: 14px;
  padding: 16px;
  display: flex;
  align-items: center;
  gap: 12px;
  box-shadow: 4px 4px 0 var(--color-obsidian);
  overflow: hidden;
}

.stat-icon-wrapper {
  position: relative;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-pattern {
  position: absolute;
  inset: 0;
  color: rgba(255, 107, 107, 0.1);
}

.stat-icon {
  position: relative;
  z-index: 1;
  color: var(--color-primary);
}

.stat-content {
  flex: 1;
}

.stat-label {
  font-family: var(--font-body);
  font-size: 10px;
  font-weight: 600;
  color: var(--color-taupe);
  margin: 0 0 4px;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.stat-value {
  font-family: var(--font-display);
  font-size: 24px;
  font-weight: 700;
  color: var(--color-obsidian);
  margin: 0;
  line-height: 1;

  .stat-unit {
    font-family: var(--font-body);
    font-size: 12px;
    font-weight: 500;
    color: var(--color-taupe);
    margin-left: 2px;
  }
}

.stat-deco {
  position: absolute;
  bottom: -10px;
  right: -10px;
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, rgba(32, 201, 151, 0.08) 0%, rgba(255, 169, 77, 0.08) 100%);
  border-radius: 50%;
}

// ===================================
// ANNOUNCEMENTS SECTION
// ===================================
.announcements-section {
  margin-bottom: 20px;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.header-icon {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #FFA94D 0%, #FF6B6B 100%);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  box-shadow: 0 4px 10px rgba(255, 169, 77, 0.25);
}

.section-header h3 {
  font-family: var(--font-display);
  font-size: 20px;
  font-weight: 700;
  color: var(--color-obsidian);
  margin: 0;
}

.header-link {
  display: flex;
  align-items: center;
  gap: 4px;
  font-family: var(--font-body);
  font-size: 12px;
  font-weight: 600;
  color: var(--color-primary);
  cursor: pointer;

  &:active {
    opacity: 0.7;
  }
}

.announcements-list {
  background: #fff;
  border: 2px solid var(--color-obsidian);
  border-radius: 14px;
  overflow: hidden;
  box-shadow: 4px 4px 0 var(--color-obsidian);
}

.announcement-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  border-bottom: 1px solid var(--color-sand);
  cursor: pointer;
  transition: background 0.2s;

  &:last-child {
    border-bottom: none;
  }

  &.item-first {
    background: linear-gradient(90deg, rgba(255, 107, 107, 0.05) 0%, transparent 100%);
  }

  &:active {
    background: var(--color-cream);
  }
}

.announcement-badge {
  flex-shrink: 0;
  width: 44px;
  height: 44px;
  background: linear-gradient(135deg, rgba(255, 107, 107, 0.15) 0%, rgba(255, 169, 77, 0.15) 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid rgba(255, 107, 107, 0.2);
}

.badge-text {
  font-family: var(--font-body);
  font-size: 9px;
  font-weight: 700;
  color: var(--color-primary-dark);
  text-align: center;
  line-height: 1.3;
  word-break: break-all;
}

.announcement-info {
  flex: 1;
  min-width: 0;
}

.announcement-title {
  font-family: var(--font-body);
  font-size: 14px;
  font-weight: 600;
  color: var(--color-obsidian);
  margin: 0 0 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.announcement-meta {
  display: flex;
  align-items: center;
  gap: 6px;
}

.meta-time {
  font-family: var(--font-body);
  font-size: 11px;
  color: var(--color-taupe);
}

.meta-dot {
  width: 3px;
  height: 3px;
  background: var(--color-taupe);
  border-radius: 50%;
  opacity: 0.4;
}

.announcement-arrow {
  width: 28px;
  height: 28px;
  background: var(--color-cream);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-obsidian);
  flex-shrink: 0;
}

// ===================================
// BOTTOM SPACER
// ===================================
.bottom-spacer {
  height: 80px;
}
</style>
