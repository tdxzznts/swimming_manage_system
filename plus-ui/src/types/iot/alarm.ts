/**
 * IoT设备告警类型定义
 */

/**
 * IoT设备告警对象
 */
export interface IotDeviceAlarm {
  id?: number
  deviceKey: string
  deviceName?: string
  alarmType: string
  alarmLevel: 'info' | 'warning' | 'critical'
  alarmTitle: string
  alarmMessage: string
  alarmData?: string
  isHandled: '0' | '1' // 0未处理 1已处理
  handleUserId?: number
  handleUserName?: string
  handleTime?: string
  handleResult?: string
  createTime: string
}

/**
 * 告警查询参数
 */
export interface AlarmQuery extends PageQuery {
  deviceKey?: string
  alarmType?: string
  alarmLevel?: string
  isHandled?: string
}

/**
 * 告警统计信息
 */
export interface AlarmStatistics {
  totalCount: number
  unhandledCount: number
  todayCount: number
  criticalCount: number
}
