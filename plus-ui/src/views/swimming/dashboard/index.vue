<template>
  <div class="swimming-dashboard">
    <!-- 顶部统计卡片 -->
    <el-row :gutter="20" class="mb-4">
      <!-- 会员统计 -->
      <el-col :span="6">
        <div class="stat-card member">
          <div class="stat-header">
            <div class="stat-icon">
              <el-icon :size="32"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-title">会员总数</div>
              <div class="stat-value">{{ statistics.memberStats?.totalMembers || 0 }}</div>
            </div>
          </div>
          <div class="stat-footer">
            <span class="stat-label">今日新增：</span>
            <span class="stat-number">+{{ statistics.memberStats?.todayNewMembers || 0 }}</span>
            <el-divider direction="vertical" />
            <span class="stat-label">总余额：</span>
            <span class="stat-number">¥{{ formatMoney(statistics.memberStats?.totalBalance) }}</span>
          </div>
        </div>
      </el-col>

      <!-- 预约统计 -->
      <el-col :span="6">
        <div class="stat-card reservation">
          <div class="stat-header">
            <div class="stat-icon">
              <el-icon :size="32"><Calendar /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-title">今日预约</div>
              <div class="stat-value">{{ statistics.reservationStats?.todayReservations || 0 }}</div>
            </div>
          </div>
          <div class="stat-footer">
            <span class="stat-label">当前在场：</span>
            <span class="stat-number success">{{ statistics.reservationStats?.currentPresent || 0 }}</span>
            <span class="stat-label">人</span>
          </div>
        </div>
      </el-col>

      <!-- 订单统计 -->
      <el-col :span="6">
        <div class="stat-card order">
          <div class="stat-header">
            <div class="stat-icon">
              <el-icon :size="32"><ShoppingCart /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-title">今日收入</div>
              <div class="stat-value">¥{{ formatMoney(statistics.orderStats?.todayIncome) }}</div>
            </div>
          </div>
          <div class="stat-footer">
            <span class="stat-label">今日订单：</span>
            <span class="stat-number">{{ statistics.orderStats?.todayOrders || 0 }}</span>
            <el-divider direction="vertical" />
            <span class="stat-label">本月收入：</span>
            <span class="stat-number">¥{{ formatMoney(statistics.orderStats?.monthIncome) }}</span>
          </div>
        </div>
      </el-col>

      <!-- 设备统计 -->
      <el-col :span="6">
        <div class="stat-card device">
          <div class="stat-header">
            <div class="stat-icon">
              <el-icon :size="32"><Monitor /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-title">设备状态</div>
              <div class="stat-value">
                {{ statistics.deviceStats?.onlineDevices || 0 }}/{{ statistics.deviceStats?.totalDevices || 0 }}
              </div>
            </div>
          </div>
          <div class="stat-footer">
            <span class="stat-label">在线：</span>
            <span class="stat-number success">{{ statistics.deviceStats?.onlineDevices || 0 }}</span>
            <el-divider direction="vertical" />
            <span class="stat-label">今日告警：</span>
            <span class="stat-number warning">{{ statistics.deviceStats?.todayAlarms || 0 }}</span>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 水质监控 -->
    <el-row :gutter="20" class="mb-4">
      <el-col :span="24">
        <el-card shadow="hover" class="water-quality-card">
          <template #header>
            <div class="card-header">
              <span>水质实时监控</span>
              <el-tag :type="getWaterQualityStatus() === 'good' ? 'success' : 'warning'">
                {{ getWaterQualityStatusText() }}
              </el-tag>
            </div>
          </template>
          <div class="water-quality-grid">
            <div class="water-item">
              <div class="water-icon">
                <el-icon :size="28"><Sunny /></el-icon>
              </div>
              <div class="water-info">
                <div class="water-label">水温</div>
                <div class="water-value">{{ statistics.waterQuality?.temperature || '--' }} <span class="unit">℃</span></div>
                <div class="water-range">标准: 26-30℃</div>
              </div>
            </div>
            <div class="water-item">
              <div class="water-icon">
                <el-icon :size="28"><SuccessFilled /></el-icon>
              </div>
              <div class="water-info">
                <div class="water-label">pH值</div>
                <div class="water-value">{{ statistics.waterQuality?.ph || '--' }}</div>
                <div class="water-range">标准: 6.5-8.5</div>
              </div>
            </div>
            <div class="water-item">
              <div class="water-icon">
                <el-icon :size="28"><InfoFilled /></el-icon>
              </div>
              <div class="water-info">
                <div class="water-label">余氯</div>
                <div class="water-value">{{ statistics.waterQuality?.chlorine || '--' }} <span class="unit">mg/L</span></div>
                <div class="water-range">标准: 0.3-1.5</div>
              </div>
            </div>
            <div class="water-item">
              <div class="water-icon">
                <el-icon :size="28"><WarningFilled /></el-icon>
              </div>
              <div class="water-info">
                <div class="water-label">浊度</div>
                <div class="water-value">{{ statistics.waterQuality?.turbidity || '--' }} <span class="unit">NTU</span></div>
                <div class="water-range">标准: 0-5</div>
              </div>
            </div>
          </div>
          <div class="water-update-time" v-if="statistics.waterQuality?.collectTime">
            <el-icon><Clock /></el-icon>
            更新时间: {{ formatTime(statistics.waterQuality.collectTime) }}
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 收入趋势图 -->
    <el-row :gutter="20" class="mb-4">
      <el-col :span="24">
        <el-card shadow="hover" class="trend-card">
          <template #header>
            <div class="card-header">
              <span>近7天充值收入趋势</span>
              <el-tag type="info">单位：元</el-tag>
            </div>
          </template>
          <div ref="trendChartRef" class="trend-chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 会员增长趋势图 -->
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card shadow="hover" class="trend-card">
          <template #header>
            <div class="card-header">
              <span>近7天会员增长趋势</span>
              <el-tag type="info">单位：人</el-tag>
            </div>
          </template>
          <div ref="memberTrendChartRef" class="trend-chart-container"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { User, Calendar, ShoppingCart, Monitor, Sunny, SuccessFilled, InfoFilled, WarningFilled, Clock } from '@element-plus/icons-vue'
