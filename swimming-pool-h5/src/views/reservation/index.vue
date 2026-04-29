<template>
  <div class="reservation-page">
    <!-- 几何浮动背景 -->
    <div class="geometric-bg">
      <div class="diagonal-lines"></div>
      <div class="floating-shapes">
        <div class="shape shape-1"></div>
        <div class="shape shape-2"></div>
        <div class="shape shape-3"></div>
        <div class="shape shape-4"></div>
        <div class="shape shape-5"></div>
        <div class="shape shape-6"></div>
      </div>
    </div>

    <!-- 顶部标题区 -->
    <div class="top-nav">
      <div class="title-decoration">
        <div class="deco-line deco-line-1"></div>
        <div class="deco-circle deco-circle-1"></div>
        <div class="deco-circle deco-circle-2"></div>
        <div class="deco-line deco-line-2"></div>
      </div>
      <h1 class="page-title">预约泳道</h1>
    </div>

    <!-- 主内容 -->
    <div class="main-content">
      <!-- 日期选择器卡片 -->
      <div class="section-card date-card">
        <div class="card-header">
          <div class="header-icon">
            <el-icon :size="20"><Calendar /></el-icon>
          </div>
          <h3 class="card-title">选择预约日期</h3>
        </div>
        <div v-if="availableDates.length > 0" class="date-list">
          <div
            v-for="dateInfo in availableDates"
            :key="dateInfo.date"
            class="date-item"
            :class="{ active: selectedDate === dateInfo.date, today: dateInfo.isToday }"
            @click="selectDate(dateInfo.date)"
          >
            <div class="date-label">{{ dateInfo.label }}</div>
            <div class="date-value">{{ dateInfo.weekday }}</div>
          </div>
        </div>
        <div v-else class="loading-wrapper">
          <el-icon class="is-loading" :size="24"><Loading /></el-icon>
        </div>
      </div>

      <!-- 泳道展示卡片 -->
      <div class="section-card lanes-card">
        <div class="card-header">
          <div class="header-icon">
            <el-icon :size="20"><Guide /></el-icon>
          </div>
          <h3 class="card-title">可用泳道</h3>
        </div>
        <div v-if="lanesLoading" class="loading-wrapper">
          <el-icon class="is-loading" :size="24"><Loading /></el-icon>
        </div>
        <div v-else-if="lanes.length === 0" class="empty-wrapper">
          <p>暂无可用泳道</p>
        </div>
        <div v-else class="lanes-list">
          <div
            v-for="lane in lanes"
            :key="lane.id"
            class="lane-item"
            :class="{ 'lane-fast': lane.laneType === '1', 'lane-medium': lane.laneType === '2', 'lane-slow': lane.laneType === '3' }"
            @click="goToTimeSlots(lane)"
          >
            <div class="lane-header">
              <span class="lane-no">{{ lane.laneNo }}</span>
              <span class="lane-type">{{ lane.laneTypeName }}</span>
            </div>
            <div class="lane-info">
              <span>容量: {{ lane.capacity }}人</span>
              <span>长×宽×深: {{ lane.length }}×{{ lane.width }}×{{ lane.depth }}米</span>
            </div>
            <div class="lane-action">
              <span>选择时段 →</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 我的预约按钮 -->
      <div class="my-reservations-btn-wrapper">
        <button class="my-reservations-btn" @click="showMyReservations">
          <el-icon><Tickets /></el-icon>
          <span>我的预约</span>
        </button>
      </div>
    </div>

    <!-- 我的预约弹窗 -->
    <el-dialog
      v-model="myReservationsVisible"
      title="我的预约"
      width="90%"
      class="custom-dialog reservations-dialog"
    >
      <div v-if="myReservationsLoading" class="loading-wrapper">
        <el-icon class="is-loading" :size="24"><Loading /></el-icon>
      </div>
      <div v-else-if="myReservations.length === 0" class="empty-wrapper">
        <p>暂无预约记录</p>
      </div>
      <div v-else class="reservations-list">
        <div
          v-for="reservation in myReservations"
          :key="reservation.id"
          class="reservation-item"
          :class="`status-${reservation.status}`"
        >
          <div class="reservation-header">
            <span class="reservation-no">{{ reservation.reservationNo }}</span>
            <el-tag :type="getStatusType(reservation.status)" size="small">
              {{ reservation.statusName }}
            </el-tag>
          </div>
          <div class="reservation-body">
            <div class="reservation-row">
              <span class="row-label">日期</span>
              <span class="row-value">{{ reservation.reservationDate }}</span>
            </div>
            <div class="reservation-row">
              <span class="row-label">时段</span>
              <span class="row-value">{{ formatTime(reservation.startTime) }} - {{ formatTime(reservation.endTime) }}</span>
            </div>
            <div class="reservation-row">
              <span class="row-label">泳道</span>
              <span class="row-value">{{ reservation.laneNo }}</span>
            </div>
            <div class="reservation-row">
              <span class="row-label">金额</span>
              <span class="row-value">¥{{ reservation.amount  }}</span>
            </div>
          </div>
          <div v-if="reservation.canCancel" class="reservation-footer">
            <el-button
              type="danger"
              plain
              size="small"
              @click="handleCancelReservation(reservation.id)"
              :loading="cancelling === reservation.id"
            >
              取消预约
            </el-button>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Calendar,
  Guide,
  Loading,
  Tickets
} from '@element-plus/icons-vue'
import {
  listLanes,
  listMyReservations,
  cancelReservation
} from '@/api/reservation'
import type { Lane, Reservation } from '@/types/reservation'
import { getMemberInfo } from '@/api/member'
import type { MemberInfo } from '@/api/member'

