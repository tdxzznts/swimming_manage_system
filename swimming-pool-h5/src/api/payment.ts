import { request } from '@/utils/request'

/**
 * 支付信息接口
 */
export interface PaymentInfo {
  orderId: number
  orderNo: string
  reservationId: number
  amount: number
  discount: number
  discountAmount: number
  actualAmount: number
  getPoint: number
  balance: number
  totalPoint: number
  cardLevel: string
  cardLevelName: string
  freeTimes: number
  pointsDouble: number
  bookingDiscount: number
}

/**
 * 获取支付信息
 */
export function getPaymentInfo(orderId: number): Promise<R<PaymentInfo>> {
  return request.get<PaymentInfo>(`/h5/payment/info/${orderId}`)
}

/**
 * 余额支付
 * @param orderId 订单ID
 * @param useFreeTimes 是否使用免费次数支付
 */
export function balancePay(orderId: number, useFreeTimes: boolean = false): Promise<R<void>> {
  return request.post<void>(`/h5/payment/balance-pay/${orderId}`, null, {
    params: { useFreeTimes }
  })
}