import { getDashboardStatistics, getRechargeTrend, getMemberGrowthTrend } from '@/api/swimming/dashboard'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import type { EChartsOption } from 'echarts'

const statistics = ref<any>({
  memberStats: {},
  reservationStats: {},
  orderStats: {},
  deviceStats: {},
  waterQuality: {}
})

const trendChartRef = ref<HTMLElement>()
const memberTrendChartRef = ref<HTMLElement>()
let trendChart: echarts.ECharts | null = null
let memberTrendChart: echarts.ECharts | null = null
let refreshTimer: any = null

// 加载数据
const loadData = async () => {
  try {
    const res = await getDashboardStatistics()
    if (res.data) {
      statistics.value = res.data
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
    ElMessage.error('加载统计数据失败')
  }
}

// 格式化金额
const formatMoney = (value: number | string) => {
  if (!value) return '0.00'
  return Number(value).toLocaleString('zh-CN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })
}

// 格式化时间
const formatTime = (time: string) => {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN')
}

// 获取水质状态
const getWaterQualityStatus = () => {
  const water = statistics.value.waterQuality
  if (!water || !water.temperature) return 'unknown'

  const temp = Number(water.temperature)
  const ph = Number(water.ph)
  const chlorine = Number(water.chlorine)
  const turbidity = Number(water.turbidity)

  const tempOk = temp >= 26 && temp <= 30
  const phOk = ph >= 6.5 && ph <= 8.5
  const chlorineOk = chlorine >= 0.3 && chlorine <= 1.5
  const turbidityOk = turbidity >= 0 && turbidity <= 5

  if (tempOk && phOk && chlorineOk && turbidityOk) {
    return 'good'
  }
  return 'warning'
}

// 获取水质状态文本
const getWaterQualityStatusText = () => {
  const status = getWaterQualityStatus()
  return status === 'good' ? '水质优良' : '注意监测'
}

// 初始化充值收入趋势图
const initTrendChart = async () => {
  if (!trendChartRef.value) return

  trendChart = echarts.init(trendChartRef.value)

  try {
    // 调用真实API获取数据
    const res = await getRechargeTrend()
    const dates = res.data?.dates || []
    const data = res.data?.values || []

    const option: EChartsOption = {
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'cross'
        },
        formatter: (params: any) => {
          const param = params[0]
          return `${param.axisValue}<br/>充值收入: ¥${param.value.toLocaleString()}`
        }
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
        data: dates,
        axisLine: {
          lineStyle: {
            color: '#ddd'
          }
        },
        axisLabel: {
          color: '#666'
        }
      },
      yAxis: {
        type: 'value',
        axisLine: {
          show: false
        },
        axisTick: {
          show: false
        },
        splitLine: {
          lineStyle: {
            color: '#f0f0f0',
            type: 'dashed'
          }
        },
        axisLabel: {
          color: '#666',
          formatter: (value: number) => {
            if (value >= 10000) {
              return (value / 10000).toFixed(1) + '万'
            }
            return value.toString()
          }
        }
      },
      series: [
        {
          name: '充值收入',
          type: 'line',
          smooth: true,
          symbol: 'circle',
          symbolSize: 8,
          lineStyle: {
            width: 3,
            color: {
              type: 'linear',
              x: 0,
              y: 0,
              x2: 1,
              y2: 0,
              colorStops: [
                { offset: 0, color: '#667eea' },
                { offset: 1, color: '#764ba2' }
              ]
            }
          },
          itemStyle: {
            color: '#667eea',
            borderColor: '#fff',
            borderWidth: 2
          },
          areaStyle: {
            color: {
              type: 'linear',
              x: 0,
              y: 0,
              x2: 0,
              y2: 1,
              colorStops: [
                { offset: 0, color: 'rgba(102, 126, 234, 0.3)' },
                { offset: 1, color: 'rgba(102, 126, 234, 0.05)' }
              ]
            }
          },
          data: data
        }
      ]
    }

    trendChart.setOption(option)
  } catch (error) {
    console.error('加载充值趋势失败:', error)
  }
}

