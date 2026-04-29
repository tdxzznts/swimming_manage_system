<template>
  <div class="announcements-page">
    <!-- 几何背景 -->
    <div class="geometric-bg">
      <div class="gradient-bg"></div>
      <div class="diagonal-stripes"></div>
      <div class="floating-shape shape-1"></div>
      <div class="floating-shape shape-2"></div>
      <div class="floating-shape shape-3"></div>
    </div>

    <!-- 公告列表 -->
    <div class="announcements-container-brutal" v-loading="loading">
      <div class="announcement-list-brutal" v-if="announcements.length > 0">
        <div
          v-for="item in announcements"
          :key="item.id"
          class="announcement-card-brutal"
          @click="handleAnnouncementClick(item)"
        >
          <div class="announcement-accent-brutal"></div>
          <div class="announcement-header-brutal">
            <div class="announcement-tag-brutal">{{ item.announcementTypeName }}</div>
            <div class="announcement-time-brutal">{{ formatTime(item.publishTime) }}</div>
          </div>
          <div class="announcement-content-brutal">
            <h3 class="announcement-title-brutal">{{ item.title }}</h3>
            <div class="announcement-footer-brutal">
              <div class="view-count-brutal">
                <el-icon><View /></el-icon>
                <span>{{ item.viewCount || 0 }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-else class="empty-state-brutal">
        <div class="empty-icon-brutal">
          <el-icon :size="60"><Bell /></el-icon>
        </div>
        <p class="empty-text-brutal">暂无公告</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Bell, View } from '@element-plus/icons-vue'
import { listAllAnnouncements } from '@/api/announcement'
import type { Announcement } from '@/types/announcement'
import { ElMessage } from 'element-plus'

const router = useRouter()

// 公告列表
const announcements = ref<Announcement[]>([])
const loading = ref(false)

// 格式化时间
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

// 加载全部公告
const loadAnnouncements = async () => {
  try {
    loading.value = true
    const response = await listAllAnnouncements()
    announcements.value = response.data || []
  } catch (error) {
    console.error('加载公告失败:', error)
    ElMessage.error('加载公告失败')
  } finally {
    loading.value = false
  }
}

// 点击公告查看详情
const handleAnnouncementClick = (announcement: Announcement) => {
  router.push({
    name: 'AnnouncementDetail',
    params: { id: announcement.id }
  })
}

// 组件挂载时加载数据
onMounted(() => {
  loadAnnouncements()
})
</script>

<style scoped lang="scss">
.announcements-page {
  min-height: 100vh;
  position: relative;
  overflow: hidden;
}

/* 几何背景 */
.geometric-bg {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 0;
  overflow: hidden;
}

.gradient-bg {
  position: absolute;
  width: 100%;
  height: 100%;
  background: linear-gradient(180deg, var(--color-obsidian) 0%, #1a3a5c 50%, var(--color-obsidian) 100%);
}

.diagonal-stripes {
  position: absolute;
  width: 100%;
  height: 100%;
  background-image: repeating-linear-gradient(
    45deg,
    transparent,
    transparent 35px,
    rgba(255, 255, 255, 0.02) 35px,
    rgba(255, 255, 255, 0.02) 70px
  );
}

.floating-shape {
  position: absolute;
  border: 2px solid rgba(255, 255, 255, 0.08);
  animation: geometric-float 20s ease-in-out infinite;

  &.shape-1 {
    width: 100px;
    height: 100px;
    top: 10%;
    right: 10%;
    border-radius: 8px;
    transform: rotate(45deg);
    animation-delay: 0s;
  }

  &.shape-2 {
    width: 60px;
    height: 60px;
    bottom: 20%;
    left: 5%;
    border-radius: 50%;
    animation-delay: -5s;
  }

  &.shape-3 {
    width: 80px;
    height: 80px;
    top: 50%;
    left: 15%;
    border-radius: 12px;
    transform: rotate(30deg);
    animation-delay: -10s;
  }
}

@keyframes geometric-float {
  0%, 100% {
    transform: translateY(0) rotate(45deg);
  }
  50% {
    transform: translateY(-30px) rotate(50deg);
  }
}

/* 公告容器 - Brutalist */
.announcements-container-brutal {
  position: relative;
  z-index: 10;
  padding: 70px 16px 90px;
}

.announcement-list-brutal {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

/* 公告卡片 - Brutalist */
.announcement-card-brutal {
  position: relative;
  background: var(--color-cream);
  border: 3px solid var(--color-obsidian);
  border-radius: 14px;
  padding: 16px;
  box-shadow: 6px 6px 0 var(--color-obsidian);
  cursor: pointer;
  transition: all 0.2s;

  &:active {
    transform: translate(2px, 2px);
    box-shadow: 3px 3px 0 var(--color-obsidian);
  }
}

.announcement-accent-brutal {
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 4px;
  background: var(--color-primary-gradient);
  border-radius: 11px 0 0 11px;
}

.announcement-header-brutal {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.announcement-tag-brutal {
  padding: 6px 14px;
  border-radius: 10px;
  font-size: 11px;
  font-weight: 600;
  font-family: var(--font-body);
  background: rgba(255, 107, 107, 0.15);
  color: var(--color-primary);
  border: 2px solid var(--color-primary);
}

.announcement-time-brutal {
  font-size: 11px;
  font-family: var(--font-body);
  color: #9CA3AF;
}

.announcement-content-brutal {
  .announcement-title-brutal {
    font-family: var(--font-display);
    font-size: 16px;
    font-weight: 700;
    color: var(--color-obsidian);
    margin: 0 0 12px 0;
    line-height: 1.5;
  }
}

.announcement-footer-brutal {
  display: flex;
  align-items: center;
  justify-content: flex-end;
}

.view-count-brutal {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 11px;
  font-family: var(--font-body);
  color: #9CA3AF;

  span {
    font-weight: 600;
  }
}

/* 空状态 - Brutalist */
.empty-state-brutal {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  background: var(--color-cream);
  border: 3px solid var(--color-obsidian);
  border-radius: 14px;
  box-shadow: 6px 6px 0 var(--color-obsidian);
}

.empty-icon-brutal {
  color: var(--color-obsidian);
  margin-bottom: 16px;
}

.empty-text-brutal {
  font-family: var(--font-body);
  font-size: 14px;
  font-weight: 600;
  color: var(--color-obsidian);
  margin: 0;
}

/* Loading */
:deep(.el-loading-mask) {
  border-radius: 14px;
  background: rgba(253, 246, 227, 0.9);
}
</style>
