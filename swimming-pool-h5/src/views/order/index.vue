<template>
  <div class="order-page">
    <!-- 几何装饰背景 -->
    <div class="geometric-bg">
      <!-- 斜纹装饰 -->
      <div class="stripe-pattern"></div>
      <!-- 浮动几何图形 -->
      <div class="floating-shapes">
        <div class="shape shape-1"></div>
        <div class="shape shape-2"></div>
        <div class="shape shape-3"></div>
        <div class="shape shape-4"></div>
        <div class="shape shape-5"></div>
        <div class="shape shape-6"></div>
      </div>
    </div>

    <!-- 顶部导航 -->
    <div class="top-nav">
      <div class="nav-decoration"></div>
      <h1 class="page-title">我的订单</h1>
      <div class="nav-decoration-bottom"></div>
    </div>

    <!-- 主内容 -->
    <div class="order-content">
      <!-- 状态筛选标签 -->
      <div class="status-tabs">
        <div
          v-for="tab in statusTabs"
          :key="tab.value"
          class="tab-item"
          :class="{ active: activeTab === tab.value }"
          @click="handleTabChange(tab.value)"
        >
          <span class="tab-text">{{ tab.label }}</span>
        </div>
      </div>

      <!-- 订单列表 -->
      <div v-if="!loading && orders.length > 0" class="order-list">
        <div
          v-for="order in orders"
          :key="order.id"
          class="order-card"
          :class="getStatusBorderClass(order.status)"
          @click="handleViewDetail(order)"
        >
          <!-- 订单头部 -->
          <div class="order-header">
            <div class="order-number">订单号：{{ order.reservationNo }}</div>
            <div
              class="status-badge"
              :class="getStatusBadgeClass(order.status)"
            >
              {{ order.statusName }}
            </div>
          </div>

          <!-- 订单详情 -->
          <div class="order-body">
            <div class="order-row">
              <div class="row-item">
                <div class="item-icon">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                    <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                    <line x1="16" y1="2" x2="16" y2="6"></line>
                    <line x1="8" y1="2" x2="8" y2="6"></line>
                    <line x1="3" y1="10" x2="21" y2="10"></line>
                  </svg>
                </div>
                <div class="item-content">
                  <span class="item-label">预约日期</span>
                  <span class="item-value">{{ order.reservationDate }}</span>
                </div>
              </div>
            </div>

            <div class="order-row">
              <div class="row-item">
                <div class="item-icon">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                    <circle cx="12" cy="12" r="10"></circle>
                    <polyline points="12 6 12 12 16 14"></polyline>
                  </svg>
                </div>
                <div class="item-content">
                  <span class="item-label">时段</span>
                  <span class="item-value">{{ formatTime(order.startTime) }} - {{ formatTime(order.endTime) }}</span>
                </div>
              </div>
            </div>

            <div class="order-row">
              <div class="row-item">
                <div class="item-icon">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                    <polygon points="12 2 2 7 12 12 22 7 12 2"></polygon>
                    <polyline points="2 17 12 22 22 17"></polyline>
                    <polyline points="2 12 12 17 22 12"></polyline>
                  </svg>
                </div>
                <div class="item-content">
                  <span class="item-label">泳道</span>
                  <span class="item-value">{{ order.laneNo }} ({{ order.laneTypeName }})</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 订单底部 -->
          <div class="order-footer">
            <div class="order-amount">
              <span class="amount-label">{{ order.payType === '3' ? '支付方式：' : '订单金额：' }}</span>
              <span class="amount-value" :class="{ 'free-amount': order.payType === '3' }">
                {{ order.payType === '3' ? '免费次数' : '¥' + (order.actualAmount || order.amount) }}
              </span>
            </div>
            <div class="order-actions" @click.stop>
              <!-- 待支付状态 -->
              <template v-if="order.status === '0'">
                <button
                  class="brutalist-btn brutalist-btn--outline"
                  @click="handleCancelOrder(order)"
                >
                  取消订单
                </button>
                <button
                  class="brutalist-btn brutalist-btn--coral"
                  @click="handlePay(order)"
                >
                  去支付
                </button>
              </template>

              <!-- 已支付状态 -->
              <template v-else-if="order.status === '1'">
                <button
                  class="brutalist-btn brutalist-btn--outline"
                  @click="handleCancelOrder(order)"
                >
                  取消订单
                </button>
                <button
                  class="brutalist-btn brutalist-btn--primary"
                  @click="handleViewDetail(order)"
                >
                  查看详情
                </button>
              </template>

              <!-- 已完成状态 -->
              <template v-else-if="order.status === '2'">
                <button
                  class="brutalist-btn brutalist-btn--primary"
                  @click="handleViewDetail(order)"
                >
                  查看详情
                </button>
              </template>

              <!-- 已取消状态 -->
              <template v-else-if="order.status === '3'">
                <button
                  class="brutalist-btn brutalist-btn--outline"
                  @click="handleDeleteOrder(order)"
                  :disabled="!canDelete(order)"
                >
                  删除订单
                </button>
              </template>
            </div>
          </div>
        </div>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="loading-container">
        <div class="loading-spinner"></div>
        <p class="loading-text">加载中...</p>
      </div>

      <!-- 空状态 -->
      <div v-if="!loading && orders.length === 0" class="empty-container">
        <div class="empty-decoration">
          <div class="empty-circle"></div>
          <div class="empty-square"></div>
          <div class="empty-triangle"></div>
        </div>
        <p class="empty-text">暂无订单</p>
        <p class="empty-subtext">快去预约游泳吧~</p>
        <button
          class="brutalist-btn brutalist-btn--large brutalist-btn--teal"
          @click="goToReservation"
        >
          立即预约
        </button>
      </div>

      <!-- 加载更多 -->
      <div v-if="!loading && hasMore && orders.length > 0" class="load-more">
        <button
          class="load-more-btn"
          :disabled="loadingMore"
          @click="loadMore"
        >
          <span v-if="loadingMore" class="loading-dots">
            <span></span>
            <span></span>
            <span></span>
          </span>
          <span v-else>加载更多</span>
        </button>
      </div>

      <div v-if="!loading && !hasMore && orders.length > 0" class="no-more">
        <div class="no-more-decoration"></div>
        <span>没有更多订单了</span>
      </div>
    </div>

    <!-- 订单详情弹窗 -->
    <div v-if="detailDialogVisible" class="detail-overlay" @click="handleCloseDetail">
      <div class="detail-dialog" @click.stop>
        <div class="detail-header">
          <h2 class="detail-title">订单详情</h2>
          <button class="close-btn" @click="handleCloseDetail">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
              <line x1="18" y1="6" x2="6" y2="18"></line>
              <line x1="6" y1="6" x2="18" y2="18"></line>
            </svg>
          </button>
        </div>

        <div v-if="selectedOrder" class="detail-content">
          <div class="detail-section">
            <h3 class="section-title">订单信息</h3>
            <div class="detail-row">
              <span class="detail-label">订单编号</span>
              <span class="detail-value">{{ selectedOrder.reservationNo }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">订单状态</span>
              <div
                class="detail-status-badge"
                :class="getStatusBadgeClass(selectedOrder.status)"
              >
                {{ selectedOrder.statusName }}
              </div>
            </div>
          </div>

          <div class="detail-section">
            <h3 class="section-title">预约信息</h3>
            <div class="detail-row">
              <span class="detail-label">预约日期</span>
              <span class="detail-value">{{ selectedOrder.reservationDate }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">预约时段</span>
              <span class="detail-value">{{ formatTime(selectedOrder.startTime) }} - {{ formatTime(selectedOrder.endTime) }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">泳道信息</span>
              <span class="detail-value">{{ selectedOrder.laneNo }} ({{ selectedOrder.laneTypeName }})</span>
            </div>
          </div>

          <div class="detail-section">
            <h3 class="section-title">支付信息</h3>

            <!-- 支付方式 -->
            <div v-if="selectedOrder.payType" class="detail-row">
              <span class="detail-label">支付方式</span>
              <div
                class="pay-type-badge"
                :class="selectedOrder.payType === '3' ? 'pay-type-badge--free' : 'pay-type-badge--paid'"
              >
                {{ getPayTypeName(selectedOrder.payType) }}
              </div>
            </div>

            <!-- 原金额 -->
            <div v-if="selectedOrder.discountAmount && selectedOrder.discountAmount > 0" class="detail-row">
              <span class="detail-label">原金额</span>
              <span class="detail-value original-price">¥{{ selectedOrder.amount }}</span>
            </div>

            <!-- 优惠金额 -->
            <div v-if="selectedOrder.discountAmount && selectedOrder.discountAmount > 0" class="detail-row">
              <span class="detail-label">优惠金额</span>
              <span class="detail-value discount-price">-¥{{ selectedOrder.discountAmount }}</span>
            </div>

            <!-- 实际支付金额 -->
            <div class="detail-row">
              <span class="detail-label">{{ selectedOrder.payType === '3' ? '实际支付' : '实际支付金额' }}</span>
              <span
                class="detail-value"
                :class="selectedOrder.payType === '3' ? 'free-price' : 'price-highlight'"
              >
                {{ selectedOrder.payType === '3' ? '免费次数' : '¥' + (selectedOrder.actualAmount || selectedOrder.amount) }}
              </span>
            </div>

            <!-- 签到/签退/取消时间 -->
            <div v-if="selectedOrder.checkInTime" class="detail-row">
              <span class="detail-label">签到时间</span>
              <span class="detail-value">{{ selectedOrder.checkInTime }}</span>
            </div>
            <div v-if="selectedOrder.checkOutTime" class="detail-row">
              <span class="detail-label">签退时间</span>
              <span class="detail-value">{{ selectedOrder.checkOutTime }}</span>
            </div>
            <div v-if="selectedOrder.cancelTime" class="detail-row">
              <span class="detail-label">取消时间</span>
              <span class="detail-value">{{ selectedOrder.cancelTime }}</span>
            </div>
          </div>
        </div>

        <div class="detail-footer">
          <button class="brutalist-btn brutalist-btn--outline" @click="detailDialogVisible = false">
            关闭
          </button>
          <button
            v-if="selectedOrder && selectedOrder.status === '0'"
            class="brutalist-btn brutalist-btn--outline"
            @click="handleCancelOrder(selectedOrder)"
          >
            取消订单
          </button>
          <button
            v-if="selectedOrder && selectedOrder.status === '0'"
            class="brutalist-btn brutalist-btn--coral"
            @click="handlePay(selectedOrder)"
          >
            去支付
          </button>
        </div>
      </div>
    </div>

    <!-- 支付弹窗 -->
    <PaymentDialog
      v-model="paymentDialogVisible"
      :order-id="paymentOrderId"
      @success="handlePaymentSuccess"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listMyReservations, cancelReservation, deleteReservation } from '@/api/reservation'
import type { Reservation } from '@/types/reservation'
import PaymentDialog from '@/components/PaymentDialog.vue'

const router = useRouter()

// 状态筛选标签
const statusTabs = [
  { label: '全部', value: '' },
  { label: '待支付', value: '0' },
  { label: '已支付', value: '1' },
  { label: '已完成', value: '2' },
  { label: '已取消', value: '3' }
]

const activeTab = ref('')
const loading = ref(false)
const loadingMore = ref(false)
const hasMore = ref(true)
const pageNum = ref(1)
const pageSize = 10
const orders = ref<Reservation[]>([])
const detailDialogVisible = ref(false)
const selectedOrder = ref<Reservation | null>(null)
const paymentDialogVisible = ref(false)
const paymentOrderId = ref<number>()

onMounted(() => {
  loadOrders()
})

function handleTabChange(status: string) {
  activeTab.value = status
  pageNum.value = 1
  orders.value = []
  hasMore.value = true
  loadOrders()
}

async function loadOrders() {
  loading.value = true
  try {
    const response = await listMyReservations({
      status: activeTab.value || undefined
    })
    if (response.code === 200) {
      orders.value = response.data || []
      hasMore.value = orders.value.length >= pageSize
    }
  } catch (error) {
    console.error('加载订单失败:', error)
    ElMessage.error('加载订单失败')
  } finally {
    loading.value = false
  }
}

async function loadMore() {
  if (loadingMore.value || !hasMore.value) return

  loadingMore.value = true
  pageNum.value++
  try {
    const response = await listMyReservations({
      status: activeTab.value || undefined
    })
    if (response.code === 200) {
      const newOrders = response.data || []
      orders.value.push(...newOrders)
      hasMore.value = newOrders.length >= pageSize
    }
  } catch (error) {
    console.error('加载更多失败:', error)
    ElMessage.error('加载更多失败')
  } finally {
    loadingMore.value = false
  }
}

function handleViewDetail(order: Reservation) {
  selectedOrder.value = order
  detailDialogVisible.value = true
}

function handleCloseDetail() {
  detailDialogVisible.value = false
  selectedOrder.value = null
}

async function handleCancelOrder(order: Reservation) {
  if (!order.canCancel) {
    ElMessage.warning('该订单不可取消')
    return
  }

  try {
    await ElMessageBox.confirm(
      '注意：只能在预约时段开始前取消订单。使用免费次数支付的将恢复免费次数，余额支付的将返还余额和积分。确定要取消该订单吗？',
      '取消订单',
      {
        confirmButtonText: '确定取消',
        cancelButtonText: '再想想',
        type: 'warning'
      }
    )

    await cancelReservation(order.id)
    ElMessage.success('订单已取消')
    detailDialogVisible.value = false
    // 刷新订单列表
    pageNum.value = 1
    orders.value = []
    loadOrders()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('取消订单失败:', error)
      ElMessage.error(error.msg || error.message || '取消订单失败')
    }
  }
}

function handlePay(order: Reservation) {
  // 检查订单是否有关联的订单ID
  if (!order.orderId) {
    ElMessage.error('订单信息异常，未找到订单ID')
    return
  }

  // 关闭详情弹窗
  detailDialogVisible.value = false

  // 设置订单ID并打开支付弹窗
  paymentOrderId.value = order.orderId
  paymentDialogVisible.value = true
}

function handlePaymentSuccess() {
  // 支付成功，刷新订单列表
  paymentDialogVisible.value = false
  pageNum.value = 1
  orders.value = []
  loadOrders()
  ElMessage.success('支付成功')
}

function canDelete(order: Reservation): boolean {
  // 已取消的订单可以立即删除
  return order.status === '3' && !!order.cancelTime
}

async function handleDeleteOrder(order: Reservation) {
  try {
    await ElMessageBox.confirm(
      '确定要删除该订单吗？删除后将无法恢复。',
      '删除订单',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
        confirmButtonClass: 'el-button--danger'
      }
    )

    loading.value = true
    await deleteReservation(order.id)
    ElMessage.success('删除成功')

    // 刷新订单列表
    await loadOrders()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除订单失败:', error)
      ElMessage.error(error.message || '删除失败，请重试')
    }
  } finally {
    loading.value = false
  }
}

