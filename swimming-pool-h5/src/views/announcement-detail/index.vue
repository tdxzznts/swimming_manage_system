<template>
  <div class="announcement-detail-page">
    <!-- 几何背景 -->
    <div class="geometric-bg">
      <div class="gradient-bg"></div>
      <div class="diagonal-stripes"></div>
      <div class="floating-shape shape-1"></div>
      <div class="floating-shape shape-2"></div>
      <div class="floating-shape shape-3"></div>
    </div>

    <!-- 主内容 -->
    <div class="detail-content-brutal" v-loading="loading">
      <!-- 公告头部 -->
      <div class="announcement-header-brutal">
        <div class="announcement-tag-brutal">{{ announcement?.announcementTypeName }}</div>
        <h1 class="announcement-title-brutal">{{ announcement?.title }}</h1>
        <div class="announcement-meta-brutal">
          <span class="meta-item-brutal">
            <el-icon><Clock /></el-icon>
            {{ formatTime(announcement?.publishTime) }}
          </span>
          <span class="meta-item-brutal">
            <el-icon><View /></el-icon>
            {{ announcement?.viewCount || 0 }} 次浏览
          </span>
        </div>
      </div>

      <!-- 公告内容 -->
      <div class="announcement-body-brutal">
        <div class="announcement-content-brutal" v-html="announcement?.content"></div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { Clock, View } from '@element-plus/icons-vue'
import { getAnnouncementDetail, incrementViewCount } from '@/api/announcement'
import type { AnnouncementDetail } from '@/types/announcement'
import { ElMessage } from 'element-plus'

const route = useRoute()

const loading = ref(false)
const announcement = ref<AnnouncementDetail | null>(null)

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

// 加载公告详情
const loadAnnouncementDetail = async () => {
  const announcementId = route.params.id as string
  if (!announcementId) {
    ElMessage.error('公告ID不能为空')
    return
  }

  loading.value = true
  try {
    const response = await getAnnouncementDetail(announcementId)
    announcement.value = response.data

    // 增加浏览次数
    try {
      await incrementViewCount(announcementId)
      // 更新显示的浏览次数
      if (announcement.value) {
        announcement.value.viewCount = (announcement.value.viewCount || 0) + 1
      }
    } catch (error) {
      console.error('增加浏览次数失败:', error)
      // 不影响主流程，静默失败
    }
  } catch (error) {
    console.error('加载公告详情失败:', error)
    ElMessage.error('加载公告详情失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadAnnouncementDetail()
})
</script>

<style scoped lang="scss">
.announcement-detail-page {
  min-height: 100vh;
  position: relative;
  overflow: hidden;
  padding-bottom: 40px;
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

/* 主内容 - Brutalist */
.detail-content-brutal {
  position: relative;
  z-index: 10;
  padding: 70px 16px 20px;
}

/* 公告头部 - Brutalist */
.announcement-header-brutal {
  background: var(--color-cream);
  border: 3px solid var(--color-obsidian);
  border-radius: 14px;
  padding: 20px;
  margin-bottom: 16px;
  box-shadow: 6px 6px 0 var(--color-obsidian);
}

.announcement-tag-brutal {
  display: inline-block;
  padding: 6px 14px;
  border-radius: 10px;
  font-size: 12px;
  font-weight: 600;
  font-family: var(--font-body);
  background: rgba(255, 107, 107, 0.15);
  color: var(--color-primary);
  border: 2px solid var(--color-primary);
  margin-bottom: 12px;
}

.announcement-title-brutal {
  font-family: var(--font-display);
  font-size: 22px;
  font-weight: 700;
  color: var(--color-obsidian);
  margin: 0 0 16px;
  line-height: 1.4;
}

.announcement-meta-brutal {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}

.meta-item-brutal {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  font-family: var(--font-body);
  color: #6B7280;

  .el-icon {
    font-size: 16px;
  }
}

/* 公告内容 - Brutalist */
.announcement-body-brutal {
  background: var(--color-cream);
  border: 3px solid var(--color-obsidian);
  border-radius: 14px;
  padding: 20px;
  box-shadow: 6px 6px 0 var(--color-obsidian);
}

.announcement-content-brutal {
  font-family: var(--font-body);
  font-size: 15px;
  line-height: 1.8;
  color: var(--color-obsidian);

  :deep(img) {
    max-width: 100%;
    height: auto;
    border-radius: 8px;
    margin: 12px 0;
    border: 2px solid var(--color-obsidian);
  }

  :deep(p) {
    margin: 12px 0;
  }

  :deep(h1),
  :deep(h2),
  :deep(h3) {
    font-family: var(--font-display);
    font-weight: 700;
    margin: 16px 0 12px;
    color: var(--color-obsidian);
  }

  :deep(ul),
  :deep(ol) {
    padding-left: 20px;
    margin: 12px 0;
  }

  :deep(li) {
    margin: 6px 0;
  }
}

/* Loading */
:deep(.el-loading-mask) {
  border-radius: 14px;
  background: rgba(253, 246, 227, 0.9);
}
</style>