interface DateInfo {
  date: string
  label: string
  weekday: string
  isToday: boolean
}

const router = useRouter()
const selectedDate = ref('')
const availableDates = ref<DateInfo[]>([])
const lanes = ref<Lane[]>([])
const myReservations = ref<Reservation[]>([])
const lanesLoading = ref(false)
const myReservationsLoading = ref(false)
const cancelling = ref<number | null>(null)
const myReservationsVisible = ref(false)

onMounted(async () => {
  await loadAvailableDates()
  if (availableDates.value.length > 0) {
    selectedDate.value = availableDates.value[0].date
    loadLanes()
  }
})

function getTodayDate(): string {
  const today = new Date()
  const year = today.getFullYear()
  const month = String(today.getMonth() + 1).padStart(2, '0')
  const day = String(today.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

// 加载可用日期（根据会员权益）
async function loadAvailableDates() {
  try {
    const response = await getMemberInfo()
    if (response.code === 200 && response.data) {
      const memberInfo: MemberInfo = response.data
      const priorityDays = memberInfo.priorityBookingDays || 0

      // 生成可用日期列表（今天 + 提前预约天数）
      const dates: DateInfo[] = []
      const today = new Date()

      for (let i = 0; i <= priorityDays; i++) {
        const date = new Date(today)
        date.setDate(today.getDate() + i)

        const year = date.getFullYear()
        const month = String(date.getMonth() + 1).padStart(2, '0')
        const day = String(date.getDate()).padStart(2, '0')
        const dateStr = `${year}-${month}-${day}`

        const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
        const weekday = weekdays[date.getDay()]

        let label = ''
        if (i === 0) {
          label = '今天'
        } else if (i === 1) {
          label = '明天'
        } else {
          label = `${month}-${day}`
        }

        dates.push({
          date: dateStr,
          label: label,
          weekday: weekday,
          isToday: i === 0
        })
      }

      availableDates.value = dates
    }
  } catch (error) {
    console.error('加载会员信息失败:', error)
    ElMessage.error('加载可用日期失败')
  }
}

// 选择日期
function selectDate(date: string) {
  selectedDate.value = date
  // 重新加载泳道
  loadLanes()
}

function goToTimeSlots(lane: Lane) {
  if (!selectedDate.value) {
    ElMessage.warning('请先选择预约日期')
    return
  }
  router.push({
    path: '/timeslots',
    query: {
      date: selectedDate.value,
      laneId: lane.id.toString(),
      laneNo: lane.laneNo
    }
  })
}

async function loadLanes() {
  lanesLoading.value = true
  try {
    const response = await listLanes()
    if (response.code === 200) {
      lanes.value = response.data || []
    }
  } catch (error) {
    console.error('加载泳道失败:', error)
    ElMessage.error('加载泳道失败')
  } finally {
    lanesLoading.value = false
  }
}

async function showMyReservations() {
  myReservationsVisible.value = true
  myReservationsLoading.value = true
  try {
    const response = await listMyReservations({})
    if (response.code === 200) {
      myReservations.value = response.data || []
    }
  } catch (error) {
    console.error('加载预约记录失败:', error)
    ElMessage.error('加载预约记录失败')
  } finally {
    myReservationsLoading.value = false
  }
}

async function handleCancelReservation(reservationId: number) {
  try {
    await ElMessageBox.confirm(
      '注意：只能在预约时段开始前取消订单。使用免费次数支付的将恢复免费次数，余额支付的将返还余额和积分。确定要取消该预约吗？',
      '取消预约',
      {
        confirmButtonText: '确定取消',
        cancelButtonText: '再想想',
        type: 'warning'
      }
    )

    cancelling.value = reservationId
    const response = await cancelReservation(reservationId)
    if (response.code === 200) {
      ElMessage.success('取消成功')
      // 刷新我的预约列表
      await showMyReservations()
    } else {
      ElMessage.error(response.msg || '取消失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消预约失败:', error)
      ElMessage.error((error as any).msg || (error as any).message || '取消预约失败')
    }
  } finally {
    cancelling.value = null
  }
}

function formatTime(time: string): string {
  // 时间格式已经是 "HH:mm" 或 "HH:mm:ss"，直接返回前5位
  if (!time) return ''
  return time.substring(0, 5)
}

function getStatusType(status: string): string {
  const statusMap: Record<string, string> = {
    '0': 'warning',   // 待支付
    '1': 'success',   // 已支付
    '2': 'info',      // 已完成
    '3': 'danger',    // 已取消
    '4': 'info'       // 已退款
  }
  return statusMap[status] || 'info'
}
</script>

<style scoped lang="scss">
// 引入复古未来主义字体
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@700;900&family=Space+Grotesk:wght@400;500;600;700&display=swap');

// 复古未来主义配色
$color-bg-start: #0B2545;
$color-bg-mid: #134074;
$color-bg-end: #1a5a9a;
$color-coral: #FF6B6B;
$color-teal: #20C997;
$color-amber: #FFA94D;
$color-white: #FFFFFF;
$color-border: #0B2545;

.reservation-page {
  min-height: 100vh;
  position: relative;
  overflow: hidden;
  padding-bottom: 80px;
  background: linear-gradient(135deg, $color-bg-start 0%, $color-bg-mid 50%, $color-bg-end 100%);
}

/* 几何浮动背景 */
.geometric-bg {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 0;
  overflow: hidden;
}

.diagonal-lines {
  position: absolute;
  width: 200%;
  height: 200%;
  top: -50%;
  left: -50%;
  background: repeating-linear-gradient(
    45deg,
    transparent,
    transparent 35px,
    rgba(255, 255, 255, 0.03) 35px,
    rgba(255, 255, 255, 0.03) 70px
  );
  animation: lines-drift 60s linear infinite;
}

@keyframes lines-drift {
  0% {
    transform: translate(0, 0);
  }
  100% {
    transform: translate(70px, 70px);
  }
}

.floating-shapes {
  position: absolute;
  width: 100%;
  height: 100%;
}

.shape {
  position: absolute;
  opacity: 0.1;
  animation: float 20s ease-in-out infinite;

  &.shape-1 {
    width: 80px;
    height: 80px;
    background: $color-coral;
    top: 10%;
    left: 15%;
    border-radius: 4px;
    transform: rotate(45deg);
    animation-delay: 0s;
  }

  &.shape-2 {
    width: 60px;
    height: 60px;
    background: $color-teal;
    top: 25%;
    right: 10%;
    border-radius: 50%;
    animation-delay: -5s;
  }

  &.shape-3 {
    width: 100px;
    height: 100px;
    background: $color-amber;
    bottom: 30%;
    left: 8%;
    clip-path: polygon(50% 0%, 0% 100%, 100% 100%);
    animation-delay: -10s;
  }

  &.shape-4 {
    width: 50px;
    height: 50px;
    background: $color-coral;
    top: 50%;
    right: 20%;
    border-radius: 4px;
    animation-delay: -3s;
  }

  &.shape-5 {
    width: 70px;
    height: 70px;
    background: $color-teal;
    bottom: 20%;
    right: 15%;
    clip-path: polygon(50% 0%, 100% 50%, 50% 100%, 0% 50%);
    animation-delay: -8s;
  }

  &.shape-6 {
    width: 90px;
    height: 90px;
    background: $color-amber;
    top: 70%;
    left: 25%;
    border-radius: 50%;
    animation-delay: -12s;
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  25% {
    transform: translateY(-30px) rotate(90deg);
  }
  50% {
    transform: translateY(0) rotate(180deg);
  }
  75% {
    transform: translateY(30px) rotate(270deg);
  }
}

/* 顶部标题区 */
.top-nav {
  position: relative;
  z-index: 10;
  padding: 24px 20px 16px;
}

.title-decoration {
  position: relative;
  height: 40px;
  margin-bottom: 8px;
}

.deco-line {
  position: absolute;
  height: 3px;
  background: $color-white;
  opacity: 0.8;

  &.deco-line-1 {
    width: 60px;
    top: 50%;
    left: 50%;
    transform: translate(-80px, -50%);
  }

  &.deco-line-2 {
    width: 60px;
    top: 50%;
    right: 50%;
    transform: translate(80px, -50%);
  }
}

.deco-circle {
  position: absolute;
  border: 3px solid $color-white;
  opacity: 0.8;

  &.deco-circle-1 {
    width: 16px;
    height: 16px;
    top: 50%;
    left: 50%;
    transform: translate(-35px, -50%);
    border-radius: 50%;
  }

  &.deco-circle-2 {
    width: 16px;
    height: 16px;
    top: 50%;
    right: 50%;
    transform: translate(19px, -50%);
    border-radius: 50%;
  }
}

.page-title {
  font-family: 'Playfair Display', serif;
  font-size: 28px;
  font-weight: 900;
  color: $color-white;
  margin: 0;
  text-align: center;
  letter-spacing: 2px;
  text-transform: uppercase;
  position: relative;

  &::before,
  &::after {
    content: '◆';
    position: absolute;
    top: 50%;
    transform: translateY(-50%);
    color: $color-coral;
    font-size: 12px;
  }

  &::before {
    left: 20px;
  }

  &::after {
    right: 20px;
  }
}

/* 主内容 */
.main-content {
  position: relative;
  z-index: 10;
  padding: 0 16px;
}

.section-card {
  background: $color-white;
  border: 3px solid $color-border;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
  box-shadow: 6px 6px 0px $color-border;
  position: relative;

  &::before {
    content: '';
    position: absolute;
    top: -3px;
    left: -3px;
    right: -3px;
    bottom: -3px;
    background: linear-gradient(135deg, $color-coral, $color-teal, $color-amber);
    z-index: -1;
    border-radius: 10px;
    opacity: 0;
    transition: opacity 0.3s;
  }

  &:hover::before {
    opacity: 0.1;
  }
}

.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 16px;
}

.header-icon {
  width: 40px;
  height: 40px;
  border-radius: 4px;
  background: linear-gradient(135deg, $color-bg-start 0%, $color-bg-mid 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: $color-white;
  border: 2px solid $color-border;
  box-shadow: 3px 3px 0px $color-border;
}

.card-title {
  font-family: 'Space Grotesk', sans-serif;
  font-size: 18px;
  font-weight: 700;
  color: $color-bg-start;
  margin: 0;
  text-transform: uppercase;
  letter-spacing: 1px;
}

/* 日期列表 - Brutalist风格 */
.date-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(85px, 1fr));
  gap: 12px;
}

.date-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 14px 10px;
  background: $color-white;
  border: 3px solid $color-border;
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
  box-shadow: 4px 4px 0px $color-border;

  &:active {
    transform: translate(2px, 2px);
    box-shadow: 2px 2px 0px $color-border;
  }

  &.active {
    background: linear-gradient(135deg, $color-coral, #ff8787);
    border-color: $color-bg-start;
    box-shadow: 4px 4px 0px $color-bg-start;
    transform: translate(-2px, -2px);

    &:active {
      transform: translate(0, 0);
      box-shadow: 2px 2px 0px $color-bg-start;
    }

    .date-label {
      color: $color-white;
      font-weight: 700;
    }

    .date-value {
      color: $color-white;
      font-weight: 600;
    }
  }

  &.today {
    border-color: $color-amber;

    &:active {
      transform: translate(2px, 2px);
      box-shadow: 2px 2px 0px $color-amber;
    }

    &.active {
      border-color: $color-bg-start;
      box-shadow: 4px 4px 0px $color-bg-start;
    }
  }
}

.date-label {
  font-family: 'Space Grotesk', sans-serif;
  font-size: 14px;
  color: $color-bg-start;
  margin-bottom: 6px;
  font-weight: 600;
}

.date-value {
  font-family: 'Space Grotesk', sans-serif;
  font-size: 12px;
  color: #666;
  font-weight: 500;
}

/* 加载和空状态 */
.loading-wrapper,
.empty-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  color: $color-bg-start;
  font-family: 'Space Grotesk', sans-serif;
  font-weight: 500;
}

