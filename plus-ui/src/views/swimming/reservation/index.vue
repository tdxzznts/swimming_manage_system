<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div v-show="showSearch" class="mb-[10px]">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true">
            <el-form-item label="用户ID" prop="userId">
              <el-input v-model="queryParams.userId" placeholder="请输入用户ID" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="时段ID" prop="timeSlotId">
              <el-input v-model="queryParams.timeSlotId" placeholder="请输入时段ID" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="泳道ID" prop="laneId">
              <el-input v-model="queryParams.laneId" placeholder="请输入泳道ID" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="预约单号" prop="reservationNo">
              <el-input v-model="queryParams.reservationNo" placeholder="请输入预约单号" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="预约日期" prop="reservationDate">
              <el-date-picker clearable
                v-model="queryParams.reservationDate"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择预约日期"
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
            <el-form-item label="状态" prop="status">
              <el-select v-model="queryParams.status" placeholder="请选择状态" clearable >
                <el-option v-for="dict in sp_reservation_status" :key="dict.value" :label="dict.label" :value="dict.value"/>
              </el-select>
            </el-form-item>
            <el-form-item label="金额" prop="amount">
              <el-input v-model="queryParams.amount" placeholder="请输入金额" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="订单ID" prop="orderId">
              <el-input v-model="queryParams.orderId" placeholder="请输入订单ID" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="签到时间" prop="checkInTime">
              <el-date-picker clearable
                v-model="queryParams.checkInTime"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择签到时间"
              />
            </el-form-item>
            <el-form-item label="签退时间" prop="checkOutTime">
              <el-date-picker clearable
                v-model="queryParams.checkOutTime"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择签退时间"
              />
            </el-form-item>
            <el-form-item label="取消时间" prop="cancelTime">
              <el-date-picker clearable
                v-model="queryParams.cancelTime"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择取消时间"
              />
            </el-form-item>
            <el-form-item label="取消原因" prop="cancelReason">
              <el-input v-model="queryParams.cancelReason" placeholder="请输入取消原因" clearable @keyup.enter="handleQuery" />
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
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['swimming:reservation:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['swimming:reservation:edit']">修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['swimming:reservation:remove']">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['swimming:reservation:export']">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" border :data="reservationList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="预约ID" align="center" prop="id" v-if="false" />
        <el-table-column label="用户" align="center" prop="userId" width="120">
          <template #default="scope">
            {{ getUserName(scope.row.userId) }}
          </template>
        </el-table-column>
        <el-table-column label="泳道" align="center" prop="laneId" width="120">
          <template #default="scope">
            {{ getLaneName(scope.row.laneId) }}
          </template>
        </el-table-column>
        <el-table-column label="预约单号" align="center" prop="reservationNo" />
        <el-table-column label="预约日期" align="center" prop="reservationDate" width="120">
          <template #default="scope">
            <span>{{ parseTime(scope.row.reservationDate, '{y}-{m}-{d}') }}</span>
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
        <el-table-column label="状态" align="center" prop="status">
          <template #default="scope">
            <dict-tag :options="sp_reservation_status" :value="scope.row.status"/>
          </template>
        </el-table-column>
        <el-table-column label="金额" align="center" prop="amount" />
        <el-table-column label="签到时间" align="center" prop="checkInTime" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.checkInTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="签退时间" align="center" prop="checkOutTime" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.checkOutTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="取消时间" align="center" prop="cancelTime" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.cancelTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="取消原因" align="center" prop="cancelReason" />
        <el-table-column label="备注" align="center" prop="remark" />
        <el-table-column label="操作" align="center" fixed="right" width="180" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="查看订单" placement="top">
              <el-button link type="primary" icon="Document" @click="handleViewOrder(scope.row)" v-hasPermi="['swimming:reservation:query']"></el-button>
            </el-tooltip>
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['swimming:reservation:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['swimming:reservation:remove']"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>

    <!-- 订单详情对话框 -->
    <el-dialog title="订单详情" v-model="orderDialogVisible" width="700px" append-to-body>
      <el-descriptions :column="2" border v-if="orderInfo" label-style="width: 120px;">
        <el-descriptions-item label="订单ID">{{ orderInfo.id }}</el-descriptions-item>
        <el-descriptions-item label="订单号">{{ orderInfo.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="订单类型">
          <dict-tag :options="order_type" :value="orderInfo.orderType"/>
        </el-descriptions-item>
        <el-descriptions-item label="订单金额">{{ orderInfo.amount }}元</el-descriptions-item>
        <el-descriptions-item label="优惠金额">{{ orderInfo.discountAmount }}元</el-descriptions-item>
        <el-descriptions-item label="实际支付">{{ orderInfo.actualAmount }}元</el-descriptions-item>
        <el-descriptions-item label="支付方式">
          <span v-if="orderInfo.payType === '1'">支付宝</span>
          <span v-else-if="orderInfo.payType === '2'">余额支付</span>
          <span v-else-if="orderInfo.payType === '3'">免费次数</span>
          <span v-else>未支付</span>
        </el-descriptions-item>
        <el-descriptions-item label="支付状态">
          <dict-tag :options="sp_pay_status" :value="orderInfo.payStatus"/>
        </el-descriptions-item>
        <el-descriptions-item label="获得积分">{{ orderInfo.getPoint }}</el-descriptions-item>
        <el-descriptions-item label="支付时间">
          {{ orderInfo.payTime ? parseTime(orderInfo.payTime) : '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <dict-tag :options="sp_order_status" :value="orderInfo.status"/>
        </el-descriptions-item>
        <el-descriptions-item label="第三方交易号" :span="2">
          {{ orderInfo.tradeNo || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">
          {{ orderInfo.remark || '-' }}
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="orderDialogVisible = false">关 闭</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 添加或修改预约记录对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" append-to-body>
      <el-form ref="reservationFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户ID" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入用户ID" />
        </el-form-item>
        <el-form-item label="时段ID" prop="timeSlotId">
          <el-input v-model="form.timeSlotId" placeholder="请输入时段ID" />
        </el-form-item>
        <el-form-item label="泳道ID" prop="laneId">
          <el-input v-model="form.laneId" placeholder="请输入泳道ID" />
        </el-form-item>
        <el-form-item label="预约单号" prop="reservationNo">
          <el-input v-model="form.reservationNo" placeholder="请输入预约单号" />
        </el-form-item>
        <el-form-item label="预约日期" prop="reservationDate">
          <el-date-picker clearable
            v-model="form.reservationDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择预约日期">
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
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option
                v-for="dict in sp_reservation_status"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="金额" prop="amount">
          <el-input v-model="form.amount" placeholder="请输入金额" />
        </el-form-item>
        <el-form-item label="订单ID" prop="orderId">
          <el-input v-model="form.orderId" placeholder="请输入订单ID" />
        </el-form-item>
        <el-form-item label="签到时间" prop="checkInTime">
          <el-date-picker clearable
            v-model="form.checkInTime"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="请选择签到时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="签退时间" prop="checkOutTime">
          <el-date-picker clearable
            v-model="form.checkOutTime"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="请选择签退时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="取消时间" prop="cancelTime">
          <el-date-picker clearable
            v-model="form.cancelTime"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="请选择取消时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="取消原因" prop="cancelReason">
            <el-input v-model="form.cancelReason" type="textarea" placeholder="请输入内容" />
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

<script setup name="Reservation" lang="ts">
import { listReservation, getReservation, delReservation, addReservation, updateReservation } from '@/api/swimming/reservation';
import { ReservationVO, ReservationQuery, ReservationForm } from '@/api/swimming/reservation/types';
import { listLane } from '@/api/swimming/lane';
import { LaneVO } from '@/api/swimming/lane/types';
import { listUser } from '@/api/system/user';
import { getOrder } from '@/api/swimming/order';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { sp_reservation_status, order_type, sp_pay_status, sp_order_status } = toRefs<any>(proxy?.useDict('sp_reservation_status', 'order_type', 'sp_pay_status', 'sp_order_status'));

const reservationList = ref<ReservationVO[]>([]);
const laneList = ref<LaneVO[]>([]);
const userList = ref<any[]>([]);
const orderDialogVisible = ref(false);
const orderInfo = ref<any>(null);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref<ElFormInstance>();
const reservationFormRef = ref<ElFormInstance>();

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: ReservationForm = {
  id: undefined,
  userId: undefined,
  timeSlotId: undefined,
  laneId: undefined,
  reservationNo: undefined,
  reservationDate: undefined,
  startTime: undefined,
  endTime: undefined,
  status: undefined,
  amount: undefined,
  orderId: undefined,
  checkInTime: undefined,
  checkOutTime: undefined,
  cancelTime: undefined,
  cancelReason: undefined,
  remark: undefined,
}
const data = reactive<PageData<ReservationForm, ReservationQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    userId: undefined,
    timeSlotId: undefined,
    laneId: undefined,
    reservationNo: undefined,
    reservationDate: undefined,
    startTime: undefined,
    endTime: undefined,
    status: undefined,
    amount: undefined,
    orderId: undefined,
    checkInTime: undefined,
    checkOutTime: undefined,
    cancelTime: undefined,
    cancelReason: undefined,
    params: {
    }
  },
  rules: {
    id: [
      { required: true, message: "预约ID不能为空", trigger: "blur" }
    ],
    userId: [
      { required: true, message: "用户ID不能为空", trigger: "blur" }
    ],
    timeSlotId: [
      { required: true, message: "时段ID不能为空", trigger: "blur" }
    ],
    laneId: [
      { required: true, message: "泳道ID不能为空", trigger: "blur" }
    ],
    reservationNo: [
      { required: true, message: "预约单号不能为空", trigger: "blur" }
    ],
    reservationDate: [
      { required: true, message: "预约日期不能为空", trigger: "blur" }
    ],
    startTime: [
      { required: true, message: "开始时间不能为空", trigger: "blur" }
    ],
    endTime: [
      { required: true, message: "结束时间不能为空", trigger: "blur" }
    ],
    amount: [
      { required: true, message: "金额不能为空", trigger: "blur" }
    ],
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询预约记录列表 */
const getList = async () => {
  loading.value = true;
  const res = await listReservation(queryParams.value);
  reservationList.value = res.rows;
  total.value = res.total;
  loading.value = false;
}

/** 查询泳道列表 */
const getLaneList = async () => {
  const res = await listLane();
  laneList.value = res.rows;
}

/** 查询用户列表 */
const getUserList = async () => {
  const res = await listUser({ pageNum: 1, pageSize: 1000 });
  userList.value = res.rows;
}

/** 根据泳道ID获取泳道名称 */
const getLaneName = (laneId: string | number) => {
  const lane = laneList.value.find(l => l.id === laneId);
  return lane ? `${lane.laneNo}号泳道` : laneId;
}

/** 根据用户ID获取用户名 */
const getUserName = (userId: string | number) => {
  const user = userList.value.find(u => u.userId === userId);
  return user ? user.nickName || user.userName : userId;
}

/** 取消按钮 */
const cancel = () => {
  reset();
  dialog.visible = false;
}

/** 表单重置 */
const reset = () => {
  form.value = {...initFormData};
  reservationFormRef.value?.resetFields();
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
const handleSelectionChange = (selection: ReservationVO[]) => {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  reset();
  dialog.visible = true;
  dialog.title = "添加预约记录";
}

/** 修改按钮操作 */
const handleUpdate = async (row?: ReservationVO) => {
  reset();
  const _id = row?.id || ids.value[0]
  const res = await getReservation(_id);
  Object.assign(form.value, res.data);
  dialog.visible = true;
  dialog.title = "修改预约记录";
}

/** 提交按钮 */
const submitForm = () => {
  reservationFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updateReservation(form.value).finally(() =>  buttonLoading.value = false);
      } else {
        await addReservation(form.value).finally(() =>  buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("操作成功");
      dialog.visible = false;
      await getList();
    }
  });
}

/** 删除按钮操作 */
const handleDelete = async (row?: ReservationVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除预约记录编号为"' + _ids + '"的数据项？').finally(() => loading.value = false);
  await delReservation(_ids);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('swimming/reservation/export', {
    ...queryParams.value
  }, `reservation_${new Date().getTime()}.xlsx`)
}

/** 查看订单详情 */
const handleViewOrder = async (row: ReservationVO) => {
  if (!row.orderId) {
    proxy?.$modal.msgWarning('该预约记录没有关联订单');
    return;
  }

  try {
    const res = await getOrder(row.orderId);
    orderInfo.value = res.data;
    orderDialogVisible.value = true;
  } catch (error) {
    proxy?.$modal.msgError('查询订单信息失败');
  }
}

onMounted(() => {
  getList();
  getLaneList();
  getUserList();
});
</script>
