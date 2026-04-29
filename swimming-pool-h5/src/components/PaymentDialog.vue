<template>
  <el-dialog
    v-model="visible"
    title="确认支付"
    width="90%"
    :close-on-click-modal="false"
    class="custom-dialog payment-dialog"
    @close="handleClose"
  >
    <div v-loading="loading" class="payment-content">
      <div v-if="paymentInfo" class="payment-sections">
        <!-- 订单信息 -->
        <div class="section-card">
          <div class="section-title">
            <el-icon><Document /></el-icon>
            <span>订单信息</span>
          </div>
          <div class="order-details">
            <div class="detail-row">
              <span class="label">订单号</span>
              <span class="value">{{ paymentInfo.orderNo }}</span>
            </div>
            <div class="detail-row">
              <span class="label">订单金额</span>
              <span class="value amount-original">¥{{ formatMoney(paymentInfo.amount) }}</span>
            </div>
            <div v-if="paymentInfo.discountAmount > 0" class="detail-row">
              <span class="label">会员折扣</span>
              <span class="value discount">{{ paymentInfo.bookingDiscount / 10 }}折</span>
            </div>
            <div v-if="paymentInfo.discountAmount > 0" class="detail-row">
              <span class="label">优惠金额</span>
              <span class="value discount">-¥{{ formatMoney(paymentInfo.discountAmount) }}</span>
            </div>
            <div class="detail-row total-row">
              <span class="label">应付金额</span>
              <span class="value amount-final">¥{{ formatMoney(paymentInfo.actualAmount) }}</span>
            </div>
          </div>
        </div>

        <!-- 余额信息 -->
        <div class="section-card">
          <div class="section-title">
            <el-icon><Wallet /></el-icon>
            <span>账户余额</span>
          </div>
          <div class="balance-details">
            <div class="balance-item">
              <span class="balance-label">余额</span>
              <span class="balance-value" :class="{ 'balance-low': paymentInfo.balance < paymentInfo.actualAmount }">
                ¥{{ formatMoney(paymentInfo.balance) }}
              </span>
            </div>
            <div class="balance-item">
              <span class="balance-label">积分余额</span>
              <span class="balance-value">{{ paymentInfo.totalPoint }} 分</span>
            </div>
          </div>
        </div>

        <!-- 会员权益 -->
        <div class="section-card">
          <div class="section-title">
            <el-icon><Star /></el-icon>
            <span>{{ paymentInfo.cardLevelName }}权益</span>
          </div>
          <div class="benefits-grid">
            <div class="benefit-card">
              <div class="benefit-icon">🎟️</div>
              <div class="benefit-info">
                <div class="benefit-name">免费次数</div>
                <div class="benefit-value">{{ paymentInfo.freeTimes }} 次</div>
              </div>
            </div>
            <div class="benefit-card">
              <div class="benefit-icon">✨</div>
              <div class="benefit-info">
                <div class="benefit-name">积分倍数</div>
                <div class="benefit-value">{{ paymentInfo.pointsDouble }} 倍</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 使用免费次数 -->
        <div v-if="paymentInfo.freeTimes > 0" class="section-card">
          <div class="free-times-option">
            <el-checkbox v-model="useFreeTimes" size="large" :disabled="paying">
              使用免费次数支付
            </el-checkbox>
            <div class="option-hint">使用免费次数支付后，本次支付将获得0积分</div>
          </div>
        </div>

        <!-- 获得积分 -->
        <div class="section-card highlight-card">
          <div class="point-info">
            <el-icon class="point-icon"><Medal /></el-icon>
            <div class="point-text">
              <div class="point-label">支付后可获得积分</div>
              <div class="point-value">+{{ useFreeTimes ? 0 : calculatedPoints }} 分</div>
              <div v-if="useFreeTimes" class="point-hint">使用免费次数支付，获得0积分</div>
              <div v-else class="point-hint">消费¥{{ formatMoney(paymentInfo.actualAmount) }} × {{ paymentInfo.pointsDouble }}倍</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose" size="large" :disabled="paying">取消</el-button>
        <el-button
          type="primary"
          @click="handleConfirmPay"
          :loading="paying"
          :disabled="isPayDisabled"
          size="large"
        >
          {{ payButtonText }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Document, Wallet, Star, Medal } from '@element-plus/icons-vue'
import { getPaymentInfo, balancePay, type PaymentInfo } from '@/api/payment'

interface Props {
  modelValue: boolean
  orderId: number
}

