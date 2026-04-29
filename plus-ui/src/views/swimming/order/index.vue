<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div v-show="showSearch" class="mb-[10px]">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true">
            <el-form-item label="订单号" prop="orderNo">
              <el-input v-model="queryParams.orderNo" placeholder="请输入订单号" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="用户名" prop="userName">
              <el-input v-model="queryParams.userName" placeholder="请输入用户名" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="订单类型" prop="orderType">
              <el-select v-model="queryParams.orderType" placeholder="请选择订单类型" clearable >
                <el-option v-for="dict in order_type" :key="dict.value" :label="dict.label" :value="dict.value"/>
              </el-select>
            </el-form-item>
            <el-form-item label="支付方式" prop="payType">
              <el-select v-model="queryParams.payType" placeholder="请选择支付方式" clearable >
                <el-option v-for="dict in sp_pay_type" :key="dict.value" :label="dict.label" :value="dict.value"/>
              </el-select>
            </el-form-item>
            <el-form-item label="支付状态" prop="payStatus">
              <el-select v-model="queryParams.payStatus" placeholder="请选择支付状态" clearable >
                <el-option v-for="dict in sp_pay_status" :key="dict.value" :label="dict.label" :value="dict.value"/>
              </el-select>
            </el-form-item>
            <el-form-item label="订单状态" prop="status">
              <el-select v-model="queryParams.status" placeholder="请选择订单状态" clearable >
                <el-option v-for="dict in sp_order_status" :key="dict.value" :label="dict.label" :value="dict.value"/>
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
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['swimming:order:remove']">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['swimming:order:export']">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" border :data="orderList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="订单ID" align="center" prop="id" v-if="false" />
        <el-table-column label="订单号" align="center" prop="orderNo" min-width="150" />
        <el-table-column label="用户名" align="center" prop="userName" min-width="100" />
        <el-table-column label="订单类型" align="center" prop="orderType" min-width="90">
          <template #default="scope">
            <dict-tag :options="order_type" :value="scope.row.orderType"/>
          </template>
        </el-table-column>
        <el-table-column label="订单金额" align="center" prop="amount" min-width="90" />
        <el-table-column label="优惠金额" align="center" prop="discountAmount" min-width="90" />
        <el-table-column label="实际支付金额" align="center" prop="actualAmount" min-width="100" />
        <el-table-column label="支付方式" align="center" prop="payType" min-width="90">
          <template #default="scope">
            <dict-tag :options="sp_pay_type" :value="scope.row.payType"/>
          </template>
        </el-table-column>
        <el-table-column label="支付状态" align="center" prop="payStatus" min-width="90">
          <template #default="scope">
            <dict-tag :options="sp_pay_status" :value="scope.row.payStatus"/>
          </template>
        </el-table-column>
        <el-table-column label="支付时间" align="center" prop="payTime" min-width="150">
          <template #default="scope">
            <span>{{ parseTime(scope.row.payTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="订单状态" align="center" prop="status" min-width="90">
          <template #default="scope">
            <dict-tag :options="sp_order_status" :value="scope.row.status"/>
          </template>
        </el-table-column>
        <el-table-column label="备注" align="center" prop="remark" min-width="120" show-overflow-tooltip />
        <el-table-column label="操作" align="center" fixed="right" min-width="120" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['swimming:order:remove']"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>
    <!-- 添加或修改订单对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="600px" append-to-body>
      <el-form ref="orderFormRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="订单号" prop="orderNo">
              <el-input v-model="form.orderNo" placeholder="请输入订单号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="订单类型" prop="orderType">
              <el-select v-model="form.orderType" placeholder="请选择订单类型">
                <el-option
                    v-for="dict in order_type"
                    :key="dict.value"
                    :label="dict.label"
                    :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="订单金额(元)" prop="amount">
              <el-input v-model="form.amount" placeholder="请输入订单金额" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="优惠金额(元)" prop="discountAmount">
              <el-input v-model="form.discountAmount" placeholder="请输入优惠金额" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="实际支付(元)" prop="actualAmount">
              <el-input v-model="form.actualAmount" placeholder="请输入实际支付金额" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="获得积分" prop="getPoint">
              <el-input v-model="form.getPoint" placeholder="请输入获得积分" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="支付方式" prop="payType">
              <el-select v-model="form.payType" placeholder="请选择支付方式">
                <el-option
                    v-for="dict in sp_pay_type"
                    :key="dict.value"
                    :label="dict.label"
                    :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="支付状态" prop="payStatus">
              <el-select v-model="form.payStatus" placeholder="请选择支付状态">
                <el-option
                    v-for="dict in sp_pay_status"
                    :key="dict.value"
                    :label="dict.label"
                    :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="支付时间" prop="payTime">
              <el-date-picker clearable
                v-model="form.payTime"
                type="datetime"
                value-format="YYYY-MM-DD HH:mm:ss"
                placeholder="请选择支付时间"
                style="width: 100%;">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="退款金额(元)" prop="refundAmount">
              <el-input v-model="form.refundAmount" placeholder="请输入退款金额" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="退款时间" prop="refundTime">
              <el-date-picker clearable
                v-model="form.refundTime"
                type="datetime"
                value-format="YYYY-MM-DD HH:mm:ss"
                placeholder="请选择退款时间"
                style="width: 100%;">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="订单状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择订单状态">
                <el-option
                    v-for="dict in sp_order_status"
                    :key="dict.value"
                    :label="dict.label"
                    :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="退款原因" prop="refundReason">
                <el-input v-model="form.refundReason" type="textarea" placeholder="请输入退款原因" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
                <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
            </el-form-item>
          </el-col>
        </el-row>
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

<script setup name="Order" lang="ts">
import { listOrder, getOrder, delOrder, addOrder, updateOrder } from '@/api/swimming/order';
import { OrderVO, OrderQuery, OrderForm } from '@/api/swimming/order/types';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { sp_pay_type, sp_pay_status, sp_order_status, order_type } = toRefs<any>(proxy?.useDict('sp_pay_type', 'sp_pay_status', 'sp_order_status', 'order_type'));

const orderList = ref<OrderVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref<ElFormInstance>();
const orderFormRef = ref<ElFormInstance>();

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: OrderForm = {
  id: undefined,
  orderNo: undefined,
  orderType: undefined,
  amount: undefined,
  discountAmount: undefined,
  actualAmount: undefined,
  getPoint: undefined,
  payType: undefined,
  payStatus: undefined,
  payTime: undefined,
  refundAmount: undefined,
  refundTime: undefined,
  refundReason: undefined,
  status: undefined,
  remark: undefined,
}
const data = reactive<PageData<OrderForm, OrderQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    orderNo: undefined,
    userName: undefined,
    orderType: undefined,
    payType: undefined,
    payStatus: undefined,
    status: undefined,
    params: {
    }
  },
  rules: {
    id: [
      { required: true, message: "订单ID不能为空", trigger: "blur" }
    ],
    orderNo: [
      { required: true, message: "订单号不能为空", trigger: "blur" }
    ],
    userId: [
      { required: true, message: "用户ID不能为空", trigger: "blur" }
    ],
    amount: [
      { required: true, message: "订单金额不能为空", trigger: "blur" }
    ],
    actualAmount: [
      { required: true, message: "实际支付金额不能为空", trigger: "blur" }
    ],
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询订单列表 */
const getList = async () => {
  loading.value = true;
  const res = await listOrder(queryParams.value);
  orderList.value = res.rows;
  total.value = res.total;
  loading.value = false;
}

/** 取消按钮 */
const cancel = () => {
  reset();
  dialog.visible = false;
}

/** 表单重置 */
const reset = () => {
  form.value = {...initFormData};
  orderFormRef.value?.resetFields();
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
const handleSelectionChange = (selection: OrderVO[]) => {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  reset();
  dialog.visible = true;
  dialog.title = "添加订单";
}

/** 修改按钮操作 */
const handleUpdate = async (row?: OrderVO) => {
  reset();
  const _id = row?.id || ids.value[0]
  const res = await getOrder(_id);
  Object.assign(form.value, res.data);
  dialog.visible = true;
  dialog.title = "修改订单";
}

/** 提交按钮 */
const submitForm = () => {
  orderFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updateOrder(form.value).finally(() =>  buttonLoading.value = false);
      } else {
        await addOrder(form.value).finally(() =>  buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("操作成功");
      dialog.visible = false;
      await getList();
    }
  });
}

/** 删除按钮操作 */
const handleDelete = async (row?: OrderVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除订单编号为"' + _ids + '"的数据项？').finally(() => loading.value = false);
  await delOrder(_ids);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('swimming/order/export', {
    ...queryParams.value
  }, `order_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList();
});
</script>
