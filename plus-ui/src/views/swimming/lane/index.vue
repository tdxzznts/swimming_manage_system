<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div v-show="showSearch" class="mb-[10px]">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true">
            <el-form-item label="泳道编号" prop="laneNo">
              <el-input v-model="queryParams.laneNo" placeholder="请输入泳道编号" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="泳道类型" prop="laneType">
              <el-select v-model="queryParams.laneType" placeholder="请选择泳道类型" clearable >
                <el-option v-for="dict in sp_lane_type" :key="dict.value" :label="dict.label" :value="dict.value"/>
              </el-select>
            </el-form-item>
            <el-form-item label="容量" prop="capacity">
              <el-input v-model="queryParams.capacity" placeholder="请输入容量" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="泳道长度" prop="length">
              <el-input v-model="queryParams.length" placeholder="请输入泳道长度" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="泳道宽度" prop="width">
              <el-input v-model="queryParams.width" placeholder="请输入泳道宽度" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="泳道深度" prop="depth">
              <el-input v-model="queryParams.depth" placeholder="请输入泳道深度" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="状态" prop="status">
              <el-select v-model="queryParams.status" placeholder="请选择状态" clearable >
                <el-option v-for="dict in sp_lane_status" :key="dict.value" :label="dict.label" :value="dict.value"/>
              </el-select>
            </el-form-item>
            <el-form-item label="排序" prop="sort">
              <el-input v-model="queryParams.sort" placeholder="请输入排序" clearable @keyup.enter="handleQuery" />
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
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['swimming:lane:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['swimming:lane:edit']">修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['swimming:lane:remove']">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['swimming:lane:export']">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" border :data="laneList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="泳道ID" align="center" prop="id" v-if="true" />
        <el-table-column label="泳道编号" align="center" prop="laneNo" />
        <el-table-column label="泳道类型" align="center" prop="laneType">
          <template #default="scope">
            <dict-tag :options="sp_lane_type" :value="scope.row.laneType"/>
          </template>
        </el-table-column>
        <el-table-column label="容量" align="center" prop="capacity" />
        <el-table-column label="泳道长度" align="center" prop="length" />
        <el-table-column label="泳道宽度" align="center" prop="width" />
        <el-table-column label="泳道深度" align="center" prop="depth" />
        <el-table-column label="状态" align="center" prop="status">
          <template #default="scope">
            <dict-tag :options="sp_lane_status" :value="scope.row.status"/>
          </template>
        </el-table-column>
        <el-table-column label="排序" align="center" prop="sort" />
        <el-table-column label="备注" align="center" prop="remark" />
        <el-table-column label="操作" align="center" fixed="right"  class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['swimming:lane:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['swimming:lane:remove']"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>
    <!-- 添加或修改泳道对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" append-to-body>
      <el-form ref="laneFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="泳道编号" prop="laneNo">
          <el-input v-model="form.laneNo" placeholder="请输入泳道编号" />
        </el-form-item>
        <el-form-item label="泳道类型" prop="laneType">
          <el-select v-model="form.laneType" placeholder="请选择泳道类型">
            <el-option
                v-for="dict in sp_lane_type"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="容量" prop="capacity">
          <el-input v-model="form.capacity" placeholder="请输入容量" />
        </el-form-item>
        <el-form-item label="泳道长度" prop="length">
          <el-input v-model="form.length" placeholder="请输入泳道长度" />
        </el-form-item>
        <el-form-item label="泳道宽度" prop="width">
          <el-input v-model="form.width" placeholder="请输入泳道宽度" />
        </el-form-item>
        <el-form-item label="泳道深度" prop="depth">
          <el-input v-model="form.depth" placeholder="请输入泳道深度" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option
                v-for="dict in sp_lane_status"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input v-model="form.sort" placeholder="请输入排序" />
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

<script setup name="Lane" lang="ts">
import { listLane, getLane, delLane, addLane, updateLane } from '@/api/swimming/lane';
import { LaneVO, LaneQuery, LaneForm } from '@/api/swimming/lane/types';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { sp_lane_type, sp_lane_status } = toRefs<any>(proxy?.useDict('sp_lane_type', 'sp_lane_status'));

const laneList = ref<LaneVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref<ElFormInstance>();
const laneFormRef = ref<ElFormInstance>();

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: LaneForm = {
  id: undefined,
  laneNo: undefined,
  laneType: undefined,
  capacity: undefined,
  length: undefined,
  width: undefined,
  depth: undefined,
  status: undefined,
  sort: undefined,
  remark: undefined,
}
const data = reactive<PageData<LaneForm, LaneQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    laneNo: undefined,
    laneType: undefined,
    capacity: undefined,
    length: undefined,
    width: undefined,
    depth: undefined,
    status: undefined,
    sort: undefined,
    params: {
    }
  },
  rules: {
    id: [
      { required: true, message: "泳道ID不能为空", trigger: "blur" }
    ],
    laneNo: [
      { required: true, message: "泳道编号不能为空", trigger: "blur" }
    ],
    capacity: [
      { required: true, message: "容量不能为空", trigger: "blur" }
    ],
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询泳道列表 */
const getList = async () => {
  loading.value = true;
  const res = await listLane(queryParams.value);
  laneList.value = res.rows;
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
  laneFormRef.value?.resetFields();
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
const handleSelectionChange = (selection: LaneVO[]) => {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  reset();
  dialog.visible = true;
  dialog.title = "添加泳道";
}

/** 修改按钮操作 */
const handleUpdate = async (row?: LaneVO) => {
  reset();
  const _id = row?.id || ids.value[0]
  const res = await getLane(_id);
  Object.assign(form.value, res.data);
  dialog.visible = true;
  dialog.title = "修改泳道";
}

/** 提交按钮 */
const submitForm = () => {
  laneFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updateLane(form.value).finally(() =>  buttonLoading.value = false);
      } else {
        await addLane(form.value).finally(() =>  buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("操作成功");
      dialog.visible = false;
      await getList();
    }
  });
}

/** 删除按钮操作 */
const handleDelete = async (row?: LaneVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除泳道编号为"' + _ids + '"的数据项？').finally(() => loading.value = false);
  await delLane(_ids);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('swimming/lane/export', {
    ...queryParams.value
  }, `lane_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList();
});
</script>