/* 泳道列表 - Brutalist风格 */
.lanes-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.lane-item {
  padding: 16px;
  background: $color-white;
  border: 3px solid $color-border;
  transition: all 0.2s;
  cursor: pointer;
  position: relative;
  box-shadow: 4px 4px 0px rgba($color-border, 0.3);

  &:active {
    transform: translate(2px, 2px);
    box-shadow: 2px 2px 0px rgba($color-border, 0.3);
  }

  &.lane-fast {
    border-color: $color-coral;
    background: linear-gradient(135deg, rgba($color-coral, 0.08) 0%, rgba($color-coral, 0.03) 100%);
    box-shadow: 4px 4px 0px rgba($color-coral, 0.3);

    .lane-type {
      color: $color-coral;
    }
  }

  &.lane-medium {
    border-color: $color-amber;
    background: linear-gradient(135deg, rgba($color-amber, 0.08) 0%, rgba($color-amber, 0.03) 100%);
    box-shadow: 4px 4px 0px rgba($color-amber, 0.3);

    .lane-type {
      color: $color-amber;
    }
  }

  &.lane-slow {
    border-color: $color-teal;
    background: linear-gradient(135deg, rgba($color-teal, 0.08) 0%, rgba($color-teal, 0.03) 100%);
    box-shadow: 4px 4px 0px rgba($color-teal, 0.3);

    .lane-type {
      color: $color-teal;
    }
  }
}

