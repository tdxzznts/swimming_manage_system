<template>
  <div class="timeslots-page">
    <!-- Retro-Futuristic Background -->
    <div class="timeslots-bg">
      <!-- Animated geometric patterns -->
      <div class="geometric-layer layer-1">
        <div v-for="i in 8" :key="`geo-${i}`" class="geo-shape" :style="geoStyle(i)"></div>
      </div>

      <!-- Diagonal stripe overlay -->
      <div class="stripe-overlay"></div>

      <!-- Gradient background -->
      <div class="gradient-bg"></div>
    </div>

    <!-- Top Navigation -->
    <div class="top-nav">
      <div class="nav-back" @click="goBack">
        <div class="back-brutal">
          <el-icon :size="22"><ArrowLeft /></el-icon>
        </div>
      </div>
      <div class="nav-title">选择时段</div>
      <div class="nav-spacer"></div>
    </div>

    <!-- Main Content -->
    <div class="main-content">
      <!-- Lane Info Card -->
      <div class="lane-info-brutal">
        <div class="lane-header">
          <div class="lane-icon">
            <el-icon :size="24"><Guide /></el-icon>
          </div>
          <div class="lane-title">
            <h3>{{ laneNo }}</h3>
            <p>选择预约时段</p>
          </div>
        </div>
        <div class="lane-detail">
          <div class="detail-date">{{ selectedDate }}</div>
          <div class="detail-deco"></div>
        </div>
      </div>

      <!-- Time Slots Card -->
      <div class="timeslots-brutal">
        <div class="timeslots-header">
          <div class="header-icon">
            <el-icon :size="22"><Clock /></el-icon>
          </div>
          <h3 class="header-title">可用时段</h3>
        </div>

        <!-- Loading State -->
        <div v-if="loading" class="loading-state">
          <div class="loading-spinner"></div>
          <p class="loading-text">加载中...</p>
        </div>

        <!-- Empty State -->
        <div v-else-if="timeSlots.length === 0" class="empty-state">
          <div class="empty-icon">
            <el-icon :size="60"><Clock /></el-icon>
          </div>
          <p class="empty-text">该日期暂无可预约时段</p>
        </div>

        <!-- Time Slots List -->
        <div v-else class="timeslots-list">
          <div
            v-for="slot in timeSlots"
            :key="slot.id"
            class="timeslot-brutal"
            :class="{
              'peak-slot': slot.slotType === '1',
              'full-slot': slot.remainingCount === 0
            }"
            @click="slot.remainingCount > 0 && showConfirmDialog(slot)"
          >
            <!-- Left accent bar -->
            <div class="slot-accent" :class="`accent-${slot.slotType === '1' ? 'peak' : 'normal'}`"></div>

            <div class="slot-content">
              <!-- Time and type -->
              <div class="slot-time-row">
                <span class="time-range">{{ formatTime(slot.startTime) }} - {{ formatTime(slot.endTime) }}</span>
                <div class="slot-type-badge" :class="`type-${slot.slotType === '1' ? 'peak' : 'normal'}`">
                  {{ slot.slotTypeName }}
                </div>
              </div>

              <!-- Details -->
              <div class="slot-details">
                <div class="detail-item">
                  <el-icon :size="18"><Grid /></el-icon>
                  <span>{{ slot.laneNo }}</span>
                </div>
                <div class="detail-item" :class="{ 'low-remaining': slot.remainingCount <= 3 }">
                  <el-icon :size="18"><User /></el-icon>
                  <span>剩余: {{ slot.remainingCount }}/{{ slot.capacity }}</span>
                </div>
                <div class="detail-item price-item">
                  <el-icon :size="18"><Coin /></el-icon>
                  <span class="price-value">¥{{ slot.price }}</span>
                </div>
              </div>

              <!-- Full overlay -->
              <div v-if="slot.remainingCount === 0" class="full-overlay">
                <span>已约满</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Confirm Dialog -->
    <el-dialog
      v-model="confirmDialogVisible"
      title="确认预约"
      width="90%"
      :close-on-click-modal="false"
      class="confirm-brutal-dialog"
    >
      <div v-if="selectedTimeSlot" class="confirm-brutal-content">
        <div class="confirm-row">
          <span class="confirm-label">预约日期</span>
          <span class="confirm-value">{{ selectedDate }}</span>
        </div>
        <div class="confirm-row">
          <span class="confirm-label">时段</span>
          <span class="confirm-value">{{ formatTime(selectedTimeSlot.startTime) }} - {{ formatTime(selectedTimeSlot.endTime) }}</span>
        </div>
        <div class="confirm-row">
          <span class="confirm-label">泳道</span>
          <span class="confirm-value">{{ selectedTimeSlot.laneNo }} ({{ selectedTimeSlot.slotTypeName }})</span>
        </div>
        <div class="confirm-row highlight-row">
          <span class="confirm-label">金额</span>
          <span class="confirm-value price-highlight">¥{{ selectedTimeSlot.price }}</span>
        </div>
      </div>
      <template #footer>
        <el-button @click="confirmDialogVisible = false" size="large" class="cancel-btn">取消</el-button>
        <el-button type="primary" @click="handleConfirmReservation" :loading="confirming" size="large" class="confirm-btn">
          确认预约
        </el-button>
      </template>
    </el-dialog>

    <!-- Payment Dialog -->
    <PaymentDialog
      v-model="paymentDialogVisible"
      :order-id="paymentOrderId"
      @success="handlePaymentSuccess"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  ArrowLeft,
  Guide,
  Clock,
  Loading,
  Grid,
  User,
  Coin
} from '@element-plus/icons-vue'
import { listTimeSlots, createReservation } from '@/api/reservation'
import type { TimeSlot } from '@/types/reservation'
import PaymentDialog from '@/components/PaymentDialog.vue'

