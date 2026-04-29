<template>
  <div class="records-page">
    <!-- 几何背景 -->
    <div class="geometric-bg">
      <div class="gradient-bg"></div>
      <div class="diagonal-stripes"></div>
      <div class="floating-shape shape-1"></div>
      <div class="floating-shape shape-2"></div>
      <div class="floating-shape shape-3"></div>
    </div>

    <!-- 主内容 -->
    <div class="records-content">
      <!-- 页面标题 -->
      <div class="page-header">
        <h1 class="page-title">消费记录</h1>
        <div class="decorative-line"></div>
      </div>

      <!-- 筛选标签 -->
      <div class="filter-tabs-brutal">
        <div
          class="tab-item-brutal"
          :class="{ active: selectedType === '' }"
          @click="handleFilterChange('')"
        >
          全部
        </div>
        <div
          class="tab-item-brutal"
          :class="{ active: selectedType === '1' }"
          @click="handleFilterChange('1')"
        >
          预约
        </div>
        <div
          class="tab-item-brutal"
          :class="{ active: selectedType === '2' }"
          @click="handleFilterChange('2')"
        >
          充值
        </div>
        <div
          class="tab-item-brutal"
          :class="{ active: selectedType === '3' }"
          @click="handleFilterChange('3')"
        >
          积分
        </div>
      </div>

      <!-- 订单列表 -->
      <div v-loading="loading" class="records-list">
        <div
          v-for="record in orderRecords"
          :key="record.id"
          class="record-card-brutal"
        >
          <div class="record-accent" :class="getRecordAccentClass(record.orderType)"></div>
          <div class="record-icon-brutal" :class="getRecordIconClass(record.orderType)">
            <el-icon :size="22">
              <component :is="record.iconName" />
            </el-icon>
          </div>
          <div class="record-content-brutal">
            <span class="record-title-brutal">{{ record.orderTypeName }}</span>
            <span class="record-time-brutal">{{ formatTime(record.createTime) }}</span>
            <span class="record-no-brutal">{{ record.orderNo }}</span>
          </div>
          <div class="record-amount-brutal">
            <span :class="record.amountClass">{{ record.amountText }}</span>
            <span class="record-status-brutal">{{ record.payStatusName }}</span>
          </div>
        </div>

        <div v-if="!loading && orderRecords.length === 0" class="empty-records-brutal">
          <div class="empty-icon-brutal">
            <el-icon :size="50"><Document /></el-icon>
          </div>
          <p>暂无记录</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Document,
  Tickets,
  Wallet,
  Star
} from '@element-plus/icons-vue'
import { getOrderList } from '@/api/order'
import type { OrderRecord } from '@/api/order'

const loading = ref(false)
const selectedType = ref('')
const orderRecords = ref<OrderRecord[]>([])

// 加载订单列表
async function loadOrderList() {
  loading.value = true
  try {
    const res = await getOrderList(selectedType.value)
    if (res.code === 200 && res.data) {
      orderRecords.value = res.data
    } else {
      ElMessage.error('加载订单记录失败')
    }
  } catch (error) {
    console.error('加载订单记录失败', error)
    ElMessage.error('加载订单记录失败')
  } finally {
    loading.value = false
  }
}

// 筛选类型变化
function handleFilterChange(type: string) {
  selectedType.value = type
  loadOrderList()
}

// 获取记录图标样式类
function getRecordIconClass(orderType: string): string {
  const classMap: Record<string, string> = {
    '1': 'icon-reservation',
    '2': 'icon-recharge',
    '3': 'icon-points'
  }
  return classMap[orderType] || 'icon-default'
}

// 获取记录强调条样式类
function getRecordAccentClass(orderType: string): string {
  const classMap: Record<string, string> = {
    '1': 'accent-reservation',
    '2': 'accent-recharge',
    '3': 'accent-points'
  }
  return classMap[orderType] || 'accent-default'
}