.lane-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}

.lane-no {
  font-family: 'Playfair Display', serif;
  font-size: 20px;
  font-weight: 700;
  color: $color-bg-start;
}

.lane-type {
  font-family: 'Space Grotesk', sans-serif;
  font-size: 13px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.lane-info {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: #666;
  font-family: 'Space Grotesk', sans-serif;
  font-weight: 500;
}

.lane-action {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-top: 10px;
  padding-top: 10px;
  border-top: 2px solid rgba($color-bg-start, 0.2);

  span {
    font-family: 'Space Grotesk', sans-serif;
    font-size: 14px;
    font-weight: 700;
    color: $color-bg-start;
    text-transform: uppercase;
    letter-spacing: 1px;
  }
}

/* 我的预约按钮 - Brutalist风格 */
.my-reservations-btn-wrapper {
  margin-top: 8px;
}

.my-reservations-btn {
  width: 100%;
  height: 56px;
  border-radius: 6px;
  border: 3px solid $color-border;
  background: linear-gradient(135deg, $color-coral 0%, $color-amber 100%);
  color: $color-white;
  font-family: 'Space Grotesk', sans-serif;
  font-size: 16px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 2px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  cursor: pointer;
  box-shadow: 6px 6px 0px $color-border;
  transition: all 0.2s;
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
    transition: left 0.5s;
  }

  &:hover::before {
    left: 100%;
  }

  &:active {
    transform: translate(3px, 3px);
    box-shadow: 3px 3px 0px $color-border;
  }
}

