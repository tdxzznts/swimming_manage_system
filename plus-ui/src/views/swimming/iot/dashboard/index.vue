<template>
  <div class="iot-dashboard">
    <!-- 设备选择工具栏 -->
    <el-row :gutter="20" class="mb-4">
      <el-col :span="24">
        <el-card shadow="hover">
          <div class="device-selector-bar">
            <div class="selector-left">
              <el-icon :size="20"><Monitor /></el-icon>
              <span class="selector-label">选择设备：</span>
              <el-select
                v-model="selectedDeviceKey"
                placeholder="请选择设备"
                size="default"
                style="width: 300px"
                @change="handleDeviceChange"
              >
                <el-option
                  v-for="device in deviceList"
                  :key="device.deviceKey"
                  :label="`${device.deviceName} (${device.location || '未设置位置'})`"
                  :value="device.deviceKey"
                >
                  <div class="device-option">
                    <span class="device-option-name">{{ device.deviceName }}</span>
                    <span class="device-option-info">{{ device.location || '未设置位置' }}</span>
                  </div>
                </el-option>
              </el-select>
              <el-tag v-if="selectedDevice" :type="selectedDevice.status === '1' ? 'success' : 'info'" class="ml-2">
                {{ selectedDevice.status === '1' ? '在线' : '离线' }}
              </el-tag>
            </div>
            <div class="selector-right">
              <el-tag type="info">共 {{ deviceList.length }} 台设备</el-tag>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 顶部统计卡片 -->
    <el-row :gutter="20" class="mb-4">
      <el-col :span="6">
        <div class="stat-card ph">
          <div class="stat-header">
            <el-icon :size="32"><SuccessFilled /></el-icon>
            <span class="stat-title">pH值</span>
          </div>
          <div class="stat-value">{{ latestData.ph || '--' }}</div>
          <div class="stat-unit">标准范围: 6.5-8.5</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card chlorine">
          <div class="stat-header">
            <el-icon :size="32"><InfoFilled /></el-icon>
            <span class="stat-title">余氯</span>
          </div>
          <div class="stat-value">{{ latestData.chlorine || '--' }} <span class="unit">mg/L</span></div>
          <div class="stat-unit">标准范围: 0.3-1.5</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card temperature">
          <div class="stat-header">
            <el-icon :size="32"><Sunny /></el-icon>
            <span class="stat-title">水温</span>
          </div>
          <div class="stat-value">{{ latestData.temperature || '--' }} <span class="unit">℃</span></div>
          <div class="stat-unit">标准范围: 26-30</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card turbidity">
          <div class="stat-header">
            <el-icon :size="32"><WarningFilled /></el-icon>
            <span class="stat-title">浊度</span>
          </div>
          <div class="stat-value">{{ latestData.turbidity || '--' }} <span class="unit">NTU</span></div>
          <div class="stat-unit">标准范围: 0-5</div>
        </div>
      </el-col>
    </el-row>

    <!-- 数据曲线图 -->
    <el-row :gutter="20" class="mb-4">
      <el-col :span="24">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>实时数据趋势</span>
              <div class="header-controls">
                <el-select v-model="timeRange" size="small" @change="handleTimeRangeChange" style="width: 120px">
                  <el-option label="最近1小时" value="1" />
                  <el-option label="最近6小时" value="6" />
                  <el-option label="最近24小时" value="24" />
                  <el-option label="最近7天" value="168" />
                </el-select>
                <el-button size="small" :icon="Refresh" @click="refreshData">刷新</el-button>
              </div>
            </div>
          </template>
          <div ref="chartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 告警列表 -->
    <el-row :gutter="20" class="mb-4">
      <el-col :span="24">
        <el-card shadow="hover" class="alarm-card">
          <template #header>
            <div class="card-header">
              <span>最新告警</span>
              <el-tag :type="alarmStatistics.unhandled > 0 ? 'danger' : 'success'">
                未处理: {{ alarmStatistics.unhandled }}
              </el-tag>
            </div>
          </template>
          <div class="alarm-list">
            <div v-for="alarm in alarmList" :key="alarm.id" class="alarm-item" :class="'alarm-' + alarm.alarmLevel">
              <div class="alarm-header">
                <el-tag :type="getAlarmTagType(alarm.alarmLevel)" size="small">
                  {{ getAlarmLevelText(alarm.alarmLevel) }}
                </el-tag>
                <span class="alarm-time">{{ formatTime(alarm.createTime) }}</span>
              </div>
              <div class="alarm-content">{{ alarm.alarmMessage }}</div>
            </div>
            <el-empty v-if="alarmList.length === 0" description="暂无告警" :image-size="80" />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { SuccessFilled, InfoFilled, Sunny, Refresh, WarningFilled, Monitor } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import type { EChartsOption } from 'echarts'