function formatTime(time: string): string {
  if (!time) return ''
  return time.substring(0, 5)
}

function getStatusBorderClass(status: string): string {
  const statusMap: Record<string, string> = {
    '0': 'order-card--warning',
    '1': 'order-card--success',
    '2': 'order-card--info',
    '3': 'order-card--danger'
  }
  return statusMap[status] || ''
}

function getStatusBadgeClass(status: string): string {
  const statusMap: Record<string, string> = {
    '0': 'status-badge--warning',
    '1': 'status-badge--success',
    '2': 'status-badge--info',
    '3': 'status-badge--danger'
  }
  return statusMap[status] || ''
}

function getPayTypeName(payType: string): string {
  const payTypeMap: Record<string, string> = {
    '1': '微信支付',
    '2': '余额支付',
    '3': '免费次数'
  }
  return payTypeMap[payType] || '未知'
}

function goToReservation() {
  router.push('/reservation')
}
</script>

<style scoped lang="scss">
// @import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@700;900&family=Space+Grotesk:wght@400;500;600;700&display=swap');

.order-page {
  min-height: 100vh;
  position: relative;
  background: linear-gradient(180deg, #0B2545 0%, #134074 50%, #1B4B7A 100%);
  overflow-x: hidden;
  font-family: 'Space Grotesk', -apple-system, BlinkMacSystemFont, sans-serif;
}

/* ===== 几何装饰背景 ===== */
.geometric-bg {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 0;
  overflow: hidden;
  pointer-events: none;
}

/* 斜纹装饰 */
.stripe-pattern {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: repeating-linear-gradient(
    45deg,
    transparent,
    transparent 35px,
    rgba(255, 255, 255, 0.03) 35px,
    rgba(255, 255, 255, 0.03) 70px
  );
}

/* 浮动几何图形 */
.floating-shapes {
  position: absolute;
  width: 100%;
  height: 100%;
}

.shape {
  position: absolute;
  opacity: 0.15;
  animation: float-shape 20s ease-in-out infinite;
}

.shape-1 {
  width: 80px;
  height: 80px;
  top: 10%;
  left: 10%;
  background: linear-gradient(135deg, #FF6B6B 0%, #FF8E8E 100%);
  transform: rotate(45deg);
  animation-delay: 0s;
}

.shape-2 {
  width: 60px;
  height: 60px;
  top: 20%;
  right: 15%;
  background: linear-gradient(135deg, #20C997 0%, #4DD4A8 100%);
  border-radius: 50%;
  animation-delay: -5s;
}

.shape-3 {
  width: 0;
  height: 0;
  top: 40%;
  left: 5%;
  border-left: 35px solid transparent;
  border-right: 35px solid transparent;
  border-bottom: 60px solid #FFA94D;
  animation-delay: -10s;
}

.shape-4 {
  width: 50px;
  height: 50px;
  top: 60%;
  right: 8%;
  background: linear-gradient(135deg, #20C997 0%, #4DD4A8 100%);
  transform: rotate(45deg);
  animation-delay: -3s;
}

.shape-5 {
  width: 70px;
  height: 70px;
  bottom: 15%;
  left: 20%;
  background: linear-gradient(135deg, #FF6B6B 0%, #FF8E8E 100%);
  border-radius: 50%;
  animation-delay: -8s;
}

.shape-6 {
  width: 0;
  height: 0;
  bottom: 25%;
  right: 25%;
  border-left: 30px solid transparent;
  border-right: 30px solid transparent;
  border-bottom: 50px solid #FFA94D;
  animation-delay: -12s;
}

@keyframes float-shape {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  25% {
    transform: translateY(-20px) rotate(5deg);
  }
  50% {
    transform: translateY(-10px) rotate(-3deg);
  }
  75% {
    transform: translateY(-15px) rotate(3deg);
  }
}

/* ===== 顶部导航 ===== */
.top-nav {
  position: relative;
  z-index: 10;
  padding: 24px 20px;
  text-align: center;
}

.nav-decoration {
  width: 60px;
  height: 4px;
  margin: 0 auto 12px;
  background: linear-gradient(90deg, #FF6B6B 0%, #20C997 50%, #FFA94D 100%);
  border-radius: 2px;
}

.nav-decoration-bottom {
  display: flex;
  justify-content: center;
  gap: 8px;
  margin-top: 12px;

  &::before,
  &::after {
    content: '';
    width: 8px;
    height: 8px;
    background: #20C997;
    transform: rotate(45deg);
  }
}

.page-title {
  font-family: 'Playfair Display', serif;
  font-size: 28px;
  font-weight: 900;
  color: #fff;
  margin: 0;
  text-transform: uppercase;
  letter-spacing: 2px;
  text-shadow: 3px 3px 0 rgba(0, 0, 0, 0.3);
}

/* ===== 主内容 ===== */
.order-content {
  position: relative;
  z-index: 10;
  padding: 0 16px 24px;
}

/* ===== 状态筛选标签 ===== */
.status-tabs {
  display: flex;
  background: #fff;
  border: 3px solid #0B2545;
  box-shadow: 5px 5px 0 #0B2545;
  margin-bottom: 20px;
  overflow: hidden;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 14px 8px;
  cursor: pointer;
  position: relative;
  transition: all 0.2s;
  background: #fff;
  border-right: 2px solid #0B2545;

  &:last-child {
    border-right: none;
  }

  &:hover {
    background: rgba(32, 201, 151, 0.1);
  }

  &.active {
    background: #20C997;

    .tab-text {
      color: #fff;
    }
  }
}

.tab-text {
  display: block;
  font-size: 13px;
  font-weight: 600;
  color: #0B2545;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

/* ===== 订单列表 ===== */
.order-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* ===== 订单卡片 ===== */
.order-card {
  background: #fff;
  border: 3px solid #0B2545;
  box-shadow: 6px 6px 0 #0B2545;
  padding: 16px;
  transition: all 0.2s;
  cursor: pointer;
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 6px;
    height: 100%;
    background: #20C997;
  }

  &.order-card--warning::before {
    background: #FFA94D;
  }

  &.order-card--success::before {
    background: #20C997;
  }

  &.order-card--info::before {
    background: #6C757D;
  }

  &.order-card--danger::before {
    background: #FF6B6B;
  }

  &:active {
    transform: translate(2px, 2px);
    box-shadow: 4px 4px 0 #0B2545;
  }
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 12px;
  border-bottom: 2px solid #0B2545;
  margin-bottom: 12px;
}

.order-number {
  font-size: 14px;
  font-weight: 700;
  color: #0B2545;
  font-family: 'Space Grotesk', monospace;
}

.status-badge {
  padding: 4px 12px;
  font-size: 12px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  border: 2px solid;
}

.status-badge--warning {
  background: #FFA94D;
  color: #fff;
  border-color: #FFA94D;
}

.status-badge--success {
  background: #20C997;
  color: #fff;
  border-color: #20C997;
}

.status-badge--info {
  background: #6C757D;
  color: #fff;
  border-color: #6C757D;
}

.status-badge--danger {
  background: #FF6B6B;
  color: #fff;
  border-color: #FF6B6B;
}

.order-body {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.order-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.row-item {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: 1;
}

.item-icon {
  color: #20C997;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  background: rgba(32, 201, 151, 0.15);
  border-radius: 50%;
}

.item-content {
  display: flex;
  flex-direction: column;
  gap: 2px;
  flex: 1;
}

.item-label {
  font-size: 11px;
  color: #6C757D;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.item-value {
  font-size: 14px;
  font-weight: 600;
  color: #0B2545;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 2px solid #E5E7EB;
}

.order-amount {
  display: flex;
  align-items: baseline;
  gap: 6px;
}

.amount-label {
  font-size: 13px;
  color: #6C757D;
  font-weight: 500;
}

.amount-value {
  font-size: 20px;
  font-weight: 900;
  color: #20C997;
  font-family: 'Space Grotesk', monospace;

  &.free-amount {
    font-size: 16px;
    color: #20C997;
  }
}

.order-actions {
  display: flex;
  gap: 8px;
}

/* ===== Brutalist 按钮 ===== */
.brutalist-btn {
  padding: 10px 16px;
  font-size: 13px;
  font-weight: 700;
  font-family: 'Space Grotesk', sans-serif;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  border: 2px solid #0B2545;
  cursor: pointer;
  transition: all 0.15s;
  position: relative;
  background: #fff;
  color: #0B2545;
  box-shadow: 3px 3px 0 #0B2545;

  &:active:not(:disabled) {
    transform: translate(2px, 2px);
    box-shadow: 1px 1px 0 #0B2545;
  }

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }

  &--coral {
    background: linear-gradient(135deg, #FF6B6B 0%, #FF8E8E 100%);
    color: #fff;
  }

  &--teal {
    background: linear-gradient(135deg, #20C997 0%, #4DD4A8 100%);
    color: #fff;
  }

  &--amber {
    background: linear-gradient(135deg, #FFA94D 0%, #FFBE6A 100%);
    color: #fff;
  }

  &--primary {
    background: linear-gradient(135deg, #134074 0%, #1B4B7A 100%);
    color: #fff;
  }

  &--outline {
    background: #fff;
    color: #0B2545;
  }

  &--large {
    padding: 14px 28px;
    font-size: 15px;
  }
}

/* ===== 加载状态 ===== */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
}

.loading-spinner {
  width: 50px;
  height: 50px;
  border: 4px solid rgba(255, 255, 255, 0.2);
  border-top-color: #20C997;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.loading-text {
  margin-top: 16px;
  font-size: 14px;
  font-weight: 600;
  color: #fff;
  letter-spacing: 1px;
}

/* ===== 空状态 ===== */
.empty-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  text-align: center;
  background: #fff;
  border: 3px solid #0B2545;
  box-shadow: 6px 6px 0 #0B2545;
}

.empty-decoration {
  position: relative;
  width: 120px;
  height: 120px;
  margin-bottom: 24px;
}

.empty-circle {
  position: absolute;
  width: 60px;
  height: 60px;
  top: 0;
  left: 0;
  background: linear-gradient(135deg, #FF6B6B 0%, #FF8E8E 100%);
  border-radius: 50%;
  border: 3px solid #0B2545;
}

.empty-square {
  position: absolute;
  width: 50px;
  height: 50px;
  top: 10px;
  right: 10px;
  background: linear-gradient(135deg, #20C997 0%, #4DD4A8 100%);
  border: 3px solid #0B2545;
  transform: rotate(15deg);
}

.empty-triangle {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 0;
  height: 0;
  border-left: 35px solid transparent;
  border-right: 35px solid transparent;
  border-bottom: 60px solid #FFA94D;
  filter: drop-shadow(0 3px 0 #0B2545);
}

.empty-text {
  font-family: 'Playfair Display', serif;
  font-size: 22px;
  font-weight: 900;
  color: #0B2545;
  margin: 0 0 8px;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.empty-subtext {
  font-size: 14px;
  color: #6C757D;
  margin: 0 0 28px;
}

/* ===== 加载更多 ===== */
.load-more {
  display: flex;
  justify-content: center;
  padding: 20px;
}

.load-more-btn {
  padding: 12px 24px;
  font-size: 14px;
  font-weight: 700;
  font-family: 'Space Grotesk', sans-serif;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  border: 2px solid #fff;
  background: transparent;
  color: #fff;
  cursor: pointer;
  transition: all 0.2s;

  &:hover:not(:disabled) {
    background: rgba(255, 255, 255, 0.1);
  }

  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }
}

.loading-dots {
  display: flex;
  gap: 4px;
  align-items: center;

  span {
    width: 8px;
    height: 8px;
    background: #fff;
    border-radius: 50%;
    animation: bounce 1.4s infinite ease-in-out both;

    &:nth-child(1) {
      animation-delay: -0.32s;
    }

    &:nth-child(2) {
      animation-delay: -0.16s;
    }
  }
}

@keyframes bounce {
  0%, 80%, 100% {
    transform: scale(0);
  }
  40% {
    transform: scale(1);
  }
}

.no-more {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  gap: 12px;
}

.no-more-decoration {
  display: flex;
  gap: 8px;

  &::before,
  &::after {
    content: '';
    width: 30px;
    height: 3px;
    background: rgba(255, 255, 255, 0.3);
  }
}

.no-more span {
  font-size: 13px;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.7);
  text-transform: uppercase;
  letter-spacing: 1px;
}

/* ===== 订单详情弹窗 ===== */
.detail-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(11, 37, 69, 0.9);
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  animation: fadeIn 0.2s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.detail-dialog {
  background: #fff;
  border: 3px solid #0B2545;
  box-shadow: 8px 8px 0 #0B2545;
  width: 100%;
  max-width: 500px;
  max-height: 80vh;
  display: flex;
  flex-direction: column;
  animation: slideUp 0.3s ease;
}

@keyframes slideUp {
  from {
    transform: translateY(20px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 3px solid #0B2545;
  background: linear-gradient(135deg, #0B2545 0%, #134074 100%);
}

.detail-title {
  font-family: 'Playfair Display', serif;
  font-size: 20px;
  font-weight: 900;
  color: #fff;
  margin: 0;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.close-btn {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
  border: 2px solid #fff;
  border-radius: 50%;
  color: #fff;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    background: rgba(255, 255, 255, 0.2);
  }

  &:active {
    transform: scale(0.95);
  }
}

.detail-content {
  padding: 20px;
  overflow-y: auto;
  flex: 1;
}

.detail-section {
  margin-bottom: 24px;

  &:last-child {
    margin-bottom: 0;
  }
}

.section-title {
  font-family: 'Playfair Display', serif;
  font-size: 16px;
  font-weight: 900;
  color: #0B2545;
  margin: 0 0 16px;
  text-transform: uppercase;
  letter-spacing: 1px;
  padding-bottom: 8px;
  border-bottom: 3px solid #20C997;
}

.detail-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 10px 0;
  gap: 16px;
}

.detail-label {
  flex-shrink: 0;
  font-size: 13px;
  font-weight: 600;
  color: #6C757D;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.detail-value {
  flex: 1;
  text-align: right;
  font-size: 14px;
  font-weight: 600;
  color: #0B2545;
  word-break: break-word;
}

.detail-status-badge {
  padding: 4px 12px;
  font-size: 12px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  border: 2px solid;
}

.pay-type-badge {
  padding: 4px 12px;
  font-size: 12px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  border: 2px solid;
}

.pay-type-badge--free {
  background: #20C997;
  color: #fff;
  border-color: #20C997;
}

.pay-type-badge--paid {
  background: #134074;
  color: #fff;
  border-color: #134074;
}

.price-highlight {
  font-size: 20px;
  font-weight: 900;
  color: #20C997;
  font-family: 'Space Grotesk', monospace;
}

.original-price {
  font-size: 15px;
  color: #9CA3AF;
  text-decoration: line-through;
}

.discount-price {
  font-size: 16px;
  font-weight: 700;
  color: #FF6B6B;
}

.free-price {
  font-size: 18px;
  font-weight: 900;
  color: #20C997;
}

.detail-footer {
  display: flex;
  gap: 10px;
  padding: 16px 20px;
  border-top: 3px solid #E5E7EB;
  background: #F8F9FA;

  .brutalist-btn {
    flex: 1;
    margin: 0;
  }
}
</style>
