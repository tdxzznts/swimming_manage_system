import request from '@/utils/request'

/**
 * IoT设备告警 API
 */

// 查询IoT设备告警列表
export function listAlarm(query?: any) {
  return request.get('/swimming/iot/alarm/list', query)
}

// 查询IoT设备告警详细
export function getAlarm(id: number) {
  return request.get('/swimming/iot/alarm/' + id)
}

// 新增IoT设备告警
export function addAlarm(data: any) {
  return request.post('/swimming/iot/alarm', data)
}

// 修改IoT设备告警
export function updateAlarm(data: any) {
  return request.put('/swimming/iot/alarm', data)
}

// 删除IoT设备告警
export function delAlarm(id: number) {
  return request.delete('/swimming/iot/alarm/' + id)
}

// 导出IoT设备告警
export function exportAlarm(query: any) {
  return request.post('/swimming/iot/alarm/export', query)
}

// 获取告警统计信息
export function getAlarmStatistics() {
  return request.get('/swimming/iot/alarm/statistics')
}

// 处理告警
export function handleAlarm(alarmId: number, handleResult: string) {
  return request.put('/swimming/iot/alarm/handle/' + alarmId, handleResult)
}

// 批量处理告警
export function batchHandleAlarms(alarmIds: number[], handleResult: string) {
  return request.put('/swimming/iot/alarm/batchHandle', { alarmIds, handleResult })
}
