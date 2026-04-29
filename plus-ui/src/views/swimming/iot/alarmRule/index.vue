<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div v-show="showSearch" class="mb-[10px]">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true">
            <el-form-item label="设备" prop="deviceKey">
              <el-input v-model="queryParams.deviceKey" placeholder="请输入设备Key" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="规则名称" prop="ruleName">
              <el-input v-model="queryParams.ruleName" placeholder="请输入规则名称" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="告警级别" prop="alarmLevel">
              <el-select v-model="queryParams.alarmLevel" placeholder="请选择告警级别" clearable>
                <el-option label="信息" value="info" />
                <el-option label="警告" value="warning" />
                <el-option label="严重" value="critical" />
              </el-select>
            </el-form-item>
            <el-form-item label="状态" prop="status">
              <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
                <el-option label="启用" value="1" />
                <el-option label="禁用" value="0" />
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
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['swimming:iot:alarmRule:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['swimming:iot:alarmRule:remove']">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['swimming:iot:alarmRule:export']">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" border :data="ruleList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="规则ID" align="center" prop="id" width="80" />
        <el-table-column label="规则名称" align="center" prop="ruleName" :show-overflow-tooltip="true" min-width="150" />
        <el-table-column label="设备Key" align="center" prop="deviceKey" :show-overflow-tooltip="true" min-width="180" />
        <el-table-column label="监控指标" align="center" prop="metricName" width="120">
          <template #default="scope">
            <el-tag>{{ getMetricLabel(scope.row.metricName) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="阈值范围" align="center" width="150">
          <template #default="scope">
            <span v-if="scope.row.thresholdMin !== null && scope.row.thresholdMax !== null">
              {{ scope.row.thresholdMin }} ~ {{ scope.row.thresholdMax }} {{ scope.row.metricUnit || '' }}
            </span>
            <span v-else-if="scope.row.thresholdMin !== null">
              ≥ {{ scope.row.thresholdMin }} {{ scope.row.metricUnit || '' }}
            </span>
            <span v-else-if="scope.row.thresholdMax !== null">
              ≤ {{ scope.row.thresholdMax }} {{ scope.row.metricUnit || '' }}
            </span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="告警级别" align="center" prop="alarmLevel" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.alarmLevel === 'info'" type="info">信息</el-tag>
            <el-tag v-else-if="scope.row.alarmLevel === 'warning'" type="warning">警告</el-tag>
            <el-tag v-else-if="scope.row.alarmLevel === 'critical'" type="danger">严重</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" align="center" prop="status" width="80">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              active-value="1"
              inactive-value="0"
              @change="handleStatusChange(scope.row)"
            ></el-switch>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" align="center" prop="createTime" width="160">
          <template #default="scope">
            <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" fixed="right" class-name="small-padding fixed-width" width="150">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['swimming:iot:alarmRule:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['swimming:iot:alarmRule:remove']"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>

    <!-- 添加或修改告警规则对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="600px" append-to-body>
      <el-form ref="ruleFormRef" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="设备Key" prop="deviceKey">
          <el-input v-model="form.deviceKey" placeholder="请输入设备Key" :disabled="!!form.id" />
          <div class="form-tip">示例: testdevice.water_sensor_001</div>
        </el-form-item>
        <el-form-item label="规则名称" prop="ruleName">
          <el-input v-model="form.ruleName" placeholder="请输入规则名称" />
        </el-form-item>
        <el-form-item label="监控指标" prop="metricName">
          <el-select v-model="form.metricName" placeholder="请选择监控指标" filterable allow-create>
            <el-option
              v-for="item in metricOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            >
              <span>{{ item.label }}</span>
              <span style="float: right; color: #8492a6; font-size: 12px">{{ item.unit }}</span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="指标单位">
          <el-input v-model="form.metricUnit" placeholder="如: mg/L, ℃, NTU" />
        </el-form-item>
        <el-form-item label="最小阈值">
          <el-input-number v-model="form.thresholdMin" :min="0" :precision="2" placeholder="不限制请留空" />
        </el-form-item>
        <el-form-item label="最大阈值">
          <el-input-number v-model="form.thresholdMax" :min="0" :precision="2" placeholder="不限制请留空" />
        </el-form-item>
        <el-form-item label="告警级别" prop="alarmLevel">
          <el-radio-group v-model="form.alarmLevel">
            <el-radio value="info">
              <el-tag type="info" size="small">信息</el-tag>
            </el-radio>
            <el-radio value="warning">
              <el-tag type="warning" size="small">警告</el-tag>
            </el-radio>
            <el-radio value="critical">
              <el-tag type="danger" size="small">严重</el-tag>
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="告警标题">
          <el-input v-model="form.alarmTitle" placeholder="如: pH值异常" />
        </el-form-item>
        <el-form-item label="告警消息模板">
          <el-input
            v-model="form.alarmMessageTemplate"
            type="textarea"
            :rows="3"
            placeholder="支持变量: {currentValue}-当前值, {thresholdMin}-最小阈值, {thresholdMax}-最大阈值, {metricUnit}-单位"
          />
          <div class="form-tip">示例: pH值异常: 当前值{currentValue}, 正常范围{thresholdMin}~{thresholdMax}</div>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio value="1">启用</el-radio>
            <el-radio value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="AlarmRule" lang="ts">
import { listAlarmRule, getAlarmRule, delAlarmRule, addAlarmRule, updateAlarmRule } from '@/api/swimming/iotAlarmRule'
import type { AlarmRule, AlarmRuleQuery } from '@/types/iot/alarmRule'

const { proxy } = getCurrentInstance() as ComponentInternalInstance
const route = useRoute()

const ruleList = ref<AlarmRule[]>([])
const buttonLoading = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref<Array<number>>([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)

const queryFormRef = ref<ElFormInstance>()
const ruleFormRef = ref<ElFormInstance>()

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
})

// 常用指标选项
const metricOptions = ref([
  { label: 'pH值', value: 'ph_value', unit: '' },
  { label: '余氯值', value: 'chlorine', unit: 'mg/L' },
  { label: '水温', value: 'temperature', unit: '℃' },
  { label: '浊度', value: 'turbidity', unit: 'NTU' },
  { label: '溶解氧', value: 'do', unit: 'mg/L' },
  { label: '电导率', value: 'conductivity', unit: 'μS/cm' },
  { label: 'ORP', value: 'orp', unit: 'mV' }
])

const initFormData: AlarmRule = {
  deviceKey: '',
  ruleName: '',
  metricName: '',
  metricUnit: '',
  thresholdMin: undefined,
  thresholdMax: undefined,
  alarmLevel: 'warning',
  alarmTitle: '',
  alarmMessageTemplate: '',
  status: '1',
  remark: ''
}

const data = reactive<PageData<AlarmRule, AlarmRuleQuery>>({
  form: { ...initFormData },
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    deviceKey: '',
    ruleName: '',
    alarmLevel: '',
    status: ''
  },
  rules: {
    deviceKey: [
      { required: true, message: "设备Key不能为空", trigger: "blur" }
    ],
    ruleName: [
      { required: true, message: "规则名称不能为空", trigger: "blur" }
    ],
    metricName: [
      { required: true, message: "监控指标不能为空", trigger: "blur" }
    ],
    alarmLevel: [
      { required: true, message: "告警级别不能为空", trigger: "change" }
    ]
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 获取指标标签 */
const getMetricLabel = (metricName: string) => {
  const metric = metricOptions.value.find(m => m.value === metricName)
  return metric ? metric.label : metricName
}

/** 查询告警规则列表 */
const getList = async () => {
  loading.value = true
  const res = await listAlarmRule(queryParams.value)
  ruleList.value = res.rows
  total.value = res.total
  loading.value = false
}

/** 取消按钮 */
const cancel = () => {
  reset()
  dialog.visible = false
}

/** 表单重置 */
const reset = () => {
  form.value = { ...initFormData }
  ruleFormRef.value?.resetFields()
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
const handleSelectionChange = (selection: AlarmRule[]) => {
  ids.value = selection.map(item => item.id!)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
const handleAdd = () => {
  reset()
  // 如果URL中有deviceKey参数，自动填充
  if (route.query.deviceKey) {
    form.value.deviceKey = route.query.deviceKey as string
  }
  dialog.visible = true
  dialog.title = "添加告警规则"
}

/** 修改按钮操作 */
const handleUpdate = async (row?: AlarmRule) => {
  reset()
  const _id = row?.id || ids.value[0]
  const res = await getAlarmRule(_id)
  Object.assign(form.value, res.data)
  dialog.visible = true
  dialog.title = "修改告警规则"
}

/** 状态切换 */
const handleStatusChange = async (row: AlarmRule) => {
  let text = row.status === "1" ? "启用" : "禁用"
  try {
    await updateAlarmRule(row)
    proxy?.$modal.msgSuccess(`${text}成功`)
    await getList()
  } catch {
    row.status = row.status === "1" ? "0" : "1"
  }
}

/** 提交按钮 */
const submitForm = () => {
  ruleFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true
      if (form.value.id) {
        await updateAlarmRule(form.value).finally(() => buttonLoading.value = false)
      } else {
        await addAlarmRule(form.value).finally(() => buttonLoading.value = false)
      }
      proxy?.$modal.msgSuccess("操作成功")
      dialog.visible = false
      await getList()
    }
  })
}

/** 删除按钮操作 */
const handleDelete = async (row?: AlarmRule) => {
  const _ids = row?.id || ids.value
  await proxy?.$modal.confirm('是否确认删除告警规则编号为"' + _ids + '"的数据项？')
  await delAlarmRule(_ids as number[])
  proxy?.$modal.msgSuccess("删除成功")
  await getList()
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('swimming/iot/alarmRule/export', {
    ...queryParams.value
  }, `alarm_rule_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  // 从路由参数获取设备Key，如果有则自动过滤
  if (route.query.deviceKey) {
    queryParams.value.deviceKey = route.query.deviceKey as string
  }
  getList()
})
</script>

<style scoped lang="scss">
.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
  line-height: 1.4;
}
</style>
