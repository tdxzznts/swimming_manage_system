<template>
  <div class="feedback-page">
    <!-- 几何背景 -->
    <div class="geometric-bg">
      <div class="gradient-bg"></div>
      <div class="diagonal-stripes"></div>
      <div class="floating-shape shape-1"></div>
      <div class="floating-shape shape-2"></div>
      <div class="floating-shape shape-3"></div>
    </div>

    <!-- 主内容 -->
    <div class="feedback-content">
      <!-- 页面标题 -->
      <div class="page-header">
        <h1 class="page-title">我的反馈</h1>
        <div class="decorative-line"></div>
      </div>

      <!-- 状态筛选标签 -->
      <div class="status-tabs-brutal">
        <div
          v-for="tab in statusTabs"
          :key="tab.value"
          class="tab-item-brutal"
          :class="{ active: activeTab === tab.value }"
          @click="handleTabChange(tab.value)"
        >
          {{ tab.label }}
          <div v-if="activeTab === tab.value" class="tab-indicator"></div>
        </div>
      </div>

      <!-- 反馈列表 -->
      <div v-if="!loading && feedbacks.length > 0" class="feedback-list">
        <div
          v-for="feedback in feedbacks"
          :key="feedback.id"
          class="feedback-card-brutal"
          @click="handleViewDetail(feedback)"
        >
          <div class="feedback-accent" :class="`type-${feedback.feedbackType}`"></div>
          <div class="feedback-header-brutal">
            <span class="feedback-number-brutal">{{ feedback.feedbackNo }}</span>
            <span class="status-badge-brutal" :class="`status-${feedback.status}`">
              {{ feedback.statusName }}
            </span>
          </div>

          <div class="feedback-type-badge-brutal" :class="`type-${feedback.feedbackType}`">
            <el-icon><ChatDotRound /></el-icon>
            <span>{{ feedback.feedbackTypeName }}</span>
          </div>

          <h3 class="feedback-title-brutal">{{ feedback.title }}</h3>
          <p class="feedback-preview-brutal">{{ feedback.content }}</p>

          <div class="feedback-footer-brutal">
            <div class="feedback-time-brutal">
              <el-icon><Clock /></el-icon>
              <span>{{ feedback.createTime }}</span>
            </div>
            <div class="feedback-action-brutal">
              <span>详情</span>
              <el-icon><ArrowRight /></el-icon>
            </div>
          </div>
        </div>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="loading-container-brutal">
        <el-icon :size="30" class="is-loading"><Loading /></el-icon>
        <p>加载中...</p>
      </div>

      <!-- 空状态 -->
      <div v-if="!loading && feedbacks.length === 0" class="empty-container-brutal">
        <div class="empty-icon-brutal">
          <el-icon :size="60"><ChatDotRound /></el-icon>
        </div>
        <p class="empty-title-brutal">暂无反馈</p>
        <p class="empty-subtext-brutal">有问题或建议？欢迎反馈~</p>
        <button class="empty-btn-brutal" @click="goToCreate">
          立即反馈
        </button>
      </div>

      <!-- 加载更多 -->
      <div v-if="!loading && hasMore && feedbacks.length > 0" class="load-more-brutal">
        <button
          class="load-more-btn-brutal"
          :disabled="loadingMore"
          @click="loadMore"
        >
          {{ loadingMore ? '加载中...' : '加载更多' }}
        </button>
      </div>

      <div v-if="!loading && !hasMore && feedbacks.length > 0" class="no-more-brutal">
        <span>没有更多了</span>
      </div>
    </div>

    <!-- 浮动按钮 -->
    <button class="fab-brutal" @click="goToCreate">
      <el-icon><Plus /></el-icon>
    </button>

    <!-- 反馈详情弹窗 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="反馈详情"
      :before-close="handleCloseDetail"
      width="85%"
      class="feedback-detail-dialog-brutal"
    >
      <div v-if="selectedFeedback" class="feedback-detail-brutal">
        <!-- 反馈信息 -->
        <div class="detail-section-brutal">
          <div class="detail-header-brutal">
            <span class="detail-number-brutal">{{ selectedFeedback.feedbackNo }}</span>
            <span class="status-badge-brutal" :class="`status-${selectedFeedback.status}`">
              {{ selectedFeedback.statusName }}
            </span>
          </div>

          <div class="detail-type-badge-brutal" :class="`type-${selectedFeedback.feedbackType}`">
            <el-icon><ChatDotRound /></el-icon>
            <span>{{ selectedFeedback.feedbackTypeName }}</span>
          </div>

          <h3 class="detail-title-brutal">{{ selectedFeedback.title }}</h3>
          <div class="detail-content-brutal">{{ selectedFeedback.content }}</div>

          <div class="detail-contact-brutal">
            <el-icon><Phone /></el-icon>
            <span>{{ selectedFeedback.contactInfo }}</span>
          </div>

          <div class="detail-time-brutal">
            <el-icon><Clock /></el-icon>
            <span>提交时间：{{ selectedFeedback.createTime }}</span>
          </div>
        </div>

        <!-- 回复信息 -->
        <div v-if="selectedFeedback.reply" class="detail-section-brutal reply-section-brutal">
          <h3 class="section-title-brutal">
            <el-icon><ChatDotRound /></el-icon>
            官方回复
          </h3>
          <div class="reply-content-brutal">{{ selectedFeedback.reply }}</div>
          <div class="reply-time-brutal">
            <el-icon><Clock /></el-icon>
            <span>{{ selectedFeedback.replyTime }}</span>
          </div>
        </div>

        <div v-else class="detail-section-brutal no-reply-section-brutal">
          <div class="no-reply-icon-brutal">
            <el-icon :size="40"><Clock /></el-icon>
          </div>
          <p class="no-reply-text-brutal">等待处理中...</p>
        </div>
      </div>

      <template #footer>
        <el-button class="dialog-close-btn-brutal" @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  ChatDotRound,
  Clock,
  ArrowRight,
  Loading,
  Plus,
  Phone
} from '@element-plus/icons-vue'
import { listMyFeedbacks } from '@/api/feedback'
import type { Feedback } from '@/types/feedback'

