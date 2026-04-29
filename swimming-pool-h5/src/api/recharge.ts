import { request } from '@/utils/request'
import type { R } from '@/types/user'

/**
 * 充值金额配置
 */
export interface RechargeAmount {
  dictCode: string
  dictSort: number
  dictLabel: string
  dictValue: string
  dictType: string
  cssClass: string
  listClass: string
  isDefault: string
  createDept: number
  createBy: number
  createTime: string
  updateBy: number
  updateTime: string
  remark: string
}

/**
 * 创建充值订单请求
 */
export interface CreateRechargeOrder {
  amount: number // 充值金额
  bonusAmount: number // 赠送金额
  payType: string // 支付类型：alipay-支付宝
}

/**
 * 充值订单结果
 */
export interface RechargeOrderResult {
  orderNo: string // 订单号
  qrCodeUrl: string // 支付二维码URL
  expireTime: string // 过期时间
  amount: number // 充值金额（分）
  bonusAmount: number // 赠送金额（分）
  actualAmount: number // 实际支付金额（分）
}

/**
 * 订单状态结果
 */
export interface OrderStatusResult {
  orderNo: string // 订单号
  payStatus: string // 支付状态（0待支付 1已支付 2支付失败 3已退款）
  payStatusDesc: string // 支付状态描述
  payTime: string // 支付时间
  status: string // 订单状态（0待支付 1已支付 2已完成 3已取消 4已退款）
  statusDesc: string // 订单状态描述
}

/**
 * 获取充值金额配置
 */
export function getRechargeAmounts(): Promise<R<RechargeAmount[]>> {
  return request.get<R<RechargeAmount[]>>('/system/dict/data/type/sp_in_money_send')
}

/**
 * 创建充值订单
 */
export function createRechargeOrder(data: CreateRechargeOrder): Promise<R<RechargeOrderResult>> {
  return request.post<R<RechargeOrderResult>>('/h5/recharge/create', data)
}

/**
 * 查询订单状态
 */
export function getRechargeOrderStatus(orderNo: string): Promise<R<OrderStatusResult>> {
  return request.get<R<OrderStatusResult>>('/h5/recharge/orderStatus', {
    orderNo
  })
}