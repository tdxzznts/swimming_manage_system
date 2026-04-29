<template>
  <div class="membership-page">
    <!-- Retro-Futuristic Background -->
    <div class="membership-bg">
      <!-- Animated geometric patterns -->
      <div class="geometric-layer layer-1">
        <div v-for="i in 10" :key="`geo-${i}`" class="geo-shape" :style="geoStyle(i)"></div>
      </div>

      <!-- Diagonal stripe overlay -->
      <div class="stripe-overlay"></div>

      <!-- Gradient background -->
      <div class="gradient-bg"></div>

      <!-- Floating orbs -->
      <div class="floating-orbs">
        <div class="orb orb-1"></div>
        <div class="orb orb-2"></div>
        <div class="orb orb-3"></div>
      </div>
    </div>

    <!-- Main Content -->
    <div class="membership-content">
      <!-- Member Card -->
      <div class="card-brutal">
        <div
          class="member-card-brutal"
          :class="`card-${memberInfo.level}`"
        >
          <!-- Card shine effect -->
          <div class="card-shine"></div>

          <!-- Card pattern -->
          <div class="card-pattern"></div>

          <!-- Card Header -->
          <div class="card-header-brutal">
            <div class="level-section">
              <div class="level-icon-brutal">
                <el-icon :size="32"><Medal /></el-icon>
              </div>
              <div class="level-info-brutal">
                <span class="level-name">{{ memberInfo.levelName }}</span>
                <span class="level-en">{{ memberInfo.levelEn }}</span>
              </div>
            </div>
            <div class="card-no-brutal">NO.{{ memberInfo.cardNo }}</div>
          </div>

          <!-- Card Body -->
          <div class="card-body-brutal">
            <div class="card-row">
              <span class="card-label">持卡人</span>
              <span class="card-value">{{ memberInfo.userName }}</span>
            </div>
            <div class="card-row">
              <span class="card-label">账户余额</span>
              <span class="card-value balance-highlight">¥{{ memberInfo.balance.toFixed(2) }}</span>
            </div>
            <div class="card-row">
              <span class="card-label">积分</span>
              <span class="card-value">{{ memberInfo.points }}</span>
            </div>
          </div>

          <!-- Card Footer -->
          <div class="card-footer-brutal">
            <span>有效期至：{{ memberInfo.validThru }}</span>
          </div>
        </div>
      </div>

      <!-- Quick Actions -->
      <div class="actions-brutal">
        <div class="action-brutal" @click="handleRecharge">
          <div class="action-icon-brutal">
            <el-icon :size="26"><Wallet /></el-icon>
          </div>
          <span class="action-label-brutal">充值</span>
        </div>
        <div class="action-brutal" @click="handleRules">
          <div class="action-icon-brutal">
            <el-icon :size="26"><Notebook /></el-icon>
          </div>
          <span class="action-label-brutal">规则</span>
        </div>
      </div>

      <!-- Member Benefits -->
      <div class="benefits-brutal">
        <div class="section-title-brutal">
          <div class="title-icon">
            <el-icon :size="22"><Trophy /></el-icon>
          </div>
          <h3>会员权益</h3>
        </div>
        <div class="benefits-list-brutal">
          <div
            v-for="benefit in memberBenefits"
            :key="benefit.id"
            class="benefit-brutal"
          >
            <div class="benefit-icon-brutal">
              <el-icon :size="20">
                <component :is="benefit.icon" />
              </el-icon>
            </div>
            <div class="benefit-content-brutal">
              <span class="benefit-name-brutal">{{ benefit.name }}</span>
              <span class="benefit-desc-brutal">{{ benefit.desc }}</span>
            </div>
            <div class="benefit-tag-brutal">
              <span class="tag-value">{{ formatBenefitValue(benefit) }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Recent Records -->
      <div class="records-brutal">
        <div class="records-header-brutal">
          <div class="section-title-brutal">
            <div class="title-icon">
              <el-icon :size="22"><Clock /></el-icon>
            </div>
            <h3>最近消费</h3>
          </div>
          <span class="view-all-brutal" @click="() => router.push('/records')">
            全部 →
          </span>
        </div>

        <div v-if="recentRecords.length > 0" class="records-list-brutal">
          <div
            v-for="record in recentRecords"
            :key="record.id"
            class="record-brutal"
          >
            <div class="record-icon-brutal">
              <el-icon :size="20">
                <component :is="record.iconName" />
              </el-icon>
            </div>
            <div class="record-content-brutal">
              <span class="record-title-brutal">{{ record.orderTypeName }}</span>
              <span class="record-time-brutal">{{ formatRecordTime(record.createTime) }}</span>
            </div>
            <div class="record-amount-brutal">
              <span :class="record.amountClass">{{ record.amountText }}</span>
            </div>
          </div>
        </div>

        <div v-else class="empty-records-brutal">
          <div class="empty-icon-brutal">
            <el-icon :size="60"><Document /></el-icon>
          </div>
          <p>暂无消费记录</p>
        </div>
      </div>
    </div>

    <!-- Recharge Dialog -->
    <el-dialog
      v-model="showRecharge"
      title="账户充值"
      width="90%"
      class="recharge-brutal-dialog"
      @close="handleRechargeDialogClose"
    >
      <div class="recharge-brutal-content">
        <!-- Current Balance -->
        <div class="balance-display-brutal">
          <span class="balance-label">当前余额</span>
          <span class="balance-value">¥{{ memberInfo.balance }}</span>
        </div>

        <!-- Amount Selection -->
        <div class="amount-section">
          <span class="section-label-brutal">选择充值金额</span>
          <div v-if="loadingAmounts" class="amount-loading-brutal">
            <div class="loading-spinner-brutal"></div>
            <span>加载中...</span>
          </div>
          <div v-else class="amount-grid-brutal">
            <div
              v-for="amount in rechargeAmounts"
              :key="amount.dictCode"
              class="amount-brutal"
              :class="{ active: selectedAmount === Number(amount.dictLabel) }"
              @click="selectedAmount = Number(amount.dictLabel)"
            >
              <span class="amount-value-brutal">¥{{ amount.dictLabel }}</span>
              <span v-if="amount.dictValue && Number(amount.dictValue) > 0" class="amount-bonus-brutal">
                送¥{{ amount.dictValue }}
              </span>
            </div>
          </div>
        </div>

        <!-- Custom Amount -->
        <div class="custom-amount-brutal">
          <el-input
            v-model="customAmount"
            type="number"
            placeholder="自定义金额"
            size="large"
            :min="1"
            class="custom-input-brutal"
          >
            <template #prepend>¥</template>
          </el-input>
        </div>

        <!-- Payment Methods -->
        <div class="payment-section">
          <span class="section-label-brutal">支付方式</span>
          <div class="payment-grid-brutal">
            <div
              v-for="method in paymentMethods"
              :key="method.id"
              class="payment-brutal"
              :class="{ active: selectedMethod === method.id, disabled: method.disabled }"
              @click="!method.disabled && (selectedMethod = method.id)"
            >
              <el-icon :size="24">
                <component :is="method.icon" />
              </el-icon>
              <span>{{ method.name }}</span>
            </div>
          </div>
        </div>
      </div>

      <template #footer>
        <el-button @click="showRecharge = false" class="dialog-cancel-btn">取消</el-button>
        <el-button
          type="primary"
          @click="handleConfirmRecharge"
          :disabled="(!selectedAmount && !customAmount) || loadingRecharge"
          class="dialog-confirm-btn"
        >
          {{ loadingRecharge ? '创建订单中...' : '确认充值' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- QR Code Dialog -->
    <el-dialog
      v-model="showPayQrCode"
      title="扫码支付"
      width="90%"
      class="qrcode-brutal-dialog"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      @close="handleQrCodeDialogClose"
    >
      <div class="qrcode-brutal-content">
        <div class="qrcode-info-brutal">
          <div class="info-row-brutal">
            <span class="info-label">订单号</span>
            <span class="info-value">{{ currentOrderNo }}</span>
          </div>
          <div class="info-row-brutal">
            <span class="info-label">充值金额</span>
            <span class="info-value amount-highlight">¥{{ rechargeOrderInfo.amount.toFixed(2) }}</span>
          </div>
          <div v-if="rechargeOrderInfo.bonusAmount > 0" class="info-row-brutal">
            <span class="info-label">赠送金额</span>
            <span class="info-value bonus-highlight">+¥{{ rechargeOrderInfo.bonusAmount.toFixed(2) }}</span>
          </div>
          <div class="info-row-brutal">
            <span class="info-label">实际支付</span>
            <span class="info-value total-highlight">¥{{ rechargeOrderInfo.actualAmount.toFixed(2) }}</span>
          </div>
        </div>

        <div class="qrcode-display-brutal">
          <div v-if="qrCodeLoading" class="qrcode-loading-brutal">
            <div class="loading-spinner-brutal"></div>
            <p>二维码生成中...</p>
          </div>
          <div v-else class="qrcode-image-brutal">
            <img :src="qrCodeUrl" alt="支付二维码" />
          </div>
        </div>

        <div class="payment-tips-brutal">
          <el-icon><InfoFilled /></el-icon>
          <span>请使用支付宝扫一扫，扫描二维码完成支付</span>
        </div>

        <div class="payment-status-brutal">
          <div v-if="paymentStatus === 'checking'" class="status-checking">
            <el-icon class="is-loading"><Loading /></el-icon>
            <span>正在查询支付状态...</span>
          </div>
          <div v-else-if="paymentStatus === 'success'" class="status-success">
            <el-icon :size="32"><CircleCheck /></el-icon>
            <span>支付成功！</span>
          </div>
          <div v-else class="status-waiting">
            <el-icon :size="32"><Clock /></el-icon>
            <span>等待支付...</span>
          </div>
        </div>
      </div>

      <template #footer>
        <el-button @click="handleQrCodeDialogClose" :disabled="paymentStatus === 'checking'" class="dialog-cancel-btn">
          {{ paymentStatus === 'success' ? '完成' : '关闭' }}
        </el-button>
        <el-button v-if="paymentStatus !== 'success'" type="primary" @click="checkPaymentStatus" class="dialog-confirm-btn">
          我已支付
        </el-button>
      </template>
    </el-dialog>

    <!-- Rules Dialog -->
    <el-dialog
      v-model="showRules"
      title="会员规则"
      width="90%"
      class="rules-brutal-dialog"
    >
      <div v-loading="rulesLoading" class="rules-brutal-content">
        <!-- Upgrade Rules -->
        <div class="rule-section-brutal">
          <h3 class="rule-title-brutal">
            <div class="title-icon">
              <el-icon><Trophy /></el-icon>
            </div>
            升级规则
          </h3>
          <div class="rule-description-brutal">
            <p v-for="(line, index) in memberRules.ruleDescription.split('\n')" :key="index">
              {{ line }}
            </p>
          </div>
        </div>

        <!-- Level Description -->
        <div class="rule-section-brutal">
          <h3 class="rule-title-brutal">
            <div class="title-icon">
              <el-icon><Medal /></el-icon>
            </div>
            等级说明
          </h3>
          <div class="levels-list-brutal">
            <div
              v-for="level in memberRules.levels"
              :key="level.id"
              class="level-brutal"
            >
              <div class="level-info-brutal">
                <span class="level-name-brutal">{{ level.name }}</span>
                <span class="level-desc-brutal">{{ level.desc }}</span>
              </div>
              <div class="level-requirement-brutal">
                <span class="requirement-label">升级要求：</span>
                <span class="requirement-value">累计≥¥{{ level.price }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Member Benefits -->
        <div class="rule-section-brutal">
          <h3 class="rule-title-brutal">
            <div class="title-icon">
              <el-icon><Star /></el-icon>
            </div>
            会员权益
          </h3>
          <div class="benefits-grid-brutal">
            <div
              v-for="benefit in memberRules.benefits"
              :key="benefit.id"
              class="rule-benefit-brutal"
            >
              <div class="benefit-icon-brutal">
                <el-icon :size="24">
                  <component :is="benefit.icon" />
                </el-icon>
              </div>
              <div class="benefit-info-brutal">
                <div class="benefit-header-brutal">
                  <span class="benefit-name-brutal">{{ benefit.name }}</span>
                  <el-tag size="small" :type="getLevelTagType(benefit.levelValue)">
                    {{ getLevelName(benefit.levelValue) }}
                  </el-tag>
                </div>
                <span class="benefit-desc-brutal">{{ benefit.desc }}</span>
              </div>
              <div class="benefit-value-brutal">
                <span class="value-tag">{{ formatBenefitValue(benefit) }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <template #footer>
        <el-button type="primary" @click="showRules = false" class="dialog-confirm-btn">我知道了</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Medal,
  Wallet,
  Document,
  Notebook,
  Clock,
  Trophy,
  Star,
  Calendar,
  Tickets,
  Discount,
  Timer,
  Headset,
  Coin,
  CreditCard,
  Loading,
  InfoFilled,
  CircleCheck
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import {
  getRechargeAmounts,
  createRechargeOrder,
  getRechargeOrderStatus
} from '@/api/recharge'
import type { RechargeAmount, RechargeOrderResult } from '@/api/recharge'
import {
  getMemberLevels,
  getMemberBenefits,
  getMemberInfo,
  getMemberRules
} from '@/api/member'
import type { MemberLevel, MemberBenefit, MemberInfo, MemberRules } from '@/api/member'
import { getRecentOrders } from '@/api/order'
import type { OrderRecord } from '@/api/order'

const router = useRouter()
const userStore = useUserStore()

// Member rules
const showRules = ref(false)
const memberRules = ref<MemberRules>({
  levels: [],
  benefits: [],
  ruleDescription: ''
})
const rulesLoading = ref(false)

// Member info
const memberInfo = ref<MemberInfo>({
  level: 2,
  levelName: '银卡会员',
  levelEn: 'SILVER',
  cardNo: '8888 8888 8888',
  userName: '游泳爱好者',
  balance: 50.00,
  points: 1200,
  validThru: '2026-12-31'
})

const memberLevels = ref<MemberLevel[]>([])
const memberBenefits = ref<MemberBenefit[]>([])
const recentRecords = ref<OrderRecord[]>([])

// Recharge
const rechargeAmounts = ref<RechargeAmount[]>([])
const loadingAmounts = ref(false)
const loadingRecharge = ref(false)

const paymentMethods = [
  { id: 'alipay', name: '支付宝', icon: 'CreditCard', disabled: false },
  { id: 'wechat', name: '微信支付', icon: 'Wallet', disabled: true }
]

const showRecharge = ref(false)
const showPayQrCode = ref(false)
const selectedAmount = ref(0)
const customAmount = ref('')
const selectedMethod = ref('alipay')

// Payment
const qrCodeUrl = ref('')
const currentOrderNo = ref('')
const pollingTimer = ref<number | null>(null)
const qrCodeLoading = ref(false)
const paymentStatus = ref<'waiting' | 'checking' | 'success'>('waiting')
const rechargeOrderInfo = ref<RechargeOrderResult>({
  orderNo: '',
  qrCodeUrl: '',
  expireTime: '',
  amount: 0,
  bonusAmount: 0,
  actualAmount: 0
})

function handleRecharge() {
  showRecharge.value = true
  loadRechargeAmounts()
}

function handleRules() {
  showRules.value = true
  loadMemberRules()
}

async function loadMemberRules() {
  if (memberRules.value.levels.length > 0) return

  rulesLoading.value = true
  try {
    const res = await getMemberRules()
    if (res.code === 200 && res.data) {
      memberRules.value = res.data
    } else {
      ElMessage.error('加载会员规则失败')
    }
  } catch (error) {
    console.error('加载会员规则失败', error)
    ElMessage.error('加载会员规则失败')
  } finally {
    rulesLoading.value = false
  }
}

async function loadMemberConfig() {
  try {
    const levelsRes = await getMemberLevels()
    if (levelsRes.code === 200 && levelsRes.data) {
      memberLevels.value = levelsRes.data
    }

    const infoRes = await getMemberInfo()
    if (infoRes.code === 200 && infoRes.data) {
      memberInfo.value = infoRes.data

      let levelValue = 0
      if (memberInfo.value.level && memberInfo.value.level > 0) {
        levelValue = memberInfo.value.level
      }

      const benefitsRes = await getMemberBenefits(levelValue.toString())
      if (benefitsRes.code === 200 && benefitsRes.data) {
        memberBenefits.value = benefitsRes.data
      }
    } else {
      const benefitsRes = await getMemberBenefits('0')
      if (benefitsRes.code === 200 && benefitsRes.data) {
        memberBenefits.value = benefitsRes.data
      }
    }
  } catch (error) {
    console.error('加载会员配置失败', error)
    ElMessage.error('加载会员配置失败')
  }
}

async function loadRechargeAmounts() {
  if (rechargeAmounts.value.length > 0) return

  loadingAmounts.value = true
  try {
    const res = await getRechargeAmounts()
    if (res.code === 200 && res.data) {
      rechargeAmounts.value = res.data
    }
  } catch (error) {
    console.error('加载充值金额配置失败', error)
    ElMessage.error('加载充值金额配置失败')
  } finally {
    loadingAmounts.value = false
  }
}

async function handleConfirmRecharge() {
  const amount = selectedAmount.value || Number(customAmount.value)
  if (!amount || amount <= 0) {
    ElMessage.warning('请选择充值金额')
    return
  }

  let bonusAmount = 0
  if (selectedAmount.value > 0) {
    const selectedConfig = rechargeAmounts.value.find(
      item => Number(item.dictLabel) === selectedAmount.value
    )
    if (selectedConfig && selectedConfig.dictValue) {
      bonusAmount = Number(selectedConfig.dictValue)
    }
  }

  loadingRecharge.value = true
  try {
    const res = await createRechargeOrder({
      amount: amount,
      bonusAmount: bonusAmount,
      payType: selectedMethod.value
    })

    if (res.code === 200 && res.data) {
      rechargeOrderInfo.value = res.data
      currentOrderNo.value = res.data.orderNo
      qrCodeUrl.value = res.data.qrCodeUrl

      showRecharge.value = false
      showPayQrCode.value = true
      qrCodeLoading.value = false

      startPolling()
    } else {
      ElMessage.error(res.msg || '创建充值订单失败')
    }
  } catch (error) {
    console.error('创建充值订单失败', error)
    ElMessage.error('创建充值订单失败')
  } finally {
    loadingRecharge.value = false
  }
}

function startPolling() {
  stopPolling()
  paymentStatus.value = 'waiting'

  pollingTimer.value = window.setInterval(() => {
    checkPaymentStatus()
  }, 3000)
}

function stopPolling() {
  if (pollingTimer.value) {
    clearInterval(pollingTimer.value)
    pollingTimer.value = null
  }
}

async function checkPaymentStatus() {
  if (!currentOrderNo.value) return

  paymentStatus.value = 'checking'
  try {
    const res = await getRechargeOrderStatus(currentOrderNo.value)
    if (res.code === 200 && res.data) {
      if (res.data.payStatus === '1') {
        paymentStatus.value = 'success'
        stopPolling()

        const totalAmount = rechargeOrderInfo.value.amount + rechargeOrderInfo.value.bonusAmount
        memberInfo.value.balance += totalAmount

        ElMessage.success('充值成功！')

        setTimeout(() => {
          handleQrCodeDialogClose()
        }, 3000)
      }
    }
  } catch (error) {
    console.error('查询支付状态失败', error)
  } finally {
    if (paymentStatus.value !== 'success') {
      paymentStatus.value = 'waiting'
    }
  }
}

function getLevelName(levelValue: number): string {
  const levelMap: Record<number, string> = {
    1: '银卡',
    2: '金卡',
    3: '钻石'
  }
  return levelMap[levelValue] || '普通'
}

function getLevelTagType(levelValue: number): string {
  const tagTypeMap: Record<number, string> = {
    1: 'success',
    2: 'warning',
    3: 'danger'
  }
  return tagTypeMap[levelValue] || 'info'
}

function formatBenefitValue(benefit: MemberBenefit): string {
  const value = benefit.value || ''

  switch (benefit.benefitCode) {
    case 'BOOKING_DISCOUNT':
      return value + '折'
    case 'PRIORITY_BOOKING':
      return value + '天'
    case 'FREE_TIMES':
      return value + '次'
    case 'POINTS_DOUBLE':
      return value + '倍'
    default:
      return value
  }
}

function handleRechargeDialogClose() {
  selectedAmount.value = 0
  customAmount.value = ''
}

function handleQrCodeDialogClose() {
  stopPolling()
  showPayQrCode.value = false
  qrCodeUrl.value = ''
  currentOrderNo.value = ''
  paymentStatus.value = 'waiting'
  rechargeOrderInfo.value = {
    orderNo: '',
    qrCodeUrl: '',
    expireTime: '',
    amount: 0,
    bonusAmount: 0,
    actualAmount: 0
  }
}

const geoStyle = (index: number) => {
  const shapes = ['circle', 'square', 'diamond']
  const shape = shapes[index % 3]
  const size = Math.random() * 50 + 30
  const left = Math.random() * 100
  const top = Math.random() * 100
  const delay = Math.random() * 4
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

async function loadRecentOrders() {
  try {
    const res = await getRecentOrders()
    if (res.code === 200 && res.data) {
      recentRecords.value = res.data
    }
  } catch (error) {
    console.error('加载最近订单记录失败', error)
  }
}

function formatRecordTime(timeStr: string): string {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  const now = new Date()
  const diff = now.getTime() - date.getTime()

  if (diff < 60000) return '刚刚'
  if (diff < 3600000) {
    const minutes = Math.floor(diff / 60000)
    return `${minutes}分钟前`
  }
  if (diff < 86400000) {
    const hours = Math.floor(diff / 3600000)
    return `${hours}小时前`
  }
  if (diff < 604800000) {
    const days = Math.floor(diff / 86400000)
    return `${days}天前`
  }

  const month = date.getMonth() + 1
  const day = date.getDate()
  return `${month}月${day}日`
}

onMounted(() => {
  loadMemberConfig()
  loadRecentOrders()
})

onUnmounted(() => {
  stopPolling()
})
</script>

<style scoped lang="scss">
.membership-page {
  min-height: 100vh;
  position: relative;
  overflow: hidden;
}

// ===================================
// BACKGROUND
// ===================================
.membership-bg {
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
    55deg,
    transparent,
    transparent 40px,
    rgba(255, 107, 107, 0.015) 40px,
    rgba(255, 107, 107, 0.015) 80px
  );
  animation: stripe-move 25s linear infinite;
}

@keyframes stripe-move {
  0% { background-position: 0 0; }
  100% { background-position: 100px 0; }
}

.geometric-layer {
  position: absolute;
  inset: 0;
}

.geo-shape {
  position: absolute;
  opacity: 0.05;
  background: linear-gradient(135deg, #FF6B6B 0%, #20C997 100%);
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
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-30px) rotate(10deg); }
}

.floating-orbs {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(40px);

  &.orb-1 {
    width: 200px;
    height: 200px;
    bottom: -50px;
    left: -50px;
    background: rgba(255, 107, 107, 0.15);
    animation: float-orb 15s ease-in-out infinite;
  }

  &.orb-2 {
    width: 150px;
    height: 150px;
    top: 30%;
    right: -30px;
    background: rgba(32, 201, 151, 0.12);
    animation: float-orb 18s ease-in-out infinite reverse;
  }

  &.orb-3 {
    width: 100px;
    height: 100px;
    top: 60%;
    left: 20%;
    background: rgba(255, 169, 77, 0.1);
    animation: float-orb 20s ease-in-out infinite;
  }
}

@keyframes float-orb {
  0%, 100% { transform: translateY(0) scale(1); }
  50% { transform: translateY(-20px) scale(1.05); }
}

// ===================================
// MAIN CONTENT
// ===================================
.membership-content {
  position: relative;
  z-index: 10;
  padding: 70px 16px 20px 16px;
}

// ===================================
// MEMBER CARD
// ===================================
.card-brutal {
  margin-bottom: 16px;
}

.member-card-brutal {
  position: relative;
  border-radius: 16px;
  padding: 24px;
  color: #fff;
  overflow: hidden;
  box-shadow: 8px 8px 0 var(--color-obsidian);
  cursor: pointer;
  transition: all 0.15s ease-out;

  &:active {
    transform: translate(2px, 2px);
    box-shadow: 4px 4px 0 var(--color-obsidian);
  }

  &.card-1 {
    background: linear-gradient(135deg, #20C997 0%, #0CA678 100%);
  }

  &.card-2 {
    background: linear-gradient(135deg, #FFA94D 0%, #FF6B6B 100%);
  }

  &.card-3 {
    background: linear-gradient(135deg, #FF6B6B 0%, #EE5A6F 100%);
  }

  &.card-4 {
    background: linear-gradient(135deg, #6366F1 0%, #8B5CF6 100%);
  }
}

.card-shine {
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: linear-gradient(
    45deg,
    transparent 30%,
    rgba(255, 255, 255, 0.15) 50%,
    transparent 70%
  );
  animation: shine 4s infinite;
}

@keyframes shine {
  0% { transform: translateX(-100%) translateY(-100%) rotate(45deg); }
  100% { transform: translateX(100%) translateY(100%) rotate(45deg); }
}

.card-pattern {
  position: absolute;
  inset: 0;
  opacity: 0.05;
  background-image: radial-gradient(circle, #fff 1px, transparent 1px);
  background-size: 16px 16px;
}

.card-header-brutal {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.level-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.level-icon-brutal {
  width: 52px;
  height: 52px;
  background: rgba(255, 255, 255, 0.2);
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(10px);
}

.level-info-brutal {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.level-name {
  font-family: var(--font-display);
  font-size: 20px;
  font-weight: 700;
  letter-spacing: -0.3px;
}

.level-en {
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0.15em;
  text-transform: uppercase;
  opacity: 0.9;
}

.card-no-brutal {
  font-family: 'Courier New', monospace;
  font-size: 12px;
  opacity: 0.8;
  letter-spacing: 0.1em;
}

.card-body-brutal {
  display: flex;
  flex-direction: column;
  gap: 14px;
  margin-bottom: 16px;
}

.card-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-label {
  font-size: 13px;
  opacity: 0.85;
  font-weight: 500;
}

.card-value {
  font-family: var(--font-body);
  font-size: 18px;
  font-weight: 600;

  &.balance-highlight {
    font-family: var(--font-display);
    font-size: 22px;
    font-weight: 700;
  }
}

.card-footer-brutal {
  border-top: 2px solid rgba(255, 255, 255, 0.2);
  padding-top: 12px;

  span {
    font-size: 12px;
    opacity: 0.85;
    font-weight: 500;
  }
}

// ===================================
// QUICK ACTIONS
// ===================================
.actions-brutal {
  display: flex;
  justify-content: space-around;
  background: #fff;
  border: 3px solid var(--color-obsidian);
  border-radius: 14px;
  box-shadow: 6px 6px 0 var(--color-obsidian);
  padding: 16px;
  margin-bottom: 16px;
}

.action-brutal {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  transition: all 0.15s ease-out;

  &:active {
    transform: scale(0.95);
  }
}

.action-icon-brutal {
  width: 52px;
  height: 52px;
  background: linear-gradient(135deg, rgba(255, 107, 107, 0.12) 0%, rgba(255, 169, 77, 0.12) 100%);
  border: 2px solid var(--color-obsidian);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-obsidian);
  box-shadow: 3px 3px 0 var(--color-obsidian);
}

.action-label-brutal {
  font-family: var(--font-body);
  font-size: 13px;
  font-weight: 600;
  color: var(--color-obsidian);
}

// ===================================
// BENEFITS SECTION
// ===================================
.benefits-brutal {
  margin-bottom: 16px;
}

.section-title-brutal {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 14px;
}

.title-icon {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #FFA94D 0%, #FF6B6B 100%);
  border: 2px solid var(--color-obsidian);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  box-shadow: 3px 3px 0 rgba(11, 37, 69, 0.2);
}

.section-title-brutal h3 {
  font-family: var(--font-display);
  font-size: 18px;
  font-weight: 700;
  color: var(--color-obsidian);
  margin: 0;
}

.benefits-list-brutal {
  background: #fff;
  border: 3px solid var(--color-obsidian);
  border-radius: 14px;
  box-shadow: 6px 6px 0 var(--color-obsidian);
  overflow: hidden;
}

.benefit-brutal {
  display: flex;
  align-items: center;
  padding: 14px 16px;
  border-bottom: 2px solid var(--color-sand);

  &:last-child {
    border-bottom: none;
  }

  &:active {
    background: var(--color-cream);
  }
}

.benefit-icon-brutal {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, rgba(255, 107, 107, 0.15) 0%, rgba(255, 169, 77, 0.15) 100%);
  border: 2px solid var(--color-obsidian);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-primary);
  margin-right: 12px;
  flex-shrink: 0;
}

.benefit-content-brutal {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.benefit-name-brutal {
  font-family: var(--font-body);
  font-size: 14px;
  font-weight:  600;
  color: var(--color-obsidian);
}

.benefit-desc-brutal {
  font-size: 11px;
  color: var(--color-taupe);
}

.benefit-tag-brutal {
  flex-shrink: 0;

  .tag-value {
    font-family: var(--font-body);
    font-size: 11px;
    font-weight: 700;
    color: var(--color-obsidian);
    background: var(--color-cream);
    border: 2px solid var(--color-obsidian);
    padding: 4px 8px;
    border-radius: 4px;
  }
}

// ===================================
// RECORDS SECTION
// ===================================
.records-brutal {
  margin-bottom: 16px;
}

.records-header-brutal {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
}

.view-all-brutal {
  font-family: var(--font-body);
  font-size: 13px;
  font-weight:  600;
  color: #fff;
  background: linear-gradient(135deg, #20C997 0%, #12B886 100%);
  border: 2px solid var(--color-obsidian);
  border-radius: 8px;
  padding: 6px 14px;
  box-shadow: 3px 3px 0 var(--color-obsidian);
  cursor: pointer;
  transition: all 0.15s ease-out;

  &:active {
    transform: translate(1px, 1px);
    box-shadow: 2px 2px 0 var(--color-obsidian);
  }
}

.records-list-brutal {
  background: #fff;
  border: 3px solid var(--color-obsidian);
  border-radius: 14px;
  box-shadow: 6px 6px 0 var(--color-obsidian);
  overflow: hidden;
}

.record-brutal {
  display: flex;
  align-items: center;
  padding: 14px 16px;
  border-bottom: 2px solid var(--color-sand);

  &:last-child {
    border-bottom: none;
  }

  &:active {
    background: var(--color-cream);
  }
}

.record-icon-brutal {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, rgba(32, 201, 151, 0.15) 0%, rgba(12, 184, 104, 0.15) 100%);
  border: 2px solid var(--color-obsidian);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-secondary);
  margin-right: 12px;
  flex-shrink: 0;
}

.record-content-brutal {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.record-title-brutal {
  font-family: var(--font-body);
  font-size: 14px;
  font-weight: 600;
  color: var(--color-obsidian);
}

.record-time-brutal {
  font-size: 11px;
  color: var(--color-taupe);
}

.record-amount-brutal {
  font-family: var(--font-body);
  font-size: 14px;
  font-weight: 700;
}

.amount-increase { color: #20C997; }
.amount-decrease { color: #FF6B6B; }

.empty-records-brutal {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  background: #fff;
  border: 3px solid var(--color-obsidian);
  border-radius: 14px;
  box-shadow: 6px 6px 0 var(--color-obsidian);

  .empty-icon-brutal {
    color: var(--color-taupe);
    opacity: 0.3;
    margin-bottom: 12px;
  }

  p {
    font-family: var(--font-body);
    font-size: 14px;
    color: var(--color-taupe);
    margin: 0;
  }
}

// ===================================
// RECHARGE DIALOG
// ===================================
:deep(.recharge-brutal-dialog) {
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
    display: flex;
    gap: 12px;

    .el-button {
      flex: 1;
      height: 48px;
      font-size: 15px;
      font-weight: 600;
      border-radius: 10px;
      border: 2px solid var(--color-obsidian);
    }
  }
}

.recharge-brutal-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.balance-display-brutal {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: var(--color-cream);
  border: 2px solid var(--color-obsidian);
  border-radius: 10px;
}

.balance-label {
  font-family: var(--font-body);
  font-size: 13px;
  font-weight: 500;
  color: var(--color-taupe);
}

.balance-value {
  font-family: var(--font-display);
  font-size: 20px;
  font-weight: 700;
  color: var(--color-obsidian);
}

.amount-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.section-label-brutal {
  font-family: var(--font-body);
  font-size: 13px;
  font-weight: 700;
  color: var(--color-obsidian);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.amount-loading-brutal {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  background: var(--color-cream);
  border: 2px solid var(--color-obsidian);
  border-radius: 10px;
  gap: 12px;
  color: var(--color-taupe);
}

.loading-spinner-brutal {
  width: 24px;
  height: 24px;
  border: 3px solid var(--color-obsidian);
  border-top-color: var(--color-primary);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.amount-grid-brutal {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
}

.amount-brutal {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 16px 10px;
  background: var(--color-cream);
  border: 2px solid var(--color-obsidian);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.15s ease-out;

  &:active {
    transform: scale(0.97);
  }

  &.active {
    background: linear-gradient(135deg, rgba(255, 107, 107, 0.15) 0%, rgba(255, 169, 77, 0.15) 100%);
    border-color: var(--color-primary);
  }
}

.amount-value-brutal {
  font-family: var(--font-display);
  font-size: 18px;
  font-weight: 700;
  color: var(--color-obsidian);
}

.amount-bonus-brutal {
  font-size: 10px;
  font-weight: 700;
  color: var(--color-warning);
  margin-top: 4px;
  text-transform: uppercase;
}

.custom-amount-brutal {
  :deep(.custom-input-brutal) {
    .el-input__wrapper {
      background: #fff;
      border: 2px solid var(--color-obsidian);
      border-radius: 10px;
      box-shadow: none;
      padding: 0 12px;
    }

    .el-input__inner {
      font-family: var(--font-body);
      font-size: 15px;
      color: var(--color-obsidian);
    }
  }
}

.payment-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.payment-grid-brutal {
  display: flex;
  gap: 10px;
}

.payment-brutal {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 16px 10px;
  background: var(--color-cream);
  border: 2px solid var(--color-obsidian);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.15s ease-out;
  gap: 8px;

  &:active {
    transform: scale(0.97);
  }

  &.active {
    background: linear-gradient(135deg, rgba(32, 201, 151, 0.15) 0%, rgba(12, 184, 104, 0.15) 100%);
    border-color: var(--color-secondary);
  }

  &.disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }

  span {
    font-family: var(--font-body);
    font-size: 12px;
    font-weight: 600;
    color: var(--color-obsidian);
  }
}

.dialog-cancel-btn {
  background: #fff;
  color: var(--color-obsidian);

  &:hover {
    background: var(--color-cream);
  }
}

.dialog-confirm-btn {
  background: linear-gradient(135deg, #20C997 0%, #12B886 100%);
  border-color: var(--color-secondary);
  color: #fff;

  &:hover:not(:disabled) {
    background: linear-gradient(135deg, #12B886 0%, #0CA678 100%);
  }
}

// ===================================
// QR CODE DIALOG
// ===================================
:deep(.qrcode-brutal-dialog) {
  .el-dialog {
    border: 3px solid var(--color-obsidian);
    border-radius: 16px;
    box-shadow: 8px 8px 0 var(--color-obsidian);
  }

  .el-dialog__header {
    background: linear-gradient(135deg, #20C997 0%, #12B886 100%);
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
    display: flex;
    gap: 12px;

    .el-button {
      flex: 1;
      height: 48px;
      font-size: 15px;
      font-weight: 600;
      border-radius: 10px;
      border: 2px solid var(--color-obsidian);
    }
  }
}

.qrcode-brutal-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.qrcode-info-brutal {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 16px;
  background: var(--color-cream);
  border: 2px solid var(--color-obsidian);
  border-radius: 10px;
}

.info-row-brutal {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.info-label {
  font-family: var(--font-body);
  font-size: 13px;
  font-weight: 500;
  color: var(--color-taupe);
}

.info-value {
  font-family: var(--font-body);
  font-size: 14px;
  font-weight: 600;
  color: var(--color-obsidian);

  &.amount-highlight {
    font-family: var(--font-display);
    font-size: 16px;
    font-weight: 700;
    color: var(--color-primary);
  }

  &.bonus-highlight {
    font-size: 16px;
    font-weight: 700;
    color: var(--color-warning);
  }

  &.total-highlight {
    font-family: var(--font-display);
    font-size: 18px;
    font-weight: 700;
    color: var(--color-secondary);
  }
}

.qrcode-display-brutal {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 260px;
  padding: 20px;
  background: var(--color-cream);
  border: 2px solid var(--color-obsidian);
  border-radius: 10px;
}

.qrcode-loading-brutal {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  color: var(--color-taupe);
}

.qrcode-image-brutal {
  display: flex;
  justify-content: center;
  align-items: center;

  img {
    width: 220px;
    height: 220px;
    border-radius: 10px;
    border: 3px solid var(--color-obsidian);
    background: #fff;
    padding: 8px;
  }
}

.payment-tips-brutal {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px;
  background: var(--color-cream);
  border: 2px solid var(--color-obsidian);
  border-radius: 8px;

  .el-icon {
    color: var(--color-secondary);
  }

  span {
    font-family: var(--font-body);
    font-size: 12px;
    font-weight: 500;
    color: var(--color-obsidian);
  }
}

.payment-status-brutal {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 50px;
}

.status-checking,
.status-waiting,
.status-success {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 15px;
  font-weight: 500;
}

.status-checking { color: var(--color-warning); }
.status-waiting { color: var(--color-taupe); }
.status-success {
  color: var(--color-secondary);
  font-size: 18px;
  font-weight: 700;
}

// ===================================
// RULES DIALOG
// ===================================
:deep(.rules-brutal-dialog) {
  .el-dialog {
    border: 3px solid var(--color-obsidian);
    border-radius: 16px;
    box-shadow: 8px 8px 0 var(--color-obsidian);
  }

  .el-dialog__header {
    background: linear-gradient(135deg, #6366F1 0%, #8B5CF6 100%);
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
    max-height: 60vh;
    overflow-y: auto;
  }

  .el-dialog__footer {
    padding: 16px 20px;
    border-top: 2px solid var(--color-sand);
  }
}

.rules-brutal-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.rule-section-brutal {
  background: var(--color-cream);
  border: 2px solid var(--color-obsidian);
  border-radius: 10px;
  padding: 16px;
}

.rule-title-brutal {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;

  .title-icon {
    width: 32px;
    height: 32px;
    background: linear-gradient(135deg, #FFA94D 0%, #FF6B6B 100%);
    border: 2px solid var(--color-obsidian);
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    box-shadow: 2px 2px 0 rgba(11, 37, 69, 0.2);
  }

  h3 {
    font-family: var(--font-display);
    font-size: 16px;
    font-weight: 700;
    color: var(--color-obsidian);
    margin: 0;
  }
}

.rule-description-brutal {
  background: #fff;
  border: 2px solid var(--color-sand);
  border-radius: 8px;
  padding: 12px;

  p {
    font-family: var(--font-body);
    font-size: 13px;
    color: var(--color-charcoal);
    margin: 0;
    line-height: 1.6;

    &:not(:last-child) {
      margin-bottom: 8px;
    }
  }
}

.levels-list-brutal {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.level-brutal {
  background: #fff;
  border: 2px solid var(--color-sand);
  border-radius: 8px;
  padding: 12px 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-left: 4px solid var(--color-secondary);
}

.level-info-brutal {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.level-name-brutal {
  font-family: var(--font-display);
  font-size: 15px;
  font-weight: 600;
  color: var(--color-obsidian);
}

.level-desc-brutal {
  font-size: 12px;
  color: var(--color-taupe);
}

.level-requirement-brutal {
  display: flex;
  align-items: center;
  gap: 6px;
}

.requirement-label {
  font-size: 12px;
  color: var(--color-taupe);
}

.requirement-value {
  font-size: 13px;
  font-weight: 700;
  color: var(--color-warning);
}

.benefits-grid-brutal {
  display: grid;
  grid-template-columns: 1fr;
  gap: 10px;
}

.rule-benefit-brutal {
  background: #fff;
  border: 2px solid var(--color-sand);
  border-radius: 8px;
  padding: 12px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.benefit-icon-brutal {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, rgba(255, 107, 107, 0.15) 0%, rgba(255, 169, 77, 0.15) 100%);
  border: 2px solid var(--color-obsidian);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-primary);
}

.benefit-info-brutal {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.benefit-header-brutal {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 2px;
}

.benefit-name-brutal {
  font-family: var(--font-body);
  font-size: 14px;
  font-weight: 500;
  color: var(--color-obsidian);
}

.benefit-desc-brutal {
  font-size: 11px;
  color: var(--color-taupe);
}

.benefit-value-brutal {
  .value-tag {
    font-family: var(--font-body);
    font-size: 11px;
    font-weight: 700;
    color: var(--color-obsidian);
    background: var(--color-cream);
    border: 2px solid var(--color-obsidian);
    padding: 3px 8px;
    border-radius: 4px;
  }
}
</style>
