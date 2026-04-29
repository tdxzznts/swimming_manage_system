import { request } from '@/utils/request'
import type { R } from '@/types/user'

/**
 * 订单记录VO
 */
export interface OrderRecord {
  id: number
  orderNo: string
  orderType: string
  orderTypeName: string
  amount: number
  discountAmount: number
  actualAmount: number
  payType: string
  payTypeName: string
  payStatus: string
  payStatusName: string
  payTime: string
  createTime: string
  iconName: string
  amountText: string
  amountClass: string
}

/**
 * 获取最近订单记录
 */
export function getRecentOrders(): Promise<R<OrderRecord[]>> {
  return request.get<R<OrderRecord[]>>('/h5/order/recent')
}

/**
 * 获取订单记录列表
 */
export function getOrderList(orderType?: string): Promise<R<OrderRecord[]>> {
  if (orderType && orderType.trim()) {
    return request.get<R<OrderRecord[]>>('/h5/order/list', {
      orderType
    })
  }
  return request.get<R<OrderRecord[]>>('/h5/order/list')
}
