import request from '@/utils/request'
import { AlarmRule, AlarmRuleQuery } from '@/types/iot/alarmRule'

/**
 * 查询告警规则列表
 */
export function listAlarmRule(query: AlarmRuleQuery) {
  return request({
    url: '/swimming/iot/alarmRule/list',
    method: 'get',
    params: query
  })
}

/**
 * 查询告警规则详细
 */
export function getAlarmRule(id: number) {
  return request({
    url: `/swimming/iot/alarmRule/${id}`,
    method: 'get'
  })
}

/**
 * 新增告警规则
 */
export function addAlarmRule(data: AlarmRule) {
  return request({
    url: '/swimming/iot/alarmRule',
    method: 'post',
    data: data
  })
}

/**
 * 修改告警规则
 */
export function updateAlarmRule(data: AlarmRule) {
  return request({
    url: '/swimming/iot/alarmRule',
    method: 'put',
    data: data
  })
}

/**
 * 删除告警规则
 */
export function delAlarmRule(ids: number[]) {
  return request({
    url: `/swimming/iot/alarmRule/${ids}`,
    method: 'delete'
  })
}

/**
 * 导出告警规则
 */
export function exportAlarmRule(query: AlarmRuleQuery) {
  return request({
    url: '/swimming/iot/alarmRule/export',
    method: 'post',
    data: query
  })
}