import { getLatestData, getHistoryData, listDevice, listAlarm } from '@/api/swimming/iotDevice'
import type { IotDevice, DeviceData } from '@/types/iot/device'

const chartRef = ref<HTMLElement>()
const timeRange = ref('1')
const selectedDeviceKey = ref<string>('')
const latestData = ref<any>({})
const alarmList = ref<any[]>([])
const deviceList = ref<IotDevice[]>([])
const deviceStatistics = ref({
  totalCount: 0,
  onlineCount: 0,
  offlineCount: 0
})
const alarmStatistics = ref({
  total: 0,
  unhandled: 0
})

let chart: echarts.ECharts | null = null
let refreshTimer: any = null

// 当前选中的设备
const selectedDevice = computed(() => {
  return deviceList.value.find(d => d.deviceKey === selectedDeviceKey.value)
})

// 设备切换处理
const handleDeviceChange = () => {
  console.log('切换设备:', selectedDeviceKey.value)
  // 重新加载数据
  loadLatestData()
  loadHistoryData()
  loadAlarmList()
}

// 获取最新数据
const loadLatestData = async () => {
  try {
    if (!selectedDeviceKey.value) {
      console.warn('未选择设备，无法加载最新数据')
      return
    }

    // 先清空旧数据
    latestData.value = {}

    console.log('正在获取设备最新数据，deviceKey:', selectedDeviceKey.value)
    const res = await getLatestData(selectedDeviceKey.value)
    if (res.data) {
      // 解析params字段
      const params = typeof res.data.params === 'string' ? JSON.parse(res.data.params) : res.data.params
      latestData.value = params
    }
  } catch (error) {
    console.error('加载最新数据失败:', error)
  }
}

// 加载历史数据
const loadHistoryData = async () => {
  if (!chart) return

  try {
    if (!selectedDeviceKey.value) {
      console.warn('未选择设备，无法加载历史数据')
      return
    }

    const endTime = new Date()
    const startTime = new Date(endTime.getTime() - parseInt(timeRange.value) * 60 * 60 * 1000)

    const startTimeStr = formatDateTime(startTime)
    const endTimeStr = formatDateTime(endTime)

    console.log('正在获取设备历史数据，deviceKey:', selectedDeviceKey.value, '时间范围:', startTimeStr, '-', endTimeStr)

    const res = await getHistoryData({
      deviceKey: selectedDeviceKey.value,
      startTime: startTimeStr,
      endTime: endTimeStr,
      pageNum: 1,
      pageSize: 100
    })

    if (res.rows && res.rows.length > 0) {
      const chartData = processChartData(res.rows)
      updateChart(chartData)
    } else {
      console.warn('历史数据为空，清空图表')
      // 清空图表
      updateChart({
        times: [],
        phValues: [],
        chlorineValues: [],
        temperatures: []
      })
    }
  } catch (error) {
    console.error('加载历史数据失败:', error)
  }
}

// 加载告警列表
const loadAlarmList = async () => {
  try {
    if (!selectedDeviceKey.value) {
      console.warn('未选择设备，无法加载告警列表')
      return
    }

    const res = await listAlarm({
      deviceKey: selectedDeviceKey.value,
      pageNum: 1,
      pageSize: 10,
      orderByColumn: 'create_time',
      isAsc: 'desc'
    })
    if (res.rows) {
      alarmList.value = res.rows
      console.log('告警列表加载成功，数量:', alarmList.value.length)
    }
  } catch (error) {
    console.error('加载告警列表失败:', error)
  }
}