/* 预约记录列表 - Brutalist风格 */
.reservations-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
  max-height: 400px;
  overflow-y: auto;
  padding-right: 4px;

  &::-webkit-scrollbar {
    width: 8px;
  }

  &::-webkit-scrollbar-track {
    background: #f1f1f1;
    border-radius: 4px;
  }

  &::-webkit-scrollbar-thumb {
    background: $color-bg-start;
    border-radius: 4px;
  }
}

.reservation-item {
  padding: 16px;
  background: $color-white;
  border: 3px solid #e0e0e0;
  position: relative;
  box-shadow: 4px 4px 0px rgba(0, 0, 0, 0.1);

  &.status-0 {
    border-color: $color-amber;
    background: linear-gradient(135deg, rgba($color-amber, 0.08) 0%, rgba($color-amber, 0.03) 100%);
  }

  &.status-1 {
    border-color: $color-teal;
    background: linear-gradient(135deg, rgba($color-teal, 0.08) 0%, rgba($color-teal, 0.03) 100%);
  }

  &.status-3 {
    opacity: 0.5;
  }
}

.reservation-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
}

.reservation-no {
  font-family: 'Space Grotesk', sans-serif;
  font-size: 15px;
  font-weight: 700;
  color: $color-bg-start;
}

.reservation-body {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 14px;
}

