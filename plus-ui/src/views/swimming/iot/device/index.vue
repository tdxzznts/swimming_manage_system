<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div v-show="showSearch" class="mb-[10px]">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true">
            <el-form-item label="产品标识" prop="productKey">
              <el-input v-model="queryParams.productKey" placeholder="请输入产品标识" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="设备名称" prop="deviceName">
              <el-input v-model="queryParams.deviceName" placeholder="请输入设备名称" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="设备状态" prop="status">
              <el-select v-model="queryParams.status" placeholder="请选择设备状态" clearable>
                <el-option label="在线" value="1" />
                <el-option label="离线" value="0" />
                <el-option label="禁用" value="3" />
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
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['swimming:iotDevice:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['swimming:iotDevice:edit']">修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['swimming:iotDevice:remove']">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['swimming:iotDevice:export']">导出</el-button>
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
              <div class="stat-label">总设备数</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card online">
            <div class="stat-content">
              <div class="stat-value">{{ statistics.onlineCount }}</div>
              <div class="stat-label">在线设备</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card offline">
            <div class="stat-content">
              <div class="stat-value">{{ statistics.offlineCount }}</div>
              <div class="stat-label">离线设备</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card today">
            <div class="stat-content">
              <div class="stat-value">{{ statistics.todayCount }}</div>
              <div class="stat-label">今日新增</div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-table v-loading="loading" border :data="deviceList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="设备ID" align="center" prop="id" v-if="false" />
        <el-table-column label="设备名称" align="center" prop="deviceName" :show-overflow-tooltip="true" />
        <el-table-column label="产品标识" align="center" prop="productKey" />
        <el-table-column label="设备类型" align="center" prop="deviceType" />
        <el-table-column label="固件版本" align="center" prop="firmwareVersion" />
        <el-table-column label="状态" align="center" prop="status">
          <template #default="scope">
            <el-tag v-if="scope.row.status === '1'" type="success">在线</el-tag>
            <el-tag v-else-if="scope.row.status === '0'" type="info">离线</el-tag>
            <el-tag v-else-if="scope.row.status === '3'" type="danger">禁用</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="安装位置" align="center" prop="location" :show-overflow-tooltip="true" />
        <el-table-column label="最后在线时间" align="center" prop="lastOnlineTime" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.lastOnlineTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" fixed="right" class-name="small-padding fixed-width" width="250">
          <template #default="scope">
            <el-tooltip content="告警规则" placement="top">
              <el-button link type="success" icon="Bell" @click="handleConfigAlarm(scope.row)"></el-button>
            </el-tooltip>
            <el-tooltip content="查看密钥" placement="top">
              <el-button link type="warning" icon="Key" @click="handleViewKeys(scope.row)"></el-button>
            </el-tooltip>
            <el-tooltip content="查看数据" placement="top">
              <el-button link type="primary" icon="DataLine" @click="handleViewData(scope.row)" v-hasPermi="['swimming:iotDevice:query']"></el-button>
            </el-tooltip>
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['swimming:iotDevice:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['swimming:iotDevice:remove']"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>

    <!-- 添加或修改IoT设备对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" append-to-body>
      <el-form ref="deviceFormRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="产品标识" prop="productKey">
          <el-input v-model="form.productKey" placeholder="请输入产品标识" />
        </el-form-item>
        <el-form-item label="设备名称" prop="deviceName">
          <el-input v-model="form.deviceName" placeholder="请输入设备名称" />
        </el-form-item>
        <el-form-item label="MAC地址" prop="mac">
          <el-input v-model="form.mac" placeholder="请输入MAC地址" />
        </el-form-item>
        <el-form-item label="设备类型" prop="deviceType">
          <el-input v-model="form.deviceType" placeholder="请输入设备类型" />
        </el-form-item>
        <el-form-item label="固件版本" prop="firmwareVersion">
          <el-input v-model="form.firmwareVersion" placeholder="请输入固件版本" />
        </el-form-item>
        <el-form-item label="安装位置" prop="location">
          <el-input v-model="form.location" placeholder="请输入安装位置" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 查看密钥对话框 -->
    <el-dialog title="设备密钥信息" v-model="keyDialogVisible" width="600px" append-to-body>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="设备名称">
          {{ currentDevice.deviceName }}
        </el-descriptions-item>
        <el-descriptions-item label="产品标识">
          {{ currentDevice.productKey }}
        </el-descriptions-item>
        <el-descriptions-item label="Device Key">
          <div class="key-item">
            <el-input v-model="currentDevice.deviceKey" readonly>
              <template #append>
                <el-button icon="CopyDocument" @click="copyToClipboard(currentDevice.deviceKey, 'Device Key')">
                  复制
                </el-button>
              </template>
            </el-input>
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="Device Secret">
          <div class="key-item">
            <el-input v-model="currentDevice.deviceSecret" readonly>
              <template #append>
                <el-button icon="CopyDocument" @click="copyToClipboard(currentDevice.deviceSecret, 'Device Secret')">
                  复制
                </el-button>
              </template>
            </el-input>
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="MQTT连接配置">
          <div class="mqtt-tips">
            <p style="color: #409eff; font-size: 14px; margin-bottom: 10px;">
              <strong>📌 所有设备使用统一的认证配置（来自application.yml）</strong>
            </p>
            <p><strong>Broker:</strong> tcp://localhost:1883</p>
            <p><strong>ClientId:</strong> {{ currentDevice.deviceKey }} <span style="color: #909399;">（设备唯一标识）</span></p>
            <p><strong>Username:</strong> swimming-server <span style="color: #909399;">（统一）</span></p>
            <p><strong>Password:</strong> swimming_server_2026 <span style="color: #909399;">（统一）</span></p>
            <el-divider style="margin: 10px 0"></el-divider>
            <p style="color: #67c23a; font-size: 12px;">
              💡 <strong>提示：</strong>设备连接时，Username和Password填写统一的值即可
            </p>
          </div>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="keyDialogVisible = false">关 闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="IotDevice" lang="ts">
import { listDevice, getDevice, delDevice, addDevice, updateDevice, exportDevice, getDeviceStatistics } from '@/api/swimming/iotDevice'
import type { IotDevice, DeviceQuery, DeviceStatistics } from '@/types/iot/device'

const { proxy } = getCurrentInstance() as ComponentInternalInstance

const deviceList = ref<IotDevice[]>([])
const buttonLoading = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref<Array<number>>([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const statistics = ref<DeviceStatistics>({
  totalCount: 0,
  onlineCount: 0,
  offlineCount: 0,
  todayCount: 0,
  onlineRate: '0.00%'
})

const queryFormRef = ref<ElFormInstance>()
const deviceFormRef = ref<ElFormInstance>()

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
})

// 密钥对话框相关
const keyDialogVisible = ref(false)
const currentDevice = reactive<Partial<IotDevice>>({
  deviceName: '',
  productKey: '',
  deviceKey: '',
  deviceSecret: ''
})

const initFormData: IotDevice = {
  productKey: '',
  deviceName: '',
  mac: '',
  deviceType: '',
  firmwareVersion: '',
  status: '0',
  location: '',
  remark: ''
}

const data = reactive<PageData<IotDevice, DeviceQuery>>({
  form: { ...initFormData },
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    productKey: '',
    deviceName: '',
    status: ''
  },
  rules: {
    productKey: [
      { required: true, message: "产品标识不能为空", trigger: "blur" }
    ],
    deviceName: [
      { required: true, message: "设备名称不能为空", trigger: "blur" }
    ]
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询IoT设备列表 */
const getList = async () => {
  loading.value = true
  const res = await listDevice(queryParams.value)
  deviceList.value = res.rows
  total.value = res.total
  loading.value = false

  // 同时获取统计信息
  loadStatistics()
}

/** 查询设备统计信息 */
const loadStatistics = async () => {
  const res = await getDeviceStatistics()
  statistics.value = res.data
}

/** 取消按钮 */
const cancel = () => {
  reset()
  dialog.visible = false
}

/** 表单重置 */
const reset = () => {
  form.value = { ...initFormData }
  deviceFormRef.value?.resetFields()
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
const handleSelectionChange = (selection: IotDevice[]) => {
  ids.value = selection.map(item => item.id!)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
const handleAdd = () => {
  reset()
  dialog.visible = true
  dialog.title = "添加IoT设备"
}

/** 修改按钮操作 */
const handleUpdate = async (row?: IotDevice) => {
  reset()
  const _id = row?.id || ids.value[0]
  const res = await getDevice(_id)
  Object.assign(form.value, res.data)
  dialog.visible = true
  dialog.title = "修改IoT设备"
}

/** 提交按钮 */
const submitForm = () => {
  deviceFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true
      if (form.value.id) {
        await updateDevice(form.value).finally(() => buttonLoading.value = false)
      } else {
        await addDevice(form.value).finally(() => buttonLoading.value = false)
      }
      proxy?.$modal.msgSuccess("操作成功")
      dialog.visible = false
      await getList()
    }
  })
}

/** 删除按钮操作 */
const handleDelete = async (row?: IotDevice) => {
  const _ids = row?.id || ids.value
  await proxy?.$modal.confirm('是否确认删除设备编号为"' + _ids + '"的数据项？').finally(() => loading.value = false)
  await delDevice(_ids)
  proxy?.$modal.msgSuccess("删除成功")
  await getList()
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('swimming/iot/device/export', {
    ...queryParams.value
  }, `iot_device_${new Date().getTime()}.xlsx`)
}

/** 查看设备密钥 */
const handleViewKeys = async (row: IotDevice) => {
  try {
    // 从后端获取完整的设备信息（包含device_key和device_secret）
    const res = await getDevice(row.id!)
    Object.assign(currentDevice, res.data)
    keyDialogVisible.value = true
  } catch (error) {
    proxy?.$modal.msgError('获取设备密钥失败')
  }
}

/** 复制到剪贴板 */
const copyToClipboard = async (text: string, label: string) => {
  try {
    await navigator.clipboard.writeText(text)
    proxy?.$modal.msgSuccess(`${label} 已复制到剪贴板`)
  } catch (error) {
    // 降级方案：使用传统的复制方法
    const textarea = document.createElement('textarea')
    textarea.value = text
    textarea.style.position = 'fixed'
    textarea.style.opacity = '0'
    document.body.appendChild(textarea)
    textarea.select()
    try {
      document.execCommand('copy')
      proxy?.$modal.msgSuccess(`${label} 已复制到剪贴板`)
    } catch (err) {
      proxy?.$modal.msgError('复制失败，请手动复制')
    }
    document.body.removeChild(textarea)
  }
}

/** 查看设备数据 */
const handleViewData = (row: IotDevice) => {
  proxy?.$router.push({
    path: '/swimming/iot/data',
    query: { deviceKey: row.deviceKey, deviceName: row.deviceName }
  })
}

/** 配置告警规则 */
const handleConfigAlarm = (row: IotDevice) => {
  proxy?.$router.push({
    path: '/swimming/iotAlarmRule',
    query: { deviceKey: row.deviceKey, deviceName: row.deviceName }
  })
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

  &.online {
    background: linear-gradient(135deg, #e8f5e9 0%, #c8e6c9 100%);
  }

  &.offline {
    background: linear-gradient(135deg, #fafafa 0%, #eeeeee 100%);
  }

  &.today {
    background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
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

.key-item {
  width: 100%;
}

.mqtt-tips {
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
  font-size: 13px;

  p {
    margin: 8px 0;
    line-height: 1.6;

    strong {
      color: #409eff;
      display: inline-block;
      min-width: 80px;
    }
  }
}

.secret-hint {
  color: #e6a23c;
  font-weight: 500;
}
</style>