// 加载告警统计
const loadAlarmStatistics = async () => {
  try {
    // 统计当前设备的告警数量
    const unhandledCount = alarmList.value.filter(alarm => alarm.handleStatus === '0' || !alarm.handleStatus).length
    alarmStatistics.value = {
      total: alarmList.value.length,
      unhandled: unhandledCount
    }
    console.log('告警统计更新 - 总数:', alarmStatistics.value.total, '未处理:', alarmStatistics.value.unhandled)
  } catch (error) {
    console.error('加载告警统计失败:', error)
  }
}

// 处理图表数据
const processChartData = (data: any[]) => {
  const times: string[] = []
  const phValues: number[] = []
  const chlorineValues: number[] = []
  const temperatures: number[] = []

  data.forEach(item => {
    const params = typeof item.params === 'string' ? JSON.parse(item.params) : item.params
    times.push(formatTime(item.collectTime))
    phValues.push(params.ph || 0)
    chlorineValues.push(params.chlorine || 0)
    temperatures.push(params.temperature || 0)
  })

  return { times, phValues, chlorineValues, temperatures }
}

// 初始化图表
const initChart = () => {
  if (!chartRef.value) return

  chart = echarts.init(chartRef.value)
  const option: EChartsOption = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross'
      }
    },
    legend: {
      data: ['pH值', '余氯', '水温']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: []
    },
    yAxis: {
      type: 'value',
      name: '数值'
    },
    series: [
      {
        name: 'pH值',
        type: 'line',
        smooth: true,
        data: [],
        itemStyle: { color: '#409eff' }
      },
      {
        name: '余氯',
        type: 'line',
        smooth: true,
        data: [],
        itemStyle: { color: '#67c23a' }
      },
      {
        name: '水温',
        type: 'line',
        smooth: true,
        data: [],
        itemStyle: { color: '#e6a23c' }
      }
    ]
  }

  chart.setOption(option)
}

// 更新图表
const updateChart = (chartData: any) => {
  if (!chart) return

  chart.setOption({
    xAxis: {
      data: chartData.times
    },
    series: [
      { data: chartData.phValues },
      { data: chartData.chlorineValues },
      { data: chartData.temperatures }
    ]
  })
}

// 加载设备列表
const loadDeviceList = async () => {
  try {
    console.log('正在加载设备列表...')
    const res = await listDevice({ status: '1', pageNum: 1, pageSize: 100 })
    deviceList.value = res.rows || []
    console.log('设备列表加载成功，设备数量:', deviceList.value.length)

    if (deviceList.value.length > 0) {
      // 如果还没有选中设备，默认选中第一个
      if (!selectedDeviceKey.value) {
        selectedDeviceKey.value = deviceList.value[0].deviceKey
        console.log('自动选中第一个设备:', selectedDeviceKey.value)
      }
    }
  } catch (error) {
    console.error('加载设备列表失败:', error)
  }
}

// 时间范围变化
const handleTimeRangeChange = () => {
  loadHistoryData()
}

// 刷新数据
const refreshData = () => {
  loadLatestData()
  loadHistoryData()
  loadAlarmList()
  loadAlarmStatistics()
  ElMessage.success('数据已刷新')
}

// 获取设备指标
const getDeviceMetric = (device: IotDevice, metric: string) => {
  // 这里应该从设备的最新数据中获取
  // 暂时返回占位符
  return '--'
}

// 获取告警标签类型
const getAlarmTagType = (level: string) => {
  const typeMap: Record<string, any> = {
    info: '',
    warning: 'warning',
    critical: 'danger'
  }
  return typeMap[level] || ''
}

// 获取告警级别文本
const getAlarmLevelText = (level: string) => {
  const textMap: Record<string, string> = {
    info: '信息',
    warning: '警告',
    critical: '严重'
  }
  return textMap[level] || level
}