interface Emits {
  (e: 'update:modelValue', value: boolean): void
  (e: 'success'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const visible = ref(false)
const loading = ref(false)
const paying = ref(false)
const paymentInfo = ref<PaymentInfo | null>(null)
const useFreeTimes = ref(false)

// 计算可获得的积分
const calculatedPoints = computed(() => {
  if (!paymentInfo.value) return 0
  return Math.floor(paymentInfo.value.actualAmount * paymentInfo.value.pointsDouble)
})

// 判断支付按钮是否禁用
const isPayDisabled = computed(() => {
  if (!paymentInfo.value) return true
  if (useFreeTimes.value) {
    return paymentInfo.value.freeTimes < 1
  }
  return paymentInfo.value.balance < paymentInfo.value.actualAmount
})

// 支付按钮文字
const payButtonText = computed(() => {
  if (!paymentInfo.value) return '确认支付'
  if (useFreeTimes.value) {
    if (paymentInfo.value.freeTimes < 1) return '免费次数不足'
    return '确认使用免费次数'
  }
  if (paymentInfo.value.balance < paymentInfo.value.actualAmount) return '余额不足'
  return '确认支付'
})

// 监听外部v-model变化
watch(() => props.modelValue, (newVal) => {
  visible.value = newVal
  if (newVal && props.orderId) {
    loadPaymentInfo()
  }
})

// 监听内部visible变化，同步到外部
watch(visible, (newVal) => {
  emit('update:modelValue', newVal)
  if (!newVal) {
    // 关闭弹窗时重置选项
    useFreeTimes.value = false
  }
})

async function loadPaymentInfo() {
  if (!props.orderId) return

  loading.value = true
  try {
    const response = await getPaymentInfo(props.orderId)
    if (response.code === 200) {
      paymentInfo.value = response.data
      // 重置选项
      useFreeTimes.value = false
    } else {
      ElMessage.error(response.msg || '获取支付信息失败')
    }
  } catch (error) {
    console.error('获取支付信息失败:', error)
    ElMessage.error('获取支付信息失败')
  } finally {
    loading.value = false
  }
}

async function handleConfirmPay() {
  if (!props.orderId) return

  paying.value = true
  try {
    const response = await balancePay(props.orderId, useFreeTimes.value)
    if (response.code === 200) {
      ElMessage.success('支付成功')
      visible.value = false
      emit('success')
    } else {
      ElMessage.error(response.msg || '支付失败')
    }
  } catch (error) {
    console.error('支付失败:', error)
    ElMessage.error('支付失败')
  } finally {
    paying.value = false
  }
}

function handleClose() {
  visible.value = false
}

function formatMoney(amount: number): string {
  return amount.toFixed(2)
}
</script>

<style scoped lang="scss">
.payment-dialog {
  :deep(.el-dialog__body) {
    padding: 20px;
    max-height: 60vh;
    overflow-y: auto;
  }
}

.payment-content {
  min-height: 200px;
}

.payment-sections {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.section-card {
  background: #F9FAFB;
  border-radius: 12px;
  padding: 16px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #0A4D68;
  margin-bottom: 12px;

  .el-icon {
    color: #05BFDB;
  }
}

.order-details {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.detail-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;

  .label {
    color: #6B7280;
  }

  .value {
    color: #0A4D68;
    font-weight: 500;

    &.amount-original {
      color: #9CA3AF;
      text-decoration: line-through;
    }

    &.discount {
      color: #EF4444;
    }
  }

  &.total-row {
    padding-top: 8px;
    border-top: 1px solid #E5E7EB;
    margin-top: 4px;

    .label {
      font-size: 16px;
      font-weight: 600;
      color: #0A4D68;
    }

    .value.amount-final {
      font-size: 20px;
      font-weight: 700;
      color: #10B981;
    }
  }
}

.balance-details {
  display: flex;
  justify-content: space-around;
  gap: 16px;
}

.balance-item {
  flex: 1;
  text-align: center;
  padding: 12px;
  background: #fff;
  border-radius: 10px;

  .balance-label {
    display: block;
    font-size: 13px;
    color: #6B7280;
    margin-bottom: 4px;
  }

  .balance-value {
    display: block;
    font-size: 18px;
    font-weight: 600;
    color: #10B981;

    &.balance-low {
      color: #EF4444;
    }
  }
}

.benefits-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.benefit-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px;
  background: #fff;
  border-radius: 10px;
  border: 2px solid #E5E7EB;
  transition: all 0.3s;
}

.benefit-icon {
  font-size: 28px;
  flex-shrink: 0;
}

.benefit-info {
  flex: 1;
}

.benefit-name {
  font-size: 13px;
  color: #6B7280;
  margin-bottom: 4px;
}

.benefit-value {
  font-size: 18px;
  font-weight: 600;
  color: #05BFDB;
}

.free-times-option {
  display: flex;
  flex-direction: column;
  gap: 8px;

  :deep(.el-checkbox) {
    .el-checkbox__label {
      font-size: 15px;
      font-weight: 500;
      color: #0A4D68;
    }

    &.is-checked {
      .el-checkbox__label {
        color: #05BFDB;
      }
    }
  }
}

.option-hint {
  font-size: 12px;
  color: #9CA3AF;
  padding-left: 24px;
}

.highlight-card {
  background: linear-gradient(135deg, rgba(5, 191, 219, 0.1) 0%, rgba(16, 185, 129, 0.1) 100%);
  border: 2px solid rgba(5, 191, 219, 0.3);
}

.point-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.point-icon {
  font-size: 32px;
  color: #F59E0B;
}

.point-text {
  flex: 1;
}

.point-label {
  font-size: 13px;
  color: #6B7280;
  margin-bottom: 4px;
}

.point-value {
  font-size: 24px;
  font-weight: 700;
  color: #F59E0B;
}

.point-hint {
  font-size: 12px;
  color: #9CA3AF;
  margin-top: 2px;
}

.dialog-footer {
  display: flex;
  gap: 12px;

  .el-button {
    flex: 1;
    font-size: 16px;
    font-weight: 600;
  }
}
</style>