const router = useRouter()
const route = useRoute()

const selectedDate = ref('')
const laneId = ref<number>()
const laneNo = ref('')
const timeSlots = ref<TimeSlot[]>([])
const loading = ref(false)
const confirming = ref(false)
const confirmDialogVisible = ref(false)
const selectedTimeSlot = ref<TimeSlot | null>(null)
const paymentDialogVisible = ref(false)
const paymentOrderId = ref<number>()

onMounted(() => {
  selectedDate.value = (route.query.date as string) || getTodayDate()
  laneId.value = Number(route.query.laneId)
  laneNo.value = (route.query.laneNo as string) || ''

  if (laneId.value) {
    loadTimeSlots()
  } else {
    ElMessage.error('泳道信息错误')
    goBack()
  }
})

function getTodayDate(): string {
  const today = new Date()
  const year = today.getFullYear()
  const month = String(today.getMonth() + 1).padStart(2, '0')
  const day = String(today.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

function goBack() {
  router.back()
}

async function loadTimeSlots() {
  if (!selectedDate.value || !laneId.value) {
    console.warn('Missing required parameters:', { date: selectedDate.value, laneId: laneId.value })
    return
  }

  loading.value = true
  try {
    const response = await listTimeSlots(selectedDate.value, laneId.value)
    if (response.code === 200) {
      timeSlots.value = response.data || []
    } else {
      ElMessage.error(response.msg || '加载时段失败')
    }
  } catch (error) {
    console.error('加载时段失败:', error)
    ElMessage.error('加载时段失败')
  } finally {
    loading.value = false
  }
}

function showConfirmDialog(slot: TimeSlot) {
  selectedTimeSlot.value = slot
  confirmDialogVisible.value = true
}

async function handleConfirmReservation() {
  if (!selectedTimeSlot.value) return

  confirming.value = true
  try {
    const response = await createReservation({
      timeSlotId: selectedTimeSlot.value.id,
      reservationDate: selectedDate.value
    })

    if (response.code === 200) {
      confirmDialogVisible.value = false

      if (response.data?.orderId) {
        paymentOrderId.value = response.data.orderId
        paymentDialogVisible.value = true
      } else {
        ElMessage.error('订单信息异常，未获取到订单ID')
      }
    } else {
      ElMessage.error(response.msg || '预约失败')
    }
  } catch (error) {
    console.error('预约异常:', error)
    ElMessage.error('预约失败，请稍后重试')
  } finally {
    confirming.value = false
  }
}

function handlePaymentSuccess() {
  setTimeout(() => {
    router.back()
  }, 500)
}

function formatTime(time: string): string {
  if (!time) return ''
  return time.substring(0, 5)
}

const geoStyle = (index: number) => {
  const shapes = ['circle', 'square', 'triangle']
  const shape = shapes[index % 3]
  const size = Math.random() * 40 + 25
  const left = Math.random() * 100
  const top = Math.random() * 100
  const delay = Math.random() * 4
  const duration = Math.random() * 8 + 12

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
.timeslots-page {
  min-height: 100vh;
  position: relative;
  overflow: hidden;
}

// ===================================
// BACKGROUND
// ===================================
.timeslots-bg {
  position: fixed;
  inset: 0;
  z-index: 0;
  overflow: hidden;
}

.gradient-bg {
  position: absolute;
  inset: 0;
  background: linear-gradient(160deg, #0B2545 0%, #134074 40%, #1a5a9a 100%);
}

.stripe-overlay {
  position: absolute;
  inset: 0;
  background: repeating-linear-gradient(
    60deg,
    transparent,
    transparent 35px,
    rgba(255, 107, 107, 0.02) 35px,
    rgba(255, 107, 107, 0.02) 70px
  );
  animation: stripe-drift 20s linear infinite;
}

@keyframes stripe-drift {
  0% { background-position: 0 0; }
  100% { background-position: 200px 0; }
}

.geometric-layer {
  position: absolute;
  inset: 0;
}

.geo-shape {
  position: absolute;
  opacity: 0.06;
  background: linear-gradient(135deg, #FF6B6B 0%, #20C997 100%);
  animation: float-geo 18s ease-in-out infinite;

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
    border-bottom: calc(var(--height) * 1.7) solid rgba(32, 201, 151, 0.08);
  }
}

@keyframes float-geo {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-25px) rotate(8deg); }
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
.main-content {
  position: relative;
  z-index: 10;
  padding: 70px 16px 20px 16px;
}

// ===================================
// LANE INFO CARD
// ===================================
.lane-info-brutal {
  background: #fff;
  border: 3px solid var(--color-obsidian);
  border-radius: 16px;
  box-shadow: 6px 6px 0 var(--color-obsidian);
  padding: 20px;
  margin-bottom: 16px;
}

.lane-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.lane-icon {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #FFA94D 0%, #FF6B6B 100%);
  border: 2px solid var(--color-obsidian);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  box-shadow: 3px 3px 0 var(--color-obsidian);
}

.lane-title {
  h3 {
    font-family: var(--font-display);
    font-size: 22px;
    font-weight: 700;
    color: var(--color-obsidian);
    margin: 0 0 2px;
    letter-spacing: -0.3px;
  }

  p {
    font-family: var(--font-body);
    font-size: 13px;
    color: var(--color-taupe);
    margin: 0;
  }
}

.lane-detail {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 12px;
  background: var(--color-cream);
  border: 2px solid var(--color-obsidian);
  border-radius: 10px;
}

.detail-date {
  font-family: var(--font-body);
  font-size: 15px;
  font-weight: 600;
  color: var(--color-obsidian);
}

.detail-deco {
  position: absolute;
  inset: 0;
  background-image: radial-gradient(circle, var(--color-taupe) 1px, transparent 1px);
  background-size: 12px 12px;
  opacity: 0.2;
  pointer-events: none;
}

// ===================================
// TIME SLOTS CARD
// ===================================
.timeslots-brutal {
  background: #fff;
  border: 3px solid var(--color-obsidian);
  border-radius: 16px;
  box-shadow: 6px 6px 0 var(--color-obsidian);
  padding: 20px;
}

.timeslots-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.header-icon {
  width: 42px;
  height: 42px;
  background: linear-gradient(135deg, #20C997 0%, #12B886 100%);
  border: 2px solid var(--color-obsidian);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  box-shadow: 3px 3px 0 var(--color-obsidian);
}

.header-title {
  font-family: var(--font-display);
  font-size: 20px;
  font-weight: 700;
  color: var(--color-obsidian);
  margin: 0;
}

// ===================================
// LOADING & EMPTY STATES
// ===================================
.loading-state,
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  min-height: 250px;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid var(--color-cream);
  border-top-color: var(--color-primary);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.loading-text {
  margin-top: 16px;
  font-size: 14px;
  color: var(--color-taupe);
}

.empty-icon {
  color: var(--color-taupe);
  opacity: 0.3;
  margin-bottom: 12px;
}

.empty-text {
  font-size: 15px;
  color: var(--color-taupe);
  margin: 0;
}

// ===================================
// TIME SLOTS LIST
// ===================================
.timeslots-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.timeslot-brutal {
  position: relative;
  background: #fff;
  border: 2px solid var(--color-obsidian);
  border-radius: 12px;
  padding: 16px;
  box-shadow: 4px 4px 0 var(--color-obsidian);
  transition: all 0.15s ease-out;
  cursor: pointer;
  overflow: hidden;

  &:active {
    transform: translate(2px, 2px);
    box-shadow: 2px 2px 0 var(--color-obsidian);
  }

  &.peak-slot {
    border-color: #FF6B6B;
    box-shadow: 4px 4px 0 #FF6B6B;
  }

  &.full-slot {
    opacity: 0.5;
    cursor: not-allowed;
  }
}

.slot-accent {
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 4px;

  &.accent-normal {
    background: linear-gradient(180deg, #20C997 0%, #0CA678 100%);
  }

  &.accent-peak {
    background: linear-gradient(180deg, #FF6B6B 0%, #E55555 100%);
  }
}

.slot-content {
  position: relative;
  z-index: 1;
}

.slot-time-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.time-range {
  font-family: var(--font-display);
  font-size: 18px;
  font-weight: 700;
  color: var(--color-obsidian);
  letter-spacing: -0.3px;
}

.slot-type-badge {
  padding: 4px 10px;
  border-radius: 6px;
  font-family: var(--font-body);
  font-size: 10px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.05em;

  &.type-normal {
    background: rgba(32, 201, 151, 0.15);
    color: #0CA678;
  }

  &.type-peak {
    background: rgba(255, 107, 107, 0.15);
    color: #E55555;
  }
}

.slot-details {
  display: flex;
  justify-content: space-around;
  gap: 12px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: var(--color-charcoal);

  &.price-item {
    .price-value {
      font-weight: 700;
      font-size: 16px;
      color: #20C997;
    }
  }

  &.low-remaining {
    color: #FF6B6B;
    font-weight: 600;
  }
}

.full-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(11, 37, 69, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 10px;

  span {
    color: #fff;
    font-family: var(--font-body);
    font-size: 15px;
    font-weight: 600;
  }
}

// ===================================
// CONFIRM DIALOG
// ===================================
:deep(.confirm-brutal-dialog) {
  .el-dialog {
    border: 3px solid var(--color-obsidian);
    border-radius: 16px;
    box-shadow: 8px 8px 0 var(--color-obsidian);
  }

  .el-dialog__header {
    background: linear-gradient(135deg, #FFA94D 0%, #FF6B6B 100%);
    border-bottom: 3px solid var(--color-obsidian);
    border-radius: 13px 13px 0 0;
    padding: 16px 20px;

    .el-dialog__title {
      font-family: var(--font-display);
      font-size: 18px;
      font-weight: 700;
      color: #fff;
    }

    .el-dialog__headerbtn .el-dialog__close {
      color: #fff;
      font-size: 20px;
    }
  }

  .el-dialog__body {
    padding: 20px;
  }

  .el-dialog__footer {
    padding: 16px 20px;
    border-top: 2px solid var(--color-sand);
    display: flex;
    gap: 12px;

    .el-button {
      flex: 1;
      height: 48px;
      font-size: 15px;
      font-weight: 600;
      border-radius: 10px;
    }
  }
}

.confirm-brutal-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.confirm-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 16px;
  background: var(--color-cream);
  border: 2px solid var(--color-obsidian);
  border-radius: 10px;

  &.highlight-row {
    background: linear-gradient(135deg, rgba(32, 201, 151, 0.1) 0%, rgba(255, 169, 77, 0.1) 100%);
    border-color: #20C997;
  }
}

.confirm-label {
  font-family: var(--font-body);
  font-size: 14px;
  font-weight: 500;
  color: var(--color-taupe);
}

.confirm-value {
  font-family: var(--font-body);
  font-size: 15px;
  font-weight: 600;
  color: var(--color-obsidian);

  &.price-highlight {
    font-size: 20px;
    font-weight: 700;
    color: #20C997;
  }
}

.cancel-btn {
  border: 2px solid var(--color-obsidian);
  color: var(--color-obsidian);
  background: #fff;

  &:hover {
    background: var(--color-cream);
  }
}

.confirm-btn {
  background: linear-gradient(135deg, #20C997 0%, #12B886 100%);
  border: 2px solid var(--color-obsidian);
  box-shadow: 4px 4px 0 var(--color-obsidian);
  color: #fff;

  &:active {
    transform: translate(2px, 2px);
    box-shadow: none;
  }
}
</style>