// 初始化会员增长趋势图
const initMemberTrendChart = async () => {
  if (!memberTrendChartRef.value) return

  memberTrendChart = echarts.init(memberTrendChartRef.value)

  try {
    // 调用真实API获取数据
    const res = await getMemberGrowthTrend()
    const dates = res.data?.dates || []
    const data = res.data?.values || []

    const option: EChartsOption = {
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'cross'
        },
        formatter: (params: any) => {
          const param = params[0]
          return `${param.axisValue}<br/>新增会员: ${param.value} 人`
        }
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
        data: dates,
        axisLine: {
          lineStyle: {
            color: '#ddd'
          }
        },
        axisLabel: {
          color: '#666'
        }
      },
      yAxis: {
        type: 'value',
        axisLine: {
          show: false
        },
        axisTick: {
          show: false
        },
        splitLine: {
          lineStyle: {
            color: '#f0f0f0',
            type: 'dashed'
          }
        },
        axisLabel: {
          color: '#666',
          formatter: (value: number) => {
            return value.toString()
          }
        }
      },
      series: [
        {
          name: '新增会员',
          type: 'bar',
          barWidth: '40%',
          itemStyle: {
            color: {
              type: 'linear',
              x: 0,
              y: 0,
              x2: 0,
              y2: 1,
              colorStops: [
                { offset: 0, color: '#f093fb' },
                { offset: 1, color: '#f5576c' }
              ]
            },
            borderRadius: [4, 4, 0, 0]
          },
          data: data
        },
        {
          name: '趋势线',
          type: 'line',
          smooth: true,
          symbol: 'circle',
          symbolSize: 6,
          lineStyle: {
            width: 2,
            color: '#f5576c'
          },
          itemStyle: {
            color: '#f5576c',
            borderColor: '#fff',
            borderWidth: 2
          },
          data: data
        }
      ]
    }

    memberTrendChart.setOption(option)
  } catch (error) {
    console.error('加载会员增长趋势失败:', error)
  }
}

onMounted(async () => {
  await loadData()
  await nextTick()
  await initTrendChart()
  await initMemberTrendChart()

  // 定时刷新（每30秒）
  refreshTimer = setInterval(() => {
    loadData()
    initTrendChart()
    initMemberTrendChart()
  }, 30000)

  // 监听窗口大小变化
  window.addEventListener('resize', () => {
    trendChart?.resize()
    memberTrendChart?.resize()
  })
})

onUnmounted(() => {
  if (refreshTimer) {
    clearInterval(refreshTimer)
  }
  trendChart?.dispose()
  memberTrendChart?.dispose()
})
</script>

<style scoped lang="scss">
.swimming-dashboard {
  padding: 20px;
  background: #f5f7fa;
  min-height: calc(100vh - 100px);
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

  &.member {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #fff;
  }

  &.reservation {
    background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
    color: #fff;
  }

  &.order {
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
    color: #fff;
  }

  &.device {
    background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
    color: #fff;
  }
}

.stat-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}

.stat-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 64px;
  height: 64px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
}

.stat-info {
  flex: 1;
}

.stat-title {
  font-size: 14px;
  opacity: 0.9;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
}

.stat-footer {
  display: flex;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.2);
  font-size: 13px;
}

.stat-label {
  opacity: 0.8;
}

.stat-number {
  font-weight: 600;

  &.success {
    color: #67c23a;
  }

  &.warning {
    color: #e6a23c;
  }
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.water-quality-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.water-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  border-radius: 12px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  transition: all 0.3s;

  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }
}

.water-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.water-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 8px;
}

.water-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 4px;

  .unit {
    font-size: 14px;
    font-weight: normal;
    color: #909399;
  }
}

.water-range {
  font-size: 12px;
  color: #909399;
}

.water-update-time {
  display: flex;
  align-items: center;
  gap: 8px;
  justify-content: flex-end;
  font-size: 13px;
  color: #909399;
}

.trend-chart-container {
  height: 400px;
}
</style>