// 格式化时间
function formatTime(timeStr: string): string {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  const now = new Date()
  const diff = now.getTime() - date.getTime()

  // 小于一分钟
  if (diff < 60000) {
    return '刚刚'
  }

  // 小于一小时
  if (diff < 3600000) {
    const minutes = Math.floor(diff / 60000)
    return `${minutes}分钟前`
  }

  // 小于24小时
  if (diff < 86400000) {
    const hours = Math.floor(diff / 3600000)
    return `${hours}小时前`
  }

  // 小于7天
  if (diff < 604800000) {
    const days = Math.floor(diff / 86400000)
    return `${days}天前`
  }

  // 显示具体日期
  const month = date.getMonth() + 1
  const day = date.getDate()
  const hour = date.getHours().toString().padStart(2, '0')
  const minute = date.getMinutes().toString().padStart(2, '0')
  return `${month}-${day} ${hour}:${minute}`
}

onMounted(() => {
  loadOrderList()
})
</script>

<style scoped lang="scss">
.records-page {
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
.records-content {
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

/* 筛选标签 - Brutalist */
.filter-tabs-brutal {
  display: flex;
  background: var(--color-cream);
  border: 3px solid var(--color-obsidian);
  border-radius: 14px;
  padding: 6px;
  margin-bottom: 20px;
  box-shadow: 6px 6px 0 var(--color-obsidian);
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
  transition: all 0.2s;

  &:active {
    transform: translate(2px, 2px);
  }

  &.active {
    background: var(--color-primary-gradient);
    color: var(--color-cream);
    box-shadow: 3px 3px 0 rgba(11, 37, 69, 0.3);
  }
}

/* 记录列表 */
.records-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

/* 记录卡片 - Brutalist */
.record-card-brutal {
  position: relative;
  display: flex;
  align-items: center;
  padding: 16px;
  background: var(--color-cream);
  border: 3px solid var(--color-obsidian);
  border-radius: 14px;
  box-shadow: 6px 6px 0 var(--color-obsidian);
  transition: all 0.2s;

  &:active {
    transform: translate(2px, 2px);
    box-shadow: 3px 3px 0 var(--color-obsidian);
  }
}

.record-accent {
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 4px;
  border-radius: 11px 0 0 11px;

  &.accent-reservation {
    background: linear-gradient(180deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  }

  &.accent-recharge {
    background: linear-gradient(180deg, var(--color-secondary) 0%, #0CA678 100%);
  }

  &.accent-points {
    background: linear-gradient(180deg, var(--color-amber) 0%, #F59E0B 100%);
  }

  &.accent-default {
    background: linear-gradient(180deg, #6B7280 0%, #4B5563 100%);
  }
}

.record-icon-brutal {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  border: 2px solid var(--color-obsidian);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 14px;
  flex-shrink: 0;

  &.icon-reservation {
    background: linear-gradient(135deg, rgba(255, 107, 107, 0.15) 0%, rgba(255, 169, 77, 0.15) 100%);
    color: var(--color-primary);
  }

  &.icon-recharge {
    background: linear-gradient(135deg, rgba(32, 201, 151, 0.15) 0%, rgba(12, 166, 120, 0.15) 100%);
    color: var(--color-secondary);
  }

  &.icon-points {
    background: linear-gradient(135deg, rgba(255, 169, 77, 0.15) 0%, rgba(245, 158, 11, 0.15) 100%);
    color: var(--color-amber);
  }

  &.icon-default {
    background: linear-gradient(135deg, rgba(107, 114, 128, 0.15) 0%, rgba(75, 85, 99, 0.15) 100%);
    color: #6B7280;
  }
}

.record-content-brutal {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 0;
}

.record-title-brutal {
  font-size: 15px;
  font-weight: 600;
  font-family: var(--font-body);
  color: var(--color-obsidian);
}

.record-time-brutal {
  font-size: 12px;
  color: #6B7280;
  font-family: var(--font-body);
}

.record-no-brutal {
  font-size: 11px;
  color: #9CA3AF;
  font-family: 'Courier New', monospace;
}

.record-amount-brutal {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
  flex-shrink: 0;
}

.record-amount-brutal > span:first-child {
  font-size: 16px;
  font-weight: 700;
  font-family: var(--font-body);
}

.amount-increase {
  color: var(--color-secondary);
}

.amount-decrease {
  color: var(--color-primary);
}

.record-status-brutal {
  font-size: 11px;
  font-weight: 600;
  color: #6B7280;
  padding: 3px 10px;
  background: var(--color-sand);
  border: 1px solid var(--color-obsidian);
  border-radius: 6px;
}

/* 空状态 - Brutalist */
.empty-records-brutal {
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

.empty-records-brutal p {
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