const router = useRouter()

// 状态筛选标签
const statusTabs = [
  { label: '全部', value: '' },
  { label: '待处理', value: '0' },
  { label: '处理中', value: '1' },
  { label: '已完成', value: '2' }
]

const activeTab = ref('')
const loading = ref(false)
const loadingMore = ref(false)
const hasMore = ref(true)
const pageNum = ref(1)
const pageSize = 10
const feedbacks = ref<Feedback[]>([])
const detailDialogVisible = ref(false)
const selectedFeedback = ref<Feedback | null>(null)

onMounted(() => {
  loadFeedbacks()
})

function handleTabChange(status: string) {
  activeTab.value = status
  pageNum.value = 1
  feedbacks.value = []
  hasMore.value = true
  loadFeedbacks()
}

async function loadFeedbacks() {
  loading.value = true
  try {
    const response = await listMyFeedbacks({
      status: activeTab.value || undefined,
      pageNum: pageNum.value,
      pageSize: pageSize
    })
    if (response.code === 200) {
      const rawData = response.data?.rows || []
      feedbacks.value = rawData.map(transformFeedbackData)
      hasMore.value = feedbacks.value.length >= pageSize
    }
  } catch (error) {
    console.error('加载反馈失败:', error)
    ElMessage.error('加载反馈失败')
  } finally {
    loading.value = false
  }
}

async function loadMore() {
  if (loadingMore.value || !hasMore.value) return

  loadingMore.value = true
  pageNum.value++
  try {
    const response = await listMyFeedbacks({
      status: activeTab.value || undefined,
      pageNum: pageNum.value,
      pageSize: pageSize
    })
    if (response.code === 200) {
      const rawData = response.data?.rows || []
      const newFeedbacks = rawData.map(transformFeedbackData)
      feedbacks.value.push(...newFeedbacks)
      hasMore.value = newFeedbacks.length >= pageSize
    }
  } catch (error) {
    console.error('加载更多失败:', error)
    ElMessage.error('加载更多失败')
  } finally {
    loadingMore.value = false
  }
}

function handleViewDetail(feedback: Feedback) {
  selectedFeedback.value = feedback
  detailDialogVisible.value = true
}

function handleCloseDetail() {
  detailDialogVisible.value = false
  selectedFeedback.value = null
}

