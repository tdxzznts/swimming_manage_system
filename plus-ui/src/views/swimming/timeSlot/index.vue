<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div v-show="showSearch" class="mb-[10px]">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true">
            <el-form-item label="泳道" prop="laneId">
              <el-select v-model="queryParams.laneId" placeholder="请选择泳道" clearable filterable>
                <el-option
                  v-for="lane in laneListAll"
                  :key="lane.id"
                  :label="`${lane.laneNo}号泳道 (${lane.laneType === '1' ? '快速' : lane.laneType === '2' ? '中速' : '慢速'})`"
                  :value="lane.id"
                >
                  <span>{{ lane.laneNo }}号泳道</span>
                  <span style="float: right; color: var(--el-text-color-secondary); font-size: 13px">
                    <span v-if="lane.status === '0'">开放</span>
                    <span v-else-if="lane.status === '1'">关闭</span>
                    <span v-else>维修</span>
                    {{ lane.laneType === '1' ? '·快速' : lane.laneType === '2' ? '·中速' : '·慢速' }}
                  </span>
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="日期" prop="slotDate">
              <el-date-picker clearable
                v-model="queryParams.slotDate"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择日期"
              />
            </el-form-item>
            <el-form-item label="开始时间" prop="startTime">
              <el-date-picker clearable
                v-model="queryParams.startTime"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择开始时间"
              />
            </el-form-item>
            <el-form-item label="结束时间" prop="endTime">
              <el-date-picker clearable
                v-model="queryParams.endTime"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择结束时间"
              />
            </el-form-item>
            <el-form-item label="时段类型" prop="slotType">
              <el-select v-model="queryParams.slotType" placeholder="请选择时段类型" clearable >
                <el-option v-for="dict in sp_slot_type" :key="dict.value" :label="dict.label" :value="dict.value"/>
              </el-select>
            </el-form-item>
            <el-form-item label="容量" prop="capacity">
              <el-input v-model="queryParams.capacity" placeholder="请输入容量" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="已预约人数" prop="bookedCount">
              <el-input v-model="queryParams.bookedCount" placeholder="请输入已预约人数" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="单价" prop="price">
              <el-input v-model="queryParams.price" placeholder="请输入单价" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="是否可预约" prop="isAvailable">
              <el-select v-model="queryParams.isAvailable" placeholder="请选择是否可预约" clearable>
                <el-option label="是" value="1" />
                <el-option label="否" value="0" />
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
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['swimming:timeSlot:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['swimming:timeSlot:edit']">修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['swimming:timeSlot:remove']">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['swimming:timeSlot:export']">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" border :data="timeSlotList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="时段ID" align="center" prop="id" v-if="false" />
        <el-table-column label="泳道" align="center" prop="laneId" width="120">
          <template #default="scope">
            {{ getLaneNo(scope.row.laneId) }}
          </template>
        </el-table-column>
        <el-table-column label="日期" align="center" prop="slotDate" width="120">
          <template #default="scope">
            <span>{{ parseTime(scope.row.slotDate, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="开始时间" align="center" prop="startTime" width="100">
          <template #default="scope">
            <span>{{ parseTime(scope.row.startTime, '{h}:{i}:{s}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="结束时间" align="center" prop="endTime" width="100">
          <template #default="scope">
            <span>{{ parseTime(scope.row.endTime, '{h}:{i}:{s}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="时段类型" align="center" prop="slotType">
          <template #default="scope">
            <dict-tag :options="sp_slot_type" :value="scope.row.slotType"/>
          </template>
        </el-table-column>
        <el-table-column label="容量" align="center" prop="capacity" />
        <el-table-column label="已预约人数" align="center" prop="bookedCount" />
        <el-table-column label="单价" align="center" prop="price" />
        <el-table-column label="是否可预约" align="center" prop="isAvailable" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.isAvailable === '1'" type="success">是</el-tag>
            <el-tag v-else-if="scope.row.isAvailable === '0'" type="info">否</el-tag>
            <el-tag v-else type="info">{{ scope.row.isAvailable }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="备注" align="center" prop="remark" />
        <el-table-column label="操作" align="center" fixed="right"  class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['swimming:timeSlot:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['swimming:timeSlot:remove']"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>
    <!-- 添加或修改预约时段对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" append-to-body>
      <el-form ref="timeSlotFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="泳道" prop="laneId">
          <el-select v-model="form.laneId" placeholder="请选择泳道" clearable filterable>
            <el-option
              v-for="lane in laneList"
              :key="lane.id"
              :label="`${lane.laneNo}号泳道 (${lane.laneType === '1' ? '快速' : lane.laneType === '2' ? '中速' : '慢速'})`"
              :value="lane.id"
            >
              <span>{{ lane.laneNo }}号泳道</span>
              <span style="float: right; color: var(--el-text-color-secondary); font-size: 13px">
                {{ lane.laneType === '1' ? '快速' : lane.laneType === '2' ? '中速' : '慢速' }}
              </span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="日期" prop="slotDate">
          <el-date-picker clearable
            v-model="form.slotDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择日期">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-time-picker clearable
            v-model="form.startTime"
            value-format="HH:mm:ss"
            placeholder="请选择开始时间">
          </el-time-picker>
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-time-picker clearable
            v-model="form.endTime"
            value-format="HH:mm:ss"
            placeholder="请选择结束时间">
          </el-time-picker>
        </el-form-item>
        <el-form-item label="时段类型" prop="slotType">
          <el-select v-model="form.slotType" placeholder="请选择时段类型">
            <el-option
                v-for="dict in sp_slot_type"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="容量" prop="capacity">
          <el-input v-model="form.capacity" placeholder="请输入容量" />
        </el-form-item>
        <el-form-item label="已预约人数" prop="bookedCount">
          <el-input v-model="form.bookedCount" placeholder="请输入已预约人数" />
        </el-form-item>
        <el-form-item label="单价" prop="price">
          <el-input v-model="form.price" placeholder="请输入单价" />
        </el-form-item>
        <el-form-item label="是否可预约" prop="isAvailable">
          <el-radio-group v-model="form.isAvailable">
            <el-radio value="1">是</el-radio>
            <el-radio value="0">否</el-radio>
          </el-radio-group>
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
  </div>
</template>

<script setup name="TimeSlot" lang="ts">
import { listTimeSlot, getTimeSlot, delTimeSlot, addTimeSlot, updateTimeSlot } from '@/api/swimming/timeSlot';
import { TimeSlotVO, TimeSlotQuery, TimeSlotForm } from '@/api/swimming/timeSlot/types';
import { listLane } from '@/api/swimming/lane';
import { LaneVO } from '@/api/swimming/lane/types';
import { parseTime } from '@/utils/ruoyi';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { sp_slot_type } = toRefs<any>(proxy?.useDict('sp_slot_type'));

const timeSlotList = ref<TimeSlotVO[]>([]);
const laneList = ref<LaneVO[]>([]);
const laneListAll = ref<LaneVO[]>([]); // 用于表格显示所有泳道
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref<ElFormInstance>();
const timeSlotFormRef = ref<ElFormInstance>();

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: TimeSlotForm = {
  id: undefined,
  laneId: undefined,
  slotDate: undefined,
  startTime: undefined,
  endTime: undefined,
  slotType: undefined,
  capacity: undefined,
  bookedCount: undefined,
  price: undefined,
  isAvailable: '1', // 默认可预约
  remark: undefined,
}
const data = reactive<PageData<TimeSlotForm, TimeSlotQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    laneId: undefined,
    slotDate: undefined,
    startTime: undefined,
    endTime: undefined,
    slotType: undefined,
    capacity: undefined,
    bookedCount: undefined,
    price: undefined,
    isAvailable: undefined,
    params: {
    }
  },
  rules: {
    laneId: [
      { required: true, message: "泳道ID不能为空", trigger: "blur" }
    ],
    slotDate: [
      { required: true, message: "日期不能为空", trigger: "blur" }
    ],
    startTime: [
      { required: true, message: "开始时间不能为空", trigger: "blur" }
    ],
    endTime: [
      { required: true, message: "结束时间不能为空", trigger: "blur" }
    ],
    capacity: [
      { required: true, message: "容量不能为空", trigger: "blur" }
    ],
    price: [
      { required: true, message: "单价不能为空", trigger: "blur" }
    ],
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询预约时段列表 */
const getList = async () => {
  loading.value = true;
  const res = await listTimeSlot(queryParams.value);
  timeSlotList.value = res.rows;
  total.value = res.total;
  loading.value = false;
}

/** 查询泳道列表 */
const getLaneList = async () => {
  // 查询所有泳道用于表格显示
  const resAll = await listLane();
  laneListAll.value = resAll.rows;

  // 只查询开放状态的泳道用于表单选择
  const res = await listLane({ status: '0' });
  laneList.value = res.rows;
}

/** 根据泳道ID获取泳道编号 */
const getLaneNo = (laneId: string | number) => {
  const lane = laneListAll.value.find(l => l.id === laneId);
  return lane ? `${lane.laneNo}号泳道` : laneId;
}

/** 取消按钮 */
const cancel = () => {
  reset();
  dialog.visible = false;
}

/** 表单重置 */
const reset = () => {
  form.value = {...initFormData};
  timeSlotFormRef.value?.resetFields();
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value?.resetFields();
  handleQuery();
}

/** 多选框选中数据 */
const handleSelectionChange = (selection: TimeSlotVO[]) => {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  reset();
  dialog.visible = true;
  dialog.title = "添加预约时段";
}

/** 修改按钮操作 */
const handleUpdate = async (row?: TimeSlotVO) => {
  reset();
  const _id = row?.id || ids.value[0]
  const res = await getTimeSlot(_id);
  Object.assign(form.value, res.data);
  // 处理时间格式：将完整日期时间转换为纯时间字符串 (HH:mm:ss)
  if (form.value.startTime) {
    form.value.startTime = parseTime(form.value.startTime, '{h}:{i}:{s}');
  }
  if (form.value.endTime) {
    form.value.endTime = parseTime(form.value.endTime, '{h}:{i}:{s}');
  }
  dialog.visible = true;
  dialog.title = "修改预约时段";
}

/** 提交按钮 */
const submitForm = () => {
  timeSlotFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updateTimeSlot(form.value).finally(() =>  buttonLoading.value = false);
      } else {
        await addTimeSlot(form.value).finally(() =>  buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("操作成功");
      dialog.visible = false;
      await getList();
    }
  });
}

/** 删除按钮操作 */
const handleDelete = async (row?: TimeSlotVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除预约时段编号为"' + _ids + '"的数据项？').finally(() => loading.value = false);
  await delTimeSlot(_ids);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('swimming/timeSlot/export', {
    ...queryParams.value
  }, `timeSlot_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList();
  getLaneList();
});
</script>
