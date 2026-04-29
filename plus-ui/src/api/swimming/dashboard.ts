import request from '@/utils/request'

/**
 * 首页统计 API
 */

/**
 * 获取首页统计数据
 */
export function getDashboardStatistics() {
  return request.get('/swimming/dashboard/statistics')
}

/**
 * 获取充值收入趋势（近7天）
 */
export function getRechargeTrend() {
  return request.get('/swimming/dashboard/recharge-trend')
}

/**
 * 获取会员增长趋势（近7天）
 */
export function getMemberGrowthTrend() {
  return request.get('/swimming/dashboard/member-growth-trend')
}
