import request from '@/utils/request'

/**
 * IoT设备 API
 */

// 查询IoT设备列表
export function listDevice(query?: any) {
  return request.get('/swimming/iot/device/list', query)
}

// 查询IoT设备详细
export function getDevice(id: number) {
  return request.get('/swimming/iot/device/' + id)
}

// 新增IoT设备
export function addDevice(data: any) {
  return request.post('/swimming/iot/device', data)
}

// 修改IoT设备
export function updateDevice(data: any) {
  return request.put('/swimming/iot/device', data)
}

// 删除IoT设备
export function delDevice(id: number) {
  return request.delete('/swimming/iot/device/' + id)
}

// 导出IoT设备
export function exportDevice(query: any) {
  return request.post('/swimming/iot/device/export', query)
}

// 获取设备统计信息
export function getDeviceStatistics() {
  return request.get('/swimming/iot/device/statistics')
}

// 更新设备配置
export function updateDeviceConfig(deviceKey: string, shadow: any) {
  return request.put('/swimming/iot/device/config/' + deviceKey, shadow)
}

/**
 * IoT设备数据 API
 */

// 获取设备最新数据
export function getLatestData(deviceKey: string) {
  return request.get('/swimming/iot/data/latest/' + encodeURIComponent(deviceKey))
}

// 查询设备历史数据
export function getHistoryData(query: any) {
  return request.get('/swimming/iot/data/history', { params: query })
}

// 获取设备数据统计
export function getDataStatistics(query: any) {
  return request.get('/swimming/iot/data/statistics', { params: query })
}

/**
 * IoT设备告警 API
 */

// 查询IoT设备告警列表
export function listAlarm(query?: any) {
  return request.get('/swimming/iot/alarm/list', { params: query })
}

// 获取告警统计信息
export function getAlarmStatistics() {
  return request.get('/swimming/iot/alarm/statistics')
}

// 处理告警
export function handleAlarm(alarmId: number, handleResult: string) {
  return request.put('/swimming/iot/alarm/handle/' + alarmId, handleResult, {
    headers: {
      'Content-Type': 'text/plain'
    }
  })
}
