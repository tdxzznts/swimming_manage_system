/**
 * IoT设备类型定义
 */

/**
 * IoT设备对象
 */
export interface IotDevice {
  id?: number
  productKey: string
  productName?: string
  deviceName: string
  deviceKey: string
  deviceSecret?: string
  mac?: string
  deviceType?: string
  firmwareVersion?: string
  status: '0' | '1' | '3' // 0离线 1在线 3禁用
  lastOnlineTime?: string
  activeTime?: string
  location?: string
  createTime?: string
  updateTime?: string
  remark?: string
}

/**
 * 设备查询参数
 */
export interface DeviceQuery extends PageQuery {
  productKey?: string
  deviceName?: string
  status?: string
  deviceType?: string
}

/**
 * 设备统计信息
 */
export interface DeviceStatistics {
  totalCount: number
  onlineCount: number
  offlineCount: number
  todayCount: number
  onlineRate: string
}

/**
 * 设备数据对象
 */
export interface DeviceData {
  id: number
  deviceKey: string
  deviceName?: string
  method: string
  params: Record<string, any>
  timestamp: number
  collectTime: string
}

/**
 * 历史数据查询参数
 */
export interface HistoryDataQuery {
  deviceKey: string
  startTime: string
  endTime: string
  pageNum?: number
  pageSize?: number
}
