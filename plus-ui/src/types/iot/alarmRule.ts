/**
 * IoT告警规则类型定义
 */

/**
 * 告警规则对象
 */
export interface AlarmRule {
  id?: number
  deviceKey: string
  deviceName?: string
  ruleName: string
  metricName: string
  metricUnit?: string
  thresholdMin?: number
  thresholdMax?: number
  alarmLevel: 'info' | 'warning' | 'critical'
  alarmTitle?: string
  alarmMessageTemplate?: string
  status: '0' | '1' // 0禁用 1启用
  remark?: string
  createTime?: string
  updateTime?: string
}

/**
 * 告警规则查询参数
 */
export interface AlarmRuleQuery extends PageQuery {
  deviceKey?: string
  ruleName?: string
  metricName?: string
  alarmLevel?: string
  status?: string
}

/**
 * 告警级别选项
 */
export interface AlarmLevelOption {
  label: string
  value: string
  color: string
}

/**
 * 常用告警指标
 */
export interface MetricOption {
  label: string
  value: string
  unit: string
}
