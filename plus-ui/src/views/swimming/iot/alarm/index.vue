<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div v-show="showSearch" class="mb-[10px]">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true">
            <el-form-item label="设备key" prop="deviceKey">
              <el-input v-model="queryParams.deviceKey" placeholder="请输入设备key" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="告警类型" prop="alarmType">
              <el-select v-model="queryParams.alarmType" placeholder="请选择告警类型" clearable>
                <el-option label="阈值超限" value="threshold_exceeded" />
                <el-option label="设备离线" value="offline" />
                <el-option label="设备故障" value="fault" />
              </el-select>
            </el-form-item>
            <el-form-item label="告警级别" prop="alarmLevel">
              <el-select v-model="queryParams.alarmLevel" placeholder="请选择告警级别" clearable>
                <el-option label="信息" value="info" />
                <el-option label="警告" value="warning" />
                <el-option label="严重" value="critical" />
              </el-select>
            </el-form-item>
            <el-form-item label="处理状态" prop="isHandled">
              <el-select v-model="queryParams.isHandled" placeholder="请选择处理状态" clearable>
                <el-option label="未处理" value="0" />
                <el-option label="已处理" value="1" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
              <el-button icon="Refresh" @click="resetQuery">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </div>
    </transition>

    <el-card shadow="never">
      <template #header>
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['swimming:iotAlarm:remove']">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Check" :disabled="!hasUnhandled" @click="handleBatchHandle" v-hasPermi="['swimming:iotAlarm:edit']">
              批量处理
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['swimming:iotAlarm:export']">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <!-- 统计卡片 -->
      <el-row :gutter="20" class="mb8">
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-content">
              <div class="stat-value">{{ statistics.totalCount }}</div>
              <div class="stat-label">总告警数</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card warning">
            <div class="stat-content">
              <div class="stat-value">{{ statistics.unhandledCount }}</div>
              <div class="stat-label">未处理</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card today">
            <div class="stat-content">
              <div class="stat-value">{{ statistics.todayCount }}</div>
              <div class="stat-label">今日告警</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card critical">
            <div class="stat-content">
              <div class="stat-value">{{ statistics.criticalCount }}</div>
              <div class="stat-label">严重告警</div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-table v-loading="loading" border :data="alarmList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="告警ID" align="center" prop="id" v-if="false" />
        <el-table-column label="告警标题" align="center" prop="alarmTitle" :show-overflow-tooltip="true" />
        <el-table-column label="告警级别" align="center" prop="alarmLevel">
          <template #default="scope">
            <el-tag v-if="scope.row.alarmLevel === 'critical'" type="danger" effect="dark">严重</el-tag>
            <el-tag v-else-if="scope.row.alarmLevel === 'warning'" type="warning">警告</el-tag>
            <el-tag v-else-if="scope.row.alarmLevel === 'info'" type="info">信息</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="告警内容" align="center" prop="alarmMessage" :show-overflow-tooltip="true" />
        <el-table-column label="设备key" align="center" prop="deviceKey" />
        <el-table-column label="处理状态" align="center" prop="isHandled">
          <template #default="scope">
            <el-tag v-if="scope.row.isHandled === '0'" type="danger">未处理</el-tag>
            <el-tag v-else-if="scope.row.isHandled === '1'" type="success">已处理</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="告警时间" align="center" prop="createTime" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="处理人" align="center" prop="handleUserName" />
        <el-table-column label="操作" align="center" fixed="right" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="查看详情" placement="top">
              <el-button link type="primary" icon="View" @click="handleViewDetail(scope.row)" v-hasPermi="['swimming:iotAlarm:query']"></el-button>
            </el-tooltip>
            <el-tooltip content="处理" placement="top" v-if="scope.row.isHandled === '0'">
              <el-button link type="primary" icon="Check" @click="handleHandle(scope.row)" v-hasPermi="['swimming:iotAlarm:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['swimming:iotAlarm:remove']"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>

    <!-- 处理告警对话框 -->
    <el-dialog :title="handleDialog.title" v-model="handleDialog.visible" width="500px" append-to-body>
      <el-form ref="handleFormRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="告警内容">
          <el-text>{{ currentAlarm.alarmMessage }}</el-text>
        </el-form-item>
        <el-form-item label="告警数据" v-if="currentAlarm.alarmData">
          <el-text type="info">{{ currentAlarm.alarmData }}</el-text>
        </el-form-item>
        <el-form-item label="处理结果" prop="handleResult">
          <el-input
            v-model="form.handleResult"
            type="textarea"
            :rows="4"
            placeholder="请输入处理结果"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitHandle" :loading="buttonLoading">确 定</el-button>
          <el-button @click="handleDialog.visible = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 告警详情对话框 -->
    <el-dialog title="告警详情" v-model="detailDialog.visible" width="600px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="告警标题">{{ currentAlarm.alarmTitle }}</el-descriptions-item>
        <el-descriptions-item label="告警级别">
          <el-tag v-if="currentAlarm.alarmLevel === 'critical'" type="danger">严重</el-tag>
          <el-tag v-else-if="currentAlarm.alarmLevel === 'warning'" type="warning">警告</el-tag>
          <el-tag v-else type="info">信息</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="告警内容">{{ currentAlarm.alarmMessage }}</el-descriptions-item>
        <el-descriptions-item label="设备key">{{ currentAlarm.deviceKey }}</el-descriptions-item>
        <el-descriptions-item label="处理状态">
          <el-tag v-if="currentAlarm.isHandled === '0'" type="danger">未处理</el-tag>
          <el-tag v-else type="success">已处理</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="告警时间">{{ parseTime(currentAlarm.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="处理人" v-if="currentAlarm.handleUserName">{{ currentAlarm.handleUserName }}</el-descriptions-item>
        <el-descriptions-item label="处理时间" v-if="currentAlarm.handleTime">{{ parseTime(currentAlarm.handleTime) }}</el-descriptions-item>
        <el-descriptions-item label="处理结果" v-if="currentAlarm.handleResult" :span="2">
          {{ currentAlarm.handleResult }}
        </el-descriptions-item>
        <el-descriptions-item label="告警数据" v-if="currentAlarm.alarmData" :span="2">
          <pre>{{ formatAlarmData(currentAlarm.alarmData) }}</pre>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup name="IotAlarm" lang="ts">
import { listAlarm, getAlarm, delAlarm, handleAlarm, batchHandleAlarms, exportAlarm, getAlarmStatistics } from '@/api/swimming/iotAlarm'
import type { IotDeviceAlarm, AlarmQuery, AlarmStatistics } from '@/types/iot/alarm'

const { proxy } = getCurrentInstance() as ComponentInternalInstance

const alarmList = ref<IotDeviceAlarm[]>([])
const loading = ref(true)
const showSearch = ref(true)
const ids = ref<Array<number>>([])
const single = ref(true)
const multiple = ref(true)
const hasUnhandled = ref(false)
const total = ref(0)
const buttonLoading = ref(false)
const currentAlarm = ref<IotDeviceAlarm>({} as IotDeviceAlarm)
const statistics = ref<AlarmStatistics>({
  totalCount: 0,
  unhandledCount: 0,
  todayCount: 0,
  criticalCount: 0
})

const queryFormRef = ref<ElFormInstance>()
const handleFormRef = ref<ElFormInstance>()

const handleDialog = reactive<DialogOption>({
  visible: false,
  title: '处理告警'
})

const detailDialog = reactive<DialogOption>({
  visible: false
})

const initFormData = {
  handleResult: ''
}

const data = reactive<PageData<IotDeviceAlarm, AlarmQuery>>({
  form: { ...initFormData },
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    deviceKey: '',
    alarmType: '',
    alarmLevel: '',
    isHandled: ''
  },
  rules: {
    handleResult: [
      { required: true, message: "处理结果不能为空", trigger: "blur" }
    ]
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询IoT设备告警列表 */
const getList = async () => {
  loading.value = true
  const res = await listAlarm(queryParams.value)
  alarmList.value = res.rows || []
  total.value = res.total || 0
  hasUnhandled.value = alarmList.value.some((item: IotDeviceAlarm) => item.isHandled === '0')
  loading.value = false

  // 同时获取统计信息
  loadStatistics()
}

/** 查询告警统计信息 */
const loadStatistics = async () => {
  const res = await getAlarmStatistics()
  statistics.value = res.data
}

/** 取消按钮 */
const cancel = () => {
  reset()
  handleDialog.visible = false
}

/** 表单重置 */
const reset = () => {
  form.value = { ...initFormData }
  handleFormRef.value?.resetFields()
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.value.pageNum = 1
  getList()
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value?.resetFields()
  handleQuery()
}

/** 多选框选中数据 */
const handleSelectionChange = (selection: IotDeviceAlarm[]) => {
  ids.value = selection.map(item => item.id!)
  single.value = selection.length != 1
  multiple.value = !selection.length
  hasUnhandled.value = selection.some((item: IotDeviceAlarm) => item.isHandled === '0')
}

/** 查看告警详情 */
const handleViewDetail = (row: IotDeviceAlarm) => {
  currentAlarm.value = row
  detailDialog.visible = true
}

/** 处理告警 */
const handleHandle = (row: IotDeviceAlarm) => {
  currentAlarm.value = row
  // 重置表单
  form.value = { ...initFormData }
  handleDialog.visible = true
  handleDialog.title = '处理告警'
}

/** 提交处理结果 */
const submitHandle = () => {
  handleFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      try {
        buttonLoading.value = true
        console.log('处理告警 - ID:', currentAlarm.value.id, '结果:', form.value.handleResult)
        await handleAlarm(currentAlarm.value.id!, form.value.handleResult)
        buttonLoading.value = false
        proxy?.$modal.msgSuccess("处理成功")
        handleDialog.visible = false
        await getList()
      } catch (error) {
        console.error('处理告警失败:', error)
        buttonLoading.value = false
        proxy?.$modal.msgError("处理失败：" + (error as Error).message)
      }
    } else {
      console.log('表单验证失败')
    }
  })
}

/** 批量处理告警 */
const handleBatchHandle = () => {
  proxy?.$modal.prompt('请输入处理结果', '批量处理告警').then(async ({ value }) => {
    if (!value) return

    await batchHandleAlarms(ids.value, value)
    proxy?.$modal.msgSuccess("批量处理成功")
    await getList()
  }).catch(() => {})
}

/** 删除按钮操作 */
const handleDelete = async (row?: IotDeviceAlarm) => {
  const _ids = row?.id || ids.value
  await proxy?.$modal.confirm('是否确认删除告警编号为"' + _ids + '"的数据项？').finally(() => loading.value = false)
  await delAlarm(_ids)
  proxy?.$modal.msgSuccess("删除成功")
  await getList()
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('swimming/iot/alarm/export', {
    ...queryParams.value
  }, `iot_alarm_${new Date().getTime()}.xlsx`)
}

/** 格式化告警数据 */
const formatAlarmData = (alarmData: string) => {
  try {
    const data = typeof alarmData === 'string' ? JSON.parse(alarmData) : alarmData
    return JSON.stringify(data, null, 2)
  } catch {
    return alarmData
  }
}

onMounted(() => {
  getList()
})
</script>

<style scoped lang="scss">
.stat-card {
  text-align: center;
  border-radius: 8px;
  transition: all 0.3s;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }

  &.warning {
    background: linear-gradient(135deg, #fdf6ec 0%, #feecd8 100%);
  }

  &.today {
    background: linear-gradient(135deg, #e8f5e9 0%, #c8e6c9 100%);
  }

  &.critical {
    background: linear-gradient(135deg, #fef0f0 0%, #fde2e2 100%);
  }
}

.stat-content {
  padding: 20px;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 10px;
}

.stat-label {
  font-size: 14px;
  color: #606266;
}
</style>