// 格式化时间
const formatTime = (time: string) => {
  if (!time) return '--'
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const minutes = Math.floor(diff / 60000)

  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  const hours = Math.floor(minutes / 60)
  if (hours < 24) return `${hours}小时前`
  const days = Math.floor(hours / 24)
  return `${days}天前`
}

// 格式化日期时间
const formatDateTime = (date: Date) => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

// 窗口大小变化时重新渲染图表
const handleResize = () => {
  chart?.resize()
}

onMounted(async () => {
  await loadDeviceList()
  await loadLatestData()
  initChart()
  await loadHistoryData()
  await loadAlarmList()
  await loadAlarmStatistics()

  // 定时刷新数据
  refreshTimer = setInterval(() => {
    loadLatestData()
    loadHistoryData()
    loadAlarmList()
    loadAlarmStatistics()
  }, 30000) // 30秒刷新一次

  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  if (refreshTimer) {
    clearInterval(refreshTimer)
  }
  chart?.dispose()
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped lang="scss">
.iot-dashboard {
  padding: 20px;
  background: #f5f7fa;
  min-height: calc(100vh - 100px);
}

.device-selector-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;

  .selector-left {
    display: flex;
    align-items: center;
    gap: 12px;

    .selector-label {
      font-size: 14px;
      color: #606266;
      font-weight: 500;
    }
  }

  .selector-right {
    display: flex;
    align-items: center;
    gap: 12px;
  }
}

.device-option {
  display: flex;
  flex-direction: column;
  gap: 4px;

  .device-option-name {
    font-size: 14px;
    font-weight: 500;
    color: #303133;
  }

  .device-option-info {
    font-size: 12px;
    color: #909399;
  }
}

.ml-2 {
  margin-left: 8px;
}


.stat-card {
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  }

  &.ph {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #fff;
  }

  &.chlorine {
    background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
    color: #fff;
  }

  &.temperature {
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
    color: #fff;
  }

  &.turbidity {
    background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
    color: #fff;
  }
}

.stat-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;

  .stat-title {
    font-size: 18px;
    font-weight: 600;
  }
}

.stat-value {
  font-size: 36px;
  font-weight: bold;
  margin-bottom: 8px;

  .unit {
    font-size: 16px;
    margin-left: 4px;
    opacity: 0.9;
  }
}

.stat-unit {
  font-size: 14px;
  opacity: 0.8;
}

.chart-card,
.alarm-card,
.device-status-card {
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;

  .header-controls {
    display: flex;
    gap: 12px;
    align-items: center;
  }

  .device-stats {
    font-size: 14px;
    color: #606266;

    .online-text {
      color: #67c23a;
      font-weight: 600;
    }

    .offline-text {
      color: #909399;
      font-weight: 600;
    }
  }
}

.chart-container {
  height: 400px;
}

.alarm-list {
  max-height: 400px;
  overflow-y: auto;
}

.alarm-item {
  padding: 12px;
  margin-bottom: 8px;
  border-radius: 8px;
  border-left: 3px solid;
  transition: all 0.2s;

  &.alarm-info {
    background: #f4f4f5;
    border-left-color: #909399;
  }

  &.alarm-warning {
    background: #fdf6ec;
    border-left-color: #e6a23c;
  }

  &.alarm-critical {
    background: #fef0f0;
    border-left-color: #f56c6c;
  }
}

.alarm-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.alarm-time {
  font-size: 12px;
  color: #909399;
}

.alarm-content {
  font-size: 14px;
  color: #303133;
}

.device-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.device-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  transition: all 0.2s;

  &.is-online {
    background: #f0f9ff;
    border-color: #b3d8ff;

    .device-status-dot {
      background: #67c23a;
    }
  }

  &:hover {
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  }
}

.device-status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #909399;
}

.device-info {
  flex: 1;

  .device-name {
    font-size: 14px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 4px;
  }

  .device-location {
    font-size: 12px;
    color: #909399;
  }
}

.device-metrics {
  display: flex;
  gap: 12px;

  .metric-item {
    font-size: 12px;
    color: #606266;
  }
}
</style>