.reservation-row {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
}

.row-label {
  font-family: 'Space Grotesk', sans-serif;
  color: #666;
  font-weight: 500;
}

.row-value {
  font-family: 'Space Grotesk', sans-serif;
  color: $color-bg-start;
  font-weight: 600;
}

.reservation-footer {
  display: flex;
  justify-content: flex-end;
}

/* 弹窗样式 - Brutalist风格 */
:deep(.custom-dialog) {
  .el-dialog {
    border-radius: 8px;
    border: 3px solid $color-border;
    box-shadow: 8px 8px 0px $color-border;
  }

  .el-dialog__header {
    padding: 20px 20px 10px;
    background: linear-gradient(135deg, $color-bg-start 0%, $color-bg-mid 100%);
    border-radius: 5px 5px 0 0;
  }

  .el-dialog__title {
    font-family: 'Playfair Display', serif;
    font-size: 20px;
    font-weight: 700;
    color: $color-white;
    text-transform: uppercase;
    letter-spacing: 2px;
  }

  .el-dialog__headerbtn .el-dialog__close {
    color: $color-white;
    font-size: 20px;

    &:hover {
      color: $color-coral;
    }
  }

  .el-dialog__body {
    padding: 20px;
  }

  .el-dialog__footer {
    padding: 10px 20px 20px;
  }

  .el-button {
    border-radius: 6px;
    font-weight: 600;
    font-family: 'Space Grotesk', sans-serif;
    text-transform: uppercase;
    letter-spacing: 1px;
    border: 2px solid currentColor;
    box-shadow: 3px 3px 0px currentColor;
    transition: all 0.2s;

    &:active {
      transform: translate(2px, 2px);
      box-shadow: 1px 1px 0px currentColor;
    }
  }
}

:deep(.reservations-dialog) {
  .el-dialog__body {
    padding-top: 0;
  }
}

/* Element Plus Tag 样式覆盖 */
:deep(.el-tag) {
  border-radius: 4px;
  border: 2px solid currentColor;
  font-weight: 600;
  font-family: 'Space Grotesk', sans-serif;
  text-transform: uppercase;
  letter-spacing: 1px;
  box-shadow: 2px 2px 0px currentColor;
}
</style>