// 转换后端数据为前端所需格式
function transformFeedbackData(item: any): Feedback {
  // 状态映射
  const statusMap: Record<string, string> = {
    '0': '待处理',
    '1': '处理中',
    '2': '已完成'
  }

  // 类型映射
  const typeMap: Record<string, string> = {
    '1': '建议',
    '2': '投诉'
  }

  return {
    id: item.id,
    feedbackNo: `FB${String(item.id).padStart(6, '0')}`,
    feedbackType: item.feedbackType,
    feedbackTypeName: typeMap[item.feedbackType] || '未知',
    title: item.title,
    content: item.content,
    contactInfo: item.contactInfo || '',
    status: item.status,
    statusName: statusMap[item.status] || '未知',
    reply: item.handleResult || null,
    replyTime: item.handleTime || null,
    createTime: item.createTime || '',
    updateTime: item.updateTime || ''
  }
}

function goToCreate() {
  router.push('/feedback/create')
}
</script>

<style scoped lang="scss">
.feedback-page {
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

/* 主内容 */
.feedback-content {
  position: relative;
  z-index: 10;
  padding: 70px 16px 90px;
}

/* 页面头部 */
.page-header {
  margin-bottom: 24px;
}

.page-title {
  font-family: var(--font-display);
  font-size: 32px;
  font-weight: 700;
  color: var(--color-cream);
  margin: 0 0 12px;
  letter-spacing: 1px;
}

.decorative-line {
  width: 80px;
  height: 4px;
  background: var(--color-primary-gradient);
  border-radius: 2px;
}

/* 状态筛选标签 - Brutalist */
.status-tabs-brutal {
  display: flex;
  background: var(--color-cream);
  border: 3px solid var(--color-obsidian);
  border-radius: 14px;
  padding: 6px;
  margin-bottom: 20px;
  box-shadow: 6px 6px 0 var(--color-obsidian);
  gap: 4px;
}

.tab-item-brutal {
  flex: 1;
  text-align: center;
  padding: 12px 8px;
  font-size: 14px;
  font-weight: 600;
  font-family: var(--font-body);
  color: var(--color-obsidian);
  cursor: pointer;
  border-radius: 10px;
  position: relative;
  transition: all 0.2s;

  &:active {
    transform: translate(2px, 2px);
  }

  &.active {
    background: var(--color-secondary);
    color: var(--color-cream);
    box-shadow: 3px 3px 0 rgba(11, 37, 69, 0.3);
  }
}

.tab-indicator {
  position: absolute;
  bottom: 6px;
  left: 50%;
  transform: translateX(-50%);
  width: 20px;
  height: 3px;
  background: var(--color-cream);
  border-radius: 2px;
}

/* 反馈列表 */
.feedback-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

/* 反馈卡片 - Brutalist */
.feedback-card-brutal {
  position: relative;
  background: var(--color-cream);
  border: 3px solid var(--color-obsidian);
  border-radius: 14px;
  padding: 16px;
  box-shadow: 6px 6px 0 var(--color-obsidian);
  transition: all 0.2s;
  cursor: pointer;

  &:active {
    transform: translate(2px, 2px);
    box-shadow: 3px 3px 0 var(--color-obsidian);
  }
}

.feedback-accent {
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 4px;
  border-radius: 11px 0 0 11px;

  &.type-1 {
    background: linear-gradient(180deg, var(--color-secondary) 0%, #0CA678 100%);
  }

  &.type-2 {
    background: linear-gradient(180deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  }
}

.feedback-header-brutal {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.feedback-number-brutal {
  font-size: 12px;
  font-weight: 600;
  font-family: 'Courier New', monospace;
  color: #9CA3AF;
}

.status-badge-brutal {
  padding: 4px 12px;
  font-size: 11px;
  font-weight: 600;
  border: 2px solid;
  border-radius: 8px;

  &.status-0 {
    color: var(--color-amber);
    border-color: var(--color-amber);
    background: rgba(255, 169, 77, 0.1);
  }

  &.status-1 {
    color: var(--color-primary);
    border-color: var(--color-primary);
    background: rgba(255, 107, 107, 0.1);
  }

  &.status-2 {
    color: var(--color-secondary);
    border-color: var(--color-secondary);
    background: rgba(32, 201, 151, 0.1);
  }
}

.feedback-type-badge-brutal {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  border-radius: 10px;
  font-size: 12px;
  font-weight: 600;
  margin-bottom: 12px;
  border: 2px solid;

  &.type-1 {
    background: rgba(32, 201, 151, 0.1);
    color: var(--color-secondary);
    border-color: var(--color-secondary);
  }

  &.type-2 {
    background: rgba(255, 107, 107, 0.1);
    color: var(--color-primary);
    border-color: var(--color-primary);
  }
}

.feedback-title-brutal {
  font-family: var(--font-display);
  font-size: 17px;
  font-weight: 700;
  color: var(--color-obsidian);
  margin: 0 0 8px;
  line-height: 1.4;
}

.feedback-preview-brutal {
  font-family: var(--font-body);
  font-size: 14px;
  color: #6B7280;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin: 0 0 12px;
}

.feedback-footer-brutal {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 2px solid var(--color-sand);
}

.feedback-time-brutal {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #9CA3AF;
  font-family: var(--font-body);
}

.feedback-action-brutal {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  font-weight: 600;
  color: var(--color-primary);
  font-family: var(--font-body);
}

/* 加载状态 - Brutalist */
.loading-container-brutal {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  background: var(--color-cream);
  border: 3px solid var(--color-obsidian);
  border-radius: 14px;
  box-shadow: 6px 6px 0 var(--color-obsidian);
  color: var(--color-obsidian);

  p {
    margin-top: 12px;
    font-family: var(--font-body);
    font-size: 14px;
    font-weight: 600;
  }
}

/* 空状态 - Brutalist */
.empty-container-brutal {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  background: var(--color-cream);
  border: 3px solid var(--color-obsidian);
  border-radius: 14px;
  box-shadow: 6px 6px 0 var(--color-obsidian);
  text-align: center;
}

.empty-icon-brutal {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  border: 3px solid var(--color-obsidian);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-obsidian);
  margin-bottom: 16px;
}

.empty-title-brutal {
  font-family: var(--font-display);
  font-size: 18px;
  font-weight: 700;
  color: var(--color-obsidian);
  margin: 0 0 8px;
}

.empty-subtext-brutal {
  font-family: var(--font-body);
  font-size: 13px;
  color: #6B7280;
  margin: 0 0 24px;
}

.empty-btn-brutal {
  padding: 12px 28px;
  font-family: var(--font-body);
  font-size: 14px;
  font-weight: 600;
  color: var(--color-cream);
  background: var(--color-primary-gradient);
  border: 3px solid var(--color-obsidian);
  border-radius: 12px;
  box-shadow: 4px 4px 0 var(--color-obsidian);
  cursor: pointer;
  transition: all 0.2s;

  &:active {
    transform: translate(2px, 2px);
    box-shadow: 2px 2px 0 var(--color-obsidian);
  }
}

/* 加载更多 - Brutalist */
.load-more-brutal {
  display: flex;
  justify-content: center;
  padding: 16px;
}

.load-more-btn-brutal {
  padding: 10px 24px;
  font-family: var(--font-body);
  font-size: 14px;
  font-weight: 600;
  color: var(--color-obsidian);
  background: var(--color-cream);
  border: 3px solid var(--color-obsidian);
  border-radius: 10px;
  box-shadow: 4px 4px 0 var(--color-obsidian);
  cursor: pointer;
  transition: all 0.2s;

  &:active:not(:disabled) {
    transform: translate(2px, 2px);
    box-shadow: 2px 2px 0 var(--color-obsidian);
  }

  &:disabled {
    opacity: 0.6;
  }
}

.no-more-brutal {
  display: flex;
  justify-content: center;
  padding: 16px;
  color: var(--color-cream);
  font-family: var(--font-body);
  font-size: 13px;
  opacity: 0.7;
}

/* 浮动按钮 - Brutalist */
.fab-brutal {
  position: fixed;
  bottom: 90px;
  right: 20px;
  z-index: 9999;
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: var(--color-primary-gradient);
  border: 3px solid var(--color-obsidian);
  box-shadow: 6px 6px 0 var(--color-obsidian);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-cream);
  font-size: 24px;
  cursor: pointer;
  transition: all 0.2s;

  &:active {
    transform: translate(2px, 2px);
    box-shadow: 3px 3px 0 var(--color-obsidian);
  }

  .el-icon {
    font-size: 24px;
  }
}

/* 反馈详情弹窗 - Brutalist */
:deep(.feedback-detail-dialog-brutal) {
  .el-dialog {
    border: 3px solid var(--color-obsidian);
    border-radius: 16px;
    box-shadow: 8px 8px 0 var(--color-obsidian);
    overflow: hidden;
  }

  .el-dialog__header {
    background: var(--color-primary-gradient);
    padding: 20px;
    border-bottom: 3px solid var(--color-obsidian);

    .el-dialog__title {
      color: var(--color-cream);
      font-size: 18px;
      font-weight: 700;
      font-family: var(--font-display);
    }

    .el-dialog__headerbtn .el-dialog__close {
      color: var(--color-cream);
      font-size: 20px;
    }
  }

  .el-dialog__body {
    padding: 20px;
    max-height: 60vh;
    overflow-y: auto;
    background: var(--color-cream);
  }

  .el-dialog__footer {
    padding: 16px 20px;
    border-top: 3px solid var(--color-obsidian);
    background: var(--color-sand);
  }
}

.feedback-detail-brutal {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.detail-section-brutal {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.detail-header-brutal {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 12px;
  border-bottom: 3px solid var(--color-sand);
}

.detail-number-brutal {
  font-size: 13px;
  font-weight: 600;
  font-family: 'Courier New', monospace;
  color: #9CA3AF;
}

.detail-type-badge-brutal {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  border-radius: 10px;
  font-size: 12px;
  font-weight: 600;
  border: 2px solid;

  &.type-1 {
    background: rgba(32, 201, 151, 0.1);
    color: var(--color-secondary);
    border-color: var(--color-secondary);
  }

  &.type-2 {
    background: rgba(255, 107, 107, 0.1);
    color: var(--color-primary);
    border-color: var(--color-primary);
  }
}

.detail-title-brutal {
  font-family: var(--font-display);
  font-size: 20px;
  font-weight: 700;
  color: var(--color-obsidian);
  margin: 0;
}

.detail-content-brutal {
  font-family: var(--font-body);
  font-size: 15px;
  color: var(--color-obsidian);
  line-height: 1.8;
  padding: 16px;
  background: var(--color-sand);
  border-radius: 12px;
  border-left: 4px solid var(--color-primary);
}

.detail-contact-brutal {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  font-family: var(--font-body);
  color: #6B7280;
}

.detail-time-brutal {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  font-family: var(--font-body);
  color: #9CA3AF;
}

/* 回复区域 */
.reply-section-brutal {
  padding: 16px;
  background: rgba(32, 201, 151, 0.08);
  border-radius: 12px;
  border: 3px solid var(--color-secondary);
}

.section-title-brutal {
  display: flex;
  align-items: center;
  gap: 8px;
  font-family: var(--font-display);
  font-size: 16px;
  font-weight: 700;
  color: var(--color-secondary);
  margin: 0 0 12px;
}

.reply-content-brutal {
  font-family: var(--font-body);
  font-size: 14px;
  color: var(--color-obsidian);
  line-height: 1.8;
  padding: 12px;
  background: var(--color-cream);
  border-radius: 8px;
}

.reply-time-brutal {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  font-family: var(--font-body);
  color: var(--color-secondary);
  margin-top: 8px;
}

/* 无回复区域 */
.no-reply-section-brutal {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 32px 20px;
  background: var(--color-sand);
  border-radius: 12px;
  border: 3px solid #CBD5E1;
}

.no-reply-icon-brutal {
  color: #9CA3AF;
  margin-bottom: 12px;
}

.no-reply-text-brutal {
  font-family: var(--font-body);
  font-size: 14px;
  color: #6B7280;
  margin: 0;
}

.dialog-close-btn-brutal {
  width: 100%;
  padding: 12px;
  font-family: var(--font-body);
  font-size: 14px;
  font-weight: 600;
  color: var(--color-obsidian);
  background: var(--color-cream);
  border: 3px solid var(--color-obsidian);
  border-radius: 10px;
  box-shadow: 4px 4px 0 var(--color-obsidian);

  &:active {
    transform: translate(2px, 2px);
    box-shadow: 2px 2px 0 var(--color-obsidian);
  }
}
</style>
